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

import org.eclipse.esmf.characteristic.Collection;
import org.eclipse.esmf.characteristic.impl.DefaultCollection;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.loader.Instantiator;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;

import org.apache.jena.rdf.model.Resource;

public class CollectionInstantiator extends Instantiator<Collection> {
   public CollectionInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Collection.class );
   }

   @Override
   public Collection apply( final Resource collection ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( collection );
      final Optional<Characteristic> elementCharacteristic = getElementCharacteristic( collection );
      final Optional<Type> dataType = elementCharacteristic.isPresent()
            ? elementCharacteristic.get().getDataType()
            : Optional.of( getType( collection ) );
      return new DefaultCollection( metaModelBaseAttributes, dataType, elementCharacteristic );
   }
}
