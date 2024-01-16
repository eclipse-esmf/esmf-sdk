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
 * A Consumer similar to {@link java.util.function.Consumer} except a {@link Throwable} can be thrown
 *
 * @param <T> the type of the input to the function
 * @param <E> the type of Throwable that is thrown
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {
   void accept( T t ) throws E;

   default ThrowingConsumer<T, E> andThen( final ThrowingConsumer<? super T, E> after ) {
      Objects.requireNonNull( after );
      return ( final T t ) -> {
         accept( t );
         after.accept( t );
      };
   }
}
