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

import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;

import org.assertj.core.api.ListAssert;
import org.assertj.core.api.OptionalAssert;

/**
 * Assert for {@link Operation}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class OperationAssert<SELF extends OperationAssert<SELF, ACTUAL>, ACTUAL extends Operation>
      extends ModelElementAssert<SELF, ACTUAL> {
   protected OperationAssert( final ACTUAL actual ) {
      super( actual, OperationAssert.class, "Operation" );
   }

   @SuppressWarnings( "unchecked" )
   public <S extends PropertyAssert<S, A>, A extends Property> PropertyAssert<S, A> hasSingleInputThat() {
      assertThat( actual.getInput() ).hasSize( 1 );
      return new PropertyAssert<>( (A) actual.getInput().get( 0 ) );
   }

   public ListAssert<Property> inputs() {
      return assertThat( actual.getInput() );
   }

   @SuppressWarnings( "unchecked" )
   public <S extends PropertyAssert<S, A>, A extends Property> PropertyAssert<S, A> hasOutputThat() {
      output().isPresent();
      return new PropertyAssert<>( (A) actual.getOutput().orElseThrow() );
   }

   public OptionalAssert<Property> output() {
      return assertThat( actual.getOutput() );
   }

   public SELF hasNoInputs() {
      assertThat( actual.getInput() ).isEmpty();
      return myself;
   }

   public SELF hasNoOutputs() {
      assertThat( actual.getOutput() ).isEmpty();
      return myself;
   }
}
