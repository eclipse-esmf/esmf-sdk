/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.java;

import java.io.File;

import io.openmanufacturing.sds.aspectmodel.generator.GenerationConfig;
import io.openmanufacturing.sds.aspectmodel.java.exception.CodeGenerationException;

/**
 * A {@link GenerationConfig} for Java code
 */
public class JavaCodeGenerationConfig implements GenerationConfig {
   private final boolean enableJacksonAnnotations;
   private final String packageName;
   private final ImportTracker importTracker;
   private final boolean executeLibraryMacros;
   private final File templateLibFile;

   public JavaCodeGenerationConfig( final boolean enableJacksonAnnotations, final String packageName, final boolean executeLibraryMacros,
         final File templateLibFile ) {
      this.enableJacksonAnnotations = enableJacksonAnnotations;
      this.packageName = packageName;
      importTracker = new ImportTracker();

      validateTemplateLibConfig( executeLibraryMacros, templateLibFile );
      this.executeLibraryMacros = executeLibraryMacros;
      this.templateLibFile = templateLibFile;
   }

   private void validateTemplateLibConfig( final boolean executeLibraryMacros, final File templateLibFile ) {
      if ( executeLibraryMacros && ( templateLibFile == null || templateLibFile.toString().isEmpty() ) ) {
         throw new CodeGenerationException( "Missing configuration. Please provide path to velocity template library file." );
      }
      if ( executeLibraryMacros && !templateLibFile.exists() ) {
         throw new CodeGenerationException( "Incorrect configuration. Please provide a valid path to the velocity template library file." );
      }
   }

   /**
    * @return a boolean indicating whether Jackson annotations are included in the generated source code or not.
    */
   public boolean doEnableJacksonAnnotations() {
      return enableJacksonAnnotations;
   }

   /**
    * @return the package name to be used for the generated source code.
    */
   public String getPackageName() {
      return packageName;
   }

   /**
    * @return an instance of the {@link ImportTracker}.
    */
   public ImportTracker getImportTracker() {
      return importTracker;
   }

   /**
    * @return a boolean indicating whether the library macros defined in the {@link #templateLibFile} file will be executed during the code generation.
    */
   public boolean doExecuteLibraryMacros() {
      return executeLibraryMacros;
   }

   /**
    * @return the path from where the file containing the library macros is to be retrieved.
    */
   public File getTemplateLibFile() {
      return templateLibFile;
   }
}
