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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.characteristic.Collection;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.impl.DefaultAspect;
import org.eclipse.esmf.metamodel.loader.Instantiator;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;

public class AspectInstantiator extends Instantiator<Aspect> {
   public AspectInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Aspect.class );
   }

   @Override
   public Aspect apply( final Resource aspect ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( aspect );
      final List<Property> properties = getPropertiesModels( aspect, bamm.properties() );
      final List<Operation> operations = getResourcesFromList( aspect, bamm.operations() )
            .map( operation -> modelElementFactory.create( Operation.class, operation ) )
            .collect( Collectors.toList() );
      final List<Event> events = getResourcesFromList( aspect, bamm.events() )
            .map( event -> modelElementFactory.create( Event.class, event ) )
            .collect( Collectors.toList() );
      final boolean isCollectionAspect = properties.stream()
            .map( Property::getCharacteristic )
            .flatMap( Optional::stream )
            .filter( Collection.class::isInstance ).count() == 1;
      return new DefaultAspect( metaModelBaseAttributes, properties, operations, events, isCollectionAspect );
   }
}
