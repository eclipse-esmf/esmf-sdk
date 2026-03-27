/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 *
 * See the AUTHORS file(s) distributed with this work for additional
 * information regarding authorship.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.eclipse.esmf.aspectmodel.generator.parquet;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.characteristic.Either;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.constraint.LengthConstraint;
import org.eclipse.esmf.metamodel.datatype.LangString;

import io.vavr.Tuple2;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.SimpleGroupFactory;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.example.ExampleParquetWriter;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Types;

/**
 * Handles the generation of Parquet files from flattened Aspect Model data.
 *
 * <p>
 * This class is responsible for:
 * </p>
 * <ul>
 * <li>Extracting and flattening Aspect Model properties into columnar data</li>
 * <li>Building the Parquet {@link MessageType} schema from the flattened data</li>
 * <li>Writing denormalized rows to a Parquet file</li>
 * <li>Mapping Parquet group values by their primitive types</li>
 * </ul>
 *
 * @see ParquetSchemaMapper
 */
class ParquetFileWriter {

   private final ParquetExampleValueGenerator parquetValueGenerator;
   private final List<String> columnNames = new ArrayList<>();
   private MessageType messageTypeSchema;

   ParquetFileWriter( final ParquetExampleValueGenerator parquetValueGenerator ) {
      this.parquetValueGenerator = parquetValueGenerator;
   }

   /**
    * Generates a Parquet file at the specified output path for the given aspect.
    *
    * @param aspect the aspect to generate data for
    * @return Apache Parquet byte[]
    * @throws IOException if an I/O error occurs during apache parquet file writing
    */
   byte[] generateParquetFile( final Aspect aspect ) throws IOException {
      final ByteArrayOutputFile outputFile = new ByteArrayOutputFile();
      final Map<String, Tuple2<Object, PrimitiveType>> flattenedExampleData = new LinkedHashMap<>();
      final Set<String> visitedTypes = new HashSet<>();

      // Extract all properties and create flattened structure
      extractAspectProperties( aspect, "", flattenedExampleData, visitedTypes );

      if ( flattenedExampleData.isEmpty() ) {
         // Create a minimal placeholder entry for aspects with no extractable data
         final PrimitiveType placeholderType = Types.primitive( PrimitiveType.PrimitiveTypeName.BINARY,
               org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.stringType() )
               .named( "_placeholder" );
         flattenedExampleData.put( "_placeholder", new Tuple2<>( aspect.getName(), placeholderType ) );
      }

      // Create MessageType from flattened data
      createMessageTypeSchemaFromFlattenedData( flattenedExampleData );

      // Generate denormalized rows based on collections
      final List<Map<String, Object>> denormalizedRows = createDenormalizedRows( aspect, flattenedExampleData );
      final Map<String, List<Map.Entry<String, Object>>> propertyNameKeyValueMap = new HashMap<>();

      denormalizedRows.stream().flatMap( map -> map.entrySet().stream() )
            .forEach( entrySet -> {
               final String mapKey = getFirstPartBeforeDoubleUnderscore( entrySet.getKey() );
               propertyNameKeyValueMap.computeIfAbsent( mapKey, k -> new ArrayList<>() ).add( entrySet );
            } );

      try ( final ParquetWriter<Group> writer = ExampleParquetWriter.builder( outputFile )
            .withType( messageTypeSchema )
            .build() ) {

         final SimpleGroupFactory simpleGroupFactory = new SimpleGroupFactory( messageTypeSchema );

         propertyNameKeyValueMap.forEach( ( key, entryList ) -> {
            final Group groupNew = simpleGroupFactory.newGroup();

            entryList.forEach( entry -> {
               final String fieldName = entry.getKey();
               final Object value = entry.getValue();
               if ( value != null && flattenedExampleData.containsKey( fieldName ) ) {
                  final PrimitiveType parquetType = flattenedExampleData.get( fieldName )._2;
                  addGroup( value, parquetType, fieldName, groupNew );
               }
            } );

            try {
               writer.write( groupNew );
            } catch ( final IOException ioException ) {
               throw new RuntimeException( ioException );
            }
         } );
      }
      return outputFile.toByteArray();
   }

   private void createMessageTypeSchemaFromFlattenedData( final Map<String, Tuple2<Object, PrimitiveType>> data ) {
      final Types.MessageTypeBuilder messageTypeBuilder = Types.buildMessage();
      for ( final Map.Entry<String, Tuple2<Object, PrimitiveType>> entry : data.entrySet() ) {
         final PrimitiveType parquetType = entry.getValue()._2;
         messageTypeBuilder.addField( parquetType );
      }
      messageTypeSchema = messageTypeBuilder.named( "AspectModel" );
   }

