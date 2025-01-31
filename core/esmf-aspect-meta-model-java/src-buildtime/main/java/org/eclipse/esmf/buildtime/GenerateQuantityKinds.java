/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.buildtime;

import java.nio.file.Path;
import java.util.stream.Collectors;

import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

/**
 * Generates the QuantityKinds enumeration
 */
public class GenerateQuantityKinds extends BuildtimeCodeGenerator {
   private final Model inputModel = loadMetaModelFile( "unit", "units.ttl" );

   protected GenerateQuantityKinds( final Path srcBuildTimePath, final Path srcGenPath ) {
      super( srcBuildTimePath, srcGenPath, "QuantityKinds", "org.eclipse.esmf.metamodel" );
   }

   @Override
   protected String interpolateVariable( final String variableName ) {
      return switch ( variableName ) {
         case "joinedQuantityKinds" -> generateQuantityKinds();
         case "generator" -> getClass().getCanonicalName();
         default -> throw new RuntimeException( "Unexpected template variable: " + variableName );
      };
   }

   private String generateQuantityKinds() {
      return Streams.stream( inputModel.listStatements( null, RDF.type, SammNs.SAMM.QuantityKind() ) ).map( Statement::getSubject )
            .map( quantityKind -> {
               final String name = quantityKind.getLocalName();
               final String label = attributeValues( quantityKind, SammNs.SAMM.preferredName() ).stream().map( Statement::getString )
                     .findAny()
                     .orElse( "" );
               return "   %s( \"%s\", \"%s\" )".formatted( toUpperSnakeCase( name ), name, label );
            } )
            .collect( Collectors.joining( ",\n" ) );
   }
}
