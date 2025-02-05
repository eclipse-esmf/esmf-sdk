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

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Namespace;
import org.eclipse.esmf.metamodel.Operation;
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
import org.eclipse.esmf.metamodel.characteristic.Either;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.characteristic.List;
import org.eclipse.esmf.metamodel.characteristic.Measurement;
import org.eclipse.esmf.metamodel.characteristic.Quantifiable;
import org.eclipse.esmf.metamodel.characteristic.Set;
import org.eclipse.esmf.metamodel.characteristic.SingleEntity;
import org.eclipse.esmf.metamodel.characteristic.SortedSet;
import org.eclipse.esmf.metamodel.characteristic.StructuredValue;
import org.eclipse.esmf.metamodel.characteristic.TimeSeries;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.constraint.EncodingConstraint;
import org.eclipse.esmf.metamodel.constraint.FixedPointConstraint;
import org.eclipse.esmf.metamodel.constraint.LanguageConstraint;
import org.eclipse.esmf.metamodel.constraint.LengthConstraint;
import org.eclipse.esmf.metamodel.constraint.LocaleConstraint;
import org.eclipse.esmf.metamodel.constraint.RangeConstraint;
import org.eclipse.esmf.metamodel.constraint.RegularExpressionConstraint;

import org.apache.jena.rdf.model.Model;
import org.assertj.core.api.InstanceOfAssertFactory;

/**
 * Convenience wrapper for the construction of all SAMM-related assertsThat() methods. Intended use is by
 * {@code import static org.eclipse.esmf.test.AspectModelAsserts.assertThat;}.
 */
