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

package org.eclipse.esmf.aspectmodel.validation.services;

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
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.shacl.violation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.samm.KnownVersion;

/**
 * Cycle detector for SAMM models.
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

   private final static String prefixes = """
         prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:%s#>
         prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:%s#>
         prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
         """;

   final Set<String> discovered = new LinkedHashSet<>();
   final Set<String> discoveredOptionals = new HashSet<>();
   final Set<String> finished = new HashSet<>();

   private Query query;

   private SAMM samm;
   private Model model;

   List<Violation> cycleDetectionReport = new ArrayList<>();

   public List<Violation> validateModel( final VersionedModel versionedModel ) {
      discovered.clear();
      discoveredOptionals.clear();
      finished.clear();
      cycleDetectionReport.clear();

      model = versionedModel.getModel();
      final Optional<KnownVersion> metaModelVersion = KnownVersion.fromVersionString( versionedModel.getMetaModelVersion().toString() );
      samm = new SAMM( metaModelVersion.get() );
      initializeQuery( metaModelVersion.get() );

      // we only want to investigate properties that are directly reachable from an Aspect
      final StmtIterator aspects = model.listStatements( null, RDF.type, samm.Aspect() );
      if ( aspects.hasNext() ) {
         final Statement aspect = aspects.nextStatement();
         final Statement properties = aspect.getSubject().getProperty( samm.properties() );
         if ( properties != null ) {
            final Iterator<RDFNode> aspectProperties = properties.getList().iterator();
            while ( aspectProperties.hasNext() ) {
               final RDFNode propRef = aspectProperties.next();
               depthFirstTraversal( propRef.asResource(), this::reportCycle );
            }
         }
      }

      return cycleDetectionReport;
   }

   private void depthFirstTraversal( final Resource currentProperty, final BiConsumer<String, Set<String>> cycleHandler ) {
      final Resource resolvedProperty = currentProperty.isAnon() ? resolvePropertyReference( currentProperty.asResource() ) : currentProperty.asResource();
      final String currentPropertyName = getUniqueName( resolvedProperty );
      if ( finished.contains( currentPropertyName ) ) {
         return;
      }
      final boolean isOptional = isOptionalProperty( currentProperty );
      if ( isOptional ) {
         discoveredOptionals.add( currentPropertyName );
      }

      if ( discovered.contains( currentPropertyName ) ) {
         // found a back edge -> cycle detected; report it as such only if not broken by an optional property
         if ( !optionalPropertyAtOrBelowBackEdge( currentPropertyName ) ) {
            cycleHandler.accept( currentPropertyName, discovered );
         }
      } else {
         discovered.add( currentPropertyName );

         final List<NextHopProperty> nextHopProperties = getDirectlyReachableProperties( model, resolvedProperty );

         // samm-c:Either makes the task somewhat more complicated - we need to know the status of both branches (left/right)
         // to be able to decide whether there really is a cycle or not
         if ( reachedViaEither( nextHopProperties ) ) {
            final EitherCycleDetector leftBranch = new EitherCycleDetector( currentPropertyName, this::reportCycle );
            final EitherCycleDetector rightBranch = new EitherCycleDetector( currentPropertyName, this::reportCycle );
            nextHopProperties.stream().filter( property -> property.eitherStatus == 1 )
                  .forEach( property -> depthFirstTraversal( property.propertyNode, leftBranch::collectCycles ) );
            nextHopProperties.stream().filter( property -> property.eitherStatus == 2 )
                  .forEach( property -> depthFirstTraversal( property.propertyNode, rightBranch::collectCycles ) );
            if ( leftBranch.hasBreakableCycles() && rightBranch.hasBreakableCycles() ) {
               // the cycles found are breakable, but they are present in both branches, resulting in an overall unbreakable cycle
               leftBranch.reportCycles( this::reportCycle );
               rightBranch.reportCycles( this::reportCycle );
            }
         } else { // "normal" path
            nextHopProperties.forEach( property -> depthFirstTraversal( property.propertyNode, cycleHandler ) );
         }

         discovered.remove( currentPropertyName );
         finished.add( currentPropertyName );
      }

      if ( isOptional ) {
         discoveredOptionals.remove( currentPropertyName );
      }
   }

   private boolean reachedViaEither( final List<NextHopProperty> nextHopProperties ) {
      return nextHopProperties.stream().anyMatch( property -> property.eitherStatus > 0 );
   }

   private boolean optionalPropertyAtOrBelowBackEdge( final String backEdge ) {
      if ( discoveredOptionals.contains( backEdge ) ) {
         return true;
      }
      final Iterator<String> path = discovered.iterator();
      // first find the back edge property within the current path
      while ( path.hasNext() ) {
         final String currentNode = path.next();
         if ( currentNode.equals( backEdge ) ) {
            break;
         }
      }
      // look for an optional property at or below the back edge property
      while ( path.hasNext() ) {
         if ( discoveredOptionals.contains( path.next() ) ) {
            return true;
         }
      }
      return false;
   }

   private Resource resolvePropertyReference( final Resource propertyNode ) {
      final Statement prop = propertyNode.getProperty( samm.property() );
      if ( prop != null ) {
         return prop.getObject().asResource();
      }
      return propertyNode;
   }

   private boolean isOptionalProperty( final Resource propertyNode ) {
      final Statement optional = propertyNode.getProperty( samm.optional() );
      return (optional != null) && optional.getBoolean();
   }

   private String getUniqueName( final Resource property ) {
      // Ugly special case: when extending Entities, the property name will always be the same ([ samm:extends samm-e:value ; samm:characteristic :someChara ]),
      // so we need a unique name in case more than one extending Entity exists in the model
      if ( property.isAnon() ) {
         if ( property.getProperty( samm._extends() ) != null ) {
            return findExtendingEntityName( property ) + "|" + model.shortForm( property.getProperty( samm._extends() ).getObject().asResource().getURI() );
         }
         // safety net
         return property.toString();
      }

      return model.shortForm( property.getURI() );
   }

   private String findExtendingEntityName( final Resource extendsProperty ) {
      return model.listSubjectsWithProperty( samm._extends() )
            .filterKeep( entity -> entity.getProperty( samm.properties() ) != null )
            .filterKeep( entity -> entity.getProperty( samm.properties() ).getList().contains( extendsProperty ) )
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
      final String queryString = String.format( """
                  %s select ?reachableProperty ?viaEither
                      where {
                        {
                          ?currentProperty samm:characteristic/samm-c:baseCharacteristic*/samm-c:left/samm:dataType/samm:properties/rdf:rest*/rdf:first ?reachableProperty
                          bind (1 as ?viaEither)
                        }
                        union
                        {
                          ?currentProperty samm:characteristic/samm-c:baseCharacteristic*/samm-c:right/samm:dataType/samm:properties/rdf:rest*/rdf:first ?reachableProperty
                          bind (2 as ?viaEither)
                        }
                        union
                        {
                          ?currentProperty samm:characteristic/samm-c:baseCharacteristic*/samm:dataType/samm:properties/rdf:rest*/rdf:first ?reachableProperty
                          bind (0 as ?viaEither)
                        }
                  }
                  """,
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

      // Cycles involving samm-c:Either can be considered breakable only if they "encompass" the property characterized by the Either construct.
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
