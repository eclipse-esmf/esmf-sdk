/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.parquet;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.StructureElement;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.characteristic.Either;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.characteristic.State;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.constraint.LengthConstraint;
import org.eclipse.esmf.metamodel.datatype.Curie;
import org.eclipse.esmf.metamodel.impl.DefaultProperty;

/**
 * Generates Parquet payload data using the visitor pattern.
 *
 * <p>
 * Each {@code visit*} method handles a specific model element type and returns a
 * {@code Map<String, Object>} representing the property names as keys and their corresponding
 * values. This follows the same structural approach as
 * {@link org.eclipse.esmf.aspectmodel.generator.json.JsonPayloadGenerator}.
 * </p>
 *
 * @see org.eclipse.esmf.aspectmodel.generator.json.JsonPayloadGenerator
 */
class ParquetPayloadGenerator implements AspectVisitor<Map<String, Object>, ParquetPayloadGenerator.Context> {

   /**
    * Constant used as the type discriminator key in payloads with entity inheritance.
    */
   static final String TYPE = "@type";

   private static final String COULD_NOT_PROCESS_PROPERTY = "Could not process Property ";

   /**
    * Constant for the either left property.
    * For example Parquet payloads the left type will be used.
    */
   private static final String EITHER_LEFT = "left";

   private final List<Property> recursiveProperty = new LinkedList<>();
   private final ParquetExampleValueGenerator exampleValueGenerator;
   private final boolean addTypeAttributeForEntityInheritance;

   /**
    * Traversal context carrying state needed during visitor traversal.
    *
    * @param constraints the constraints that apply at the current position
    * @param visitedProperties the properties visited during traversal to detect cycles
    * @param ignoreExampleValue whether to ignore the exampleValue for subsequent levels
    */
   record Context(
         List<Constraint> constraints,
         Set<Property> visitedProperties,
         boolean ignoreExampleValue
   ) {
      Context() {
         this( List.of(), new HashSet<>(), false );
      }

      Context withConstraints( final List<Constraint> constraints ) {
         return new Context( constraints, visitedProperties(), ignoreExampleValue() );
      }

      Context doIgnoreExampleValue( final boolean ignore ) {
         return new Context( constraints(), visitedProperties(), ignore );
      }
   }

   ParquetPayloadGenerator( final ParquetExampleValueGenerator exampleValueGenerator,
         final boolean addTypeAttributeForEntityInheritance ) {
      this.exampleValueGenerator = exampleValueGenerator;
      this.addTypeAttributeForEntityInheritance = addTypeAttributeForEntityInheritance;
   }

   @Override
   public Map<String, Object> visitBase( final ModelElement modelElement, final Context context ) {
      throw new UnsupportedOperationException( "Cannot generate payload for " + modelElement );
   }

   @Override
   public Map<String, Object> visitAspect( final Aspect aspect, final Context context ) {
      return visitStructureElement( aspect, context );
   }

   @Override
   public Map<String, Object> visitStructureElement( final StructureElement structureElement, final Context context ) {
      return transformProperties( structureElement.getProperties(), !context.ignoreExampleValue() );
   }

   @Override
   public Map<String, Object> visitEntity( final Entity entity, final Context context ) {
      final Map<String, Object> generatedProperties = transformProperties( entity.getAllProperties(),
            !context.ignoreExampleValue() );
      if ( entity.getExtends().isPresent() && addTypeAttributeForEntityInheritance ) {
         generatedProperties.put( TYPE, entity.getName() );
      }
      return generatedProperties;
   }

   @Override
   public Map<String, Object> visitAbstractEntity( final AbstractEntity abstractEntity, final Context context ) {
      final ComplexType extendingComplexType = abstractEntity.getExtendingElements().get( 0 );
      final Map<String, Object> generatedProperties = transformProperties( extendingComplexType.getAllProperties(),
            !context.ignoreExampleValue() );
      if ( addTypeAttributeForEntityInheritance ) {
         generatedProperties.put( TYPE, extendingComplexType.getName() );
      }
      return generatedProperties;
   }

   @Override
   public Map<String, Object> visitProperty( final Property property, final Context context ) {
      if ( property.isNotInPayload() ) {
         return Map.of();
      }
      // Handle recursive properties
      if ( property.isOptional() && context.visitedProperties().contains( property ) ) {
         return Map.of();
      }
      if ( recursiveProperty.contains( property ) ) {
         return recursiveProperty( property );
      }
      context.visitedProperties().add( property );

      recursiveProperty.add( property );
      try {
         return transformPropertyValue( property, !context.ignoreExampleValue() );
      } finally {
         recursiveProperty.remove( property );
      }
   }

   @Override
   public Map<String, Object> visitCharacteristic( final Characteristic characteristic, final Context context ) {
      return characteristic.getDataType()
            .map( t -> t.accept( this, context ) )
            .orElse( Map.of() );
   }

   @Override
   public Map<String, Object> visitTrait( final Trait trait, final Context context ) {
      return trait.getBaseCharacteristic().accept( this, context.withConstraints( trait.getConstraints() ) );
   }

