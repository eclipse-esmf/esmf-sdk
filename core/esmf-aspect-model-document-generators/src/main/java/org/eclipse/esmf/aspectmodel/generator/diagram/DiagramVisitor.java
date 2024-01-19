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
 * AspectVisitor that creates an {@link Diagram} representation for the given Aspect Model.
 */
public class DiagramVisitor implements AspectVisitor<Diagram, Optional<Context>> {
   private final Locale locale;
   private final Map<ModelElement, Diagram.Box> seenElements = new HashMap<>();

   public DiagramVisitor( final Locale locale ) {
      this.locale = locale;
   }

   @Override
   public Diagram visitBase( final ModelElement modelElement, final Optional<Context> context ) {
      return Diagram.EMPTY;
   }

   @Override
   public Diagram visitStructureElement( final StructureElement element, final Optional<Context> context ) {
      final Diagram result = defaultBox( element, context.orElseThrow().prototype() );
      final Diagram.Box box = result.getFocusBox();
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
   public Diagram visitAspect( final Aspect aspect, final Optional<Context> context ) {
      if ( seenElements.containsKey( aspect ) ) {
         return new Diagram( seenElements.get( aspect ) );
      }

      final Diagram result = visitStructureElement( aspect, Optional.of( new Context( null, "Aspect" ) ) );
      final Diagram.Box box = result.getFocusBox();
      aspect.getEvents().stream().map( event -> childElementDiagram( box, event, "event" ) ).forEach( result::add );
      aspect.getOperations().stream().map( operation -> childElementDiagram( box, operation, "operation" ) ).forEach( result::add );
      return result;
   }

   @Override
   public Diagram visitEntity( final Entity entity, final Optional<Context> context ) {
      if ( seenElements.containsKey( entity ) ) {
         return new Diagram( seenElements.get( entity ) );
      }

      final Diagram result = visitStructureElement( entity,
            context.map( oldContext -> new Context( oldContext.parent(), "Entity" ) ) );
      final Diagram.Box box = result.getFocusBox();
      entity.getExtends().stream().map( superType -> childElementDiagram( box, superType, "extends" ) ).forEach( result::add );
      return result;
   }

   @Override
   public Diagram visitProperty( final Property property, final Optional<Context> context ) {
      if ( seenElements.containsKey( property ) ) {
         return new Diagram( seenElements.get( property ) );
      }

      final Diagram result = defaultBox( property, (property.isAbstract() ? "Abstract" : "") + "Property" );
      final Diagram.Box box = result.getFocusBox();
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
   public Diagram visitCharacteristic( final Characteristic characteristic, final Optional<Context> context ) {
      if ( seenElements.containsKey( characteristic ) ) {
         return new Diagram( seenElements.get( characteristic ) );
      }

      final Diagram result = defaultBox( characteristic, "Characteristic" );
      final Diagram.Box box = result.getFocusBox();
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
   public Diagram visitOperation( final Operation operation, final Optional<Context> context ) {
      if ( seenElements.containsKey( operation ) ) {
         return new Diagram( seenElements.get( operation ) );
      }

      final Diagram result = defaultBox( operation, "Operation" );
      final Diagram.Box box = result.getFocusBox();

      for ( final Property property : operation.getInput() ) {
         result.add( childElementDiagram( box, property, "input" ) );
      }
      operation.getOutput().ifPresent( output -> result.add( childElementDiagram( box, output, "output" ) ) );
      return result;
   }

   @Override
   public Diagram visitAbstractEntity( final AbstractEntity abstractEntity, final Optional<Context> context ) {
      if ( seenElements.containsKey( abstractEntity ) ) {
         return new Diagram( seenElements.get( abstractEntity ) );
      }

      final Diagram result =
            visitStructureElement( abstractEntity, context.map( oldContext -> new Context( oldContext.parent(), "AbstractEntity" ) ) );
      final Diagram.Box box = result.getFocusBox();
      abstractEntity.getExtends().stream().map( superType -> childElementDiagram( box, superType, "extends" ) ).forEach( result::add );
      return result;
   }

   @Override
   public Diagram visitEvent( final Event event, final Optional<Context> context ) {
      if ( seenElements.containsKey( event ) ) {
         return new Diagram( seenElements.get( event ) );
      }
      return visitStructureElement( event, context.map( oldContext -> new Context( oldContext.parent(), "Event" ) ) );
   }

   @Override
   public Diagram visitUnit( final Unit unit, final Optional<Context> context ) {
      if ( seenElements.containsKey( unit ) ) {
         return new Diagram( seenElements.get( unit ) );
      }

      final Diagram result = defaultBox( unit, "Unit" );
      final Diagram.Box box = result.getFocusBox();
      unit.getSymbol().ifPresent( symbol -> box.addEntry( attribute( "symbol", String.class, () -> symbol ) ) );
      unit.getReferenceUnit().ifPresent( referenceUnit ->
            box.addEntry( attribute( "referenceUnit", String.class, () -> referenceUnit ) ) );
      unit.getCode().ifPresent( code -> box.addEntry( attribute( "code", String.class, () -> code ) ) );
      unit.getConversionFactor().ifPresent( conversionFactor ->
            box.addEntry( attribute( "conversionFactor", String.class, () -> conversionFactor ) ) );
      return result;
   }

   @Override
   public Diagram visitTrait( final Trait trait, final Optional<Context> context ) {
      if ( seenElements.containsKey( trait ) ) {
         return new Diagram( seenElements.get( trait ) );
      }

      final Diagram result = defaultBox( trait, "Trait" );
      final Diagram.Box box = result.getFocusBox();
      result.add( childElementDiagram( box, trait.getBaseCharacteristic(), "baseCharacteristic" ) );
      for ( final Constraint constraint : trait.getConstraints() ) {
         result.add( childElementDiagram( box, constraint, "constraint" ) );
      }
      return result;
   }

   @Override
   public Diagram visitLengthConstraint( final LengthConstraint lengthConstraint, final Optional<Context> context ) {
      if ( seenElements.containsKey( lengthConstraint ) ) {
         return new Diagram( seenElements.get( lengthConstraint ) );
      }

      final Diagram result = defaultBox( lengthConstraint, "LengthConstraint" );
      final Diagram.Box box = result.getFocusBox();
      lengthConstraint.getMinValue().ifPresent( minValue -> box.addEntry( attribute( "minValue", BigInteger.class, () -> minValue ) ) );
      lengthConstraint.getMinValue().ifPresent( maxValue -> box.addEntry( attribute( "maxValue", BigInteger.class, () -> maxValue ) ) );
      return result;
   }

   @Override
   public Diagram visitRangeConstraint( final RangeConstraint rangeConstraint, final Optional<Context> context ) {
      if ( seenElements.containsKey( rangeConstraint ) ) {
         return new Diagram( seenElements.get( rangeConstraint ) );
      }

      final Diagram result = defaultBox( rangeConstraint, "RangeConstraint" );
      final Diagram.Box box = result.getFocusBox();
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
   public Diagram visitFixedPointConstraint( final FixedPointConstraint fixedPointConstraint, final Optional<Context> context ) {
      if ( seenElements.containsKey( fixedPointConstraint ) ) {
         return new Diagram( seenElements.get( fixedPointConstraint ) );
      }

      final Diagram result = defaultBox( fixedPointConstraint, "FixedPointConstraint" );
      final Diagram.Box box = result.getFocusBox();
      box.addEntry( attribute( "integer", Integer.class, fixedPointConstraint::getInteger ) );
      box.addEntry( attribute( "scale", Integer.class, fixedPointConstraint::getScale ) );
      return result;
   }

   @Override
   public Diagram visitEncodingConstraint( final EncodingConstraint encodingConstraint, final Optional<Context> context ) {
      if ( seenElements.containsKey( encodingConstraint ) ) {
         return new Diagram( seenElements.get( encodingConstraint ) );
      }

      final Diagram result = defaultBox( encodingConstraint, "EncodingConstraint" );
      final Diagram.Box box = result.getFocusBox();
      box.addEntry( attribute( "charset", Charset.class, encodingConstraint::getValue ) );
      return result;
   }

   @Override
   public Diagram visitLanguageConstraint( final LanguageConstraint languageConstraint, final Optional<Context> context ) {
      if ( seenElements.containsKey( languageConstraint ) ) {
         return new Diagram( seenElements.get( languageConstraint ) );
      }

      final Diagram result = defaultBox( languageConstraint, "LanguageConstraint" );
      final Diagram.Box box = result.getFocusBox();
      box.addEntry( attribute( "charset", String.class, () -> languageConstraint.getLanguageCode().toLanguageTag() ) );
      return result;
   }

   @Override
   public Diagram visitRegularExpressionConstraint( final RegularExpressionConstraint regularExpressionConstraint,
         final Optional<Context> context ) {
      if ( seenElements.containsKey( regularExpressionConstraint ) ) {
         return new Diagram( seenElements.get( regularExpressionConstraint ) );
      }

      final Diagram result = defaultBox( regularExpressionConstraint, "RegularExpressionConstraint" );
      final Diagram.Box box = result.getFocusBox();
      box.addEntry( attribute( "value", String.class, regularExpressionConstraint::getValue ) );
      return result;
   }

   @Override
   public Diagram visitQuantifiable( final Quantifiable quantifiable, final Optional<Context> context ) {
      if ( seenElements.containsKey( quantifiable ) ) {
         return new Diagram( seenElements.get( quantifiable ) );
      }

      final Diagram result = visitCharacteristic( quantifiable, context );
      final Diagram.Box box = result.getFocusBox();
      box.setPrototype( "Quantifiable" );
      quantifiable.getUnit().ifPresent( unit -> result.add( childElementDiagram( box, unit, "unit" ) ) );
      return result;
   }

   @Override
   public Diagram visitMeasurement( final Measurement measurement, final Optional<Context> context ) {
      if ( seenElements.containsKey( measurement ) ) {
         return new Diagram( seenElements.get( measurement ) );
      }

      final Diagram result = visitQuantifiable( measurement, context );
      result.getFocusBox().setPrototype( "Measurement" );
      return result;
   }

   @Override
   public Diagram visitCode( final Code code, final Optional<Context> context ) {
      if ( seenElements.containsKey( code ) ) {
         return new Diagram( seenElements.get( code ) );
      }

      final Diagram result = visitCharacteristic( (Characteristic) code, context );
      final Diagram.Box box = result.getFocusBox();
      box.setPrototype( "Code" );
      return result;
   }

   @Override
   public Diagram visitCollection( final Collection collection, final Optional<Context> context ) {
      if ( seenElements.containsKey( collection ) ) {
         return new Diagram( seenElements.get( collection ) );
      }

      final Diagram result;
      if ( collection.getElementCharacteristic().isPresent() ) {
         // If the collection has an elementCharacteristic, don't use visitCharacteristic to prevent additional dataType edge
         result = defaultBox( collection, "Collection" );
      } else {
         result = visitCharacteristic( (Characteristic) collection, context );
         result.getFocusBox().setPrototype( "Collection" );
      }

      final Diagram.Box box = result.getFocusBox();
      collection.getElementCharacteristic().ifPresent( elementCharacteristic ->
            result.add( childElementDiagram( box, elementCharacteristic, "elementCharacteristic" ) ) );
      return result;
   }

   @Override
   public Diagram visitList( final org.eclipse.esmf.characteristic.List list, final Optional<Context> context ) {
      if ( seenElements.containsKey( list ) ) {
         return new Diagram( seenElements.get( list ) );
      }

      final Diagram result = visitCollection( list, context );
      final Diagram.Box box = result.getFocusBox();
      box.setPrototype( "List" );
      return result;
   }

   @Override
   public Diagram visitSet( final Set set, final Optional<Context> context ) {
      if ( seenElements.containsKey( set ) ) {
         return new Diagram( seenElements.get( set ) );
      }

      final Diagram result = visitCollection( set, context );
      final Diagram.Box box = result.getFocusBox();
      box.setPrototype( "Set" );
      return result;
   }

   @Override
   public Diagram visitSortedSet( final SortedSet sortedSet, final Optional<Context> context ) {
      if ( seenElements.containsKey( sortedSet ) ) {
         return new Diagram( seenElements.get( sortedSet ) );
      }

      final Diagram result = visitCollection( sortedSet, context );
      final Diagram.Box box = result.getFocusBox();
      box.setPrototype( "SortedSet" );
      return result;
   }

   @Override
   public Diagram visitDuration( final Duration duration, final Optional<Context> context ) {
      if ( seenElements.containsKey( duration ) ) {
         return new Diagram( seenElements.get( duration ) );
      }

      final Diagram result = visitQuantifiable( duration, context );
      final Diagram.Box box = result.getFocusBox();
      box.setPrototype( "Duration" );
      return result;
   }

   @Override
   public Diagram visitEnumeration( final Enumeration enumeration, final Optional<Context> context ) {
      if ( seenElements.containsKey( enumeration ) ) {
         return new Diagram( seenElements.get( enumeration ) );
      }

      final Diagram result = visitCharacteristic( enumeration, context );
      final Diagram.Box box = result.getFocusBox();
      box.setPrototype( "Enumeration" );
      final Diagram collectionDiagram = processValuesCollection( box, enumeration.getValues(), "value" );
      if ( collectionDiagram.getScalarValue() == null ) {
         result.add( collectionDiagram );
      } else {
         box.addEntry( attribute( "values", String.class, () -> String.join( ", ", collectionDiagram.getScalarValue() ) ) );
      }
      return result;
   }

   @Override
   public Diagram visitEither( final Either either, final Optional<Context> context ) {
      final Diagram result = defaultBox( either, "Either" );
      final Diagram.Box box = result.getFocusBox();
      result.add( childElementDiagram( box, either.getLeft(), "left" ) );
      result.add( childElementDiagram( box, either.getRight(), "right" ) );
      return result;
   }

   @Override
   public Diagram visitSingleEntity( final SingleEntity singleEntity, final Optional<Context> context ) {
      if ( seenElements.containsKey( singleEntity ) ) {
         return new Diagram( seenElements.get( singleEntity ) );
      }

      final Diagram result = visitCharacteristic( (Characteristic) singleEntity, context );
      final Diagram.Box box = result.getFocusBox();
      box.setPrototype( "SingleEntity" );
      return result;
   }

   @Override
   public Diagram visitState( final State state, final Optional<Context> context ) {
      final Diagram result = visitEnumeration( state, context );
      final Diagram.Box box = result.getFocusBox();
      box.setPrototype( "State" );
      final Diagram valueDiagram = state.getDefaultValue().accept( this, Optional.of( new Context( box ) ) );
      result.add( valueDiagram );
      if ( valueDiagram.getScalarValue() != null ) {
         box.addEntry( attribute( "defaultValue", String.class, valueDiagram::getScalarValue ) );
      }
      if ( valueDiagram.getFocusBox() != null ) {
         result.addEdge( new Diagram.Edge( box, valueDiagram.getFocusBox(), "defaultValue" ) );
      }
      return result;
   }

   @Override
   public Diagram visitStructuredValue( final StructuredValue structuredValue, final Optional<Context> context ) {
      final Diagram result = visitCharacteristic( (Characteristic) structuredValue, context );
      final Diagram.Box box = result.getFocusBox();
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
   public Diagram visitTimeSeries( final TimeSeries timeSeries, final Optional<Context> context ) {
      final Diagram result = visitSortedSet( timeSeries, context );
      final Diagram.Box box = result.getFocusBox();
      box.setPrototype( "TimeSeries" );
      return result;
   }

   @Override
   public Diagram visitScalarValue( final ScalarValue value, final Optional<Context> context ) {
      final Diagram result = new Diagram( null );
      if ( value.getValue() instanceof final LangString langString ) {
         result.setScalarValue( "\"%s\"@%s".formatted( langString.getValue(), langString.getLanguageTag() ) );
      } else {
         result.setScalarValue( value.getValue().toString() );
      }
      return result;
   }

   @Override
   public Diagram visitCollectionValue( final CollectionValue collection, final Optional<Context> context ) {
      return context.map( parentContext -> processValuesCollection( parentContext.parent(), collection.getValues(),
            Optional.ofNullable( parentContext.edgeLabel() ).orElse( "value" ) ) ).orElse( Diagram.EMPTY );
   }

   @Override
   public Diagram visitEntityInstance( final EntityInstance instance, final Optional<Context> context ) {
      if ( seenElements.containsKey( instance ) ) {
         return new Diagram( seenElements.get( instance ) );
      }

      final Diagram result = defaultBox( instance, instance.getEntityType().getName() );
      final Diagram.Box box = result.getFocusBox();
      result.add( childElementDiagram( box, instance.getEntityType(), "type" ) );
      for ( final Map.Entry<Property, Value> assertion : instance.getAssertions().entrySet() ) {
         final Property property = assertion.getKey();
         final String propertyName = property.getName()
               + (property.getPayloadName().equals( property.getName() ) ? "" : " (%s)".formatted( property.getPayloadName() ));
         final Diagram valueDiagram = assertion.getValue().accept( this, Optional.of( new Context( box, "", propertyName ) ) );
         if ( valueDiagram.getScalarValue() == null ) {
            // If the value's diagram representation's scalar value is not set, the value is itself a box
            result.add( valueDiagram );
            // If the value is a collection, it doesn't have a focus box
            if ( valueDiagram.getFocusBox() != null ) {
               result.addEdge( new Diagram.Edge( box, valueDiagram.getFocusBox(), propertyName ) );
            }
         } else {
            // If the value's diagram representation's scalar value is set, use it for an attribute entry in the entity instance's box
            box.addEntry( attribute( propertyName, String.class, valueDiagram::getScalarValue ) );
         }
      }

      return result;
   }

   private <T extends java.util.Collection<Value>> Diagram processValuesCollection( final Diagram.Box parentBox, final T collection,
         final String edgeLabel ) {
      final Diagram result = new Diagram( null );
      boolean hasScalarValues = false;
      final List<String> scalarValues = new ArrayList<>();
      for ( final Value value : collection ) {
         final Diagram valueDiagram = value.accept( this, Optional.of( new Context( parentBox ) ) );
         if ( valueDiagram.getScalarValue() == null ) {
            result.add( valueDiagram );
            result.addEdge( new Diagram.Edge( parentBox, valueDiagram.getFocusBox(), edgeLabel ) );
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

   private Diagram childElementDiagram( final Diagram.Box parent, final ModelElement child, final String edgeLabel ) {
      final Optional<Context> childElementContext = Optional.of( new Context( parent ) );
      final Diagram result = child.accept( this, childElementContext );
      result.addEdge( new Diagram.Edge( parent, result.getFocusBox(), edgeLabel ) );
      return result;
   }

   private Diagram defaultBox( final NamedElement element, final String prototype ) {
      final Diagram.Box box = new Diagram.Box( prototype, element.getAspectModelUrn().isPresent() ? element.getName() : "" );
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
      return new Diagram( box );
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
