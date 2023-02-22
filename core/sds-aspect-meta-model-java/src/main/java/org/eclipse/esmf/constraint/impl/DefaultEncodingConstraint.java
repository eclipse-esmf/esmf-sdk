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
package org.eclipse.esmf.constraint.impl;

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.StringJoiner;

import org.eclipse.esmf.constraint.EncodingConstraint;

import org.eclipse.esmf.metamodel.impl.DefaultConstraint;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

public class DefaultEncodingConstraint extends DefaultConstraint implements EncodingConstraint {
   private final Charset value;

   public DefaultEncodingConstraint( final MetaModelBaseAttributes metaModelBaseAttributes, final Charset value ) {
      super( metaModelBaseAttributes );
      this.value = value;
   }

   /**
    * Constrains the encoding (character set) of a property.
    *
    * @return the value.
    */
   @Override
   public Charset getValue() {
      return value;
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
      return visitor.visitEncodingConstraint( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultEncodingConstraint.class.getSimpleName() + "[", "]" )
            .add( "value=" + value )
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
      final DefaultEncodingConstraint that = (DefaultEncodingConstraint) o;
      return Objects.equals( value, that.value );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), value );
   }
}
