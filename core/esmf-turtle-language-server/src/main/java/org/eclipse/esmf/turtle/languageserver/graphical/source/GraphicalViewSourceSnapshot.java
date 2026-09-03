/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.turtle.languageserver.graphical.source;

import java.net.URI;

import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;

/** Immutable source text captured for one graphical-view request. */
public record GraphicalViewSourceSnapshot(
      URI uri, String content
) {
   public Document document() {
      return new Document( uri, content );
   }
}
