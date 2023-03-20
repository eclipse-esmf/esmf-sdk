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

package org.eclipse.esmf.characteristic.impl;

import java.util.Optional;
import java.util.StringJoiner;

import org.eclipse.esmf.characteristic.SortedSet;

import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

public class DefaultSortedSet extends DefaultCollection implements SortedSet {

   public DefaultSortedSet( final MetaModelBaseAttributes metaModelBaseAttributes,
         final Optional<Type> dataType, final Optional<Characteristic> elementCharacteristic ) {
      super( metaModelBaseAttributes, dataType, false, true, elementCharacteristic );
   }

   @Override
   public CollectionValue.CollectionType getCollectionType() {
      return CollectionValue.CollectionType.SORTEDSET;
   }

   /**
    * Accepts an Aspect visitor
    *
    * @param visitor The visitor to accept
    * @param <T> The result type of the traversal operation
    * @param <C> The context of the visitor traversal
    */
   @Override
   public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
      return visitor.visitSortedSet( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultSortedSet.class.getSimpleName() + "[", "]" )
            .toString();
   }
}
