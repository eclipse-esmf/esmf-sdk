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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.esmf.turtle.languageserver.aspect.service.AspectValidationCoordinator;
import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspectDiagnosticsWorkflow {
   private static final Logger LOG = LoggerFactory.getLogger( AspectDiagnosticsWorkflow.class );
   private final AspectValidationCoordinator aspectValidationCoordinator;
   private final TextDocumentClientNotifier clientNotifier;
   private final Map<Document, DiagnosticReport> diagnostics = new ConcurrentHashMap<>();

   public AspectDiagnosticsWorkflow(
         final AspectValidationCoordinator aspectValidationCoordinator,
         final TextDocumentClientNotifier clientNotifier ) {
      this.aspectValidationCoordinator = aspectValidationCoordinator;
      this.clientNotifier = clientNotifier;
   }

   public void onDocumentChanged( final Document document ) {
      aspectValidationCoordinator.cancel( document );
      diagnostics.remove( document );
   }

   public void onDocumentClosed( final Document document ) {
      aspectValidationCoordinator.cancel( document );
      diagnostics.clear();
   }

   public void onDocumentSaved( final Document document ) {
      final long generation = aspectValidationCoordinator.nextGeneration( document );
      aspectValidationCoordinator.submit( document, generation, ( completedGeneration, result ) -> {
         final long currentGeneration = aspectValidationCoordinator.currentGeneration( document );
         if ( completedGeneration != currentGeneration ) {
            LOG.debug( "[publish diagnostics] ignoring stale aspect diagnostics for uri={}, generation={}, current={}", document.getUri(),
                  completedGeneration, currentGeneration );
            return;
         }
         diagnostics.put( document, result );
         clientNotifier.publishDiagnostics( document, result );
      } );
   }
}
