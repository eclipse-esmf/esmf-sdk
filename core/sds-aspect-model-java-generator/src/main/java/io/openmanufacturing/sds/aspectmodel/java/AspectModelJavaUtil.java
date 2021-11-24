/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.java;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.java.exception.CodeGenerationException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.DataType;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.metamodel.AbstractEntity;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Collection;
import io.openmanufacturing.sds.metamodel.ComplexType;
import io.openmanufacturing.sds.metamodel.Either;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Enumeration;
import io.openmanufacturing.sds.metamodel.HasProperties;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Quantifiable;
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.SortedSet;
import io.openmanufacturing.sds.metamodel.StructureElement;
import io.openmanufacturing.sds.metamodel.Trait;
import io.openmanufacturing.sds.metamodel.Type;

public class AspectModelJavaUtil {

   public static final Converter<String, String> TO_CONSTANT = CaseFormat.UPPER_CAMEL.converterTo( CaseFormat.UPPER_UNDERSCORE );

   private AspectModelJavaUtil() {
   }

   /**
    * Determines the type of a property and wraps it in an Optional if it has been marked as optional.
    *
    * @param metaProperty the property meta model instance to determine the type for
    * @param codeGenerationConfig the configuration for code generation
    * @return the final type of the property
    */
   public static String getPropertyType( final Property metaProperty, final boolean inclValidation, JavaCodeGenerationConfig codeGenerationConfig ) {
      final String propertyType = determinePropertyType( metaProperty.getCharacteristic(), inclValidation, codeGenerationConfig );
      if ( metaProperty.isOptional() ) {
         return containerType( Optional.class, propertyType, Optional.empty() );
      }
      return propertyType;
   }

   /**
    * Resolves and tracks data type of the given meta model property.
    *
    * @param metaProperty the meta model property to resolve the data type for
    * @return the fully qualified class name (potentially including type parameters) of the resolved data type
    */
   public static String getPropertyType( final Property metaProperty, JavaCodeGenerationConfig codeGenerationConfig ) {
      final String propertyType = determinePropertyType( metaProperty.getCharacteristic(), false, codeGenerationConfig );
      codeGenerationConfig.getImportTracker().trackPotentiallyParameterizedType( propertyType );
      if ( metaProperty.isOptional() ) {
         return containerType( Optional.class, propertyType, Optional.empty() );
      }
      return propertyType;
   }

   /**
    * Determines whether the property has a container type, i.e. it will result in an Optional, Collection or
    * something similar.
    *
    * @param metaProperty the property to check
    * @return {@code true} if the property has a container type, {@code false} else
    */
   public static boolean hasContainerType( final Property metaProperty ) {
      return metaProperty.isOptional() || (metaProperty.getEffectiveCharacteristic() instanceof Collection)
            || metaProperty.getDataType().map( dataType -> dataType.getUrn().equals( RDF.langString.getURI() ) )
            .orElse( false );
   }

   /**
    * Determines whether the property has a Quantifiable characteristic that actually has a Unit assigned.
    *
    * @param characteristic the characteristic to check
    * @return {@code true} if the property carries a Unit, {@code false} else
    */
   public static boolean hasUnit( final Characteristic characteristic ) {
      if ( characteristic instanceof Quantifiable ) {
         final Quantifiable quantifiable = (Quantifiable) characteristic;
         return quantifiable.getUnit().isPresent();
      }
      return false;
   }

