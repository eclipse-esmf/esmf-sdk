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

import java.util.Random;

import org.eclipse.esmf.aspectmodel.generator.GenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.JsonGenerationConfig;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * A {@link GenerationConfig} for JSON sample payload generation.
 *
 * @param randomStrategy the Random instance to use for random value generation
 * @param addTypeAttributeForEntityInheritance if set to true, adds "@type" attribute in payloads for inherited entities
 */
@RecordBuilder
public record JsonPayloadGenerationConfig(
      Random randomStrategy,
      boolean addTypeAttributeForEntityInheritance
) implements JsonGenerationConfig {
   public JsonPayloadGenerationConfig {
      if ( randomStrategy == null ) {
         randomStrategy = new Random();
      }
   }
}
