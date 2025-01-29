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

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;

import org.assertj.core.api.ListAssert;
import org.assertj.core.api.OptionalAssert;

/**
 * Assert for {@link Aspect}s.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class AspectAssert<SELF extends AspectAssert<SELF, ACTUAL>, ACTUAL extends Aspect>
      extends NamedElementAssert<SELF, ACTUAL> {
   public AspectAssert( final ACTUAL actual ) {
      super( actual, AspectAssert.class, "Aspect" );
   }

   public SELF isCollectionAspect() {
      isNotNull();
      if ( !actual.isCollectionAspect() ) {
         failWithMessage( "Expected <%s> to be a collection Aspect, but it wasn't", actual.urn() );
      }
      return myself;
   }

   public SELF isNoCollectionAspect() {
      isNotNull();
      if ( actual.isCollectionAspect() ) {
         failWithMessage( "Expected <%s> to not be a collection Aspect, but it was", actual.urn() );
      }
      return myself;
   }

   /**
    * Assumes that the Aspect has exactly one Property, and returns the assert for it
    */
   @SuppressWarnings( "unchecked" )
   public <S extends PropertyAssert<S, A>, A extends Property> PropertyAssert<S, A> hasSinglePropertyThat() {
      assertThat( actual.getProperties() ).hasSize( 1 );
      return new PropertyAssert<>( (A) actual.getProperties().get( 0 ) );
   }

   public ListAssert<Property> properties() {
      return assertThat( actual.getProperties() );
   }

   public SELF hasNoProperties() {
      assertThat( actual.getProperties() ).isEmpty();
      return myself;
   }

   public OptionalAssert<Property> propertyByName( final String name ) {
      return assertThat( actual.getPropertyByName( name ) );
   }

   public ListAssert<Operation> operations() {
      return assertThat( actual.getOperations() );
   }

   public SELF hasNoOperations() {
      assertThat( actual.getOperations() ).isEmpty();
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends OperationAssert<S, A>, A extends Operation> OperationAssert<S, A> hasSingleOperationThat() {
      assertThat( actual.getOperations() ).hasSize( 1 );
      return new OperationAssert<>( (A) actual.getOperations().get( 0 ) );
   }

   public ListAssert<Event> events() {
      return assertThat( actual.getEvents() );
   }

   public SELF hasNoEvents() {
      assertThat( actual.getEvents() ).isEmpty();
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends EventAssert<S, A>, A extends Event> EventAssert<S, A> hasSingleEventThat() {
      assertThat( actual.getEvents() ).hasSize( 1 );
      return new EventAssert<>( (A) actual.getEvents().get( 0 ) );
   }
}
