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

package org.eclipse.esmf.aspectmodel.generator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class AbstractGenerator {

   protected void writeCharSequenceToOutputStream( final CharSequence charSequence, final OutputStream outputStream )
         throws IOException {
      try ( final Writer writer = new OutputStreamWriter( outputStream, StandardCharsets.UTF_8 ) ) {
         for ( int i = 0; i < charSequence.length(); i++ ) {
            writer.write( charSequence.charAt( i ) );
         }
         writer.flush();
      }
   }

   public static final String SAMM_EXTENSION = "x-samm-aspect-model-urn";
}