   @Override
   public Map<String, Object> visitCollection( final Collection collection, final Context context ) {
      // Delegate to getCollectionValues logic — this is called from transformCollectionProperty
      return Map.of();
   }

   @Override
   public Map<String, Object> visitEither( final Either either, final Context context ) {
      return either.getLeft().accept( this, context );
   }

   @Override
   public Map<String, Object> visitEnumeration( final Enumeration enumeration, final Context context ) {
      final Value firstValue = enumeration.getValues().getFirst();
      final Object value = firstValue instanceof final ScalarValue scalarValue
            ? scalarValue.getValue()
            : firstValue.toString();
      return Map.of( "_value_", value );
   }

   @Override
   public Map<String, Object> visitState( final State state, final Context context ) {
      return Map.of( "_value_", state.getDefaultValue() );
   }

   /**
    * Transforms a list of properties into a combined key-value map.
    *
    * @param properties the meta model properties to transform
    * @param useModelExampleValue whether to use the model's example value if present
    * @return a map representing the given properties
    */
   @SuppressWarnings( "squid:S2250" )
   // Amount of elements in list is in regard to amount of properties in Aspect Model. Even in bigger
   // aspects this
   // should not lead to performance issues
   Map<String, Object> transformProperties( final List<Property> properties, final boolean useModelExampleValue ) {
      return Stream.concat(
            properties.stream().filter( recursiveProperty::contains ).map( this::recursiveProperty ),
            properties.stream()
                  .filter( property -> !recursiveProperty.contains( property ) )
                  .filter( property -> !property.isAbstract() )
                  .map( property -> {
                     recursiveProperty.add( property );
                     final Map<String, Object> result = transformPropertyValue( property, useModelExampleValue );
                     recursiveProperty.remove( property );
                     return result;
                  } )
      ).collect( HashMap::new, HashMap::putAll, HashMap::putAll );
   }

   private Map<String, Object> recursiveProperty( final Property property ) {
      if ( !property.isNotInPayload() && !property.isOptional() ) {
         throw new IllegalArgumentException(
               String.format( "Having a recursive Property: %s which is not Optional nor marked as NotInPayload is not valid.",
                     property ) );
      }
      return Map.of();
   }

   /**
    * Transforms a single property into a key-value map entry by dispatching to the appropriate
    * handler based on the property's characteristic type.
    *
    * <p>
    * A {@link Collection} will be represented as a {@link java.util.List}.
    * {@link Entity} and {@link Either} will be represented as {@link Map}.
    * </p>
    *
    * @param property the property to transform
    * @param useModelExampleValue whether to use the model's example value if present
    * @return a map representing the property name as key and the property value as value
    */
   private Map<String, Object> transformPropertyValue( final Property property, final boolean useModelExampleValue ) {
      final Characteristic characteristic =
            property.getCharacteristic().orElseThrow( () -> new IllegalArgumentException( COULD_NOT_PROCESS_PROPERTY + property ) );

      // Collection handling (with or without Trait wrapper)
      if ( characteristic.is( Collection.class ) ) {
         final List<Object> collectionValues = getCollectionValues( property, (Collection) characteristic );
         return toMap( property.getPayloadName(), collectionValues );
      }
      if ( isConstrainedCollection( characteristic ) ) {
         final Collection collection = characteristic.as( Trait.class ).getBaseCharacteristic().as( Collection.class );
         final List<Constraint> constraints = characteristic.as( Trait.class ).getConstraints().stream()
               .filter( trait -> trait.is( LengthConstraint.class ) )
               .toList();
         return !constraints.isEmpty()
               ? toMap( property.getPayloadName(), getCollectionValues( property, collection,
                     (LengthConstraint) characteristic.as( Trait.class ).getConstraints().get( 0 ) ) )
               : toMap( property.getPayloadName(), getCollectionValues( property, collection ) );
      }

      // Enumeration handling
      final Optional<Enumeration> enumeration = getForCharacteristic( characteristic, Enumeration.class );
      if ( enumeration.isPresent() ) {
         return extractEnumerationValues( property, enumeration.get() );
      }

      // AbstractEntity handling
      final Optional<AbstractEntity> abstractEntity = getForCharacteristic( characteristic, AbstractEntity.class );
      if ( abstractEntity.isPresent() ) {
         final Map<String, Object> entityResult = visitAbstractEntity( abstractEntity.get(), new Context() );
         return toMap( property.getPayloadName(), entityResult );
      }

      // Entity handling
      final Optional<Entity> entity = getForCharacteristic( characteristic, Entity.class );
      if ( entity.isPresent() ) {
         final Map<String, Object> entityResult = visitEntity( entity.get(),
               new Context().doIgnoreExampleValue( !useModelExampleValue ) );
         return toMap( property.getPayloadName(), entityResult );
      }

      // Either handling
      final Optional<Either> either = getForCharacteristic( characteristic, Either.class );
      if ( either.isPresent() ) {
         return transformEitherProperty( property, either.get(), useModelExampleValue );
      }

      // Simple property (scalar)
      return toMap( property.getPayloadName(), getExampleValueOrElseRandom( property, useModelExampleValue ) );
   }

