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

import org.eclipse.esmf.characteristic.TimeSeries;
import org.eclipse.esmf.characteristic.impl.DefaultTimeSeries;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.loader.Instantiator;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;

import org.apache.jena.rdf.model.Resource;

public class TimeSeriesInstantiator extends Instantiator<TimeSeries> {
   public TimeSeriesInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, TimeSeries.class );
   }

   @Override
   public TimeSeries apply( final Resource timeSeries ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( timeSeries );
      final Optional<Characteristic> elementCharacteristic = getElementCharacteristic( timeSeries );
      final Optional<Type> dataType = elementCharacteristic.isPresent()
            ? elementCharacteristic.get().getDataType()
            : Optional.of( getType( timeSeries ) );
      return new DefaultTimeSeries( metaModelBaseAttributes, dataType, elementCharacteristic );
   }
}
