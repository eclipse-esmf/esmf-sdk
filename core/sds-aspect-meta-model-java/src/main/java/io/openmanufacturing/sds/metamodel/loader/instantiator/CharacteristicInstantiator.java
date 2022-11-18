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
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.impl.DefaultCharacteristic;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

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