   private void addGroup( final Object value, final PrimitiveType primitiveType, final String fieldName, final Group group ) {
      if ( value == null ) {
         return;
      }

      final LogicalTypeAnnotation logicalType = primitiveType.getLogicalTypeAnnotation();
      final int typeLength = primitiveType.getTypeLength();

      if ( logicalType != null ) {
         if ( logicalType instanceof LogicalTypeAnnotation.StringLogicalTypeAnnotation ) {
            group.add( fieldName, value.toString() );
         } else if ( logicalType instanceof LogicalTypeAnnotation.DateLogicalTypeAnnotation ) {
            addDateValue( value, fieldName, group );
         } else if ( logicalType instanceof LogicalTypeAnnotation.TimestampLogicalTypeAnnotation ) {
            addTimestampValue( value, fieldName, group );
         } else {
            addValueByPrimitiveType( value, primitiveType.getPrimitiveTypeName(), fieldName, group, typeLength );
         }
      } else {
         addValueByPrimitiveType( value, primitiveType.getPrimitiveTypeName(), fieldName, group, typeLength );
      }
   }

   private void addDateValue( final Object value, final String fieldName, final Group group ) {
      if ( value instanceof final XMLGregorianCalendar xmlCal ) {
         final LocalDate localDate = LocalDate.of( xmlCal.getYear(), xmlCal.getMonth(), xmlCal.getDay() );
         final long daysSinceEpoch = localDate.toEpochDay();
         group.add( fieldName, (int) daysSinceEpoch );
      } else if ( value instanceof final Number number ) {
         group.add( fieldName, number.intValue() );
      } else if ( value instanceof String ) {
         try {
            final LocalDate date = LocalDate.parse( value.toString() );
            final long daysSinceEpoch = date.toEpochDay();
            group.add( fieldName, (int) daysSinceEpoch );
         } catch ( final Exception ignored ) {
            group.add( fieldName, 0 );
         }
      } else {
         group.add( fieldName, 0 );
      }
   }

   private void addTimestampValue( final Object value, final String fieldName, final Group group ) {
      if ( value instanceof XMLGregorianCalendar ) {
         XMLGregorianCalendar xmlCal = (XMLGregorianCalendar) value;
         if ( xmlCal.getTimezone() != DatatypeConstants.FIELD_UNDEFINED ) {
            xmlCal = xmlCal.normalize();
         }
         final long timestampMicros = xmlCal.toGregorianCalendar().getTimeInMillis() * 1000;
         group.add( fieldName, timestampMicros );
      } else if ( value instanceof final Number number ) {
         group.add( fieldName, number.longValue() );
      }
   }

   private void addValueByPrimitiveType( final Object value, final PrimitiveType.PrimitiveTypeName primitiveTypeName,
         final String fieldName, final Group group, final int typeLength ) {
      switch ( primitiveTypeName ) {
         case INT32:
            if ( value instanceof final Number number ) {
               group.add( fieldName, number.intValue() );
            } else if ( value instanceof final String stringValue ) {
               try {
                  group.add( fieldName, Integer.parseInt( stringValue ) );
               } catch ( final NumberFormatException ignored ) {
                  group.add( fieldName, 0 );
               }
            }
            break;
         case INT64:
            if ( value instanceof final Number number ) {
               group.add( fieldName, number.longValue() );
            } else if ( value instanceof final String stringValue ) {
               try {
                  group.add( fieldName, Long.parseLong( stringValue ) );
               } catch ( final NumberFormatException ignored ) {
                  group.add( fieldName, 0L );
               }
            }
            break;
         case FLOAT:
            if ( value instanceof final Number number ) {
               group.add( fieldName, number.floatValue() );
            } else if ( value instanceof final String stringValue ) {
               try {
                  group.add( fieldName, Float.parseFloat( stringValue ) );
               } catch ( final NumberFormatException ignored ) {
                  group.add( fieldName, 0.0f );
               }
            }
            break;
         case DOUBLE:
            if ( value instanceof final Number number ) {
               group.add( fieldName, number.doubleValue() );
            } else if ( value instanceof final String stringValue ) {
               try {
                  group.add( fieldName, Double.parseDouble( stringValue ) );
               } catch ( final NumberFormatException ignored ) {
                  group.add( fieldName, 0.0d );
               }
            }
            break;
         case BOOLEAN:
            if ( value instanceof final Boolean booleanValue ) {
               group.add( fieldName, booleanValue );
            } else if ( value instanceof final String stringValue ) {
               group.add( fieldName, Boolean.parseBoolean( stringValue ) );
            }
            break;
         case FIXED_LEN_BYTE_ARRAY:
            final String stringValue = value.toString();
            final byte[] bytes = stringValue.getBytes( StandardCharsets.UTF_8 );
            final byte[] paddedBytes = new byte[typeLength];
            System.arraycopy( bytes, 0, paddedBytes, 0, Math.min( bytes.length, typeLength ) );
            group.add( fieldName, Binary.fromConstantByteArray( paddedBytes ) );
            break;
         case BINARY:
         default:
            group.add( fieldName, value.toString() );
            break;
      }
   }

