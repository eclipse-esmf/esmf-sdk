/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.RdfBasedValidator;
import org.eclipse.esmf.aspectmodel.validation.RegularExpressionConstraintViolation;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.xerces.impl.xpath.regex.ParseException;
import org.apache.xerces.impl.xpath.regex.RegularExpression;

/**
 * Validates that the samm:value of each samm-c:RegularExpressionConstraint adheres to
 * <a href="https://www.w3.org/TR/xpath-functions-3/#regex-syntax">XQuery 1.0 and XPath 2.0 Functions and Operators</a>
 */
public class RegularExpressionExampleValueValidator implements RdfBasedValidator<Violation, List<Violation>> {
   @Override
   public List<Violation> validateModel( final Model model ) {
      return Streams.stream( model.listStatements( null, RDF.type, SammNs.SAMMC.RegularExpressionConstraint() ) )
            .filter( Objects::nonNull )
            .map( Statement::getSubject )
            .flatMap( constraint -> Streams.stream( model.listStatements( constraint, SammNs.SAMM.value(), (RDFNode) null ) )
                  .filter( Objects::nonNull )
                  .<Violation> flatMap( statement -> {
                     final String regex = statement.getLiteral().getString();
                     try {
                        new RegularExpression( regex, "m" );
                        return Stream.of();
                     } catch ( final ParseException exception ) {
                        final EvaluationContext context = new EvaluationContext( constraint, null, Optional.empty(),
                              Optional.empty(), Optional.empty(), List.of( statement ), null, model );
                        return Stream.of( new RegularExpressionConstraintViolation( context, regex ) );
                     }
                  } ) )
            .toList();
   }
}