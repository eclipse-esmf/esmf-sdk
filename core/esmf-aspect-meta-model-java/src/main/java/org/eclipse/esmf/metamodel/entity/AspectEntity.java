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
package org.eclipse.esmf.metamodel.entity;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class AspectEntity extends ModelEntityImpl implements Aspect {
   private List<Property> properties;
   private List<Operation> operations;
   private List<Event> events;
   private boolean isCollectionAspect;

   public AspectEntity(final MetaModelBaseAttributes metaModelBaseAttributes,
                       List<PropertyEntity> properties,
                       List<OperationEntity> operations,
                       List<EventEntity> events,
                       boolean isCollectionAspect ) {
      super( metaModelBaseAttributes );
      this.properties = new ArrayList<>( properties );
      this.operations = new ArrayList<>( operations );
      this.events = new ArrayList<>( events );
      this.isCollectionAspect = isCollectionAspect;
   }

   public AspectEntity(MetaModelBaseAttributes metaModelBaseAttributes) {
      super(metaModelBaseAttributes);
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
    * A list of Events provided by the Aspect.
    *
    * @return the events.
    */
   @Override
   public List<Event> getEvents() {
      return events;
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
      return new StringJoiner( ", ", AspectEntity.class.getSimpleName() + "[", "]" )
            .add( "properties=" + properties )
            .add( "operations=" + operations )
            .add( "events=" + events )
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
      final AspectEntity that = (AspectEntity) o;
      return isCollectionAspect == that.isCollectionAspect &&
            Objects.equals( properties, that.properties ) &&
            Objects.equals( operations, that.operations ) &&
            Objects.equals( events, that.events );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), properties, operations, events, isCollectionAspect );
   }
}