   private Map<String, Object> extractEnumerationValues( final Property property, final Enumeration enumeration ) {
      final Value firstValue = enumeration.getValues().getFirst();
      final Object value = firstValue instanceof final ScalarValue scalarValue
            ? scalarValue.getValue()
            : firstValue.toString();
      return toMap( property.getPayloadName(), value );
   }

   private Map<String, Object> transformEitherProperty( final Property property, final Either either,
         final boolean useModelExampleValue ) {
      final Map<String, Object> leftResult = transformPropertyValue(
            new DefaultProperty( MetaModelBaseAttributes.builder().build(),
                  Optional.of( either.getLeft() ), Optional.empty(), false, false,
                  Optional.of( EITHER_LEFT ), false, Optional.empty() ),
            useModelExampleValue );
      return toMap( property.getPayloadName(), leftResult );
   }

   boolean isConstrainedCollection( final Characteristic characteristic ) {
      if ( !characteristic.is( Trait.class ) ) {
         return false;
      }
      final Trait trait = characteristic.as( Trait.class );
      return trait.getBaseCharacteristic().is( Collection.class ) && ( !trait.getConstraints().isEmpty() );
   }

   /**
    * Returns the example value from the property or generates a random one.
    *
    * @param property the property to get the value for
    * @param useModelExampleValue whether to use the model's example value if present
    * @return the example value or a random value
    */
   Object getExampleValueOrElseRandom( final Property property, final boolean useModelExampleValue ) {
      final Characteristic characteristic =
            property.getCharacteristic().orElseThrow( () -> new IllegalArgumentException( COULD_NOT_PROCESS_PROPERTY + property ) );
      if ( characteristic.is( State.class ) ) {
         return characteristic.as( State.class ).getDefaultValue();
      }
      if ( characteristic.is( Enumeration.class ) ) {
         return characteristic.as( Enumeration.class ).getValues().get( 0 );
      }

      Optional<Characteristic> elementCharacteristics = Optional.empty();
      if ( characteristic.is( Collection.class ) ) {
         elementCharacteristics = ( (Collection) characteristic ).getElementCharacteristic();
      }
      final Characteristic effectiveCharacteristics = elementCharacteristics.orElse( characteristic );

      if ( !useModelExampleValue ) {
         return generateExampleValue( effectiveCharacteristics );
      }

      return property.getExampleValue()
            .map( exampleValue -> exampleValue.as( ScalarValue.class ).getValue() )
            .map( value -> value instanceof Curie( final String curieValue ) ? curieValue : value )
            .orElseGet( () -> generateExampleValue( effectiveCharacteristics ) );
   }

   private static Map<String, Object> toMap( final String key, final Object value ) {
      final Map<String, Object> result = new HashMap<>();
      result.put( key, value );
      return result;
   }

   private List<Object> getCollectionValues( final Property property, final Collection collection ) {
      return getCollectionValues( property, collection, null );
   }

   private List<Object> getCollectionValues( final Property property, final Collection collection,
         final LengthConstraint lengthConstraint ) {
      final Type dataType = collection.getDataType()
            .orElseThrow( () -> new IllegalArgumentException( "DataType for collection is required." ) );

      if ( dataType.is( Scalar.class ) ) {
         final Object payload = getExampleValueOrElseRandom( property, lengthConstraint == null );
         return payload instanceof final List list ? list : List.of( payload );
      }

      final BigInteger minLength = lengthConstraint == null ? BigInteger.ONE : lengthConstraint.getMinValue().orElse( BigInteger.ONE );
      final List<Object> returnValues = new ArrayList<>();
      for ( int i = 0; i < minLength.intValue(); i++ ) {
         returnValues.add( generateCollectionValue( dataType, minLength.intValue() ) );
      }
      return returnValues;
   }

   private Object generateCollectionValue( final Type dataType, final int minCount ) {
      if ( dataType.is( AbstractEntity.class ) ) {
         return visitAbstractEntity( dataType.as( AbstractEntity.class ),
               new Context().doIgnoreExampleValue( minCount >= 2 ) );
      }
      if ( dataType.is( Entity.class ) ) {
         return transformProperties( dataType.as( Entity.class ).getAllProperties(), minCount < 2 );
      }
      throw new IllegalArgumentException( String.format( "DataType %s is unknown", dataType ) );
   }

   private <T> Optional<T> getForCharacteristic( final Characteristic characteristic, final Class<T> type ) {
      if ( type.isAssignableFrom( characteristic.getClass() ) ) {
         return Optional.of( type.cast( characteristic ) );
      }
      return characteristic.getDataType()
            .filter( dataType -> type.isAssignableFrom( dataType.getClass() ) )
            .map( type::cast );
   }

   private Object generateExampleValue( final Characteristic characteristic ) {
      return characteristic.accept( exampleValueGenerator, new ParquetExampleValueGenerator.Context() );
   }
}
