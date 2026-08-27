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
package org.eclipse.esmf.turtle.languageserver.lsp.request;
import java.util.List;
/** Render response. Warning outcomes contain no SVG and no targets. */
public record GraphicalViewRenderResult( String uri, String svg, List<GraphicalViewRenderTarget> targets,
      List<GraphicalViewRenderWarning> warnings ) {
   public GraphicalViewRenderResult { targets = List.copyOf( targets ); warnings = List.copyOf( warnings ); }
   public static GraphicalViewRenderResult warning( final String uri, final GraphicalViewRenderWarning warning ) {
      return new GraphicalViewRenderResult( uri, null, List.of(), List.of( warning ) );
   }
}
