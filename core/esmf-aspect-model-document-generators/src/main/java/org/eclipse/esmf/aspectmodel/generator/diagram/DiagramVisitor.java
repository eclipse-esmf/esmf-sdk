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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.characteristic.Code;
import org.eclipse.esmf.characteristic.Collection;
import org.eclipse.esmf.characteristic.Duration;
import org.eclipse.esmf.characteristic.Either;
import org.eclipse.esmf.characteristic.Enumeration;
import org.eclipse.esmf.characteristic.Measurement;
import org.eclipse.esmf.characteristic.Quantifiable;
import org.eclipse.esmf.characteristic.Set;
import org.eclipse.esmf.characteristic.SingleEntity;
import org.eclipse.esmf.characteristic.SortedSet;
import org.eclipse.esmf.characteristic.State;
import org.eclipse.esmf.characteristic.StructuredValue;
import org.eclipse.esmf.characteristic.TimeSeries;
import org.eclipse.esmf.characteristic.Trait;
import org.eclipse.esmf.constraint.EncodingConstraint;
import org.eclipse.esmf.constraint.FixedPointConstraint;
import org.eclipse.esmf.constraint.LanguageConstraint;
import org.eclipse.esmf.constraint.LengthConstraint;
import org.eclipse.esmf.constraint.RangeConstraint;
import org.eclipse.esmf.constraint.RegularExpressionConstraint;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.StructureElement;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.metamodel.impl.BoundDefinition;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;
import org.eclipse.esmf.samm.KnownVersion;

import com.google.common.collect.ImmutableList;
import org.apache.commons.text.WordUtils;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

/**
 * AspectVisitor that creates an {@link AbstractDiagram} representation for the given Aspect Model.
 */
public class DiagramVisitor implements AspectVisitor<AbstractDiagram, Optional<Context>> {
   private final Locale locale;
   private final Map<ModelElement, Box> seenElements = new HashMap<>();

   public DiagramVisitor( final Locale locale ) {
      this.locale = locale;
   }

   @Override
   public AbstractDiagram visitBase( final ModelElement modelElement, final Optional<Context> context ) {
      return AbstractDiagram.EMPTY;
   }

   @Override
   public AbstractDiagram visitStructureElement( final StructureElement element, final Optional<Context> context ) {
      final AbstractDiagram result = defaultBox( element, context.orElseThrow().prototype() );
      final Box box = result.getFocusBox();
      for ( final Property property : element.getProperties() ) {
         final StringBuilder labelBuilder = new StringBuilder();
         labelBuilder.append( element instanceof Event ? "parameter" : "property" );
         if ( property.isOptional() ) {
            labelBuilder.append( " (optional)" );
         } else if ( property.isNotInPayload() ) {
            labelBuilder.append( " (not in payload)" );
         }
         if ( !property.getName().equals( property.getPayloadName() ) ) {
            labelBuilder.append( " (" ).append( property.getPayloadName() ).append( ")" );
         }
         result.add( childElementDiagram( box, property, labelBuilder.toString() ) );
      }
      return result;
   }

   @Override
   public AbstractDiagram visitAspect( final Aspect aspect, final Optional<Context> context ) {
      if ( seenElements.containsKey( aspect ) ) {
         return new AbstractDiagram( seenElements.get( aspect ) );
      }

      final AbstractDiagram result = visitStructureElement( aspect, Optional.of( new Context( null, "Aspect" ) ) );
      final Box box = result.getFocusBox();
      aspect.getEvents().stream().map( event -> childElementDiagram( box, event, "event" ) ).forEach( result::add );
      aspect.getOperations().stream().map( operation -> childElementDiagram( box, operation, "operation" ) ).forEach( result::add );
      return result;
   }

   @Override
   public AbstractDiagram visitEntity( final Entity entity, final Optional<Context> context ) {
      if ( seenElements.containsKey( entity ) ) {
         return new AbstractDiagram( seenElements.get( entity ) );
      }

      final AbstractDiagram result = visitStructureElement( entity,
            context.map( oldContext -> new Context( oldContext.parent(), "Entity" ) ) );
      final Box box = result.getFocusBox();
      entity.getExtends().stream().map( superType -> childElementDiagram( box, superType, "extends" ) ).forEach( result::add );
      return result;
   }

