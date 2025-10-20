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

package org.eclipse.esmf.aspectmodel.java.metamodel;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.generator.exception.CodeGenerationException;
import org.eclipse.esmf.aspectmodel.java.AspectModelJavaUtil;
import org.eclipse.esmf.aspectmodel.java.DeconstructionSet;
import org.eclipse.esmf.aspectmodel.java.StructuredValuePropertiesDeconstructor;
import org.eclipse.esmf.aspectmodel.java.ValueExpressionVisitor;
import org.eclipse.esmf.aspectmodel.java.ValueInitializer;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.BoundDefinition;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.QuantityKind;
import org.eclipse.esmf.metamodel.QuantityKinds;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.Units;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.characteristic.Code;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.characteristic.Duration;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.characteristic.Measurement;
import org.eclipse.esmf.metamodel.characteristic.Quantifiable;
import org.eclipse.esmf.metamodel.characteristic.SingleEntity;
import org.eclipse.esmf.metamodel.characteristic.State;
import org.eclipse.esmf.metamodel.characteristic.StructuredValue;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultCode;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultCollection;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultEnumeration;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultList;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultSet;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultSingleEntity;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultSortedSet;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultState;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultStructuredValue;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultTrait;
import org.eclipse.esmf.metamodel.constraint.EncodingConstraint;
import org.eclipse.esmf.metamodel.constraint.FixedPointConstraint;
import org.eclipse.esmf.metamodel.constraint.LanguageConstraint;
import org.eclipse.esmf.metamodel.constraint.LengthConstraint;
import org.eclipse.esmf.metamodel.constraint.LocaleConstraint;
import org.eclipse.esmf.metamodel.constraint.RangeConstraint;
import org.eclipse.esmf.metamodel.constraint.RegularExpressionConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultEncodingConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultFixedPointConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultLanguageConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultLengthConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultLocaleConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultRangeConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultRegularExpressionConstraint;
import org.eclipse.esmf.metamodel.impl.DefaultAbstractEntity;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.metamodel.impl.DefaultCollectionValue;
import org.eclipse.esmf.metamodel.impl.DefaultEntity;
import org.eclipse.esmf.metamodel.impl.DefaultEntityInstance;
import org.eclipse.esmf.metamodel.impl.DefaultQuantityKind;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.impl.DefaultScalarValue;
import org.eclipse.esmf.metamodel.impl.DefaultUnit;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;

public class StaticMetaModelVisitor implements AspectVisitor<String, StaticCodeGenerationContext> {
   private final ValueExpressionVisitor valueExpressionVisitor = new ValueExpressionVisitor();
   private final ValueInitializer valueInitializer = new ValueInitializer();

   private final Supplier<CodeGenerationException> noTypeException = () -> new CodeGenerationException(
         "Characteristic is missing its dataType" );

