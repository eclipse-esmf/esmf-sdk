/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.shacl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * Represents a <a href="https://www.w3.org/TR/shacl-js/#JSLibrary">SHACL JavaScript library</a>
 */
public class JsLibrary {
   private final Optional<String> uri;
   private final String javaScriptCode;

   public JsLibrary( final Optional<String> uri, final List<String> libraryUrls, final List<JsLibrary> includedLibraries ) {
      this.uri = uri;

      final StringBuilder code = new StringBuilder();
      for ( final String libraryUrl : libraryUrls ) {
         try ( final InputStream inputStream = URI.create( libraryUrl ).toURL().openStream() ) {
            code.append( new String( inputStream.readAllBytes(), StandardCharsets.UTF_8 ) );
            code.append( "\n" );
         } catch ( final IOException exception ) {
            throw new ShaclValidationException( "Could not resolve JsLibrary " + uri, exception );
         }
      }

      for ( final JsLibrary includedLibrary : includedLibraries ) {
         code.append( includedLibrary.javaScriptCode );
      }

      javaScriptCode = code.toString();
   }

   public Optional<String> uri() {
      return uri;
   }

   public String javaScriptCode() {
      return javaScriptCode;
   }
}
