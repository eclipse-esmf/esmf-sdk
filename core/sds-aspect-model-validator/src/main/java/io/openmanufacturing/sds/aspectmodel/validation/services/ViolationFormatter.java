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

package io.openmanufacturing.sds.aspectmodel.validation.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.openmanufacturing.sds.aspectmodel.shacl.violation.InvalidSyntaxViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.ProcessingViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;

/**
 * Formats one or multiple {@link Violation}s in a human-readable way. Note that this is intended only for places with raw textual output,
 * such as a text console. For a more sensible representation of violations in other contexts, implement {@link Violation.Visitor}.
 */
public class ViolationFormatter implements Function<List<Violation>, String>, Violation.Visitor<String> {
   @Override
   public String apply( final List<Violation> violations ) {
      final List<Violation> nonSemanticViolations = violations.stream().filter( violation ->
            violation.errorCode().equals( InvalidSyntaxViolation.ERROR_CODE ) || violation.errorCode().equals( ProcessingViolation.ERROR_CODE ) ).toList();
      if ( !nonSemanticViolations.isEmpty() ) {
         return processNonSemanticViolation( nonSemanticViolations );
      }

      return processSemanticViolations( violations );
   }

   protected String processNonSemanticViolation( final List<Violation> violations ) {
      return violations.stream().map( violation -> violation.accept( this ) ).collect( Collectors.joining( "\n\n" ) );
   }

   protected Map<String, List<Violation>> violationsByElement( final List<Violation> violations ) {
      final Map<String, List<Violation>> result = new HashMap<>();
      for ( final Violation violation : violations ) {
         final String elementName = violation.elementName();
         final List<Violation> elementViolations = result.computeIfAbsent( elementName, ( element ) -> new ArrayList<>() );
         elementViolations.add( violation );
      }
      return result;
   }

   protected String processSemanticViolations( final List<Violation> violations ) {
      if ( violations.isEmpty() ) {
         return String.format( "Input model is valid%n" );
      }

      final Map<String, List<Violation>> violationsByElement = violationsByElement( violations );
      final StringBuilder builder = new StringBuilder();
      builder.append( String.format( "Semantic violations were found:%n%n" ) );
      for ( final Map.Entry<String, List<Violation>> entry : violationsByElement.entrySet() ) {
         final String elementName = entry.getKey();
         final List<Violation> elementViolations = entry.getValue();
         builder.append( String.format( "> %s :%n", elementName ) );
         for ( final Violation violation : elementViolations ) {
            builder.append( String.format( "  %s%n", violation.accept( this ) ) );
         }
         builder.append( String.format( "%n" ) );
      }

      return builder.toString();
   }

   /**
    * Default formatting for most violations
    * @param violation the violation
    * @return formatted representation
    */
   @Override
   public String visit( final Violation violation ) {
      return violation.message();
   }

   /**
    * Processing violation, e.g. a model element that could not be resolved
    * @param violation the violation
    * @return formatted representation
    */
   @Override
   public String visitProcessingViolation( final ProcessingViolation violation ) {
      return String.format( "Validation failed:%n%s%n", violation.message() );
   }

   /**
    * Syntax error in the source file
    * @param violation the violation
    * @return formatted representation
    */
   @Override
   public String visitInvalidSyntaxViolation( final InvalidSyntaxViolation violation ) {
      final StringBuilder builder = new StringBuilder();
      builder.append( String.format( "Syntax error in line %d, column %d: %s%n%n", violation.line(), violation.column(), violation.message() ) );
      final String[] lines = violation.source().split( "\n" );
      final int linesBeforeAndAfter = 5;
      final int lowerIndex = violation.line() > linesBeforeAndAfter ? (int) (violation.line() - linesBeforeAndAfter - 1) : 0;
      final int upperIndex = violation.line() + linesBeforeAndAfter + 1 < lines.length ? (int) (violation.line() + linesBeforeAndAfter - 1) : lines.length - 1;
      for ( int i = lowerIndex; i <= upperIndex; i++ ) {
         builder.append( String.format( "%2s%3d: %s%n", (i + 1 == violation.line() ? "->" : ""), i + 1, lines[i] ) );
      }
      builder.append( "\n" );
      return builder.toString();
   }
}
