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
import java.util.Optional;
import java.util.StringJoiner;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

public class DefaultEntity extends BaseImpl implements Entity {
   private final List<Property> properties;
   private final Optional<AspectModelUrn> refines;

   public DefaultEntity( final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<? extends Property> properties,
         final Optional<AspectModelUrn> refines ) {
      super( metaModelBaseAttributes );
      this.properties = new ArrayList<>( properties );
      this.refines = refines;
   }

   /**
    * A list of properties defined in the scope of the Entity.
    *
    * @return the properties.
    */
   @Override
   public List<Property> getProperties() {
      return properties;
   }

   @Override
   public Optional<AspectModelUrn> getRefines() {
      return refines;
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
      return visitor.visitEntity( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultEntity.class.getSimpleName() + "[", "]" )
            .add( "properties=" + properties )
            .add( "refines=" + refines )
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
      final DefaultEntity that = (DefaultEntity) o;
      return Objects.equals( properties, that.properties ) &&
            Objects.equals( refines, that.refines );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), properties, refines );
   }
}
