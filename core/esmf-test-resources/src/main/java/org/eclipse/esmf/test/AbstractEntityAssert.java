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

import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Entity;

/**
 * Assert for {@link AbstractEntity}. Note that this class is not abstract, because it's an Assert for "{@link AbstractEntity}",
 * not an abstract  Assert for "{@link Entity}".
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type.
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class AbstractEntityAssert<SELF extends AbstractEntityAssert<SELF, ACTUAL>, ACTUAL extends AbstractEntity>
      extends AbstractComplexTypeAssert<SELF, ACTUAL> {
   public AbstractEntityAssert( final ACTUAL actual ) {
      super( actual, AbstractEntityAssert.class, "AbstractEntity" );
   }
}
