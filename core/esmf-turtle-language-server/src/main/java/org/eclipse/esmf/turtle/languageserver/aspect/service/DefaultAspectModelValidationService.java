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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.InvalidLexicalValueViolation;
import org.eclipse.esmf.aspectmodel.validation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.DetailedViolationFormatter;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationError;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationErrorType;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationResult;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectViolationInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAspectModelValidationService implements AspectModelValidationService {
   private static final Logger LOAD_LOG = LoggerFactory.getLogger( "org.eclipse.esmf.turtle.languageserver.validation.aspect.load" );
   private static final Logger RESOLVE_LOG = LoggerFactory.getLogger( "org.eclipse.esmf.turtle.languageserver.validation.aspect.resolve" );
   private static final Logger VALIDATE_LOG =
         LoggerFactory.getLogger( "org.eclipse.esmf.turtle.languageserver.validation.aspect.validate" );

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
   public AspectValidationResult validate( final Path path ) {
      if ( path == null ) {
         return failedResult( AspectValidationErrorType.LOAD, "Path must not be null" );
      }

      if ( !Files.exists( path ) ) {
         return failedResult( AspectValidationErrorType.LOAD, "Aspect model file does not exist: " + path );
      }

      if ( !Files.isRegularFile( path ) || !Files.isReadable( path ) ) {
         return failedResult( AspectValidationErrorType.LOAD, "Aspect model file is not readable: " + path );
      }

      try {
         LOAD_LOG.debug( "[load] loading aspect model from {}", path );
         final List<Violation> violations = validator.validateModel( () -> loadAspectModel( path ) );
         VALIDATE_LOG.debug( "[validate] validation finished for {} with {} violation(s)", path, violations.size() );
         final String report = new DetailedViolationFormatter().apply( violations );
         final AspectValidationError error = classifyError( violations );
         return new AspectValidationResult( violations.isEmpty(), report, violations.stream().map( this::toViolationInfo ).toList(),
               error );
      } catch ( final Exception exception ) {
         VALIDATE_LOG.error( "[validate] unexpected runtime failure for {}", path, exception );
         return failedResult( AspectValidationErrorType.PROCESSING, exception.getMessage() );
      }
   }

   private AspectModel loadAspectModel( final Path path ) {
      RESOLVE_LOG.debug( "[resolve imports] resolving imports for {}", path );
      return loader.load( path.toFile() );
   }

   private AspectValidationResult failedResult( final AspectValidationErrorType type, final String message ) {
      return new AspectValidationResult( false, message, List.of(), new AspectValidationError( type, message ) );
   }

   private AspectValidationError classifyError( final List<Violation> violations ) {
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
      if ( violation instanceof final InvalidSyntaxViolation syntaxViolation ) {
         return new AspectViolationInfo(
               syntaxViolation.errorCode(),
               syntaxViolation.message(),
               syntaxViolation.sourceLocation().orElse( null ),
               syntaxViolation.line(),
               syntaxViolation.column()
         );
      }
      if ( violation instanceof final InvalidLexicalValueViolation lexicalValueViolation ) {
         return new AspectViolationInfo(
               lexicalValueViolation.errorCode(),
               lexicalValueViolation.message(),
               lexicalValueViolation.sourceLocation().orElse( null ),
               (long) lexicalValueViolation.line(),
               (long) lexicalValueViolation.column()
         );
      }

      return new AspectViolationInfo(
            violation.errorCode(),
            violation.message(),
            violation.sourceLocation().orElse( null ),
            null,
            null
      );
   }
}