// Note that this class uses a lot of @SuppressWarnings( "IncorrectFormatting" ); for some reason IntelliJ code style does not
// use correct continuation indents.
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class AspectModelAsserts {
   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends AbstractEntityAssert<SELF, ACTUAL>, ACTUAL extends AbstractEntity> AbstractEntityAssert<SELF, ACTUAL>
         assertThat( final ACTUAL abstractEntity ) {
      //@formatter:on
      return new AbstractEntityAssert<>( abstractEntity );
   }

   public static <SELF extends AspectAssert<SELF, ACTUAL>, ACTUAL extends Aspect> AspectAssert<SELF, ACTUAL> assertThat(
         final ACTUAL aspect ) {
      return new AspectAssert<>( aspect );
   }

   public static <SELF extends AspectModelAssert<SELF, ACTUAL>, ACTUAL extends AspectModel> AspectModelAssert<SELF, ACTUAL> assertThat(
         final ACTUAL aspectModel ) {
      return new AspectModelAssert<>( aspectModel );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends AspectModelFileAssert<SELF, ACTUAL>, ACTUAL extends AspectModelFile> AspectModelFileAssert<SELF, ACTUAL>
         assertThat( final ACTUAL aspectModelFile ) {
      //@formatter:on
      return new AspectModelFileAssert<>( aspectModelFile );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends AspectModelUrnAssert<SELF, ACTUAL>, ACTUAL extends AspectModelUrn> AspectModelUrnAssert<SELF, ACTUAL>
         assertThat( final ACTUAL aspectModelUrn ) {
      //@formatter:on
      return new AspectModelUrnAssert<>( aspectModelUrn );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CharacteristicAssert<SELF, ACTUAL>, ACTUAL extends Characteristic> CharacteristicAssert<SELF, ACTUAL>
         assertThat( final ACTUAL characteristic ) {
      //@formatter:on
      return new CharacteristicAssert<>( characteristic );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CollectionValueAssert<SELF, ACTUAL>, ACTUAL extends CollectionValue> CollectionValueAssert<SELF, ACTUAL>
         assertThat( final ACTUAL collectionValue ) {
      //@formatter:on
      return new CollectionValueAssert<>( collectionValue );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends ConstraintAssert<SELF, ACTUAL>, ACTUAL extends Constraint> ConstraintAssert<SELF, ACTUAL>
         assertThat( final ACTUAL constraint ) {
      //@formatter:on
      return new ConstraintAssert<>( constraint );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends TypeAssert<SELF, ACTUAL>, ACTUAL extends Type> TypeAssert<SELF, ACTUAL>
         assertThat( final ACTUAL dataType ) {
      //@formatter:on
      return new TypeAssert<>( dataType );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends EntityAssert<SELF, ACTUAL>, ACTUAL extends Entity> EntityAssert<SELF, ACTUAL>
         assertThat( final ACTUAL entity ) {
      //@formatter:on
      return new EntityAssert<>( entity );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends EntityInstanceAssert<SELF, ACTUAL>, ACTUAL extends EntityInstance> EntityInstanceAssert<SELF, ACTUAL>
         assertThat( final ACTUAL entityInstance ) {
      //@formatter:on
      return new EntityInstanceAssert<>( entityInstance );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends EventAssert<SELF, ACTUAL>, ACTUAL extends Event> EventAssert<SELF, ACTUAL>
         assertThat( final ACTUAL event ) {
      //@formatter:on
      return new EventAssert<>( event );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends ModelElementAssert<SELF, ACTUAL>, ACTUAL extends ModelElement> ModelElementAssert<SELF, ACTUAL>
         assertThat( final ACTUAL modelElement ) {
      //@formatter:on
      return new ModelElementAssert<>( modelElement );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends NamespaceAssert<SELF, ACTUAL>, ACTUAL extends Namespace> NamespaceAssert<SELF, ACTUAL>
         assertThat( final ACTUAL namespace ) {
      //@formatter:on
      return new NamespaceAssert<>( namespace );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends OperationAssert<SELF, ACTUAL>, ACTUAL extends Operation> OperationAssert<SELF, ACTUAL>
         assertThat( final ACTUAL operation ) {
      //@formatter:on
      return new OperationAssert<>( operation );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends PropertyAssert<SELF, ACTUAL>, ACTUAL extends Property> PropertyAssert<SELF, ACTUAL>
         assertThat( final ACTUAL property ) {
      //@formatter:on
      return new PropertyAssert<>( property );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends QuantityKindAssert<SELF, ACTUAL>, ACTUAL extends QuantityKind> QuantityKindAssert<SELF, ACTUAL>
         assertThat( final ACTUAL quantityKind ) {
      //@formatter:on
      return new QuantityKindAssert<>( quantityKind );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends ScalarAssert<SELF, ACTUAL>, ACTUAL extends Scalar> ScalarAssert<SELF, ACTUAL>
         assertThat( final ACTUAL scalar ) {
      //@formatter:on
      return new ScalarAssert<>( scalar );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends ScalarValueAssert<SELF, ACTUAL>, ACTUAL extends ScalarValue> ScalarValueAssert<SELF, ACTUAL>
         assertThat( final ACTUAL scalarValue ) {
      //@formatter:on
      return new ScalarValueAssert<>( scalarValue );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends UnitAssert<SELF, ACTUAL>, ACTUAL extends Unit> UnitAssert<SELF, ACTUAL>
         assertThat( final ACTUAL unit ) {
      //@formatter:on
      return new UnitAssert<>( unit );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends ValueAssert<SELF, ACTUAL>, ACTUAL extends Value> ValueAssert<SELF, ACTUAL>
         assertThat( final ACTUAL value ) {
      //@formatter:on
      return new ValueAssert<>( value );
   }

   public static <SELF extends ModelAssert<SELF, ACTUAL>, ACTUAL extends Model> ModelAssert<SELF, ACTUAL> assertThat( final ACTUAL model ) {
      return new ModelAssert<>( model );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CharacteristicAssert.TraitAssert<SELF, ACTUAL>, ACTUAL extends Trait>
         CharacteristicAssert.TraitAssert<SELF, ACTUAL> assertThat( final ACTUAL trait ) {
      //@formatter:on
      return new CharacteristicAssert.TraitAssert<>( trait );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CharacteristicAssert.QuantifiableAssert<SELF, ACTUAL>, ACTUAL extends Quantifiable>
         CharacteristicAssert.QuantifiableAssert<SELF, ACTUAL> assertThat( final ACTUAL quantifiable ) {
      //@formatter:on
      return new CharacteristicAssert.QuantifiableAssert<>( quantifiable );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CharacteristicAssert.MeasurementAssert<SELF, ACTUAL>, ACTUAL extends Measurement>
         CharacteristicAssert.MeasurementAssert<SELF, ACTUAL> assertThat( final ACTUAL measurement ) {
      //@formatter:on
      return new CharacteristicAssert.MeasurementAssert<>( measurement );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CharacteristicAssert.EnumerationAssert<SELF, ACTUAL>, ACTUAL extends Enumeration>
         CharacteristicAssert.EnumerationAssert<SELF, ACTUAL> assertThat( final ACTUAL enumeration ) {
      //@formatter:on
      return new CharacteristicAssert.EnumerationAssert<>( enumeration );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CharacteristicAssert.DurationAssert<SELF, ACTUAL>, ACTUAL extends Duration>
         CharacteristicAssert.DurationAssert<SELF, ACTUAL> assertThat( final ACTUAL duration ) {
      //@formatter:on
      return new CharacteristicAssert.DurationAssert<>( duration );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CharacteristicAssert.CollectionAssert<SELF, ACTUAL>, ACTUAL extends Collection>
         CharacteristicAssert.CollectionAssert<SELF, ACTUAL> assertThat( final ACTUAL collection ) {
      //@formatter:on
      return new CharacteristicAssert.CollectionAssert<>( collection );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CharacteristicAssert.ListAssert<SELF, ACTUAL>, ACTUAL extends List>
         CharacteristicAssert.ListAssert<SELF, ACTUAL> assertThat( final ACTUAL list ) {
      //@formatter:on
      return new CharacteristicAssert.ListAssert<>( list );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CharacteristicAssert.SetAssert<SELF, ACTUAL>, ACTUAL extends Set>
         CharacteristicAssert.SetAssert<SELF, ACTUAL> assertThat( final ACTUAL set ) {
      //@formatter:on
      return new CharacteristicAssert.SetAssert<>( set );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CharacteristicAssert.SortedSetAssert<SELF, ACTUAL>, ACTUAL extends SortedSet>
         CharacteristicAssert.SortedSetAssert<SELF, ACTUAL> assertThat( final ACTUAL sortedSet ) {
      //@formatter:on
      return new CharacteristicAssert.SortedSetAssert<>( sortedSet );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CharacteristicAssert.TimeSeriesAssert<SELF, ACTUAL>, ACTUAL extends TimeSeries>
         CharacteristicAssert.TimeSeriesAssert<SELF, ACTUAL> assertThat( final ACTUAL timeSeries ) {
      //@formatter:on
      return new CharacteristicAssert.TimeSeriesAssert<>( timeSeries );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CharacteristicAssert.CodeAssert<SELF, ACTUAL>, ACTUAL extends Code>
         CharacteristicAssert.CodeAssert<SELF, ACTUAL> assertThat( final ACTUAL code ) {
      //@formatter:on
      return new CharacteristicAssert.CodeAssert<>( code );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CharacteristicAssert.EitherAssert<SELF, ACTUAL>, ACTUAL extends Either>
         CharacteristicAssert.EitherAssert<SELF, ACTUAL> assertThat( final ACTUAL either ) {
      //@formatter:on
      return new CharacteristicAssert.EitherAssert<>( either );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CharacteristicAssert.SingleEntityAssert<SELF, ACTUAL>, ACTUAL extends SingleEntity>
         CharacteristicAssert.SingleEntityAssert<SELF, ACTUAL> assertThat( final ACTUAL element ) {
      //@formatter:on
      return new CharacteristicAssert.SingleEntityAssert<>( element );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends CharacteristicAssert.StructuredValueAssert<SELF, ACTUAL>, ACTUAL extends StructuredValue>
         CharacteristicAssert.StructuredValueAssert<SELF, ACTUAL> assertThat( final ACTUAL structuredValue ) {
      //@formatter:on
      return new CharacteristicAssert.StructuredValueAssert<>( structuredValue );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends ConstraintAssert.LanguageConstraintAssert<SELF, ACTUAL>, ACTUAL extends LanguageConstraint>
         ConstraintAssert.LanguageConstraintAssert<SELF, ACTUAL> assertThat( final ACTUAL element ) {
      //@formatter:on
      return new ConstraintAssert.LanguageConstraintAssert<>( element );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends ConstraintAssert.LocaleConstraintAssert<SELF, ACTUAL>, ACTUAL extends LocaleConstraint>
         ConstraintAssert.LocaleConstraintAssert<SELF, ACTUAL> assertThat( final ACTUAL element ) {
      //@formatter:on
      return new ConstraintAssert.LocaleConstraintAssert<>( element );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends ConstraintAssert.RangeConstraintAssert<SELF, ACTUAL>, ACTUAL extends RangeConstraint>
         ConstraintAssert.RangeConstraintAssert<SELF, ACTUAL> assertThat( final ACTUAL element ) {
      //@formatter:on
      return new ConstraintAssert.RangeConstraintAssert<>( element );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends ConstraintAssert.EncodingConstraintAssert<SELF, ACTUAL>, ACTUAL extends EncodingConstraint>
         ConstraintAssert.EncodingConstraintAssert<SELF, ACTUAL> assertThat( final ACTUAL element ) {
      //@formatter:on
      return new ConstraintAssert.EncodingConstraintAssert<>( element );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends ConstraintAssert.LengthConstraintAssert<SELF, ACTUAL>, ACTUAL extends LengthConstraint>
         ConstraintAssert.LengthConstraintAssert<SELF, ACTUAL> assertThat( final ACTUAL element ) {
      //@formatter:on
      return new ConstraintAssert.LengthConstraintAssert<>( element );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends ConstraintAssert.RegularExpressionConstraintAssert<SELF, ACTUAL>, ACTUAL extends RegularExpressionConstraint>
         ConstraintAssert.RegularExpressionConstraintAssert<SELF, ACTUAL> assertThat( final ACTUAL element ) {
      //@formatter:on
      return new ConstraintAssert.RegularExpressionConstraintAssert<>( element );
   }

   @SuppressWarnings( "IncorrectFormatting" )
   //@formatter:off
   public static <SELF extends ConstraintAssert.FixedPointConstraintAssert<SELF, ACTUAL>, ACTUAL extends FixedPointConstraint>
         ConstraintAssert.FixedPointConstraintAssert<SELF, ACTUAL> assertThat( final ACTUAL element ) {
      //@formatter:on
      return new ConstraintAssert.FixedPointConstraintAssert<>( element );
   }

   // Assertion factories that are required when asserts for collections should be chained with their actual type, e.g.:
   // assertThat( aspectModel ).namespaces().first( as( NAMESPACE ) ).hasName( "urn:samm:org.eclipse.esmf.test:1.0.0" );
   // Without the InstanceOfAssertFactory, first() on a ListAssert would only provide a generic ObjectAssert;
   // with it the result of first() is an actual NamespaceAssert.

   public static final InstanceOfAssertFactory<AbstractEntity, AbstractEntityAssert<?, ?>> ABSTRACT_ENTITY =
         new InstanceOfAssertFactory<>( AbstractEntity.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<Aspect, AspectAssert<?, ?>> ASPECT =
         new InstanceOfAssertFactory<>( Aspect.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<AspectModel, AspectModelAssert<?, ?>> ASPECT_MODEL =
         new InstanceOfAssertFactory<>( AspectModel.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<AspectModelFile, AspectModelFileAssert<?, ?>> ASPECT_MODEL_FILE =
         new InstanceOfAssertFactory<>( AspectModelFile.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<AspectModelUrn, AspectModelUrnAssert<?, ?>> ASPECT_MODEL_URN =
         new InstanceOfAssertFactory<>( AspectModelUrn.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<Characteristic, CharacteristicAssert<?, ?>> CHARACTERISTIC =
         new InstanceOfAssertFactory<>( Characteristic.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<CollectionValue, CollectionValueAssert<?, ?>> COLLECTION_VALUE =
         new InstanceOfAssertFactory<>( CollectionValue.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<Constraint, ConstraintAssert<?, ?>> CONSTRAINT =
         new InstanceOfAssertFactory<>( Constraint.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<Entity, EntityAssert<?, ?>> ENTITY =
         new InstanceOfAssertFactory<>( Entity.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<EntityInstance, EntityInstanceAssert<?, ?>> ENTITY_INSTANCE =
         new InstanceOfAssertFactory<>( EntityInstance.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<Event, EventAssert<?, ?>> EVENT =
         new InstanceOfAssertFactory<>( Event.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<Model, ModelAssert<?, ?>> MODEL =
         new InstanceOfAssertFactory<>( Model.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<ModelElement, ModelElementAssert<?, ?>> MODEL_ELEMENT =
         new InstanceOfAssertFactory<>( ModelElement.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<Namespace, NamespaceAssert<?, ?>> NAMESPACE =
         new InstanceOfAssertFactory<>( Namespace.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<Operation, OperationAssert<?, ?>> OPERATION =
         new InstanceOfAssertFactory<>( Operation.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<Property, PropertyAssert<?, ?>> PROPERTY =
         new InstanceOfAssertFactory<>( Property.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<QuantityKind, QuantityKindAssert<?, ?>> QUANTITY_KIND =
         new InstanceOfAssertFactory<>( QuantityKind.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<Scalar, ScalarAssert<?, ?>> SCALAR =
         new InstanceOfAssertFactory<>( Scalar.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<ScalarValue, ScalarValueAssert<?, ?>> SCALAR_VALUE =
         new InstanceOfAssertFactory<>( ScalarValue.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<Type, TypeAssert<?, ?>> TYPE =
         new InstanceOfAssertFactory<>( Type.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<Unit, UnitAssert<?, ?>> UNIT =
         new InstanceOfAssertFactory<>( Unit.class, AspectModelAsserts::assertThat );
   public static final InstanceOfAssertFactory<Value, ValueAssert<?, ?>> VALUE =
         new InstanceOfAssertFactory<>( Value.class, AspectModelAsserts::assertThat );
}
