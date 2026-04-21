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

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.esmf.turtle.languageserver.aspect.service.AspectValidationCoordinator;
import org.eclipse.esmf.turtle.languageserver.common.uri.DocumentUriResolver;

public class AspectDiagnosticsWorkflow {
   private static final Logger LOG = LoggerFactory.getLogger( AspectDiagnosticsWorkflow.class );
   private final AspectValidationCoordinator aspectValidationCoordinator;
   private final DocumentDiagnosticsService diagnosticsService;
   private final TextDocumentClientNotifier clientNotifier;

   public AspectDiagnosticsWorkflow(
         final AspectValidationCoordinator aspectValidationCoordinator,
         final DocumentDiagnosticsService diagnosticsService,
         final TextDocumentClientNotifier clientNotifier ) {
      this.aspectValidationCoordinator = aspectValidationCoordinator;
      this.diagnosticsService = diagnosticsService;
      this.clientNotifier = clientNotifier;
   }

   public void onDocumentChanged( final String uri ) {
      aspectValidationCoordinator.cancel( uri );
      diagnosticsService.clearAspect( uri );
   }

   public void onDocumentClosed( final String uri ) {
      aspectValidationCoordinator.cancel( uri );
      diagnosticsService.clearAll( uri );
   }

   public void onDocumentSaved( final String uri ) {
      final Path path = DocumentUriResolver.toPath( uri );
      if ( path == null ) {
         LOG.info( "[scheduleAspectValidation] unsupported non-file uri={}, skipping aspect validation", uri );
         diagnosticsService.clearAspect( uri );
         clientNotifier.publishCombinedDiagnostics( uri );
         return;
      }

      final long generation = aspectValidationCoordinator.nextGeneration( uri );
      aspectValidationCoordinator.submit( uri, path, generation, ( completedGeneration, result ) -> {
         final long currentGeneration = aspectValidationCoordinator.currentGeneration( uri );
         if ( completedGeneration != currentGeneration ) {
            LOG.debug( "[publish diagnostics] ignoring stale aspect diagnostics for uri={}, generation={}, current={}", uri,
                  completedGeneration, currentGeneration );
            return;
         }

         diagnosticsService.updateAspect( uri, result );
         clientNotifier.publishCombinedDiagnostics( uri );
      } );
   }
}
