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

package org.eclipse.esmf.aspectmodel.validation.services;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.shacl.fix.Fix;
import org.eclipse.esmf.aspectmodel.shacl.violation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

/**
 * Formats one or multiple {@link Violation}s in a human-readable way. Note that this is intended only for places with raw textual output,
 * such as a text console. For a more sensible representation of violations in other contexts, implement {@link Violation.Visitor}.
 */
public class ViolationFormatter implements Function<List<Violation>, String>, Violation.Visitor<String> {
   @Override
   public String apply( final List<Violation> violations ) {
      final List<Violation> nonSemanticViolations = violations.stream().filter( violation ->
            violation.errorCode().equals( InvalidSyntaxViolation.ERROR_CODE ) || violation.errorCode()
                  .equals( ProcessingViolation.ERROR_CODE ) ).toList();
      if ( !nonSemanticViolations.isEmpty() ) {
         return processNonSemanticViolation( nonSemanticViolations );
      }

      return processSemanticViolations( violations );
   }

   protected String processNonSemanticViolation( final List<Violation> violations ) {
      return violations.stream().map( violation -> violation.accept( this ) ).collect( Collectors.joining( "\n\n" ) );
   }

   protected String processSemanticViolations( final List<Violation> violations ) {
      if ( violations.isEmpty() ) {
         return String.format( "Input model is valid%n" );
      }

      final Map<String, List<Violation>> violationsByElement = violations.stream().collect( Collectors.groupingBy( violation ->
            violation.context().elementName() ) );
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
    *
    * @param violation the violation
    * @return formatted representation
    */
   @Override
   public String visit( final Violation violation ) {
      final StringBuilder builder = new StringBuilder();
      builder.append( violation.message() );
      for ( final Fix possibleFix : violation.fixes() ) {
         builder.append( String.format( "%n  > Possible fix: %s", possibleFix.description() ) );
      }
      return builder.toString();
   }

   /**
    * Processing violation, e.g. a model element that could not be resolved
    *
    * @param violation the violation
    * @return formatted representation
    */
   @Override
   public String visitProcessingViolation( final ProcessingViolation violation ) {
      if ( violation.cause() != null
            && violation.cause() instanceof final ModelResolutionException modelResolutionException
            && !modelResolutionException.getCheckedLocations().isEmpty() ) {
         return modelResolutionException.getCheckedLocations()
               .stream()
               .collect( Collectors.groupingBy( ModelResolutionException.LoadingFailure::element ) )
               .entrySet()
               .stream()
               .map( entry ->
                     entry.getValue().stream()
                           .map( failure -> "  - %s (%s)".formatted( failure.location(), failure.description() ) )
                           .collect( Collectors.joining( "\n", "- " + entry.getKey() + "\n", "" ) ) )
               .collect( Collectors.joining( "\n", "There were references in the model that could not be resolved. Checked locations:\n",
                     "" ) );
      }
      return String.format( "Validation failed:%n%s%n", violation.message() );
   }

   /**
    * Syntax error in the source file
    *
    * @param violation the violation
    * @return formatted representation
    */
   @Override
   public String visitInvalidSyntaxViolation( final InvalidSyntaxViolation violation ) {
      final StringBuilder builder = new StringBuilder();
      builder.append(
            String.format( "Syntax error in line %d, column %d: %s%n%n", violation.line(), violation.column(), violation.message() ) );
      printSyntaxViolationSource( violation, builder, "" );
      return builder.toString();
   }

   protected void printSyntaxViolationSource( final InvalidSyntaxViolation violation, final StringBuilder builder,
         final String additionalIndentation ) {
      final String[] lines = violation.source().split( "\n" );
      final int linesBeforeAndAfter = 5;
      final int lowerIndex = violation.line() > linesBeforeAndAfter ? (int) ( violation.line() - linesBeforeAndAfter - 1 ) : 0;
      final int upperIndex = violation.line() + linesBeforeAndAfter + 1 < lines.length
            ? (int) ( violation.line() + linesBeforeAndAfter - 1 )
            : lines.length - 1;
      for ( int i = lowerIndex; i <= upperIndex; i++ ) {
         builder.append(
               String.format( "%s%2s%3d: %s%n", additionalIndentation, ( i + 1 == violation.line() ? "->" : "" ), i + 1, lines[i] ) );
      }
      builder.append( "\n" );
   }
}
