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

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import io.openmanufacturing.sds.metamodel.Operation;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.impl.DefaultOperation;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

@SuppressWarnings( "unused" ) // Instantiator is constructured via reflection from ModelElementFactory
public class OperationInstantiator extends Instantiator<Operation> {
   public OperationInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Operation.class );
   }

   @Override
   public Operation apply( final Resource operation ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( operation );
      final List<Property> input = getPropertiesModels( operation, bamm.input() );
      final Optional<Property> output =
            optionalPropertyValue( operation, bamm.output() )
                  .map( Statement::getResource )
                  .map( outputPropertyResource -> modelElementFactory
                        .create( Property.class, outputPropertyResource ) );
      return new DefaultOperation( metaModelBaseAttributes, input, output );
   }
}
