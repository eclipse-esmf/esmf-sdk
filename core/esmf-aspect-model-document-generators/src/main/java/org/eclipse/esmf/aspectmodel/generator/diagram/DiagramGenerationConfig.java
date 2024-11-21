/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import java.util.Locale;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.GenerationConfig;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record DiagramGenerationConfig(
      Locale language,
      Format format
) implements GenerationConfig {
   public enum Format {
      SVG, PNG;

      public static String allValues() {
         return String.join( ", ", Stream.of( values() ).map( Format::toString ).map( String::toUpperCase ).toList() );
      }
   }

   public DiagramGenerationConfig {
      if ( format == null ) {
         format = Format.SVG;
      }
      if ( language == null ) {
         language = Locale.ENGLISH;
      }
   }
}
