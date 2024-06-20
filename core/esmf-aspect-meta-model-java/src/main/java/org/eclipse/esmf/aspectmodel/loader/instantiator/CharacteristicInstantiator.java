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

import java.util.Optional;

import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.aspectmodel.loader.Instantiator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;

import org.apache.jena.rdf.model.Resource;

public class CharacteristicInstantiator extends Instantiator<Characteristic> {
   public CharacteristicInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Characteristic.class );
   }

   @Override
   public Characteristic apply( final Resource characteristic ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( characteristic );
      final Optional<Type> type = Optional.of( getType( characteristic ) );
      return new DefaultCharacteristic( metaModelBaseAttributes, type );
   }
}
