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

package io.openmanufacturing.sds.metamodel.loader.instantiator;

import org.apache.jena.rdf.model.Resource;

import io.openmanufacturing.sds.characteristic.SingleEntity;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.characteristic.impl.DefaultSingleEntity;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

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
