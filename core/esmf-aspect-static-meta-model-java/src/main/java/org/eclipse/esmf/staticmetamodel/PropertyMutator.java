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

/**
 * Interface for any class that can modify property values.
 *
 * @param <C> the type containing the property
 * @param <T> the type of the property
 * @see StaticProperty
 */

public interface PropertyMutator<C, T> {

   /**
    * Sets a new value for the property.
    *
    * @param object the instance containing the property
    * @param value the new value to set
    */
   void setValue( C object, T value );
}
