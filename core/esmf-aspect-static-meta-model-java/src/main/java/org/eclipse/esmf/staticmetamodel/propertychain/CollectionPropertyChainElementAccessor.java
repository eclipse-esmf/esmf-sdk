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

package org.eclipse.esmf.staticmetamodel.propertychain;

import java.util.Collection;
import java.util.stream.Stream;

import org.eclipse.esmf.staticmetamodel.StaticProperty;
import org.eclipse.esmf.staticmetamodel.propertychain.spi.PropertyChainElementAccessor;

/**
 * A {@link PropertyChainElementAccessor} that extracts the next value from a {@link Collection}.
 */
public class CollectionPropertyChainElementAccessor
      implements PropertyChainElementAccessor<Collection<Object>> {
   @Override
   public Class<Collection<Object>> getHandledElementClass() {
      return (Class) Collection.class;
   }

   @Override
   public Object getValue( final Collection<Object> currentValue,
         final StaticProperty<Object, Object> property ) {
      return currentValue.stream().flatMap( v -> {
         final Object nextValue = property.getValue( v );
         if ( nextValue instanceof Collection<?> nextCollection ) {
            return nextCollection.stream();
         }

         return Stream.ofNullable( nextValue );
      } ).toList();
   }
}
