/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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
package io.openmanufacturing.sds.metamodel.datatypes;

import java.util.Objects;

/**
 * Represents the bamm:curie data type
 */
public class Curie {
   private final String value;

   public Curie( final String value ) {
      this.value = value;
   }

   public String getValue() {
      return value;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }

      final Curie curie = (Curie) o;
      return Objects.equals( value, curie.value );
   }

   @Override
   public int hashCode() {
      return value != null ? value.hashCode() : 0;
   }

   @Override
   public String toString() {
      return value;
   }
}
