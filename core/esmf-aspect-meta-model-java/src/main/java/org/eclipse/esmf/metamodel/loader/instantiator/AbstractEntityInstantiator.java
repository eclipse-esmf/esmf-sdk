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

package org.eclipse.esmf.metamodel.loader.instantiator;

import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.impl.DefaultAbstractEntity;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;

public class AbstractEntityInstantiator extends ComplexTypeInstantiator<AbstractEntity> {

   public AbstractEntityInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, AbstractEntity.class );
   }

   @Override
   protected AbstractEntity createDefaultEntity(
         MetaModelBaseAttributes metaModelBaseAttributes,
         List<Property> properties,
         Optional<ComplexType> extendedEntity,
         List<AspectModelUrn> extendingComplexTypes ) {
      return new DefaultAbstractEntity(
            metaModelBaseAttributes,
            properties,
            extendedEntity,
            extendingComplexTypes );
   }
}
