/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.metamodel;

import java.util.Optional;

/**
 * A group of values which may be either of a simple or complex type. The values may be duplicated and are not ordered.
 *
 * @since BAMM 1.0.0
 */
public interface Collection extends Characteristic {
   enum CollectionType {
      COLLECTION, SET, SORTEDSET, LIST
   }

   /**
    * @return a {@link boolean} which determines whether the elements in the collection are ordered.
    */
   boolean isOrdered();

   /**
    * @return a {@link boolean} which determines whether the collection may contain duplicate values.
    */
   boolean isAllowDuplicates();

   /**
    * @return {@link Optional} containing the {@link Characteristic} describing the elements of the Collection
    *
    * @since BAMM 1.0.0
    */
   default Optional<Characteristic> getElementCharacteristic() {
      return Optional.empty();
   }

   CollectionType getCollectionType();
}
