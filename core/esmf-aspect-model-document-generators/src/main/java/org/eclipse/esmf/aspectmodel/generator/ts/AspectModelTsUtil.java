/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.ts;

import static org.eclipse.esmf.aspectmodel.generator.ts.TsDataTypeMapping.resolveType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.esmf.aspectmodel.VersionInfo;
import org.eclipse.esmf.aspectmodel.generator.exception.CodeGenerationException;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.StructureElement;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.characteristic.Either;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.characteristic.Quantifiable;
import org.eclipse.esmf.metamodel.characteristic.State;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.UnicodeUnescaper;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

public class AspectModelTsUtil {

   public static final Converter<String, String> TO_CONSTANT = CaseFormat.UPPER_CAMEL.converterTo( CaseFormat.UPPER_UNDERSCORE );

   public static final UnicodeUnescaper UNESCAPER = new UnicodeUnescaper();

   public static final String CURRENT_DATE_ISO_8601 = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSX" ).format( new Date() );

   private AspectModelTsUtil() {
   }

   /**
    * Determines whether the property has a container type, i.e. it will result in an Optional, Collection or
    * something similar.
    *
    * @param property the property to check
    * @return {@code true} if the property has a container type, {@code false} else
    */
   public static boolean hasContainerType( final Property property ) {
      return property.isOptional()
            || ( property.getEffectiveCharacteristic().map( characteristic -> characteristic.is( Collection.class ) ).orElse( false ) );
   }

   /**
    * Determines whether the property has a Quantifiable characteristic that actually has a Unit assigned.
    *
    * @param characteristic the characteristic to check
    * @return {@code true} if the property carries a Unit, {@code false} else
    */
   public static boolean hasUnit( final Characteristic characteristic ) {
      if ( characteristic instanceof final Quantifiable quantifiable ) {
         return quantifiable.getUnit().isPresent();
      }
      return false;
   }

   public static String getCharacteristicTsType( final Property property, final TsCodeGenerationConfig codeGenerationConfig ) {
      final Supplier<RuntimeException> error = () -> new CodeGenerationException( "No data type found for Property " + property.getName() );
      if ( hasContainerType( property ) ) {
         return getDataType( property.getCharacteristic().orElseThrow( error ).getDataType(), codeGenerationConfig.importTracker(),
               codeGenerationConfig );
      }

      return property.getEffectiveCharacteristic().flatMap( Characteristic::getDataType ).map( type -> {
         if ( type.is( Scalar.class ) ) {
            return determinePropertyType( property.getEffectiveCharacteristic(), codeGenerationConfig );
         } else if ( type.is( Entity.class ) ) {
            return type.as( Entity.class ).getName();
         } else {
            throw new CodeGenerationException( "Unknown Characteristic data type " + type );
         }
      } ).orElseThrow( error );
   }

   /**
    * Determines the type of a property and wraps it in an Optional if it has been marked as optional.
    *
    * @param property the property instance to determine the type for
    * @param codeGenerationConfig the configuration for code generation
    * @return the final type of the property
    */
   public static String getPropertyType( final Property property, final TsCodeGenerationConfig codeGenerationConfig ) {
      if ( property.isAbstract() ) {
         return "any";
      }
      return determinePropertyType( property.getCharacteristic(), codeGenerationConfig );
   }

   public static String getPropertyTypeAndAddImport( final Property property, final TsCodeGenerationConfig codeGenerationConfig ) {
      final String propertyType = getPropertyType( property, codeGenerationConfig );
      codeGenerationConfig.importTracker().trackPotentiallyParameterizedType( propertyType );
      return propertyType;
   }

