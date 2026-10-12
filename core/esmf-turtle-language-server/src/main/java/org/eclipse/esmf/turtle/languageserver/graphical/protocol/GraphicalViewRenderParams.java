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
package org.eclipse.esmf.turtle.languageserver.graphical.protocol;

/** Parameters for {@code turtle/graphicalView/render}. */
public record GraphicalViewRenderParams(
      String uri, Boolean includeAttributeRows
) {
   public GraphicalViewRenderParams( final String uri ) {
      this( uri, null );
   }

   public boolean attributeRowsRequested() {
      return Boolean.TRUE.equals( includeAttributeRows );
   }
}
