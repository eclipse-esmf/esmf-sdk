/* Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0 */
package org.eclipse.esmf.turtle.languageserver.lsp.request;

/** Marker interface for the graphical-view render target union. */
public sealed interface GraphicalViewRenderTarget permits GraphicalViewTarget, GraphicalViewAttributeTarget {
   String id();
   String kind();
}
