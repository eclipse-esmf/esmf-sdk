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

import org.eclipse.esmf.aspectmodel.generator.Artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenApiSchemaArtifact implements Artifact<String, JsonNode> {
   private static final Logger LOG = LoggerFactory.getLogger( OpenApiSchemaArtifact.class );
   protected final String id;
   final JsonNode content;

   protected OpenApiSchemaArtifact( final String id, final JsonNode content ) {
      this.id = id;
      this.content = content;
   }

   @Override
   public String getId() {
      return id;
   }

   @Override
   public JsonNode getContent() {
      return content;
   }

   public String getContentAsYaml() {
      final JsonNode json = getContent();
      try {
         return new YAMLMapper().enable( YAMLGenerator.Feature.MINIMIZE_QUOTES ).writeValueAsString( json );
      } catch ( final JsonProcessingException e ) {
         LOG.error( "YAML mapper couldn't write the given JSON as string.", e );
         return json.toString();
      }
   }
}
