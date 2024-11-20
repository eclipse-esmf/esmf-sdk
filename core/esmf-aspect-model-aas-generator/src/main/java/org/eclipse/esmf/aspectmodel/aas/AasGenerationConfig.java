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

package org.eclipse.esmf.aspectmodel.aas;

import java.util.List;

import org.eclipse.esmf.aspectmodel.generator.GenerationConfig;

import com.fasterxml.jackson.databind.JsonNode;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record AasGenerationConfig(
      AasFileFormat format,
      JsonNode aspectData,
      List<PropertyMapper<?>> propertyMappers
) implements GenerationConfig {
   public AasGenerationConfig {
      if ( format == null ) {
         format = AasFileFormat.XML;
      }
      if ( propertyMappers == null ) {
         propertyMappers = List.of();
      }
   }
}
