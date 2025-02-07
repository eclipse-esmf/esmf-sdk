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

package org.eclipse.esmf.test.shared;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.QuantityKinds;
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

import org.assertj.core.api.AbstractStringAssert;

/**
 * Assert for {@link Characteristic}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class CharacteristicAssert<SELF extends CharacteristicAssert<SELF, ACTUAL>, ACTUAL extends Characteristic>
      extends AbstractCharacteristicAssert<SELF, ACTUAL> {
   // Used when asserting a "default Characteristic" object
   public CharacteristicAssert( final ACTUAL actual ) {
      this( actual, CharacteristicAssert.class, "Characteristic" );
   }

   // Used from subclassing asserts for specific Characteristics
   public CharacteristicAssert( final ACTUAL actual, final Class<?> selfType, final String modelElementType ) {
      super( actual, selfType, modelElementType );
   }

   /**
    * Assert for {@link Trait}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class TraitAssert<SELF extends TraitAssert<SELF, ACTUAL>, ACTUAL extends Trait>
         extends AbstractCharacteristicAssert<SELF, ACTUAL> {
      public TraitAssert( final ACTUAL actual ) {
         super( actual, TraitAssert.class, "Trait" );
      }

      @SuppressWarnings( "unchecked" )
      public <S extends CharacteristicAssert<S, A>, A extends Characteristic> CharacteristicAssert<S, A> baseCharacteristic() {
         return new CharacteristicAssert<>( (A) actual.getBaseCharacteristic() );
      }

      @SuppressWarnings( "unchecked" )
      public <S extends ConstraintAssert<S, A>, A extends Constraint> ConstraintAssert<S, A> hasSingleConstraintThat() {
         if ( actual.getConstraints().size() != 1 ) {
            failWithMessage( "Expected %s to have exactly one Constraint, but it didn't", modelElementType );
         }
         return new ConstraintAssert<>( (A) actual.getConstraints().get( 0 ) );
      }

      public SELF hasNoConstraints() {
         assertThat( actual.getConstraints() ).isEmpty();
         return myself;
      }

      public org.assertj.core.api.ListAssert<Constraint> constraints() {
         return assertThat( actual.getConstraints() );
      }
   }

   public SELF isTrait() {
      assertThat( actual ).isInstanceOf( Trait.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends TraitAssert<S, A>, A extends Trait> TraitAssert<S, A> isTraitThat() {
      isTrait();
      return new TraitAssert<>( (A) actual );
   }

   /**
    * Assert for {@link Quantifiable}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class QuantifiableAssert<SELF extends QuantifiableAssert<SELF, ACTUAL>, ACTUAL extends Quantifiable>
         extends AbstractCharacteristicAssert<SELF, ACTUAL> {
      public QuantifiableAssert( final ACTUAL actual ) {
         this( actual, QuantifiableAssert.class, "Quantifiable" );
      }

      protected QuantifiableAssert( final ACTUAL actual, final Class<?> selfType, final String modelElementType ) {
         super( actual, selfType, modelElementType );
      }

      @SuppressWarnings( "unchecked" )
      public <S extends UnitAssert<S, A>, A extends Unit> UnitAssert<S, A> unit() {
         assertThat( actual.getUnit() ).isPresent();
         return new UnitAssert<>( (A) actual.getUnit().orElseThrow() );
      }

      public SELF hasNoUnit() {
         assertThat( actual.getUnit() ).isEmpty();
         return myself;
      }
   }

   public SELF isQuantifiable() {
      assertThat( actual ).isInstanceOf( Quantifiable.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends QuantifiableAssert<S, A>, A extends Quantifiable> QuantifiableAssert<S, A> isQuantifiableThat() {
      isQuantifiable();
      return new QuantifiableAssert<>( (A) actual );
   }

   /**
    * Assert for {@link Measurement}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class MeasurementAssert<SELF extends MeasurementAssert<SELF, ACTUAL>, ACTUAL extends Measurement>
         extends QuantifiableAssert<SELF, ACTUAL> {
      public MeasurementAssert( final ACTUAL actual ) {
         super( actual, MeasurementAssert.class, "Measurement" );
      }
   }

   public SELF isMeasurement() {
      assertThat( actual ).isInstanceOf( Measurement.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends MeasurementAssert<S, A>, A extends Measurement> MeasurementAssert<S, A> isMeasurementThat() {
      isMeasurement();
      return new MeasurementAssert<>( (A) actual );
   }

   /**
    * Assert for {@link Enumeration}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class EnumerationAssert<SELF extends EnumerationAssert<SELF, ACTUAL>, ACTUAL extends Enumeration>
         extends AbstractCharacteristicAssert<SELF, ACTUAL> {
      public EnumerationAssert( final ACTUAL actual ) {
         this( actual, EnumerationAssert.class, "Enumeration" );
      }

      protected EnumerationAssert( final ACTUAL actual, final Class<?> selfType, final String modelElementType ) {
         super( actual, selfType, modelElementType );
      }

      public SELF containsValue( final Value value ) {
         assertThat( actual.getValues() ).contains( value );
         return myself;
      }

      public SELF hasNoValues() {
         assertThat( actual.getValues() ).isEmpty();
         return myself;
      }

      public org.assertj.core.api.ListAssert<Value> values() {
         return assertThat( actual.getValues() );
      }
   }

   public SELF isEnumeration() {
      assertThat( actual ).isInstanceOf( Enumeration.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends EnumerationAssert<S, A>, A extends Enumeration> EnumerationAssert<S, A> isEnumerationThat() {
      isEnumeration();
      return new EnumerationAssert<>( (A) actual );
   }

   /**
    * Assert for {@link State}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class StateAssert<SELF extends StateAssert<SELF, ACTUAL>, ACTUAL extends State>
         extends EnumerationAssert<SELF, ACTUAL> {
      protected StateAssert( final ACTUAL actual ) {
         super( actual, StateAssert.class, "State" );
      }

      public SELF hasDefaultValue( final Value defaultValue ) {
         assertThat( actual.getDefaultValue() ).isEqualTo( defaultValue );
         return myself;
      }

      @SuppressWarnings( "unchecked" )
      public <S extends ValueAssert<S, A>, A extends Value> ValueAssert<S, A> defaultValue() {
         return new ValueAssert<>( (A) actual.getDefaultValue() );
      }
   }

   public SELF isState() {
      assertThat( actual ).isInstanceOf( State.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends StateAssert<S, A>, A extends State> StateAssert<S, A> isStateThat() {
      isState();
      return new StateAssert<>( (A) actual );
   }

   /**
    * Assert for {@link Duration}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class DurationAssert<SELF extends DurationAssert<SELF, ACTUAL>, ACTUAL extends Duration>
         extends QuantifiableAssert<SELF, ACTUAL> {
      public DurationAssert( final ACTUAL actual ) {
         super( actual, DurationAssert.class, "Duration" );
      }

      @Override
      public <S extends UnitAssert<S, A>, A extends Unit> UnitAssert<S, A> unit() {
         assertThat( actual.getUnit() ).isPresent().map( Unit::getQuantityKinds ).contains( java.util.Set.of( QuantityKinds.TIME ) );
         return super.unit();
      }
   }

   public SELF isDuration() {
      assertThat( actual ).isInstanceOf( Duration.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends DurationAssert<S, A>, A extends Duration> DurationAssert<S, A> isDurationThat() {
      isDuration();
      return new DurationAssert<>( (A) actual );
   }

   /**
    * Assert for {@link Collection}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class CollectionAssert<SELF extends CollectionAssert<SELF, ACTUAL>, ACTUAL extends Collection>
         extends AbstractCharacteristicAssert<SELF, ACTUAL> {
      public CollectionAssert( final ACTUAL actual ) {
         super( actual, CollectionAssert.class, "Collection" );
      }

      protected CollectionAssert( final ACTUAL actual, final Class<?> selfType, final String modelElementType ) {
         super( actual, selfType, modelElementType );
      }

      public SELF isOrdered() {
         if ( !actual.isOrdered() ) {
            failWithMessage( "Expected %s <%s> to be ordered, but it wasn't", modelElementType, actual.urn() );
         }
         return myself;
      }

      public SELF isUnOrdered() {
         if ( actual.isOrdered() ) {
            failWithMessage( "Expected %s <%s> to be unordered, but it wasn't", modelElementType, actual.urn() );
         }
         return myself;
      }

      public SELF allowsDuplicates() {
         if ( !actual.allowsDuplicates() ) {
            failWithMessage( "Expected %s <%s> to allow duplicates, but it didn't", modelElementType, actual.urn() );
         }
         return myself;
      }

      public SELF allowsNoDuplicates() {
         if ( actual.allowsDuplicates() ) {
            failWithMessage( "Expected %s <%s> to not allow duplicates, but it did", modelElementType, actual.urn() );
         }
         return myself;
      }

      public SELF hasElementCharacteristic( final Characteristic characteristic ) {
         hasSomeElementCharacteristic();
         assertThat( actual.getElementCharacteristic() ).contains( characteristic );
         return myself;
      }

      public SELF hasSomeElementCharacteristic() {
         assertThat( actual.getElementCharacteristic() ).isPresent();
         return myself;
      }

      public SELF hasNoElementCharacteristic() {
         assertThat( actual.getElementCharacteristic() ).isEmpty();
         return myself;
      }

      public SELF hasNoDatatype() {
         assertThat( actual.getDataType() ).isEmpty();
         return myself;
      }

      @SuppressWarnings( "unchecked" )
      public <S extends CharacteristicAssert<S, A>, A extends Characteristic> CharacteristicAssert<S, A> elementCharacteristic() {
         assertThat( actual.getElementCharacteristic() ).isPresent();
         return new CharacteristicAssert<>( (A) actual.getElementCharacteristic().orElseThrow() );
      }
   }

   public SELF isCollection() {
      assertThat( actual ).isInstanceOf( Collection.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends CollectionAssert<S, A>, A extends Collection> CollectionAssert<S, A> isCollectionThat() {
      isCollection();
      return new CollectionAssert<>( (A) actual );
   }

   /**
    * Assert for {@link List}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class ListAssert<SELF extends ListAssert<SELF, ACTUAL>, ACTUAL extends List> extends CollectionAssert<SELF, ACTUAL> {
      public ListAssert( final ACTUAL actual ) {
         super( actual, ListAssert.class, "List" );
      }
   }

   public SELF isList() {
      assertThat( actual ).isInstanceOf( List.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends ListAssert<S, A>, A extends List> ListAssert<S, A> isListThat() {
      isList();
      return new ListAssert<>( (A) actual );
   }

   /**
    * Assert for {@link Set}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class SetAssert<SELF extends SetAssert<SELF, ACTUAL>, ACTUAL extends Set> extends CollectionAssert<SELF, ACTUAL> {
      public SetAssert( final ACTUAL actual ) {
         super( actual, SetAssert.class, "Set" );
      }
   }

   public SELF isSet() {
      assertThat( actual ).isInstanceOf( Set.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends SetAssert<S, A>, A extends Set> SetAssert<S, A> isSetThat() {
      isSet();
      return new SetAssert<>( (A) actual );
   }

   /**
    * Assert for {@link SortedSet}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class SortedSetAssert<SELF extends SortedSetAssert<SELF, ACTUAL>, ACTUAL extends SortedSet>
         extends CollectionAssert<SELF, ACTUAL> {
      public SortedSetAssert( final ACTUAL actual ) {
         this( actual, SortedSetAssert.class, "SortedSet" );
      }

      protected SortedSetAssert( final ACTUAL actual, final Class<?> selfType, final String modelElementType ) {
         super( actual, selfType, modelElementType );
      }
   }

   public SELF isSortedSet() {
      assertThat( actual ).isInstanceOf( SortedSet.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends SortedSetAssert<S, A>, A extends SortedSet> SortedSetAssert<S, A> isSortedSetThat() {
      isSortedSet();
      return new SortedSetAssert<>( (A) actual );
   }

   /**
    * Assert for {@link TimeSeries}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class TimeSeriesAssert<SELF extends TimeSeriesAssert<SELF, ACTUAL>, ACTUAL extends TimeSeries>
         extends SortedSetAssert<SELF, ACTUAL> {
      public TimeSeriesAssert( final ACTUAL actual ) {
         super( actual, TimeSeriesAssert.class, "TimeSeries" );
      }
   }

   public SELF isTimeSeries() {
      assertThat( actual ).isInstanceOf( TimeSeries.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends TimeSeriesAssert<S, A>, A extends TimeSeries> TimeSeriesAssert<S, A> isTimeSeriesThat() {
      isTimeSeries();
      return new TimeSeriesAssert<>( (A) actual );
   }

   /**
    * Assert for {@link Code}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class CodeAssert<SELF extends CodeAssert<SELF, ACTUAL>, ACTUAL extends Code>
         extends AbstractCharacteristicAssert<SELF, ACTUAL> {
      public CodeAssert( final ACTUAL actual ) {
         super( actual, CodeAssert.class, "Code" );
      }
   }

   public SELF isCode() {
      assertThat( actual ).isInstanceOf( Code.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends CodeAssert<S, A>, A extends Code> CodeAssert<S, A> isCodeThat() {
      isCode();
      return new CodeAssert<>( (A) actual );
   }

   /**
    * Assert for {@link Either}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class EitherAssert<SELF extends EitherAssert<SELF, ACTUAL>, ACTUAL extends Either>
         extends AbstractCharacteristicAssert<SELF, ACTUAL> {
      public EitherAssert( final ACTUAL actual ) {
         super( actual, EitherAssert.class, "Either" );
      }

      public SELF hasLeft( final Characteristic characteristic ) {
         assertThat( actual.getLeft() ).isEqualTo( characteristic );
         return myself;
      }

      @SuppressWarnings( "unchecked" )
      public <S extends CharacteristicAssert<S, A>, A extends Characteristic> CharacteristicAssert<S, A> left() {
         return new CharacteristicAssert<>( (A) actual.getLeft() );
      }

      public SELF hasRight( final Characteristic characteristic ) {
         assertThat( actual.getRight() ).isEqualTo( characteristic );
         return myself;
      }

      @SuppressWarnings( "unchecked" )
      public <S extends CharacteristicAssert<S, A>, A extends Characteristic> CharacteristicAssert<S, A> right() {
         return new CharacteristicAssert<>( (A) actual.getRight() );
      }
   }

   public SELF isEither() {
      assertThat( actual ).isInstanceOf( Either.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends EitherAssert<S, A>, A extends Either> EitherAssert<S, A> isEitherThat() {
      isEither();
      return new EitherAssert<>( (A) actual );
   }

   /**
    * Assert for {@link SingleEntity}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class SingleEntityAssert<SELF extends SingleEntityAssert<SELF, ACTUAL>, ACTUAL extends SingleEntity>
         extends AbstractCharacteristicAssert<SELF, ACTUAL> {
      public SingleEntityAssert( final ACTUAL actual ) {
         super( actual, SingleEntityAssert.class, "SingleEntity" );
      }
   }

   public SELF isSingleEntity() {
      assertThat( actual ).isInstanceOf( SingleEntity.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends SingleEntityAssert<S, A>, A extends SingleEntity> SingleEntityAssert<S, A> isSingleEntityThat() {
      isSingleEntity();
      return new SingleEntityAssert<>( (A) actual );
   }

   /**
    * Assert for {@link StructuredValue}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class StructuredValueAssert<SELF extends StructuredValueAssert<SELF, ACTUAL>, ACTUAL extends StructuredValue>
         extends AbstractCharacteristicAssert<SELF, ACTUAL> {
      public StructuredValueAssert( final ACTUAL actual ) {
         super( actual, StructuredValueAssert.class, "StructuredValue" );
      }

      public SELF hasDeconstructionRule( final String deconstructionRule ) {
         assertThat( actual.getDeconstructionRule() ).isEqualTo( deconstructionRule );
         return myself;
      }

      public SELF hasDeconstructionRuleMatching( final String string ) {
         assertThat( string ).matches( actual.getDeconstructionRule() );
         return myself;
      }

      @SuppressWarnings( "unchecked" )
      public <S extends AbstractStringAssert<S>> AbstractStringAssert<S> deconstructionRule() {
         return (AbstractStringAssert<S>) assertThat( actual.getDeconstructionRule() );
      }

      public org.assertj.core.api.ListAssert<Object> elements() {
         return assertThat( actual.getElements() );
      }
   }

   public SELF isStructuredValue() {
      assertThat( actual ).isInstanceOf( StructuredValue.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends StructuredValueAssert<S, A>, A extends StructuredValue> StructuredValueAssert<S, A> isStructuredValueThat() {
      isStructuredValue();
      return new StructuredValueAssert<>( (A) actual );
   }
}
