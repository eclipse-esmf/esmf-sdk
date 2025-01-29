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

import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.CollectionAssert;

/**
 * Assert for {@link CollectionValue}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class CollectionValueAssert<SELF extends CollectionValueAssert<SELF, ACTUAL>, ACTUAL extends CollectionValue>
      extends AbstractAssert<SELF, ACTUAL> {
   public CollectionValueAssert( final ACTUAL actual ) {
      super( actual, CollectionValueAssert.class );
   }

   public SELF hasCollectionType( final CollectionValue.CollectionType collectionType ) {
      assertThat( actual.getCollectionType() ).isEqualTo( collectionType );
      return myself;
   }

   public SELF hasType( final Type type ) {
      assertThat( actual.getType() ).isEqualTo( type );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends TypeAssert<S, A>, A extends Type> TypeAssert<S, A> type() {
      return new TypeAssert<>( (A) actual.getType() );
   }

   public CollectionAssert<Value> values() {
      return new CollectionAssert<>( actual.getValues() );
   }
}
