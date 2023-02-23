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

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import org.eclipse.esmf.characteristic.Measurement;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.characteristic.impl.DefaultMeasurement;
import org.eclipse.esmf.metamodel.loader.Instantiator;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;

public class MeasurementInstantiator extends Instantiator<Measurement> {
   public MeasurementInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Measurement.class );
   }

   @Override
   public Measurement apply( final Resource measurement ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( measurement );
      final Type type = getType( measurement );
      final Optional<Unit> unit = optionalAttributeValue( measurement, SAMMC.unit() )
            .map( Statement::getResource )
            .map( modelElementFactory::findOrCreateUnit );
      return new DefaultMeasurement( metaModelBaseAttributes, type, unit );
   }
}
