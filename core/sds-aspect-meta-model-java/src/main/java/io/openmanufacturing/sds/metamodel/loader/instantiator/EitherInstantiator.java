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

import java.util.Optional;

import org.apache.jena.rdf.model.Resource;

import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.characteristic.Either;
import io.openmanufacturing.sds.characteristic.impl.DefaultEither;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

public class EitherInstantiator extends Instantiator<Either> {
   public EitherInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Either.class );
   }

   @Override
   public Either apply( final Resource either ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( either );
      final Characteristic left = modelElementFactory
            .create( Characteristic.class, attributeValue( either, bammc.left() ).getResource() );
      final Characteristic right = modelElementFactory
            .create( Characteristic.class, attributeValue( either, bammc.right() ).getResource() );
      return new DefaultEither( metaModelBaseAttributes, Optional.empty(), left, right );
   }
}
