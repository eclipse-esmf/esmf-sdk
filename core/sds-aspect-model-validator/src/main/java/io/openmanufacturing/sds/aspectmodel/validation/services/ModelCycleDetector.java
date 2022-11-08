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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.vocabulary.RDF;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.ProcessingViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;

/**
 * Cycle detector for BAMM models.
 *
 * Because of the limitations of the property paths in Sparql queries, it is impossible to realize the cycle detection together with
 * other validations via Shacl shapes.
 *
 * According to graph theory:
 *    A directed graph G is acyclic if and only if a depth-first search of G yields no back edges.
 *
 * So a depth-first traversal of the "resolved" (via Characteristics/Entities etc.) property references is able to deliver all cycles present in the model.
 */
public class ModelCycleDetector {

   static String ERR_CYCLE_DETECTED =
         "The Aspect Model contains a cycle which includes following properties: %s. Please remove any cycles that do not allow a finite json payload.";

   private final static String prefixes = "prefix bamm: <urn:bamm:io.openmanufacturing:meta-model:%s#> \r\n" +
         "prefix bamm-c: <urn:bamm:io.openmanufacturing:characteristic:%s#> \r\n" +
         "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \r\n";

   final Set<String> discovered = new LinkedHashSet<>();
   final Set<String> finished = new HashSet<>();

   private Query query;

   private BAMM bamm;
   private Model model;

   List<Violation> cycleDetectionReport = new ArrayList<>();

   public List<Violation> validateModel( final VersionedModel versionedModel ) {
      discovered.clear();
      finished.clear();
      cycleDetectionReport.clear();

      model = versionedModel.getModel();
      final Optional<KnownVersion> metaModelVersion = KnownVersion.fromVersionString( versionedModel.getVersion().toString() );
      bamm = new BAMM( metaModelVersion.get() );
      initializeQuery( metaModelVersion.get() );

      // we only want to investigate properties that are directly reachable from an Aspect
      final StmtIterator aspects = model.listStatements( null, RDF.type, bamm.Aspect() );
      if ( aspects.hasNext() ) {
         final Statement aspect = aspects.nextStatement();
         final Statement properties = aspect.getSubject().getProperty( bamm.properties() );
         if ( properties != null ) {
            final Iterator<RDFNode> aspectProperties = properties.getList().iterator();
            while ( aspectProperties.hasNext() ) {
               final RDFNode propRef = aspectProperties.next();
               final Resource property;
               if ( propRef.isAnon() ) {
                  if ( isOptionalProperty( propRef.asResource() ) ) {
                     continue;
                  }
                  property = resolvePropertyReference( propRef.asResource() );
               } else {
                  property = propRef.asResource();
               }
               final String propertyName = getUniqueName( property );
               if ( !discovered.contains( propertyName ) && !finished.contains( propertyName ) ) {
                  depthFirstTraversal( property, this::reportCycle );
               }
            }
         }
      }

      return cycleDetectionReport;
   }

   private void depthFirstTraversal( final Resource currentProperty, final BiConsumer<String, Set<String>> cycleHandler ) {
      final String currentPropertyName = getUniqueName( currentProperty );
      discovered.add( currentPropertyName );

      final List<NextHopProperty> nextHopProperties = getDirectlyReachableProperties( model, currentProperty );

      // bamm-c:Either makes the task somewhat more complicated - we need to know the status of both branches (left/right)
      // to be able to decide whether there really is a cycle or not
      if ( reachedViaEither( nextHopProperties ) ) {
         final EitherCycleDetector leftBranch = new EitherCycleDetector( currentPropertyName, this::reportCycle );
         final EitherCycleDetector rightBranch = new EitherCycleDetector( currentPropertyName, this::reportCycle );
         nextHopProperties.stream().filter( property -> property.eitherStatus == 1 )
               .forEach( property -> investigateProperty( property.propertyNode, leftBranch::collectCycles ) );
         nextHopProperties.stream().filter( property -> property.eitherStatus == 2 )
               .forEach( property -> investigateProperty( property.propertyNode, rightBranch::collectCycles ) );
         if ( leftBranch.hasBreakableCycles() && rightBranch.hasBreakableCycles() ) {
            // the cycles found are breakable, but they are present in both branches, resulting in an overall unbreakable cycle
            leftBranch.reportCycles( this::reportCycle );
            rightBranch.reportCycles( this::reportCycle );
         }
      } else { // "normal" path
         nextHopProperties.forEach( property -> investigateProperty( property.propertyNode, cycleHandler ) );
      }

      discovered.remove( currentPropertyName );
      finished.add( currentPropertyName );
   }