   @Override
   public AbstractDiagram visitProperty( final Property property, final Optional<Context> context ) {
      if ( seenElements.containsKey( property ) ) {
         return new AbstractDiagram( seenElements.get( property ) );
      }

      final AbstractDiagram result = defaultBox( property, (property.isAbstract() ? "Abstract" : "") + "Property" );
      final Box box = result.getFocusBox();
      property.getCharacteristic()
            .filter( characteristic -> !(characteristic.getAspectModelUrn().isEmpty() && characteristic.getName()
                  .equals( "UnnamedCharacteristic" )) )
            .map( characteristic ->
                  childElementDiagram( box, characteristic, "characteristic" ) )
            .ifPresent( result::add );
      property.getExtends().ifPresent( superProperty -> result.add( childElementDiagram( box, superProperty, "extends" ) ) );
      return result;
   }

   @Override
   public AbstractDiagram visitCharacteristic( final Characteristic characteristic, final Optional<Context> context ) {
      if ( seenElements.containsKey( characteristic ) ) {
         return new AbstractDiagram( seenElements.get( characteristic ) );
      }

      final AbstractDiagram result = defaultBox( characteristic, "Characteristic" );
      final Box box = result.getFocusBox();
      characteristic.getDataType().ifPresent( type -> {
         if ( type.isScalar() ) {
            final Scalar scalar = type.as( Scalar.class );
            final String typeName = scalar.getUrn().replace( XSD.NS, "" ).replace( RDF.uri, "" )
                  .replace( new SAMM( KnownVersion.getLatest() ).getNamespace(), "" );
            result.getFocusBox().addEntry( attribute( "dataType", String.class, () -> typeName ) );
         } else {
            result.add( childElementDiagram( box, type.as( ComplexType.class ), "dataType" ) );
         }
      } );
      return result;
   }

   @Override
   public AbstractDiagram visitOperation( final Operation operation, final Optional<Context> context ) {
      if ( seenElements.containsKey( operation ) ) {
         return new AbstractDiagram( seenElements.get( operation ) );
      }

      final AbstractDiagram result = defaultBox( operation, "Operation" );
      final Box box = result.getFocusBox();

      for ( final Property property : operation.getInput() ) {
         result.add( childElementDiagram( box, property, "input" ) );
      }
      operation.getOutput().ifPresent( output -> result.add( childElementDiagram( box, output, "output" ) ) );
      return result;
   }

   @Override
   public AbstractDiagram visitAbstractEntity( final AbstractEntity abstractEntity, final Optional<Context> context ) {
      if ( seenElements.containsKey( abstractEntity ) ) {
         return new AbstractDiagram( seenElements.get( abstractEntity ) );
      }

      final AbstractDiagram result =
            visitStructureElement( abstractEntity, context.map( oldContext -> new Context( oldContext.parent(), "AbstractEntity" ) ) );
      final Box box = result.getFocusBox();
      abstractEntity.getExtends().stream().map( superType -> childElementDiagram( box, superType, "extends" ) ).forEach( result::add );
      return result;
   }

   @Override
   public AbstractDiagram visitEvent( final Event event, final Optional<Context> context ) {
      if ( seenElements.containsKey( event ) ) {
         return new AbstractDiagram( seenElements.get( event ) );
      }
      return visitStructureElement( event, context.map( oldContext -> new Context( oldContext.parent(), "Event" ) ) );
   }

   @Override
   public AbstractDiagram visitUnit( final Unit unit, final Optional<Context> context ) {
      if ( seenElements.containsKey( unit ) ) {
         return new AbstractDiagram( seenElements.get( unit ) );
      }

      final AbstractDiagram result = defaultBox( unit, "Unit" );
      final Box box = result.getFocusBox();
      unit.getSymbol().ifPresent( symbol -> box.addEntry( attribute( "symbol", String.class, () -> symbol ) ) );
      unit.getReferenceUnit().ifPresent( referenceUnit ->
            box.addEntry( attribute( "referenceUnit", String.class, () -> referenceUnit ) ) );
      unit.getCode().ifPresent( code -> box.addEntry( attribute( "code", String.class, () -> code ) ) );
      unit.getConversionFactor().ifPresent( conversionFactor ->
            box.addEntry( attribute( "conversionFactor", String.class, () -> conversionFactor ) ) );
      return result;
   }

