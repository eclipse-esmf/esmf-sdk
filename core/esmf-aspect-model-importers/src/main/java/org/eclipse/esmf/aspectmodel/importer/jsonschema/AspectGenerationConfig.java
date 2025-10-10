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

import java.util.function.Function;

import org.eclipse.esmf.aspectmodel.generator.GenerationConfig;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import com.fasterxml.jackson.databind.JsonNode;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * @param aspectModelUrn the URN of the element (Aspect or Entity) to generate
 * @param addTodo determines whether descritptions with "TO DO" comments should be added to new elements
 * @param customRefResolver function to override resolution of "$ref" attributes
 */
@RecordBuilder
public record AspectGenerationConfig(
      AspectModelUrn aspectModelUrn,
      boolean addTodo,
      Function<String, JsonNode> customRefResolver
) implements GenerationConfig {
   public AspectGenerationConfig {
   }
}
