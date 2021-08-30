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

import io.openmanufacturing.sds.aspectmodel.generator.GenerationConfig;

/**
 * A {@link GenerationConfig} for Java code
 */
public class JavaCodeGenerationConfig implements GenerationConfig {
   private final boolean enableJacksonAnnotations;
   private final String packageName;
   private final ImportTracker importTracker;

   public JavaCodeGenerationConfig( final boolean enableJacksonAnnotations, final String packageName ) {
      this.enableJacksonAnnotations = enableJacksonAnnotations;
      this.packageName = packageName;
      importTracker = new ImportTracker();
   }

   public boolean doEnableJacksonAnnotations() {
      return enableJacksonAnnotations;
   }

   public String getPackageName() {
      return packageName;
   }

   public ImportTracker getImportTracker() {
      return importTracker;
   }
}
