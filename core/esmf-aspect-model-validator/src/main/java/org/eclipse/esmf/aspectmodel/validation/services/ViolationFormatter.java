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

import static org.eclipse.esmf.aspectmodel.StreamUtil.asMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.parser.PlainTextFormatter;
import org.eclipse.esmf.aspectmodel.resolver.parser.RdfTextFormatter;
import org.eclipse.esmf.aspectmodel.shacl.RustLikeFormatter;
import org.eclipse.esmf.aspectmodel.shacl.fix.Fix;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.validation.CycleViolation;
import org.eclipse.esmf.aspectmodel.validation.InvalidLexicalValueViolation;
import org.eclipse.esmf.aspectmodel.validation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.validation.RegularExpressionConstraintViolation;

/**
 * Formats one or multiple {@link Violation}s in a human-readable way. Note that this is intended only for places with raw textual output,
 * such as a text console. For a more sensible representation of violations in other contexts, implement {@link Violation.Visitor}.
 */
public class ViolationFormatter implements Function<List<Violation>, String>, Violation.Visitor<String> {
   protected final RdfTextFormatter textFormatter;
   protected final String additionalHints;
   protected final RustLikeFormatter formatter;

   private static final String ERROR_CODES_DOC_LINK = "https://eclipse-esmf.github.io/esmf-developer-guide/tooling-guide/error-codes.html#";
   private static final String ERROR_CODES_DOC_STRING = "For more information, see documentation: " + ERROR_CODES_DOC_LINK;

   public ViolationFormatter( final String additionalHints ) {
      this( new PlainTextFormatter(), additionalHints );
   }

   public ViolationFormatter() {
      this( new PlainTextFormatter(), "" );
   }

   public ViolationFormatter( final RdfTextFormatter textFormatter ) {
      this( textFormatter, "" );
   }

   public ViolationFormatter( final RdfTextFormatter textFormatter, final String additionalHints ) {
      this.textFormatter = textFormatter;
      this.additionalHints = additionalHints;
      formatter = new RustLikeFormatter( textFormatter );
   }

   protected String indent( final String string, final int indentation ) {
      return string.lines().map( line -> " ".repeat( indentation ) + line ).collect( Collectors.joining( "\n", "", "\n" ) );
   }

   @Override
   public String apply( final List<Violation> violations ) {
      if ( violations.isEmpty() ) {
         return String.format( "Input model is valid%n" );
      }

      final Map<String, List<Violation>> violationsByElement = violations.stream()
            .filter( violation -> violation.context() != null )
            .collect( Collectors.groupingBy( violation -> violation.context().elementName() ) );
      final List<Violation> contextFreeViolations = violations.stream()
            .filter( violation -> violation.context() == null )
            .toList();

      final StringBuilder builder = new StringBuilder();
      builder.append( String.format( "Validation errors were found:%n%n" ) );
      for ( final Map.Entry<String, List<Violation>> entry : violationsByElement.entrySet() ) {
         final String elementName = entry.getKey();
         final List<Violation> elementViolations = entry.getValue();
         builder.append( String.format( "> %s :%n", textFormatter.formatName( elementName ) ) );
         for ( final Violation violation : elementViolations ) {
            // Include error code in the violation message
            final String errorCode = violation.errorCode();
            final String enhancedMessage = String.format( "[%s] %s", errorCode, violation.message() );
            builder.append( indent( violation.accept( this ), 2 ) ).append( System.lineSeparator() );
            for ( final Fix possibleFix : violation.fixes() ) {
               builder.append( "  > Possible fix: " )
                     .append( possibleFix.description() );
            }
            // Add documentation link
            builder.append( String.format( ERROR_CODES_DOC_STRING + "#%s%n",
                  errorCode.toLowerCase().replace( "_", "-" ) ) );
            builder.append( System.lineSeparator() );
         }
         builder.append( System.lineSeparator() );
      }

      for ( final Violation violation : contextFreeViolations ) {
         if ( violation instanceof final ProcessingViolation processingViolation ) {
            builder.append( processingViolation.accept( this ) );
         } else if ( violation.violationSpecificMessage() == null || violation.violationSpecificMessage().equals( violation.message() ) ) {
            final String errorCode = violation.errorCode();
            final String enhancedMessage = String.format( "[%s] %s", errorCode, violation.message() );
            builder.append( String.format( "> %s%n", enhancedMessage ) );
            // Add documentation link for context-free violations
            builder.append( ERROR_CODES_DOC_STRING
                  + errorCode.toLowerCase().replace( "_", "-" ) );
         } else {
            final String errorCode = violation.errorCode();
            final String enhancedMessage = String.format( "[%s] %s", errorCode, violation.message() );
            builder.append( String.format( "> %s: %n", enhancedMessage ) );
            builder.append( indent( violation.accept( this ), 2 ) ).append( System.lineSeparator() );
            // Add documentation link
            builder.append( ERROR_CODES_DOC_STRING
                  + errorCode.toLowerCase().replace( "_", "-" ) );
         }
         builder.append( System.lineSeparator() );
      }

      return builder.toString();
   }

