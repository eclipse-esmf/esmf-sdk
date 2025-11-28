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

package org.eclipse.esmf.aspectmodel.java;

/**
 * System properties that control test features to help debugging.
 */
public class Flags {
   /**
    * In order to ease debugging of unit tests that generate code, run the corresponding JVM with
    * -Dorg.eclipse.esmf.javacompiler.writesources=true
    * This will write the generated sources into files in src/test/java.
    */
   public static final String WRITE_SOURCES_PROPERTY = "org.eclipse.esmf.javacompiler.writesources";

   /**
    * Run performance tests that check runtimes and memory usage
    */
   public static final String RUN_PERFORMANCE_TEST = "org.eclipse.esmf.javacompiler.performancetest";

   private Flags() {
   }
}
