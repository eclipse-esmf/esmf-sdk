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
package io.openmanufacturing.sds.characteristic.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import io.openmanufacturing.sds.characteristic.Enumeration;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.Value;
import io.openmanufacturing.sds.metamodel.impl.DefaultCharacteristic;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

@SuppressWarnings( "squid:S1150" ) // Sonar thinks this implements java.util.Enumeration, which it does not
public class DefaultEnumeration extends DefaultCharacteristic implements Enumeration {
   private final List<Value> values;

   public DefaultEnumeration( final MetaModelBaseAttributes metaModelBaseAttributes, final Type dataType, final List<Value> values ) {
      super( metaModelBaseAttributes, Optional.of( dataType ) );
      this.values = values;
   }

   /**
    * A list of valid states.
    *
    * @return the values.
    */
   @Override
   public List<Value> getValues() {
      return values;
   }

   /**
    * Accepts an Aspect visitor
    *
    * @param visitor The visitor to accept
    * @param <T> The result type of the traversal operation
    * @param <C> The context of the visitor traversal
    */
   @Override
   public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
      return visitor.visitEnumeration( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultEnumeration.class.getSimpleName() + "[", "]" )
            .add( "values=" + values )
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
      if ( !super.equals( o ) ) {
         return false;
      }
      final DefaultEnumeration that = (DefaultEnumeration) o;
      return Objects.equals( values, that.values );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), values );
   }
}
