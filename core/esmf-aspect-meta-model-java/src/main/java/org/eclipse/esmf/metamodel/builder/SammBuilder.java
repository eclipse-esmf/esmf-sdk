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

package org.eclipse.esmf.metamodel.builder;

import static org.eclipse.esmf.metamodel.DataTypes.dataTypeByUri;
import static org.eclipse.esmf.metamodel.DataTypes.xsd;
import static org.eclipse.esmf.metamodel.Elements.*;

import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ValueInstantiator;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.BoundDefinition;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.QuantityKinds;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.characteristic.Code;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.characteristic.Duration;
import org.eclipse.esmf.metamodel.characteristic.Either;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.characteristic.List;
import org.eclipse.esmf.metamodel.characteristic.Measurement;
import org.eclipse.esmf.metamodel.characteristic.Quantifiable;
import org.eclipse.esmf.metamodel.characteristic.Set;
import org.eclipse.esmf.metamodel.characteristic.SingleEntity;
import org.eclipse.esmf.metamodel.characteristic.SortedSet;
import org.eclipse.esmf.metamodel.characteristic.State;
import org.eclipse.esmf.metamodel.characteristic.StructuredValue;
import org.eclipse.esmf.metamodel.characteristic.TimeSeries;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultCode;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultCollection;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultDuration;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultEither;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultEnumeration;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultList;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultMeasurement;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultQuantifiable;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultSet;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultSingleEntity;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultSortedSet;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultState;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultStructuredValue;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultTimeSeries;
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
import org.eclipse.esmf.metamodel.datatype.LangString;
import org.eclipse.esmf.metamodel.impl.DefaultAspect;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.metamodel.impl.DefaultEntity;
import org.eclipse.esmf.metamodel.impl.DefaultEntityInstance;
import org.eclipse.esmf.metamodel.impl.DefaultProperty;
import org.eclipse.esmf.metamodel.impl.DefaultScalarValue;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

/**
 * Builder for SAMM elements.
 */
@SuppressWarnings( { "NewClassNamingConvention", "checkstyle:ClassTypeParameterName", "checkstyle:MethodTypeParameterName" } )
public class SammBuilder {
   /**
    * Base class for all builders.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   protected abstract static class Builder<SELF extends Builder<SELF, ACTUAL>, ACTUAL> {
      protected final SELF myself;
      protected final java.util.Set<LangString> preferredNames = new HashSet<>();
      protected final java.util.Set<LangString> descriptions = new HashSet<>();
      protected final java.util.List<String> see = new ArrayList<>();

      @SuppressWarnings( "unchecked" )
      public Builder( final Class<?> selfType ) {
         myself = (SELF) selfType.cast( this );
      }

      public SELF preferredName( final String preferredName ) {
         return preferredName( preferredName, Locale.ENGLISH );
      }

      public SELF preferredName( final String preferredName, final Locale language ) {
         return preferredName( new LangString( preferredName, language ) );
      }

      public SELF preferredName( final LangString preferredName ) {
         preferredNames.add( preferredName );
         return myself;
      }

      public SELF description( final String description ) {
         return description( description, Locale.ENGLISH );
      }

      public SELF description( final String description, final Locale language ) {
         return description( new LangString( description, language ) );
      }

      public SELF description( final LangString description ) {
         descriptions.add( description );
         return myself;
      }

      public SELF see( final URI uri ) {
         return see( uri.toASCIIString() );
      }

      public SELF see( final String uri ) {
         see.add( uri );
         return myself;
      }

      public abstract ACTUAL build();

      protected abstract MetaModelBaseAttributes baseAttributes();
   }

   /**
    * Base class for all model elements that are optionally name, e.g., Characteristics and Constraints.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   protected abstract static class OptionallyNamedElementBuilder<SELF extends OptionallyNamedElementBuilder<SELF, ACTUAL>,
         ACTUAL extends ModelElement> extends Builder<SELF, ACTUAL> {
      protected final AspectModelUrn urn;

      public OptionallyNamedElementBuilder( final Class<?> selfType ) {
         super( selfType );
         urn = null;
      }

      public OptionallyNamedElementBuilder( final Class<?> selfType, final AspectModelUrn urn ) {
         super( selfType );
         this.urn = urn;
      }

      @Override
      protected MetaModelBaseAttributes baseAttributes() {
         final MetaModelBaseAttributes.Builder builder = urn == null
               ? MetaModelBaseAttributes.builder()
               : MetaModelBaseAttributes.builder().withUrn( urn );
         return builder
               .withDescriptions( descriptions )
               .withPreferredNames( preferredNames )
               .withSee( see )
               .build();
      }
   }

   /**
    * Base class for all builders that have mandatory names, e.g., Aspect and Property.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   protected abstract static class NamedElementBuilder<SELF extends NamedElementBuilder<SELF, ACTUAL>,
         ACTUAL extends ModelElement> extends Builder<SELF, ACTUAL> {
      protected final AspectModelUrn urn;

      public NamedElementBuilder( final Class<?> selfType, final AspectModelUrn urn ) {
         super( selfType );
         this.urn = urn;
      }

      @Override
      protected MetaModelBaseAttributes baseAttributes() {
         return MetaModelBaseAttributes.builder()
               .withUrn( urn )
               .withDescriptions( descriptions )
               .withPreferredNames( preferredNames )
               .withSee( see )
               .build();
      }
   }

   /**
    * Builder for {@link Aspect}
    *
    * @param <SELF> the self type
    */
   public static class AspectBuilder<SELF extends AspectBuilder<SELF>> extends NamedElementBuilder<SELF, Aspect> {
      private final java.util.List<Property> properties = new ArrayList<>();
      private final java.util.List<Operation> operations = new ArrayList<>();
      private final java.util.List<Event> events = new ArrayList<>();

      public AspectBuilder( final AspectModelUrn urn ) {
         super( AspectBuilder.class, urn );
      }

      public SELF property( final Property property ) {
         properties.add( property );
         return myself;
      }

      public SELF operation( final Operation operation ) {
         operations.add( operation );
         return myself;
      }

      public SELF event( final Event event ) {
         events.add( event );
         return myself;
      }

      @Override
      public Aspect build() {
         return new DefaultAspect( baseAttributes(), properties, operations, events );
      }
   }

   /**
    * Builder for {@link Entity}
    *
    * @param <SELF> the self type
    */
   public static class EntityBuilder<SELF extends EntityBuilder<SELF>> extends NamedElementBuilder<SELF, Entity> {
      private final java.util.List<Property> properties = new ArrayList<>();
      private ComplexType superType;

      public EntityBuilder( final AspectModelUrn urn ) {
         super( EntityBuilder.class, urn );
      }

      public SELF property( final Property property ) {
         properties.add( property );
         return myself;
      }

      @SuppressWarnings( "NewMethodNamingConvention" )
      public SELF extends_( final ComplexType superType ) {
         this.superType = superType;
         return myself;
      }

      @Override
      public Entity build() {
         return new DefaultEntity( baseAttributes(), properties, Optional.ofNullable( superType ), java.util.List.of(), null );
      }
   }

   /**
    * Builder for {@link EntityInstance}
    *
    * @param <SELF> the self type
    */
   public static class EntityInstanceBuilder<SELF extends EntityInstanceBuilder<SELF>> extends NamedElementBuilder<SELF, EntityInstance> {
      private Entity dataType;
      private final Map<Property, Value> assertions = new HashMap<>();

