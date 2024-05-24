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

import org.eclipse.esmf.aspectmodel.vocabulary.SammNs;
import org.eclipse.esmf.characteristic.State;
import org.eclipse.esmf.characteristic.impl.DefaultState;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.loader.Instantiator;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;

import org.apache.jena.rdf.model.Resource;

public class StateInstantiator extends Instantiator<State> {
   public StateInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, State.class );
   }

   @Override
   public State apply( final Resource state ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( state );
      final Type type = getType( state );
      final List<Value> enumValues = getNodesFromList( state, SammNs.SAMMC.values() )
            .map( node -> buildValue( node, Optional.of( state ), type ) )
            .collect( Collectors.toList() );
      final Value defaultValue = buildValue( attributeValue( state, SammNs.SAMMC.defaultValue() ).getObject(), Optional.of( state ), type );
      return new DefaultState( metaModelBaseAttributes, type, enumValues, defaultValue );
   }
}
