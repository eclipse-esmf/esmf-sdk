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

package io.openmanufacturing.sds.aspectmodel.shacl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.shacl.constraint.Constraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.MinCountConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.path.PathNodeRetriever;
import io.openmanufacturing.sds.aspectmodel.shacl.path.PredicatePath;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;

/**
 * Implementation of a SHACL engine that allows validation on a per-element basis: {@link #validateElement(Resource)} can be used to retrieve validation
 * results only for this specific resource.
 */
public class ShaclValidator {
   private static final Logger LOG = LoggerFactory.getLogger( ShaclValidator.class );
   private final List<Shape.Node> shapes;
   private final Map<Resource, List<Shape.Node>> shapesWithClassTargets;
   private final Model shapesModel;

   /**
    * Constructor to provide a custom RDF model containing SHACL shapes
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
    * {@link Resource#getModel()} on the element must not return null, i.e., the resource may not be created using
    * {@link org.apache.jena.rdf.model.ResourceFactory#createProperty(String)}, but instead must be created via {@link Model#createResource(String)}.
    * @param element the element to be validated
    * @return the list of {@link Violation}s if there are violations
    */
   public List<Violation> validateElement( final Resource element ) {
      final List<Violation> violations = new ArrayList<>();
      for ( final Shape.Node shape : targetClassShapesThatApplyToElement( element ) ) {
         violations.addAll( validateShapeForElement( element, shape ) );
      }
      for ( final Shape.Node shape : targetSubjectShapesThatApplyToElement( element ) ) {
         violations.addAll( validateShapeForElement( element, shape ) );
      }
      return violations;
   }

   public List<Violation> validateElements( final List<Resource> elements ) {
      return elements.stream().flatMap( element -> validateElement( element ).stream() ).toList();
   }

   public List<Violation> validateShapeForElement( final Resource element, final Shape.Node shape ) {
      final Model model = element.getModel();
      final List<Violation> violations = new ArrayList<>();
      final PathNodeRetriever retriever = new PathNodeRetriever();
      for ( final Shape.Property property : shape.properties() ) {
         for ( final Constraint constraint : property.attributes().constraints() ) {
            final List<Statement> reachableNodes = property.path().accept( element, retriever );
            // For all values that are present on the target node, check the applicable shapes and collect violations
            for ( final Statement assertion : reachableNodes ) {
               final EvaluationContext context = new EvaluationContext( element, shape, Optional.of( assertion.getPredicate() ), reachableNodes, this );
               violations.addAll( constraint.apply( assertion.getObject(), context ) );
            }

            // MinCount needs to be handled separately: If the property is not used at all on the target node, but a MinCount constraints >= 1
            // exists, a violation must be emitted even though no value for the property exists
            if ( reachableNodes.isEmpty() && constraint instanceof MinCountConstraint && property.path() instanceof PredicatePath predicatePath ) {
               final Property rdfProperty = model.createProperty( predicatePath.predicate().getURI() );
               final EvaluationContext context = new EvaluationContext( element, shape, Optional.of( rdfProperty ), List.of(), this );
               violations.addAll( constraint.apply( null, context ) );
            }
         }
      }

      final EvaluationContext context = new EvaluationContext( element, shape, Optional.empty(), List.of(), this );
      for ( final Constraint constraint : shape.attributes().constraints() ) {
         if ( !constraint.canBeUsedOnNodeShapes() ) {
            continue;
         }
         violations.addAll( constraint.apply( element, context ) );
      }

      return violations;
   }

   /**
    * Returns the shapes that apply to the element because the element has a type (or the type has a transitive supertype) that
    * is given as sh:targetClass
    * @param element a model element
    * @return the stream of shapes
    */
   private Set<Shape.Node> targetClassShapesThatApplyToElement( final Resource element ) {
      return RdfTypes.typesOfElement( element ).stream()
            .flatMap( type -> Optional.ofNullable( shapesWithClassTargets.get( type ) ).stream() )
            .flatMap( List::stream )
            .collect( Collectors.toSet() );
   }

   /**
    * Returns the shapes that apply to the element because the element uses a property which is given as sh:targetSubjectsOf
    * @param element a model element
    * @return the stream of shapes
    */
   private Set<Shape.Node> targetSubjectShapesThatApplyToElement( final Resource element ) {
      return shapes.stream()
            .filter( shape ->
                  shape.attributes().targetSubjectsOf().map( property -> element.getProperty( property ) != null ).orElse( false ) )
            .collect( Collectors.toSet() );
   }

   public List<Shape.Node> getShapes() {
      return shapes;
   }

   public Model getShapesModel() {
      return shapesModel;
   }
}
