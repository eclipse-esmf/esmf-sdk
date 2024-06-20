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

import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultEnumeration;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.aspectmodel.loader.Instantiator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;

import org.apache.jena.rdf.model.Resource;

public class EnumerationInstantiator extends Instantiator<Enumeration> {
   public EnumerationInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Enumeration.class );
   }

   @Override
   public Enumeration apply( final Resource enumeration ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( enumeration );
      final Type type = getType( enumeration );
      final List<Value> enumValues = getNodesFromList( enumeration, SammNs.SAMMC.values() )
            .map( node -> buildValue( node, Optional.of( enumeration ), type ) )
            .collect( Collectors.toList() );
      return new DefaultEnumeration( metaModelBaseAttributes, type, enumValues );
   }
}
