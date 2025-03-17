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

package org.eclipse.esmf.metamodel.impl;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.datatype.LangString;

public class DefaultScalarValue implements ScalarValue {
   private final Object value;
   private final Scalar type;

   private final MetaModelBaseAttributes metaModelBaseAttributes;

   public DefaultScalarValue( final MetaModelBaseAttributes metaModelBaseAttributes, final Object value, final Scalar type ) {
      this.metaModelBaseAttributes = metaModelBaseAttributes;
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
   public List<String> getSee() {
      return metaModelBaseAttributes.getSee();
   }

   @Override
   public Set<LangString> getPreferredNames() {
      return metaModelBaseAttributes.getPreferredNames();
   }

   @Override
   public Set<LangString> getDescriptions() {
      return metaModelBaseAttributes.getDescriptions();
   }

   /**
    * Similar to {@link DefaultScalar#getSourceFile()}, scalar values are not defined in Aspect Model files, so this returns null.
    *
    * @return null
    */
   @Override
   public AspectModelFile getSourceFile() {
      return null;
   }

   @Override
   public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
      return visitor.visitScalarValue( this, context );
   }

   @Override
   public int compareTo( final ScalarValue other ) {
      if ( !type.equals( other.getType() ) ) {
         throw new UnsupportedOperationException( "Tried to compare values of different types" );
      }
      if ( value instanceof Comparable ) {
         return compareTo( getValue(), other.getValue() );
      }
      return 0;
   }

   @SuppressWarnings( "unchecked" )
   private <T extends Comparable<T>> int compareTo( final Object value1, final Object value2 ) {
      return ((T) value1).compareTo( (T) value2 );
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
