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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.esmf.aspectmodel.DocumentLocationViolation;
import org.eclipse.esmf.aspectmodel.DocumentViolation;
import org.eclipse.esmf.aspectmodel.ElementFocussedViolation;
import org.eclipse.esmf.aspectmodel.Violation;
import org.eclipse.esmf.aspectmodel.ViolationReport;
import org.eclipse.esmf.aspectmodel.resolver.ModelResolutionViolation;
import org.eclipse.esmf.aspectmodel.resolver.parser.PlainTextFormatter;
import org.eclipse.esmf.aspectmodel.resolver.parser.RdfTextFormatter;
import org.eclipse.esmf.aspectmodel.shacl.RustLikeFormatter;
import org.eclipse.esmf.aspectmodel.shacl.violation.ShaclViolation;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.validation.InvalidLexicalValueViolation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;

public class ViolationFormatter implements Function<ViolationReport, String> {
   protected final RdfTextFormatter textFormatter;
   protected final RustLikeFormatter formatter;
   protected final LinkedHashMap<Class<? extends Violation>, Function<Violation, String>> handlers = new LinkedHashMap<>();

   public ViolationFormatter( final RdfTextFormatter textFormatter, final String additionalHints ) {
      this.textFormatter = textFormatter;
      formatter = new RustLikeFormatter( textFormatter );

      // Order matters: Handlers for the classes or their interfaces are tried top to bottom.
      // So more specific at the top, more generic at the bottom.
      // Not all violation types need to be included here, if they can appropriately be rendered using a
      // given interface, such as DocumentLocationViolation.
      handlers.put( ProcessingViolation.class, v -> handleProcessingViolation( (ProcessingViolation) v ) );
      handlers.put( ShaclViolation.class, v -> handleShaclViolation( (ShaclViolation) v ) );
      handlers.put( ElementFocussedViolation.class, v -> handleElementFocussedViolation( (ElementFocussedViolation) v ) );
      // No need to handle DocumentViolation separately, since it only provides the information about the
      // file context
      handlers.put( InvalidLexicalValueViolation.class, v -> handleInvalidLexicalValueViolation( (InvalidLexicalValueViolation) v ) );
      handlers.put( DocumentLocationViolation.class, v -> handleDocumentLocationViolation( (DocumentLocationViolation) v ) );
      handlers.put( Violation.class, this::handleViolation );
   }

   public ViolationFormatter( final RdfTextFormatter textFormatter ) {
      this( textFormatter, "" );
   }

   public ViolationFormatter() {
      this( new PlainTextFormatter() );
   }

   protected String handleShaclViolation( final ShaclViolation violation ) {
      final String code = violation.code().href().isPresent()
            ? textFormatter.formatHyperlink( violation.errorCode(), violation.code().href().get() )
            : violation.errorCode();
      final String enhancedMessage =
            String.format( "[%s] %s : %s", code, textFormatter.formatName( violation.context().elementName() ), violation.message() );
      return formatter.constructDetailedMessage( violation.highlight(), enhancedMessage, violation.context().element().getModel() );
   }

   protected String handleViolation( final Violation violation ) {
      final String code = violation.code().href().isPresent()
            ? textFormatter.formatHyperlink( violation.code().code(), violation.code().href().get() )
            : violation.code().code();
      return String.format( "[%s] %s", code, violation.message() );
   }

   protected String handleDocumentLocationViolation( final DocumentLocationViolation violation ) {
      final Map<Integer, String> sourceContext = sourceContext( violation.documentContent().get(),
            violation.location().fromLine() + 1 );
      final String code = violation.code().href().isPresent()
            ? textFormatter.formatHyperlink( violation.code().code(), violation.code().href().get() )
            : violation.code().code();
      return "[%s] %s".formatted( code,
            formatter.formatError( 1, sourceContext, violation.location().fromLine() + 1,
                  violation.location().fromColumn() + 1, violation.message(), violation.sourceDocument() ) );
   }

   protected String handleInvalidLexicalValueViolation( final InvalidLexicalValueViolation violation ) {
      final Map<Integer, String> sourceContext = sourceContext( violation.documentContent().get(),
            violation.location().fromLine() + 1 );
      final String code = violation.code().href().isPresent()
            ? textFormatter.formatHyperlink( violation.code().code(), violation.code().href().get() )
            : violation.code().code();
      return "[%s] %s".formatted( code,
            formatter.formatError( violation.value().toString().length(), sourceContext, violation.location().fromLine() + 1,
                  violation.location().fromColumn() + 1, violation.message(), violation.sourceDocument() ) );
   }

   protected String handleElementFocussedViolation( final ElementFocussedViolation violation ) {
      final String code = violation.code().href().isPresent()
            ? textFormatter.formatHyperlink( violation.code().code(), violation.code().href().get() )
            : violation.code().code();
      final String enhancedMessage = String.format( "[%s] %s", code, violation.message() );
      return formatter.constructDetailedMessage( violation.highlight(), enhancedMessage, violation.highlight().getModel() );
   }

   protected String handleProcessingViolation( final ProcessingViolation processingViolation ) {
      return processingViolation.message();
   }

