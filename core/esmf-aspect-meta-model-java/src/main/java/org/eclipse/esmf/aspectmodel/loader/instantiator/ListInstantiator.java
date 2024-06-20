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

import org.eclipse.esmf.metamodel.characteristic.List;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultList;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.aspectmodel.loader.Instantiator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;

import org.apache.jena.rdf.model.Resource;

public class ListInstantiator extends Instantiator<List> {
   public ListInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, List.class );
   }

   @Override
   public List apply( final Resource list ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( list );
      final Optional<Characteristic> elementCharacteristic = getElementCharacteristic( list );
      final Optional<Type> dataType = elementCharacteristic.isPresent()
            ? elementCharacteristic.get().getDataType()
            : Optional.of( getType( list ) );
      return new DefaultList( metaModelBaseAttributes, dataType, elementCharacteristic );
   }
}
