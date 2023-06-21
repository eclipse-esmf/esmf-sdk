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

package org.eclipse.esmf.aspectmodel.shacl.constraint;

import java.util.List;
import java.util.function.BiFunction;

import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Represents a SHACL constraint component as a function that takes the <a href="https://www.w3.org/TR/shacl/#value-nodes">value node</a> as
 * input and returns a (possibly empty) list of violations.
 * <br/>
 * Not implemented: sh:qualifiedValueShape, sh:qualifiedMinCount, sh:qualifiedMaxCount
 */
public interface Constraint extends BiFunction<RDFNode, EvaluationContext, List<Violation>> {
   /**
    * Determines whether this constraint can be used on a node shape, as certain constraints (e.g., sh:minCount) can only be used on
    * property shapes.
    *
    * @return the allowed value
    */
   default boolean canBeUsedOnNodeShapes() {
      return true;
   }

   /**
    * The name of the constraint as given by sh:name
    *
    * @return the name
    */
   String name();

   /**
    * Visitor pattern for Constraints: Accept the constraint visitor
    *
    * @param visitor the visitor
    * @param <T> the visitor's result type
    * @return the value corresponding to the visitor
    */
   <T> T accept( Visitor<T> visitor );

   /**
    * The visitor interface for Constraints
    *
    * @param <T> the return type for the visitor
    */
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
