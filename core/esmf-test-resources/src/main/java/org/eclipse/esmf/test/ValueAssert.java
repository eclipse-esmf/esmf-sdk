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
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;

/**
 * Assert for {@link Value}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class ValueAssert<SELF extends ValueAssert<SELF, ACTUAL>, ACTUAL extends Value>
      extends ModelElementAssert<SELF, ACTUAL> {
   protected ValueAssert( final ACTUAL actual ) {
      super( actual, ValueAssert.class, "Value" );
   }

   public SELF hasDataType( final Type type ) {
      assertThat( actual.getType() ).isEqualTo( type );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends TypeAssert<S, A>, A extends Type> TypeAssert<S, A> dataType() {
      return new TypeAssert<>( (A) actual.getType() );
   }

   @Override
   @SuppressWarnings( "unchecked" )
   public <S extends EntityInstanceAssert<S, A>, A extends EntityInstance> EntityInstanceAssert<S, A> isEntityInstanceThat() {
      if ( !( actual instanceof EntityInstance ) ) {
         failWithMessage( "Expected %s to be an Entity Instance, but it wasn't", modelElementType );
      }
      return new EntityInstanceAssert<>( (A) actual );
   }

   @Override
   @SuppressWarnings( "unchecked" )
   public <S extends ScalarValueAssert<S, A>, A extends ScalarValue> ScalarValueAssert<S, A> isScalarValueThat() {
      if ( !( actual instanceof ScalarValue ) ) {
         failWithMessage( "Expected %s to be a Scalar Value, but it wasn't", modelElementType );
      }
      return new ScalarValueAssert<>( (A) actual );
   }

   @Override
   @SuppressWarnings( "unchecked" )
   public <S extends CollectionValueAssert<S, A>, A extends CollectionValue> CollectionValueAssert<S, A> isCollectionValueThat() {
      if ( !( actual instanceof CollectionValue ) ) {
         failWithMessage( "Expected %s to be a Collection Value, but it wasn't", modelElementType );
      }
      return new CollectionValueAssert<>( (A) actual );
   }
}
