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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.shacl.constraint.Constraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinCountConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.SparqlConstraint;
import org.eclipse.esmf.aspectmodel.shacl.path.PathNodeRetriever;
import org.eclipse.esmf.aspectmodel.shacl.path.PredicatePath;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import com.google.common.collect.Streams;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

/**
 * Implementation of a SHACL engine that allows validation on a per-element basis: {@link #validateElement(Resource)} can be used to
 * retrieve validation results only for this specific resource.
 */
public class ShaclValidator {
   private final List<Shape.Node> shapes;
   private final Map<Resource, List<Shape.Node>> shapesWithClassTargets;
   private final Model shapesModel;
   private final PathNodeRetriever retriever = new PathNodeRetriever();

   /**
    * Constructor to provide a custom RDF model containing SHACL shapes
    *
    * @param shapesModel the shapes model
    */
   public ShaclValidator( final Model shapesModel ) {
      this.shapesModel = shapesModel;
      shapes = new ShapeLoader().apply( shapesModel );
      final Set<Resource> distinctTargetResources = shapes.stream()
            .flatMap( shape -> shape.attributes().targetClass().stream() )
            .collect( Collectors.toSet() );
      shapesWithClassTargets = distinctTargetResources.stream().collect(
            Collectors.toMap( Function.identity(),
                  targetResource -> shapes.stream()
                        .filter( shape ->
                              shape.attributes().targetClass().map( resource -> resource.equals( targetResource ) ).orElse( false ) )
                        .toList() ) );
   }

   /**
    * Validates a model element using the SHACL shapes the validator was initialized with.
    * If you have more than one element to validate, prefer the method {@link #validateElements(List)} to calling this method in a loop
    * for better performance.
    * {@link Resource#getModel()} on the element must not return null, i.e., the resource may not be created using
    * {@link ResourceFactory#createProperty(String)}, but instead must be created via {@link Model#createResource(String)}.
    *
    * @param element the element to be validated
    * @return the list of {@link Violation}s if there are violations
    */
   public List<Violation> validateElement( final Resource element ) {
      final Map<Resource, List<Shape.Node>> sparqlTargets = findSparqlTargets( element.getModel() );
      return validateElement( element, sparqlTargets, element.getModel() );
   }

   private List<Violation> validateElement( final Resource element, final Map<Resource, List<Shape.Node>> sparqlTargets,
         final Model resolvedModel ) {
      final List<Violation> violations = new ArrayList<>();
      for ( final Shape.Node shape : targetClassShapesThatApplyToElement( element, resolvedModel ) ) {
         violations.addAll( validateShapeForElement( element, shape, resolvedModel ) );
      }
      for ( final Shape.Node shape : targetSubjectShapesThatApplyToElement( element ) ) {
         violations.addAll( validateShapeForElement( element, shape, resolvedModel ) );
      }
      for ( final Shape.Node shape : targetObjectShapesThatApplyToElement( element ) ) {
         violations.addAll( validateShapeForElement( element, shape, resolvedModel ) );
      }
      for ( final Shape.Node shape : targetNodeShapesThatApplyToElement( element ) ) {
         violations.addAll( validateShapeForElement( element, shape, resolvedModel ) );
      }
      if ( sparqlTargets.containsKey( element ) ) {
         for ( final Shape.Node shape : sparqlTargets.get( element ) ) {
            violations.addAll( validateShapeForElement( element, shape, resolvedModel ) );
         }
      }
      return violations;
   }

   /**
    * Validates a model using the SHACL shapes the validator was initialized with.
    *
    * @param model the model to be validated
    * @return the list of {@link Violation}s if there are violations
    */
   @SuppressWarnings( "UnstableApiUsage" ) // Usage of Streams.stream is deemed ok
   public List<Violation> validateModel( final VersionedModel model ) {
      final Map<Resource, List<Shape.Node>> sparqlTargetsWithShapes = findSparqlTargets( model.getRawModel() );
      return Streams.stream( model.getRawModel().listStatements( null, RDF.type, (RDFNode) null ) )
            .map( Statement::getSubject )
            .filter( Resource::isURIResource )
            .flatMap( element -> validateElement( element, sparqlTargetsWithShapes, model.getModel() ).stream() )
            .toList();
   }

   private Map<Resource, List<Shape.Node>> findSparqlTargets( final Model model ) {
      final Map<Resource, List<Shape.Node>> resourceShapes = new HashMap<>();
      for ( final Shape.Node shape : targetSparqlShapes() ) {
         final List<Resource> shapeTargets = querySparqlTargets( model, shape.attributes().targetSparql().orElseThrow( () ->
               new ShaclValidationException( "SPARQL node shape is missing a target SPARQL expression" ) ) );
         for ( final Resource node : shapeTargets ) {
            addResourceShape( resourceShapes, node, shape );
         }
      }
      return resourceShapes;
   }

   // single resource can be sparql target to more than one shape
   private void addResourceShape( final Map<Resource, List<Shape.Node>> map, final Resource resource, final Shape.Node shape ) {
      if ( map.containsKey( resource ) ) {
         map.get( resource ).add( shape );
      } else {
         final ArrayList<Shape.Node> shapes = new ArrayList<>();
         shapes.add( shape );
         map.put( resource, shapes );
      }
   }

   private List<Resource> querySparqlTargets( final Model model, final Query query ) {
      final List<Resource> targets = new ArrayList<>();
      try ( final QueryExecution queryExecution = QueryExecutionFactory.create( query, model ) ) {
         final ResultSet resultSet = queryExecution.execSelect();
         while ( resultSet.hasNext() ) {
            final QuerySolution solution = resultSet.next();
            targets.add( solution.getResource( "this" ) );
         }
      }
      return targets;
   }

