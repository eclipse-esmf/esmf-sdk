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

import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Property;

import org.assertj.core.api.ListAssert;
import org.assertj.core.api.OptionalAssert;

/**
 * Base class for {@link ComplexType} asserts.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public abstract class AbstractComplexTypeAssert<SELF extends AbstractComplexTypeAssert<SELF, ACTUAL>, ACTUAL extends ComplexType>
      extends NamedElementAssert<SELF, ACTUAL> {
   public AbstractComplexTypeAssert( final ACTUAL actual, final Class<?> selfType, final String modelElementType ) {
      super( actual, selfType, modelElementType );
   }

   /**
    * Assumes that the complex type has exactly one Property, and returns the assert for it
    */
   @SuppressWarnings( "unchecked" )
   public <S extends PropertyAssert<S, A>, A extends Property> PropertyAssert<S, A> hasSinglePropertyThat() {
      assertThat( actual.getProperties() ).hasSize( 1 );
      return new PropertyAssert<>( (A) actual.getProperties().get( 0 ) );
   }

   public ListAssert<Property> properties() {
      return assertThat( actual.getProperties() );
   }

   public ListAssert<Property> allProperties() {
      return assertThat( actual.getAllProperties() );
   }

   public OptionalAssert<Property> propertyByName( final String name ) {
      return assertThat( actual.getPropertyByName( name ) );
   }

   public ListAssert<ComplexType> extendingElements() {
      return assertThat( actual.getExtendingElements() );
   }

   public ListAssert<ComplexType> allSuperTypes() {
      return assertThat( actual.getAllSuperTypes() );
   }

   public OptionalAssert<ComplexType> superType() {
      return assertThat( actual.getExtends() );
   }
}