   private void extractAspectProperties( final Aspect aspect, final String prefix,
         final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final Set<String> visitedTypes ) {
      for ( final Property property : aspect.getProperties() ) {
         if ( property.isNotInPayload() ) {
            continue;
         }
         extractPropertyData( property, prefix, flattenedData, visitedTypes );
      }
   }

   private void extractPropertyData( final Property property, final String prefix,
         final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final Set<String> visitedTypes ) {
      final String columnName = prefix.isEmpty() ? property.getPayloadName() : prefix + "__" + property.getPayloadName();

      if ( property.getCharacteristic().isEmpty() ) {
         return;
      }

      final Characteristic characteristic = property.getCharacteristic().orElse( null );

      BigInteger maxLength = null;
      if ( characteristic instanceof final Trait trait ) {
         final LengthConstraint lengthConstraint = trait.getConstraints().stream()
               .filter( LengthConstraint.class::isInstance )
               .map( LengthConstraint.class::cast )
               .findFirst().orElse( null );
         if ( lengthConstraint != null ) {
            maxLength = lengthConstraint.getMaxValue().orElse( null );
         }
      }

      extractCharacteristicData( characteristic, property, columnName, flattenedData, visitedTypes, maxLength );
   }

   private void extractCharacteristicData( final Characteristic characteristic, final Property property,
         final String columnName, final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final Set<String> visitedTypes, final BigInteger maxLength ) {

      switch ( characteristic ) {
         case final Collection collection ->
            extractCollectionData( collection, property, columnName, flattenedData, visitedTypes, maxLength );
         case final Trait trait ->
            extractCharacteristicData( trait.getBaseCharacteristic(), property, columnName, flattenedData, visitedTypes, maxLength );
         case final Either either -> extractEitherData( either, property, columnName, flattenedData, visitedTypes, maxLength );
         default -> extractScalarOrEntityData( characteristic, property, columnName, flattenedData, visitedTypes, maxLength );
      }
   }

   private void extractCollectionData( final Collection collection, final Property property,
         final String columnName, final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final Set<String> visitedTypes, final BigInteger maxLength ) {
      final Type dataType = collection.getDataType().orElse( null );
      if ( dataType == null ) {
         return;
      }

      if ( dataType instanceof final ComplexType complexType ) {
         extractComplexTypeProperties( complexType, columnName, flattenedData, visitedTypes );
      } else if ( dataType instanceof final Scalar scalar ) {
         Object exampleValue = extractExampleValueFromProperty( property, collection );
         String language = null;
         if ( RDF.langString.getURI().equals( scalar.getUrn() ) ) {
            switch ( exampleValue ) {
               case final LangString langString -> {
                  language = Optional.ofNullable( langString.getLanguageTag() ).map( Locale::getLanguage ).orElse( null );
                  exampleValue = langString.getValue();
               }
               case final Map<?, ?> map when !map.isEmpty() -> {
                  final Map.Entry<?, ?> firstEntry = map.entrySet().iterator().next();
                  language = firstEntry.getKey().toString();
                  exampleValue = firstEntry.getValue();
               }
               default -> language = Locale.ENGLISH.getLanguage();
            }
         }

         final Resource xsdResource = ResourceFactory.createResource( scalar.getUrn() );
         boolean isTimezoneAvailable = false;
         if ( ( XSD.dateTime.equals( xsdResource ) || XSD.dateTimeStamp.equals( xsdResource ) )
               && exampleValue instanceof XMLGregorianCalendar ) {
            final XMLGregorianCalendar xmlCal = (XMLGregorianCalendar) exampleValue;
            if ( xmlCal.getTimezone() != DatatypeConstants.FIELD_UNDEFINED ) {
               isTimezoneAvailable = true;
            }
         }

         final PrimitiveType parquetType = ParquetSchemaMapper.mapToAnnotatedPrimitiveType(
               scalar.getUrn(), columnName, language, isTimezoneAvailable, maxLength );
         flattenedData.put( language == null ? columnName : columnName + "-" + language, new Tuple2<>( exampleValue, parquetType ) );
      }
   }

