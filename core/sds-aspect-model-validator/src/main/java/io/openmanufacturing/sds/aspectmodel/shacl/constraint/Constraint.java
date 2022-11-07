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

import java.util.List;
import java.util.function.BiFunction;

import org.apache.jena.rdf.model.RDFNode;

import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;

/**
 * Represents a SHACL constraint component as a function that takes the <a href="https://www.w3.org/TR/shacl/#value-nodes">value node</a> as input
 * and returns a (possibly empty) list of violations.
 *
 * Not implemented: sh:qualifiedValueShape, sh:qualifiedMinCount, sh:qualifiedMaxCount
 */
public interface Constraint extends BiFunction<RDFNode, EvaluationContext, List<Violation>> {
   default boolean canBeUsedOnNodeShapes() {
      return true;
   }

   String name();

   <T> T accept( Visitor<T> visitor );

   interface Visitor<T> {
      T visit( Constraint constraint );

      default T visitAllowedLanguagesConstraint( final AllowedLanguagesConstraint constraint ) {
         return visit( constraint );
      }

      default T visitAllowedValuesConstraint( final AllowedValuesConstraint constraint ) {
         return visit( constraint );
      }

      default T visitAndConstraint( final AndConstraint constraint ) {
         return visit( constraint );
      }

      default T visitClassConstraint( final ClassConstraint constraint ) {
         return visit( constraint );
      }

      default T visitClosedConstraint( final ClosedConstraint constraint ) {
         return visit( constraint );
      }

      default T visitDatatypeConstraint( final DatatypeConstraint constraint ) {
         return visit( constraint );
      }

      default T visitDisjointConstraint( final DisjointConstraint constraint ) {
         return visit( constraint );
      }

      default T visitEqualsConstraint( final EqualsConstraint constraint ) {
         return visit( constraint );
      }

      default T visitHasValueConstraint( final HasValueConstraint constraint ) {
         return visit( constraint );
      }

      default T visitLessThanConstraint( final LessThanConstraint constraint ) {
         return visit( constraint );
      }

      default T visitLessThanOrEqualsConstraint( final LessThanOrEqualsConstraint constraint ) {
         return visit( constraint );
      }

      default T visitMaxCountConstraint( final MaxCountConstraint constraint ) {
         return visit( constraint );
      }

      default T visitMaxExclusiveConstraint( final MaxExclusiveConstraint constraint ) {
         return visit( constraint );
      }

      default T visitMaxInclusiveConstraint( final MaxInclusiveConstraint constraint ) {
         return visit( constraint );
      }

      default T visitMaxLengthConstraint( final MaxLengthConstraint constraint ) {
         return visit( constraint );
      }

      default T visitMinCountConstraint( final MinCountConstraint constraint ) {
         return visit( constraint );
      }

      default T visitMinExclusiveConstraint( final MinExclusiveConstraint constraint ) {
         return visit( constraint );
      }

      default T visitMinInclusiveConstraint( final MinInclusiveConstraint constraint ) {
         return visit( constraint );
      }

      default T visitMinLengthConstraint( final MinLengthConstraint constraint ) {
         return visit( constraint );
      }

      default T visitNodeConstraint( final NodeConstraint constraint ) {
         return visit( constraint );
      }

      default T visitNodeKindConstraint( final NodeKindConstraint constraint ) {
         return visit( constraint );
      }

      default T visitNotConstraint( final NotConstraint constraint ) {
         return visit( constraint );
      }

      default T visitOrConstraint( final OrConstraint constraint ) {
         return visit( constraint );
      }

      default T visitPatternConstraint( final PatternConstraint constraint ) {
         return visit( constraint );
      }

      default T visitSparqlConstraint( final SparqlConstraint constraint ) {
         return visit( constraint );
      }

      default T visitUniqueLangConstraint( final UniqueLangConstraint constraint ) {
         return visit( constraint );
      }

      default T visitXoneConstraint( final XoneConstraint constraint ) {
         return visit( constraint );
      }

      default T visitJsConstraint( final JsConstraint constraint ) {
         return visit( constraint );
      }
   }
}
