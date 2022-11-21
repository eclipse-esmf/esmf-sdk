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
import io.openmanufacturing.sds.characteristic.Collection;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.characteristic.impl.DefaultCollection;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

public class CollectionInstantiator extends Instantiator<Collection> {
   public CollectionInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Collection.class );
   }

   @Override
   public Collection apply( final Resource collection ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( collection );
      final Optional<Characteristic> elementCharacteristic = getElementCharacteristic( collection );
      final Optional<Type> dataType = elementCharacteristic.isPresent() ?
            elementCharacteristic.get().getDataType() :
            Optional.of( getType( collection ) );
      return new DefaultCollection( metaModelBaseAttributes, dataType, elementCharacteristic );
   }
}
