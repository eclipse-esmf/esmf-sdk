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

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
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

   /**
    * Returns a collector that turns a stream of objects into a set. This collector automatically removes 'null' objects.
    *
    * @param <T> the type of the set
    * @return the set of elements
    */
   public static <T> Collector<T, ?, Set<T>> asSet() {
      return new CollectorImpl<>(
            HashSet::new,
            ( set, object ) -> {
               if ( object != null ) {
                  set.add( object );
               }
            }, ( left, right ) -> {
         if ( left.size() < right.size() ) {
            right.addAll( left );
            return right;
         } else {
            left.addAll( right );
            return left;
         }
      },
            castingIdentity(),
            Collections.unmodifiableSet( EnumSet.of( Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH ) ) );
   }

   /**
    * Simple implementation of a {@link Collector}
    */
   public record CollectorImpl<T, A, R>(
         Supplier<A> supplier,
         BiConsumer<A, T> accumulator,
         BinaryOperator<A> combiner,
         Function<A, R> finisher,
         Set<Characteristics> characteristics
   ) implements Collector<T, A, R> {
   }

   @SuppressWarnings( "unchecked" )
   private static <I, R> Function<I, R> castingIdentity() {
      return i -> (R) i;
   }
}
