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

package org.eclipse.esmf.aspectmodel.generator.sql;

import org.eclipse.esmf.aspectmodel.generator.GenerationConfig;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Configuration for generating SQL scripts from an Aspect Model.
 *
 * @param dialect the SQL dialect to generate for
 * @param mappingStrategy the mapping strategy to use
 * @param dialectSpecificConfig the dialect-specific configuration
 */
@RecordBuilder
public record SqlGenerationConfig(
      Dialect dialect,
      MappingStrategy mappingStrategy,
      DialectSpecificConfig dialectSpecificConfig
) implements GenerationConfig {
   public enum Dialect {
      DATABRICKS
   }

   public enum MappingStrategy {
      DENORMALIZED
   }

   public interface DialectSpecificConfig extends GenerationConfig {
   }

   public static class DefaultDialectSpecificConfig implements DialectSpecificConfig {
   }

   public SqlGenerationConfig {
      if ( dialect == null ) {
         dialect = Dialect.DATABRICKS;
      }

      if ( mappingStrategy == null ) {
         mappingStrategy = MappingStrategy.DENORMALIZED;
      }

      if ( dialectSpecificConfig == null ) {
         dialectSpecificConfig = new DefaultDialectSpecificConfig();
      }
   }
}
