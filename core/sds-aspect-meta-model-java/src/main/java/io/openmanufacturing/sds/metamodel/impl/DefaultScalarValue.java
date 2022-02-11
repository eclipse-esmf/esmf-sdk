/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.metamodel.impl;

import java.util.Objects;
import java.util.StringJoiner;

import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.ScalarValue;

public class DefaultScalarValue extends BaseValue implements ScalarValue {
   private final Object value;
   private final Scalar type;

   public DefaultScalarValue( final Object value, final Scalar type ) {
      this.value = value;
      this.type = type;
   }

   @Override
   public Object getValue() {
      return value;
   }

   @Override
   public Scalar getType() {
      return type;
   }

   @Override
   public boolean isScalar() {
      return true;
   }

   @Override
   public ScalarValue asScalarValue() {
      return this;
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultScalarValue.class.getSimpleName() + "[", "]" )
            .add( "value=" + value )
            .add( "typeUri='" + type + "'" )
            .toString();
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final DefaultScalarValue that = (DefaultScalarValue) o;
      return Objects.equals( value, that.value ) && Objects.equals( type, that.type );
   }

   @Override
   public int hashCode() {
      return Objects.hash( value, type );
   }
}
