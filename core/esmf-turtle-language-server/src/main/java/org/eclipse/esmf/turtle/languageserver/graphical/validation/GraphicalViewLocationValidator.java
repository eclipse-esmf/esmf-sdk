/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.turtle.languageserver.graphical.validation;

import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewResolveTargetWarning;
import org.eclipse.esmf.turtle.languageserver.graphical.source.GraphicalViewSourceContext;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;

/** Applies the shared local-URI and range boundary to every resolved location. */
public final class GraphicalViewLocationValidator {
   public Result validate( final Location location ) {
      if ( location == null || location.getUri() == null ) {
         return Result.warning( GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      }
      if ( !GraphicalViewSourceContext.isLocalFileUri( location.getUri() ) ) {
         return Result.warning( GraphicalViewResolveTargetWarning.UNSUPPORTED_URI );
      }
      if ( location.getRange() == null || !validPosition( location.getRange().getStart() )
            || !validPosition( location.getRange().getEnd() ) ) {
         return Result.warning( GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      }
      final Position start = location.getRange().getStart();
      final Position end = location.getRange().getEnd();
      if ( start.getLine() > end.getLine()
            || ( start.getLine() == end.getLine() && start.getCharacter() > end.getCharacter() ) ) {
         return Result.warning( GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      }
      return new Result( location, null );
   }

   private static boolean validPosition( final Position position ) {
      return position != null && position.getLine() >= 0 && position.getCharacter() >= 0;
   }

   public record Result(
         Location location, GraphicalViewResolveTargetWarning warning
   ) {
      static Result warning( final GraphicalViewResolveTargetWarning warning ) {
         return new Result( null, warning );
      }
   }
}