   /**
    * Determines the type of a property
    *
    * @param characteristic the {@link Characteristic} which describes the data type for a property
    * @param inclValidation a boolean indicating whether the element validation annotations should be included for
    *       the Collection declarations
    * @return {@link String} containing the definition of the Java Data Type for the property
    */
   public static String determinePropertyType( final Characteristic characteristic,
         final boolean inclValidation, final JavaCodeGenerationConfig codeGenerationConfig ) {
      final Optional<Type> dataType = characteristic.getDataType();

      if ( characteristic instanceof Collection ) {
         return determineCollectionType( (Collection) characteristic, inclValidation, codeGenerationConfig );
      }

      if ( characteristic instanceof Enumeration ) {
         return characteristic.getName();
      }

      if ( characteristic instanceof Trait ) {
         final Characteristic baseCharacteristic = ((Trait) characteristic).getBaseCharacteristic();
         if ( baseCharacteristic instanceof Collection ) {
            return determineCollectionType( (Collection) baseCharacteristic, inclValidation, codeGenerationConfig );
         }
      }

      if ( characteristic instanceof Either ) {
         if ( codeGenerationConfig.doEnableJacksonAnnotations() ) {
            codeGenerationConfig.getImportTracker().importExplicit( "io.openmanufacturing.sds.aspectmodel.jackson.Either" );
         } else {
            codeGenerationConfig.getImportTracker().importExplicit( io.openmanufacturing.sds.aspectmodel.java.types.Either.class );
         }
         String left = determinePropertyType( ((Either) characteristic).getLeft(), inclValidation, codeGenerationConfig );
         String right = determinePropertyType( ((Either) characteristic).getRight(), inclValidation, codeGenerationConfig );
         return String.format( "Either<%s,%s>", left, right );
      }

      return getDataType( dataType, codeGenerationConfig );
   }

   public static String determineCollectionAspectClassDefinition( final StructureElement element, final JavaCodeGenerationConfig codeGenerationConfig ) {
      codeGenerationConfig.getImportTracker().importExplicit( CollectionAspect.class );
      for ( final Property property : element.getProperties() ) {
         final Characteristic characteristic = property.getEffectiveCharacteristic();
         if ( characteristic instanceof Collection ) {
            final String collectionType = determineCollectionType( (Collection) characteristic, false, codeGenerationConfig );
            final String dataType = getDataType( characteristic.getDataType(), codeGenerationConfig );
            return String.format( "public class %s implements CollectionAspect<%s,%s>",
                  element.getName(), collectionType, dataType );
         }
      }
      throw new CodeGenerationException( "Tried to generate a Collection Aspect class definition, but no "
            + "Property has a Collection Characteristic in " + element.getName() );
   }

   public static String determineComplexTypeClassDefinition( final ComplexType element ) {
      final StringBuilder classDefinitionBuilder = new StringBuilder( "public " );
      if ( element.isAbstractEntity() ) {
         classDefinitionBuilder.append( "abstract " );
      }
      classDefinitionBuilder.append( "class " ).append( element.getName() );
      if ( element.getExtends().isPresent() ) {
         final ComplexType extendedComplexType = element.getExtends().get();
         classDefinitionBuilder.append( " extends " ).append( extendedComplexType.getName() );
      }
      classDefinitionBuilder.append( " {" );
      return classDefinitionBuilder.toString();
   }

   public static String generateAbstractEntityClassAnnotations( final ComplexType element, final JavaCodeGenerationConfig codeGenerationConfig ) {
      final StringBuilder classAnnotationBuilder = new StringBuilder();
      if ( element.isAbstractEntity() ) {
         codeGenerationConfig.getImportTracker().importExplicit( JsonTypeInfo.class );
         codeGenerationConfig.getImportTracker().importExplicit( JsonSubTypes.class );

         final AbstractEntity abstractEntity = (AbstractEntity) element;
         classAnnotationBuilder.append( "@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)" );
         classAnnotationBuilder.append( "@JsonSubTypes({" );
         final Iterator<ComplexType> extendingComplexTypeIterator = abstractEntity.getExtendingElements().iterator();
         while ( extendingComplexTypeIterator.hasNext() ) {
            final ComplexType extendingComplexType = extendingComplexTypeIterator.next();
            classAnnotationBuilder.append( "@JsonSubTypes.Type(value = " );
            classAnnotationBuilder.append( extendingComplexType.getName() );
            classAnnotationBuilder.append( ".class, name = \"" );
            classAnnotationBuilder.append( extendingComplexType.getName() );
            classAnnotationBuilder.append( "\")" );
            if ( extendingComplexTypeIterator.hasNext() ) {
               classAnnotationBuilder.append( "," );
            }
         }
         classAnnotationBuilder.append( "})" );
      }
      return classAnnotationBuilder.toString();
   }