      public EntityInstanceBuilder( final AspectModelUrn urn ) {
         super( EntityInstanceBuilder.class, urn );
      }

      public SELF dataType( final Entity type ) {
         dataType = type;
         return myself;
      }

      @Override
      public SELF preferredName( final String preferredName ) {
         throw new AspectModelBuildingException( "Entity instance may not have a preferredName" );
      }

      @Override
      public SELF preferredName( final String preferredName, final Locale language ) {
         throw new AspectModelBuildingException( "Entity instance may not have a preferredName" );
      }

      @Override
      public SELF preferredName( final LangString preferredName ) {
         throw new AspectModelBuildingException( "Entity instance may not have a preferredName" );
      }

      @Override
      public SELF description( final String description ) {
         throw new AspectModelBuildingException( "Entity instance may not have a description" );
      }

      @Override
      public SELF description( final String description, final Locale language ) {
         throw new AspectModelBuildingException( "Entity instance may not have a description" );
      }

      @Override
      public SELF description( final LangString description ) {
         throw new AspectModelBuildingException( "Entity instance may not have a description" );
      }

      @Override
      public SELF see( final URI uri ) {
         throw new AspectModelBuildingException( "Entity instance may not have a see reference" );
      }

      @Override
      public SELF see( final String uri ) {
         throw new AspectModelBuildingException( "Entity instance may not have a see reference" );
      }

      public SELF propertyAssertion( final Property property, final Value value ) {
         if ( dataType == null ) {
            throw new AspectModelBuildingException( "EntityInstance is missing a type" );
         }
         if ( !dataType.getAllProperties().contains( property ) ) {
            throw new AspectModelBuildingException( "Property " + property + " is not part of Entity type " + dataType );
         }
         assertions.put( property, value );
         return myself;
      }

      @Override
      public EntityInstance build() {
         return new DefaultEntityInstance( baseAttributes(), assertions, dataType );
      }
   }

   /**
    * Builder for {@link Property}
    *
    * @param <SELF> the self type
    */
   public static class PropertyBuilder<SELF extends PropertyBuilder<SELF>> extends NamedElementBuilder<SELF, Property> {
      private Characteristic characteristic;
      private ScalarValue exampleValue;
      private boolean optional;
      private boolean notInPayload;
      private String payloadName;
      private boolean isAbstract;
      @SuppressWarnings( { "checkstyle:MemberName", "FieldNamingConvention" } )
      private Property extends_;

      public PropertyBuilder( final AspectModelUrn urn ) {
         super( PropertyBuilder.class, urn );
      }

      public SELF characteristic( final Characteristic characteristic ) {
         this.characteristic = characteristic;
         return myself;
      }

      public SELF exampleValue( final ScalarValue exampleValue ) {
         if ( characteristic == null ) {
            throw new AspectModelBuildingException( "Property is missing a characteristic" );
         }
         final Type type = characteristic.getDataType().orElseThrow( () ->
               new AspectModelBuildingException( "Property's characteristic is missing a dataType" ) );
         if ( !exampleValue.getType().isTypeOrSubtypeOf( type ) ) {
            throw new AspectModelBuildingException( "Property's example value's type is not and no subtype of " + type );
         }
         this.exampleValue = exampleValue;
         return myself;
      }

      public SELF optional() {
         optional = true;
         return myself;
      }

      public SELF optional( final boolean optional ) {
         this.optional = optional;
         return myself;
      }

      public SELF notInPayload() {
         notInPayload = true;
         return myself;
      }

      public SELF notInPayload( final boolean notInPayload ) {
         this.notInPayload = notInPayload;
         return myself;
      }

      public SELF payloadName( final String payloadName ) {
         this.payloadName = payloadName;
         return myself;
      }

      public SELF isAbstract() {
         isAbstract = true;
         return myself;
      }

      public SELF isAbstract( final boolean isAbstract ) {
         this.isAbstract = isAbstract;
         return myself;
      }

      @SuppressWarnings( { "NewMethodNamingConvention", "checkstyle:MemberName" } )
      public SELF extends_(
            @SuppressWarnings( { "MethodParameterNamingConvention", "checkstyle:ParameterName" } ) final Property extends_ ) {
         this.extends_ = extends_;
         return myself;
      }

      @Override
      public Property build() {
         return new DefaultProperty(
               baseAttributes(),
               Optional.ofNullable( characteristic ),
               Optional.ofNullable( exampleValue ),
               optional,
               notInPayload,
               Optional.ofNullable( payloadName ),
               isAbstract,
               Optional.ofNullable( extends_ ) );
      }
   }

   /**
    * Builder for {@link Characteristic}
    *
    * @param <SELF> the self type
    */
   public static class CharacteristicBuilder<SELF extends CharacteristicBuilder<SELF>>
         extends OptionallyNamedElementBuilder<SELF, Characteristic> {
      private Type dataType;

      public CharacteristicBuilder( final AspectModelUrn urn ) {
         super( CharacteristicBuilder.class, urn );
      }

      public CharacteristicBuilder() {
         super( CharacteristicBuilder.class );
      }

      public SELF dataType( final Type type ) {
         dataType = type;
         return myself;
      }

      @Override
      public Characteristic build() {
         return new DefaultCharacteristic( baseAttributes(), Optional.ofNullable( dataType ) );
      }
   }

   /**
    * Builder for {@link Trait}
    *
    * @param <SELF> the self type
    */
   public static class TraitBuilder<SELF extends TraitBuilder<SELF>> extends OptionallyNamedElementBuilder<SELF, Trait> {
      private final java.util.List<Constraint> constraints = new ArrayList<>();
      private Characteristic baseCharacteristic;

      public TraitBuilder() {
         super( TraitBuilder.class );
      }

      public TraitBuilder( final AspectModelUrn urn ) {
         super( TraitBuilder.class, urn );
      }

      public SELF baseCharacteristic( final Characteristic baseCharacteristic ) {
         this.baseCharacteristic = baseCharacteristic;
         return myself;
      }

      public SELF constraint( final Constraint constraint ) {
         constraints.add( constraint );
         return myself;
      }

      @Override
      public Trait build() {
         if ( baseCharacteristic == null ) {
            throw new AspectModelBuildingException( "Trait must have a baseCharacteristic" );
         }
         return new DefaultTrait( baseAttributes(), baseCharacteristic, constraints );
      }
   }

   /**
    * Builder for {@link Quantifiable}
    *
    * @param <SELF> the self type
    */
   public static class QuantifiableBuilder<SELF extends QuantifiableBuilder<SELF>>
         extends OptionallyNamedElementBuilder<SELF, Quantifiable> {
      private Unit unit;
      private Type dataType;

      public QuantifiableBuilder() {
         super( QuantifiableBuilder.class );
      }

      public QuantifiableBuilder( final AspectModelUrn urn ) {
         super( QuantifiableBuilder.class, urn );
      }

      public SELF unit( final Unit unit ) {
         this.unit = unit;
         return myself;
      }

      public SELF dataType( final Type type ) {
         dataType = type;
         return myself;
      }

      @Override
      public Quantifiable build() {
         return new DefaultQuantifiable( baseAttributes(), dataType, Optional.ofNullable( unit ) );
      }
   }

   /**
    * Builder for {@link Measurement}
    *
    * @param <SELF> the self type
    */
   public static class MeasurementBuilder<SELF extends MeasurementBuilder<SELF>> extends OptionallyNamedElementBuilder<SELF, Measurement> {
      private Unit unit;
      private Type type;