   private void extractComplexTypeProperties( final ComplexType complexType, final String prefix,
         final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final Set<String> visitedTypes ) {

      final String typeKey = complexType.urn().toString();
      if ( visitedTypes.contains( typeKey ) ) {
         return; // Prevent infinite recursion
      }
      visitedTypes.add( typeKey );

      try {
         for ( final Property property : complexType.getAllProperties() ) {
            if ( property.isNotInPayload() ) {
               continue;
            }
            extractPropertyData( property, prefix, flattenedData, new HashSet<>( visitedTypes ) );
         }
      } finally {
         visitedTypes.remove( typeKey );
      }
   }

   private void extractEitherData( final Either either, final Property property,
         final String columnName, final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final Set<String> visitedTypes, final BigInteger maxLength ) {
      extractCharacteristicData( either.getLeft(), property, columnName + "__left", flattenedData, visitedTypes, maxLength );
   }

   private void extractScalarOrEntityData( final Characteristic characteristic, final Property property,
         final String columnName, final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final Set<String> visitedTypes, final BigInteger maxLength ) {

      final Type dataType = characteristic.getDataType().orElse( null );
      if ( dataType == null ) {
         return;
      }

      if ( dataType instanceof final ComplexType complexType ) {
         extractComplexTypeProperties( complexType, columnName, flattenedData, visitedTypes );
      } else if ( dataType instanceof final Scalar scalar ) {
         columnNames.add( columnName );

         Object exampleValue = extractExampleValueFromProperty( property, characteristic );
         String language = null;
         if ( RDF.langString.getURI().equals( scalar.getUrn() ) ) {
            switch ( exampleValue ) {
               case final LangString langString -> {
                  language = Optional.ofNullable( langString.getLanguageTag() ).map( Locale::getLanguage ).orElse( null );
                  exampleValue = langString.getValue();
               }
               case final Map<?, ?> map when !map.isEmpty() -> {
                  final Map.Entry<?, ?> firstEntry = map.entrySet().iterator().next();
                  language = firstEntry.getKey().toString();
                  exampleValue = firstEntry.getValue();
               }
               default -> language = Locale.ENGLISH.getLanguage();
            }
         }
         final Resource xsdResource = ResourceFactory.createResource( scalar.getUrn() );
         boolean isTimezoneAvailable = false;
         if ( ( XSD.dateTime.equals( xsdResource ) || XSD.dateTimeStamp.equals( xsdResource ) )
               && exampleValue instanceof final XMLGregorianCalendar xmlCal
               && xmlCal.getTimezone() != DatatypeConstants.FIELD_UNDEFINED ) {
            isTimezoneAvailable = true;
         }

         final PrimitiveType parquetType = ParquetSchemaMapper.mapToAnnotatedPrimitiveType(
               scalar.getUrn(), columnName, language, isTimezoneAvailable, maxLength );
         flattenedData.put( language == null ? columnName : columnName + "-" + language, new Tuple2<>( exampleValue, parquetType ) );
      }
   }

   private Object extractExampleValueFromProperty( final Property property, final Characteristic characteristic ) {
      if ( property.getExampleValue().isPresent() ) {
         final Value exampleValue = property.getExampleValue().orElse( null );
         return extractActualValue( exampleValue );
      }

      if ( characteristic instanceof final Enumeration enumeration
            && !enumeration.getValues().isEmpty() ) {
         final Value firstValue = enumeration.getValues().getFirst();
         return extractActualValue( firstValue );
      }

      final Characteristic effectiveCharacteristic = property.getCharacteristic().orElse( characteristic );
      if ( effectiveCharacteristic != null ) {
         return effectiveCharacteristic.accept( parquetValueGenerator, new ParquetExampleValueGenerator.Context() );
      }
      return null;
   }

   private Object extractActualValue( final Value value ) {
      if ( value instanceof final ScalarValue scalarValue ) {
         return scalarValue.getValue();
      }
      return value == null ? null : value.toString();
   }

