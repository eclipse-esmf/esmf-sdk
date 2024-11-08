/*
 * Copyright (c) 2024 Bosch Software Innovations GmbH. All rights reserved.
 */

package org.eclipse.esmf.aspectmodel.generator.zip;

import org.eclipse.esmf.aspectmodel.generator.GenerationConfig;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record NamespacePackageGenerationConfig(
      String rootPath
) implements GenerationConfig {
   public NamespacePackageGenerationConfig {
      if ( rootPath == null ) {
         rootPath = "";
      }
   }
}
