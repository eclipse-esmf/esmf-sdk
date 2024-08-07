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

import org.eclipse.esmf.aspectmodel.loader.Instantiator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.characteristic.Duration;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultDuration;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class DurationInstantiator extends Instantiator<Duration> {
   public DurationInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Duration.class );
   }

   @Override
   public Duration apply( final Resource duration ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( duration );
      final Type type = getType( duration );
      final Optional<Unit> unit = optionalAttributeValue( duration, SammNs.SAMMC.unit() )
            .map( Statement::getResource )
            .map( modelElementFactory::findOrCreateUnit );
      return new DefaultDuration( metaModelBaseAttributes, type, unit );
   }
}