   private List<Map<String, Object>> createDenormalizedRows( final Aspect aspect,
         final Map<String, Tuple2<Object, PrimitiveType>> flattenedData ) {
      final List<Map<String, Object>> rows = new ArrayList<>();
      final Map<String, List<Property>> collectionsMap = new HashMap<>();

      identifyCollections( aspect.getProperties(), "", collectionsMap );

      if ( collectionsMap.isEmpty() ) {
         final Map<String, Object> singleRow = new HashMap<>();
         flattenedData.forEach( ( key, value ) -> singleRow.put( key, value._1 ) );
         rows.add( singleRow );
      } else {
         createRowsForCollections( aspect, flattenedData, rows );
      }

      return rows;
   }

   private void createRowsForCollections( final Aspect aspect,
         final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final List<Map<String, Object>> rows ) {

      for ( final Property property : aspect.getProperties() ) {
         if ( property.isNotInPayload() ) {
            continue;
         }

         final String propertyName = property.getPayloadName();
         final Map<String, Object> row = new HashMap<>();

         flattenedData.keySet().forEach( key -> row.put( key, null ) );
         fillRowForProperty( property, propertyName, row, flattenedData );

         if ( row.values().stream().anyMatch( Objects::nonNull ) ) {
            rows.add( row );
         }
      }
   }

   private void identifyCollections( final List<Property> properties, final String prefix,
         final Map<String, List<Property>> collectionsMap ) {
      for ( final Property property : properties ) {
         if ( property.isNotInPayload() || property.getCharacteristic().isEmpty() ) {
            continue;
         }

         final String propertyPath = prefix.isEmpty() ? property.getPayloadName() : prefix + "__" + property.getPayloadName();
         final Characteristic characteristic = property.getCharacteristic().orElse( null );

         if ( characteristic instanceof java.util.Collection ) {
            collectionsMap.computeIfAbsent( propertyPath, k -> new ArrayList<>() ).add( property );
         } else if ( characteristic != null && characteristic.getDataType().isPresent()
               && characteristic.getDataType().orElse( null ) instanceof final ComplexType complexType
               && columnNames.contains( propertyPath ) ) {
            identifyCollections( complexType.getAllProperties(), propertyPath, collectionsMap );
         }
      }
   }

   private void fillRowForProperty( final Property property, final String prefix,
         final Map<String, Object> row, final Map<String, Tuple2<Object, PrimitiveType>> flattenedData ) {

      if ( property.getCharacteristic().isEmpty() ) {
         return;
      }

      final Characteristic characteristic = property.getCharacteristic().orElse( null );

      if ( characteristic instanceof final Collection collection ) {
         fillRowForCollection( collection, property, prefix, row );
      } else if ( characteristic != null && characteristic.getDataType().isPresent()
            && characteristic.getDataType().orElse( null ) instanceof final ComplexType complexType ) {
         fillRowForComplexType( complexType, prefix, row, flattenedData );
      } else if ( characteristic != null ) {
         final Object value = extractExampleValueFromProperty( property, characteristic );
         row.put( prefix, value );
      }
   }

   private void fillRowForCollection( final Collection collection, final Property property, final String prefix,
         final Map<String, Object> row ) {

      final Type dataType = collection.getDataType().orElse( null );
      if ( dataType instanceof final ComplexType complexType ) {
         for ( final Property complexProperty : complexType.getAllProperties() ) {
            if ( complexProperty.isNotInPayload() ) {
               continue;
            }
            final String columnName = prefix + "__" + complexProperty.getPayloadName();
            final Object value = extractExampleValueFromProperty( complexProperty,
                  complexProperty.getCharacteristic().orElse( null ) );
            row.put( columnName, value );
         }
      } else {
         final Object value = extractExampleValueFromProperty( property, collection );
         row.put( prefix, value );
      }
   }

   private void fillRowForComplexType( final ComplexType complexType, final String prefix,
         final Map<String, Object> row, final Map<String, Tuple2<Object, PrimitiveType>> flattenedData ) {

      for ( final Property complexProperty : complexType.getAllProperties() ) {
         if ( complexProperty.isNotInPayload() ) {
            continue;
         }
         final String columnName = prefix + "__" + complexProperty.getPayloadName();
         fillRowForProperty( complexProperty, columnName, row, flattenedData );
      }
   }

   static String getFirstPartBeforeDoubleUnderscore( final String input ) {
      if ( input != null && input.contains( "__" ) ) {
         return input.split( "__" )[0];
      }
      return input;
   }
}
