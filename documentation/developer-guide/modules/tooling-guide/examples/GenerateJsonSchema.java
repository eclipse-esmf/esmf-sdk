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

package examples;

// tag::imports[]
import org.eclipse.esmf.aspectmodel.generator.jsonschema.AspectModelJsonSchemaGenerator;
import org.eclipse.esmf.aspectmodel.generator.jsonschema.JsonSchemaGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.jsonschema.JsonSchemaGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.metamodel.AspectModel;

import java.util.Locale;
import com.fasterxml.jackson.databind.JsonNode;
// end::imports[]
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class GenerateJsonSchema {
   @Test
   public void generate() throws IOException {
      // tag::generate[]
      // AspectModel as returned by the AspectModelLoader
      final AspectModel aspectModel = // ...
            // end::generate[]
            new AspectModelLoader().load(
                  new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" ) );
      // tag::generate[]

      final JsonSchemaGenerationConfig config = JsonSchemaGenerationConfigBuilder.builder()
            .locale( Locale.ENGLISH )
            .build();
      final AspectModelJsonSchemaGenerator generator = new AspectModelJsonSchemaGenerator( aspectModel.aspect(), config );
      // Get result as type-safe JSON object
      final JsonNode jsonNode = generator.getContent();
      // Or as pretty-printed JSON String:
      final String json = generator.generateJson();
      // end::generate[]
   }
}
