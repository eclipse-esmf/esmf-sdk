/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.SystemUtils;

/**
 * Utility class providing helpers and workarounds needed to get the native image working properly on different platforms.
 */
public class NativeImageHelpers {

   public static void ensureRequiredEnvironment() {
      if ( System.getProperty( "java.home" ) == null && SystemUtils.OS_NAME.startsWith( "Windows" ) ) { // GraalVM native image on Windows
         // font handling which we use in diagram/document generators relies on this variable being set
         // and the font config files must be present in the "lib" subdirectory of the path from where the native image is started
         final Path nativeImagePath = Paths.get( "." ).toAbsolutePath().normalize();  // current working directory
         System.setProperty( "java.home", nativeImagePath.toString() );
      }
   }
}
