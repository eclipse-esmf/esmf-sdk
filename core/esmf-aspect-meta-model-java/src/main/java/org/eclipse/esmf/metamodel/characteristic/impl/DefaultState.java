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
package org.eclipse.esmf.metamodel.characteristic.impl;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.characteristic.State;

public class DefaultState extends DefaultEnumeration implements State {
   private final Value defaultValue;

   public DefaultState( final MetaModelBaseAttributes metaModelBaseAttributes, final Type dataType, final List<Value> values,
         final Value defaultValue ) {
      super( metaModelBaseAttributes, dataType, values );
      this.defaultValue = defaultValue;
   }

   /**
    * The default value for this state
    *
    * @return the defaultValue.
    */
   @Override
   public Value getDefaultValue() {
      return defaultValue;
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
      return visitor.visitState( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultState.class.getSimpleName() + "[", "]" )
            .add( "defaultValue=" + defaultValue )
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
      final DefaultState that = (DefaultState) o;
      return Objects.equals( defaultValue, that.defaultValue );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), defaultValue );
   }
}
