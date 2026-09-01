/* Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0 */
package org.eclipse.esmf.turtle.languageserver.graphical.protocol;
/** Parameters for {@code turtle/graphicalView/render}. */
public record GraphicalViewRenderParams( String uri, Boolean includeAttributeRows ) {
   public GraphicalViewRenderParams( final String uri ) {
      this( uri, null );
   }

   public boolean attributeRowsRequested() {
      return Boolean.TRUE.equals( includeAttributeRows );
   }
}
