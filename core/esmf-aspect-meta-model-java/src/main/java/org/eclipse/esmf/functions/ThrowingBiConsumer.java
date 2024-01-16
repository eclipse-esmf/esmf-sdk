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

package org.eclipse.esmf.functions;

import java.util.Objects;

/**
 * A BiConsumer similar to {@link java.util.function.BiConsumer} except a {@link Throwable} can be thrown
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <E> the type of Throwable that is thrown
 */
@FunctionalInterface
public interface ThrowingBiConsumer<T, U, E extends Throwable> {
   void accept( T t, U u ) throws E;

   default ThrowingBiConsumer<T, U, E> andThen( final ThrowingBiConsumer<? super T, ? super U, E> after ) throws E {
      Objects.requireNonNull( after );

      return ( l, r ) -> {
         accept( l, r );
         after.accept( l, r );
      };
   }
}
