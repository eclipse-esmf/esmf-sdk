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

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.SystemUtils;

/**
 * A {@link ProcessLauncher} that executes an executable jar. The absolute path of the jar must be set using the system property
 * "executableJar".
 */
public class ExecutableJarLauncher extends OsProcessLauncher {
   public ExecutableJarLauncher() {
      super( buildCommand() );
   }

   private static List<String> buildCommand() {
      final String jarFile = System.getProperty( "executableJar" );
      if ( jarFile == null || !new File( jarFile ).exists() ) {
         fail( "Executable jar " + jarFile + " not found" );
      }

      final List<String> commandWithArguments = new ArrayList<>();
      commandWithArguments.add( ProcessHandle.current().info().command().orElse( "java" ) );
      commandWithArguments.add( "-Djava.awt.headless=true" );
      commandWithArguments.add( "-jar" );
      commandWithArguments.add( jarFile );
      return commandWithArguments;
   }
}