   protected String handleModelResolutionViolations( final List<ModelResolutionViolation> violations ) {
      if ( violations.isEmpty() ) {
         return "";
      }
      if ( violations.size() == 1 && violations.getFirst().element().isEmpty() ) {
         final ModelResolutionViolation violation = violations.getFirst();
         return "In %s: %s.".formatted( textFormatter.formatIri( violation.location().toString() ), violation.message() );
      }
      final StringBuilder result = new StringBuilder();
      final Map<String, List<ModelResolutionViolation>> violationsByElement = violations.stream()
            .collect( Collectors.groupingBy( violation -> violation.element().map( AspectModelUrn::toString ).orElse( "" ) ) );
      violationsByElement.entrySet().stream().filter( entry -> !entry.getKey().isEmpty() ).findFirst().ifPresent( entry -> {
         result.append(
               "Could not resolve %s. Tried to look at the following locations:%n".formatted( textFormatter.formatIri( entry.getKey() ) ) );
         for ( final ModelResolutionViolation violation : entry.getValue() ) {
            result.append( "- %s: %s%n".formatted( textFormatter.formatIri( violation.location().toString() ), violation.message() ) );
         }
         if ( violationsByElement.size() > 1 ) {
            result.append( "%s additional elements could not be resolved.".formatted(
                  textFormatter.formatIri( "" + ( violationsByElement.size() - 1 ) ) ) );
         }
      } );
      return result.toString();
   }

   private Function<Violation, String> handlerForViolation( final Violation violation ) {
      return handlers.entrySet().stream()
            .filter( entry -> entry.getKey().isAssignableFrom( violation.getClass() ) )
            .map( Map.Entry::getValue )
            .findFirst()
            .orElse( this::handleViolation );
   }

   protected String formatUri( final URI sourceDocument ) {
      return sourceDocument.getScheme().equals( "file" )
            ? new File( sourceDocument ).getAbsolutePath()
            : sourceDocument.toString();
   }

   protected String indent( final String string, final int indentation ) {
      return string.lines()
            .map( line -> " ".repeat( indentation ) + line )
            .collect( Collectors.joining( "\n", "", "\n" ) );
   }

   protected String noViolationsFound() {
      return String.format( "Input model is valid%n" );
   }

   protected String violationsFound() {
      return String.format( "Validation errors were found:%n%n" );
   }

   protected String startOfFileSection( final String fileName ) {
      return String.format( "> In %s:%n", textFormatter.formatName( fileName ) );
   }

   /**
    * Convenience wrapper for {@link #apply(ViolationReport)}
    *
    * @param violation the single violation
    * @return the violation formatted as a string
    */
   public String apply( final Violation violation ) {
      return apply( new ViolationReport( List.of( violation ) ) );
   }

   @Override
   public String apply( final ViolationReport violations ) {
      if ( violations.isEmpty() ) {
         return noViolationsFound();
      }
      final StringBuilder builder = new StringBuilder();
      builder.append( violationsFound() );

      // Different categories of violations to handle:
      // ModelResolutionViolations are handled together, since failure to resolve one
      // element often times also means many other elements can't be resolved. Don't
      // overwhelm the user with all findings at once.
      final List<ModelResolutionViolation> modelResolutionViolations = new ArrayList<>();
      // Violations specific to a certain file
      final Map<URI, List<DocumentViolation>> documentViolations = new HashMap<>();
      // Other, non-file specific violations
      final List<Violation> otherViolations = new ArrayList<>();

      for ( final Violation violation : violations.violations() ) {
         if ( violation instanceof final ModelResolutionViolation modelResolutionViolation ) {
            modelResolutionViolations.add( modelResolutionViolation );
         } else if ( violation instanceof final DocumentViolation documentViolation ) {
            documentViolations.computeIfAbsent( documentViolation.sourceDocument(),
                  _ -> new ArrayList<>() ).add( documentViolation );
         } else {
            otherViolations.add( violation );
         }
      }
      modelResolutionViolations.sort( Comparator.comparing( ModelResolutionViolation::location )
            .thenComparing( violation -> violation.element().map( AspectModelUrn::toString ).orElse( "" ) ) );
      otherViolations.sort( Comparator.comparing( violation -> violation.getClass().getName() ) );
      documentViolations.values().forEach( list -> list.sort( Comparator.comparing( DocumentViolation::sourceDocument ) ) );

      builder.append( handleModelResolutionViolations( modelResolutionViolations ) );

      // file-related violations
      for ( final Map.Entry<URI, List<DocumentViolation>> entry : documentViolations.entrySet() ) {
         final String fileName = formatUri( entry.getKey() );
         builder.append( startOfFileSection( fileName ) );
         for ( final Violation violation : entry.getValue() ) {
            final Function<Violation, String> processor = handlerForViolation( violation );
            final String result = indent( processor.apply( violation ), 2 );
            builder.append( result );
            builder.append( "\n" );
         }
      }

      // non-file related violations
      for ( final Violation violation : otherViolations ) {
         final Function<Violation, String> processor = handlerForViolation( violation );
         final String result = processor.apply( violation );
         builder.append( result );
         builder.append( "\n" );
      }

      return builder.toString();
   }

   /**
    * Returns the list of lines of the source document surrounding a given line, indexed by original
    * line number (0-based)
    *
    * @param sourceDocument the source document
    * @param line the line to focus on
    * @return the lines in the context of the focus line
    */
   protected Map<Integer, String> sourceContext( final String sourceDocument, final long line ) {
      final List<String> listOfLines = sourceDocument.lines().toList();
      return IntStream.range( 0, listOfLines.size() )
            .filter( i -> Math.abs( line - i ) <= 3 )
            .mapToObj( i -> Map.entry( i + 1, listOfLines.get( i ) ) )
            .collect( asMap() );
   }
}
