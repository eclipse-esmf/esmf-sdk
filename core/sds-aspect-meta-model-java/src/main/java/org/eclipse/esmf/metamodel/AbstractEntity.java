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

import java.util.Collections;
import java.util.List;

/**
 * Represents a generic domain specific concept which may have multiple specific variants.
 *
 * @since BAMM 2.0.0
 */
public interface AbstractEntity extends ComplexType {

   /**
    * @return a {@link java.util.List} of {@link ComplexType}s which extend this Abstract Entity
    */
   default List<ComplexType> getExtendingElements() {
      return Collections.emptyList();
   }

   @Override
   default boolean isAbstractEntity() {
      return true;
   }
}