   public List<Violation> validateElements( final List<Resource> elements ) {
      final Map<Resource, List<Shape.Node>> sparqlTargets = !elements.isEmpty() ?
            findSparqlTargets( elements.get( 0 ).getModel() ) : Map.of();
      return elements.stream().flatMap( element -> validateElement( element, sparqlTargets, element.getModel() ).stream() ).toList();
   }

   public List<Violation> validateShapeForElement( final Resource element, final Shape.Node nodeShape, final Model resolvedModel ) {
      return validateShapeForElement( element, nodeShape, resolvedModel, Optional.empty(), Optional.empty() );
   }

   public List<Violation> validateShapeForElement( final Resource element, final Shape.Node nodeShape, final Model resolvedModel,
         final Optional<Resource> parentElement, final Optional<Property> parentProperty ) {
      final List<Violation> violations = new ArrayList<>();
      for ( final Shape.Property propertyShape : nodeShape.properties() ) {
         violations.addAll( validateShapeForElement( element, nodeShape, propertyShape, resolvedModel, parentElement, parentProperty ) );
      }

      final EvaluationContext context = new EvaluationContext( element, nodeShape, Optional.empty(), Optional.empty(),
            parentElement, parentProperty, List.of(), this, resolvedModel );
      for ( final Constraint constraint : nodeShape.attributes().constraints() ) {
         if ( !constraint.canBeUsedOnNodeShapes() ) {
            continue;
         }
         violations.addAll( constraint.apply( element, context ) );
      }
      return violations;
   }

   public List<Violation> validateShapeForElement( final Resource element, final Shape.Node nodeShape, final Shape.Property propertyShape,
         final Model resolvedModel, final Optional<Resource> parentElement, final Optional<Property> parentProperty ) {
      final List<Violation> violations = new ArrayList<>();

      for ( final Constraint constraint : propertyShape.attributes().constraints() ) {
         final List<Statement> reachableNodes = propertyShape.path().accept( element, retriever );
         // For all values that are present on the target node, check the applicable shapes and collect violations
         for ( final Statement assertion : reachableNodes ) {
            final EvaluationContext context = new EvaluationContext( element, nodeShape, Optional.of( propertyShape ),
                  Optional.of( assertion.getPredicate() ),
                  parentElement, parentProperty, List.of( assertion ), this, resolvedModel );
            violations.addAll( constraint.apply( assertion.getObject(), context ) );
         }

         // important detail: Sparql constraints must run independent of whether there are any matches via the sh:path property or not
         // ( the check could be the verification whether the property exists )
         if ( reachableNodes.isEmpty() && constraint instanceof SparqlConstraint ) {
            final EvaluationContext context = new EvaluationContext( element, nodeShape, Optional.of( propertyShape ), Optional.empty(),
                  parentElement, parentProperty, List.of(), this, resolvedModel );
            violations.addAll( constraint.apply( null, context ) );
         }

         // MinCount needs to be handled separately: If the property is not used at all on the target node, but a MinCount constraints
         // >= 1 exists, a violation must be emitted even though no value for the property exists
         if ( reachableNodes.isEmpty() && constraint instanceof MinCountConstraint
               && propertyShape.path() instanceof final PredicatePath predicatePath ) {
            final Property rdfProperty = resolvedModel.createProperty( predicatePath.predicate().getURI() );
            final EvaluationContext context = new EvaluationContext( element, nodeShape, Optional.of( propertyShape ),
                  Optional.of( rdfProperty ), parentElement, parentProperty, List.of(), this, resolvedModel );
            violations.addAll( constraint.apply( null, context ) );
         }
      }

      return violations;
   }

   /**
    * Returns the shapes that apply to the element because the element has a type (or the type has a transitive supertype) that
    * is given as sh:targetClass
    *
    * @param element a model element
    * @return the stream of shapes
    */
   private Set<Shape.Node> targetClassShapesThatApplyToElement( final Resource element, final Model resolvedModel ) {
      return RdfTypes.typesOfElement( element, resolvedModel ).stream()
            .flatMap( type -> Optional.ofNullable( shapesWithClassTargets.get( type ) ).stream() )
            .flatMap( List::stream )
            .collect( Collectors.toSet() );
   }

   /**
    * Returns the shapes that apply to the element because the element uses a property which is given as sh:targetSubjectsOf
    *
    * @param element a model element
    * @return the stream of shapes
    */
   private Set<Shape.Node> targetSubjectShapesThatApplyToElement( final Resource element ) {
      return shapes.stream()
            .filter( shape ->
                  shape.attributes().targetSubjectsOf().map( property -> element.getProperty( property ) != null ).orElse( false ) )
            .collect( Collectors.toSet() );
   }

   private Set<Shape.Node> targetObjectShapesThatApplyToElement( final Resource element ) {
      return shapes.stream()
            .filter( shape ->
                  shape.attributes().targetObjectsOf().map( property -> element.getProperty( property ) != null ).orElse( false ) )
            .collect( Collectors.toSet() );
   }

   private Set<Shape.Node> targetNodeShapesThatApplyToElement( final Resource element ) {
      return shapes.stream()
            .filter( shape ->
                  shape.attributes().targetNode().map( element::equals ).orElse( false ) )
            .collect( Collectors.toSet() );
   }

   private Set<Shape.Node> targetSparqlShapes() {
      return shapes.stream()
            .filter( shape -> shape.attributes().targetSparql().isPresent() )
            .collect( Collectors.toSet() );
   }

   public List<Shape.Node> getShapes() {
      return shapes;
   }

   public Model getShapesModel() {
      return shapesModel;
   }

   public PathNodeRetriever getRetriever() {
      return retriever;
   }
}