   private static String determineCollectionType( final Collection collection, final boolean inclValidation,
         final JavaCodeGenerationConfig codeGenerationConfig ) {
      final Optional<Type> dataType = collection.getDataType();

      final Optional<String> elementConstraint = inclValidation ?
            buildConstraintForCollectionElements( collection, codeGenerationConfig ) :
            Optional.empty();

      if ( collection.isAllowDuplicates() && collection.isOrdered() ) {
         codeGenerationConfig.getImportTracker().importExplicit( List.class );
         return containerType( List.class, getDataType( dataType, codeGenerationConfig ), elementConstraint );
      }
      if ( !collection.isAllowDuplicates() && collection.isOrdered() ) {
         codeGenerationConfig.getImportTracker().importExplicit( LinkedHashSet.class );
         return containerType( LinkedHashSet.class, getDataType( dataType, codeGenerationConfig ), elementConstraint );
      }
      if ( collection.isAllowDuplicates() && !collection.isOrdered() ) {
         codeGenerationConfig.getImportTracker().importExplicit( java.util.Collection.class );
         return containerType( java.util.Collection.class, getDataType( dataType, codeGenerationConfig ), elementConstraint );
      }
      if ( !collection.isAllowDuplicates() && !collection.isOrdered() ) {
         codeGenerationConfig.getImportTracker().importExplicit( Set.class );
         return containerType( Set.class, getDataType( dataType, codeGenerationConfig ), elementConstraint );
      }
      throw new CodeGenerationException( "Could not determine Java collection type for " + collection.getName() );
   }

   private static Optional<String> buildConstraintForCollectionElements( final Collection collection, final JavaCodeGenerationConfig codeGenerationConfig ) {
      return collection.getElementCharacteristic()
            .filter( elementCharacteristic -> Trait.class
                  .isAssignableFrom( elementCharacteristic.getClass() ) )
            .map( elementCharacteristic -> buildConstraintsForCharacteristic( (Trait) elementCharacteristic, codeGenerationConfig ) );
   }

   public static String containerType( final Class<?> containerClass, final String elementType,
         final Optional<String> elementConstraint ) {
      final StringBuilder containerTypeBuilder = new StringBuilder().append( containerClass.getName() )
            .append( "<" );
      elementConstraint.ifPresent( containerTypeBuilder::append );
      containerTypeBuilder.append( elementType ).append( ">" );
      return containerTypeBuilder.toString();
   }

   @SuppressWarnings( "squid:S1166" ) // Exception is thrown as regular part of logic
   public static boolean classAnyOf( final Set<Class<?>> classes, final String className ) {
      try {
         final Class<?> clazz = Class.forName( className );
         return classes.stream().anyMatch( clazz2 -> clazz2.isAssignableFrom( clazz ) );
      } catch ( final ClassNotFoundException cnf ) {
         return false;
      }
   }

   /**
    * Determines the Java Data Type
    *
    * @param dataType the raw data type
    * @param codeGenerationConfig the configuration for code generation
    * @return a {@link String} containing the definition of the Java Data Type
    */
   public static String getDataType( final Optional<Type> dataType, final JavaCodeGenerationConfig codeGenerationConfig ) {
      return dataType.map( type -> {
         final Type actualDataType = dataType.get();
         if ( actualDataType instanceof ComplexType ) {
            return ((ComplexType) actualDataType).getName();
         }

         if ( actualDataType instanceof Scalar ) {
            final Resource typeResource = ResourceFactory.createResource( actualDataType.getUrn() );
            if ( typeResource.getURI().equals( RDF.langString.getURI() ) ) {
               codeGenerationConfig.getImportTracker().importExplicit( java.util.Map.class );
               codeGenerationConfig.getImportTracker().importExplicit( java.util.Locale.class );
               return "Map<Locale, String>";
            }
            final Class<?> result = DataType.getJavaTypeForMetaModelType( typeResource, actualDataType.getMetaModelVersion() );
            codeGenerationConfig.getImportTracker().importExplicit( result );
            return result.getTypeName();
         }

         throw new CodeGenerationException( "Could not determine Java type for model type that is "
               + "neither Scalar nor Entity: " + type.getUrn() );
      } ).orElseThrow(
            () -> new CodeGenerationException( "Failed to determine Java data type for empty model type" ) );
   }