      public MeasurementBuilder() {
         super( MeasurementBuilder.class );
      }

      public MeasurementBuilder( final AspectModelUrn urn ) {
         super( MeasurementBuilder.class, urn );
      }

      public SELF unit( final Unit unit ) {
         this.unit = unit;
         return myself;
      }

      public SELF dataType( final Type type ) {
         this.type = type;
         return myself;
      }

      @Override
      public Measurement build() {
         if ( unit == null ) {
            throw new AspectModelBuildingException( "Measurement must have a unit" );
         }
         return new DefaultMeasurement( baseAttributes(), type, Optional.ofNullable( unit ) );
      }
   }

   /**
    * Builder for {@link Enumeration}
    *
    * @param <SELF> the self type
    */
   public static class EnumerationBuilder<SELF extends EnumerationBuilder<SELF>> extends OptionallyNamedElementBuilder<SELF, Enumeration> {
      private Type dataType;
      private final java.util.List<Value> values = new ArrayList<>();

      public EnumerationBuilder() {
         super( EnumerationBuilder.class );
      }

      public EnumerationBuilder( final AspectModelUrn urn ) {
         super( EnumerationBuilder.class, urn );
      }

      public SELF dataType( final Type type ) {
         dataType = type;
         return myself;
      }

      public <T extends Value, C extends java.util.Collection<T>> SELF values( final C values ) {
         for ( final Value value : values ) {
            value( value );
         }
         return myself;
      }

      public SELF values( final Value... values ) {
         for ( final Value value : values ) {
            value( value );
         }
         return myself;
      }

      public SELF value( final Value value ) {
         if ( dataType == null ) {
            throw new AspectModelBuildingException( "Enumeration is missing a type" );
         }
         if ( !value.getType().isTypeOrSubtypeOf( dataType ) ) {
            throw new AspectModelBuildingException( "Value " + value + "'s type is not and no subtype of " + dataType );
         }
         values.add( value );
         return myself;
      }

      @Override
      public Enumeration build() {
         if ( values.isEmpty() ) {
            throw new AspectModelBuildingException( "Enumeration's values may not be empty" );
         }
         return new DefaultEnumeration( baseAttributes(), dataType, values );
      }
   }

   /**
    * Builder for {@link State}
    *
    * @param <SELF> the self type
    */
   public static class StateBuilder<SELF extends StateBuilder<SELF>> extends OptionallyNamedElementBuilder<SELF, State> {
      private Type dataType;
      private final java.util.List<Value> values = new ArrayList<>();
      private Value defaultValue;

      public StateBuilder() {
         super( StateBuilder.class );
      }

      public StateBuilder( final AspectModelUrn urn ) {
         super( StateBuilder.class, urn );
      }

      public SELF defaultValue( final Value defaultValue ) {
         if ( dataType == null ) {
            throw new AspectModelBuildingException( "State is missing a type" );
         }
         if ( !defaultValue.getType().isTypeOrSubtypeOf( dataType ) ) {
            throw new AspectModelBuildingException( "Default value " + defaultValue + "'s type is not and no subtype of " + dataType );
         }
         this.defaultValue = defaultValue;
         return myself;
      }

      public SELF dataType( final Type type ) {
         dataType = type;
         return myself;
      }

      public <T extends Value, C extends java.util.Collection<T>> SELF values( final C values ) {
         for ( final Value value : values ) {
            value( value );
         }
         return myself;
      }

      public SELF values( final Value... values ) {
         for ( final Value value : values ) {
            value( value );
         }
         return myself;
      }

      public SELF value( final Value value ) {
         if ( dataType == null ) {
            throw new AspectModelBuildingException( "State is missing a type" );
         }
         if ( !value.getType().isTypeOrSubtypeOf( dataType ) ) {
            throw new AspectModelBuildingException( "Value " + value + "'s type is not and no subtype of " + dataType );
         }
         values.add( value );
         return myself;
      }

      @Override
      public State build() {
         if ( defaultValue == null ) {
            throw new AspectModelBuildingException( "State is missing a default value" );
         }
         if ( !values.contains( defaultValue ) ) {
            throw new AspectModelBuildingException( "Default value must be part of the values of the State" );
         }
         return new DefaultState( baseAttributes(), dataType, values, defaultValue );
      }
   }

   /**
    * Builder for {@link Duration}
    *
    * @param <SELF> the self type
    */
   public static class DurationBuilder<SELF extends DurationBuilder<SELF>> extends OptionallyNamedElementBuilder<SELF, Duration> {
      private Unit unit;
      private Type dataType;

      public DurationBuilder() {
         super( DurationBuilder.class );
      }

      public DurationBuilder( final AspectModelUrn urn ) {
         super( DurationBuilder.class, urn );
      }

      public SELF unit( final Unit unit ) {
         if ( !unit.getQuantityKinds().contains( QuantityKinds.TIME ) ) {
            throw new AspectModelBuildingException( "Unit of Duration must have the quantity kind 'time'" );
         }
         this.unit = unit;
         return myself;
      }

      public SELF dataType( final Type type ) {
         dataType = type;
         return myself;
      }

      @Override
      public Duration build() {
         if ( unit == null ) {
            throw new AspectModelBuildingException( "Duration is missing a unit" );
         }
         return new DefaultDuration( baseAttributes(), dataType, Optional.of( unit ) );
      }
   }

   /**
    * Builder for {@link Collection}
    *
    * @param <SELF> the self type
    */
   public static class CollectionBuilder<SELF extends CollectionBuilder<SELF>> extends OptionallyNamedElementBuilder<SELF, Collection> {
      private Type dataType;
      private Characteristic elementCharacteristic;

      public CollectionBuilder() {
         super( CollectionBuilder.class );
      }

      public CollectionBuilder( final AspectModelUrn urn ) {
         super( CollectionBuilder.class, urn );
      }

      public SELF dataType( final Type type ) {
         dataType = type;
         return myself;
      }

      public SELF elementCharacteristic( final Characteristic elementCharacteristic ) {
         this.elementCharacteristic = elementCharacteristic;
         return myself;
      }

      @Override
      public Collection build() {
         if ( dataType != null && elementCharacteristic != null ) {
            throw new AspectModelBuildingException( "Collection must not have both dataType and elementCharacteristic" );
         }
         return new DefaultCollection( baseAttributes(), Optional.ofNullable( dataType ), Optional.ofNullable( elementCharacteristic ) );
      }
   }

   /**
    * Builder for {@link List}
    *
    * @param <SELF> the self type
    */
   public static class ListBuilder<SELF extends ListBuilder<SELF>> extends OptionallyNamedElementBuilder<SELF, List> {
      private Type dataType;
      private Characteristic elementCharacteristic;

      public ListBuilder() {
         super( ListBuilder.class );
      }

      public ListBuilder( final AspectModelUrn urn ) {
         super( ListBuilder.class, urn );
      }

      public SELF dataType( final Type type ) {
         dataType = type;
         return myself;
      }

      public SELF elementCharacteristic( final Characteristic elementCharacteristic ) {
         this.elementCharacteristic = elementCharacteristic;
         return myself;
      }

      @Override
      public List build() {
         if ( dataType != null && elementCharacteristic != null ) {
            throw new AspectModelBuildingException( "List must not have both dataType and elementCharacteristic" );
         }
         return new DefaultList( baseAttributes(), Optional.ofNullable( dataType ), Optional.ofNullable( elementCharacteristic ) );
      }
   }

