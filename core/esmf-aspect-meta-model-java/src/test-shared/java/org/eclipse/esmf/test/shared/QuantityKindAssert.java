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

import org.eclipse.esmf.metamodel.QuantityKind;

/**
 * Assert for {@link QuantityKind}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class QuantityKindAssert<SELF extends QuantityKindAssert<SELF, ACTUAL>, ACTUAL extends QuantityKind>
      extends ModelElementAssert<SELF, ACTUAL> {
   public QuantityKindAssert( final ACTUAL actual ) {
      super( actual, QuantityKindAssert.class, "QuantityKind" );
   }

   public SELF hasLabel( final String label ) {
      assertThat( actual.getLabel() ).isEqualTo( label );
      return myself;
   }
}
