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

public class TurtleWorkspaceService implements WorkspaceService {
   public TurtleWorkspaceService( final TurtleTextDocumentService textDocumentService ) {}

   @Override
   public void didChangeConfiguration( final DidChangeConfigurationParams params ) {}

   @Override
   public void didChangeWatchedFiles( final DidChangeWatchedFilesParams params ) {}
}
