/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.staticmetamodel;

import java.util.function.Function;

/**
 * Interface for any function that provides a property value access.
 *
 * @param <C> the type containing the property
 * @param <T> the type of the property
 * @see StaticProperty
 */

public interface PropertyAccessor<C, T> extends Function<C, T> {

   /**
    * Performs the property access and returns its value.
    *
    * @param object the instance containing the property
    * @return the property value of the given instance.
    */
   T getValue( C object );

   @Override
   default T apply( C object ) {
      return getValue( object );
   }
}
