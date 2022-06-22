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

import io.openmanufacturing.sds.metamodel.Duration;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.Unit;
import io.openmanufacturing.sds.metamodel.impl.DefaultDuration;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

@SuppressWarnings( "unused" ) // Instantiator is constructured via reflection from ModelElementFactory
public class DurationInstantiator extends Instantiator<Duration> {
   public DurationInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Duration.class );
   }

   @Override
   public Duration apply( final Resource duration ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( duration );
      final Type type = getType( duration );
      final Optional<Unit> unit = optionalAttributeValue( duration, bammc.unit() )
            .map( Statement::getResource )
            .flatMap( this::findOrCreateUnit );
      return new DefaultDuration( metaModelBaseAttributes, type, unit );
   }
}
