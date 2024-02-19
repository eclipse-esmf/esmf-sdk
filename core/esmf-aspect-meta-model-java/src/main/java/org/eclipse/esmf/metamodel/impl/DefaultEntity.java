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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

public class DefaultEntity extends DefaultComplexType implements Entity {

   public static DefaultEntity createDefaultEntity( final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<? extends Property> properties, @SuppressWarnings( "checkstyle:ParameterName" ) final Optional<ComplexType> extends_ ) {
      return new DefaultEntity( metaModelBaseAttributes, properties, extends_, Collections.emptyList(), null );
   }

   public DefaultEntity(
         final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<? extends Property> properties ) {
      this( metaModelBaseAttributes, properties, Optional.empty(), Collections.emptyList(), null );
   }

   public DefaultEntity(
         final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<? extends Property> properties,
         @SuppressWarnings( "checkstyle:ParameterName" ) final Optional<ComplexType> extends_,
         final List<AspectModelUrn> extendingElements,
         final ModelElementFactory loadedElements ) {
      super( metaModelBaseAttributes, properties, extends_, extendingElements, loadedElements );
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
}
