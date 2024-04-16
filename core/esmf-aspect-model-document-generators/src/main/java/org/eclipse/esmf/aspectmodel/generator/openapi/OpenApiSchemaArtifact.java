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

package org.eclipse.esmf.aspectmodel.generator.openapi;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.generator.AbstractSchemaArtifact;
import org.eclipse.esmf.aspectmodel.generator.Artifact;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The result of generating an OpenAPI specification from an Aspect Model. The result can be retrieved in JSON ({@link #getContent()}
 * or YAML ({@link #getContentAsYaml()} formats as self-contained schemas, or as {@link #getContentWithSeparateSchemasAsJson()} and
 * {@link #getContentWithSeparateSchemasAsYaml()} as a map with that contains separate schema documents.
 */
public class OpenApiSchemaArtifact extends AbstractSchemaArtifact implements Artifact<String, JsonNode> {
   private final String id;
   private final JsonNode content;

   protected OpenApiSchemaArtifact( final String id, final JsonNode content ) {
      this.id = id;
      this.content = content;
   }

   @Override
   public String getId() {
      return id;
   }

   /**
    * Returns the OpenAPI schema as a single JSON object
    *
    * @return the OpenAPI schema
    */
   @Override
   public JsonNode getContent() {
      return content;
   }

   /**
    * Returns the OpenAPI schema a single YAML string
    *
    * @return the OpenAPI schema
    */
   public String getContentAsYaml() {
      return jsonToYaml( getContent() );
   }

   /**
    * Returns the OpenAPI schema with separate files for schemas. In the resulting map, the key is the path
    * that names a schema and the value is the corresponding JSON structure. The root schema will be called
    * like the originating Aspect with a ".oai.json" suffix.
    *
    * @return the OpenAPI schema definition as separate files
    */
   @Override
   public Map<Path, JsonNode> getContentWithSeparateSchemasAsJson() {
      return getContentWithSeparateSchemasAsJson( Optional.of( "oai" ) );
   }

   /**
    * Returns the OpenAPI schema with separate files for schemas. In the resulting map, the key is the path
    * that names a schema and the value is the corresponding YAML structure. The root schema will be called
    * like the originating Aspect with a ".oai.yaml" suffix.
    *
    * @return the OpenAPI schema definition as separate files
    */
   @Override
   public Map<Path, String> getContentWithSeparateSchemasAsYaml() {
      return getContentWithSeparateSchemasAsYaml( Optional.of( "oai" ) );
   }
}
