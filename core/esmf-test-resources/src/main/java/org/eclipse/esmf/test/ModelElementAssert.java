/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;

/**
 * Base class for {@link ModelElement} asserts.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class ModelElementAssert<SELF extends ModelElementAssert<SELF, ACTUAL>, ACTUAL extends ModelElement>
      extends AbstractHasDescriptionAssert<SELF, ACTUAL> {
   protected final String modelElementType;

   public ModelElementAssert( final ACTUAL actual ) {
      this( actual, ModelElementAssert.class, "ModelElement" );
   }

   protected ModelElementAssert( final ACTUAL actual, final Class<?> selfType, final String modelElementType ) {
      super( actual, selfType );
      this.modelElementType = modelElementType;
   }

   public SELF hasName( final String name ) {
      assertThat( actual.getName() ).isEqualTo( name );
      return myself;
   }

   public SELF hasVersion( final String version ) {
      urn().hasVersion( version );
      return myself;
   }

   public SELF hasVersion( final VersionNumber version ) {
      urn().hasVersion( version.toString() );
      return myself;
   }

   public SELF hasUrn( final AspectModelUrn urn ) {
      urn().isEqualTo( urn );
      return myself;
   }

   public SELF hasUrn( final String urn ) {
      urn().hasToString( urn );
      return myself;
   }

   public SELF isAnonymous() {
      assertThat( actual.isAnonymous() ).isTrue();
      return myself;
   }

   public SELF isNamedElement() {
      assertThat( actual.isAnonymous() ).isFalse();
      return myself;
   }

   public SELF hasSameNamespaceAs( final ModelElement otherElement ) {
      urn().hasNamespaceMainPart( otherElement.urn().getNamespaceMainPart() )
            .hasVersion( otherElement.urn().getVersion() );
      return myself;
   }

   public SELF hasSourceFile( final AspectModelFile sourceFile ) {
      assertThat( actual.getSourceFile() ).isEqualTo( sourceFile );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends AspectModelFileAssert<S, A>, A extends AspectModelFile> AspectModelFileAssert<S, A> sourceFile() {
      return new AspectModelFileAssert<>( (A) actual.getSourceFile() );
   }

   @SuppressWarnings( "unchecked" )
   public <S extends AspectModelUrnAssert<S, A>, A extends AspectModelUrn> AspectModelUrnAssert<S, A> urn() {
      return new AspectModelUrnAssert<>( (A) actual.urn() );
   }

   public SELF isAspect() {
      assertThat( actual ).isInstanceOf( Aspect.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends AspectAssert<S, A>, A extends Aspect> AspectAssert<S, A> isAspectThat() {
      isAspect();
      return new AspectAssert<>( (A) actual );
   }

   public SELF isProperty() {
      assertThat( actual ).isInstanceOf( Property.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends PropertyAssert<S, A>, A extends Property> PropertyAssert<S, A> isPropertyThat() {
      isProperty();
      return new PropertyAssert<>( (A) actual );
   }

   public SELF isCharacteristic() {
      assertThat( actual ).isInstanceOf( Characteristic.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends CharacteristicAssert<S, A>, A extends Characteristic> CharacteristicAssert<S, A> isCharacteristicThat() {
      isCharacteristic();
      return new CharacteristicAssert<>( (A) actual );
   }

   public SELF isConstraint() {
      assertThat( actual ).isInstanceOf( Constraint.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends ConstraintAssert<S, A>, A extends Constraint> ConstraintAssert<S, A> isConstraintThat() {
      isConstraint();
      return new ConstraintAssert<>( (A) actual );
   }

   public SELF isOperation() {
      assertThat( actual ).isInstanceOf( Operation.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends OperationAssert<S, A>, A extends Operation> OperationAssert<S, A> isOperationThat() {
      isOperation();
      return new OperationAssert<>( (A) actual );
   }

   public SELF isEvent() {
      assertThat( actual ).isInstanceOf( Event.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends EventAssert<S, A>, A extends Event> EventAssert<S, A> isEventThat() {
      isEvent();
      return new EventAssert<>( (A) actual );
   }

   public SELF isAbstractEntity() {
      assertThat( actual ).isInstanceOf( AbstractEntity.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends AbstractEntityAssert<S, A>, A extends AbstractEntity> AbstractEntityAssert<S, A> isAbstractEntityThat() {
      isAbstractEntity();
      return new AbstractEntityAssert<>( (A) actual );
   }

   public SELF isEntity() {
      assertThat( actual ).isInstanceOf( Entity.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends EntityAssert<S, A>, A extends Entity> EntityAssert<S, A> isEntityThat() {
      isEntity();
      return new EntityAssert<>( (A) actual );
   }

   public SELF isCollectionValue() {
      assertThat( actual ).isInstanceOf( CollectionValue.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends CollectionValueAssert<S, A>, A extends CollectionValue> CollectionValueAssert<S, A> isCollectionValueThat() {
      isCollectionValue();
      return new CollectionValueAssert<>( (A) actual );
   }

   public SELF isScalarValue() {
      assertThat( actual ).isInstanceOf( ScalarValue.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends ScalarValueAssert<S, A>, A extends ScalarValue> ScalarValueAssert<S, A> isScalarValueThat() {
      isScalarValue();
      return new ScalarValueAssert<>( (A) actual );
   }

   public SELF isEntityInstance() {
      assertThat( actual ).isInstanceOf( EntityInstance.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends EntityInstanceAssert<S, A>, A extends EntityInstance> EntityInstanceAssert<S, A> isEntityInstanceThat() {
      isEntityInstance();
      return new EntityInstanceAssert<>( (A) actual );
   }

   public SELF isScalar() {
      assertThat( actual ).isInstanceOf( Scalar.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends ScalarAssert<S, A>, A extends Scalar> ScalarAssert<S, A> isScalarThat() {
      isScalar();
      return new ScalarAssert<>( (A) actual );
   }

   public SELF isType() {
      assertThat( actual ).isInstanceOf( Type.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends TypeAssert<S, A>, A extends Type> TypeAssert<S, A> isTypeThat() {
      isType();
      return new TypeAssert<>( (A) actual );
   }

   public SELF isValue() {
      assertThat( actual ).isInstanceOf( Value.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends ValueAssert<S, A>, A extends Value> ValueAssert<S, A> isValueThat() {
      isValue();
      return new ValueAssert<>( (A) actual );
   }
}
