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

package org.eclipse.esmf.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.ObjectAssert;

/**
 * Assert for {@link ScalarValue}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class ScalarValueAssert<SELF extends ScalarValueAssert<SELF, ACTUAL>, ACTUAL extends ScalarValue>
      extends AbstractAssert<SELF, ACTUAL> {
   protected ScalarValueAssert( final ACTUAL actual ) {
      super( actual, ScalarValueAssert.class );
   }

   public SELF hasType( final Type type ) {
      if ( !type.isScalar() ) {
         failWithMessage( "Expected type <%s> to be scalar, but it wasn't", type.getUrn() );
      }
      return hasType( (Scalar) type );
   }

   public SELF hasType( final Scalar type ) {
      assertThat( actual.getType() ).isEqualTo( type );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends ScalarAssert<S, A>, A extends Scalar> ScalarAssert<S, A> type() {
      hasType( actual.getType() );
      return new ScalarAssert<>( (A) actual.getType() );
   }

   public SELF hasValue( final Object value ) {
      value().isEqualTo( value );
      return myself;
   }

   public ObjectAssert<Object> value() {
      return assertThat( actual.getValue() );
   }

   public SELF canBeCastTo( final Type other ) {
      if ( !other.isScalar() || !actual.getType().isTypeOrSubtypeOf( other ) ) {
         failWithMessage( "Expected value's type <%s> to be the castable to <%s>, but it wasn't", actual.getType().urn(),
               other.getUrn() );
      }
      return myself;
   }
}