   private boolean reachedViaEither( final List<NextHopProperty> nextHopProperties ) {
      return nextHopProperties.stream().anyMatch( property -> property.eitherStatus > 0 );
   }

   private void investigateProperty( Resource propertyNode, final BiConsumer<String, Set<String>> cycleHandler ) {
      if ( propertyNode.isAnon() ) {
         // [ bamm:property :propName ; bamm:optional value ; ]
         if ( isOptionalProperty( propertyNode ) ) {
            // presence of bamm:optional = true; no need to continue on this path, the potential cycle would be broken by the optional property anyway
            return;
         }

         propertyNode = resolvePropertyReference( propertyNode );
      }

      final String propertyName = getUniqueName( propertyNode );

      if ( discovered.contains( propertyName ) ) {
         // found a back edge -> cycle detected
         cycleHandler.accept( propertyName, discovered );
      } else if ( !finished.contains( propertyName ) ) {
         depthFirstTraversal( propertyNode, cycleHandler );
      }
   }

   private Resource resolvePropertyReference( final Resource propertyNode ) {
      final Statement prop = propertyNode.getProperty( bamm.property() );
      if ( prop != null ) {
         return prop.getObject().asResource();
      }
      return propertyNode;
   }

   private boolean isOptionalProperty( final Resource propertyNode ) {
      final Statement optional = propertyNode.getProperty( bamm.optional() );
      return (optional != null) && optional.getBoolean();
   }

   private String getUniqueName( final Resource property ) {
      // Ugly special case: when extending Entities, the property name will always be the same ([ bamm:extends bamm-e:value ; bamm:characteristic :someChara ]),
      // so we need a unique name in case more than one extending Entity exists in the model
      if ( property.isAnon() ) {
         if ( property.getProperty( bamm._extends() ) != null ) {
            return findExtendingEntityName( property ) + "|" + model.shortForm( property.getProperty( bamm._extends() ).getObject().asResource().getURI() );
         }
         // safety net
         return property.toString();
      }

      return model.shortForm( property.getURI() );
   }

   private String findExtendingEntityName( final Resource extendsProperty ) {
      return model.listSubjectsWithProperty( bamm._extends() )
            .filterKeep( entity -> entity.getProperty( bamm.properties() ) != null )
            .filterKeep( entity -> entity.getProperty( bamm.properties() ).getList().contains( extendsProperty ) )
            .mapWith( resource -> model.shortForm( resource.getURI() ) )
            .nextOptional().orElse( extendsProperty.toString() );
   }

   private void reportCycle( final String backEdgePropertyName, final Set<String> currentPath ) {
      reportCycle( formatCurrentCycle( backEdgePropertyName, currentPath ) );
   }

   private void reportCycle( final String cyclePath ) {
      cycleDetectionReport.add( new ProcessingViolation( String.format( ERR_CYCLE_DETECTED, cyclePath ), null ) );
   }

