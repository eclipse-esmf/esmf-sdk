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
import io.openmanufacturing.sds.characteristic.Set;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.characteristic.impl.DefaultSet;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

public class SetInstantiator extends Instantiator<Set> {
   public SetInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Set.class );
   }

   @Override
   public Set apply( final Resource set ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( set );
      final Optional<Characteristic> elementCharacteristic = getElementCharacteristic( set );
      final Optional<Type> dataType = elementCharacteristic.isPresent() ?
            elementCharacteristic.get().getDataType() :
            Optional.of( getType( set ) );
      return new DefaultSet( metaModelBaseAttributes, dataType, elementCharacteristic );
   }
}
