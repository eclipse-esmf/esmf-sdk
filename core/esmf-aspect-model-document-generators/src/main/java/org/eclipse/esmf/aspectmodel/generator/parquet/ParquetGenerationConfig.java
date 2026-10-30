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
 * @param ignoreExampleValue if a random value is generated for each property, even if an example
 *        value is defined in the Aspect Model
 * @param currentTimestamp if set to true, generates current timestamp dynamically instead of the
 *        fixed default timestamp (start of Unix epoch)
 * @param randomTimestamp whether to generate random date/time values between 1970 (inclusive) and
 *        2100 (exclusive) in UTC instead of using the Unix epoch
 * @param count the number of records to generate
 */
@RecordBuilder
public record ParquetGenerationConfig(
      Random randomStrategy,
      boolean addTypeAttributeForEntityInheritance,
      CompressionCodecName compressionCodec,
      boolean ignoreExampleValue,
      boolean currentTimestamp,
      boolean randomTimestamp,
      int count
) implements GenerationConfig {
   public ParquetGenerationConfig {
      if ( randomStrategy == null ) {
         randomStrategy = new Random();
      }
      if ( compressionCodec == null ) {
         compressionCodec = CompressionCodecName.SNAPPY;
      }
      if ( count < 1 ) {
         count = 1;
      }
   }
}
