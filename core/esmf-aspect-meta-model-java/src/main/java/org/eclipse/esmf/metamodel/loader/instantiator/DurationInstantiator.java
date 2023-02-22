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

import org.eclipse.esmf.characteristic.Duration;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.characteristic.impl.DefaultDuration;
import org.eclipse.esmf.metamodel.loader.Instantiator;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;

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
            .map( modelElementFactory::findOrCreateUnit );
      return new DefaultDuration( metaModelBaseAttributes, type, unit );
   }
}
