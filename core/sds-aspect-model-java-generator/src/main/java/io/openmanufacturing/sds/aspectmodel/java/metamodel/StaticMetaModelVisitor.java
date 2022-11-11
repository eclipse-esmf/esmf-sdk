/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.java.metamodel;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.java.AspectModelJavaUtil;
import io.openmanufacturing.sds.aspectmodel.java.ValueExpressionVisitor;
import io.openmanufacturing.sds.aspectmodel.java.ValueInitializer;
import io.openmanufacturing.sds.aspectmodel.java.exception.CodeGenerationException;
import io.openmanufacturing.sds.metamodel.AbstractEntity;
import io.openmanufacturing.sds.metamodel.Base;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.characteristic.Code;
import io.openmanufacturing.sds.characteristic.Collection;
import io.openmanufacturing.sds.metamodel.CollectionValue;
import io.openmanufacturing.sds.metamodel.ComplexType;
import io.openmanufacturing.sds.metamodel.Constraint;
import io.openmanufacturing.sds.characteristic.Duration;
import io.openmanufacturing.sds.constraint.EncodingConstraint;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.EntityInstance;
import io.openmanufacturing.sds.characteristic.Enumeration;
import io.openmanufacturing.sds.constraint.FixedPointConstraint;
import io.openmanufacturing.sds.metamodel.IsDescribed;
import io.openmanufacturing.sds.constraint.LanguageConstraint;
import io.openmanufacturing.sds.constraint.LengthConstraint;
import io.openmanufacturing.sds.constraint.LocaleConstraint;
import io.openmanufacturing.sds.characteristic.Measurement;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.characteristic.Quantifiable;
import io.openmanufacturing.sds.metamodel.QuantityKind;
import io.openmanufacturing.sds.metamodel.QuantityKinds;
import io.openmanufacturing.sds.constraint.RangeConstraint;
import io.openmanufacturing.sds.constraint.RegularExpressionConstraint;
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.ScalarValue;
import io.openmanufacturing.sds.metamodel.SingleEntity;
import io.openmanufacturing.sds.characteristic.State;
import io.openmanufacturing.sds.characteristic.StructuredValue;
import io.openmanufacturing.sds.characteristic.Trait;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.Unit;
import io.openmanufacturing.sds.metamodel.Units;
import io.openmanufacturing.sds.metamodel.Value;
import io.openmanufacturing.sds.metamodel.impl.BoundDefinition;
import io.openmanufacturing.sds.metamodel.impl.DefaultAbstractEntity;
import io.openmanufacturing.sds.metamodel.impl.DefaultCharacteristic;
import io.openmanufacturing.sds.metamodel.impl.DefaultCode;
import io.openmanufacturing.sds.metamodel.impl.DefaultCollection;
import io.openmanufacturing.sds.metamodel.impl.DefaultCollectionValue;
import io.openmanufacturing.sds.metamodel.impl.DefaultEncodingConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultEntity;
import io.openmanufacturing.sds.metamodel.impl.DefaultEntityInstance;
import io.openmanufacturing.sds.metamodel.impl.DefaultEnumeration;
import io.openmanufacturing.sds.metamodel.impl.DefaultFixedPointConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultLanguageConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultLengthConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultList;
import io.openmanufacturing.sds.metamodel.impl.DefaultLocaleConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultRangeConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultRegularExpressionConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultScalar;
import io.openmanufacturing.sds.metamodel.impl.DefaultScalarValue;
import io.openmanufacturing.sds.metamodel.impl.DefaultSet;
import io.openmanufacturing.sds.metamodel.impl.DefaultSingleEntity;
import io.openmanufacturing.sds.metamodel.impl.DefaultSortedSet;
import io.openmanufacturing.sds.metamodel.impl.DefaultState;
import io.openmanufacturing.sds.metamodel.impl.DefaultStructuredValue;
import io.openmanufacturing.sds.metamodel.impl.DefaultTrait;
import io.openmanufacturing.sds.metamodel.impl.DefaultUnit;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

