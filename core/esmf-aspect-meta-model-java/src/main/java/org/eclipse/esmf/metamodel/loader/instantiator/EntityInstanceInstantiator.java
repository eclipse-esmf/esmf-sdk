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

import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.loader.Instantiator;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;

import org.apache.jena.rdf.model.Resource;

/**
 * Instantiates entity instances for a certain given entity type
 */
public class EntityInstanceInstantiator extends Instantiator<EntityInstance> {
   private final Entity entity;

   public EntityInstanceInstantiator( final ModelElementFactory modelElementFactory, final Entity entity ) {
      super( modelElementFactory, EntityInstance.class );
      this.entity = entity;
   }

   @Override
   public EntityInstance apply( final Resource resource ) {
      return buildEntityInstance( resource, entity );
   }
}