   @Override
   public String visitBase( final ModelElement modelElement, final StaticCodeGenerationContext context ) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String visitScalarValue( final ScalarValue value, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultScalarValue.class );
      final ValueExpressionVisitor.Context valueContext = new ValueExpressionVisitor.Context( context.codeGenerationConfig(), false );
      final String metaModelAttributes = ( !value.getSee().isEmpty() && !value.getPreferredNames().isEmpty() )
            ? getMetaModelBaseAttributes( value, context )
            : "MetaModelBaseAttributes.builder().build()";
      return "new DefaultScalarValue("
            // Object value
            + metaModelAttributes + ","
            // Object value
            + value.accept( valueExpressionVisitor, valueContext ) + ","
            // Scalar type
            + value.getType().accept( this, context ) + ")";
   }

   @Override
   public String visitCollectionValue( final CollectionValue collection, final StaticCodeGenerationContext context ) {
      final Class<?> collectionClass = collection.getValues().getClass();
      context.codeGenerationConfig().importTracker().importExplicit( DefaultCollectionValue.class );
      context.codeGenerationConfig().importTracker().importExplicit( collectionClass );
      final StringBuilder result = new StringBuilder();
      result.append( "new DefaultCollectionValue(" );
      // Collection<Value> values
      result.append( "new " );
      result.append( collectionClass.getSimpleName() );
      result.append( "<>() {{" );
      collection.getValues().forEach( value -> {
         result.append( "add(" );
         result.append( value.accept( this, context ) );
         result.append( ");" );
      } );
      result.append( "}}" );
      result.append( "," );
      // Collection.CollectionType collectionType
      result.append( CollectionValue.CollectionType.class.getName().replace( "$", "." ) ).append( "." );
      result.append( collection.getCollectionType() ).append( "," );
      // Type element
      result.append( collection.getType().accept( this, context ) );
      result.append( ")" );
      return result.toString();
   }

   @Override
   public String visitEntityInstance( final EntityInstance instance, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultEntityInstance.class );
      context.codeGenerationConfig().importTracker().importExplicit( Map.class );
      context.codeGenerationConfig().importTracker().importExplicit( HashMap.class );
      context.codeGenerationConfig().importTracker().importExplicit( Property.class );
      context.codeGenerationConfig().importTracker().importExplicit( Value.class );
      final Entity entity = instance.getEntityType();
      final StringBuilder builder = new StringBuilder();
      builder.append( "new DefaultEntityInstance(" );
      builder.append( getMetaModelBaseAttributes( instance, context ) );
      builder.append( "," );
      builder.append( "new HashMap<Property, Value>() {{" );
      for ( final Property property : instance.getEntityType().getProperties() ) {
         final Value instancePropertyValue = instance.getAssertions().get( property );
         if ( instancePropertyValue == null ) {
            continue;
         }
         final String className = "Meta" + entity.getName();
         final String staticPropertyField = AspectModelJavaUtil.toConstant( property.getName() );
         builder.append( "put(" );
         builder.append( className );
         builder.append( "." );
         builder.append( staticPropertyField );
         builder.append( "," );
         builder.append( instancePropertyValue.accept( this, context ) );
         builder.append( ");" );
      }
      builder.append( "}}," );
      builder.append( instance.getEntityType().accept( this, context ) );
      builder.append( ")" );

      return builder.toString();
   }

   @Override
   public String visitSingleEntity( final SingleEntity singleEntity, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultSingleEntity.class );
      return "new DefaultSingleEntity("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( singleEntity, context ) + ", "
            // Type dataType
            + singleEntity.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ")";
   }

   @Override
   public String visitCollection( final Collection collection, final StaticCodeGenerationContext context ) {
      final Class<?> implementationClass = switch ( collection.getCollectionType() ) {
         case LIST -> DefaultList.class;
         case SET -> DefaultSet.class;
         case SORTEDSET -> DefaultSortedSet.class;
         default -> DefaultCollection.class;
      };

      context.codeGenerationConfig().importTracker().importExplicit( implementationClass );
      final String optionalType = collection.getDataType().map( type -> type.accept( this, context ) )
            .map( type -> "Optional.of(" + type + ")" )
            .orElse( "Optional.empty()" );
      final String optionalElementCharacteristic = collection.getElementCharacteristic()
            .map( characteristic -> characteristic.accept( this, context ) )
            .map( characteristic -> "Optional.of(" + characteristic + ")" ).orElse( "Optional.empty()" );
      return "new " + implementationClass.getSimpleName() + "(" + getMetaModelBaseAttributes( collection, context ) + "," + optionalType
            + ","
            + optionalElementCharacteristic + ")";
   }

   @Override
   public String visitCode( final Code code, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultCode.class );
      return "new DefaultCode("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( code, context ) + ","
            // Type dataType
            + code.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ")";
   }

   @Override
   public String visitDuration( final Duration duration, final StaticCodeGenerationContext context ) {
      return generateForQuantifiable( duration, context );
   }

   @Override
   public String visitMeasurement( final Measurement measurement, final StaticCodeGenerationContext context ) {
      return generateForQuantifiable( measurement, context );
   }

   @Override
   public String visitQuantifiable( final Quantifiable quantifiable, final StaticCodeGenerationContext context ) {
      return generateForQuantifiable( quantifiable, context );
   }

   private <T extends Quantifiable> String generateForQuantifiable( final T quantifiable, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( quantifiable.getClass() );
      context.codeGenerationConfig().importTracker().importExplicit( Units.class );
      return "new " + quantifiable.getClass().getSimpleName() + "("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( quantifiable, context ) + ","
            // Type dataType
            + quantifiable.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ","
            // Optional<Unit> unit
            + quantifiable.getUnit().map( unit -> unit.accept( this, context ) ).orElse( "Optional.empty()" ) + ")";
   }

   private String optionalString( final Optional<String> optionalString ) {
      return optionalString.map( value -> "Optional.of(\"" + value + "\")" ).orElse( "Optional.empty()" );
   }

   @Override
   public String visitUnit( final Unit unit, final StaticCodeGenerationContext context ) {
      final Optional<Unit> unitFromCatalog = Units.fromName( unit.getName() );
      if ( unitFromCatalog.isPresent() ) {
         context.codeGenerationConfig().importTracker().importExplicit( Units.class );
         return "Units.fromName(\"" + unit.getName() + "\")";
      }

      context.codeGenerationConfig().importTracker().importExplicit( DefaultUnit.class );
      context.codeGenerationConfig().importTracker().importExplicit( HashSet.class );
      context.codeGenerationConfig().importTracker().importExplicit( Optional.class );
      return "Optional.of(new DefaultUnit("
            + getMetaModelBaseAttributes( unit, context ) + ","
            + optionalString( unit.getSymbol() ) + ","
            + optionalString( unit.getCode() ) + ","
            + optionalString( unit.getReferenceUnit() ) + ","
            + optionalString( unit.getConversionFactor() ) + ","
            + "new HashSet<>(){{"
            + unit.getQuantityKinds().stream()
            .map( quantityKind -> quantityKind.accept( this, context ) )
            .map( quantityKindInitializer -> String.format( "add(%s);", quantityKindInitializer ) )
            .collect( Collectors.joining() )
            + "}}"
            + "))";
   }

   @Override
   public String visitQuantityKind( final QuantityKind quantityKind, final StaticCodeGenerationContext context ) {
      // The quantity kind is one of the default ones defined in the QuantityKinds enum?
      if ( QuantityKinds.fromName( quantityKind.getName() ).isPresent() ) {
         context.codeGenerationConfig().importTracker().importExplicit( QuantityKinds.class );
         return "QuantityKinds." + AspectModelJavaUtil.toConstant( quantityKind.getName() );
      }

      // If not, create a new instance of the default implementation
      context.codeGenerationConfig().importTracker().importExplicit( DefaultQuantityKind.class );
      return "new DefaultQuantityKind("
            + getMetaModelBaseAttributes( quantityKind, context ) + ","
            + "\"" + StringEscapeUtils.escapeJava( quantityKind.getLabel() ) + "\""
            + ")";
   }

   @Override
   public String visitState( final State state, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultState.class );
      context.codeGenerationConfig().importTracker().importExplicit( ArrayList.class );
      context.codeGenerationConfig().importTracker().importExplicit( Value.class );
      return "new DefaultState("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( state, context ) + ","
            // Type type
            + state.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ","
            // List<Value> values
            + "new ArrayList<Value>(){{" + state.getValues().stream().sorted()
            .map( value -> String.format( "add(%s);", value.accept( this, context ) ) )
            .collect( Collectors.joining() ) + "}},"
            // Value defaultValue
            + state.getDefaultValue().accept( this, context ) + ")";
   }

   @Override
   public String visitEnumeration( final Enumeration enumeration, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultEnumeration.class );
      context.codeGenerationConfig().importTracker().importExplicit( ArrayList.class );
      context.codeGenerationConfig().importTracker().importExplicit( Value.class );
      return "new DefaultEnumeration("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( enumeration, context ) + ","
            // Type type
            + enumeration.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ","
            // List<Value> values
            + "new ArrayList<Value>(){{" + enumeration.getValues().stream().sorted()
            .map( value -> String.format( "add(%s);", value.accept( this, context ) ) )
            .collect( Collectors.joining() ) + "}})";
   }

   @Override
   public String visitStructuredValue( final StructuredValue structuredValue, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultStructuredValue.class );
      context.codeGenerationConfig().importTracker().importExplicit( ArrayList.class );

      // The referenced Properties created by the StructuredValuePropertiesDeconstructor could differ from the referenced Properties
      // given in the StructuredValue: For example, when two properties "startDate" and "endDate" both use a StructuredValue
      // with elements ( :year "-" :month "-" :day ), the StructuredValue will refer to "year", "month" and "day", while
      // referencedProperties retrieved here will refer to "startDateYear", "startDateMonth", "starteDateDay" or
      // "endDateYear", "endDateMonth", "endDateDay", respectively; since those are the names of the fields created (to prevent
      // name clashes). The latter are the names we need to refer to in the "new DefaultStructuredValue" call.
      final Property originalProperty = context.currentProperty();
      final StructuredValuePropertiesDeconstructor deconstructor = new StructuredValuePropertiesDeconstructor( context.currentElement() );
      final List<Property> referencedProperties = deconstructor.getDeconstructionSets()
            .stream()
            .collect( Collectors.groupingBy( DeconstructionSet::originalProperty ) )
            .get( originalProperty )
            .getFirst()
            .elementProperties();

      return "new DefaultStructuredValue("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( structuredValue, context ) + ","
            // Type type
            + structuredValue.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ","
            // String deconstructionRule
            + AspectModelJavaUtil.createLiteral( structuredValue.getDeconstructionRule() ) + ","
            // List<Object> elements
            + "new ArrayList<Object>(){{" + structuredValue.getElements().stream().sequential()
            .map( element -> {
               if ( element instanceof final Property referencedPropertyFromStructuredValue ) {
                  // Find the qualified property in the referencedProperties list that corresponds to referencedPropertyFromStructuredValue
                  final String targetName = deconstructor.createQualifiedPropertyName( originalProperty,
                        referencedPropertyFromStructuredValue );
                  final Property referencedProperty = referencedProperties.stream()
                        .filter( p -> p.urn().equals( referencedPropertyFromStructuredValue.urn() ) )
                        .findFirst()
                        .or( () -> referencedProperties.stream()
                              .filter( p -> p.getName().equals( targetName ) )
                              .findFirst() )
                        .orElseThrow();
                  return AspectModelJavaUtil.toConstant( referencedProperty.getName() );
               } else {
                  return AspectModelJavaUtil.createLiteral( element.toString() );
               }
            } )
            .map( s -> "add(" + s + ");" )
            .collect( Collectors.joining() ) + "}})";
   }

   @Override
   public String visitTrait( final Trait trait, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultTrait.class );
      context.codeGenerationConfig().importTracker().importExplicit( ArrayList.class );
      return "new DefaultTrait("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( trait, context ) + ","
            // Characteristic baseCharacteristic
            + trait.getBaseCharacteristic().accept( this, context ) + ","
            // List<Constraint> constraints
            + "new ArrayList<Constraint>(){{" + trait.getConstraints().stream().sorted()
            .map( constraint -> String.format( "add(%s);", constraint.accept( this, context.withCurrentCharacteristic( trait ) ) ) )
            .collect( Collectors.joining() ) + "}})";
   }

   @Override
   public String visitCharacteristic( final Characteristic characteristic, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultCharacteristic.class );
      return "new DefaultCharacteristic("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( characteristic, context ) + ","
            // Optional<Type> type
            + characteristic.getDataType().map( type -> "Optional.of(" + type.accept( this, context ) + ")" ).orElse( "Optional.empty()" )
            + ")";
   }

   @Override
   public String visitLengthConstraint( final LengthConstraint lengthConstraint, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultLengthConstraint.class );
      final Scalar nonNegativeInteger = new DefaultScalar( XSD.nonNegativeInteger.getURI() );
      return "new DefaultLengthConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( lengthConstraint, context ) + ","
            // Optional<BigInteger> min
            + getOptionalStaticDeclarationValue( nonNegativeInteger, lengthConstraint.getMinValue(), context ) + ","
            // Optional<BigInteger> max
            + getOptionalStaticDeclarationValue( nonNegativeInteger, lengthConstraint.getMaxValue(), context ) + ")";
   }

   @Override
   public String visitRangeConstraint( final RangeConstraint rangeConstraint, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultRangeConstraint.class );
      context.codeGenerationConfig().importTracker().importExplicit( BoundDefinition.class );
      final Type characteristicType = context.currentCharacteristic().getDataType().orElseThrow( noTypeException );
      return "new DefaultRangeConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( rangeConstraint, context ) + ","
            // Optional<Object> minValue
            + getOptionalStaticDeclarationValue( characteristicType, rangeConstraint.getMinValue(), context ) + ","
            // Optional<Object> maxValue
            + getOptionalStaticDeclarationValue( characteristicType, rangeConstraint.getMaxValue(), context ) + ","
            // BoundDefinition lowerBoundDefinition
            + "BoundDefinition." + rangeConstraint.getLowerBoundDefinition().name() + ","
            // BoundDefinition upperBoundDefinition
            + "BoundDefinition." + rangeConstraint.getUpperBoundDefinition().name() + ")";
   }

   @Override
   public String visitRegularExpressionConstraint( final RegularExpressionConstraint regularExpressionConstraint,
         final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultRegularExpressionConstraint.class );
      return "new DefaultRegularExpressionConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( regularExpressionConstraint, context ) + ","
            // String value
            + AspectModelJavaUtil.createLiteral( regularExpressionConstraint.getValue() ) + ")";
   }

   @Override
   public String visitEncodingConstraint( final EncodingConstraint encodingConstraint, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultEncodingConstraint.class );
      context.codeGenerationConfig().importTracker().importExplicit( Charset.class );
      return "new DefaultEncodingConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( encodingConstraint, context ) + ","
            // Charset value
            + "Charset.forName(\"" + encodingConstraint.getValue() + "\"))";
   }

   @Override
   public String visitLanguageConstraint( final LanguageConstraint languageConstraint, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultLanguageConstraint.class );
      context.codeGenerationConfig().importTracker().importExplicit( Locale.class );
      return "new DefaultLanguageConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( languageConstraint, context ) + ","
            // Locale languageCode
            + "Locale.forLanguageTag(\"" + languageConstraint.getLanguageCode().toLanguageTag() + "\"))";
   }

   @Override
   public String visitLocaleConstraint( final LocaleConstraint localeConstraint, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultLocaleConstraint.class );
      context.codeGenerationConfig().importTracker().importExplicit( Locale.class );
      // TODO is it bug?
      return "new DefaultLanguageConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( localeConstraint, context ) + ","
            // Locale localeCode
            + "Locale.forLanguageTag(\"" + localeConstraint.getLocaleCode().toLanguageTag() + "\"))";
   }

   @Override
   public String visitFixedPointConstraint( final FixedPointConstraint fixedPointConstraint, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultFixedPointConstraint.class );
      return "new DefaultFixedPointConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( fixedPointConstraint, context ) + ","
            // Integer scale
            + fixedPointConstraint.getScale() + ","
            // Integer scale
            + fixedPointConstraint.getInteger() + ")";
   }

   @Override
   public String visitConstraint( final Constraint constraint, final StaticCodeGenerationContext context ) {
      throw new UnsupportedOperationException( "Could not generate code for unknown constraint: " + constraint.getName() );
   }

   @Override
   public String visitEntity( final Entity entity, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultEntity.class );
      return "DefaultEntity.createDefaultEntity("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( entity, context ) + ","
            // List<Property> properties
            + "Meta" + entity.getName() + ".INSTANCE.getProperties(),"
            // Optional<ComplexType> _extends
            + extendsComplexType( entity, context ) + ")";
   }

   @Override
   public String visitAbstractEntity( final AbstractEntity abstractEntity, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultAbstractEntity.class );
      return "DefaultAbstractEntity.createDefaultAbstractEntity("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( abstractEntity, context ) + ","
            // List<Property> properties
            + "Meta" + abstractEntity.getName() + ".INSTANCE.getProperties()" + ","
            // Optional<ComplexType> _extends
            + extendsComplexType( abstractEntity, context ) + ","
            // List<AspectModelUrn> extendingElements
            + "List.of(" + abstractEntity.getExtendingElements().stream().sorted()
            .map( extendingElement -> "AspectModelUrn.fromUrn(\"" + extendingElement.getUrn() + "\")" ).collect( Collectors.joining( "," ) )
            + "))";
   }

   @Override
   public String visitScalar( final Scalar scalar, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( DefaultScalar.class );
      return "new DefaultScalar(\"" + scalar.getUrn() + "\" )";
   }

   private String extendsComplexType( final ComplexType complexType, final StaticCodeGenerationContext context ) {
      if ( complexType.getExtends().isEmpty() ) {
         return "Optional.empty()";
      }

      final ComplexType type = complexType.getExtends().get();
      if ( type.is( Entity.class ) ) {
         final Entity entity = type.as( Entity.class );
         context.codeGenerationConfig().importTracker().importExplicit( DefaultEntity.class );
         return "Optional.of(DefaultEntity.createDefaultEntity(" + getMetaModelBaseAttributes( complexType, context ) + "," + "Meta"
               + entity.getName() + ".INSTANCE.getProperties()," + extendsComplexType( entity, context ) + "))";
      }
      // AbstractEntity
      final AbstractEntity abstractEntity = type.as( AbstractEntity.class );
      context.codeGenerationConfig().importTracker().importExplicit( DefaultAbstractEntity.class );
      return "Optional.of(DefaultAbstractEntity.createDefaultAbstractEntity(" + getMetaModelBaseAttributes( abstractEntity, context ) + ","
            + "Meta"
            + abstractEntity.getName() + ".INSTANCE.getProperties()," + extendsComplexType( abstractEntity, context ) + "," + "List.of("
            + abstractEntity.getExtendingElements().stream().sorted()
            .map( extendingElement -> "AspectModelUrn.fromUrn( \"" + extendingElement.getUrn() + "\" )" )
            .collect( Collectors.joining( "," ) ) + ")" + "))";
   }

   private <T> String getOptionalStaticDeclarationValue( final Type type, final Optional<T> optionalValue,
         final StaticCodeGenerationContext context ) {
      if ( optionalValue.isEmpty() ) {
         return "Optional.empty()";
      }

      if ( optionalValue.get() instanceof final ScalarValue scalarValue ) {
         return "Optional.of(" + ( scalarValue ).accept( this, context ) + ")";
      }

      context.codeGenerationConfig().importTracker().importExplicit( AspectModelJavaUtil.getDataTypeClass( type ) );
      final Resource xsdType = ResourceFactory.createResource( type.getUrn() );
      String valueExpression = optionalValue.get().toString();
      if ( type.getUrn().endsWith( "#float" ) ) {
         valueExpression = valueExpression + "f";
      }
      if ( AspectModelJavaUtil.doesValueNeedToBeQuoted( type.getUrn() ) ) {
         valueExpression = "\"" + valueExpression + "\"";
      } else {
         valueExpression = StringEscapeUtils.escapeJava( valueExpression );
      }
      return "Optional.of(" + valueInitializer.apply( xsdType, valueExpression ) + ")";
   }

   public String exampleValue( final Property property, final StaticCodeGenerationContext context ) {
      return property.getExampleValue()
            .map( exampleValue -> {
               if ( exampleValue instanceof final ScalarValue scalarValue ) {
                  return "Optional.of(" + visitScalarValue( scalarValue, context ) + ")";
               } else if ( exampleValue instanceof final Value value ) {
                  return "Optional.of(" + visitValue( value, context ) + ")";
               } else {
                  throw new IllegalArgumentException( "Unexpected exampleValue type: " + exampleValue.getClass() );
               }
            } )
            .orElse( "Optional.empty()" );
   }

   /*
    * This method is a crutch because velocity is not able to call the parameterized actual method
    */
   public String metaModelBaseAttributes( final Property property, final StaticCodeGenerationContext context ) {
      return getMetaModelBaseAttributes( property, context );
   }

   public String getMetaModelBaseAttributes( final ModelElement element, final StaticCodeGenerationContext context ) {
      final StringBuilder builder = new StringBuilder();
      builder.append( "MetaModelBaseAttributes.builder()" );
      if ( element.isAnonymous() ) {
         builder.append( ".isAnonymous()" );
      } else {
         builder.append( ".withUrn(" );
         builder.append( elementUrn( element, context ) );
         builder.append( ")" );
      }

      element.getPreferredNames().stream().sorted().forEach( preferredName -> {
         builder.append( ".withPreferredName(Locale.forLanguageTag(\"" ).append( preferredName.getLanguageTag().toLanguageTag() )
               .append( "\")," );
         builder.append( AspectModelJavaUtil.createLiteral( preferredName.getValue() ) ).append( ")" );
      } );
      element.getDescriptions().stream().sorted().forEach( description -> {
         builder.append( ".withDescription(Locale.forLanguageTag(\"" ).append( description.getLanguageTag().toLanguageTag() )
               .append( "\")," );
         builder.append( AspectModelJavaUtil.createLiteral( description.getValue() ) ).append( ")" );
      } );
      element.getSee().stream().sorted()
            .forEach( see -> builder.append( ".withSee(" ).append( AspectModelJavaUtil.createLiteral( see ) ).append( ")" ) );

      builder.append( ".build()" );
      return builder.toString();
   }

   public String elementUrn( final ModelElement element, final StaticCodeGenerationContext context ) {
      if ( element.urn().toString().startsWith( context.modelUrnPrefix() ) ) {
         return "AspectModelUrn.fromUrn( NAMESPACE + \"" + element.getName() + "\" )";
      }
      if ( element.urn().toString().startsWith( context.characteristicBaseUrn() ) ) {
         return "AspectModelUrn.fromUrn( CHARACTERISTIC_NAMESPACE + \"#" + element.getName() + "\" )";
      }
      return "AspectModelUrn.fromUrn( \"" + element.urn() + "\" )";
   }
}
