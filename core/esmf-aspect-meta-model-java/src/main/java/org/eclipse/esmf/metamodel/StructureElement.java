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
 * Represents structural model elements, i.e. that are named and have {@link Property}s
 */
public interface StructureElement extends NamedElement, HasProperties {
   default boolean isComplexType() {
      return false;
   }

   /**
    * @return a {@code boolean} which indicates whether the structure element is a Collection Aspect
    */
   default boolean isCollectionAspect() {
      return false;
   }
}
