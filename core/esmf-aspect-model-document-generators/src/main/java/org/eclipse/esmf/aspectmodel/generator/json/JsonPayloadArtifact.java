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

package org.eclipse.esmf.aspectmodel.generator.json;

import org.eclipse.esmf.aspectmodel.generator.JsonArtifact;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonPayloadArtifact extends JsonArtifact<JsonNode> {
   public JsonPayloadArtifact( final String id, final JsonNode content ) {
      super( id, content );
   }
}
