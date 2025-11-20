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

import java.util.Arrays;
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
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Unit;
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
import org.eclipse.esmf.metamodel.constraint.EncodingConstraint;
import org.eclipse.esmf.metamodel.constraint.FixedPointConstraint;
import org.eclipse.esmf.metamodel.constraint.LanguageConstraint;
import org.eclipse.esmf.metamodel.constraint.LengthConstraint;
import org.eclipse.esmf.metamodel.constraint.LocaleConstraint;
import org.eclipse.esmf.metamodel.constraint.RangeConstraint;
import org.eclipse.esmf.metamodel.constraint.RegularExpressionConstraint;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

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
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultScalarValue", "./aspect-meta-model/default-scalar-value" );
      final ValueExpressionVisitor.Context valueContext = new ValueExpressionVisitor.Context( context.codeGenerationConfig(), false );
      return "new DefaultScalarValue("
            //  type
            + value.getType().accept( this, context ) + ","
            //  value
            + value.accept( valueExpressionVisitor, valueContext )
            + ")";
   }

   @Override
   public String visitCollectionValue( final CollectionValue collection, final StaticCodeGenerationContext context ) {
      final Class<?> collectionClass = collection.getValues().getClass();
      context.codeGenerationConfig().importTracker()
            .importExplicit( "DefaultCollectionValue", "./aspect-meta-model/default-collection-value" );
      final StringBuilder result = new StringBuilder();
      result.append( "new DefaultCollectionValue(" );
      // collectionType
      result.append( collectionClass.getSimpleName() ).append( "," );
      // values
      result.append( "[ " );
      collection.getValues()
            .forEach( value -> result.append( value.accept( this, context ) ).append( "," ) );
      result.append( " ]," );
      // type
      result.append( collection.getType().accept( this, context ) );
      result.append( ")" );
      return result.toString();
   }

   @Override
   public String visitEntityInstance( final EntityInstance instance, final StaticCodeGenerationContext context ) {

      context.codeGenerationConfig().importTracker()
            .importExplicit( "DefaultEntityInstance", "./aspect-meta-model/default-entity-instance" );
      final Entity entity = instance.getEntityType();
      final StringBuilder builder = new StringBuilder();
      builder.append( "new DefaultEntityInstance(" );
      // name
      builder.append( NULL_PARAM );
      // metaModelType
      builder.append( instance.getEntityType().accept( this, context ) );
      builder.append( END_PARAM );
      // _descriptions
      if ( !instance.getDescriptions().isEmpty() ) {
         builder.append( "new Map([" );
         for ( final Property property : instance.getEntityType().getProperties() ) {
            final Value instancePropertyValue = instance.getAssertions().get( property );
            if ( instancePropertyValue == null ) {
               continue;
            }
            final String className = "Meta" + entity.getName();
            final String staticPropertyField = AspectModelTsUtil.toConstant( property.getName() );
            builder.append( "[" );
            builder.append( className );
            builder.append( "." );
            builder.append( staticPropertyField );
            builder.append( "," );
            builder.append( instancePropertyValue.accept( this, context ) );
            builder.append( "]," );
         }
         builder.append( "])," );
      } else {
         builder.append( UNDEFINED );
      }
      builder.append( ")" );
      return builder.toString();
   }

   // validate
   @Override
   public String visitSingleEntity( final SingleEntity singleEntity, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultSingleEntity", "./aspect-meta-model" );
      final String constructor = "new DefaultSingleEntity("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // Type dataType
            + singleEntity.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ")";
      return wrapMetaModelBaseAttributes( singleEntity, context, constructor, "defaultSingleEntity" );
   }

   // validate
   @Override
   public String visitCollection( final Collection collection, final StaticCodeGenerationContext context ) {
      final String implementationClassName = switch ( collection.getCollectionType() ) {
         case LIST -> "DefaultList";
         case SET -> "DefaultSet";
         case SORTEDSET -> "DefaultSortedSet";
         default -> "DefaultCollection";
      };
      context.codeGenerationConfig().importTracker().importExplicit( implementationClassName, "./aspect-meta-model" );

      final String elementCharacteristic = collection.getElementCharacteristic()
            .map( characteristic -> characteristic.accept( this, context ) )
            .orElse( UNDEFINED );

      final String constructor;
      if ( implementationClassName.equals( "DefaultCollection" ) ) {
         constructor = "new DefaultCollection("
               // metaModelVersion
               + NULL_PARAM
               // aspectModelUrn
               + NULL_PARAM
               // name
               + NULL_PARAM
               // _isAllowDuplicates
               + "true, "
               // _isOrdered
               + "false, "
               // _elementCharacteristic
               + elementCharacteristic + END_PARAM
               // Type dataType
               + collection.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ")";
      } else {
         constructor = "new " + implementationClassName + "("
               // metaModelVersion
               + NULL_PARAM
               // aspectModelUrn
               + NULL_PARAM
               // name
               + NULL_PARAM
               // _elementCharacteristic
               + elementCharacteristic + END_PARAM
               // Type dataType
               + collection.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ")";
      }

      final String variableName = implementationClassName.substring( 0, 1 ).toLowerCase() + implementationClassName.substring( 1 );
      return wrapMetaModelBaseAttributes( collection, context, constructor, variableName );
   }

   // validate
   @Override
   public String visitCode( final Code code, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultCode", "./aspect-meta-model" );
      final String constructor = "new DefaultCode("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // Type dataType
            + code.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ")";
      return wrapMetaModelBaseAttributes( code, context, constructor, "defaultCode" );
   }

   @Override
   public String visitDuration( final Duration duration, final StaticCodeGenerationContext context ) {
      return generateForQuantifiable( duration, context );
   }

   @Override
   public String visitQuantifiable( final Quantifiable quantifiable, final StaticCodeGenerationContext context ) {
      return generateForQuantifiable( quantifiable, context );
   }

   @Override
   public String visitMeasurement( final Measurement measurement, final StaticCodeGenerationContext context ) {
      final String simpleName = measurement.getClass().getSimpleName();
      context.codeGenerationConfig().importTracker().importExplicit( simpleName, "./aspect-meta-model" );
      final String constructor = "new " + simpleName + "("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // unit? Unit
            + measurement.getUnit().map( unit -> unit.accept( this, context ) ).orElse( UNDEFINED ) + ","
            // Type dataType
            + measurement.getDataType().orElseThrow( noTypeException ).accept( this, context )
            + ")";
      final String variableName = simpleName.substring( 0, 1 ).toLowerCase() + simpleName.substring( 1 );
      return wrapMetaModelBaseAttributes( measurement, context, constructor, variableName );
   }

   // validate
   private <T extends Quantifiable> String generateForQuantifiable( final T quantifiable, final StaticCodeGenerationContext context ) {
      final String simpleName = quantifiable.getClass().getSimpleName();
      context.codeGenerationConfig().importTracker().importExplicit( simpleName, "./aspect-meta-model" );
      final String constructor = "new " + simpleName + "("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // Type dataType
            + quantifiable.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ","
            // unit? Unit
            + quantifiable.getUnit().map( unit -> unit.accept( this, context ) ).orElse( UNDEFINED )
            + ")";
      final String variableName = simpleName.substring( 0, 1 ).toLowerCase() + simpleName.substring( 1 );
      return wrapMetaModelBaseAttributes( quantifiable, context, constructor, variableName );
   }

   // validate
   @Override
   public String visitUnit( final Unit unit, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultUnit", "./aspect-meta-model/default-unit" );
      final String constructor = "new DefaultUnit("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // Type dataType
            // _symbol
            + unit.getSymbol().map( AspectModelTsUtil::createLiteral ).orElse( UNDEFINED ) + ","
            // _code
            + unit.getCode().map( AspectModelTsUtil::createLiteral ).orElse( UNDEFINED ) + ","
            // _referenceUnit
            + unit.getReferenceUnit().map( AspectModelTsUtil::createLiteral ).orElse( UNDEFINED ) + ","
            // _conversionFactor
            + unit.getConversionFactor().map( AspectModelTsUtil::createLiteral ).orElse( UNDEFINED ) + ","
            // _quantityKinds
            + "[ "
            + unit.getQuantityKinds().stream()
            .map( quantityKind -> quantityKind.accept( this, context ) )
            .collect( Collectors.joining( END_PARAM ) )
            + " ]"
            + ")";
      return wrapMetaModelBaseAttributes( unit, context, constructor, "defaultUnit" );
   }

   // validate
   @Override
   public String visitQuantityKind( final QuantityKind quantityKind, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultQuantityKind", "./aspect-meta-model" );
      final String constructor = "new DefaultQuantityKind("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // _label
            + createLiteral( quantityKind.getLabel() ) + ")";
      return wrapMetaModelBaseAttributes( quantityKind, context, constructor, "defaultQuantityKind" );
   }

   // validate
   @Override
   public String visitState( final State state, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultState", "./aspect-meta-model" );
      final String constructor = "new DefaultState("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // values
            + "["
            + state.getValues().stream().sorted()
            .map( value -> value.accept( this, context ) )
            .collect( Collectors.joining( END_PARAM ) )
            + "]" + ","
            // defaultValue
            + state.getDefaultValue().accept( this, context ) + ","
            // Type dataType
            + state.getDataType().orElseThrow( noTypeException ).accept( this, context )
            + ")";
      return wrapMetaModelBaseAttributes( state, context, constructor, "defaultState" );
   }

   // validate
   @Override
   public String visitEnumeration( final Enumeration enumeration, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultEnumeration", "./aspect-meta-model" );
      final String constructor = "new DefaultEnumeration("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // values
            + "["
            + enumeration.getValues().stream().sorted()
            .map( value -> value.accept( this, context ) )
            .collect( Collectors.joining( END_PARAM ) )
            + "],"
            // Type dataType
            + enumeration.getDataType().orElseThrow( noTypeException ).accept( this, context )
            + ")";
      return wrapMetaModelBaseAttributes( enumeration, context, constructor, "defaultEnumeration" );
   }

   // validate
   @Override
   public String visitStructuredValue( final StructuredValue structuredValue, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultStructuredValue", "./aspect-meta-model" );
      // The referenced Properties created by the StructuredValuePropertiesDeconstructor could differ from the referenced Properties
      // given in the StructuredValue: For example, when two properties "startDate" and "endDate" both use a StructuredValue
      // with elements ( :year "-" :month "-" :day ), the StructuredValue will refer to "year", "month" and "day", while
      // referencedProperties retrieved here will refer to "startDateYear", "startDateMonth", "startDateDay" or
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

      final String constructor = "new DefaultStructuredValue("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // _deconstructionRule
            + createLiteral( structuredValue.getDeconstructionRule() ) + ","
            // _elements
            + "["
            + structuredValue.getElements().stream()
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
            .collect( Collectors.joining( END_PARAM ) )
            + "],"
            // Type dataType
            + structuredValue.getDataType().orElseThrow( noTypeException ).accept( this, context )
            + ")";
      return wrapMetaModelBaseAttributes( structuredValue, context, constructor, "defaultStructuredValue" );
   }

   // validate
   @Override
   public String visitTrait( final Trait trait, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultTrait", "./aspect-meta-model" );
      final String constructor = "new DefaultTrait("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // _baseCharacteristic
            + trait.getBaseCharacteristic().accept( this, context ) + ","
            // _constraints
            + "["
            + trait.getConstraints().stream().sorted()
            .map( constraint -> constraint.accept( this, context.withCurrentCharacteristic( trait ) ) )
            .collect( Collectors.joining( END_PARAM ) )
            + "]"
            + ")";
      return wrapMetaModelBaseAttributes( trait, context, constructor, "trait" );
   }

   @Override
   public String visitCharacteristic( final Characteristic characteristic, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultCharacteristic", "./aspect-meta-model" );
      final String constructor = "new DefaultCharacteristic("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // Type dataType
            + characteristic.getDataType()
            .map( a -> a.accept( this, context ) )
            .orElse( UNDEFINED )
            + ")";
      return wrapMetaModelBaseAttributes( characteristic, context, constructor, "defaultCharacteristic" );
   }

   // validate
   @Override
   public String visitLengthConstraint( final LengthConstraint lengthConstraint, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultLengthConstraint", "./aspect-meta-model" );
      final Scalar nonNegativeInteger = new DefaultScalar( XSD.nonNegativeInteger.getURI() );
      final String constructor = "new DefaultLengthConstraint("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // _minValue
            + getOptionalStaticDeclarationValue( nonNegativeInteger, lengthConstraint.getMinValue(), context ) + ","
            // _maxValue
            + getOptionalStaticDeclarationValue( nonNegativeInteger, lengthConstraint.getMinValue(), context ) + ","
            + ")";
      return wrapMetaModelBaseAttributes( lengthConstraint, context, constructor, "lengthConstraint" );
   }

   // validate
   @Override
   public String visitRangeConstraint( final RangeConstraint rangeConstraint, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultRangeConstraint", "./aspect-meta-model" );
      context.codeGenerationConfig().importTracker().importExplicit( "BoundDefinition", "./aspect-meta-model/bound-definition" );
      final Type characteristicType = context.currentCharacteristic().getDataType().orElseThrow( noTypeException );
      final String constructor = "new DefaultRangeConstraint("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // upperBoundDefinition
            + "BoundDefinition." + rangeConstraint.getUpperBoundDefinition().name() + ","
            // lowerBoundDefinition
            + "BoundDefinition." + rangeConstraint.getLowerBoundDefinition().name() + ","
            // minValue
            + getOptionalStaticDeclarationValue( characteristicType, rangeConstraint.getMinValue(), context ) + ","
            // maxValue
            + getOptionalStaticDeclarationValue( characteristicType, rangeConstraint.getMaxValue(), context ) + ","
            + ")";
      return wrapMetaModelBaseAttributes( rangeConstraint, context, constructor, "defaultRangeConstraint" );
   }

   // validate
   @Override
   public String visitRegularExpressionConstraint( final RegularExpressionConstraint regularExpressionConstraint,
         final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultRegularExpressionConstraint", "./aspect-meta-model" );
      final String constructor = "new DefaultRegularExpressionConstraint("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // value
            + createLiteral( regularExpressionConstraint.getValue() )
            + ")";
      return wrapMetaModelBaseAttributes( regularExpressionConstraint, context, constructor, "regularExpressionConstraint" );
   }

   // validate
   @Override
   public String visitEncodingConstraint( final EncodingConstraint encodingConstraint, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultEncodingConstraint", "./aspect-meta-model" );
      final String constructor = "new DefaultEncodingConstraint("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // value
            + createLiteral( encodingConstraint.getValue().toString() )
            + ")";
      return wrapMetaModelBaseAttributes( encodingConstraint, context, constructor, "encodingConstraint" );
   }

   // validate
   @Override
   public String visitLanguageConstraint( final LanguageConstraint languageConstraint, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultLanguageConstraint", "./aspect-meta-model" );
      final String constructor = "new DefaultLanguageConstraint("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // languageCode
            + createLiteral( languageConstraint.getLanguageCode().toLanguageTag() )
            + ")";
      return wrapMetaModelBaseAttributes( languageConstraint, context, constructor, "defaultLanguageConstraint" );
   }

   // validate
   @Override
   public String visitLocaleConstraint( final LocaleConstraint localeConstraint, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultLocaleConstraint", "./aspect-meta-model" );
      final String constructor = "new DefaultLocaleConstraint("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // languageCode
            + localeConstraint.getLocaleCode().toLanguageTag()
            + ")";
      return wrapMetaModelBaseAttributes( localeConstraint, context, constructor, "defaultLocaleConstraint" );
   }

   // validate
   @Override
   public String visitFixedPointConstraint( final FixedPointConstraint fixedPointConstraint, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultFixedPointConstraint", "./aspect-meta-model" );
      final String constructor = "new DefaultFixedPointConstraint("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // scale
            + fixedPointConstraint.getScale() + END_PARAM
            // integer
            + fixedPointConstraint.getInteger()
            + ")";
      return wrapMetaModelBaseAttributes( fixedPointConstraint, context, constructor, "defaultFixedPointConstraint" );
   }

   @Override
   public String visitConstraint( final Constraint constraint, final StaticCodeGenerationContext context ) {
      throw new UnsupportedOperationException( "Could not generate code for unknown constraint: " + constraint.getName() );
   }

   // validate
   @Override
   public String visitEntity( final Entity entity, final StaticCodeGenerationContext context ) {
      final String metaName = "Meta" + entity.getName();
      context.codeGenerationConfig().importTracker().trackPotentiallyParameterizedType( metaName );
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultEntity", "./aspect-meta-model" );
      final String constructor = "new DefaultEntity("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // _properties
            + metaName + INSTANCE_GET_PROPERTIES
            // _isAbstract
            + entity.isAbstractEntity() + END_PARAM
            // _extends
            + extendsComplexType( entity, context )
            + ")";
      return wrapMetaModelBaseAttributes( entity, context, constructor, "defaultEntity" + entity.getName() );
   }

   // validate
   @Override
   public String visitAbstractEntity( final AbstractEntity abstractEntity, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultEntity", "./aspect-meta-model" );
      final String metaName = "Meta" + abstractEntity.getName();
      context.codeGenerationConfig().importTracker().trackPotentiallyParameterizedType( metaName );
      final String constructor = "new DefaultEntity("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // _properties
            + metaName + INSTANCE_GET_PROPERTIES
            // _isAbstract
            + true + END_PARAM
            // _extends
            + extendsComplexType( abstractEntity, context )
            + ")";
      return wrapMetaModelBaseAttributes( abstractEntity, context, constructor, "abstractDefaultEntity" + abstractEntity.getName() );
   }

   // validate
   @Override
   public String visitScalar( final Scalar scalar, final StaticCodeGenerationContext context ) {
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultScalar", "./aspect-meta-model" );
      return "new DefaultScalar(\"" + scalar.getUrn() + "\" )";
   }

   // validate
   private String extendsComplexType( final ComplexType complexType, final StaticCodeGenerationContext context ) {
      if ( complexType.getExtends().isEmpty() ) {
         return UNDEFINED;
      }
      final ComplexType type = complexType.getExtends().get();
      context.codeGenerationConfig().importTracker().importExplicit( "DefaultEntity", "./aspect-meta-model" );
      if ( type.is( Entity.class ) ) {
         final Entity entity = type.as( Entity.class );
         final String metaName = "Meta" + entity.getName();
         context.codeGenerationConfig().importTracker().trackPotentiallyParameterizedType( metaName );
         final String constructor = "new DefaultEntity("
               // metaModelVersion
               + NULL_PARAM
               // aspectModelUrn
               + NULL_PARAM
               // name
               + NULL_PARAM
               // _properties
               + metaName + INSTANCE_GET_PROPERTIES
               // _isAbstract
               + false + END_PARAM
               // _extends
               + extendsComplexType( entity, context )
               + ")";
         return wrapMetaModelBaseAttributes( entity, context, constructor, "extendsDefaultEntity" + entity.getName() );
      }
      // AbstractEntity
      final AbstractEntity abstractEntity = type.as( AbstractEntity.class );
      final String metaName = "Meta" + abstractEntity.getName();
      context.codeGenerationConfig().importTracker().trackPotentiallyParameterizedType( metaName );
      final String constructor = "new DefaultEntity("
            // metaModelVersion
            + NULL_PARAM
            // aspectModelUrn
            + NULL_PARAM
            // name
            + NULL_PARAM
            // _properties
            + metaName + INSTANCE_GET_PROPERTIES
            // _isAbstract
            + true + END_PARAM
            // _extends
            + extendsComplexType( abstractEntity, context )
            + ")";
      return wrapMetaModelBaseAttributes( abstractEntity, context, constructor, "extendsDefaultEntity" + abstractEntity.getName() );
   }

   // validate
   private <T> String getOptionalStaticDeclarationValue( final Type type, final Optional<T> optionalValue,
         final StaticCodeGenerationContext context ) {
      if ( optionalValue.isEmpty() ) {
         return UNDEFINED;
      }

      if ( optionalValue.get() instanceof final ScalarValue scalarValue ) {
         return ( scalarValue ).accept( this, context );
      }
      final Resource xsdType = ResourceFactory.createResource( type.getUrn() );
      String valueExpression = optionalValue.get().toString();
      if ( AspectModelTsUtil.doesValueNeedToBeQuoted( type.getUrn() ) ) {
         valueExpression = "'" + valueExpression + "'";
      } else {
         valueExpression = StringEscapeUtils.escapeJava( valueExpression );
      }

      if ( xsdType.equals( SammNs.SAMM.curie() ) ) {
         context.codeGenerationConfig().importTracker().importExplicit( "Curie", "./core/curie" );
         return String.format( "new Curie( %s )", valueExpression );
      }
      return valueInitializer.apply( xsdType, valueExpression );
   }

   // validate
   public String exampleValue( final Property property, final StaticCodeGenerationContext context ) {
      return property.getExampleValue()
            .map( exampleValue -> {
               if ( exampleValue instanceof final ScalarValue scalarValue ) {
                  return visitScalarValue( scalarValue, context );
               } else if ( exampleValue instanceof final Value value ) {
                  return visitValue( value, context );
               } else {
                  throw new IllegalArgumentException( "Unexpected exampleValue type: " + exampleValue.getClass() );
               }
            } )
            .orElse( UNDEFINED );
   }
   
   private String wrapMetaModelBaseAttributes( final ModelElement element, final StaticCodeGenerationContext context,
         final String initializationConstructor, final String variableName ) {
      final StringBuilder builder = new StringBuilder();

      builder.append( "(() => {" );
      builder.append( " const " ).append( variableName ).append( " = " ).append( initializationConstructor ).append( NEW_LINE );
      // variable.isAnonymousNode = true/false
      if ( element.isAnonymous() ) {
         builder.append( variableName ).append( ".isAnonymousNode = " ).append( element.isAnonymous() )
               .append( END_INITIALIZATION );
      } else {
         // variable.addAspectModelUrn = urn
         builder.append( variableName ).append( ".addAspectModelUrn = " ).append( elementUrn( element, context ) )
               .append( END_INITIALIZATION );
      }

      // variable.addPreferredName( 'en' , 'name' )
      element.getPreferredNames().stream().sorted()
            .forEach( preferredName -> builder.append(
                  applyMethod(
                        variableName,
                        "addPreferredName",
                        createLiteral( preferredName.getLanguageTag().toLanguageTag() ), createLiteral( preferredName.getValue() )
                  )
            ).append( END_INITIALIZATION ) );

      // variable.addDescription( 'en' , 'name' )
      element.getDescriptions().stream().sorted()
            .forEach( description -> builder.append(
                  applyMethod(
                        variableName,
                        "addDescription",
                        createLiteral( description.getLanguageTag().toLanguageTag() ), createLiteral( description.getValue() )
                  )
            ).append( END_INITIALIZATION ) );

      // variable.addSeeReference( 'example' )
      element.getSee().stream().sorted().forEach( see -> builder.append(
                  applyMethod( variableName, "addSeeReference", createLiteral( see ) )
            ).append( END_INITIALIZATION )
      );
      // return variable
      builder.append( " return " ).append( variableName ).append( ";" );
      builder.append( " })()" );
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

   public static final String NEW_LINE = "\n";
   public static final String END_INITIALIZATION = ";\n";
   public static final String END_PARAM = ",\n";
   public static final String END = "}";
   public static final String INSTANCE_GET_PROPERTIES = ".INSTANCE.getProperties(),";
   public static final String UNDEFINED = "undefined";
   public static final String NULL_PARAM = "null, \n";

   public static final String applyMethod( String name, String methodName, Object... params ) {
      return name + "." + methodName + "(" + Arrays.stream( params )
            .map( Object::toString )
            .collect( Collectors.joining( " , " ) ) + ")";
   }

}
