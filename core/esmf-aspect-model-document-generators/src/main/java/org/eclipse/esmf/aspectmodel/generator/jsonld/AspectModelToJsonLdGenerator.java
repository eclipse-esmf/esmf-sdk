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

package org.eclipse.esmf.aspectmodel.generator.jsonld;

import java.io.StringWriter;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.JsonGenerator;
import org.eclipse.esmf.metamodel.Aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspectModelToJsonLdGenerator extends JsonGenerator<JsonLdGenerationConfig, JsonNode, JsonLdArtifact> {
   public static final JsonLdGenerationConfig DEFAULT_CONFIG = null;
   private static final Logger LOG = LoggerFactory.getLogger( AspectModelToJsonLdGenerator.class );

   public AspectModelToJsonLdGenerator( final Aspect aspect ) {
      this( aspect, DEFAULT_CONFIG );
   }

   public AspectModelToJsonLdGenerator( final Aspect aspect, final JsonLdGenerationConfig config ) {
      super( aspect, config );
   }

   /**
    * Generates a JSON-LD representation of the aspect's source model and writes it to an output stream.
    */
   @Override
   public Stream<JsonLdArtifact> generate() {
      final StringWriter stringWriter = new StringWriter();
      aspect.getSourceFile().sourceModel().write( stringWriter, RDFLanguages.strLangJSONLD );
      final String content = stringWriter.toString();
      try {
         return Stream.of( new JsonLdArtifact( aspect.getName() + ".json", objectMapper.readTree( content ) ) );
      } catch ( final JsonProcessingException exception ) {
         LOG.error( "Could not parse JSON-LD for {}", aspect.getName() );
      }
      return Stream.empty();
   }
}
