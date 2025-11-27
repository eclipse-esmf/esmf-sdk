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

package org.eclipse.esmf.aspectmodel.generator.ts;

import java.io.File;

import org.eclipse.esmf.aspectmodel.generator.GenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.exception.CodeGenerationException;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.apache.commons.lang3.StringUtils;

/**
 * A {@link GenerationConfig} for Java code.
 *
 * @param packageName the package name that classes should be created in
 * @param importTracker the instance of the tracker that tracks imports during code generation
 * @param executeLibraryMacros determines whether template macros given in templateLibFile should be evaluated
 * @param templateLibFile a file containing velocity macros overriding sections in the default code templates
 * @param namePrefix custom class name prefix
 * @param namePostfix custom class name postfix
 */
@RecordBuilder
public record TsCodeGenerationConfig(
      String packageName,
      ImportTracker importTracker,
      boolean executeLibraryMacros,
      File templateLibFile,
      String namePrefix,
      String namePostfix,
      boolean disablePrettierFormatter,
      String prettierConfigPath,
      String metaModelPackageName
) implements GenerationConfig {

   public TsCodeGenerationConfig {
      if ( packageName == null ) {
         packageName = "";
      }
      if ( StringUtils.isNotBlank( metaModelPackageName ) ) {
         importTracker = new ImportTracker( metaModelPackageName );
      }
      if ( importTracker == null ) {
         importTracker = new ImportTracker();
      }
      if ( executeLibraryMacros && ( templateLibFile == null || templateLibFile.toString().isEmpty() ) ) {
         throw new CodeGenerationException( "Missing configuration. Please provide path to velocity template library file." );
      }
      if ( executeLibraryMacros && !templateLibFile.exists() ) {
         throw new CodeGenerationException( "Incorrect configuration. Please provide a valid path to the velocity template library file." );
      }
      if ( namePrefix == null ) {
         namePrefix = "";
      }
      if ( namePostfix == null ) {
         namePostfix = "";
      }
   }
}
