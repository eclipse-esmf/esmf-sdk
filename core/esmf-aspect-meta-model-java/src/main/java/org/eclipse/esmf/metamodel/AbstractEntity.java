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
 * Represents a generic domain specific concept which may have multiple specific variants.
 *
 * @since SAMM 2.0.0
 */
public interface AbstractEntity extends ComplexType {

   @Override
   default boolean isAbstractEntity() {
      return true;
   }
}
