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

package io.openmanufacturing.sds.aspectmodel.shacl.constraint;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.UniqueLanguageViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#UniqueLangConstraintComponent">sh:uniqueLang</a>
 */
public record UniqueLangConstraint() implements Constraint {
   @Override
   public boolean canBeUsedOnNodeShapes() {
      return false;
   }

   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      if ( context.property().isEmpty() ) {
         return List.of();
      }
      final Property property = context.property().get();
      final List<String> languageList = context.element().listProperties( property )
            .mapWith( Statement::getObject )
            .filterKeep( RDFNode::isLiteral )
            .mapWith( RDFNode::asLiteral )
            .mapWith( Literal::getLanguage ).toList();
      final Set<String> seen = new HashSet<>();
      final Set<String> duplicates = languageList.stream()
            .filter( e -> !seen.add( e ) )
            .collect( Collectors.toSet() );

      return duplicates.size() == 0 ?
            List.of() :
            List.of( new UniqueLanguageViolation( context, seen ) );
   }
}
