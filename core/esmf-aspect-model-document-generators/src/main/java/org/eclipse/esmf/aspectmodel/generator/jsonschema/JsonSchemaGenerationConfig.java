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

import java.util.List;
import java.util.Locale;

import org.eclipse.esmf.aspectmodel.generator.GenerationConfig;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * A {@link GenerationConfig} for JSON schema
 */
@RecordBuilder
public record JsonSchemaGenerationConfig(
      Locale locale,
      boolean generateForOpenApi,
      boolean generateCommentForSeeAttributes,
      boolean useExtendedTypes,
      List<String> reservedSchemaNames
) implements GenerationConfig {
   public JsonSchemaGenerationConfig {
      if ( locale == null ) {
         locale = Locale.ENGLISH;
      }
      if ( reservedSchemaNames == null ) {
         reservedSchemaNames = List.of();
      }
   }
}
