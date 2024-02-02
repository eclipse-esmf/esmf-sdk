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

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.eclipse.esmf.staticmetamodel.propertychain.spi.PropertyChainElementAccessor;

/**
 * Handles loading of all available {@link PropertyChainElementAccessor} implementations.
 *
 * @see DefaultPropertyChainElementAccessor
 */
public class PropertyChainElementAccessors {
   private static final PropertyChainElementAccessor<Object> DEFAULT_ACCESSOR;

   static {
      DEFAULT_ACCESSOR = getAccessorByName( DefaultPropertyChainElementAccessor.class.getName() );
   }

   private PropertyChainElementAccessors() {
   }

   /**
    * @return all available {@link PropertyChainElementAccessor} implementations
    */
   public static List<PropertyChainElementAccessor<Object>> getAllPropertyAccessors() {
      List<PropertyChainElementAccessor<Object>> accessors = new ArrayList<>();
      ServiceLoader<PropertyChainElementAccessor<Object>> loader = ServiceLoader.load( (Class) PropertyChainElementAccessor.class );
      loader.forEach( accessors::add );

      accessors.removeIf( accessor -> DEFAULT_ACCESSOR.getClass().equals( accessor.getClass() ) );

      return accessors;
   }

   /**
    * @return the {@link DefaultPropertyChainElementAccessor}
    */
   public static PropertyChainElementAccessor<Object> getDefaultAccessor() {
      return DEFAULT_ACCESSOR;
   }

   /**
    * Tries to find a {@link PropertyChainElementAccessor} with the given name.
    *
    * @param accessorName the name of the accessor to look up
    * @return the acccessor
    * @throws IllegalArgumentException if no accessor with the given name could be found
    */
   public static PropertyChainElementAccessor<Object> getAccessorByName( String accessorName ) {
      ServiceLoader<PropertyChainElementAccessor<Object>> serviceLoader = ServiceLoader.load( (Class) PropertyChainElementAccessor.class );

      return serviceLoader.stream()
            .filter( accessor -> accessorName.equals( accessor.type().getName() ) )
            .findAny()
            .map( ServiceLoader.Provider::get )
            .orElseThrow( () -> new IllegalArgumentException( "Property Chain Element Accessor " + accessorName + " not found" ) );
   }
}
