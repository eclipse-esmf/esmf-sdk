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

package org.eclipse.esmf.staticmetamodel.propertychain.spi;

import org.eclipse.esmf.staticmetamodel.StaticProperty;

/**
 * Service Provider Interface (SPI) for classes that can handle specific cases during the resolving of a property chain.
 * These are e.g. needed when types within the chain are switched to complex ones like container types ({@code Optional},
 * {@code Collection} or similar). Application developers can provide custom accessors using this interface and they will be picked up by
 * the property chain implementation.
 *
 * @param <T> the type of the chain element this accessor is able to handle
 */
public interface PropertyChainElementAccessor<T> {
   /**
    * @return the class of the chain element type this accessor is able to handle
    */
   Class<T> getHandledElementClass();

   /**
    * Extracts the given property from the value of the previous chain property.
    *
    * @param currentValue the current value (of the previous chain property)
    * @param property the property to extract the value for
    * @return the extracted property value
    */
   Object getValue( T currentValue, StaticProperty<Object, Object> property );
}