   /**
    * Builder for {@link Set}
    *
    * @param <SELF> the self type
    */
   public static class SetBuilder<SELF extends SetBuilder<SELF>> extends OptionallyNamedElementBuilder<SELF, Set> {
      private Type dataType;
      private Characteristic elementCharacteristic;

      public SetBuilder() {
         super( SetBuilder.class );
      }

      public SetBuilder( final AspectModelUrn urn ) {
         super( SetBuilder.class, urn );
      }

      public SELF dataType( final Type type ) {
         dataType = type;
         return myself;
      }

      public SELF elementCharacteristic( final Characteristic elementCharacteristic ) {
         this.elementCharacteristic = elementCharacteristic;
         return myself;
      }

      @Override
      public Set build() {
         if ( dataType != null && elementCharacteristic != null ) {
            throw new AspectModelBuildingException( "Set must not have both dataType and elementCharacteristic" );
         }
         return new DefaultSet( baseAttributes(), Optional.ofNullable( dataType ), Optional.ofNullable( elementCharacteristic ) );
      }
   }

   /**
    * Builder for {@link SortedSet}
    *
    * @param <SELF> the self type
    */
   public static class SortedSetBuilder<SELF extends SortedSetBuilder<SELF>> extends OptionallyNamedElementBuilder<SELF, SortedSet> {
      private Type dataType;
      private Characteristic elementCharacteristic;

      public SortedSetBuilder() {
         super( SortedSetBuilder.class );
      }

      public SortedSetBuilder( final AspectModelUrn urn ) {
         super( SortedSetBuilder.class, urn );
      }

      public SELF dataType( final Type type ) {
         dataType = type;
         return myself;
      }

      public SELF elementCharacteristic( final Characteristic elementCharacteristic ) {
         this.elementCharacteristic = elementCharacteristic;
         return myself;
      }

      @Override
      public SortedSet build() {
         if ( dataType != null && elementCharacteristic != null ) {
            throw new AspectModelBuildingException( "SortedSet must not have both dataType and elementCharacteristic" );
         }
         return new DefaultSortedSet( baseAttributes(), Optional.ofNullable( dataType ), Optional.ofNullable( elementCharacteristic ) );
      }
   }

   /**
    * Builder for {@link TimeSeries}
    *
    * @param <SELF> the self type
    */
   public static class TimeSeriesBuilder<SELF extends TimeSeriesBuilder<SELF>> extends OptionallyNamedElementBuilder<SELF, TimeSeries> {
      private Entity dataType;

      public TimeSeriesBuilder() {
         super( TimeSeriesBuilder.class );
      }

      public TimeSeriesBuilder( final AspectModelUrn urn ) {
         super( TimeSeriesBuilder.class, urn );
      }

      public SELF dataType( final Entity entity ) {
         if ( !entity.isTypeOrSubtypeOf( samm_e.TimeSeriesEntity ) ) {
            throw new AspectModelBuildingException( "Datatype of TimeSeries must extend samm-e:TimeSeriesEntity" );
         }
         dataType = entity;
         return myself;
      }

      @Override
      public TimeSeries build() {
         if ( dataType == null ) {
            throw new AspectModelBuildingException( "TimeSeries is missing a dataType" );
         }
         return new DefaultTimeSeries( baseAttributes(), Optional.of( dataType ), Optional.empty() );
      }
   }

   /**
    * Builder for {@link Code}
    *
    * @param <SELF> the self type
    */
   public static class CodeBuilder<SELF extends CodeBuilder<SELF>> extends OptionallyNamedElementBuilder<SELF, Code> {
      private Type dataType;

      public CodeBuilder() {
         super( CodeBuilder.class );
      }

      public CodeBuilder( final AspectModelUrn urn ) {
         super( CodeBuilder.class, urn );
      }

      public SELF dataType( final Type type ) {
         dataType = type;
         return myself;
      }

      @Override
      public Code build() {
         return new DefaultCode( baseAttributes(), dataType );
      }
   }

   /**
    * Builder for {@link Either}
    *
    * @param <SELF> the self type
    */
   public static class EitherBuilder<SELF extends EitherBuilder<SELF>> extends OptionallyNamedElementBuilder<SELF, Either> {
      private Characteristic left;
      private Characteristic right;

      public EitherBuilder() {
         super( EitherBuilder.class );
      }

      public EitherBuilder( final AspectModelUrn urn ) {
         super( EitherBuilder.class, urn );
      }

      public SELF left( final Characteristic characteristic ) {
         left = characteristic;
         return myself;
      }

      public SELF right( final Characteristic characteristic ) {
         right = characteristic;
         return myself;
      }

      @Override
      public Either build() {
         if ( left == null ) {
            throw new AspectModelBuildingException( "Either is missing a left characteristic" );
         }
         if ( right == null ) {
            throw new AspectModelBuildingException( "Either is missing a right characteristic" );
         }
         return new DefaultEither( baseAttributes(), Optional.empty(), left, right );
      }
   }

   /**
    * Builder for {@link SingleEntity}
    *
    * @param <SELF> the self type
    */
   public static class SingleEntityBuilder<SELF extends SingleEntityBuilder<SELF>>
         extends OptionallyNamedElementBuilder<SELF, SingleEntity> {
      private Type dataType;

      public SingleEntityBuilder() {
         super( SingleEntityBuilder.class );
      }

      public SingleEntityBuilder( final AspectModelUrn urn ) {
         super( SingleEntityBuilder.class, urn );
      }

      public SELF dataType( final Type type ) {
         dataType = type;
         return myself;
      }

      @Override
      public SingleEntity build() {
         return new DefaultSingleEntity( baseAttributes(), dataType );
      }
   }

   /**
    * Builder for {@link StructuredValue}
    *
    * @param <SELF> the self type
    */
   public static class StructuredValueBuilder<SELF extends StructuredValueBuilder<SELF>>
         extends OptionallyNamedElementBuilder<SELF, StructuredValue> {
      private Type dataType;
      private String deconstructionRule;
      private final java.util.List<Object> elements = new ArrayList<>();

      public StructuredValueBuilder() {
         super( StructuredValueBuilder.class );
      }

      public StructuredValueBuilder( final AspectModelUrn urn ) {
         super( StructuredValueBuilder.class, urn );
      }

      public SELF dataType( final Type type ) {
         if ( !( type instanceof final Scalar scalar ) || !scalar.hasStringLikeValueSpace() ) {
            throw new AspectModelBuildingException( "StructuredValue must have a dataType with a string-like value space" );
         }
         dataType = type;
         return myself;
      }

      public SELF deconstructionRule( final String deconstructionRule ) {
         try {
            Pattern.compile( deconstructionRule );
         } catch ( final Exception exception ) {
            throw new AspectModelBuildingException( "StructuredValue's deconstructionRule is invalid" );
         }
         this.deconstructionRule = deconstructionRule;
         return myself;
      }

      public <C extends java.util.Collection<Object>> SELF elements( final C elements ) {
         for ( final Object value : elements ) {
            element( value );
         }
         return myself;
      }

      public SELF values( final Object... elements ) {
         for ( final Object value : elements ) {
            element( value );
         }
         return myself;
      }

      public SELF element( final Object element ) {
         if ( !( element instanceof String || element instanceof Property ) ) {
            throw new AspectModelBuildingException( "StructuredValue's element list may only contain Strings and Properties" );
         }
         elements.add( element );
         return myself;
      }

      @Override
      public StructuredValue build() {
         return new DefaultStructuredValue( baseAttributes(), dataType, deconstructionRule, elements );
      }
   }

