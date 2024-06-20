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

import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.impl.DefaultOperation;
import org.eclipse.esmf.aspectmodel.loader.Instantiator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class OperationInstantiator extends Instantiator<Operation> {
   public OperationInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Operation.class );
   }

   @Override
   public Operation apply( final Resource operation ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( operation );
      final List<Property> input = getPropertiesModels( operation, SammNs.SAMM.input() );
      final Optional<Property> output =
            optionalAttributeValue( operation, SammNs.SAMM.output() )
                  .map( Statement::getResource )
                  .map( outputPropertyResource -> modelElementFactory
                        .create( Property.class, outputPropertyResource ) );
      return new DefaultOperation( metaModelBaseAttributes, input, output );
   }
}
