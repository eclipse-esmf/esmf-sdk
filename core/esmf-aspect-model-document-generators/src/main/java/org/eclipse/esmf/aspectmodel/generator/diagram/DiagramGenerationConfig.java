/*
 * Copyright (c) 2024 Bosch Software Innovations GmbH. All rights reserved.
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
         return String.join( ", ", Stream.of( values() ).map( Format::toString ).toList() );
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
