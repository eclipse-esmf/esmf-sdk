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

import java.util.Optional;

import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.metamodel.characteristic.Either;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultEither;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.aspectmodel.loader.Instantiator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;

import org.apache.jena.rdf.model.Resource;

public class EitherInstantiator extends Instantiator<Either> {
   public EitherInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Either.class );
   }

   @Override
   public Either apply( final Resource either ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( either );
      final Characteristic left = modelElementFactory
            .create( Characteristic.class, attributeValue( either, SammNs.SAMMC.left() ).getResource() );
      final Characteristic right = modelElementFactory
            .create( Characteristic.class, attributeValue( either, SammNs.SAMMC.right() ).getResource() );
      return new DefaultEither( metaModelBaseAttributes, Optional.empty(), left, right );
   }
}
