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

package org.eclipse.esmf.aspectmodel.generator.asyncapi;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.generator.AbstractSchemaArtifact;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The result of generating an AsyncAPI specification from an Aspect Model. The result can be retrieved in JSON ({@link #getContent()}
 * format as self-contained schemas.
 */
public class AsyncApiSchemaArtifact extends AbstractSchemaArtifact<JsonNode> {
   protected AsyncApiSchemaArtifact( final String id, final JsonNode content ) {
      super( id, content );
   }

   /**
    * Returns the AsyncAPI schema a single YAML string
    *
    * @return the AsyncAPI schema
    */
   @Override
   public String getContentAsYaml() {
      return jsonToYaml( getContent() );
   }

   @Override
   public Map<Path, JsonNode> getContentWithSeparateSchemasAsJson() {
      return getContentWithSeparateSchemasAsJson( Optional.of( "aai" ) );
   }

   @Override
   public Map<Path, String> getContentWithSeparateSchemasAsYaml() {
      return getContentWithSeparateSchemasAsYaml( Optional.of( "aai" ) );
   }
}
