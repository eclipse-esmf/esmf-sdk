/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.turtle.languageserver.turtle.completion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.aspectmodel.shacl.ShapeLoader;
import org.eclipse.esmf.aspectmodel.shacl.constraint.ClassConstraint;
import org.eclipse.esmf.aspectmodel.shacl.path.PredicatePath;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

/**
 * The classes a SAMM predicate expects as its object, read from the {@code sh:path} and
 * {@code sh:class}
 * constraints of the meta model shapes, plus the classes of predicates whose shapes restrict the
 * object only via
 * SPARQL or {@code mmm:listType}.
 */
public final class SammShapeUtils {
   private static final Model META_MODEL_DEFINITIONS = MetaModelFile.metaModelDefinitions();

   /**
    * IRIs of all predicates of the SAMM shapes, filled by the static block below
    */
   private static final Set<String> SHAPE_PREDICATES = new HashSet<>();

   /**
    * Predicate IRI -> classes the SAMM shapes expect for its object
    */
   public static final Map<String, Set<Resource>> EXPECTED_CLASSES;

   /**
    * IRIs of all predicates of the SAMM shapes, including those without an expected class
    */
   public static final Set<String> PREDICATES;

   /*
    * Reads the expected classes of the predicates from the meta model shapes, and adds all predicates
    * of the shapes to
    * SHAPE_PREDICATES
    */
   static {
      final Map<String, Set<Resource>> expectedClasses = new HashMap<>();
      for ( final var shape : new ShapeLoader().apply( MetaModelFile.metaModelShapes() ) ) {
         for ( final var property : shape.properties() ) {
            if ( property.path() instanceof final PredicatePath path ) {
               final String predicate = path.predicate().getURI();
               SHAPE_PREDICATES.add( predicate );
               for ( final var constraint : property.attributes().constraints() ) {
                  if ( constraint instanceof final ClassConstraint classConstraint ) {
                     expectedClasses.computeIfAbsent( predicate, key -> new HashSet<>() ).add( classConstraint.allowedClass() );
                  }
               }
            }
         }
      }
      // samm:dataType accepts an XSD data type or an entity
      expectedClasses.put( SammNs.SAMM.dataType().getURI(),
            Set.of( META_MODEL_DEFINITIONS.createResource( SammNs.SAMM.Entity().getURI() ),
                  META_MODEL_DEFINITIONS.createResource( SammNs.SAMM.AbstractEntity().getURI() ) ) );
      // samm:properties is a list of samm:Property
      expectedClasses.put( SammNs.SAMM.properties().getURI(),
            Set.of( META_MODEL_DEFINITIONS.createResource( SammNs.SAMM.Property().getURI() ) ) );
      EXPECTED_CLASSES = expectedClasses;
      PREDICATES = Set.copyOf( SHAPE_PREDICATES );
   }

   private SammShapeUtils() {}

   /**
    * @return whether the type is one of the given classes or a subclass of one of them
    */
   public static boolean isSubclassOfAny( final String typeIri, final Set<Resource> classes ) {
      final Resource type = META_MODEL_DEFINITIONS.createResource( typeIri );
      return classes.stream().anyMatch( expected -> RdfUtil.isTypeOrSubtypeOf( type, expected ) );
   }

   /**
    * The IRIs of the subjects of a document whose type is (a subclass of) one of the classes the SAMM
    * shapes expect
    * for the object of the predicate. Empty if the predicate has no expected classes, i.e. any subject
    * is allowed.
    *
    * @param subjectTypes subject IRI -> types of the document
    */
   public static Optional<Set<String>> expectedSubjects( final Map<String, Set<String>> subjectTypes,
         final Optional<String> predicateIri ) {
      final Set<Resource> expectedClasses = predicateIri.map( iri -> EXPECTED_CLASSES.getOrDefault( iri, Set.of() ) )
            .orElse( Set.of() );
      if ( expectedClasses.isEmpty() ) {
         return Optional.empty();
      }
      return Optional.of( subjectTypes.entrySet().stream()
            .filter( entry -> entry.getValue().stream().anyMatch( type -> isSubclassOfAny( type, expectedClasses ) ) )
            .map( Map.Entry::getKey )
            .collect( Collectors.toSet() ) );
   }

   /**
    * @return the IRIs of the predicates whose shapes expect objects of exactly the given class
    */
   public static Set<String> predicatesExpecting( final Resource expectedClass ) {
      return EXPECTED_CLASSES.entrySet().stream()
            .filter( entry -> entry.getValue().stream().anyMatch( clazz -> clazz.getURI().equals( expectedClass.getURI() ) ) )
            .map( Map.Entry::getKey )
            .collect( Collectors.toUnmodifiableSet() );
   }
}
