/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.metamodel;

/**
 * Marker interface which defines types to be used as data type of a {@link Characteristic}.
 */
public interface Type extends ModelElement {
   /**
    * @return the URN which identifies the type
    */
   String getUrn();

   default boolean isScalar() {
      return false;
   }

   default boolean isComplexType() {
      return false;
   }
}


