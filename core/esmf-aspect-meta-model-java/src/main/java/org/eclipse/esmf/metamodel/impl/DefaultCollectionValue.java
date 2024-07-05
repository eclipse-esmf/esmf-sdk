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

import java.util.Collection;
import java.util.Objects;
import java.util.StringJoiner;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;

public class DefaultCollectionValue implements CollectionValue {
   private final Collection<Value> values;
   private final CollectionType collectionType;
   private final Type elementType;

   public DefaultCollectionValue(
         final Collection<Value> values,
         final CollectionType collectionType,
         final Type elementType
   ) {
      this.values = values;
      this.collectionType = collectionType;
      this.elementType = elementType;
   }

   @Override
   public Collection<Value> getValues() {
      return values;
   }

   @Override
   public Type getType() {
      return elementType;
   }

   @Override
   public CollectionType getCollectionType() {
      return collectionType;
   }

   /**
    * Similar to {@link DefaultScalarValue#getSourceFile()}, collection values are not defined in Aspect Model files, so this returns null.
    *
    * @return null
    */
   @Override
   public AspectModelFile getSourceFile() {
      return null;
   }

   @Override
   public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
      return visitor.visitCollectionValue( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultCollectionValue.class.getSimpleName() + "[", "]" )
            .add( "values=" + values )
            .add( "collectionType=" + collectionType )
            .add( "elementType=" + elementType )
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
      return Objects.equals( values, that.values ) && Objects.equals( collectionType, that.collectionType ) && Objects.equals(
            elementType, that.elementType );
   }

   @Override
   public int hashCode() {
      return Objects.hash( values, collectionType, elementType );
   }
}
