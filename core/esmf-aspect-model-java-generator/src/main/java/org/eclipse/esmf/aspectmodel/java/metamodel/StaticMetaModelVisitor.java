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

package org.eclipse.esmf.aspectmodel.java.metamodel;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.java.AspectModelJavaUtil;
import org.eclipse.esmf.aspectmodel.java.ValueExpressionVisitor;
import org.eclipse.esmf.aspectmodel.java.ValueInitializer;
import org.eclipse.esmf.aspectmodel.java.exception.CodeGenerationException;
import org.eclipse.esmf.characteristic.Code;
import org.eclipse.esmf.characteristic.Collection;
import org.eclipse.esmf.characteristic.Duration;
import org.eclipse.esmf.characteristic.Enumeration;
import org.eclipse.esmf.characteristic.Measurement;
import org.eclipse.esmf.characteristic.Quantifiable;
import org.eclipse.esmf.characteristic.SingleEntity;
import org.eclipse.esmf.characteristic.State;
import org.eclipse.esmf.characteristic.StructuredValue;
import org.eclipse.esmf.characteristic.Trait;
import org.eclipse.esmf.characteristic.impl.DefaultCode;
import org.eclipse.esmf.characteristic.impl.DefaultCollection;
import org.eclipse.esmf.characteristic.impl.DefaultEnumeration;
import org.eclipse.esmf.characteristic.impl.DefaultList;
import org.eclipse.esmf.characteristic.impl.DefaultSet;
import org.eclipse.esmf.characteristic.impl.DefaultSingleEntity;
import org.eclipse.esmf.characteristic.impl.DefaultSortedSet;
import org.eclipse.esmf.characteristic.impl.DefaultState;
import org.eclipse.esmf.characteristic.impl.DefaultStructuredValue;
import org.eclipse.esmf.characteristic.impl.DefaultTrait;
import org.eclipse.esmf.constraint.EncodingConstraint;
import org.eclipse.esmf.constraint.FixedPointConstraint;
import org.eclipse.esmf.constraint.LanguageConstraint;
import org.eclipse.esmf.constraint.LengthConstraint;
import org.eclipse.esmf.constraint.LocaleConstraint;
import org.eclipse.esmf.constraint.RangeConstraint;
import org.eclipse.esmf.constraint.RegularExpressionConstraint;
import org.eclipse.esmf.constraint.impl.DefaultEncodingConstraint;
import org.eclipse.esmf.constraint.impl.DefaultFixedPointConstraint;
import org.eclipse.esmf.constraint.impl.DefaultLanguageConstraint;
import org.eclipse.esmf.constraint.impl.DefaultLengthConstraint;
import org.eclipse.esmf.constraint.impl.DefaultLocaleConstraint;
import org.eclipse.esmf.constraint.impl.DefaultRangeConstraint;
import org.eclipse.esmf.constraint.impl.DefaultRegularExpressionConstraint;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.QuantityKind;
import org.eclipse.esmf.metamodel.QuantityKinds;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.Units;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.impl.BoundDefinition;
import org.eclipse.esmf.metamodel.impl.DefaultAbstractEntity;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.metamodel.impl.DefaultCollectionValue;
import org.eclipse.esmf.metamodel.impl.DefaultEntity;
import org.eclipse.esmf.metamodel.impl.DefaultEntityInstance;
import org.eclipse.esmf.metamodel.impl.DefaultQuantityKind;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.impl.DefaultScalarValue;
import org.eclipse.esmf.metamodel.impl.DefaultUnit;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;
import org.eclipse.esmf.samm.KnownVersion;

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
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultScalarValue.class );
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
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultCollectionValue.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( collectionClass );
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
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultEntityInstance.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( Map.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( HashMap.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( Property.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( Value.class );
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
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultSingleEntity.class );
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

      context.getCodeGenerationConfig().importTracker().importExplicit( implementationClass );
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
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultCode.class );
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
      context.getCodeGenerationConfig().importTracker().importExplicit( quantifiable.getClass() );
      context.getCodeGenerationConfig().importTracker().importExplicit( Units.class );
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
         context.getCodeGenerationConfig().importTracker().importExplicit( Units.class );
         return "Units.fromName(\"" + unit.getName() + "\", KnownVersion." + unit.getMetaModelVersion() + ")";
      }

      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultUnit.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( HashSet.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( Optional.class );
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
         context.getCodeGenerationConfig().importTracker().importExplicit( QuantityKinds.class );
         return "QuantityKinds." + AspectModelJavaUtil.toConstant( quantityKind.getName() );
      }

      // If not, create a new instance of the default implementation
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultQuantityKind.class );
      return "new DefaultQuantityKind("
            + getMetaModelBaseAttributes( quantityKind, context ) + ","
            + "\"" + StringEscapeUtils.escapeJava( quantityKind.getLabel() ) + "\""
            + ")";
   }

   @Override
   public String visitState( final State state, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultState.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( ArrayList.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( Value.class );
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
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultEnumeration.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( ArrayList.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( Value.class );
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
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultStructuredValue.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( ArrayList.class );
      return "new DefaultStructuredValue("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( structuredValue, context ) + ","
            // Type type
            + structuredValue.getDataType().orElseThrow( noTypeException ).accept( this, context ) + ","
            // String deconstructionRule
            + AspectModelJavaUtil.createLiteral( structuredValue.getDeconstructionRule() ) + ","
            // List<Object> elements
            + "new ArrayList<Object>(){{" + structuredValue.getElements().stream().sequential()
            .map( element -> String.format( "add(%s);", AspectModelJavaUtil.printStructuredValueElement( element ) ) )
            .collect( Collectors.joining() ) + "}})";
   }

   @Override
   public String visitTrait( final Trait trait, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultTrait.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( ArrayList.class );
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
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultCharacteristic.class );
      return "new DefaultCharacteristic("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( characteristic, context ) + ","
            // Optional<Type> type
            + characteristic.getDataType().map( type -> "Optional.of(" + type.accept( this, context ) + ")" ).orElse( "Optional.empty()" )
            + ")";
   }

   @Override
   public String visitLengthConstraint( final LengthConstraint lengthConstraint, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultLengthConstraint.class );
      final Scalar nonNegativeInteger = new DefaultScalar( XSD.nonNegativeInteger.getURI(), lengthConstraint.getMetaModelVersion() );
      return "new DefaultLengthConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( lengthConstraint, context ) + ","
            // Optional<BigInteger> min
            + getOptionalStaticDeclarationValue( nonNegativeInteger, lengthConstraint.getMinValue(), lengthConstraint.getMetaModelVersion(),
            context ) + ","
            // Optional<BigInteger> max
            + getOptionalStaticDeclarationValue( nonNegativeInteger, lengthConstraint.getMaxValue(), lengthConstraint.getMetaModelVersion(),
            context ) + ")";
   }

   @Override
   public String visitRangeConstraint( final RangeConstraint rangeConstraint, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultRangeConstraint.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( BoundDefinition.class );
      final Type characteristicType = context.getCurrentCharacteristic().getDataType().orElseThrow( noTypeException );
      return "new DefaultRangeConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( rangeConstraint, context ) + ","
            // Optional<Object> minValue
            + getOptionalStaticDeclarationValue( characteristicType, rangeConstraint.getMinValue(), rangeConstraint.getMetaModelVersion(),
            context ) + ","
            // Optional<Object> maxValue
            + getOptionalStaticDeclarationValue( characteristicType, rangeConstraint.getMaxValue(), rangeConstraint.getMetaModelVersion(),
            context ) + ","
            // BoundDefinition lowerBoundDefinition
            + "BoundDefinition." + rangeConstraint.getLowerBoundDefinition().name() + ","
            // BoundDefinition upperBoundDefinition
            + "BoundDefinition." + rangeConstraint.getUpperBoundDefinition().name() + ")";
   }

   @Override
   public String visitRegularExpressionConstraint( final RegularExpressionConstraint regularExpressionConstraint,
         final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultRegularExpressionConstraint.class );
      return "new DefaultRegularExpressionConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( regularExpressionConstraint, context ) + ","
            // String value
            + AspectModelJavaUtil.createLiteral( regularExpressionConstraint.getValue() ) + ")";
   }

   @Override
   public String visitEncodingConstraint( final EncodingConstraint encodingConstraint, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultEncodingConstraint.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( Charset.class );
      return "new DefaultEncodingConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( encodingConstraint, context ) + ","
            // Charset value
            + "Charset.forName(\"" + encodingConstraint.getValue() + "\"))";
   }

   @Override
   public String visitLanguageConstraint( final LanguageConstraint languageConstraint, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultLanguageConstraint.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( Locale.class );
      return "new DefaultLanguageConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( languageConstraint, context ) + ","
            // Locale languageCode
            + "Locale.forLanguageTag(\"" + languageConstraint.getLanguageCode().toLanguageTag() + "\"))";
   }

   @Override
   public String visitLocaleConstraint( final LocaleConstraint localeConstraint, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultLocaleConstraint.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( Locale.class );
      return "new DefaultLanguageConstraint("
            // MetaModelBaseAttributes
            + getMetaModelBaseAttributes( localeConstraint, context ) + ","
            // Locale localeCode
            + "Locale.forLanguageTag(\"" + localeConstraint.getLocaleCode().toLanguageTag() + "\"))";
   }

   @Override
   public String visitFixedPointConstraint( final FixedPointConstraint fixedPointConstraint, final StaticCodeGenerationContext context ) {
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultFixedPointConstraint.class );
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
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultEntity.class );
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
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultAbstractEntity.class );
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
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultScalar.class );
      return "new DefaultScalar(\"" + scalar.getUrn() + "\", KnownVersion." + scalar.getMetaModelVersion() + ")";
   }

   private String extendsComplexType( final ComplexType complexType, final StaticCodeGenerationContext context ) {
      if ( complexType.getExtends().isEmpty() ) {
         return "Optional.empty()";
      }

      final ComplexType type = complexType.getExtends().get();
      if ( type.is( Entity.class ) ) {
         final Entity entity = type.as( Entity.class );
         context.getCodeGenerationConfig().importTracker().importExplicit( DefaultEntity.class );
         return "Optional.of(DefaultEntity.createDefaultEntity(" + getMetaModelBaseAttributes( complexType, context ) + "," + "Meta"
               + entity.getName()
               + ".INSTANCE.getProperties()," + extendsComplexType( entity, context ) + "))";
      }
      // AbstractEntity
      final AbstractEntity abstractEntity = type.as( AbstractEntity.class );
      context.getCodeGenerationConfig().importTracker().importExplicit( DefaultAbstractEntity.class );
      return "Optional.of(DefaultAbstractEntity.createDefaultAbstractEntity(" + getMetaModelBaseAttributes( abstractEntity, context ) + ","
            + "Meta"
            + abstractEntity.getName() + ".INSTANCE.getProperties()," + extendsComplexType( abstractEntity, context ) + "," + "List.of("
            + abstractEntity.getExtendingElements().stream().sorted()
            .map( extendingElement -> "AspectModelUrn.fromUrn( \"" + extendingElement.getUrn() + "\" )" )
            .collect( Collectors.joining( "," ) ) + ")" + "))";
   }

   private <T> String getOptionalStaticDeclarationValue( final Type type, final Optional<T> optionalValue,
         final KnownVersion metaModelVersion,
         final StaticCodeGenerationContext context ) {
      if ( optionalValue.isEmpty() ) {
         return "Optional.empty()";
      }

      if ( optionalValue.get() instanceof ScalarValue ) {
         return "Optional.of(" + ((ScalarValue) optionalValue.get()).accept( this, context ) + ")";
      }

      context.getCodeGenerationConfig().importTracker().importExplicit( AspectModelJavaUtil.getDataTypeClass( type ) );
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
      return "Optional.of(" + valueInitializer.apply( xsdType, valueExpression, metaModelVersion ) + ")";
   }

   /*
    * This method is a crutch because velocity is not able to call the parameterized actual method
    */
   public String metaModelBaseAttributes( final Property property, final StaticCodeGenerationContext context ) {
      return getMetaModelBaseAttributes( property, context );
   }

   public <T extends NamedElement & ModelElement> String getMetaModelBaseAttributes( final T element,
         final StaticCodeGenerationContext context ) {
      if ( element.getPreferredNames().isEmpty() && element.getDescriptions().isEmpty() && element.getSee().isEmpty() ) {
         return "MetaModelBaseAttributes.from(" + "KnownVersion." + element.getMetaModelVersion().toString() + ", " + elementUrn( element,
               context ) + ", "
               + "\"" + element.getName() + "\" )";
      }

      final StringBuilder builder = new StringBuilder();
      builder.append( "MetaModelBaseAttributes.builderFor( \"" ).append( element.getName() ).append( "\" )" );
      builder.append( ".withMetaModelVersion(KnownVersion." ).append( element.getMetaModelVersion().toString() ).append( ")" );
      builder.append( ".withUrn(" ).append( elementUrn( element, context ) ).append( ")" );
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

   public String elementUrn( final NamedElement element, final StaticCodeGenerationContext context ) {
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
