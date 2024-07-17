/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.java;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.java.exception.CodeGenerationException;
import org.eclipse.esmf.aspectmodel.visitor.AspectStreamTraversalVisitor;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.HasProperties;
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
import org.eclipse.esmf.metamodel.datatype.LangString;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.UnicodeUnescaper;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

public class AspectModelJavaUtil {

   public static final Converter<String, String> TO_CONSTANT = CaseFormat.UPPER_CAMEL.converterTo( CaseFormat.UPPER_UNDERSCORE );

   public static final UnicodeUnescaper UNESCAPER = new UnicodeUnescaper();

   private AspectModelJavaUtil() {
   }

   /**
    * Determines the type of a property and wraps it in an Optional if it has been marked as optional.
    *
    * @param property the property instance to determine the type for
    * @param codeGenerationConfig the configuration for code generation
    * @return the final type of the property
    */
   public static String getPropertyType( final Property property, final boolean inclValidation,
         final JavaCodeGenerationConfig codeGenerationConfig ) {
      final String propertyType = determinePropertyType( property.getCharacteristic(), inclValidation, codeGenerationConfig );
      if ( property.isOptional() ) {
         return containerType( Optional.class, propertyType,
               inclValidation && property.getCharacteristic().isPresent() && property.getCharacteristic().get() instanceof final Trait t
                     ? Optional.of( buildConstraintsForCharacteristic( t, codeGenerationConfig ) )
                     : Optional.empty() );
      }
      return propertyType;
   }