   @Override
   public AbstractDiagram visitTrait( final Trait trait, final Optional<Context> context ) {
      if ( seenElements.containsKey( trait ) ) {
         return new AbstractDiagram( seenElements.get( trait ) );
      }

      final AbstractDiagram result = defaultBox( trait, "Trait" );
      final Box box = result.getFocusBox();
      result.add( childElementDiagram( box, trait.getBaseCharacteristic(), "baseCharacteristic" ) );
      for ( final Constraint constraint : trait.getConstraints() ) {
         result.add( childElementDiagram( box, constraint, "constraint" ) );
      }
      return result;
   }

   @Override
   public AbstractDiagram visitLengthConstraint( final LengthConstraint lengthConstraint, final Optional<Context> context ) {
      if ( seenElements.containsKey( lengthConstraint ) ) {
         return new AbstractDiagram( seenElements.get( lengthConstraint ) );
      }

      final AbstractDiagram result = defaultBox( lengthConstraint, "LengthConstraint" );
      final Box box = result.getFocusBox();
      lengthConstraint.getMinValue().ifPresent( minValue -> box.addEntry( attribute( "minValue", BigInteger.class, () -> minValue ) ) );
      lengthConstraint.getMinValue().ifPresent( maxValue -> box.addEntry( attribute( "maxValue", BigInteger.class, () -> maxValue ) ) );
      return result;
   }

   @Override
   public AbstractDiagram visitRangeConstraint( final RangeConstraint rangeConstraint, final Optional<Context> context ) {
      if ( seenElements.containsKey( rangeConstraint ) ) {
         return new AbstractDiagram( seenElements.get( rangeConstraint ) );
      }

      final AbstractDiagram result = defaultBox( rangeConstraint, "RangeConstraint" );
      final Box box = result.getFocusBox();
      rangeConstraint.getMinValue().ifPresent( minValue -> box.addEntry( attribute( "minValue", ScalarValue.class, () -> minValue ) ) );
      rangeConstraint.getMaxValue().ifPresent( maxValue -> box.addEntry( attribute( "maxValue", ScalarValue.class, () -> maxValue ) ) );
      if ( rangeConstraint.getLowerBoundDefinition() == BoundDefinition.AT_LEAST
            || rangeConstraint.getLowerBoundDefinition() == BoundDefinition.GREATER_THAN ) {
         box.addEntry( attribute( "lowerBoundDefinition", String.class,
               () -> rangeConstraint.getLowerBoundDefinition().toString().toLowerCase() ) );
      }
      if ( rangeConstraint.getUpperBoundDefinition() == BoundDefinition.AT_MOST
            || rangeConstraint.getUpperBoundDefinition() == BoundDefinition.LESS_THAN ) {
         box.addEntry( attribute( "upperBoundDefinition", String.class,
               () -> rangeConstraint.getUpperBoundDefinition().toString().toLowerCase() ) );
      }

      return result;
   }

   @Override
   public AbstractDiagram visitFixedPointConstraint( final FixedPointConstraint fixedPointConstraint, final Optional<Context> context ) {
      if ( seenElements.containsKey( fixedPointConstraint ) ) {
         return new AbstractDiagram( seenElements.get( fixedPointConstraint ) );
      }

      final AbstractDiagram result = defaultBox( fixedPointConstraint, "FixedPointConstraint" );
      final Box box = result.getFocusBox();
      box.addEntry( attribute( "integer", Integer.class, fixedPointConstraint::getInteger ) );
      box.addEntry( attribute( "scale", Integer.class, fixedPointConstraint::getScale ) );
      return result;
   }

   @Override
   public AbstractDiagram visitEncodingConstraint( final EncodingConstraint encodingConstraint, final Optional<Context> context ) {
      if ( seenElements.containsKey( encodingConstraint ) ) {
         return new AbstractDiagram( seenElements.get( encodingConstraint ) );
      }

      final AbstractDiagram result = defaultBox( encodingConstraint, "EncodingConstraint" );
      final Box box = result.getFocusBox();
      box.addEntry( attribute( "charset", Charset.class, encodingConstraint::getValue ) );
      return result;
   }

   @Override
   public AbstractDiagram visitLanguageConstraint( final LanguageConstraint languageConstraint, final Optional<Context> context ) {
      if ( seenElements.containsKey( languageConstraint ) ) {
         return new AbstractDiagram( seenElements.get( languageConstraint ) );
      }

      final AbstractDiagram result = defaultBox( languageConstraint, "LanguageConstraint" );
      final Box box = result.getFocusBox();
      box.addEntry( attribute( "charset", String.class, () -> languageConstraint.getLanguageCode().toLanguageTag() ) );
      return result;
   }

