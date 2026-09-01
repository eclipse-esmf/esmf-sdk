/* Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0 */
package org.eclipse.esmf.turtle.languageserver.graphical.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewResolveTargetWarning;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Test;

class GraphicalViewLocationValidatorTest {
   private final GraphicalViewLocationValidator validator = new GraphicalViewLocationValidator();

   @Test
   void acceptsNormalizedLocalLocationForBothResolveResultKinds() {
      final Location location = new Location( "file:///model.ttl",
            new Range( new Position( 1, 2 ), new Position( 1, 5 ) ) );

      final var result = validator.validate( location );

      assertThat( result.location() ).isSameAs( location );
      assertThat( result.warning() ).isNull();
   }

   @Test
   void rejectsNonLocalMissingNegativeAndReversedRanges() {
      assertWarning( new Location( "https://example/model.ttl", new Range( new Position(), new Position() ) ),
            GraphicalViewResolveTargetWarning.UNSUPPORTED_URI );
      final Location missingRange = new Location();
      missingRange.setUri( "file:///model.ttl" );
      assertWarning( missingRange, GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      assertWarning( new Location( "file:///model.ttl", new Range( new Position( -1, 0 ), new Position() ) ),
            GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      assertWarning( new Location( "file:///model.ttl", new Range( new Position( 2, 0 ), new Position( 1, 0 ) ) ),
            GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
   }

   private void assertWarning( final Location location, final GraphicalViewResolveTargetWarning warning ) {
      final var result = validator.validate( location );
      assertThat( result.location() ).isNull();
      assertThat( result.warning() ).isEqualTo( warning );
   }
}
