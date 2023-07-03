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

import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.shacl.constraint.AllowedLanguagesConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.AllowedValuesConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.AndConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.ClassConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.Constraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.DatatypeConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.DisjointConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.EqualsConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.HasValueConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.LessThanConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.LessThanOrEqualsConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxCountConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxExclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxInclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxLengthConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinCountConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinExclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinInclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinLengthConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.NodeConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.NodeKindConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.NotConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.OrConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.PatternConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.XoneConstraint;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;

import org.apache.jena.rdf.model.Property;

/**
 * Creates a short textual summary of a {@link Shape} or a {@link Constraint}.
 */
public class ShapeSummarizer implements Shape.Visitor<String>, Constraint.Visitor<String> {
   private final EvaluationContext context;

   public ShapeSummarizer( final EvaluationContext context ) {
      this.context = context;
   }

   @Override
   public String visitNodeShape( final Shape.Node nodeShape ) {
      return "%s: %s".formatted( nodeShape.attributes().uri().map( context::shortUri ).orElse( "(anonymous node shape)" ),
            nodeShape.attributes().constraints().stream().map( constraint ->
                  constraint.accept( this ) ).collect( Collectors.joining( ", " ) ) );
   }

   @Override
   public String visitPropertyShape( final Shape.Property propertyShape ) {
      return "%s with path %s: %s".formatted(
            propertyShape.attributes().uri().map( context::shortUri ).orElse( "(anonymous property shape)" ), propertyShape.path(),
            propertyShape.attributes().constraints().stream().map( constraint ->
                  constraint.accept( this ) ).collect( Collectors.joining( ", " ) ) );
   }

   /**
    * Default implementation for those constraints that either have no args (e.g. sh:closed) or for which the args should be
    * excluded since they could be too long (e.g. sh:SPARQLConstraint).
    *
    * @param constraint the constraint
    * @return a short string representation of the constraint
    */
   @Override
   public String visit( final Constraint constraint ) {
      return constraint.name();
   }

   @Override
   public String visitAllowedLanguagesConstraint( final AllowedLanguagesConstraint constraint ) {
      return "%s (allowed languages: %s)".formatted( constraint.name(), constraint.allowedLanguages() );
   }

   @Override
   public String visitAllowedValuesConstraint( final AllowedValuesConstraint constraint ) {
      return "%s (allowed values: %s)".formatted( constraint.name(), constraint.allowedValues().stream().map( context::value ).toList() );
   }

   @Override
   public String visitAndConstraint( final AndConstraint constraint ) {
      return "%s (with referenced shapes: %s)".formatted( constraint.name(),
            constraint.shapes().stream().map( shape -> shape.accept( this ) ) );
   }

   @Override
   public String visitClassConstraint( final ClassConstraint constraint ) {
      return "%s (allowed class: %s)".formatted( constraint.name(), context.shortUri( constraint.allowedClass().getURI() ) );
   }

   @Override
   public String visitDatatypeConstraint( final DatatypeConstraint constraint ) {
      return "%s (allowed type: %s)".formatted( constraint.name(), context.shortUri( constraint.allowedTypeUri() ) );
   }

   @Override
   public String visitDisjointConstraint( final DisjointConstraint constraint ) {
      return "%s (disjoint with: %s which has value %s)".formatted( constraint.name(),
            context.shortUri( constraint.otherProperty().getURI() ), otherPropertyValue( constraint.otherProperty() ) );
   }

   @Override
   public String visitEqualsConstraint( final EqualsConstraint constraint ) {
      return "%s (equal to: %s which has value %s)".formatted( constraint.name(),
            context.shortUri( constraint.otherProperty().getURI() ), otherPropertyValue( constraint.otherProperty() ) );
   }

   @Override
   public String visitHasValueConstraint( final HasValueConstraint constraint ) {
      return "%s (allowed value: %s)".formatted( constraint.name(), context.value( constraint.allowedValue() ) );
   }

   @Override
   public String visitLessThanConstraint( final LessThanConstraint constraint ) {
      return "%s (less than to: %s which has value %s)".formatted( constraint.name(),
            context.shortUri( constraint.otherProperty().getURI() ), otherPropertyValue( constraint.otherProperty() ) );
   }

   @Override
   public String visitLessThanOrEqualsConstraint( final LessThanOrEqualsConstraint constraint ) {
      return "%s (less than or equal to: %s which has value %s)".formatted( constraint.name(),
            context.shortUri( constraint.otherProperty().getURI() ), otherPropertyValue( constraint.otherProperty() ) );
   }

   @Override
   public String visitMaxCountConstraint( final MaxCountConstraint constraint ) {
      return "%s (max occurrences: %d)".formatted( constraint.name(), constraint.maxCount() );
   }

   @Override
   public String visitMaxExclusiveConstraint( final MaxExclusiveConstraint constraint ) {
      return "%s (max value: %s)".formatted( constraint.name(), context.value( constraint.maxValue() ) );
   }

   @Override
   public String visitMaxInclusiveConstraint( final MaxInclusiveConstraint constraint ) {
      return "%s (max value: %s)".formatted( constraint.name(), context.value( constraint.maxValue() ) );
   }

   @Override
   public String visitMaxLengthConstraint( final MaxLengthConstraint constraint ) {
      return "%s (max length: %d)".formatted( constraint.name(), constraint.maxLength() );
   }

   @Override
   public String visitMinCountConstraint( final MinCountConstraint constraint ) {
      return "%s (min occurrences: %d)".formatted( constraint.name(), constraint.minCount() );
   }

   @Override
   public String visitMinExclusiveConstraint( final MinExclusiveConstraint constraint ) {
      return "%s (min value: %s)".formatted( constraint.name(), context.value( constraint.minValue() ) );
   }

   @Override
   public String visitMinInclusiveConstraint( final MinInclusiveConstraint constraint ) {
      return "%s (min value: %s)".formatted( constraint.name(), context.value( constraint.minValue() ) );
   }

   @Override
   public String visitMinLengthConstraint( final MinLengthConstraint constraint ) {
      return "%s (min length: %d)".formatted( constraint.name(), constraint.minLength() );
   }

   @Override
   public String visitNodeConstraint( final NodeConstraint constraint ) {
      return "%s (referenced shape: %s)".formatted( constraint.name(), constraint.targetShape().get().accept( this ) );
   }

   @Override
   public String visitNodeKindConstraint( final NodeKindConstraint constraint ) {
      return "%s (allowed node kind: %s)".formatted( constraint.name(), constraint.allowedNodeKind().humanReadable() );
   }

   @Override
   public String visitNotConstraint( final NotConstraint constraint ) {
      return "%s (negated: %s)".formatted( constraint.name(), constraint.constraint().accept( this ) );
   }

   @Override
   public String visitOrConstraint( final OrConstraint constraint ) {
      return "%s (with referenced shapes: %s)".formatted( constraint.name(),
            constraint.shapes().stream().map( shape -> shape.accept( this ) ) );
   }

   @Override
   public String visitPatternConstraint( final PatternConstraint constraint ) {
      return "%s (pattern: %s)".formatted( constraint.name(), constraint.pattern() );
   }

   @Override
   public String visitXoneConstraint( final XoneConstraint constraint ) {
      return "%s (with referenced shapes: %s)".formatted( constraint.name(),
            constraint.shapes().stream().map( shape -> shape.accept( this ) ) );
   }

   protected String otherPropertyValue( final Property property ) {
      return context.value( context.element().getProperty( property ).getObject() );
   }
}
