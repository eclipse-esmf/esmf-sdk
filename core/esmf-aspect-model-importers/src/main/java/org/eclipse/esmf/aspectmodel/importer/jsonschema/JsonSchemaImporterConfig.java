/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.importer.jsonschema;

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.esmf.aspectmodel.generator.GenerationConfig;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import com.fasterxml.jackson.databind.JsonNode;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Configuration for the JSON Schema importer
 *
 * @param aspectModelUrn the URN of the element (Aspect or Entity) to generate
 * @param addTodo determines whether descriptions with "TO DO" comments should be added to new elements that don't have descriptions
 * attached in the original schema
 * @param customRefResolver function to override resolution of "$ref" attributes. If the schema to parse contains "$ref"s that are
 * not local, i.e., don't start with '#', then the customRefResolver must be provided to resolve the corresponding JsonNodes, e.g.,
 * when the $ref points to a path in a different file (which could be a local file, a remote resource, etc.), it must load that file and
 * return the corresponding JsonNode.
 */
@RecordBuilder
public record JsonSchemaImporterConfig(
      AspectModelUrn aspectModelUrn,
      boolean addTodo,
      Function<String, Optional<JsonNode>> customRefResolver
) implements GenerationConfig {
   public JsonSchemaImporterConfig( final AspectModelUrn aspectModelUrn ) {
      this( aspectModelUrn, false, null );
   }
}