   /**
    * Determines the type of property
    *
    * @param optionalCharacteristic the {@link Characteristic} which describes the data type for a property
    * @return {@link String} containing the definition of the Ts Data Type for the property
    */
   public static String determinePropertyType( final Optional<Characteristic> optionalCharacteristic,
         final TsCodeGenerationConfig codeGenerationConfig ) {

      final Optional<Type> dataType = optionalCharacteristic.flatMap( Characteristic::getDataType );
      final Characteristic characteristic = optionalCharacteristic.orElseThrow( () ->
            new CodeGenerationException( "Can not determine type of missing Characteristic" ) );

      if ( characteristic.is( Collection.class ) ) {
         return determineCollectionType( characteristic.as( Collection.class ), codeGenerationConfig );
      }

      if ( characteristic.is( Enumeration.class ) ) {
         return characteristic.getName();
      }

      if ( characteristic.is( Trait.class ) ) {
         final Characteristic baseCharacteristic = characteristic.as( Trait.class ).getBaseCharacteristic();
         if ( baseCharacteristic.is( Collection.class ) ) {
            return determineCollectionType( baseCharacteristic.as( Collection.class ), codeGenerationConfig );
         }
      }

      if ( characteristic.is( Either.class ) ) {

         codeGenerationConfig.importTracker().importExplicit( "Either", "./core/Either" );

         final String left = determinePropertyType(
               optionalCharacteristic.map( c -> c.as( Either.class ) ).map( Either::getLeft ),
               codeGenerationConfig );
         final String right = determinePropertyType(
               optionalCharacteristic.map( c -> c.as( Either.class ) ).map( Either::getRight ),
               codeGenerationConfig );
         return String.format( "Either<%s, %s>", left, right );
      }

      return getDataType( dataType, codeGenerationConfig.importTracker(), codeGenerationConfig );
   }

   private static String determineCollectionType( final Collection collection, final TsCodeGenerationConfig codeGenerationConfig ) {
      final Optional<Type> dataType = collection.getDataType();
      return collectionType( getDataType( dataType, codeGenerationConfig.importTracker(), codeGenerationConfig ) );
   }

   public static String collectionType( final String elementType ) {
      return elementType + "[]";
   }

   /**
    * Determines the Ts Data Type
    *
    * @param dataType the raw data type
    * @param importTracker the import tracker
    * @return a {@link String} containing the definition of the Ts Data Type
    */
   public static String getDataType( final Optional<Type> dataType, final ImportTracker importTracker,
         final TsCodeGenerationConfig codeGenerationConfig ) {
      return dataType.map( type -> {
         final Type actualDataType = dataType.get();
         if ( actualDataType instanceof ComplexType ) {
            final String complexDataType = actualDataType.getName();
            if ( ( !codeGenerationConfig.namePrefix().isBlank() || !codeGenerationConfig.namePostfix().isBlank() ) ) {
               return codeGenerationConfig.namePrefix() + complexDataType + codeGenerationConfig.namePostfix();
            }
            return complexDataType;
         }

         if ( actualDataType instanceof Scalar ) {
            final Resource typeResource = ResourceFactory.createResource( actualDataType.getUrn() );
            if ( typeResource.getURI().equals( RDF.langString.getURI() ) ) {
               // TODO fix imports
               importTracker.importExplicit( "LangString", "./core/langString" );
               return "LangString";
            }
            final Class<?> result = SammXsdType.getJavaTypeForMetaModelType( typeResource );
            return resolveType( result );
         }

         throw new CodeGenerationException(
               "Could not determine Ts type for model type that is " + "neither Scalar nor Entity: " + type.getUrn() );
      } ).orElseThrow( () -> new CodeGenerationException( "Failed to determine Ts data type for empty model type" ) );
   }

   public static Class<?> getDataTypeClass( final Type dataType ) {
      if ( dataType instanceof final ComplexType complexType ) {
         return complexType.getClass();
      }

      final Resource typeResource = ResourceFactory.createResource( dataType.getUrn() );
      if ( typeResource.getURI().equals( RDF.langString.getURI() ) ) {
         return Map.class;
      }
      final Class<?> result = SammXsdType.getJavaTypeForMetaModelType( typeResource );
      return result;
   }

