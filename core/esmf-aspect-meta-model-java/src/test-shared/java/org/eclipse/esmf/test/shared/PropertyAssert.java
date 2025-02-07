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
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.ScalarValue;

import org.assertj.core.api.AbstractStringAssert;

/**
 * Assert for {@link Property}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class PropertyAssert<SELF extends PropertyAssert<SELF, ACTUAL>, ACTUAL extends Property>
      extends ModelElementAssert<SELF, ACTUAL> {
   public PropertyAssert( final ACTUAL modelElement ) {
      super( modelElement, PropertyAssert.class, "Property" );
   }

   public SELF isOptional() {
      assertThat( actual.isOptional() ).isTrue();
      return myself;
   }

   public SELF isMandatory() {
      assertThat( actual.isOptional() ).isFalse();
      return myself;
   }

   public SELF isNotOptional() {
      return isMandatory();
   }

   public SELF isAbstract() {
      assertThat( actual.isAbstract() ).isTrue();
      return myself;
   }

   public SELF isNotAbstract() {
      assertThat( actual.isAbstract() ).isFalse();
      return myself;
   }

   public SELF isConcrete() {
      return isNotAbstract();
   }

   public SELF isNotInPayload() {
      assertThat( actual.isNotInPayload() ).isTrue();
      return myself;
   }

   public SELF isInPayload() {
      assertThat( actual.isNotInPayload() ).isFalse();
      return myself;
   }

   public SELF hasCharacteristic( final Characteristic characteristic ) {
      assertThat( actual.getCharacteristic() ).isPresent().contains( characteristic );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends CharacteristicAssert<S, A>, A extends Characteristic> CharacteristicAssert<S, A> characteristic() {
      assertThat( actual.getCharacteristic() ).isPresent();
      return new CharacteristicAssert<>( (A) actual.getCharacteristic().orElseThrow() );
   }

   public SELF hasPayloadName( final String payloadName ) {
      assertThat( actual.getPayloadName() ).isEqualTo( payloadName );
      return myself;
   }

   public AbstractStringAssert<?> payloadName() {
      return assertThat( actual.getPayloadName() );
   }

   public SELF hasNoExampleValue() {
      assertThat( actual.getExampleValue() ).isEmpty();
      return myself;
   }

   public SELF hasSomeExampleValue() {
      assertThat( actual.getExampleValue() ).isNotEmpty();
      return myself;
   }

   public SELF hasExampleValue( final ScalarValue exampleValue ) {
      assertThat( actual.getExampleValue() ).isPresent().contains( exampleValue );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends ScalarValueAssert<S, A>, A extends ScalarValue> ScalarValueAssert<S, A> exampleValue() {
      hasSomeExampleValue();
      return new ScalarValueAssert<>( (A) actual.getExampleValue().orElseThrow() );
   }

   public SELF extendsSomeProperty() {
      assertThat( actual.getExtends() ).isNotEmpty();
      return myself;
   }

   public SELF extendsProperty( final Property superProperty ) {
      assertThat( actual.getExtends() ).isPresent().contains( superProperty );
      return myself;
   }

   @SuppressWarnings( { "unchecked", "NewMethodNamingConvention" } )
   public <S extends PropertyAssert<S, A>, A extends Property> PropertyAssert<S, A> extends_() {
      extendsSomeProperty();
      return new PropertyAssert<>( (A) actual.getExtends().orElseThrow() );
   }
}
