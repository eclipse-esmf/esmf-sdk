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

import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.Property;

import org.assertj.core.api.ListAssert;

/**
 * Assert for {@link Event}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class EventAssert<SELF extends EventAssert<SELF, ACTUAL>, ACTUAL extends Event>
      extends ModelElementAssert<SELF, ACTUAL> {
   protected EventAssert( final ACTUAL actual ) {
      super( actual, EventAssert.class, "Event" );
   }

   @SuppressWarnings( "unchecked" )
   public <S extends PropertyAssert<S, A>, A extends Property> PropertyAssert<S, A> hasSinglePropertyThat() {
      assertThat( actual.getProperties() ).hasSize( 1 );
      return new PropertyAssert<>( (A) actual.getProperties().get( 0 ) );
   }

   public ListAssert<Property> properties() {
      return assertThat( actual.getProperties() );
   }
}