   /**
    * Builder for {@link LanguageConstraint}
    *
    * @param <SELF> the self type
    */
   public static class LanguageConstraintBuilder<SELF extends LanguageConstraintBuilder<SELF>>
         extends OptionallyNamedElementBuilder<SELF, LanguageConstraint> {
      private Locale languageCode;

      public LanguageConstraintBuilder() {
         super( LanguageConstraintBuilder.class );
      }

      public LanguageConstraintBuilder( final AspectModelUrn urn ) {
         super( LanguageConstraintBuilder.class, urn );
      }

      public SELF languageCode( final Locale languageCode ) {
         this.languageCode = languageCode;
         return myself;
      }

      @Override
      public LanguageConstraint build() {
         return new DefaultLanguageConstraint( baseAttributes(), languageCode );
      }
   }

   /**
    * Builder for {@link LocaleConstraint}
    *
    * @param <SELF> the self type
    */
   public static class LocaleConstraintBuilder<SELF extends LocaleConstraintBuilder<SELF>>
         extends OptionallyNamedElementBuilder<SELF, LocaleConstraint> {
      private Locale languageCode;

      public LocaleConstraintBuilder() {
         super( LocaleConstraintBuilder.class );
      }

      public LocaleConstraintBuilder( final AspectModelUrn urn ) {
         super( LocaleConstraintBuilder.class, urn );
      }

      public SELF languageCode( final Locale languageCode ) {
         this.languageCode = languageCode;
         return myself;
      }

      @Override
      public LocaleConstraint build() {
         return new DefaultLocaleConstraint( baseAttributes(), languageCode );
      }
   }

   /**
    * Builder for {@link RangeConstraint}
    *
    * @param <SELF> the self type
    */
   public static class RangeConstraintBuilder<SELF extends RangeConstraintBuilder<SELF>>
         extends OptionallyNamedElementBuilder<SELF, RangeConstraint> {
      private ScalarValue minValue;
      private ScalarValue maxValue;
      private BoundDefinition lowerBound;
      private BoundDefinition upperBound;

      public RangeConstraintBuilder() {
         super( RangeConstraintBuilder.class );
      }

      public RangeConstraintBuilder( final AspectModelUrn urn ) {
         super( RangeConstraint.class, urn );
      }

      public SELF minValue( final ScalarValue minValue ) {
         this.minValue = minValue;
         return myself;
      }

      public SELF maxValue( final ScalarValue maxValue ) {
         this.maxValue = maxValue;
         return myself;
      }

      public SELF lowerBound( final BoundDefinition lowerBound ) {
         final java.util.List<BoundDefinition> allowedBounds = java.util.List.of( BoundDefinition.OPEN, BoundDefinition.AT_LEAST,
               BoundDefinition.GREATER_THAN );
         if ( !allowedBounds.contains( lowerBound ) ) {
            throw new AspectModelBuildingException( "Invalid lowerBound for RangeConstraint: Allowed values are "
                  + allowedBounds.stream().map( BoundDefinition::toString ).collect( Collectors.joining( ", " ) ) );
         }
         this.lowerBound = lowerBound;
         return myself;
      }

      public SELF upperBound( final BoundDefinition upperBound ) {
         final java.util.List<BoundDefinition> allowedBounds = java.util.List.of( BoundDefinition.OPEN, BoundDefinition.AT_MOST,
               BoundDefinition.LESS_THAN );
         if ( !allowedBounds.contains( upperBound ) ) {
            throw new AspectModelBuildingException( "Invalid upperBound for RangeConstraint: Allowed values are "
                  + allowedBounds.stream().map( BoundDefinition::toString ).collect( Collectors.joining( ", " ) ) );
         }
         this.upperBound = upperBound;
         return myself;
      }

      @Override
      public RangeConstraint build() {
         if ( minValue == null && maxValue == null ) {
            throw new AspectModelBuildingException(
                  "At least one of minValue or maxValue must be given for RangeConstraint" );
         }
         if ( minValue != null && lowerBound == null ) {
            throw new AspectModelBuildingException(
                  "RangeConstraint with a minValue must set lowerBoundDefinition to either AT_LEAST or GREATER_THAN" );
         }
         if ( maxValue != null && upperBound == null ) {
            throw new AspectModelBuildingException(
                  "RangeConstraint with a maxValue must set upperBoundDefinition to AT_MOST or LESS_THAN" );
         }
         return new DefaultRangeConstraint( baseAttributes(),
               Optional.ofNullable( minValue ),
               Optional.ofNullable( maxValue ),
               Optional.ofNullable( lowerBound ).orElse( BoundDefinition.OPEN ),
               Optional.ofNullable( upperBound ).orElse( BoundDefinition.OPEN ) );
      }
   }

   /**
    * Builder for {@link EncodingConstraint}
    *
    * @param <SELF> the self type
    */
   public static class EncodingConstraintBuilder<SELF extends EncodingConstraintBuilder<SELF>>
         extends OptionallyNamedElementBuilder<SELF, EncodingConstraint> {
      private Charset encoding;

      public EncodingConstraintBuilder() {
         super( EncodingConstraintBuilder.class );
      }

      public EncodingConstraintBuilder( final AspectModelUrn urn ) {
         super( EncodingConstraintBuilder.class, urn );
      }

      public SELF value( final Charset encoding ) {
         this.encoding = encoding;
         return myself;
      }

      @Override
      public EncodingConstraint build() {
         return new DefaultEncodingConstraint( baseAttributes(), encoding );
      }
   }

   /**
    * Builder for {@link LengthConstraint}
    *
    * @param <SELF> the self type
    */
   public static class LengthConstraintBuilder<SELF extends LengthConstraintBuilder<SELF>>
         extends OptionallyNamedElementBuilder<SELF, LengthConstraint> {
      private BigInteger minValue;
      private BigInteger maxValue;

      public LengthConstraintBuilder() {
         super( LengthConstraintBuilder.class );
      }

      public LengthConstraintBuilder( final AspectModelUrn urn ) {
         super( LengthConstraintBuilder.class, urn );
      }

      public SELF minValue( final BigInteger minValue ) {
         this.minValue = minValue;
         return myself;
      }

      public SELF maxValue( final BigInteger maxValue ) {
         this.maxValue = maxValue;
         return myself;
      }

      @Override
      public LengthConstraint build() {
         if ( minValue == null && maxValue == null ) {
            throw new AspectModelBuildingException( "LengthConstraint is missing at least one of minValue or maxValue" );
         }
         return new DefaultLengthConstraint( baseAttributes(), Optional.ofNullable( minValue ), Optional.ofNullable( maxValue ) );
      }
   }

   /**
    * Builder for {@link RegularExpressionConstraint}
    *
    * @param <SELF> the self type
    */
   public static class RegularExpressionConstraintBuilder<SELF extends RegularExpressionConstraintBuilder<SELF>>
         extends OptionallyNamedElementBuilder<SELF, RegularExpressionConstraint> {
      private String regularExpression;

      public RegularExpressionConstraintBuilder() {
         super( RegularExpressionConstraintBuilder.class );
      }

      public RegularExpressionConstraintBuilder( final AspectModelUrn urn ) {
         super( RegularExpressionConstraintBuilder.class, urn );
      }

      public SELF value( final String regularExpression ) {
         try {
            Pattern.compile( regularExpression );
         } catch ( final Exception exception ) {
            throw new AspectModelBuildingException( "RegularExpressionConstraints's regular expression is invalid" );
         }
         this.regularExpression = regularExpression;
         return myself;
      }

      @Override
      public RegularExpressionConstraint build() {
         return new DefaultRegularExpressionConstraint( baseAttributes(), regularExpression );
      }
   }

