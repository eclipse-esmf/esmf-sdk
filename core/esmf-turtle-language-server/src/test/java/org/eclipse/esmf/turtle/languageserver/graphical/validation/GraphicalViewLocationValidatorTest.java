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

      final GraphicalViewLocationValidator.Result result = validator.validate( location );

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
      assertWarning( missingRange, GraphicalViewResolveTargetWarning.UNRESOLVABLE );
      assertWarning( new Location( uri, new Range( new Position( -1, 0 ), new Position() ) ),
            GraphicalViewResolveTargetWarning.UNRESOLVABLE );
      assertWarning( new Location( uri, new Range( new Position( 2, 0 ), new Position( 1, 0 ) ) ),
            GraphicalViewResolveTargetWarning.UNRESOLVABLE );
   }

   private void assertWarning( final Location location, final GraphicalViewResolveTargetWarning warning ) {
      final GraphicalViewLocationValidator.Result result = validator.validate( location );
      assertThat( result.location() ).isNull();
      assertThat( result.warning() ).isEqualTo( warning );
   }
}
