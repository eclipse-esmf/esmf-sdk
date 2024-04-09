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

import org.eclipse.esmf.aspectmodel.generator.Artifact;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The result of generating an AsyncAPI specification from an Aspect Model. The result can be retrieved in JSON ({@link #getContent()}
 * format as self-contained schemas.
 */
public class AsyncApiSchemaArtifact implements Artifact<String, JsonNode> {
   private static final Logger LOG = LoggerFactory.getLogger( AsyncApiSchemaArtifact.class );
   private final String id;
   private final JsonNode content;

   protected AsyncApiSchemaArtifact( final String id, final JsonNode content ) {
      this.id = id;
      this.content = content;
   }

   @Override
   public String getId() {
      return id;
   }

   /**
    * Returns the AsyncAPI schema as a single JSON object
    *
    * @return the AsyncAPI schema
    */
   @Override
   public JsonNode getContent() {
      return content;
   }
}