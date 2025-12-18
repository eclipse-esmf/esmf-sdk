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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.CycleViolation;
import org.eclipse.esmf.aspectmodel.validation.RdfBasedValidator;
import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionDatasetBuilder;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

/**
 * Cycle detector for SAMM models.
 * <br/>
 * Because of the limitations of the property paths in SPARQL queries, it is impossible to realize the cycle detection together with
 * other validations via SHACL shapes.
 * <br/>
 * According to graph theory:
 * A directed graph G is acyclic if and only if a depth-first search of G yields no back edges.
 * <br/>
 * So a depth-first traversal of the "resolved" (via Characteristics/Entities etc.) property references is able to deliver all cycles
 * present in the model.
 */
public class ModelCycleDetector implements RdfBasedValidator<Violation, List<Violation>> {
   final Set<Resource> discovered = new LinkedHashSet<>();
   final Set<Resource> discoveredOptionals = new HashSet<>();
   final Set<Resource> finished = new HashSet<>();

   private Query query;

   private final SAMM samm = SammNs.SAMM;
   private Model model;

   final List<Violation> cycleDetectionReport = new ArrayList<>();

   @Override
   public List<Violation> validateModel( final Model rawModel ) {
      discovered.clear();
      discoveredOptionals.clear();
      finished.clear();
      cycleDetectionReport.clear();

      model = rawModel;
      initializeQuery();

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

   private void depthFirstTraversal( final Resource currentProperty, final BiConsumer<Resource, Set<Resource>> cycleHandler ) {
      final Resource resolvedProperty = resolvePropertyReference( currentProperty );
      if ( finished.contains( resolvedProperty ) ) {
         return;
      }
      final boolean isOptional = isOptionalProperty( currentProperty );
      if ( isOptional ) {
         discoveredOptionals.add( resolvedProperty );
      }

      if ( discovered.contains( resolvedProperty ) ) {
         // found a back edge -> cycle detected; report it as such only if not broken by an optional property
         if ( !optionalPropertyAtOrBelowBackEdge( resolvedProperty ) ) {
            cycleHandler.accept( resolvedProperty, discovered );
         }
      } else {
         discovered.add( resolvedProperty );

         final List<NextHopProperty> nextHopProperties = getDirectlyReachableProperties( model,
               isOptional ? resolvedProperty : currentProperty );

         // samm-c:Either makes the task somewhat more complicated - we need to know the status of both branches (left/right)
         // to be able to decide whether there really is a cycle or not
         if ( reachedViaEither( nextHopProperties ) ) {
            final EitherCycleDetector leftBranch = new EitherCycleDetector( resolvedProperty, this::reportCycle );
            final EitherCycleDetector rightBranch = new EitherCycleDetector( resolvedProperty, this::reportCycle );
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

         discovered.remove( resolvedProperty );
         finished.add( resolvedProperty );
      }

      if ( isOptional ) {
         discoveredOptionals.remove( resolvedProperty );
      }
   }

   private boolean reachedViaEither( final List<NextHopProperty> nextHopProperties ) {
      return nextHopProperties.stream().anyMatch( property -> property.eitherStatus > 0 );
   }

   private boolean optionalPropertyAtOrBelowBackEdge( final Resource backEdge ) {
      if ( discoveredOptionals.contains( backEdge ) ) {
         return true;
      }
      final Iterator<Resource> path = discovered.iterator();
      // first find the back edge property within the current path
      while ( path.hasNext() ) {
         final Resource currentNode = path.next();
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

   private Resource resolvePropertyReference( final Resource property ) {
      if ( property.isURIResource() ) {
         return property;
      }
      return Optional.ofNullable( property.getProperty( samm.property() ) )
            .or( () -> Optional.ofNullable( property.getProperty( samm._extends() ) ) )
            .map( Statement::getObject )
            .map( RDFNode::asResource )
            .orElse( property );
   }

   private boolean isOptionalProperty( final Resource propertyNode ) {
      final Statement optional = propertyNode.getProperty( samm.optional() );
      return ( optional != null ) && optional.getBoolean();
   }

   private void reportCycle( final Resource backEdgeProperty, final Set<Resource> currentPath ) {
      reportCycle( formatCurrentCycle( backEdgeProperty, currentPath ) );
   }

   private void reportCycle( final List<Resource> cyclePath ) {
      cycleDetectionReport.add( new CycleViolation( cyclePath ) );
   }

   @SuppressWarnings( "checkstyle:LineLength" )
   private void initializeQuery() {
      final String currentVersionPrefixes = SammNs.wellKnownNamespaces()
            .map( ns -> "prefix %s: <%s>".formatted( ns.getShortForm(), ns.getNamespace() ) )
            .collect( Collectors.joining( "\n" ) );

      //noinspection LongLine
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

   private static List<Resource> formatCurrentCycle( final Resource backEdgeProperty, final Set<Resource> currentPath ) {
      return Stream.concat( currentPath.stream(), Stream.of( backEdgeProperty ) ).toList();
   }

   private List<NextHopProperty> getDirectlyReachableProperties( final Model model, final Resource currentProperty ) {
      final List<NextHopProperty> nextHopProperties = new ArrayList<>();
      try ( final QueryExecution qexec = new QueryExecutionDatasetBuilder()
            .substitution( "currentProperty", currentProperty )
            .query( query )
            .model( model )
            .build() ) {
         final ResultSet results = qexec.execSelect();
         while ( results.hasNext() ) {
            final QuerySolution solution = results.nextSolution();
            nextHopProperties.add(
                  new NextHopProperty( solution.getResource( "reachableProperty" ), solution.getLiteral( "viaEither" ).getInt() ) );
         }
      }
      return nextHopProperties;
   }

   private static class NextHopProperty {
      public final Resource propertyNode;
      public final int eitherStatus;

      private NextHopProperty( final Resource propertyNode, final int viaEither ) {
         this.propertyNode = propertyNode;
         eitherStatus = viaEither;
      }
   }

   private static class EitherCycleDetector {
      private final Resource eitherProperty;
      private final List<Resource> breakableCycles = new ArrayList<>();
      private final BiConsumer<Resource, Set<Resource>> cycleHandler;

      EitherCycleDetector( final Resource eitherProperty, final BiConsumer<Resource, Set<Resource>> cycleHandler ) {
         this.eitherProperty = eitherProperty;
         this.cycleHandler = cycleHandler;
      }

      private void collectCycles( final Resource backEdgeProperty, final Set<Resource> currentPath ) {
         if ( cycleIsBreakable( backEdgeProperty, currentPath ) ) {
            breakableCycles.addAll( formatCurrentCycle( backEdgeProperty, currentPath ) );
         } else {  // unbreakable cycles are simply immediately reported and not retained for later evaluation
            cycleHandler.accept( backEdgeProperty, currentPath );
         }
      }

      // Cycles involving samm-c:Either can be considered breakable only if they "encompass" the property characterized by the Either
      // construct.
      // Consider these two examples: ( E is the Either property )
      // a -> E -> b -> c -> a : this cycle can be broken by the other branch of the Either construct
      // a -> E -> b -> c -> b : this cycle is unbreakable and can be reported immediately
      private boolean cycleIsBreakable( final Resource backEdgePropertyName, final Set<Resource> currentPath ) {
         final Resource firstInPath = currentPath.stream()
               .filter( propertyName -> propertyName.equals( backEdgePropertyName ) || propertyName.equals( eitherProperty ) )
               .findFirst().get();
         return backEdgePropertyName.equals( firstInPath );
      }

      boolean hasBreakableCycles() {
         return !breakableCycles.isEmpty();
      }

      void reportCycles( final Consumer<List<Resource>> reportCycle ) {
         reportCycle.accept( breakableCycles );
      }
   }
}
