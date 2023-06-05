/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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
package org.eclipse.esmf.aspectmodel.generator.json.testclasses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AspectWithEither {

   private final Either<String, Integer> either;

   public AspectWithEither( @JsonProperty( "testProperty" ) Either<String, Integer> either ) {
      this.either = either;
   }

   public Either<String, Integer> getEither() {
      return either;
   }
}
