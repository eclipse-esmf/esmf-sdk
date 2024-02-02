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
 * Allows to enhance static properties with information about containing type and property type.
 *
 * @param <C> the containing type
 * @param <T> the property type
 */
public interface PropertyTypeInformation<C, T> {
   /**
    * @return the type of the Property represented as a class.
    */
   Class<T> getPropertyType();

   /**
    * @return the type of the class containing the Property, represented as a class
    */
   Class<C> getContainingType();
}