   @Override
   public AbstractDiagram visitRegularExpressionConstraint( final RegularExpressionConstraint regularExpressionConstraint,
         final Optional<Context> context ) {
      if ( seenElements.containsKey( regularExpressionConstraint ) ) {
         return new AbstractDiagram( seenElements.get( regularExpressionConstraint ) );
      }

      final AbstractDiagram result = defaultBox( regularExpressionConstraint, "RegularExpressionConstraint" );
      final Box box = result.getFocusBox();
      box.addEntry( attribute( "value", String.class, regularExpressionConstraint::getValue ) );
      return result;
   }

   @Override
   public AbstractDiagram visitQuantifiable( final Quantifiable quantifiable, final Optional<Context> context ) {
      if ( seenElements.containsKey( quantifiable ) ) {
         return new AbstractDiagram( seenElements.get( quantifiable ) );
      }

      final AbstractDiagram result = visitCharacteristic( quantifiable, context );
      final Box box = result.getFocusBox();
      box.setPrototype( "Quantifiable" );
      quantifiable.getUnit().ifPresent( unit -> result.add( childElementDiagram( box, unit, "unit" ) ) );
      return result;
   }

   @Override
   public AbstractDiagram visitMeasurement( final Measurement measurement, final Optional<Context> context ) {
      if ( seenElements.containsKey( measurement ) ) {
         return new AbstractDiagram( seenElements.get( measurement ) );
      }

      final AbstractDiagram result = visitQuantifiable( measurement, context );
      result.getFocusBox().setPrototype( "Measurement" );
      return result;
   }

   @Override
   public AbstractDiagram visitCode( final Code code, final Optional<Context> context ) {
      if ( seenElements.containsKey( code ) ) {
         return new AbstractDiagram( seenElements.get( code ) );
      }

      final AbstractDiagram result = visitCharacteristic( (Characteristic) code, context );
      final Box box = result.getFocusBox();
      box.setPrototype( "Code" );
      return result;
   }

   @Override
   public AbstractDiagram visitCollection( final Collection collection, final Optional<Context> context ) {
      if ( seenElements.containsKey( collection ) ) {
         return new AbstractDiagram( seenElements.get( collection ) );
      }

      final AbstractDiagram result;
      if ( collection.getElementCharacteristic().isPresent() ) {
         // If the collection has an elementCharacteristic, don't use visitCharacteristic to prevent additional dataType edge
         result = defaultBox( collection, "Collection" );
      } else {
         result = visitCharacteristic( (Characteristic) collection, context );
         result.getFocusBox().setPrototype( "Collection" );
      }

      final Box box = result.getFocusBox();
      collection.getElementCharacteristic().ifPresent( elementCharacteristic ->
            result.add( childElementDiagram( box, elementCharacteristic, "elementCharacteristic" ) ) );
      return result;
   }

   @Override
   public AbstractDiagram visitList( final org.eclipse.esmf.characteristic.List list, final Optional<Context> context ) {
      if ( seenElements.containsKey( list ) ) {
         return new AbstractDiagram( seenElements.get( list ) );
      }

      final AbstractDiagram result = visitCollection( list, context );
      final Box box = result.getFocusBox();
      box.setPrototype( "List" );
      return result;
   }

   @Override
   public AbstractDiagram visitSet( final Set set, final Optional<Context> context ) {
      if ( seenElements.containsKey( set ) ) {
         return new AbstractDiagram( seenElements.get( set ) );
      }

      final AbstractDiagram result = visitCollection( set, context );
      final Box box = result.getFocusBox();
      box.setPrototype( "Set" );
      return result;
   }

   @Override
   public AbstractDiagram visitSortedSet( final SortedSet sortedSet, final Optional<Context> context ) {
      if ( seenElements.containsKey( sortedSet ) ) {
         return new AbstractDiagram( seenElements.get( sortedSet ) );
      }

      final AbstractDiagram result = visitCollection( sortedSet, context );
      final Box box = result.getFocusBox();
      box.setPrototype( "SortedSet" );
      return result;
   }

   @Override
   public AbstractDiagram visitDuration( final Duration duration, final Optional<Context> context ) {
      if ( seenElements.containsKey( duration ) ) {
         return new AbstractDiagram( seenElements.get( duration ) );
      }

      final AbstractDiagram result = visitQuantifiable( duration, context );
      final Box box = result.getFocusBox();
      box.setPrototype( "Duration" );
      return result;
   }

