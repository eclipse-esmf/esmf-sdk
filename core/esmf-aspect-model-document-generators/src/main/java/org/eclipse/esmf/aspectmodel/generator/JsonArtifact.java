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

package org.eclipse.esmf.aspectmodel.generator;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.jackson.databind.JsonNode;
import tools.jackson.dataformat.yaml.YAMLFactory;
import tools.jackson.dataformat.yaml.YAMLMapper;
import tools.jackson.dataformat.yaml.YAMLWriteFeature;

public class JsonArtifact<T extends JsonNode> implements Artifact<String, T> {
   private static final Logger LOG = LoggerFactory.getLogger( JsonArtifact.class );

   private final String id;
   private final T content;

   public JsonArtifact( final String id, final T content ) {
      this.id = id;
      this.content = content;
   }

   @Override
   public String getId() {
      return id;
   }

   @Override
   public T getContent() {
      return content;
   }

   public String getContentAsYaml() {
      return jsonToYaml( getContent() );
   }

   @Override
   public byte[] serialize() {
      return getContent().toPrettyString().getBytes( StandardCharsets.UTF_8 );
   }

   protected String jsonToYaml( final JsonNode json ) {
      try {
         final YAMLFactory yamlFactory = YAMLFactory.builder()
               .stringQuotingChecker( new AbstractSchemaArtifact.OpenApiStringQuotingChecker() )
               .enable( YAMLWriteFeature.MINIMIZE_QUOTES )
               .build();
         return new YAMLMapper( yamlFactory ).writeValueAsString( json );
      } catch ( final Exception exception ) {
         LOG.error( "JSON could not be converted to YAML", exception );
         return json.toString();
      }
   }
}
