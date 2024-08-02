/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.edit;

import java.io.StringWriter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class RdfUtil {
   public static Model getModelElementDefinition( final Resource element ) {
      final Model result = ModelFactory.createDefaultModel();
      element.getModel().listStatements( element, null, (RDFNode) null ).toList().forEach( statement -> {
         final Resource subject = statement.getSubject();
         final Resource newSubject = subject.isAnon()
               ? result.createResource( subject.getId() )
               : result.createResource( subject.getURI() );
         final Property newPredicate = result.createProperty( statement.getPredicate().getURI() );
         final RDFNode newObject;
         if ( statement.getObject().isURIResource() ) {
            newObject = result.createResource( statement.getObject().asResource().getURI() );
         } else if ( statement.getObject().isLiteral() ) {
            newObject = statement.getObject();
         } else if ( statement.getObject().isAnon() ) {
            newObject = result.createResource( statement.getObject().asResource().getId() );
            result.add( getModelElementDefinition( statement.getObject().asResource() ) );
         } else {
            newObject = statement.getObject();
         }
         result.add( newSubject, newPredicate, newObject );
      } );
      return result;
   }

   public static String modelToString( final Model model ) {
      final StringWriter stringWriter = new StringWriter();
      model.write( stringWriter, "TURTLE" );
      return stringWriter.toString();
   }
}