   /**
    * Resolves and tracks data type of the given meta model property.
    *
    * @param property the property to resolve the data type for
    * @return the fully qualified class name (potentially including type parameters) of the resolved data type
    */
   public static String getPropertyType( final Property property, final JavaCodeGenerationConfig codeGenerationConfig ) {
      if ( property.isAbstract() ) {
         return "Object";
      }
      final String propertyType = determinePropertyType( property.getCharacteristic(), false, codeGenerationConfig );
      codeGenerationConfig.importTracker().trackPotentiallyParameterizedType( propertyType );
      if ( property.isOptional() ) {
         return containerType( Optional.class, propertyType, Optional.empty() );
      }
      return propertyType;
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
            || (property.getEffectiveCharacteristic().map( characteristic -> characteristic.is( Collection.class ) ).orElse( false ));
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

   /**
    * Determines the type of a property
    *
    * @param optionalCharacteristic the {@link Characteristic} which describes the data type for a property
    * @param inclValidation a boolean indicating whether the element validation annotations should be included for the Collection
    * declarations
    * @return {@link String} containing the definition of the Java Data Type for the property
    */
   public static String determinePropertyType( final Optional<Characteristic> optionalCharacteristic, final boolean inclValidation,
         final JavaCodeGenerationConfig codeGenerationConfig ) {

      final Optional<Type> dataType = optionalCharacteristic.flatMap( Characteristic::getDataType );
      final Characteristic characteristic = optionalCharacteristic.orElseThrow( () ->
            new CodeGenerationException( "Can not determine type of missing Characteristic" ) );

      if ( characteristic.is( Collection.class ) ) {
         return determineCollectionType( characteristic.as( Collection.class ), inclValidation, codeGenerationConfig );
      }

      if ( characteristic.is( Enumeration.class ) ) {
         return characteristic.getName();
      }

      if ( characteristic.is( Trait.class ) ) {
         final Characteristic baseCharacteristic = characteristic.as( Trait.class ).getBaseCharacteristic();
         if ( baseCharacteristic.is( Collection.class ) ) {
            return determineCollectionType( baseCharacteristic.as( Collection.class ), inclValidation, codeGenerationConfig );
         }
      }

      if ( characteristic.is( Either.class ) ) {
         if ( codeGenerationConfig.enableJacksonAnnotations() ) {
            codeGenerationConfig.importTracker().importExplicit( "org.eclipse.esmf.aspectmodel.jackson.Either" );
         } else {
            codeGenerationConfig.importTracker().importExplicit( org.eclipse.esmf.aspectmodel.java.types.Either.class );
         }
         final String left = determinePropertyType(
               optionalCharacteristic.map( c -> c.as( Either.class ) ).map( Either::getLeft ), inclValidation, codeGenerationConfig );
         final String right = determinePropertyType(
               optionalCharacteristic.map( c -> c.as( Either.class ) ).map( Either::getRight ), inclValidation, codeGenerationConfig );
         return String.format( "Either<%s,%s>", left, right );
      }

      return getDataType( dataType, codeGenerationConfig.importTracker() );
   }

   public static String determineCollectionAspectClassDefinition( final StructureElement element,
         final JavaCodeGenerationConfig codeGenerationConfig ) {
      final Supplier<RuntimeException> error = () -> new CodeGenerationException(
            "Tried to generate a Collection Aspect class definition, but no " + "Property has a Collection Characteristic in "
                  + element.getName() );
      codeGenerationConfig.importTracker().importExplicit( CollectionAspect.class );
      for ( final Property property : element.getProperties() ) {
         final Characteristic characteristic = property.getEffectiveCharacteristic().orElseThrow( error );
         if ( characteristic instanceof Collection ) {
            final String collectionType = determineCollectionType( (Collection) characteristic, false, codeGenerationConfig );
            final String dataType = getDataType( characteristic.getDataType(), codeGenerationConfig.importTracker() );
            return String.format( "public class %s implements CollectionAspect<%s,%s>", element.getName(), collectionType, dataType );
         }
      }
      throw error.get();
   }

   public static String determineComplexTypeClassDefinition( final ComplexType element,
         final JavaCodeGenerationConfig codeGenerationConfig ) {
      final StringBuilder classDefinitionBuilder = new StringBuilder( "public " );
      if ( element.isAbstractEntity() ) {
         classDefinitionBuilder.append( "abstract " );
      }
      classDefinitionBuilder.append( "class " ).append( element.getName() );
      classDefinitionBuilder.append( genericClassSignature( element ) );
      if ( element.getExtends().isPresent() ) {
         final ComplexType extendedComplexType = element.getExtends().get();
         classDefinitionBuilder.append( " extends " );
         classDefinitionBuilder.append( extendedComplexType.getName() );
         final String generics = element.getProperties().stream()
               .filter( property -> property.getExtends().isPresent() )
               .map( property -> getPropertyType( property, false, codeGenerationConfig ) )
               .collect( Collectors.joining( "," ) );
         final String superTypeGenerics = generics.isEmpty() ? "" : "<" + generics + ">";
         classDefinitionBuilder.append( superTypeGenerics );
      }
      classDefinitionBuilder.append( " {" );
      return classDefinitionBuilder.toString();
   }

   public static String generateAbstractEntityClassAnnotations( final ComplexType element,
         final JavaCodeGenerationConfig codeGenerationConfig, final Set<ComplexType> extendingEntities ) {
      final StringBuilder classAnnotationBuilder = new StringBuilder();
      if ( element.isAbstractEntity() || !element.getExtendingElements().isEmpty() ) {
         codeGenerationConfig.importTracker().importExplicit( JsonTypeInfo.class );
         codeGenerationConfig.importTracker().importExplicit( JsonSubTypes.class );

         classAnnotationBuilder.append( "@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)" );
         classAnnotationBuilder.append( "@JsonSubTypes({" );
         final Iterator<ComplexType> extendingComplexTypeIterator = getExtendingClosure( element, extendingEntities ).iterator();
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
      } else if ( element.getExtends().isPresent() ) {
         codeGenerationConfig.importTracker().importExplicit( JsonTypeInfo.class );
         classAnnotationBuilder.append( "@JsonTypeInfo(use = JsonTypeInfo.Id.NAME," );
         classAnnotationBuilder.append( "              defaultImpl = " );
         classAnnotationBuilder.append( element.getName() );
         classAnnotationBuilder.append( ".class)" );
      }
      return classAnnotationBuilder.toString();
   }

   private static Stream<ComplexType> getExtendingClosure( final ComplexType element, final Set<ComplexType> extendingEntities ) {
      return Stream.concat( extendingEntities.stream().filter( entity -> element.equals( entity.getExtends().get() ) ),
            extendingEntities.stream()
                  .filter( entity -> element.equals( entity.getExtends().get() ) )
                  .flatMap( extendingElement -> getExtendingClosure( extendingElement, extendingEntities ) ) );
   }

   private static String determineCollectionType( final Collection collection, final boolean inclValidation,
         final JavaCodeGenerationConfig codeGenerationConfig ) {
      final Optional<Type> dataType = collection.getDataType();

      final Optional<String> elementConstraint = inclValidation
            ? buildConstraintForCollectionElements( collection, codeGenerationConfig )
            : Optional.empty();

      if ( collection.isAllowDuplicates() && collection.isOrdered() ) {
         codeGenerationConfig.importTracker().importExplicit( List.class );
         return containerType( List.class, getDataType( dataType, codeGenerationConfig.importTracker() ), elementConstraint );
      }
      if ( !collection.isAllowDuplicates() && collection.isOrdered() ) {
         codeGenerationConfig.importTracker().importExplicit( LinkedHashSet.class );
         return containerType( LinkedHashSet.class, getDataType( dataType, codeGenerationConfig.importTracker() ), elementConstraint );
      }
      if ( collection.isAllowDuplicates() && !collection.isOrdered() ) {
         codeGenerationConfig.importTracker().importExplicit( java.util.Collection.class );
         return containerType( java.util.Collection.class, getDataType( dataType, codeGenerationConfig.importTracker() ),
               elementConstraint );
      }
      if ( !collection.isAllowDuplicates() && !collection.isOrdered() ) {
         codeGenerationConfig.importTracker().importExplicit( Set.class );
         return containerType( Set.class, getDataType( dataType, codeGenerationConfig.importTracker() ), elementConstraint );
      }
      throw new CodeGenerationException( "Could not determine Java collection type for " + collection.getName() );
   }

   private static Optional<String> buildConstraintForCollectionElements( final Collection collection,
         final JavaCodeGenerationConfig codeGenerationConfig ) {
      return collection.getElementCharacteristic()
            .filter( elementCharacteristic -> elementCharacteristic.is( Trait.class ) )
            .map( elementCharacteristic -> buildConstraintsForCharacteristic( (Trait) elementCharacteristic, codeGenerationConfig ) );
   }

   public static String containerType( final Class<?> containerClass, final String elementType, final Optional<String> elementConstraint ) {
      final StringBuilder containerTypeBuilder = new StringBuilder().append( containerClass.getName() ).append( "<" );
      elementConstraint.ifPresent( containerTypeBuilder::append );
      containerTypeBuilder.append( elementType ).append( ">" );
      return containerTypeBuilder.toString();
   }

   /**
    * Determines the Java Data Type
    *
    * @param dataType the raw data type
    * @param importTracker the import tracker
    * @return a {@link String} containing the definition of the Java Data Type
    */
   public static String getDataType( final Optional<Type> dataType, final ImportTracker importTracker ) {
      return dataType.map( type -> {
         final Type actualDataType = dataType.get();
         if ( actualDataType instanceof ComplexType ) {
            return ((ComplexType) actualDataType).getName();
         }

         if ( actualDataType instanceof Scalar ) {
            final Resource typeResource = ResourceFactory.createResource( actualDataType.getUrn() );
            if ( typeResource.getURI().equals( RDF.langString.getURI() ) ) {
               importTracker.importExplicit( LangString.class );
               return "LangString";
            }
            final Class<?> result = SammXsdType.getJavaTypeForMetaModelType( typeResource );
            importTracker.importExplicit( result );
            return result.getTypeName();
         }

         throw new CodeGenerationException(
               "Could not determine Java type for model type that is " + "neither Scalar nor Entity: " + type.getUrn() );
      } ).orElseThrow( () -> new CodeGenerationException( "Failed to determine Java data type for empty model type" ) );
   }

   public static Class<?> getDataTypeClass( final Type dataType ) {
      if ( dataType instanceof ComplexType ) {
         return ((ComplexType) dataType).getClass();
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
    * Escapes a string properly to be used as a literal. Performs escaping according to Java String rules and afterwards additionally
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
    * Generates an enum key based on the given value
    *
    * @param value the actual value to generate the enum key for
    * @return a string representing the enum key
    */
   public static String generateEnumKey( final Value value ) {
      return value.accept( new ValueToEnumKeyVisitor(), null );
   }

   /**
    * Takes a class body with FQCNs and replaces them with applied imports (i.e. simply use the class name).
    */
   public static String applyImports( final String body, final JavaCodeGenerationConfig codeGenerationConfig ) {
      String importsApplied = body;
      for ( final String oneImport : codeGenerationConfig.importTracker().getUsedImports() ) {
         final String className = oneImport.substring( oneImport.lastIndexOf( '.' ) + 1 );
         importsApplied = importsApplied.replaceAll( oneImport, className );
      }
      return importsApplied;
   }

   public static boolean isPropertyNotInPayload( final Property property, final JavaCodeGenerationConfig codeGenerationConfig ) {
      if ( property.isNotInPayload() ) {
         codeGenerationConfig.importTracker().importExplicit( "com.fasterxml.jackson.annotation.JsonIgnore" );
         return true;
      }
      return false;
   }

   public static String buildConstraintsForCharacteristic( final Trait trait, final JavaCodeGenerationConfig codeGenerationConfig ) {
      return trait.getConstraints().stream()
            .map( constraint -> new ConstraintAnnotationBuilder().setConstraintClass( constraint )
                  .setImportTracker( codeGenerationConfig.importTracker() )
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

   public static String generateInitializer( final Property property, final String value,
         final JavaCodeGenerationConfig codeGenerationConfig,
         final ValueInitializer valueInitializer ) {
      return property.getDataType().map( type -> {
         final Resource typeResource = ResourceFactory.createResource( type.getUrn() );
         final Class<?> result = SammXsdType.getJavaTypeForMetaModelType( typeResource );
         codeGenerationConfig.importTracker().importExplicit( result );
         return valueInitializer.apply( typeResource, value );
      } ).orElseThrow( () -> new CodeGenerationException(
            "The Either Characteristic is not allowed for Properties used as elements in a StructuredValue" ) );
   }

   public static String generateEnumValue( final Value value, final JavaCodeGenerationConfig codeGenerationConfig ) {
      final ValueExpressionVisitor.Context context = new ValueExpressionVisitor.Context( codeGenerationConfig, false );
      return value.accept( new ValueExpressionVisitor(), context );
   }

   public static String generateFilterCompare( final Optional<Type> optionalDataType ) {
      final Type dataType = optionalDataType.orElseThrow(
            () -> new CodeGenerationException( "Could not generate equals expression for empty Enumeration datatype" ) );

      if ( dataType instanceof Scalar ) {
         return "enumValue.getValue().equals(value)";
      }

      final Entity entity = (Entity) dataType;
      if ( entity.getProperties().isEmpty() ) {
         return "enumValue.getValue().equals(value)";
      } else {
         return entity.getProperties().stream().filter( property -> !property.isNotInPayload() ).map( property -> {
            final String propertyName = StringUtils.capitalize( property.getName() );
            return String.format( "enumValue.getValue().get%s().equals(value.get%s())", propertyName, propertyName );
         } ).collect( Collectors.joining( " && " ) );
      }
   }

   public static String getCharacteristicJavaType( final Property property, final JavaCodeGenerationConfig codeGenerationConfig ) {
      final Supplier<RuntimeException> error = () -> new CodeGenerationException( "No data type found for Property " + property.getName() );
      if ( hasContainerType( property ) ) {
         return getDataType( property.getCharacteristic().orElseThrow( error ).getDataType(), codeGenerationConfig.importTracker() );
      }

      return property.getEffectiveCharacteristic().flatMap( Characteristic::getDataType ).map( type -> {
         if ( type.is( Scalar.class ) ) {
            return determinePropertyType( property.getEffectiveCharacteristic(), false, codeGenerationConfig );
         } else if ( type.is( Entity.class ) ) {
            return type.as( Entity.class ).getName();
         } else {
            throw new CodeGenerationException( "Unknown Characteristic data type " + type );
         }
      } ).orElseThrow( error );
   }

   public static String printStructuredValueElement( final Object object ) {
      if ( object instanceof String ) {
         return createLiteral( object.toString() );
      }
      return toConstant( ((Property) object).getName() );
   }

   public static boolean isXmlDatatypeFactoryRequired( final StructureElement element ) {
      final AspectStreamTraversalVisitor visitor = new AspectStreamTraversalVisitor();
      return visitor.visitStructureElement( element, null )
            .filter( modelElement -> Scalar.class.isAssignableFrom( modelElement.getClass() ) )
            .map( Scalar.class::cast ).map( Type::getUrn ).anyMatch(
                  typeUrn -> typeUrn.equals( XSD.date.getURI() )
                        || typeUrn.equals( XSD.time.getURI() )
                        || typeUrn.equals( XSD.dateTime.getURI() )
                        || typeUrn.equals( XSD.dateTimeStamp.getURI() )
                        || typeUrn.equals( XSD.gYear.getURI() )
                        || typeUrn.equals( XSD.gMonth.getURI() )
                        || typeUrn.equals( XSD.gDay.getURI() )
                        || typeUrn.equals( XSD.gYearMonth.getURI() )
                        || typeUrn.equals( XSD.gMonthDay.getURI() )
                        || typeUrn.equals( XSD.duration.getURI() )
                        || typeUrn.equals( XSD.yearMonthDuration.getURI() )
                        || typeUrn.equals(
                        XSD.dayTimeDuration.getURI() ) );
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

   public static String genericClassSignature( final StructureElement element ) {
      final List<Property> properties = element.getProperties();
      final String generics = IntStream.range( 0, properties.size() )
            .filter( i -> properties.get( i ).isAbstract() )
            .mapToObj( i -> "T" + i + " /* type of " + properties.get( i ).getName() + " */" )
            .collect( Collectors.joining( "," ) );
      return generics.isEmpty() ? "" : "<" + generics + ">";
   }

   /**
    * Generates the comma-separated list of arguments in a constructor call
    *
    * @param allProperties the list of properties that are turned into arguments
    * @param codeGenerationConfig the code generation context
    * @param enableJacksonAnnotations overriding whether Jackson annotations should be generated or not. This is because in certain
    * situations multiple constructors are generated, only one of which is @JsonCreator and uses @JsonProperty on the arguments
    * @return the constructor argument string
    */
   public static String constructorArguments( final List<Property> allProperties, final JavaCodeGenerationConfig codeGenerationConfig,
         final boolean enableJacksonAnnotations ) {
      return allProperties.stream()
            .filter( property -> !property.isAbstract() )
            .map( property -> {
               String declaration = "";
               if ( enableJacksonAnnotations ) {
                  declaration += "@JsonProperty( value = \"" + property.getPayloadName() + "\" ) ";
               }
               declaration += getPropertyType( property, false, codeGenerationConfig ) + " " + property.getPayloadName();
               return declaration;
            } )
            .collect( Collectors.joining( ", " ) );
   }

   public static String objectEqualsExpression( final StructureElement element ) {
      return element.getProperties().stream()
            .filter( property -> !property.isAbstract() )
            .map( property -> "Objects.equals(" + property.getPayloadName() + ", that." + property.getPayloadName() + ")" )
            .collect( Collectors.joining( " && " ) );
   }

   public static String objectsHashCodeExpression( final StructureElement element, final boolean needsLeadingComma ) {
      final String elementString = element.getProperties().stream()
            .filter( property -> !property.isAbstract() )
            .map( Property::getPayloadName )
            .collect( Collectors.joining( ", " ) );
      if ( StringUtils.isNotBlank( elementString ) && needsLeadingComma ) {
         return ", " + elementString;
      }
      return elementString;
   }

   /**
    * Returns the string that is put between super( ... )
    */
   public static String superConstructorCallExpression( final List<Property> allProperties, final List<Property> elementProperties ) {
      return allProperties.stream()
            .filter( property -> !elementProperties.contains( property ) )
            .filter( property -> property.getExtends().isEmpty() && !property.isAbstract() )
            .map( Property::getPayloadName )
            .collect( Collectors.joining( ", " ) );
   }

   public static String staticPropertiesExpression( final StructureElement element ) {
      return element.getProperties().stream()
            .filter( property -> !property.isAbstract() )
            .map( property -> toConstant( property.getName() ) )
            .collect( Collectors.joining( ", " ) );
   }

   public static Trait castToTrait( final Characteristic characteristic ) {
      return (Trait) characteristic;
   }

   public static Entity castToEntity( final ComplexType complexType ) {
      return (Entity) complexType;
   }

   public static ComplexType castToComplexType( final StructureElement structureElement ) {
      return (ComplexType) structureElement;
   }

   public static State castToState( final Enumeration enumeration ) {
      return (State) enumeration;
   }

   public static AbstractEntity castToAbstractEntity( final ComplexType complexType ) {
      return (AbstractEntity) complexType;
   }

   public static Quantifiable castToQuantifiable( final Characteristic characteristic ) {
      return (Quantifiable) characteristic;
   }

   public static String getterName( final Property property ) {
      final boolean isBooleanType = !property.isOptional()
            && property.getDataType()
            .filter( Type::isScalar )
            .map( type -> XSD.xboolean.getURI().equals( type.getUrn() ) )
            .orElse( false );
      return (isBooleanType ? "is" : "get") + StringUtils.capitalize( property.getPayloadName() );
   }
}
