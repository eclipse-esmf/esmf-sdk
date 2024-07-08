/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.loader.instantiator;

import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.impl.DefaultAbstractEntity;

public class AbstractEntityInstantiator extends ComplexTypeInstantiator<AbstractEntity> {
   public AbstractEntityInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, AbstractEntity.class );
   }

   @Override
   protected AbstractEntity createDefaultEntity(
         final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<Property> properties,
         final Optional<ComplexType> extendedEntity,
         final List<AspectModelUrn> extendingComplexTypes ) {
      return new DefaultAbstractEntity( metaModelBaseAttributes, properties, extendedEntity, extendingComplexTypes, modelElementFactory );
   }
}