   private void initializeQuery( final KnownVersion metaModelVersion ) {
      final String currentVersionPrefixes = String.format( prefixes, metaModelVersion.toVersionString(), metaModelVersion.toVersionString() );
      final String queryString = String.format(
            "%s select ?reachableProperty ?viaEither"
                  + "    where {"
                  + "      {"
                  + "        ?currentProperty bamm:characteristic/bamm-c:baseCharacteristic*/bamm-c:left/bamm:dataType/bamm:properties/rdf:rest*/rdf:first ?reachableProperty"
                  + "        bind (1 as ?viaEither)"
                  + "      }"
                  + "      union"
                  + "      {"
                  + "        ?currentProperty bamm:characteristic/bamm-c:baseCharacteristic*/bamm-c:right/bamm:dataType/bamm:properties/rdf:rest*/rdf:first ?reachableProperty"
                  + "        bind (2 as ?viaEither)"
                  + "      }"
                  + "      union"
                  + "      {"
                  + "        ?currentProperty bamm:characteristic/bamm-c:baseCharacteristic*/bamm:dataType/bamm:properties/rdf:rest*/rdf:first ?reachableProperty"
                  + "        bind (0 as ?viaEither)"
                  + "      }"
                  + "}",
            currentVersionPrefixes );
      query = QueryFactory.create( queryString );
   }

   private static String formatCurrentCycle( final String backEdgePropertyName, final Set<String> currentPath ) {
      return String.join( " -> ", currentPath ) + " -> " + backEdgePropertyName;
   }

   private List<NextHopProperty> getDirectlyReachableProperties( final Model model, final Resource currentProperty ) {
      final List<NextHopProperty> nextHopProperties = new ArrayList<>();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, model ) ) {
         qexec.setInitialBinding( Binding.builder().add( Var.alloc( "currentProperty" ), currentProperty.asNode() ).build() );
         final ResultSet results = qexec.execSelect();
         while ( results.hasNext() ) {
            final QuerySolution solution = results.nextSolution();
            nextHopProperties.add( new NextHopProperty( solution.getResource( "reachableProperty" ), solution.getLiteral( "viaEither" ).getInt() ) );
         }
      }
      return nextHopProperties;
   }

   private static class NextHopProperty {
      public final Resource propertyNode;
      public final int eitherStatus;

      public NextHopProperty( final Resource propertyNode, final int viaEither ) {
         this.propertyNode = propertyNode;
         eitherStatus = viaEither;
      }
   }

   private static class EitherCycleDetector {
      private final String eitherPropertyName;
      private final List<String> breakableCycles = new ArrayList<>();
      private final BiConsumer<String, Set<String>> cycleHandler;

      EitherCycleDetector( final String eitherPropertyName, final BiConsumer<String, Set<String>> cycleHandler ) {
         this.eitherPropertyName = eitherPropertyName;
         this.cycleHandler = cycleHandler;
      }

      private void collectCycles( final String backEdgePropertyName, final Set<String> currentPath ) {
         if ( cycleIsBreakable( backEdgePropertyName, currentPath ) ) {
            breakableCycles.add( formatCurrentCycle( backEdgePropertyName, currentPath ) );
         } else {  // unbreakable cycles are simply immediately reported and not retained for later evaluation
            cycleHandler.accept( backEdgePropertyName, currentPath );
         }
      }

      // Cycles involving bamm-c:Either can be considered breakable only if they "encompass" the property characterized by the Either construct.
      // Consider these two examples: ( E is the Either property )
      // a -> E -> b -> c -> a : this cycle can be broken by the other branch of the Either construct
      // a -> E -> b -> c -> b : this cycle is unbreakable and can be reported immediately
      private boolean cycleIsBreakable( final String backEdgePropertyName, final Set<String> currentPath ) {
         final String firstInPath = currentPath.stream()
               .filter( propertyName -> propertyName.equals( backEdgePropertyName ) || propertyName.equals( eitherPropertyName ) )
               .findFirst().get();
         return backEdgePropertyName.equals( firstInPath );
      }

      boolean hasBreakableCycles() {
         return !breakableCycles.isEmpty();
      }

      void reportCycles( final Consumer<String> reportCycle ) {
         breakableCycles.forEach( reportCycle );
      }
   }
}
