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

import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

public class DefaultEvent extends ModelElementImpl implements Event {
   private final List<Property> properties;

   public DefaultEvent( final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<Property> properties ) {
      super( metaModelBaseAttributes );
      this.properties = properties;
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
   public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
      return visitor.visitEvent( this, context );
   }
}
