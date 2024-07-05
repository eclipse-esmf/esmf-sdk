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
import org.eclipse.esmf.metamodel.characteristic.Measurement;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultMeasurement;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.aspectmodel.loader.Instantiator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class MeasurementInstantiator extends Instantiator<Measurement> {
   public MeasurementInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Measurement.class );
   }

   @Override
   public Measurement apply( final Resource measurement ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( measurement );
      final Type type = getType( measurement );
      final Optional<Unit> unit = optionalAttributeValue( measurement, SammNs.SAMMC.unit() )
            .map( Statement::getResource )
            .map( modelElementFactory::findOrCreateUnit );
      return new DefaultMeasurement( metaModelBaseAttributes, type, unit );
   }
}
