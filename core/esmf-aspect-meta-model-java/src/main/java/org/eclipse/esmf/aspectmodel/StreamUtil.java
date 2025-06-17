/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Commonly used Stream utility functions
 */
public class StreamUtil {
   /**
    * Returns a collector that turns a stream of Map entries into a map
    *
    * @param <K> the map's key type
    * @param <V> the map's value type
    * @return the collector
    */
   public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> asMap() {
      return Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue );
   }

   /**
    * Returns a collector that turns a stream of Map entries into a map, while keeping the original stream element order
    *
    * @param <K> the map's key type
    * @param <V> the map's value type
    * @return the collector
    */
   public static <K, V> Collector<Map.Entry<K, V>, ?, LinkedHashMap<K, V>> asSortedMap() {
      return Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue, ( v1, v2 ) -> {
         throw new RuntimeException( String.format( "Duplicate key for values %s and %s", v1, v2 ) );
      }, LinkedHashMap::new );
   }
}
