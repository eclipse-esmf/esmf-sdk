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

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.loader.Instantiator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.impl.DefaultAspect;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import org.apache.jena.rdf.model.Resource;

public class AspectInstantiator extends Instantiator<Aspect> {
   public AspectInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Aspect.class );
   }

   @Override
   public Aspect apply( final Resource aspect ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( aspect );
      final List<Property> properties = getPropertiesModels( aspect, SammNs.SAMM.properties() );
      final List<Operation> operations = getResourcesFromList( aspect, SammNs.SAMM.operations() )
            .map( operation -> modelElementFactory.create( Operation.class, operation ) )
            .collect( Collectors.toList() );
      final List<Event> events = getResourcesFromList( aspect, SammNs.SAMM.events() )
            .map( event -> modelElementFactory.create( Event.class, event ) )
            .collect( Collectors.toList() );
      return new DefaultAspect( metaModelBaseAttributes, properties, operations, events );
   }
}
