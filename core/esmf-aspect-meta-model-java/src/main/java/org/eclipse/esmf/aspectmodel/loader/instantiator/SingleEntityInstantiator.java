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

import org.eclipse.esmf.aspectmodel.loader.Instantiator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.characteristic.SingleEntity;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultSingleEntity;

import org.apache.jena.rdf.model.Resource;

public class SingleEntityInstantiator extends Instantiator<SingleEntity> {
   public SingleEntityInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, SingleEntity.class );
   }

   @Override
   public SingleEntity apply( final Resource singleEntity ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( singleEntity );
      final Type type = getType( singleEntity );
      return new DefaultSingleEntity( metaModelBaseAttributes, type );
   }
}