   @Override
   public String visit( final Violation violation ) {
      final String errorCode = violation.errorCode();
      final String enhancedMessage = String.format( "[%s] %s", errorCode, violation.message() );
      return formatter.constructDetailedMessage( violation.highlight(), enhancedMessage, violation.context().element().getModel() );
   }

   @Override
   public String visitProcessingViolation( final ProcessingViolation violation ) {
      if ( violation.cause() != null
            && violation.cause() instanceof final ModelResolutionException modelResolutionException
            && !modelResolutionException.getCheckedLocations().isEmpty() ) {

         final Map<AspectModelUrn, List<ModelResolutionException.LoadingFailure>> failuresByElement =
               modelResolutionException.getCheckedLocations()
                     .stream()
                     .filter( failure -> failure.element().isPresent() )
                     .collect( Collectors.groupingBy( failure -> failure.element().get() ) );
         final String elementFailure = failuresByElement.keySet().stream().sorted().findFirst().map( element -> {
            final List<ModelResolutionException.LoadingFailure> loadingFailures = failuresByElement.get( element );
            return loadingFailures.stream()
                  .map( failure -> "- %s (%s)".formatted( failure.location(),
                        textFormatter.formatError( failure.description() ) ) )
                  .collect( Collectors.joining( "\n",
                        "No file containing the definition of " + textFormatter.formatIri( element.toString() )
                              + " could be resolved. Checked locations:\n", "" ) );
         } ).map( message ->
               modelResolutionException.getCheckedLocations().size() > 1
                     ? "%s%n%n%s additional elements could not be resolved. %s%n".formatted(
                     message, textFormatter.formatIri( "" + ( modelResolutionException.getCheckedLocations().size() - 1 ) ),
                     additionalHints )
                     : message ).orElse( "" );

         final String fileLoadingFailure = modelResolutionException.getCheckedLocations()
               .stream()
               .filter( failure -> failure.element().isEmpty() )
               .findFirst()
               .map( failure -> "Aspect Model file %s could not be loaded: %s".formatted(
                     textFormatter.formatName( failure.location() ), failure.description() ) ).orElse( "" );

         return elementFailure.isEmpty()
               ? fileLoadingFailure
               : elementFailure + "\n" + fileLoadingFailure;
      }
      return violation.message();
   }

   /**
    * Returns the list of lines of the source document surrounding a given line, indexed by original line number (0-based)
    *
    * @param sourceDocument the source document
    * @param line the line to focus on
    * @return the lines in in the context of the focus line
    */
   protected Map<Integer, String> sourceContext( final String sourceDocument, final long line ) {
      final List<String> listOfLines = sourceDocument.lines().toList();
      return IntStream.range( 0, listOfLines.size() )
            .filter( i -> Math.abs( line - i ) <= 3 )
            .mapToObj( i -> Map.entry( i + 1, listOfLines.get( i ) ) )
            .collect( asMap() );
   }

   @Override
   public String visitInvalidSyntaxViolation( final InvalidSyntaxViolation violation ) {
      return formatter.formatError( 1, sourceContext( violation.source(), violation.line() ),
            (int) violation.line(), (int) violation.column() - 1,
            violation.violationSpecificMessage(), violation.sourceLocation() );
   }

   @Override
   public String visitInvalidLexicalValueViolation( final InvalidLexicalValueViolation violation ) {
      return formatter.formatError( violation.value().toString(), violation.sourceLine(), violation.line(), violation.column(),
            violation.violationSpecificMessage(), violation.sourceLocation() );
   }

   @Override
   public String visitCycleViolation( final CycleViolation violation ) {
      return formatter.constructDetailedMessage( violation.highlight(), violation.violationSpecificMessage(),
            violation.highlight().getModel() );
   }

   @Override
   public String visitRegularExpressionConstraint( final RegularExpressionConstraintViolation violation ) {
      return formatter.constructDetailedMessage( violation.highlight(), violation.violationSpecificMessage(),
            violation.highlight().getModel() );
   }
}