   /**
    * Convert a string given as upper or lower camel case into a constant format. For example {@code someVariable} would become
    * {@code SOME_VARIABLE}.
    *
    * @param upperOrLowerCamel the string to convert
    * @return the string formatted as a constant.
    */
   public static String toConstant( final String upperOrLowerCamel ) {
      if ( isAllUppercaseWithUnderscore( upperOrLowerCamel ) ) {
         return upperOrLowerCamel;
      }
      return TO_CONSTANT.convert( StringUtils.capitalize( upperOrLowerCamel ) );
   }

   public static boolean isAllUppercaseWithUnderscore( final String upperOrLowerCamel ) {
      return upperOrLowerCamel != null && upperOrLowerCamel.matches( "[A-Z0-9_]+" );
   }

   /**
    * Creates a string literal with escaped double quotes around the given string. The string is escaped using
    * {@link #escapeForLiteral(String)}.
    *
    * @param value the string to create the literal for
    * @return the literal
    */
   public static String createLiteral( final String value ) {
      return "\"" + escapeForLiteral( value ) + "\"";
   }

   /**
    * Escapes a string properly to be used as a literal. Performs escaping according to Ts String rules and afterwards additionally
    * translates escaped Unicode characters back to Unicode. The latter step is necessary to avoid Unicode escape sequences within the
    * String literal.
    *
    * @param value the string to be escaped
    * @return the escaped string
    */
   public static String escapeForLiteral( final String value ) {
      return UNESCAPER.translate( StringEscapeUtils.escapeJava( value ) );
   }

   /**
    * Takes a class body with FQCNs and replaces them with applied imports (i.e. simply use the class name).
    */
   //TODO potentially can be removed
   public static String applyImports( final String body, final TsCodeGenerationConfig codeGenerationConfig ) {
      //      String importsApplied = body;
      //      for ( final QualifiedName oneImport : codeGenerationConfig.importTracker().getUsedImports() ) {
      //         final String className = oneImport.modulePath().substring( oneImport.modulePath().lastIndexOf( '.' ) + 1 );
      //         importsApplied = importsApplied.replaceAll( oneImport.modulePath(), className );
      //      }
      return body;
   }

   public static List<Property> getAllProperties( final ComplexType element ) {
      final List<Property> allProperties = new ArrayList<>( element.getProperties() );
      if ( element.getExtends().isPresent() ) {
         final ComplexType extendedComplexType = element.getExtends().get();
         final List<Property> allPropertiesFromExtendedComplexType = getAllProperties( extendedComplexType );
         allProperties.addAll( allPropertiesFromExtendedComplexType );
      }
      return allProperties;
   }

   public static boolean doesValueNeedToBeQuoted( final String typeUrn ) {
      return typeUrn.equals( XSD.integer.getURI() )
            || typeUrn.equals( XSD.xshort.getURI() )
            || typeUrn.equals( XSD.decimal.getURI() )
            || typeUrn.equals( XSD.unsignedLong.getURI() )
            || typeUrn.equals( XSD.positiveInteger.getURI() )
            || typeUrn.equals( XSD.nonNegativeInteger.getURI() )
            || typeUrn.equals( XSD.negativeInteger.getURI() )
            || typeUrn.equals( XSD.nonPositiveInteger.getURI() )
            || typeUrn.equals( XSD.date.getURI() )
            || typeUrn.equals( XSD.time.getURI() )
            || typeUrn.equals( XSD.dateTime.getURI() )
            || typeUrn.equals( XSD.dateTimeStamp.getURI() )
            || typeUrn.equals( XSD.gDay.getURI() )
            || typeUrn.equals( XSD.gMonth.getURI() )
            || typeUrn.equals( XSD.gYearMonth.getURI() )
            || typeUrn.equals( XSD.gMonthDay.getURI() )
            || typeUrn.equals( XSD.duration.getURI() )
            || typeUrn.equals( XSD.yearMonthDuration.getURI() )
            || typeUrn.equals( XSD.dayTimeDuration.getURI() );
   }

   //TODO investigate TS generics
   public static String genericClassSignature( final StructureElement element ) {
      final List<Property> properties = element.getProperties();
      final String generics = IntStream.range( 0, properties.size() )
            .filter( i -> properties.get( i ).isAbstract() )
            .mapToObj( i -> "T" + i + " /* type of " + properties.get( i ).getName() + " */" )
            .collect( Collectors.joining( "," ) );
      return generics.isEmpty() ? "" : "<" + generics + ">";
   }

