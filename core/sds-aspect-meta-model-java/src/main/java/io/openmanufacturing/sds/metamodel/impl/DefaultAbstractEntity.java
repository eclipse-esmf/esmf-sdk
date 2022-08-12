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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.AbstractEntity;
import io.openmanufacturing.sds.metamodel.ComplexType;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

public class DefaultAbstractEntity extends DefaultComplexType implements AbstractEntity {

   private final List<AspectModelUrn> extendingElements;

   public static DefaultAbstractEntity createDefaultAbstractEntity(
         final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<? extends Property> properties,
         final Optional<ComplexType> _extends,
         final List<AspectModelUrn> extendingElements ) {
      final DefaultAbstractEntity defaultAbstractEntity = new DefaultAbstractEntity( metaModelBaseAttributes, properties, _extends, extendingElements );
      instances.put( metaModelBaseAttributes.getUrn().get(), defaultAbstractEntity );
      return defaultAbstractEntity;
   }

   private DefaultAbstractEntity( final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<? extends Property> properties,
         final Optional<ComplexType> _extends,
         final List<AspectModelUrn> extendingElements ) {
      super( metaModelBaseAttributes, properties, _extends );
      this.extendingElements = extendingElements;
   }

   /**
    * @return all {@link ComplexType} instances from the {@link DefaultComplexType#instances} Map which extend this
    *       Abstract Entity.
    */
   @Override
   public List<ComplexType> getExtendingElements() {
      return extendingElements.stream().map( instances::get ).filter( Objects::nonNull ).collect( Collectors.toList() );
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
      return visitor.visitAbstractEntity( this, context );
   }
}
