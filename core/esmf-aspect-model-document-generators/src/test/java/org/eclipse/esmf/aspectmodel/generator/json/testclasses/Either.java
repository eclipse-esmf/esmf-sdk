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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Either<L, R> {

   private final L left;
   private final R right;

   @JsonCreator
   public Either( @JsonProperty( "left" ) L left, @JsonProperty( "right" ) R right ) {
      this.left = left;
      this.right = right;
   }

   public L getLeft() {
      return left;
   }

   public R getRight() {
      return right;
   }
}
