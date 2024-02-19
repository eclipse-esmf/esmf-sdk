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

package org.eclipse.esmf;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import io.vavr.control.Either;

public interface Configuration {
   sealed interface Argument<T> {
      interface ParserErrorOrResult<T> extends Either<String, T> {}

      interface Parser<T> extends Function<String, ParserErrorOrResult<T>> {}
   }

   record Option<T>(
         T type,
         String fullName,
         Optional<String> shortName,
         String description,
         Optional<T> defaultValue,
         boolean mandatory,
         Parser<T> parser
   ) implements Argument<T> {}

   record SingleParameter<T>(
         T type,
         String label
   ) implements Argument<T> {}

   record RepeatedParameter<T>(
         T type,
         String label,
         Optional<Integer> minCount,
         Optional<Integer> maxCount,
         Argument.Parser<T> parser
   ) implements Argument<T> {}

   List<Argument<?>> arguments();

   interface ValueBinding<T> extends Map.Entry<Argument<T>, T> {
      T type();
   }

   interface Config extends List<ValueBinding<?>> {
   }
}
