/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.turtle.languageserver.aspect.service;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.InvalidLexicalValueViolation;
import org.eclipse.esmf.aspectmodel.validation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.DetailedViolationFormatter;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationError;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationErrorType;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationResult;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectViolationInfo;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAspectModelValidationService implements AspectModelValidationService {
   private static final Logger LOG = LoggerFactory.getLogger( DefaultAspectModelValidationService.class );

   private final AspectModelLoader loader;
   private final AspectModelValidator validator;

   public DefaultAspectModelValidationService() {
      this( new AspectModelLoader(), new AspectModelValidator() );
   }

   DefaultAspectModelValidationService( final AspectModelLoader loader, final AspectModelValidator validator ) {
      this.loader = loader;
      this.validator = validator;
   }

   @Override
   public AspectValidationResult validate( final Document document ) {
      try ( final InputStream inputStream = document.getInputStream() ) {
         LOG.debug( "[load] loading aspect model from {}", document.getUri() );
         final List<Violation> violations =
               validator.validateModel( () -> loader.load( inputStream, URI.create( document.getUri() ) ) );
         LOG.debug( "[validate] validation finished for {} with {} violation(s)", document.getUri(), violations.size() );
         final String report = new DetailedViolationFormatter().apply( violations );
         final AspectValidationError error = classifyError( violations );
         return new AspectValidationResult( violations.isEmpty(), report, violations.stream().map( this::toViolationInfo ).toList(),
               error );
      } catch ( final Exception exception ) {
         LOG.error( "[validate] unexpected runtime failure for {}", document.getUri(), exception );
         return failedResult( AspectValidationErrorType.PROCESSING, exception.getMessage() );
      }
   }

   private AspectValidationResult failedResult( final AspectValidationErrorType type, final String message ) {
      return new AspectValidationResult( false, message, List.of(), new AspectValidationError( type, message ) );
   }

   private @Nullable AspectValidationError classifyError( final List<Violation> violations ) {
      final Optional<Violation> firstFailure = violations.stream()
            .filter( violation -> violation instanceof InvalidSyntaxViolation || violation instanceof InvalidLexicalValueViolation
                  || violation instanceof ProcessingViolation )
            .findFirst();

      if ( firstFailure.isEmpty() ) {
         return null;
      }

      final Violation violation = firstFailure.get();
      if ( violation instanceof final InvalidSyntaxViolation syntaxViolation ) {
         return new AspectValidationError( AspectValidationErrorType.PARSE, syntaxViolation.message() );
      }
      if ( violation instanceof final InvalidLexicalValueViolation lexicalValueViolation ) {
         return new AspectValidationError( AspectValidationErrorType.PARSE, lexicalValueViolation.message() );
      }

      final String message = violation.message();
      final AspectValidationErrorType type = message != null && message.toLowerCase().contains( "resolve" )
            ? AspectValidationErrorType.RESOLVE
            : AspectValidationErrorType.PROCESSING;
      return new AspectValidationError( type, message );
   }

   private AspectViolationInfo toViolationInfo( final Violation violation ) {
      return switch ( violation ) {
         case final InvalidSyntaxViolation syntaxViolation ->
            new AspectViolationInfo(
                  syntaxViolation.errorCode(),
                  syntaxViolation.message(),
                  syntaxViolation.sourceLocation().orElse( null ),
                  syntaxViolation.line(),
                  syntaxViolation.column() );
         case final InvalidLexicalValueViolation lexicalValueViolation ->
            new AspectViolationInfo(
                  lexicalValueViolation.errorCode(),
                  lexicalValueViolation.message(),
                  lexicalValueViolation.sourceLocation().orElse( null ),
                  (long) lexicalValueViolation.line(),
                  (long) lexicalValueViolation.column() );
         default -> new AspectViolationInfo(
               violation.errorCode(),
               violation.message(),
               violation.sourceLocation().orElse( null ),
               null,
               null
         );
      };
   }
}
