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
import org.apache.jena.rdf.model.Statement;

import io.openmanufacturing.sds.metamodel.Measurement;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.Unit;
import io.openmanufacturing.sds.metamodel.impl.DefaultMeasurement;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

public class MeasurementInstantiator extends Instantiator<Measurement> {
   public MeasurementInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Measurement.class );
   }

   @Override
   public Measurement apply( final Resource measurement ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( measurement );
      final Optional<Type> type = Optional.of( getType( measurement ) );
      final Optional<Unit> unit = optionalPropertyValue( measurement, bammc.unit() )
            .map( Statement::getResource )
            .flatMap( this::findOrCreateUnit );
      return new DefaultMeasurement( metaModelBaseAttributes, type, unit );
   }
}
