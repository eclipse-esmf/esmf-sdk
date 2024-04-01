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

import java.util.Locale;

import org.eclipse.esmf.aspectmodel.generator.GenerationConfig;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * A {@link GenerationConfig} for AsyncAPI schema.
 *
 * @param locale the locale for choosing the preferred language for description and preferred name.
 * @param useSemanticVersion if set to true, the complete semantic version of the Aspect Model will be used as the version of the API.
 * @param applicationId for setup id parameter in the spec.
 * @param channelAddress the channel address for providing channel address.
 * specification
 */
@RecordBuilder
public record AsyncApiSchemaGenerationConfig(
      Locale locale,
      boolean useSemanticVersion,
      String applicationId,
      String channelAddress
) implements GenerationConfig {
   public AsyncApiSchemaGenerationConfig {
      if ( locale == null ) {
         locale = Locale.ENGLISH;
      }
   }
}
