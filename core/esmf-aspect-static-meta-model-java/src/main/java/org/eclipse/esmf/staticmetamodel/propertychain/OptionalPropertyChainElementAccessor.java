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

import java.util.Optional;

import org.eclipse.esmf.staticmetamodel.StaticProperty;
import org.eclipse.esmf.staticmetamodel.propertychain.spi.PropertyChainElementAccessor;

/**
 * A {@link PropertyChainElementAccessor} that extracts the next value from an {@link Optional}.
 */
public class OptionalPropertyChainElementAccessor
      implements PropertyChainElementAccessor<Optional<Object>> {
   @Override
   public Class<Optional<Object>> getHandledElementClass() {
      return (Class) Optional.class;
   }

   @Override
   public Optional<Object> getValue( final Optional<Object> currentValue, final StaticProperty<Object, Object> property ) {
      return currentValue.map( v -> {
         final Object nextValue = property.getValue( v );
         if ( nextValue instanceof Optional<?> nextOptional ) {
            return nextOptional.orElse( null );
         }

         return nextValue;
      } );
   }
}
