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

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Scalar;

/**
 * Assert for {@link Scalar}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class ScalarAssert<SELF extends ScalarAssert<SELF, ACTUAL>, ACTUAL extends Scalar> extends ModelElementAssert<SELF, Scalar> {
   public ScalarAssert( final Scalar scalar ) {
      super( scalar, ScalarAssert.class, "Scalar" );
   }

   public SELF canBeCastTo( final Scalar other ) {
      if ( !actual.isTypeOrSubtypeOf( other ) ) {
         failWithMessage( "Expected %s <%s> to be the same or a subtype of %s, but it wasn't", modelElementType, actual.urn(),
               other.getUrn() );
      }
      return myself;
   }

   public SELF hasStringLikeValueSpace() {
      if ( !actual.hasStringLikeValueSpace() ) {
         failWithMessage( "Expected %s <%s> to have a string-like value space, but it hasn't", modelElementType, actual.urn() );
      }
      return myself;
   }

   @Override
   public SELF hasUrn( final String urn ) {
      assertThat( actual.getUrn() ).isEqualTo( urn );
      return myself;
   }

   @Override
   public SELF hasUrn( final AspectModelUrn urn ) {
      // XSD types are identified by URIs that are never valied AspectModelUrns
      throw new UnsupportedOperationException( "Scalar types can not have AspectModelUrn" );
   }
}