   public static String generateClassName( final ModelElement element, final TsCodeGenerationConfig config ) {
      if ( ( !config.namePrefix().isBlank() || !config.namePostfix().isBlank() ) && element.is( StructureElement.class ) ) {
         return config.namePrefix() + element.getName() + config.namePostfix();
      }
      return element.getName();
   }

   public static String staticPropertiesExpression( final String className, final StructureElement element ) {
      return element.getProperties().stream()
            .filter( property -> !property.isAbstract() )
            .map( property -> className + "." + toConstant( property.getName() ) )
            .collect( Collectors.joining( ", " ) );
   }

   public static String generateEnumKey( final Value value ) {
      return value.accept( new ValueToEnumKeyVisitor(), null );
   }

   public static String generateEnumValue( final Value value, final TsCodeGenerationConfig codeGenerationConfig ) {
      final ValueExpressionVisitor.Context context = new ValueExpressionVisitor.Context( codeGenerationConfig, false );
      return value.accept( new ValueExpressionVisitor(), context );
   }

   public static ComplexType castToComplexType( final StructureElement structureElement ) {
      return (ComplexType) structureElement;
   }

   public static State castToState( final Enumeration enumeration ) {
      return (State) enumeration;
   }

   public static String codeGeneratorName() {
      return "esmf-sdk " + VersionInfo.ESMF_SDK_VERSION;
   }

   //TODO review comlex type generics
   public static String determineComplexTypeClassDefinition( final ComplexType element,
         final TsCodeGenerationConfig codeGenerationConfig ) {
      final StringBuilder classDefinitionBuilder = new StringBuilder( "export " );
      if ( element.isAbstractEntity() ) {
         classDefinitionBuilder.append( "abstract " );
      }
      classDefinitionBuilder.append( "class " ).append( generateClassName( element, codeGenerationConfig ) );
      classDefinitionBuilder.append( genericClassSignature( element ) );
      if ( element.getExtends().isPresent() ) {
         final ComplexType extendedComplexType = element.getExtends().get();
         classDefinitionBuilder.append( " extends " );
         classDefinitionBuilder.append( extendedComplexType.getName() );
         codeGenerationConfig.importTracker().trackPotentiallyParameterizedType( extendedComplexType.getName() );
         final String generics = element.getProperties().stream()
               .filter( property -> property.getExtends().isPresent() )
               .map( property -> getPropertyType( property, codeGenerationConfig ) )
               .collect( Collectors.joining( "," ) );
         final String superTypeGenerics = generics.isEmpty() ? "" : "<" + generics + ">";
         classDefinitionBuilder.append( superTypeGenerics );
      }
      classDefinitionBuilder.append( " {" );
      return classDefinitionBuilder.toString();
   }

   public static String determineCollectionAspectClassDefinition( final StructureElement element,
         final TsCodeGenerationConfig codeGenerationConfig ) {
      final Supplier<RuntimeException> error = () -> new CodeGenerationException(
            "Tried to generate a Collection Aspect class definition, but no " + "Property has a Collection Characteristic in "
                  + element.getName() );
      // TODO fix imports
      codeGenerationConfig.importTracker().importExplicit( "CollectionAspect", "./core/collectionAspect" );
      for ( final Property property : element.getProperties() ) {
         final Characteristic characteristic = property.getEffectiveCharacteristic().orElseThrow( error );
         if ( characteristic instanceof Collection characteristicCollection ) {
            final String collectionType = determineCollectionType( characteristicCollection, codeGenerationConfig );
            final String dataType = getDataType( characteristicCollection.getDataType(), codeGenerationConfig.importTracker(),
                  codeGenerationConfig );
            return String.format( "export class %s extends CollectionAspect<%s,%s>", generateClassName( element, codeGenerationConfig ),
                  collectionType, dataType );
         }
      }
      throw error.get();
   }
}
