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

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.EntityInstance;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Value;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

public class DefaultEntityInstance extends BaseImpl implements EntityInstance {
   private final Map<Property, Value> assertions;
   private final Entity type;

   public DefaultEntityInstance( final MetaModelBaseAttributes metaModelBaseAttributes, final Map<Property, Value> assertions, final Entity type ) {
      super( metaModelBaseAttributes );
      this.assertions = assertions;
      this.type = type;
   }

   @Override
   public Entity getType() {
      return type;
   }

   @Override
   public Map<Property, Value> getAssertions() {
      return assertions;
   }

   @Override
   public int compareTo( final EntityInstance other ) {
      return getName().compareTo( other.getName() );
   }

   @Override
   public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
      return visitor.visitEntityInstance( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultEntityInstance.class.getSimpleName() + "[", "]" )
            .add( "assertions=" + assertions )
            .add( "type=" + type )
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
      final DefaultEntityInstance that = (DefaultEntityInstance) o;
      return Objects.equals( assertions, that.assertions ) && Objects.equals( type, that.type );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), assertions, type );
   }
}
