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

package org.eclipse.esmf.aspectmodel.shacl;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 * Provides functions to find out information about the types of resources
 */
public class RdfTypes {
   public static List<Resource> superTypesOfType( final Resource type, final Model resolvedModel ) {
      final List<Resource> types = new ArrayList<>();
      for ( final StmtIterator it = resolvedModel.listStatements( type, RDFS.subClassOf, (RDFNode) null ); it.hasNext(); ) {
         final Statement statement = it.next();
         final Resource superType = statement.getResource();
         types.add( superType );
         types.addAll( superTypesOfType( superType, resolvedModel ) );
      }
      return types;
   }

   public static List<Resource> typesOfElement( final Resource element, final Model resolvedModel ) {
      final Statement typeAssertion = element.getProperty( RDF.type );
      if ( typeAssertion == null ) {
         return List.of();
      }
      final Resource type = typeAssertion.getResource();
      return ImmutableList.<Resource> builder()
            .add( type )
            .addAll( superTypesOfType( type, resolvedModel ) )
            .build();
   }
}
