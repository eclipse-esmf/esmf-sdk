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

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.StructuredValue;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.impl.DefaultStructuredValue;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

public class StructuredValueInstantiator extends Instantiator<StructuredValue> {
   public StructuredValueInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, StructuredValue.class );
   }

   @Override
   public StructuredValue apply( final Resource structuredValue ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( structuredValue );
      final String deconstructionRule = propertyValue( structuredValue, bammc.deconstructionRule() ).getString();
      final List<Object> elements = getNodesFromList( structuredValue, bammc.elements() )
            .map( this::toElement )
            .collect( Collectors.toList() );
      final Optional<Type> type = Optional.of( getType( structuredValue ) );
      return new DefaultStructuredValue( metaModelBaseAttributes, type, deconstructionRule, elements );
   }

   /**
    * Takes as input an RDFNode given in an bamm-c:elements list and turns it into either a String or
    * a {@link Property}
    *
    * @param node the input element node
    * @return Either a String or Property corresponding to the element
    */
   private Object toElement( final RDFNode node ) {
      if ( node.isLiteral() ) {
         return ((Literal) node).getString();
      }
      return modelElementFactory.create( Property.class, node.asResource() );
   }
}
