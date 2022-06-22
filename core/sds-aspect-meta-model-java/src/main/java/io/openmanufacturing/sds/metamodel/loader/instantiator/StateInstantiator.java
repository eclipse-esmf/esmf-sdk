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

import io.openmanufacturing.sds.metamodel.State;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.Value;
import io.openmanufacturing.sds.metamodel.impl.DefaultState;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

@SuppressWarnings( "unused" ) // Instantiator is constructured via reflection from ModelElementFactory
public class StateInstantiator extends Instantiator<State> {
   public StateInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, State.class );
   }

   @Override
   public State apply( final Resource state ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( state );
      final Type type = getType( state );
      final List<Value> enumValues = getNodesFromList( state, bammc.values() )
            .map( node -> buildValue( node, Optional.of( state ), type ) )
            .collect( Collectors.toList() );
      final Value defaultValue = buildValue( attributeValue( state, bammc.defaultValue() ).getObject(), Optional.of( state ), type );
      return new DefaultState( metaModelBaseAttributes, type, enumValues, defaultValue );
   }
}
