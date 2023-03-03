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

package io.openmanufacturing.sds;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * A {@link ProcessLauncher} that executes a native binary. The absolute path of the binary must be set using the system property "binary".
 */
public class BinaryLauncher extends OsProcessLauncher {
   public BinaryLauncher() {
      super( List.of( getBinaryName() ) );
   }

   private static String getBinaryName() {
      String binary = System.getProperty( "binary" );
      if ( System.getProperty( "os.name" ).startsWith( "Windows" ) ) {
         binary = binary.replace( "/", "\\" );
         binary = binary + ".exe";
      }
      final File binaryFile = new File( binary );
      if ( binary == null || !binaryFile.exists() ) {
         fail( "Binary " + binary + " not found" );
      }
      return binary;
   }

   @Override
   protected File workingDirectoryForSubprocess( final ExecutionContext context ) {
      // for the native image we want the CWD to be the directory where the exe is located
      return Path.of( getBinaryName() ).getParent().toFile();
   }
}
