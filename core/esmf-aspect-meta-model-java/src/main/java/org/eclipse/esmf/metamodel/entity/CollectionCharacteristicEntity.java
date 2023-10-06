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
package org.eclipse.esmf.metamodel.entity;

import org.eclipse.esmf.characteristic.Collection;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;

public class CollectionCharacteristicEntity extends CharacteristicEntity implements Collection {
   public CollectionCharacteristicEntity(MetaModelBaseAttributes metaModelBaseAttributes, Type dataType ) {
      super( metaModelBaseAttributes, dataType );
   }

   @Override
   public boolean isOrdered() {
      return false;
   }

   @Override
   public boolean isAllowDuplicates() {
      return true;
   }

   @Override
   public CollectionValue.CollectionType getCollectionType() {
      return CollectionValue.CollectionType.COLLECTION;
   }
}
