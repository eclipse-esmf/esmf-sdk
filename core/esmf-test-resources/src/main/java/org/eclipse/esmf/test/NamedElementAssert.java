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

import org.eclipse.esmf.metamodel.ModelElement;

/**
 * Base class for named {@link ModelElement} asserts.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public abstract class NamedElementAssert<SELF extends NamedElementAssert<SELF, ACTUAL>, ACTUAL extends ModelElement>
      extends ModelElementAssert<SELF, ACTUAL> {
   protected NamedElementAssert( final ACTUAL actual, final Class<?> selfType, final String modelElementType ) {
      super( actual, selfType, modelElementType );
   }

   @Override
   public SELF isAnonymous() {
      throw new UnsupportedOperationException( modelElementType + "s may not be anonymous" );
   }
}
