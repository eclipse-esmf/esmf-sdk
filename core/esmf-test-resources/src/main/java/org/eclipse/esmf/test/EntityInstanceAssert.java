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

import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;

import org.assertj.core.api.MapAssert;

/**
 * Assert for {@link EntityInstance}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class EntityInstanceAssert<SELF extends EntityInstanceAssert<SELF, ACTUAL>, ACTUAL extends EntityInstance>
      extends NamedElementAssert<SELF, ACTUAL> {
   protected EntityInstanceAssert( final ACTUAL actual ) {
      super( actual, EntityInstanceAssert.class, "EntityInstance" );
   }

   public SELF hasType( final Type type ) {
      if ( !( type instanceof Entity ) ) {
         failWithMessage( "Expected type <%s> to be an Entity, but it wasn't", type.getUrn() );
      }
      return hasType( (Entity) type );
   }

   public SELF hasType( final Entity type ) {
      assertThat( actual.getType() ).isEqualTo( type );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends EntityAssert<S, A>, A extends Entity> EntityAssert<S, A> type() {
      hasType( actual.getType() );
      return new EntityAssert<>( (A) actual.getType() );
   }

   public SELF hasValueForProperty( final Property property ) {
      assertThat( actual.getAssertions().get( property ) ).isNotNull();
      return myself;
   }

   public MapAssert<Property, Value> assertions() {
      return assertThat( actual.getAssertions() );
   }
}
