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

package org.eclipse.esmf.turtle.languageserver.lsp.text;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationError;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationErrorType;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationResult;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectViolationInfo;
import org.eclipse.esmf.turtle.languageserver.aspect.service.AspectValidationCoordinator;
import org.eclipse.esmf.turtle.languageserver.common.uri.DocumentUriResolver;

public class DocumentAspectValidationService {
   private static final Logger LOG = LoggerFactory.getLogger( DocumentAspectValidationService.class );
   private final AspectValidationCoordinator aspectValidationCoordinator;

   public DocumentAspectValidationService( final AspectValidationCoordinator aspectValidationCoordinator ) {
      this.aspectValidationCoordinator = aspectValidationCoordinator;
   }

   public AspectValidationResult validateDocument( final String uri, final String content ) {
      if ( content == null ) {
         return failedValidation( AspectValidationErrorType.LOAD, "Document is not available in memory: " + uri );
      }

      final Path path = DocumentUriResolver.toPath( uri );
      if ( path == null ) {
         return failedValidation( AspectValidationErrorType.LOAD, "Aspect validation supports only file URIs: " + uri );
      }

      return validateOpenDocument( uri, path, content );
   }

   private AspectValidationResult validateOpenDocument( final String uri, final Path originalPath, final String content ) {
      final Path parent = originalPath.getParent();
      if ( parent == null ) {
         return failedValidation( AspectValidationErrorType.LOAD, "Document path has no parent directory: " + originalPath );
      }

      final String originalFileName = originalPath.getFileName() != null ? originalPath.getFileName().toString() : "aspect";
      String tempPrefix = originalFileName.replaceAll( "[^A-Za-z0-9._-]", "_" ) + "-";
      if ( tempPrefix.length() < 3 ) {
         tempPrefix = "ttl-";
      }

      Path tempFile = null;
      try {
         tempFile = Files.createTempFile( parent, tempPrefix, ".ttl" );
         Files.writeString( tempFile, content, StandardOpenOption.TRUNCATE_EXISTING );
         final AspectValidationResult result = aspectValidationCoordinator.validateSync( tempFile );
         return remapValidationResult( result, tempFile, originalPath, uri );
      } catch ( final IOException exception ) {
         LOG.error( "[validateDocument] failed to prepare in-memory validation for {}", uri, exception );
         return failedValidation( AspectValidationErrorType.PROCESSING, exception.getMessage() );
      } finally {
         if ( tempFile != null ) {
            try {
               Files.deleteIfExists( tempFile );
            } catch ( final IOException exception ) {
               LOG.warn( "[validateDocument] failed to delete temp file {}", tempFile, exception );
            }
         }
      }
   }

   private AspectValidationResult remapValidationResult( final AspectValidationResult result, final Path tempFile, final Path originalPath,
         final String originalUri ) {
      final URI tempUri = tempFile.toUri();
      final List<AspectViolationInfo> remappedViolations = result.violations().stream()
            .map( violation -> remapViolation( violation, tempUri, originalUri ) )
            .toList();
      final String remappedReport = remapReport( result.report(), tempFile, originalPath, originalUri );
      return new AspectValidationResult( result.valid(), remappedReport, remappedViolations, result.error() );
   }

   private AspectViolationInfo remapViolation( final AspectViolationInfo violation, final URI tempUri, final String originalUri ) {
      if ( !Objects.equals( violation.sourceLocation(), tempUri ) ) {
         return violation;
      }

      return new AspectViolationInfo(
            violation.code(),
            violation.message(),
            URI.create( originalUri ),
            violation.line(),
            violation.column()
      );
   }

   private String remapReport( final String report, final Path tempFile, final Path originalPath, final String originalUri ) {
      if ( report == null || report.isBlank() ) {
         return report;
      }

      return report
            .replace( tempFile.toUri().toString(), originalUri )
            .replace( tempFile.toAbsolutePath().toString(), originalPath.toAbsolutePath().toString() );
   }

   private AspectValidationResult failedValidation( final AspectValidationErrorType type, final String message ) {
      return new AspectValidationResult( false, message, List.of(), new AspectValidationError( type, message ) );
   }
}
