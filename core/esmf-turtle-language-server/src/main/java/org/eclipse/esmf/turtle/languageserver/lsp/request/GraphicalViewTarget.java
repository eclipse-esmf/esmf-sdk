/* Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0 */
package org.eclipse.esmf.turtle.languageserver.lsp.request;
/** A response-local SVG marker and its semantic target. */
public record GraphicalViewTarget( String id, String kind, String elementUrn ) {
   public static final String ELEMENT_HEADER = "elementHeader";
}