public class StaticMetaModelVisitor implements AspectVisitor<String, StaticCodeGenerationContext> {
   private final ValueExpressionVisitor valueExpressionVisitor = new ValueExpressionVisitor();
   private final ValueInitializer valueInitializer = new ValueInitializer();

   private final Supplier<CodeGenerationException> noTypeException = () -> new CodeGenerationException( "Characteristic is missing its dataType" );

   @Override
   public String visitBase( final Base base, final StaticCodeGenerationContext context ) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String visitScalarValue( final ScalarValue value, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultScalarValue.class );
      final ValueExpressionVisitor.Context valueContext = new ValueExpressionVisitor.Context( context.getCodeGenerationConfig(), false );
      return "new DefaultScalarValue("
            // Object value
            + value.accept( valueExpressionVisitor, valueContext ) + ","
            // Scalar type
            + value.getType().accept( this, context ) + ")";
   }

   @Override
   public String visitCollectionValue( final CollectionValue collection, final StaticCodeGenerationContext context ) {
      final Class<?> collectionClass = collection.getValues().getClass();
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultCollectionValue.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( collectionClass );
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
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultEntityInstance.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( Map.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( HashMap.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( Property.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( Value.class );
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
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultSingleEntity.class );
      return "new DefaultSingleEntity("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( singleEntity, context ) + ", "
            // Type dataType
            + singleEntity.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ")";
   }

   @Override
   public String visitCollection( final Collection collection, final StaticCodeGenerationContext context ) {
      final Class<?> implementationClass;
      switch ( collection.getCollectionType() ) {
      case LIST:
         implementationClass = DefaultList.class;
         break;
      case SET:
         implementationClass = DefaultSet.class;
         break;
      case SORTEDSET:
         implementationClass = DefaultSortedSet.class;
         break;
      default:
         implementationClass = DefaultCollection.class;
         break;
      }

      context.getCodeGenerationConfig().getImportTracker().importExplicit( implementationClass );
      final String optionalType = collection.getDataType().map( type -> type.accept( this, context ) ).map( type -> "Optional.of(" + type + ")" )
            .orElse( "Optional.empty()" );
      final String optionalElementCharacteristic = collection.getElementCharacteristic().map( characteristic -> characteristic.accept( this, context ) )
            .map( characteristic -> "Optional.of(" + characteristic + ")" ).orElse( "Optional.empty()" );
      return "new " + implementationClass.getSimpleName() + "(" + getMetaModelBaseAttributes( collection, context ) + "," + optionalType + ","
            + optionalElementCharacteristic + ")";
   }

   @Override
   public String visitCode( final Code code, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultCode.class );
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
      context.getCodeGenerationConfig().getImportTracker().importExplicit( quantifiable.getClass() );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( Units.class );
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
      final Optional<Unit> unitFromCatalog = Units.fromName( unit.getName(), unit.getMetaModelVersion() );
      if ( unitFromCatalog.isPresent() ) {
         context.getCodeGenerationConfig().getImportTracker().importExplicit( Units.class );
         return "Units.fromName(\"" + unit.getName() + "\", KnownVersion." + unit.getMetaModelVersion() + ")";
      }

      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultUnit.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( HashSet.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( Optional.class );
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
      context.getCodeGenerationConfig().getImportTracker().importExplicit( QuantityKinds.class );
      return "QuantityKinds." + AspectModelJavaUtil.toConstant( quantityKind.getName() );
   }

   @Override
   public String visitState( final State state, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultState.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( ArrayList.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( Value.class );
      return "new DefaultState("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( state, context ) + ","
            // Type type
            + state.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ","
            // List<Value> values
            + "new ArrayList<Value>(){{" + state.getValues().stream().sorted().map( value -> String.format( "add(%s);", value.accept( this, context ) ) )
            .collect( Collectors.joining() ) + "}},"
            // Value defaultValue
            + state.getDefaultValue().accept( this, context ) + ")";
   }

   @Override
   public String visitEnumeration( final Enumeration enumeration, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultEnumeration.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( ArrayList.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( Value.class );
      return "new DefaultEnumeration("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( enumeration, context ) + ","
            // Type type
            + enumeration.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ","
            // List<Value> values
            + "new ArrayList<Value>(){{" + enumeration.getValues().stream().sorted().map( value -> String.format( "add(%s);", value.accept( this, context ) ) )
            .collect( Collectors.joining() ) + "}})";
   }

   @Override
   public String visitStructuredValue( final StructuredValue structuredValue, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultStructuredValue.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( ArrayList.class );
      return "new DefaultStructuredValue("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( structuredValue, context ) + ","
            // Type type
            + structuredValue.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ","
            // String deconstructionRule
            + "\"" + StringEscapeUtils.escapeJava( structuredValue.getDeconstructionRule() ) + "\","
            // List<Object> elements
            + "new ArrayList<Object>(){{" + structuredValue.getElements().stream().sequential()
            .map( element -> String.format( "add(%s);", AspectModelJavaUtil.printStructuredValueElement( element ) ) ).collect( Collectors.joining() ) + "}})";
   }

   @Override
   public String visitTrait( final Trait trait, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultTrait.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( ArrayList.class );
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
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultCharacteristic.class );
      return "new DefaultCharacteristic("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( characteristic, context ) + ","
            // Optional<Type> type
            + characteristic.getDataType().map( type -> "Optional.of(" + type.accept( this, context ) + ")" ).orElse( "Optional.empty()" ) + ")";
   }

   @Override
   public String visitLengthConstraint( final LengthConstraint lengthConstraint, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultLengthConstraint.class );
      final Scalar nonNegativeInteger = new DefaultScalar( XSD.nonNegativeInteger.getURI(), lengthConstraint.getMetaModelVersion() );
      return "new DefaultLengthConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( lengthConstraint, context ) + ","
            // Optional<BigInteger> min
            + getOptionalStaticDeclarationValue( nonNegativeInteger, lengthConstraint.getMinValue(), lengthConstraint.getMetaModelVersion(), context ) + ","
            // Optional<BigInteger> max
            + getOptionalStaticDeclarationValue( nonNegativeInteger, lengthConstraint.getMaxValue(), lengthConstraint.getMetaModelVersion(), context ) + ")";
   }

   @Override
   public String visitRangeConstraint( final RangeConstraint rangeConstraint, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultRangeConstraint.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( BoundDefinition.class );
      final Type characteristicType = context.getCurrentCharacteristic().getDataType().orElseThrow( noTypeException );
      return "new DefaultRangeConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( rangeConstraint, context ) + ","
            // Optional<Object> minValue
            + getOptionalStaticDeclarationValue( characteristicType, rangeConstraint.getMinValue(), rangeConstraint.getMetaModelVersion(), context ) + ","
            // Optional<Object> maxValue
            + getOptionalStaticDeclarationValue( characteristicType, rangeConstraint.getMaxValue(), rangeConstraint.getMetaModelVersion(), context ) + ","
            // BoundDefinition lowerBoundDefinition
            + "BoundDefinition." + rangeConstraint.getLowerBoundDefinition().name() + ","
            // BoundDefinition upperBoundDefinition
            + "BoundDefinition." + rangeConstraint.getUpperBoundDefinition().name() + ")";
   }

   @Override
   public String visitRegularExpressionConstraint( final RegularExpressionConstraint regularExpressionConstraint, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultRegularExpressionConstraint.class );
      return "new DefaultRegularExpressionConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( regularExpressionConstraint, context ) + ","
            // String value
            + "\"" + StringEscapeUtils.escapeJava( regularExpressionConstraint.getValue() ) + "\")";
   }

   @Override
   public String visitEncodingConstraint( final EncodingConstraint encodingConstraint, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultEncodingConstraint.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( Charset.class );
      return "new DefaultEncodingConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( encodingConstraint, context ) + ","
            // Charset value
            + "Charset.forName(\"" + encodingConstraint.getValue() + "\"))";
   }

   @Override
   public String visitLanguageConstraint( final LanguageConstraint languageConstraint, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultLanguageConstraint.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( Locale.class );
      return "new DefaultLanguageConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( languageConstraint, context ) + ","
            // Locale languageCode
            + "Locale.forLanguageTag(\"" + languageConstraint.getLanguageCode().toLanguageTag() + "\"))";
   }

   @Override
   public String visitLocaleConstraint( final LocaleConstraint localeConstraint, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultLocaleConstraint.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( Locale.class );
      return "new DefaultLanguageConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( localeConstraint, context ) + ","
            // Locale localeCode
            + "Locale.forLanguageTag(\"" + localeConstraint.getLocaleCode().toLanguageTag() + "\"))";
   }

   @Override
   public String visitFixedPointConstraint( final FixedPointConstraint fixedPointConstraint, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultFixedPointConstraint.class );
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
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultEntity.class );
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
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultAbstractEntity.class );
      return "DefaultAbstractEntity.createDefaultAbstractEntity("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( abstractEntity, context ) + ","
            // List<Property> properties
            + "Meta" + abstractEntity.getName() + ".INSTANCE.getProperties()" + ","
            // Optional<ComplexType> _extends
            + extendsComplexType( abstractEntity, context ) + ","
            // List<AspectModelUrn> extendingElements
            + "List.of(" + abstractEntity.getExtendingElements().stream().sorted()
            .map( extendingElement -> "AspectModelUrn.fromUrn(\"" + extendingElement.getUrn() + "\")" ).collect( Collectors.joining( "," ) ) + "))";
   }

   @Override
   public String visitScalar( final Scalar scalar, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultScalar.class );
      return "new DefaultScalar(\"" + scalar.getUrn() + "\", KnownVersion." + scalar.getMetaModelVersion() + ")";
   }

   private String extendsComplexType( final ComplexType complexType, final StaticCodeGenerationContext context ) {
      if ( complexType.getExtends().isEmpty() ) {
         return "Optional.empty()";
      }

      final ComplexType type = complexType.getExtends().get();
      if ( type.is( Entity.class ) ) {
         final Entity entity = type.as( Entity.class );
         context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultEntity.class );
         return "Optional.of(DefaultEntity.createDefaultEntity(" + getMetaModelBaseAttributes( complexType, context ) + "," + "Meta" + entity.getName()
               + ".INSTANCE.getProperties()," + extendsComplexType( entity, context ) + "))";
      }
      // AbstractEntity
      final AbstractEntity abstractEntity = type.as( AbstractEntity.class );
      context.getCodeGenerationConfig().getImportTracker().importExplicit( DefaultAbstractEntity.class );
      return "Optional.of(DefaultAbstractEntity.createDefaultAbstractEntity(" + getMetaModelBaseAttributes( abstractEntity, context ) + "," + "Meta"
            + abstractEntity.getName() + ".INSTANCE.getProperties()," + extendsComplexType( abstractEntity, context ) + "," + "List.of("
            + abstractEntity.getExtendingElements().stream().sorted()
            .map( extendingElement -> "AspectModelUrn.fromUrn( \"" + extendingElement.getUrn() + "\" )" ).collect( Collectors.joining( "," ) ) + ")" + "))";
   }

   private <T> String getOptionalStaticDeclarationValue( final Type type, final Optional<T> optionalValue, final KnownVersion metaModelVersion,
         final StaticCodeGenerationContext context ) {
      if ( optionalValue.isEmpty() ) {
         return "Optional.empty()";
      }

      if ( optionalValue.get() instanceof ScalarValue ) {
         return "Optional.of(" + ((ScalarValue) optionalValue.get()).accept( this, context ) + ")";
      }

      context.getCodeGenerationConfig().getImportTracker().importExplicit( AspectModelJavaUtil.getDataTypeClass( type ) );
      final Resource xsdType = ResourceFactory.createResource( type.getUrn() );
      String valueExpression = optionalValue.get().toString();
      if ( type.getUrn().endsWith( "#float" ) ) {
         valueExpression = valueExpression + "f";
      }
      if ( AspectModelJavaUtil.doesValueNeedsToBeQuoted( type.getUrn() ) ) {
         valueExpression = "\"" + valueExpression + "\"";
      } else {
         valueExpression = StringEscapeUtils.escapeJava( valueExpression );
      }
      return "Optional.of(" + valueInitializer.apply( xsdType, valueExpression, metaModelVersion ) + ")";
   }

   /*
    * This method is a crutch because velocity is not able to call the parameterized actual method
    */
   public String metaModelBaseAttributes( final Property property, final StaticCodeGenerationContext context ) {
      return getMetaModelBaseAttributes( property, context );
   }

   public <T extends IsDescribed & Base> String getMetaModelBaseAttributes( final T element, final StaticCodeGenerationContext context ) {
      if ( element.getPreferredNames().isEmpty() && element.getDescriptions().isEmpty() && element.getSee().isEmpty() ) {
         return "MetaModelBaseAttributes.from(" + "KnownVersion." + element.getMetaModelVersion().toString() + ", " + elementUrn( element, context ) + ", "
               + "\"" + element.getName() + "\" )";
      }

      final StringBuilder builder = new StringBuilder();
      builder.append( "MetaModelBaseAttributes.builderFor( \"" ).append( element.getName() ).append( "\" )" );
      builder.append( ".withMetaModelVersion(KnownVersion." ).append( element.getMetaModelVersion().toString() ).append( ")" );
      builder.append( ".withUrn(" ).append( elementUrn( element, context ) ).append( ")" );
      element.getPreferredNames().stream().sorted().forEach( preferredName -> {
         builder.append( ".withPreferredName(Locale.forLanguageTag(\"" ).append( preferredName.getLanguageTag().toLanguageTag() ).append( "\")," );
         builder.append( "\"" ).append( StringEscapeUtils.escapeJava( preferredName.getValue() ) ).append( "\")" );
      } );
      element.getDescriptions().stream().sorted().forEach( description -> {
         builder.append( ".withDescription(Locale.forLanguageTag(\"" ).append( description.getLanguageTag().toLanguageTag() ).append( "\")," );
         builder.append( "\"" ).append( StringEscapeUtils.escapeJava( description.getValue() ) ).append( "\")" );
      } );
      element.getSee().stream().sorted().forEach( see -> builder.append( ".withSee(\"" ).append( StringEscapeUtils.escapeJava( see ) ).append( "\")" ) );
      builder.append( ".build()" );
      return builder.toString();
   }

   public String elementUrn( final IsDescribed element, final StaticCodeGenerationContext context ) {
      if ( element.getAspectModelUrn().isEmpty() ) {
         return "null";
      }
      if ( element.getAspectModelUrn().get().toString().startsWith( context.getModelUrnPrefix() ) ) {
         return "AspectModelUrn.fromUrn( NAMESPACE + \"" + element.getName() + "\" )";
      }
      if ( element.getAspectModelUrn().get().toString().startsWith( context.getCharacteristicBaseUrn() ) ) {
         return "AspectModelUrn.fromUrn( CHARACTERISTIC_NAMESPACE + \"#" + element.getName() + "\" )";
      }
      return "AspectModelUrn.fromUrn( \"" + element.getAspectModelUrn().get() + "\" )";
   }
}