   /**
    * Builder for {@link FixedPointConstraint}
    *
    * @param <SELF> the self type
    */
   public static class FixedPointConstraintBuilder<SELF extends FixedPointConstraintBuilder<SELF>>
         extends OptionallyNamedElementBuilder<SELF, FixedPointConstraint> {
      private Integer scale;
      private Integer integer;

      public FixedPointConstraintBuilder() {
         super( FixedPointConstraintBuilder.class );
      }

      public FixedPointConstraintBuilder( final AspectModelUrn urn ) {
         super( FixedPointConstraintBuilder.class, urn );
      }

      public SELF scale( final Integer scale ) {
         this.scale = scale;
         return myself;
      }

      public SELF integer( final Integer integer ) {
         this.integer = integer;
         return myself;
      }

      @Override
      public FixedPointConstraint build() {
         if ( scale == null ) {
            throw new AspectModelBuildingException( "FixedPointConstraint is missing a scale value" );
         }
         if ( integer == null ) {
            throw new AspectModelBuildingException( "FixedPointConstraint is missing a integer value" );
         }
         return new DefaultFixedPointConstraint( baseAttributes(), scale, integer );
      }
   }

   public static <T extends EntityInstanceBuilder<T>> EntityInstanceBuilder<T> entityInstance( final AspectModelUrn urn ) {
      return new EntityInstanceBuilder<>( urn );
   }

   public static <T extends EntityInstanceBuilder<T>> EntityInstanceBuilder<T> entityInstance( final String urn ) {
      return entityInstance( AspectModelUrn.fromUrn( urn ) );
   }

   public static ScalarValue value( final String stringValue ) {
      return new DefaultScalarValue( MetaModelBaseAttributes.builder().build(), stringValue, xsd.string );
   }

   public static ScalarValue value( final boolean booleanValue ) {
      return new DefaultScalarValue( MetaModelBaseAttributes.builder().build(), booleanValue, xsd.boolean_ );
   }

   public static ScalarValue value( final float floatValue ) {
      return new DefaultScalarValue( MetaModelBaseAttributes.builder().build(), floatValue, xsd.float_ );
   }

   public static ScalarValue value( final double doubleValue ) {
      return new DefaultScalarValue( MetaModelBaseAttributes.builder().build(), doubleValue, xsd.double_ );
   }

   /* Intentionally no value(int) method here, because an int value could imply different XSD types */

   public static ScalarValue value( final Object value, final Scalar type ) {
      MetaModelBaseAttributes metaModelBaseAttributes;

      if ( value instanceof ScalarValue scalarValue ) {
         metaModelBaseAttributes = MetaModelBaseAttributes.builder()
               .withUrn( scalarValue.urn() )
               .withPreferredNames( scalarValue.getPreferredNames() )
               .withDescriptions( scalarValue.getDescriptions() )
               .withSee( scalarValue.getSee() )
               .isAnonymous( scalarValue.isAnonymous() )
               .withSourceFile( scalarValue.getSourceFile() )
               .build();
      } else {
         metaModelBaseAttributes = MetaModelBaseAttributes.builder()
               .isAnonymous( false )
               .build();
      }
      return new DefaultScalarValue( metaModelBaseAttributes, value, dataTypeByUri( type.getUrn() ) );
   }

   public static <T> java.util.List<ScalarValue> values( final Scalar type, final T... values ) {
      final java.util.List<ScalarValue> result = new ArrayList<>();
      for ( final T value : values ) {
         result.add( value( value, type ) );
      }
      return result;
   }

   public static ScalarValue value( final String lexicalValue, final Scalar type ) {
      return new ValueInstantiator().buildScalarValue( lexicalValue, null, type.getUrn() )
            .orElseThrow( () -> new AspectModelBuildingException( "Value '" + lexicalValue + "' is invalid for type " + type ) );
   }

   public static ScalarValue value( final String text, final Locale language ) {
      return new ValueInstantiator().buildScalarValue( text, language.toLanguageTag(), RDF.langString.getURI() )
            .orElseThrow( AspectModelBuildingException::new );
   }

   public static <SELF extends AspectBuilder<SELF>> AspectBuilder<SELF> aspect( final AspectModelUrn urn ) {
      return new AspectBuilder<>( urn );
   }

