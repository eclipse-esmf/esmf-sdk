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

import java.util.Collection;
import java.util.Objects;
import java.util.StringJoiner;

import io.openmanufacturing.sds.metamodel.CollectionValue;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.Value;

public class DefaultCollectionValue extends BaseValue implements CollectionValue {
   private final Collection<Value> values;
   private final Type type;

   public DefaultCollectionValue( final Collection<Value> values, final Type type ) {
      this.values = values;
      this.type = type;
   }

   @Override
   public Collection<Value> getValues() {
      return values;
   }

   @Override
   public Type getType() {
      return type;
   }

   @Override
   public boolean isCollection() {
      return true;
   }

   @Override
   public CollectionValue asCollectionValue() {
      return this;
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultCollectionValue.class.getSimpleName() + "[", "]" )
            .add( "values=" + values )
            .add( "type='" + type + "'" )
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
      final DefaultCollectionValue that = (DefaultCollectionValue) o;
      return Objects.equals( values, that.values ) && Objects.equals( type, that.type );
   }

   @Override
   public int hashCode() {
      return Objects.hash( values, type );
   }
}
