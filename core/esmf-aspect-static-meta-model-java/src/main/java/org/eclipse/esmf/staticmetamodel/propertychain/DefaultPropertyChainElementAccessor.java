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

import org.eclipse.esmf.staticmetamodel.StaticProperty;
import org.eclipse.esmf.staticmetamodel.propertychain.spi.PropertyChainElementAccessor;

/**
 * The default {@link PropertyChainElementAccessor} that simply extracts the next value from the property as-is, assuming the current value
 * is an entity (i.e. not the end of the chain).
 */
public class DefaultPropertyChainElementAccessor implements PropertyChainElementAccessor<Object> {
   @Override
   public Class<Object> getHandledElementClass() {
      return Object.class;
   }

   @Override
   public Object getValue( final Object currentValue, final StaticProperty<Object, Object> property ) {
      return property.getValue( currentValue );
   }
}