   public static <SELF extends AspectBuilder<SELF>> AspectBuilder<SELF> aspect( final String urn ) {
      return aspect( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends AspectBuilder<SELF>> AspectBuilder<SELF> aspect( final Resource rdfResource ) {
      if ( rdfResource.isAnon() ) {
         throw new AspectModelBuildingException( "Aspect must have a URN" );
      }
      return aspect( AspectModelUrn.fromUrn( rdfResource.getURI() ) );
   }

   public static <SELF extends EntityBuilder<SELF>> EntityBuilder<SELF> entity( final AspectModelUrn urn ) {
      return new EntityBuilder<>( urn );
   }

   public static <SELF extends EntityBuilder<SELF>> EntityBuilder<SELF> entity( final String urn ) {
      return new EntityBuilder<>( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends EntityBuilder<SELF>> EntityBuilder<SELF> entity( final Resource rdfResource ) {
      if ( rdfResource.isAnon() ) {
         throw new AspectModelBuildingException( "Entity must have a URN" );
      }
      return new EntityBuilder<>( AspectModelUrn.fromUrn( rdfResource.getURI() ) );
   }

   public static <SELF extends PropertyBuilder<SELF>> PropertyBuilder<SELF> property( final AspectModelUrn urn ) {
      return new PropertyBuilder<>( urn );
   }

   public static <SELF extends PropertyBuilder<SELF>> PropertyBuilder<SELF> property( final String urn ) {
      return property( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends PropertyBuilder<SELF>> PropertyBuilder<SELF> property( final Resource rdfResource ) {
      if ( rdfResource.isAnon() ) {
         throw new AspectModelBuildingException( "Property must have a URN" );
      }
      return property( AspectModelUrn.fromUrn( rdfResource.getURI() ) );
   }

   public static <SELF extends CharacteristicBuilder<SELF>> CharacteristicBuilder<SELF> characteristic() {
      return new CharacteristicBuilder<>();
   }

   public static <SELF extends CharacteristicBuilder<SELF>> CharacteristicBuilder<SELF> characteristic( final AspectModelUrn urn ) {
      return new CharacteristicBuilder<>( urn );
   }

   public static <SELF extends CharacteristicBuilder<SELF>> CharacteristicBuilder<SELF> characteristic( final String urn ) {
      return characteristic( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends CharacteristicBuilder<SELF>> CharacteristicBuilder<SELF> characteristic( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? characteristic()
            : characteristic( rdfResource.getURI() );
   }

   public static <SELF extends TraitBuilder<SELF>> TraitBuilder<SELF> trait() {
      return new TraitBuilder<>();
   }

   public static <SELF extends TraitBuilder<SELF>> TraitBuilder<SELF> trait( final AspectModelUrn urn ) {
      return new TraitBuilder<>( urn );
   }

   public static <SELF extends TraitBuilder<SELF>> TraitBuilder<SELF> trait( final String urn ) {
      return trait( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends TraitBuilder<SELF>> TraitBuilder<SELF> trait( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? trait()
            : trait( rdfResource.getURI() );
   }

   public static <SELF extends QuantifiableBuilder<SELF>> QuantifiableBuilder<SELF> quantifiable() {
      return new QuantifiableBuilder<>();
   }

   public static <SELF extends QuantifiableBuilder<SELF>> QuantifiableBuilder<SELF> quantifiable( final AspectModelUrn urn ) {
      return new QuantifiableBuilder<>( urn );
   }

   public static <SELF extends QuantifiableBuilder<SELF>> QuantifiableBuilder<SELF> quantifiable( final String urn ) {
      return quantifiable( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends QuantifiableBuilder<SELF>> QuantifiableBuilder<SELF> quantifiable( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? quantifiable()
            : quantifiable( rdfResource.getURI() );
   }

   public static <SELF extends MeasurementBuilder<SELF>> MeasurementBuilder<SELF> measurement() {
      return new MeasurementBuilder<>();
   }

   public static <SELF extends MeasurementBuilder<SELF>> MeasurementBuilder<SELF> measurement( final AspectModelUrn urn ) {
      return new MeasurementBuilder<>( urn );
   }

   public static <SELF extends MeasurementBuilder<SELF>> MeasurementBuilder<SELF> measurement( final String urn ) {
      return measurement( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends MeasurementBuilder<SELF>> MeasurementBuilder<SELF> measurement( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? measurement()
            : measurement( rdfResource.getURI() );
   }

   public static <SELF extends EnumerationBuilder<SELF>> EnumerationBuilder<SELF> enumeration() {
      return new EnumerationBuilder<>();
   }

   public static <SELF extends EnumerationBuilder<SELF>> EnumerationBuilder<SELF> enumeration( final AspectModelUrn urn ) {
      return new EnumerationBuilder<>( urn );
   }

   public static <SELF extends EnumerationBuilder<SELF>> EnumerationBuilder<SELF> enumeration( final String urn ) {
      return enumeration( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends EnumerationBuilder<SELF>> EnumerationBuilder<SELF> enumeration( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? enumeration()
            : enumeration( rdfResource.getURI() );
   }

   public static <SELF extends StateBuilder<SELF>> StateBuilder<SELF> state() {
      return new StateBuilder<>();
   }

   public static <SELF extends StateBuilder<SELF>> StateBuilder<SELF> state( final AspectModelUrn urn ) {
      return new StateBuilder<>( urn );
   }

   public static <SELF extends StateBuilder<SELF>> StateBuilder<SELF> state( final String urn ) {
      return state( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends StateBuilder<SELF>> StateBuilder<SELF> state( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? state()
            : state( rdfResource.getURI() );
   }

   public static <SELF extends DurationBuilder<SELF>> DurationBuilder<SELF> duration() {
      return new DurationBuilder<>();
   }

   public static <SELF extends DurationBuilder<SELF>> DurationBuilder<SELF> duration( final AspectModelUrn urn ) {
      return new DurationBuilder<>( urn );
   }

   public static <SELF extends DurationBuilder<SELF>> DurationBuilder<SELF> duration( final String urn ) {
      return duration( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends DurationBuilder<SELF>> DurationBuilder<SELF> duration( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? duration()
            : duration( rdfResource.getURI() );
   }

   public static <SELF extends CollectionBuilder<SELF>> CollectionBuilder<SELF> collection() {
      return new CollectionBuilder<>();
   }

   public static <SELF extends CollectionBuilder<SELF>> CollectionBuilder<SELF> collection( final AspectModelUrn urn ) {
      return new CollectionBuilder<>( urn );
   }

   public static <SELF extends CollectionBuilder<SELF>> CollectionBuilder<SELF> collection( final String urn ) {
      return collection( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends CollectionBuilder<SELF>> CollectionBuilder<SELF> collection( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? collection()
            : collection( rdfResource.getURI() );
   }

   public static <SELF extends ListBuilder<SELF>> ListBuilder<SELF> list() {
      return new ListBuilder<>();
   }

   public static <SELF extends ListBuilder<SELF>> ListBuilder<SELF> list( final AspectModelUrn urn ) {
      return new ListBuilder<>( urn );
   }

   public static <SELF extends ListBuilder<SELF>> ListBuilder<SELF> list( final String urn ) {
      return list( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends ListBuilder<SELF>> ListBuilder<SELF> list( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? list()
            : list( rdfResource.getURI() );
   }

   public static <SELF extends SetBuilder<SELF>> SetBuilder<SELF> set() {
      return new SetBuilder<>();
   }

   public static <SELF extends SetBuilder<SELF>> SetBuilder<SELF> set( final AspectModelUrn urn ) {
      return new SetBuilder<>( urn );
   }

   public static <SELF extends SetBuilder<SELF>> SetBuilder<SELF> set( final String urn ) {
      return set( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends SetBuilder<SELF>> SetBuilder<SELF> set( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? set()
            : set( rdfResource.getURI() );
   }

   public static <SELF extends SortedSetBuilder<SELF>> SortedSetBuilder<SELF> sortedSet() {
      return new SortedSetBuilder<>();
   }

   public static <SELF extends SortedSetBuilder<SELF>> SortedSetBuilder<SELF> sortedSet( final AspectModelUrn urn ) {
      return new SortedSetBuilder<>( urn );
   }

   public static <SELF extends SortedSetBuilder<SELF>> SortedSetBuilder<SELF> sortedSet( final String urn ) {
      return sortedSet( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends SortedSetBuilder<SELF>> SortedSetBuilder<SELF> sortedSet( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? sortedSet()
            : sortedSet( rdfResource.getURI() );
   }

   public static <SELF extends TimeSeriesBuilder<SELF>> TimeSeriesBuilder<SELF> timeSeries() {
      return new TimeSeriesBuilder<>();
   }

   public static <SELF extends TimeSeriesBuilder<SELF>> TimeSeriesBuilder<SELF> timeSeries( final AspectModelUrn urn ) {
      return new TimeSeriesBuilder<>( urn );
   }

   public static <SELF extends TimeSeriesBuilder<SELF>> TimeSeriesBuilder<SELF> timeSeries( final String urn ) {
      return timeSeries( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends TimeSeriesBuilder<SELF>> TimeSeriesBuilder<SELF> timeSeries( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? timeSeries()
            : timeSeries( rdfResource.getURI() );
   }

   public static <SELF extends CodeBuilder<SELF>> CodeBuilder<SELF> code() {
      return new CodeBuilder<>();
   }

   public static <SELF extends CodeBuilder<SELF>> CodeBuilder<SELF> code( final AspectModelUrn urn ) {
      return new CodeBuilder<>( urn );
   }

   public static <SELF extends CodeBuilder<SELF>> CodeBuilder<SELF> code( final String urn ) {
      return code( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends CodeBuilder<SELF>> CodeBuilder<SELF> code( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? code()
            : code( rdfResource.getURI() );
   }

   public static <SELF extends EitherBuilder<SELF>> EitherBuilder<SELF> either() {
      return new EitherBuilder<>();
   }

   public static <SELF extends EitherBuilder<SELF>> EitherBuilder<SELF> either( final AspectModelUrn urn ) {
      return new EitherBuilder<>( urn );
   }

   public static <SELF extends EitherBuilder<SELF>> EitherBuilder<SELF> either( final String urn ) {
      return either( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends EitherBuilder<SELF>> EitherBuilder<SELF> either( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? either()
            : either( rdfResource.getURI() );
   }

   public static <SELF extends SingleEntityBuilder<SELF>> SingleEntityBuilder<SELF> singleEntity() {
      return new SingleEntityBuilder<>();
   }

   public static <SELF extends SingleEntityBuilder<SELF>> SingleEntityBuilder<SELF> singleEntity( final AspectModelUrn urn ) {
      return new SingleEntityBuilder<>( urn );
   }

   public static <SELF extends SingleEntityBuilder<SELF>> SingleEntityBuilder<SELF> singleEntity( final String urn ) {
      return singleEntity( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends SingleEntityBuilder<SELF>> SingleEntityBuilder<SELF> singleEntity( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? singleEntity()
            : singleEntity( rdfResource.getURI() );
   }

   public static <SELF extends StructuredValueBuilder<SELF>> StructuredValueBuilder<SELF> structuredValue() {
      return new StructuredValueBuilder<>();
   }

   public static <SELF extends StructuredValueBuilder<SELF>> StructuredValueBuilder<SELF> structuredValue( final AspectModelUrn urn ) {
      return new StructuredValueBuilder<>( urn );
   }

   public static <SELF extends StructuredValueBuilder<SELF>> StructuredValueBuilder<SELF> structuredValue( final String urn ) {
      return structuredValue( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends StructuredValueBuilder<SELF>> StructuredValueBuilder<SELF> structuredValue( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? structuredValue()
            : structuredValue( rdfResource.getURI() );
   }

   public static <SELF extends LanguageConstraintBuilder<SELF>> LanguageConstraintBuilder<SELF> languageConstraint() {
      return new LanguageConstraintBuilder<>();
   }

   public static <SELF extends LanguageConstraintBuilder<SELF>> LanguageConstraintBuilder<SELF> languageConstraint(
         final AspectModelUrn urn ) {
      return new LanguageConstraintBuilder<>( urn );
   }

   public static <SELF extends LanguageConstraintBuilder<SELF>> LanguageConstraintBuilder<SELF> languageConstraint( final String urn ) {
      return languageConstraint( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends LanguageConstraintBuilder<SELF>> LanguageConstraintBuilder<SELF> languageConstraint(
         final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? languageConstraint()
            : languageConstraint( rdfResource.getURI() );
   }

   public static <SELF extends LocaleConstraintBuilder<SELF>> LocaleConstraintBuilder<SELF> localeConstraint() {
      return new LocaleConstraintBuilder<>();
   }

   public static <SELF extends LocaleConstraintBuilder<SELF>> LocaleConstraintBuilder<SELF> localeConstraint( final AspectModelUrn urn ) {
      return new LocaleConstraintBuilder<>( urn );
   }

   public static <SELF extends LocaleConstraintBuilder<SELF>> LocaleConstraintBuilder<SELF> localeConstraint( final String urn ) {
      return localeConstraint( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends LocaleConstraintBuilder<SELF>> LocaleConstraintBuilder<SELF> localeConstraint( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? localeConstraint()
            : localeConstraint( rdfResource.getURI() );
   }

   public static <SELF extends RangeConstraintBuilder<SELF>> RangeConstraintBuilder<SELF> rangeConstraint() {
      return new RangeConstraintBuilder<>();
   }

   public static <SELF extends RangeConstraintBuilder<SELF>> RangeConstraintBuilder<SELF> rangeConstraint( final AspectModelUrn urn ) {
      return new RangeConstraintBuilder<>( urn );
   }

   public static <SELF extends RangeConstraintBuilder<SELF>> RangeConstraintBuilder<SELF> rangeConstraint( final String urn ) {
      return rangeConstraint( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends RangeConstraintBuilder<SELF>> RangeConstraintBuilder<SELF> rangeConstraint( final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? rangeConstraint()
            : rangeConstraint( rdfResource.getURI() );
   }

   public static <SELF extends EncodingConstraintBuilder<SELF>> EncodingConstraintBuilder<SELF> encodingConstraint() {
      return new EncodingConstraintBuilder<>();
   }

   public static <SELF extends EncodingConstraintBuilder<SELF>> EncodingConstraintBuilder<SELF> encodingConstraint(
         final AspectModelUrn urn ) {
      return new EncodingConstraintBuilder<>( urn );
   }

   public static <SELF extends EncodingConstraintBuilder<SELF>> EncodingConstraintBuilder<SELF> encodingConstraint( final String urn ) {
      return encodingConstraint( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends EncodingConstraintBuilder<SELF>> EncodingConstraintBuilder<SELF> encodingConstraint(
         final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? encodingConstraint()
            : encodingConstraint( rdfResource.getURI() );
   }

   public static <SELF extends LengthConstraintBuilder<SELF>> LengthConstraintBuilder<SELF> lengthConstraint() {
      return new LengthConstraintBuilder<>();
   }

   public static <SELF extends LengthConstraintBuilder<SELF>> LengthConstraintBuilder<SELF> lengthConstraint(
         final AspectModelUrn urn ) {
      return new LengthConstraintBuilder<>( urn );
   }

   public static <SELF extends LengthConstraintBuilder<SELF>> LengthConstraintBuilder<SELF> lengthConstraint( final String urn ) {
      return lengthConstraint( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends LengthConstraintBuilder<SELF>> LengthConstraintBuilder<SELF> lengthConstraint(
         final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? lengthConstraint()
            : lengthConstraint( rdfResource.getURI() );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends RegularExpressionConstraintBuilder<SELF>> RegularExpressionConstraintBuilder<SELF>
         regularExpressionConstraint() {
      //@formatter:on
      return new RegularExpressionConstraintBuilder<>();
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends RegularExpressionConstraintBuilder<SELF>> RegularExpressionConstraintBuilder<SELF>
         regularExpressionConstraint( final AspectModelUrn urn ) {
      //@formatter:on
      return new RegularExpressionConstraintBuilder<>( urn );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends RegularExpressionConstraintBuilder<SELF>> RegularExpressionConstraintBuilder<SELF>
         regularExpressionConstraint( final String urn ) {
      //@formatter:on
      return regularExpressionConstraint( AspectModelUrn.fromUrn( urn ) );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends RegularExpressionConstraintBuilder<SELF>> RegularExpressionConstraintBuilder<SELF>
         regularExpressionConstraint( final Resource rdfResource ) {
      //@formatter:on
      return rdfResource.isAnon()
            ? regularExpressionConstraint()
            : regularExpressionConstraint( rdfResource.getURI() );
   }

   public static <SELF extends FixedPointConstraintBuilder<SELF>> FixedPointConstraintBuilder<SELF> fixedPointConstraint() {
      return new FixedPointConstraintBuilder<>();
   }

   public static <SELF extends FixedPointConstraintBuilder<SELF>> FixedPointConstraintBuilder<SELF> fixedPointConstraint(
         final AspectModelUrn urn ) {
      return new FixedPointConstraintBuilder<>( urn );
   }

   public static <SELF extends FixedPointConstraintBuilder<SELF>> FixedPointConstraintBuilder<SELF> fixedPointConstraint(
         final String urn ) {
      return fixedPointConstraint( AspectModelUrn.fromUrn( urn ) );
   }

   public static <SELF extends FixedPointConstraintBuilder<SELF>> FixedPointConstraintBuilder<SELF> fixedPointConstraint(
         final Resource rdfResource ) {
      return rdfResource.isAnon()
            ? fixedPointConstraint()
            : fixedPointConstraint( rdfResource.getURI() );
   }
}