   /**
    * Convert a string given as upper or lower camel case into a constant format.
    *
    * For example {@code someVariable} would become {@code SOME_VARIABLE}.
    *
    * @param upperOrLowerCamelString the string to convert
    * @return the string formatted as a constant.
    */
   public static String toConstant( final String upperOrLowerCamelString ) {
      return TO_CONSTANT.convert( StringUtils.capitalize( upperOrLowerCamelString ) );
   }

   public static String createLiteral( final String value ) {
      return "\"" + StringEscapeUtils.escapeJava( value ) + "\"";
   }

   /**
    * Generates an enum key based on the given value
    *
    * @param optionalType the raw data type of the given value
    * @param value the actual value to generate the enum key for
    * @param codeGenerationConfig the configuration for code generation
    * @return a string representing the enum key
    */
   public static String generateEnumKey( final Optional<Type> optionalType, final Object value, final JavaCodeGenerationConfig codeGenerationConfig ) {
      final String dataType = getDataType( optionalType, codeGenerationConfig );
      if ( isNumberType( dataType ) ) {
         return "NUMBER_" + value.toString().replaceAll( "[^\\p{Alnum}]", "_" );
      }
      if ( isEntityType( optionalType ) ) {
         return optionalType.map( type -> {
            @SuppressWarnings( "unchecked" ) final String valueAsString = ((Map<String, String>) value)
                  .get( new BAMM( type.getMetaModelVersion() ).name().toString() );
            return toConstant( valueAsString.substring( valueAsString.lastIndexOf( '#' ) + 1 ) );
         } ).orElse( generateDefaultEnumKey( value ) );
      }
      return generateDefaultEnumKey( value );
   }

   private static String generateDefaultEnumKey( final Object value ) {
      return value.toString().replaceAll( "([^\\p{Alnum}_])", "_" ).replaceAll( "^[^\\p{Alpha}_]", "_" )
            .toUpperCase();
   }

   public static boolean isNumberType( final String dataTypeName ) {
      return classAnyOf( Set.of( Number.class ), dataTypeName );
   }

   public static boolean isEntityType( final Optional<Type> dataType ) {
      return dataType.map( type -> type instanceof Entity ).orElse( false );
   }

   /**
    * Takes a class body with FQCNs and replaces them with applied imports (i.e. simply use the class name).
    */
   public static String applyImports( final String body, final JavaCodeGenerationConfig codeGenerationConfig ) {
      String importsApplied = body;
      for ( final String oneImport : codeGenerationConfig.getImportTracker().getUsedImports() ) {
         final String className = oneImport.substring( oneImport.lastIndexOf( '.' ) + 1 );
         importsApplied = importsApplied.replaceAll( oneImport, className );
      }
      return importsApplied;
   }

   public static boolean isPropertyNotInPayload( final Property property, final JavaCodeGenerationConfig codeGenerationConfig ) {
      if ( property.isNotInPayload() ) {
         codeGenerationConfig.getImportTracker().importExplicit( "com.fasterxml.jackson.annotation.JsonIgnore" );
         return true;
      }
      return false;
   }

   public static String buildConstraintsForCharacteristic( final Trait trait, final JavaCodeGenerationConfig codeGenerationConfig ) {
      return trait.getConstraints().stream().map( constraint ->
            new ConstraintAnnotationBuilder().setConstraintClass( constraint )
                  .setImportTracker( codeGenerationConfig.getImportTracker() )
                  .build() ).collect( Collectors.joining() );
   }