   @Override
   public AbstractDiagram visitEnumeration( final Enumeration enumeration, final Optional<Context> context ) {
      if ( seenElements.containsKey( enumeration ) ) {
         return new AbstractDiagram( seenElements.get( enumeration ) );
      }

      final AbstractDiagram result = visitCharacteristic( enumeration, context );
      final Box box = result.getFocusBox();
      box.setPrototype( "Enumeration" );
      final AbstractDiagram collectionDiagram = processValuesCollection( box, enumeration.getValues(), "value" );
      if ( collectionDiagram.getScalarValue() == null ) {
         result.add( collectionDiagram );
      } else {
         box.addEntry( attribute( "values", String.class, () -> String.join( ", ", collectionDiagram.getScalarValue() ) ) );
      }
      return result;
   }

   @Override
   public AbstractDiagram visitEither( final Either either, final Optional<Context> context ) {
      final AbstractDiagram result = defaultBox( either, "Either" );
      final Box box = result.getFocusBox();
      result.add( childElementDiagram( box, either.getLeft(), "left" ) );
      result.add( childElementDiagram( box, either.getRight(), "right" ) );
      return result;
   }

   @Override
   public AbstractDiagram visitSingleEntity( final SingleEntity singleEntity, final Optional<Context> context ) {
      if ( seenElements.containsKey( singleEntity ) ) {
         return new AbstractDiagram( seenElements.get( singleEntity ) );
      }

      final AbstractDiagram result = visitCharacteristic( (Characteristic) singleEntity, context );
      final Box box = result.getFocusBox();
      box.setPrototype( "SingleEntity" );
      return result;
   }

   @Override
   public AbstractDiagram visitState( final State state, final Optional<Context> context ) {
      final AbstractDiagram result = visitEnumeration( state, context );
      final Box box = result.getFocusBox();
      box.setPrototype( "State" );
      final AbstractDiagram valueDiagram = state.getDefaultValue().accept( this, Optional.of( new Context( box ) ) );
      result.add( valueDiagram );
      if ( valueDiagram.getScalarValue() != null ) {
         box.addEntry( attribute( "defaultValue", String.class, valueDiagram::getScalarValue ) );
      }
      if ( valueDiagram.getFocusBox() != null ) {
         result.addEdge( new Edge( box, valueDiagram.getFocusBox(), "defaultValue" ) );
      }
      return result;
   }

   @Override
   public AbstractDiagram visitStructuredValue( final StructuredValue structuredValue, final Optional<Context> context ) {
      final AbstractDiagram result = visitCharacteristic( (Characteristic) structuredValue, context );
      final Box box = result.getFocusBox();
      box.setPrototype( "StructuredValue" );
      int index = 1;
      for ( final Object element : structuredValue.getElements() ) {
         if ( element instanceof final Property property ) {
            result.add( childElementDiagram( box, property, "element (%d)".formatted( index ) ) );
            index++;
         }
      }

      box.addEntry( attribute( "deconstructionRule", String.class, structuredValue::getDeconstructionRule ) );
      return result;
   }

   @Override
   public AbstractDiagram visitTimeSeries( final TimeSeries timeSeries, final Optional<Context> context ) {
      final AbstractDiagram result = visitSortedSet( timeSeries, context );
      final Box box = result.getFocusBox();
      box.setPrototype( "TimeSeries" );
      return result;
   }

   @Override
   public AbstractDiagram visitScalarValue( final ScalarValue value, final Optional<Context> context ) {
      final AbstractDiagram result = new AbstractDiagram( null );
      if ( value.getValue() instanceof final LangString langString ) {
         result.setScalarValue( "\"%s\"@%s".formatted( langString.getValue(), langString.getLanguageTag() ) );
      } else {
         result.setScalarValue( value.getValue().toString() );
      }
      return result;
   }

   @Override
   public AbstractDiagram visitCollectionValue( final CollectionValue collection, final Optional<Context> context ) {
      return context.map( parentContext -> processValuesCollection( parentContext.parent(), collection.getValues(),
            Optional.ofNullable( parentContext.edgeLabel() ).orElse( "value" ) ) ).orElse( AbstractDiagram.EMPTY );
   }

