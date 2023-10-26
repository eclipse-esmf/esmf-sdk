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

import org.eclipse.esmf.substitution.IsWindows;

/**
 * Utility class providing helpers and workarounds needed to get the native image working properly on different platforms.
 */
public class NativeImageHelpers {
   public static void ensureRequiredEnvironment() {
      if ( System.getProperty( "java.home" ) == null ) {
         // Font handling which we use in diagram/document generators relies on this variable being set
         // and the font config files must be present in the "lib" subdirectory of the path from where the native image is started.
         // The check for the java.home property is done by sun.awt.FontConfiguration#findFontConfigFile which is called
         // transitively by other AWT code
         final Path nativeImagePath = Paths.get( "." ).toAbsolutePath().normalize();  // current working directory
         System.setProperty( "java.home", nativeImagePath.toString() );

         if ( new IsWindows().getAsBoolean() ) {
            // Set to headless mode, because instantiation of AWT graphics context in Windows is flaky
            System.setProperty( "java.awt.headless", "true" );
         }
      }
   }
}
