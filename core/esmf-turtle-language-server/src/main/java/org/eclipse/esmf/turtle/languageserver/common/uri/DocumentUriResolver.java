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

package org.eclipse.esmf.turtle.languageserver.common.uri;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class DocumentUriResolver {
   private DocumentUriResolver() {}

   public static Path toPath( final String uri ) {
      if ( uri == null || !uri.startsWith( "file:" ) ) {
         return null;
      }

      return Paths.get( URI.create( uri ) );
   }
}
