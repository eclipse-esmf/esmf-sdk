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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

   protected String jsonToYaml( final JsonNode json ) {
      try {
         final YAMLFactory yamlFactory = YAMLFactory.builder()
               .stringQuotingChecker( new AbstractSchemaArtifact.OpenApiStringQuotingChecker() ).build();
         return new YAMLMapper( yamlFactory ).enable( YAMLGenerator.Feature.MINIMIZE_QUOTES )
               .writeValueAsString( json );
      } catch ( final JsonProcessingException exception ) {
         LOG.error( "JSON could not be converted to YAML", exception );
         return json.toString();
      }
   }
}
