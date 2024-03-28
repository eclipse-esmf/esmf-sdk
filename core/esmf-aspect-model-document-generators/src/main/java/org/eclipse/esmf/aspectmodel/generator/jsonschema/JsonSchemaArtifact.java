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

package org.eclipse.esmf.aspectmodel.generator.jsonschema;

import org.eclipse.esmf.aspectmodel.generator.Artifact;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonSchemaArtifact implements Artifact<String, JsonNode> {
   private final String id;
   private final JsonNode content;

   public JsonSchemaArtifact( final String id, final JsonNode content ) {
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
}
