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

import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.impl.ModelElementImpl;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

import java.util.ArrayList;
import java.util.List;

public class EventEntity extends ModelEntityImpl implements Event {
   private List<Property> properties;

   public EventEntity(MetaModelBaseAttributes metaModelBaseAttributes, List<PropertyEntity> properties ) {
      super( metaModelBaseAttributes );
      this.properties = new ArrayList<>( properties );
   }

   @Override
   public List<Property> getProperties() {
      return properties;
   }

   /**
    * Accepts an Aspect visitor
    *
    * @param visitor The visitor to accept
    * @param <T> The result type of the traversal operation
    * @param <C> The context of the visitor traversal
    */
   @Override
   public <T, C> T accept( AspectVisitor<T, C> visitor, C context ) {
      return visitor.visitEvent( this, context );
   }

   public void setProperties(List<Property> properties) {
      this.properties = properties;
   }
}
