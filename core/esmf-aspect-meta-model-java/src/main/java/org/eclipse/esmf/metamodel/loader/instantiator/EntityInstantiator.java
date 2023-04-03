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
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.impl.DefaultEntity;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;

public class EntityInstantiator extends ComplexTypeInstantiator<Entity> {
   public EntityInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Entity.class );
   }

   @Override
   protected Entity createDefaultEntity(
         MetaModelBaseAttributes metaModelBaseAttributes,
         List<Property> properties,
         Optional<ComplexType> extendedEntity,
         List<AspectModelUrn> extendingComplexTypes ) {
      return new DefaultEntity(
            metaModelBaseAttributes,
            properties,
            extendedEntity,
            extendingComplexTypes );
   }
}
