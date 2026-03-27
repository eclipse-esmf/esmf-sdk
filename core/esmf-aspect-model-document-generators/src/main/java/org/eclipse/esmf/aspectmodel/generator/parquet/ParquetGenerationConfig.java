/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.parquet;

import java.util.Random;

import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.eclipse.esmf.aspectmodel.generator.GenerationConfig;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * A {@link GenerationConfig} for Apache Parquet sample payload generation.
 *
 * @param randomStrategy the Random instance to use for random value generation
 * @param addTypeAttributeForEntityInheritance if set to true, adds "@type" attribute in payloads
 *        for inherited entities
 * @param compressionCodec the compression codec to use for Parquet file generation (default:
 *        SNAPPY)
 */
@RecordBuilder
public record ParquetGenerationConfig(
      Random randomStrategy,
      boolean addTypeAttributeForEntityInheritance,
      CompressionCodecName compressionCodec
) implements GenerationConfig {
   public ParquetGenerationConfig {
      if ( randomStrategy == null ) {
         randomStrategy = new Random();
      }
      if ( compressionCodec == null ) {
         compressionCodec = CompressionCodecName.SNAPPY;
      }
   }
}