   public static boolean anyPropertyNotInPayload( final HasProperties element ) {
      return element.getProperties().stream().anyMatch( Property::isNotInPayload );
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

   public static List<Property> getAllPropertiesInPayload( final ComplexType element ) {
      final List<Property> allPropertiesInPayload = getPropertiesInPayload( element );
      if ( element.getExtends().isPresent() ) {
         final ComplexType extendedComplexType = element.getExtends().get();
         final List<Property> allPropertiesFromExtendedComplexType = getPropertiesInPayload( extendedComplexType );
         allPropertiesInPayload.addAll( allPropertiesFromExtendedComplexType );
      }
      return allPropertiesInPayload;
   }

   public static List<Property> getPropertiesInPayload( final HasProperties element ) {
      final Predicate<Property> notInPayload = Property::isNotInPayload;
      final Predicate<Property> inPayload = notInPayload.negate();
      return element.getProperties().stream().filter( inPayload ).collect( Collectors.toList() );
   }

   public static String generateInitializer( final Property property, final String value, final JavaCodeGenerationConfig codeGenerationConfig,
         final ValueInitializer valueInitializer ) {
      return property.getDataType().map( type -> {
         final Resource typeResource = ResourceFactory.createResource( type.getUrn() );
         final KnownVersion metaModelVersion = property.getMetaModelVersion();
         final Class<?> result = DataType.getJavaTypeForMetaModelType( typeResource, metaModelVersion );
         codeGenerationConfig.getImportTracker().importExplicit( result );
         return valueInitializer.apply( typeResource, value, metaModelVersion );
      } ).orElseThrow( () -> new CodeGenerationException(
            "The Either Characteristic is not allowed for Properties used as elements in a StructuredValue" ) );
   }

   public static String generateEnumValue( final Optional<Type> valueDataType, final Object value, final boolean isOptional,
         final JavaCodeGenerationConfig codeGenerationConfig ) {
      final String dataTypeName = getDataType( valueDataType, codeGenerationConfig );
      if ( dataTypeName.contains( "String" ) ) {
         final String escapedValue = StringEscapeUtils.escapeJava( (String) value );
         return isOptional ? String.format( "Optional.of(\"%s\")", escapedValue ) : "\"" + escapedValue + "\"";
      }

      if ( dataTypeName.contains( "Boolean" ) ) {
         return value.toString();
      }

      if ( isNumberType( dataTypeName ) ) {
         return generateNumericEnumValue( value, isOptional, dataTypeName );
      }

      if ( isEntityType( valueDataType ) ) {
         return generateEntityEnumValue( valueDataType, value, codeGenerationConfig, dataTypeName );
      }

      return value.toString();
   }

   private static String generateEntityEnumValue( final Optional<Type> valueDataType, final Object value, final JavaCodeGenerationConfig codeGenerationConfig,
         final String dataTypeName ) {
      return valueDataType.map( type -> {
         final Entity entity = (Entity) type;
         return String.format( "new %s(%s)", dataTypeName, entity.getProperties().stream().map( property ->
                     generateComplexValue( property, value, codeGenerationConfig ) )
               .collect( Collectors.joining( "," ) ) );
      } ).orElseThrow( () -> new CodeGenerationException( "Can not generate enum value for empty type" ) );
   }

   private static String generateNumericEnumValue( final Object value, final boolean isOptional,
         final String dataTypeName ) {
      if ( needsValueOf( dataTypeName ) ) {
         return isOptional ? String.format( "Optional.of(%s.valueOf(%s))", dataTypeName, value ) :
               String.format( "%s.valueOf(%s)", dataTypeName, value );
      }
      if ( needsValueOfString( dataTypeName ) ) {
         return isOptional ? String.format( "Optional.of(%s.valueOf(\"%s\"))", dataTypeName, value ) :
               String.format( "%s.valueOf(\"%s\")", dataTypeName, value );
      }
      return isOptional ? String.format( "Optional.of(%s)", value ) : value.toString();
   }

   private static String generateComplexValue( final Property property, final Object valueMap, final JavaCodeGenerationConfig codeGenerationConfig ) {

      if ( property.getCharacteristic() instanceof io.openmanufacturing.sds.metamodel.Set ) {
         @SuppressWarnings( "unchecked" ) final Set<Object> objects = (((Map<String, Set<Object>>) valueMap)
               .get( property.getName() ));
         final String values = objects.stream().map( value ->
                     generateEnumValue( property.getCharacteristic().getDataType(), value, false, codeGenerationConfig ) )
               .collect( Collectors.joining( "," ) );
         return String.format( "Set.of(%s)", values );
      }

      if ( property.getCharacteristic() instanceof SortedSet ) {
         codeGenerationConfig.getImportTracker().importExplicit( LinkedHashSet.class );
         @SuppressWarnings( "unchecked" ) final Set<Object> objects = (((Map<String, Set<Object>>) valueMap)
               .get( property.getName() ));
         final String values = objects.stream().map( value ->
                     generateEnumValue( property.getCharacteristic().getDataType(), value, false, codeGenerationConfig ) )
               .map( value -> String.format( "add(%s);", value ) )
               .collect( Collectors.joining() );

         final Optional<Class<?>> setType = objects.stream().findAny().map( Object::getClass );
         setType.ifPresent( codeGenerationConfig.getImportTracker()::importExplicit );
         final String classLabel = setType.map( Class::getSimpleName ).orElse( "Object" );
         return String.format( "new LinkedHashSet<%s>(){{ %s }}", classLabel, values );
      }

      if ( property.getCharacteristic() instanceof io.openmanufacturing.sds.metamodel.List
            || property.getCharacteristic() instanceof io.openmanufacturing.sds.metamodel.Collection ) {
         @SuppressWarnings( "unchecked" ) final List<Object> objects = (((Map<String, ArrayList<Object>>) valueMap)
               .get( property.getName() ));
         final String values = objects.stream().map( value ->
                     generateEnumValue( property.getCharacteristic().getDataType(), value, false, codeGenerationConfig ) )
               .collect( Collectors.joining( "," ) );
         return String.format( "List.of(%s)", values );
      }
      //noinspection unchecked
      return generateEnumValue( property.getCharacteristic().getDataType(),
            ((Map<String, Object>) valueMap).get( property.getName() ), property.isOptional(), codeGenerationConfig );
   }

   public static boolean needsValueOf( final String dataTypeName ) {
      return classAnyOf( Set.of( BigInteger.class, BigDecimal.class ), dataTypeName );
   }

   public static boolean needsValueOfString( final String dataTypeName ) {
      return classAnyOf( Set.of( Short.class, Byte.class ), dataTypeName );
   }

   public static String generateFilterCompare( final Optional<Type> optionalDataType ) {
      final Type dataType = optionalDataType.orElseThrow( () ->
            new CodeGenerationException( "Could not generate equals expression for empty Enumeration datatype" ) );

      if ( dataType instanceof Scalar ) {
         return "enumValue.getValue().equals(value)";
      }

      final Entity entity = (Entity) dataType;
      return entity.getProperties().stream().filter( property -> !property.isNotInPayload() ).map( property -> {
         final String propertyName = StringUtils.capitalize( property.getName() );
         return String.format( "enumValue.getValue().get%s().equals(value.get%s())", propertyName, propertyName );
      } ).collect( Collectors.joining( " && " ) );
   }

   public static String getCharacteristicJavaType( final Property property, final JavaCodeGenerationConfig codeGenerationConfig ) {
      if ( hasContainerType( property ) ) {
         return getDataType( property.getCharacteristic().getDataType(), codeGenerationConfig );
      }

      return property.getEffectiveCharacteristic().getDataType().map( type -> {
         if ( type instanceof Scalar ) {
            return determinePropertyType( property.getEffectiveCharacteristic(), false, codeGenerationConfig );
         } else if ( type instanceof Entity ) {
            return ((Entity) type).getName();
         } else {
            throw new CodeGenerationException( "Unknown Characteristic data type " + type );
         }
      } ).orElseThrow( () -> new CodeGenerationException( "No data type found for Property " + property.getName() ) );
   }

   public static String printStructuredValueElement( final Object object ) {
      if ( object instanceof String ) {
         return "\"" + StringEscapeUtils.escapeJava( object.toString() ) + "\"";
      }
      return toConstant( ((Property) object).getName() );
   }
}