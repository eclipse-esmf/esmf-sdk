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

import io.openmanufacturing.sds.metamodel.Enumeration;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.impl.DefaultEnumeration;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

public class EnumerationInstantiator extends Instantiator<Enumeration> {
   public EnumerationInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Enumeration.class );
   }

   @Override
   public Enumeration apply( final Resource enumeration ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( enumeration );
      final List<Object> enumValues = getNodesFromList( enumeration, bammc.values() )
            .map( this::toEnumNodeValue )
            .collect( Collectors.toList() );
      final Optional<Type> type = Optional.of( getType( enumeration ) );
      return new DefaultEnumeration( metaModelBaseAttributes, type, enumValues );
   }
}
