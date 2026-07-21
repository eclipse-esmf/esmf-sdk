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

package org.eclipse.esmf.turtle.languageserver.lsp.workspace;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.services.WorkspaceService;

import org.eclipse.esmf.turtle.languageserver.lsp.text.TurtleTextDocumentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurtleWorkspaceService implements WorkspaceService {
   private static final Logger LOG = LoggerFactory.getLogger( TurtleWorkspaceService.class );

   private final TurtleTextDocumentService textDocumentService;

   public TurtleWorkspaceService( final TurtleTextDocumentService textDocumentService ) {
      this.textDocumentService = textDocumentService;
   }

   @Override
   public void didChangeConfiguration( final DidChangeConfigurationParams params ) {
      LOG.debug( "[didChangeConfiguration] received configuration change notification" );
      textDocumentService.resolutionStrategyService().applyConfigurationChange( params.getSettings() );
   }

   @Override
   public void didChangeWatchedFiles( final DidChangeWatchedFilesParams params ) {}
}
