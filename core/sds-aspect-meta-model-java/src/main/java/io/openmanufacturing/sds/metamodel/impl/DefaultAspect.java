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
package io.openmanufacturing.sds.metamodel.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Operation;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

public class DefaultAspect extends BaseImpl implements Aspect {
   private final List<Property> properties;
   private final List<Operation> operations;
   private final boolean isCollectionAspect;

   public DefaultAspect( final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<Property> properties,
         final List<Operation> operations,
         final boolean isCollectionAspect ) {
      super( metaModelBaseAttributes );
      this.properties = new ArrayList<>( properties );
      this.operations = new ArrayList<>( operations );
      this.isCollectionAspect = isCollectionAspect;
   }

   /**
    * A list of Properties exposed by the Aspect.
    *
    * @return the properties.
    */
   @Override
   public List<Property> getProperties() {
      return properties;
   }

   /**
    * A list of Operations exposed by the Aspect.
    *
    * @return the operations.
    */
   @Override
   public List<Operation> getOperations() {
      return operations;
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
      return visitor.visitAspect( this, context );
   }

   @Override
   public boolean isCollectionAspect() {
      return isCollectionAspect;
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultAspect.class.getSimpleName() + "[", "]" )
            .add( "properties=" + properties )
            .add( "operations=" + operations )
            .add( "isCollectionAspect=" + isCollectionAspect )
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
      final DefaultAspect that = (DefaultAspect) o;
      return isCollectionAspect == that.isCollectionAspect &&
            Objects.equals( properties, that.properties ) &&
            Objects.equals( operations, that.operations );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), properties, operations, isCollectionAspect );
   }
}
