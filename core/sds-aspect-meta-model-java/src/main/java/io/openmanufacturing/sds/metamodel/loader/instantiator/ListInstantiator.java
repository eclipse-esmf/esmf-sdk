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
import io.openmanufacturing.sds.characteristic.List;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.impl.DefaultList;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

@SuppressWarnings( "unused" ) // Instantiator is constructured via reflection from ModelElementFactory
public class ListInstantiator extends Instantiator<List> {
   public ListInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, List.class );
   }

   @Override
   public List apply( final Resource list ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( list );
      final Optional<Characteristic> elementCharacteristic = getElementCharacteristic( list );
      final Optional<Type> dataType = elementCharacteristic.isPresent() ?
            elementCharacteristic.get().getDataType() :
            Optional.of( getType( list ) );
      return new DefaultList( metaModelBaseAttributes, dataType, elementCharacteristic );
   }
}
