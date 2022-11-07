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

package io.openmanufacturing.sds.aspectmodel.validation.services;

import java.net.URI;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.jenax.util.RDFLabels;
import org.topbraid.shacl.engine.ShapesGraph;
import org.topbraid.shacl.validation.ValidationEngine;
import org.topbraid.shacl.validation.ValidationEngineFactory;

/**
 * Custom {@link ValidationEngineFactory} which provides a {@link ValidationEngine} with a custom label function.
 */
@Deprecated( forRemoval = true )
public class BammValidationEngineFactory extends ValidationEngineFactory {

   /**
    * Creates a {@link ValidationEngine} with a custom label function which uses {@link RDFLabels#getNodeLabel(RDFNode)}
    * to determine the label used in the validation result message.
    *
    * @see ValidationEngineFactory#create(Dataset, URI, ShapesGraph, Resource)
    */
   @Override
   public ValidationEngine create( final Dataset dataset, final URI shapesGraphURI, final ShapesGraph shapesGraph,
         final Resource report ) {
      final ValidationEngine engine = super.create( dataset, shapesGraphURI, shapesGraph, report );
      engine.setLabelFunction( rdfNode -> RDFLabels.get().getNodeLabel( rdfNode ) );
      return engine;
   }
}
