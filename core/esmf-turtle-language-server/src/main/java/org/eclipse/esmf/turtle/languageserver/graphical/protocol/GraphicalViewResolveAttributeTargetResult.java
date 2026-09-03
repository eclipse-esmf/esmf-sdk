/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.turtle.languageserver.graphical.protocol;

import org.eclipse.lsp4j.Location;

/** Fresh attribute statement location, or one controlled warning. */
public record GraphicalViewResolveAttributeTargetResult(
      Location location,
      GraphicalViewResolveTargetWarning warning
) {
   public static GraphicalViewResolveAttributeTargetResult warning( final GraphicalViewResolveTargetWarning warning ) {
      return new GraphicalViewResolveAttributeTargetResult( null, warning );
   }
}