   @Override
   public AbstractDiagram visitEntityInstance( final EntityInstance instance, final Optional<Context> context ) {
      if ( seenElements.containsKey( instance ) ) {
         return new AbstractDiagram( seenElements.get( instance ) );
      }

      final AbstractDiagram result = defaultBox( instance, instance.getEntityType().getName() );
      final Box box = result.getFocusBox();
      result.add( childElementDiagram( box, instance.getEntityType(), "type" ) );
      for ( final Map.Entry<Property, Value> assertion : instance.getAssertions().entrySet() ) {
         final Property property = assertion.getKey();
         final String propertyName = property.getName()
               + (property.getPayloadName().equals( property.getName() ) ? "" : " (%s)".formatted( property.getPayloadName() ));
         final AbstractDiagram valueDiagram = assertion.getValue().accept( this, Optional.of( new Context( box, "", propertyName ) ) );
         if ( valueDiagram.getScalarValue() == null ) {
            // If the value's diagram representation's scalar value is not set, the value is itself a box
            result.add( valueDiagram );
            // If the value is a collection, it doesn't have a focus box
            if ( valueDiagram.getFocusBox() != null ) {
               result.addEdge( new Edge( box, valueDiagram.getFocusBox(), propertyName ) );
            }
         } else {
            // If the value's diagram representation's scalar value is set, use it for an attribute entry in the entity instance's box
            box.addEntry( attribute( propertyName, String.class, valueDiagram::getScalarValue ) );
         }
      }

      return result;
   }

   private <T extends java.util.Collection<Value>> AbstractDiagram processValuesCollection( final Box parentBox, final T collection,
         final String edgeLabel ) {
      final AbstractDiagram result = new AbstractDiagram( null );
      boolean hasScalarValues = false;
      final List<String> scalarValues = new ArrayList<>();
      for ( final Value value : collection ) {
         final AbstractDiagram valueDiagram = value.accept( this, Optional.of( new Context( parentBox ) ) );
         if ( valueDiagram.getScalarValue() == null ) {
            result.add( valueDiagram );
            result.addEdge( new Edge( parentBox, valueDiagram.getFocusBox(), edgeLabel ) );
         } else {
            hasScalarValues = true;
            scalarValues.add( valueDiagram.getScalarValue() );
         }
      }
      if ( hasScalarValues ) {
         result.setScalarValue( String.join( ", ", scalarValues ) );
      }
      return result;
   }

   private AbstractDiagram childElementDiagram( final Box parent, final ModelElement child, final String edgeLabel ) {
      final Optional<Context> childElementContext = Optional.of( new Context( parent ) );
      final AbstractDiagram result = child.accept( this, childElementContext );
      result.addEdge( new Edge( parent, result.getFocusBox(), edgeLabel ) );
      return result;
   }

   private AbstractDiagram defaultBox( final NamedElement element, final String prototype ) {
      final Box box = new Box( prototype, element.getAspectModelUrn().isPresent() ? element.getName() : "" );
      final ImmutableList.Builder<String> standardAttributes = ImmutableList.builder();
      element.getPreferredNames().stream()
            .filter( preferredName -> preferredName.getLanguageTag().equals( locale ) )
            .findFirst()
            .map( LangString::getValue )
            .ifPresent( preferredName ->
                  standardAttributes.addAll( attribute( "preferredName", String.class, () -> preferredName ) ) );
      element.getDescriptions().stream()
            .filter( description -> description.getLanguageTag().equals( locale ) )
            .findFirst()
            .map( LangString::getValue )
            .ifPresent( description ->
                  standardAttributes.addAll( attribute( "description", String.class, () -> description ) ) );
      if ( !element.getSee().isEmpty() ) {
         standardAttributes.addAll( attribute( "see", String.class, () -> String.join( ", ", element.getSee() ) ) );
      }
      box.addEntry( standardAttributes.build() );
      seenElements.put( element, box );
      return new AbstractDiagram( box );
   }

   private <T> List<String> attribute( final String attributeName, final Class<T> type, final Supplier<T> attribute ) {
      final String value;
      if ( type.equals( ScalarValue.class ) ) {
         value = ((ScalarValue) attribute.get()).getValue().toString();
      } else {
         value = attribute.get().toString();
      }

      final String entry = "%s: %s".formatted( attributeName, value );
      final String[] lines = WordUtils.wrap( entry, 60 ).split( "\n" );
      final ImmutableList.Builder<String> builder = ImmutableList.builder();
      for ( int i = 0; i < lines.length; i++ ) {
         builder.add( i > 0 ? "   " + lines[i] : lines[i] );
      }
      return builder.build();
   }
}
