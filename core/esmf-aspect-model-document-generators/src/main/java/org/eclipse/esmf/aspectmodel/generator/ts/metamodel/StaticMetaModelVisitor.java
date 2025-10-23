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

package org.eclipse.esmf.aspectmodel.generator.ts.metamodel;

import static org.eclipse.esmf.aspectmodel.generator.ts.AspectModelTsUtil.createLiteral;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.generator.exception.CodeGenerationException;
import org.eclipse.esmf.aspectmodel.generator.ts.AspectModelTsUtil;
import org.eclipse.esmf.aspectmodel.generator.ts.DeconstructionSet;
import org.eclipse.esmf.aspectmodel.generator.ts.StructuredValuePropertiesDeconstructor;
import org.eclipse.esmf.aspectmodel.generator.ts.ValueExpressionVisitor;
import org.eclipse.esmf.aspectmodel.generator.ts.ValueInitializer;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.AbstractEntity;
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
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultCollection;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultList;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultSet;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultSortedSet;
import org.eclipse.esmf.metamodel.constraint.EncodingConstraint;
import org.eclipse.esmf.metamodel.constraint.FixedPointConstraint;
import org.eclipse.esmf.metamodel.constraint.LanguageConstraint;
import org.eclipse.esmf.metamodel.constraint.LengthConstraint;
import org.eclipse.esmf.metamodel.constraint.LocaleConstraint;
import org.eclipse.esmf.metamodel.constraint.RangeConstraint;
import org.eclipse.esmf.metamodel.constraint.RegularExpressionConstraint;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;

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

   // TODO validate generated code
   @Override
   public String visitScalarValue( final ScalarValue value, final StaticCodeGenerationContext context ) {
      final ValueExpressionVisitor.Context valueContext = new ValueExpressionVisitor.Context( context.codeGenerationConfig(), false );
      final String metaModelAttributes = ( !value.getSee().isEmpty() && !value.getPreferredNames().isEmpty() )
            ? getMetaModelBaseAttributes( value, context )
            : "{}";
      return START
            // Object value
            + META_MODEL_BASE_ATTRIBUTES_NAME + ASSIGNMENT + metaModelAttributes + END_PARAM
            // Object value
            + VALUE_NAME + ASSIGNMENT + value.accept( valueExpressionVisitor, valueContext ) + END_PARAM
            // Scalar type
            + TYPE_NAME + ASSIGNMENT + value.getType().accept( this, context ) + END_PARAM
            + END;
   }

   // TODO investigate DefaultCollectionValue
   @Override
   public String visitCollectionValue( final CollectionValue collection, final StaticCodeGenerationContext context ) {
      final Class<?> collectionClass = collection.getValues().getClass();
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultCollectionValue.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( collectionClass );
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

   // TODO investigate DefaultEntityInstance
   @Override
   public String visitEntityInstance( final EntityInstance instance, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultEntityInstance.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( Map.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( HashMap.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( Property.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( Value.class );
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
         final String staticPropertyField = AspectModelTsUtil.toConstant( property.getName() );
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

   // TODO investigate DefaultSingleEntity
   @Override
   public String visitSingleEntity( final SingleEntity singleEntity, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultSingleEntity.class );
      return "new DefaultSingleEntity("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( singleEntity, context ) + ", "
            // Type dataType
            + singleEntity.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ")";
   }

   // TODO investigate implementation collection
   @Override
   public String visitCollection( final Collection collection, final StaticCodeGenerationContext context ) {
      final Class<?> implementationClass = switch ( collection.getCollectionType() ) {
         case LIST -> DefaultList.class;
         case SET -> DefaultSet.class;
         case SORTEDSET -> DefaultSortedSet.class;
         default -> DefaultCollection.class;
      };
      //      context.codeGenerationConfig().importTracker().importExplicit( implementationClass );
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

   // TODO validate generated code
   @Override
   public String visitCode( final Code code, final StaticCodeGenerationContext context ) {
      return START
            + META_MODEL_BASE_ATTRIBUTES_NAME + ASSIGNMENT + getMetaModelBaseAttributes( code, context ) + END_PARAM
            + DATA_TYPE_NAME + ASSIGNMENT + code.getDataType().orElseThrow( noTypeException ).accept( this, context ) + END_PARAM
            + END;
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

   // TODO investigate Quantifiable
   private <T extends Quantifiable> String generateForQuantifiable( final T quantifiable, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( quantifiable.getClass() );
      //      context.codeGenerationConfig().importTracker().importExplicit( Units.class );
      return "new " + quantifiable.getClass().getSimpleName() + "("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( quantifiable, context ) + ","
            // Type dataType
            + quantifiable.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ","
            // Optional<Unit> unit
            + quantifiable.getUnit().map( unit -> unit.accept( this, context ) ).orElse( "Optional.empty()" ) + ")";
   }

   // TODO investigate DefaultUnit
   @Override
   public String visitUnit( final Unit unit, final StaticCodeGenerationContext context ) {
      final Optional<Unit> unitFromCatalog = Units.fromName( unit.getName() );
      if ( unitFromCatalog.isPresent() ) {
         //         context.codeGenerationConfig().importTracker().importExplicit( Units.class );
         return "Units.fromName(\"" + unit.getName() + "\")";
      }

      //TODO fix it investigate Unit logic
      return "Optional.of(new DefaultUnit("
            + getMetaModelBaseAttributes( unit, context ) + ","
            + unit.getSymbol() + ","
            + unit.getCode() + ","
            + unit.getReferenceUnit() + ","
            + unit.getConversionFactor() + ","
            + "new HashSet<>(){{"
            + unit.getQuantityKinds().stream()
            .map( quantityKind -> quantityKind.accept( this, context ) )
            .map( quantityKindInitializer -> String.format( "add(%s);", quantityKindInitializer ) )
            .collect( Collectors.joining() )
            + "}}"
            + "))";
   }

   // TODO investigate QuantityKinds
   @Override
   public String visitQuantityKind( final QuantityKind quantityKind, final StaticCodeGenerationContext context ) {
      // The quantity kind is one of the default ones defined in the QuantityKinds enum?
      if ( QuantityKinds.fromName( quantityKind.getName() ).isPresent() ) {
         //         context.codeGenerationConfig().importTracker().importExplicit( QuantityKinds.class );
         return "QuantityKinds." + AspectModelTsUtil.toConstant( quantityKind.getName() );
      }

      // If not, create a new instance of the default implementation
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultQuantityKind.class );
      return "new DefaultQuantityKind("
            + getMetaModelBaseAttributes( quantityKind, context ) + ","
            + "\"" + StringEscapeUtils.escapeJava( quantityKind.getLabel() ) + "\""
            + ")";
   }

   // TODO investigate DefaultState
   @Override
   public String visitState( final State state, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultState.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( ArrayList.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( Value.class );
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

   // TODO investigate DefaultEnumeration
   @Override
   public String visitEnumeration( final Enumeration enumeration, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultEnumeration.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( ArrayList.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( Value.class );
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

   // TODO investigate DefaultStructuredValue
   @Override
   public String visitStructuredValue( final StructuredValue structuredValue, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultStructuredValue.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( ArrayList.class );

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
            + createLiteral( structuredValue.getDeconstructionRule() ) + ","
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
                  return AspectModelTsUtil.toConstant( referencedProperty.getName() );
               } else {
                  return createLiteral( element.toString() );
               }
            } )
            .map( s -> "add(" + s + ");" )
            .collect( Collectors.joining() ) + "}})";
   }

   // TODO investigate DefaultTrait
   @Override
   public String visitTrait( final Trait trait, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultTrait.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( ArrayList.class );
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
      final StringBuilder builder = new StringBuilder();
      builder.append( START );
      builder.append( META_MODEL_BASE_ATTRIBUTES_NAME ).append( ASSIGNMENT ).append( getMetaModelBaseAttributes( characteristic, context ) )
            .append( END_PARAM );
      characteristic.getDataType()
            .map( type -> type.accept( this, context ) )
            .filter( String::isEmpty )
            .ifPresent( str -> builder.append( DATA_TYPE_NAME ).append( ASSIGNMENT ).append( str ) );
      builder.append( END );
      return builder.toString();
   }

   // TODO investigate DefaultLengthConstraint
   @Override
   public String visitLengthConstraint( final LengthConstraint lengthConstraint, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultLengthConstraint.class );
      final Scalar nonNegativeInteger = new DefaultScalar( XSD.nonNegativeInteger.getURI() );
      return "new DefaultLengthConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( lengthConstraint, context ) + ","
            // Optional<BigInteger> min
            + getOptionalStaticDeclarationValue( nonNegativeInteger, lengthConstraint.getMinValue(), context ) + ","
            // Optional<BigInteger> max
            + getOptionalStaticDeclarationValue( nonNegativeInteger, lengthConstraint.getMaxValue(), context ) + ")";
   }

   // TODO investigate DefaultLengthConstraint
   @Override
   public String visitRangeConstraint( final RangeConstraint rangeConstraint, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultRangeConstraint.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( BoundDefinition.class );
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

   // TODO investigate DefaultRegularExpressionConstraint
   @Override
   public String visitRegularExpressionConstraint( final RegularExpressionConstraint regularExpressionConstraint,
         final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultRegularExpressionConstraint.class );
      return "new DefaultRegularExpressionConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( regularExpressionConstraint, context ) + ","
            // String value
            + createLiteral( regularExpressionConstraint.getValue() ) + ")";
   }

   // TODO investigate DefaultEncodingConstraint
   @Override
   public String visitEncodingConstraint( final EncodingConstraint encodingConstraint, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultEncodingConstraint.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( Charset.class );
      return "new DefaultEncodingConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( encodingConstraint, context ) + ","
            // Charset value
            + "Charset.forName(\"" + encodingConstraint.getValue() + "\"))";
   }

   // TODO investigate DefaultLanguageConstraint
   @Override
   public String visitLanguageConstraint( final LanguageConstraint languageConstraint, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultLanguageConstraint.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( Locale.class );
      return "new DefaultLanguageConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( languageConstraint, context ) + ","
            // Locale languageCode
            + "Locale.forLanguageTag(\"" + languageConstraint.getLanguageCode().toLanguageTag() + "\"))";
   }

   // TODO investigate DefaultLocaleConstraint
   @Override
   public String visitLocaleConstraint( final LocaleConstraint localeConstraint, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultLocaleConstraint.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( Locale.class );
      return "new DefaultLanguageConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( localeConstraint, context ) + ","
            // Locale localeCode
            + "Locale.forLanguageTag(\"" + localeConstraint.getLocaleCode().toLanguageTag() + "\"))";
   }

   // TODO investigate DefaultFixedPointConstraint
   @Override
   public String visitFixedPointConstraint( final FixedPointConstraint fixedPointConstraint, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultFixedPointConstraint.class );
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

   // TODO investigate DefaultEntity
   @Override
   public String visitEntity( final Entity entity, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultEntity.class );
      final String metaName = "Meta" + entity.getName();
      context.codeGenerationConfig().importTracker().trackPotentiallyParameterizedType( metaName );
      return "DefaultEntity.createDefaultEntity("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( entity, context ) + ","
            // List<Property> properties
            + metaName + INSTANCE_GET_PROPERTIES
            // Optional<ComplexType> _extends
            + extendsComplexType( entity, context ) + ")";
   }

   // TODO investigate DefaultAbstractEntity
   @Override
   public String visitAbstractEntity( final AbstractEntity abstractEntity, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultAbstractEntity.class );
      final String metaName = "Meta" + abstractEntity.getName();
      context.codeGenerationConfig().importTracker().trackPotentiallyParameterizedType( metaName );
      return "DefaultAbstractEntity.createDefaultAbstractEntity("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( abstractEntity, context ) + ","
            // List<Property> properties
            + metaName + INSTANCE_GET_PROPERTIES
            // Optional<ComplexType> _extends
            + extendsComplexType( abstractEntity, context ) + ","
            // List<AspectModelUrn> extendingElements
            + "List.of(" + abstractEntity.getExtendingElements().stream().sorted()
            .map( extendingElement -> "AspectModelUrn.fromUrn(\"" + extendingElement.getUrn() + "\")" ).collect( Collectors.joining( "," ) )
            + "))";
   }

   // TODO investigate DefaultScalar
   @Override
   public String visitScalar( final Scalar scalar, final StaticCodeGenerationContext context ) {
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultScalar.class );
      return "new DefaultScalar(\"" + scalar.getUrn() + "\" )";
   }

   //TODO fix it
   private String extendsComplexType( final ComplexType complexType, final StaticCodeGenerationContext context ) {
      if ( complexType.getExtends().isEmpty() ) {
         return "Optional.empty()";
      }

      final ComplexType type = complexType.getExtends().get();
      if ( type.is( Entity.class ) ) {
         final Entity entity = type.as( Entity.class );
         //         context.codeGenerationConfig().importTracker().importExplicit( DefaultEntity.class );
         final String metaName = "Meta" + entity.getName();
         context.codeGenerationConfig().importTracker().trackPotentiallyParameterizedType( metaName );
         return "Optional.of(DefaultEntity.createDefaultEntity(" + getMetaModelBaseAttributes( complexType, context ) + "," + metaName
               + INSTANCE_GET_PROPERTIES + extendsComplexType( entity, context ) + "))";
      }
      // AbstractEntity
      final AbstractEntity abstractEntity = type.as( AbstractEntity.class );
      //      context.codeGenerationConfig().importTracker().importExplicit( DefaultAbstractEntity.class );
      final String metaName = "Meta" + abstractEntity.getName();
      context.codeGenerationConfig().importTracker().trackPotentiallyParameterizedType( metaName );
      return "Optional.of(DefaultAbstractEntity.createDefaultAbstractEntity(" + getMetaModelBaseAttributes( abstractEntity, context ) + ","
            + metaName + INSTANCE_GET_PROPERTIES + extendsComplexType( abstractEntity, context ) + "," + "List.of("
            + abstractEntity.getExtendingElements().stream().sorted()
            .map( extendingElement -> "AspectModelUrn.fromUrn( \"" + extendingElement.getUrn() + "\" )" )
            .collect( Collectors.joining( "," ) ) + ")" + "))";
   }

   //TODO fix it
   private <T> String getOptionalStaticDeclarationValue( final Type type, final Optional<T> optionalValue,
         final StaticCodeGenerationContext context ) {
      if ( optionalValue.isEmpty() ) {
         return "Optional.empty()";
      }

      if ( optionalValue.get() instanceof final ScalarValue scalarValue ) {
         return "Optional.of(" + ( scalarValue ).accept( this, context ) + ")";
      }

      //      context.codeGenerationConfig().importTracker().importExplicit( AspectModelTsUtil.getDataTypeClass( type ) );
      final Resource xsdType = ResourceFactory.createResource( type.getUrn() );
      String valueExpression = optionalValue.get().toString();
      if ( type.getUrn().endsWith( "#float" ) ) {
         valueExpression = valueExpression + "f";
      }
      if ( AspectModelTsUtil.doesValueNeedToBeQuoted( type.getUrn() ) ) {
         valueExpression = "\"" + valueExpression + "\"";
      } else {
         valueExpression = StringEscapeUtils.escapeJava( valueExpression );
      }
      return "Optional.of(" + valueInitializer.apply( xsdType, valueExpression ) + ")";
   }

   // TODO validate generated code
   public String exampleValue( final Property property, final StaticCodeGenerationContext context ) {
      return property.getExampleValue()
            .map( exampleValue -> visitScalarValue( exampleValue, context ) )
            .orElse( "{}" );
   }

   /*
    * This method is a crutch because velocity is not able to call the parameterized actual method
    */
   public String metaModelBaseAttributes( final Property property, final StaticCodeGenerationContext context ) {
      return getMetaModelBaseAttributes( property, context );
   }

   private String getMetaModelBaseAttributes( final ModelElement element, final StaticCodeGenerationContext context ) {
      final StringBuilder builder = new StringBuilder();
      builder.append( START );
      if ( element.isAnonymous() ) {
         builder.append( "isAnonymous" ).append( ASSIGNMENT ).append( element.isAnonymous() ).append( END_PARAM );
      } else {
         builder.append( "urn" ).append( ASSIGNMENT ).append( elementUrn( element, context ) ).append( END_PARAM );
      }

      builder.append( "preferredNames" ).append( ASSIGNMENT ).append( START_ARRAY );
      element.getPreferredNames().stream().sorted().forEach( preferredName -> {
         builder.append( START );
         builder.append( VALUE_NAME ).append( ASSIGNMENT ).append( createLiteral( preferredName.getValue() ) ).append( END_PARAM );
         builder.append( "languageTag" ).append( ASSIGNMENT )
               .append( "'" ).append( preferredName.getLanguageTag().toLanguageTag() ).append( "'" ).append( END_PARAM );
         builder.append( END ).append( END_PARAM );
      } );
      builder.append( END_ARRAY ).append( END_PARAM );

      builder.append( "descriptions" ).append( ASSIGNMENT ).append( START_ARRAY );
      element.getDescriptions().stream().sorted().forEach( description -> {
         builder.append( START );
         builder.append( VALUE_NAME ).append( ASSIGNMENT ).append( createLiteral( description.getValue() ) ).append( END_PARAM );
         builder.append( "languageTag" ).append( ASSIGNMENT )
               .append( "'" ).append( description.getLanguageTag().toLanguageTag() ).append( "'" ).append( END_PARAM );
         builder.append( END ).append( END_PARAM );
      } );
      builder.append( END_ARRAY ).append( END_PARAM );

      builder.append( "see" ).append( ASSIGNMENT ).append( START_ARRAY );
      element.getSee().stream().sorted().forEach( see -> builder.append( "'" ).append( see ).append( "'" ).append( END_PARAM ) );
      builder.append( END_ARRAY ).append( END_PARAM );

      builder.append( END );
      return builder.toString();
   }

   private String elementUrn( final ModelElement element, final StaticCodeGenerationContext context ) {
      if ( element.urn().toString().startsWith( context.modelUrnPrefix() ) ) {
         return "this.NAMESPACE + '" + element.getName() + "'";
      }
      if ( element.urn().toString().startsWith( context.characteristicBaseUrn() ) ) {
         return "this.CHARACTERISTIC_NAMESPACE + '#" + element.getName() + "'";
      }
      return "'" + element.urn() + "'";
   }

   public static final String END_PARAM = ",\n";
   public static final String ASSIGNMENT = " : ";
   public static final String START = "{\n";
   public static final String END = "}";
   public static final String START_ARRAY = "[ ";
   public static final String END_ARRAY = " ]";
   public static final String META_MODEL_BASE_ATTRIBUTES_NAME = "metaModelBaseAttributes";
   public static final String VALUE_NAME = "value";
   public static final String TYPE_NAME = "type";
   public static final String DATA_TYPE_NAME = "dataType";
   public static final String INSTANCE_GET_PROPERTIES = ".INSTANCE.getProperties(),";
}
