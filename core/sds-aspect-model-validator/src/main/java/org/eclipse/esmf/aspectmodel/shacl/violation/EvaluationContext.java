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

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.eclipse.esmf.aspectmodel.shacl.ShaclValidator;
import org.eclipse.esmf.aspectmodel.shacl.Shape;

public record EvaluationContext(Resource element, Shape shape, Optional<Property> property, List<Statement> offendingStatements, ShaclValidator validator) {
   public EvaluationContext withProperty( final Property newProperty ) {
      return new EvaluationContext( element(), shape(), Optional.of( newProperty ), offendingStatements(), validator() );
   }

   public EvaluationContext withElement( final Resource newElement ) {
      return new EvaluationContext( newElement, shape(), property(), offendingStatements(), validator() );
   }

   public EvaluationContext withOffendingStatements( final List<Statement> newOffendingStatements ) {
      return new EvaluationContext( element(), shape(), property(), newOffendingStatements, validator() );
   }
}
