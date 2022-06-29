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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;

import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Collection;
import io.openmanufacturing.sds.metamodel.Event;
import io.openmanufacturing.sds.metamodel.Operation;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.impl.DefaultAspect;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

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
