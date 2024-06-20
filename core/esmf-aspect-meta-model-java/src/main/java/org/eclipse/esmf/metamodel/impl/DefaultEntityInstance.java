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

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;

public class DefaultEntityInstance extends ModelElementImpl implements EntityInstance {
   private final Map<Property, Value> assertions;
   private final Entity type;

   public DefaultEntityInstance( final MetaModelBaseAttributes metaModelBaseAttributes, final Map<Property, Value> assertions,
         final Entity type ) {
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
