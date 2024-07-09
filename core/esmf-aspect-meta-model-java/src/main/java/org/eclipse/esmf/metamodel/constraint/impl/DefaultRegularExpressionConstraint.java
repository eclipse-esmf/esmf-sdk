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
package org.eclipse.esmf.metamodel.constraint.impl;

import java.util.Objects;
import java.util.StringJoiner;

import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.constraint.RegularExpressionConstraint;
import org.eclipse.esmf.metamodel.impl.DefaultConstraint;

public class DefaultRegularExpressionConstraint extends DefaultConstraint implements RegularExpressionConstraint {
   private final String value;

   public DefaultRegularExpressionConstraint( final MetaModelBaseAttributes metaModelBaseAttributes,
         final String value ) {
      super( metaModelBaseAttributes );
      this.value = value;
   }

   /**
    * Constrains the lexical value of a property.
    *
    * @return the value.
    */
   @Override
   public String getValue() {
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
      return visitor.visitRegularExpressionConstraint( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultRegularExpressionConstraint.class.getSimpleName() + "[", "]" )
            .add( "value='" + value + "'" )
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
      final DefaultRegularExpressionConstraint that = (DefaultRegularExpressionConstraint) o;
      return Objects.equals( value, that.value );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), value );
   }
}
