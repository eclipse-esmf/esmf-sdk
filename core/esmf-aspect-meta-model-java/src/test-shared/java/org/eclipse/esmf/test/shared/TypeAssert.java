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

import org.eclipse.esmf.metamodel.Type;

/**
 * Assert for {@link Type}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class TypeAssert<SELF extends TypeAssert<SELF, ACTUAL>, ACTUAL extends Type>
      extends NamedElementAssert<SELF, ACTUAL> {
   public TypeAssert( final ACTUAL actual ) {
      super( actual, TypeAssert.class, "Type" );
   }

   public SELF isComplexType() {
      if ( !actual.isComplexType() ) {
         failWithMessage( "Expected %s <%s> to be a complex type, but it wasn't", modelElementType, actual.urn() );
      }
      return myself;
   }

   public SELF isTypeOrSuperTypeOf( final Type other ) {
      if ( !actual.isTypeOrSubtypeOf( other ) ) {
         failWithMessage( "Expected %s <%s> to be the same or a subtype of %s, but it wasn't", modelElementType, actual.urn(),
               other.getUrn() );
      }
      return myself;
   }
}
