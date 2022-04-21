/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.serializer;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

public class RdfComparison {
   public static String hash( final Model model ) {
      return model.listStatements().toList().stream().sequential().map( RdfComparison::hash ).sorted().collect( Collectors.joining() );
   }

   private static String hash( final Statement statement ) {
      return hash( statement.getSubject() ) + hash( statement.getPredicate() ) + hash( statement.getObject() );
   }

   private static String hash( final RDFNode object ) {
      if ( object.isResource() ) {
         return hash( object.asResource() );
      }
      return object.asLiteral().getValue().toString();
   }

   private static String hash( final Resource resource ) {
      if ( resource.isURIResource() ) {
         return resource.getURI();
      }

      return hashAnonymousResource( resource );
   }

   private static String hashAnonymousResource( final Resource resource ) {
      if ( resource.getModel().contains( resource, RDF.rest, (RDFNode) null ) ) {
         final List<RDFNode> listElements = resource.as( RDFList.class ).asJavaList();
         return listElements.stream().map( RdfComparison::hash ).sorted().collect( Collectors.joining( " ", "list(", ")" ) );
      }

      return resource.listProperties().toList().stream()
            .map( statement -> hash( statement.getPredicate() ) + hash( statement.getObject() ) )
            .sorted()
            .collect( Collectors.joining() );
   }
}
