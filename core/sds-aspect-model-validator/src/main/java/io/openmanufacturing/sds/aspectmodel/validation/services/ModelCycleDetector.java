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

package io.openmanufacturing.sds.aspectmodel.validation.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.vocabulary.RDF;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationError;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationReport;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationReportBuilder;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;

/**
 * Cycle detector for the models.
 *
 * Because of the limitations of the property paths in Sparql queries, it is impossible to realize the cycle detection together with
 * other validations via Shacl shapes.
 *
 * According to graph theory:
 *    A directed graph G is acyclic if and only if a depth-first search of G yields no back edges.
 * So a depth-first traversal of the "resolved" property references (via complex types like Entities) is able to deliver us all cycles present in the model.
 */
public class ModelCycleDetector {

   private final static String prefixes = "prefix bamm: <urn:bamm:io.openmanufacturing:meta-model:%s#> \r\n" +
         "prefix bamm-c: <urn:bamm:io.openmanufacturing:characteristic:%s#> \r\n" +
         "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \r\n";

   final OrderedHashSet<String> discovered = new OrderedHashSet<>();
   final Set<String> finished = new HashSet<>();

   private Query query;
   private Property bammOptional;
   private Property bammProperty;

   List<ValidationError.Semantic> cycleReports = new ArrayList<>();

   public ValidationReport validateModel( final VersionedModel versionedModel ) {
      discovered.clear();
      finished.clear();
      cycleReports.clear();

      final Model model = versionedModel.getModel();
      final Optional<KnownVersion> metaModelVersion = KnownVersion.fromVersionString( versionedModel.getVersion().toString() );
      final BAMM bamm = new BAMM( metaModelVersion.get() );
      bammProperty = bamm.property();
      bammOptional = bamm.optional();
      initializeQuery( metaModelVersion.get() );

      final StmtIterator properties = model.listStatements( null, RDF.type, bamm.Property() );
      while ( properties.hasNext() ) {
         final Statement property = properties.nextStatement();
         final String fullPropertyName = property.getSubject().getURI();
         if ( !discovered.contains( fullPropertyName ) && !finished.contains( fullPropertyName ) ) {
            depthFirstTraversal( model, property.getSubject() );
         }
      }

      return cycleReports.isEmpty() ?
            new ValidationReport.ValidReport() :
            new ValidationReportBuilder().withValidationErrors( cycleReports ).buildInvalidReport();
   }

   private void depthFirstTraversal( final Model model, final Resource currentProperty ) {
      final String currentPropertyName = currentProperty.getURI();
      discovered.add( currentPropertyName );

      // if (either) -> continue with fake cycleReports for both branches and only add the cycle if both branches have cycles
      //    reachableObject.getObject == bammEither
      //
      // else normal handling of properties

      final List<Resource> nextHopProperties = getDirectlyReachableProperties( model, currentProperty );
      for ( Resource reachableProperty : nextHopProperties ) {

         if ( reachableProperty.isAnon() ) { // property usage of the type "[ bamm:property :propName ; bamm:optional true ; ]"
            final Statement optional = reachableProperty.getProperty( bammOptional );
            if ( (null != optional) && optional.getBoolean() ) {
               // presence of bamm:optional = true; no need to continue on this path, the potential cycle would be broken by the optional property anyway
               continue;
            }
            // resolve the property reference
            reachableProperty = reachableProperty.getProperty( bammProperty ).getObject().asResource();
         }

         final String reachablePropertyName = reachableProperty.getURI();

         if ( discovered.contains( reachablePropertyName ) ) {
            // cycle detected
            reportCycle();
         } else if ( !finished.contains( reachablePropertyName ) ) {
            depthFirstTraversal( model, reachableProperty );
         }
      }

      discovered.remove( discovered.size() - 1 ); // OrderedHashSet does not implement remove( Object )
      finished.add( currentPropertyName );
   }

   private void reportCycle() {
      final String cycledNodes = String.join( " -> ", discovered );
      cycleReports.add( new ValidationError.Semantic(
            String.format(
                  "The Aspect Model contains a cycle which includes following properties: %s. Please remove any cycles that do not allow a finite json payload.",
                  cycledNodes ),
            "", "", "ERROR", "" ) );
   }

   private void initializeQuery( final KnownVersion metaModelVersion ) {
      final String currentVersionPrefixes = String.format( prefixes, metaModelVersion.toVersionString(), metaModelVersion.toVersionString() );
      final String queryString = String.format(
            "%s select ?reachableProperty " +
                  "where { ?currentProperty bamm:characteristic/bamm-c:baseCharacteristic*/bamm-c:left*/bamm-c:right*/bamm:dataType/bamm:properties/rdf:rest*/rdf:first ?reachableProperty }",
            currentVersionPrefixes );
      query = QueryFactory.create( queryString );
   }

   private List<Resource> getDirectlyReachableProperties( final Model model, final Resource currentProperty ) {
      final List<Resource> reachableProperties = new ArrayList<>();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, model ) ) {
         qexec.setInitialBinding( Binding.builder().add( Var.alloc( "currentProperty" ), currentProperty.asNode() ).build() );
         final ResultSet results = qexec.execSelect();
         while ( results.hasNext() ) {
            final QuerySolution solution = results.nextSolution();
            reachableProperties.add( solution.getResource( "reachableProperty" ) );
         }
      }
      return reachableProperties;
   }
}
