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

package org.eclipse.esmf.aspectmodel.shacl.violation;

import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.shacl.ShaclValidator;
import org.eclipse.esmf.aspectmodel.shacl.Shape;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

/**
 * The state during the evaluation of SHACL constraints
 *
 * @param element the resource a constraint is evaluated on
 * @param shape the shape that is being evaluated
 * @param propertyShape if a property shape for a given node shape is evaluated, it is given here
 * @param property if a property shape for a given node shape is evaluated, this is the corresponding property
 * @param offendingStatements one ore more statements that are the cause of the violation if one occurs
 * @param validator the validator
 * @param resolvedModel the model being evaluated
 */
public record EvaluationContext( Resource element, Shape shape, Optional<Shape.Property> propertyShape, Optional<Property> property,
      List<Statement> offendingStatements, ShaclValidator validator, Model resolvedModel ) {
   public EvaluationContext withProperty( final Property newProperty ) {
      return new EvaluationContext( element(), shape(), propertyShape(), Optional.of( newProperty ), offendingStatements(), validator(),
            resolvedModel );
   }

   public EvaluationContext withElement( final Resource newElement ) {
      return new EvaluationContext( newElement, shape(), propertyShape(), property(), offendingStatements(), validator(), resolvedModel );
   }

   public EvaluationContext withOffendingStatements( final List<Statement> newOffendingStatements ) {
      return new EvaluationContext( element(), shape(), propertyShape(), property(), newOffendingStatements, validator(), resolvedModel );
   }
}
