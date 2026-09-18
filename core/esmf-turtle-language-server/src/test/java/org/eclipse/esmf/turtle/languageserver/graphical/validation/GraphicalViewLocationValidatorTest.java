/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.turtle.languageserver.graphical.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewResolveTargetWarning;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GraphicalViewLocationValidatorTest {
   private final GraphicalViewLocationValidator validator = new GraphicalViewLocationValidator();

   @Test
   void acceptsNormalizedLocalLocationForBothResolveResultKinds( @TempDir final Path directory ) {
      final Location location = new Location( directory.resolve( "model.ttl" ).toUri().toString(),
            new Range( new Position( 1, 2 ), new Position( 1, 5 ) ) );

      final var result = validator.validate( location );

      assertThat( result.location() ).isSameAs( location );
      assertThat( result.warning() ).isNull();
   }

   @Test
   void rejectsNonLocalMissingNegativeAndReversedRanges( @TempDir final Path directory ) {
      final String uri = directory.resolve( "model.ttl" ).toUri().toString();
      assertWarning( new Location( "https://example/model.ttl", new Range( new Position(), new Position() ) ),
            GraphicalViewResolveTargetWarning.UNSUPPORTED_URI );
      final Location missingRange = new Location();
      missingRange.setUri( uri );
      assertWarning( missingRange, GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      assertWarning( new Location( uri, new Range( new Position( -1, 0 ), new Position() ) ),
            GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      assertWarning( new Location( uri, new Range( new Position( 2, 0 ), new Position( 1, 0 ) ) ),
            GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
   }

   private void assertWarning( final Location location, final GraphicalViewResolveTargetWarning warning ) {
      final var result = validator.validate( location );
      assertThat( result.location() ).isNull();
      assertThat( result.warning() ).isEqualTo( warning );
   }
}
