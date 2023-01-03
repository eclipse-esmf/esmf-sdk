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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * A {@link ProcessLauncher} that executes an executable jar. The absolute path of the jar must be set using the system property "executableJar".
 * Additionally, if GraalVM's native-binary tool is found on the PATH and the system property "graalVmConfigPath" is set, the execution of the
 * executable jar will use GraalVM's
 * <a href="https://www.graalvm.org/22.3/reference-manual/native-image/metadata/AutomaticMetadataCollection/">Tracing Agent</a>
 * and write corresponding config files to the directory given in graalVmConfigPath.
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
      if ( isNativeImageBinaryOnPath() && System.getProperty( "graalVmConfigPath" ) != null ) {
         commandWithArguments.add( "-agentlib:native-image-agent=config-merge-dir=" + System.getProperty( "graalVmConfigPath" ) );
      }
      commandWithArguments.add( "-Djava.awt.headless=true" );
      commandWithArguments.add( "-jar" );
      commandWithArguments.add( jarFile );
      return commandWithArguments;
   }

   private static boolean isNativeImageBinaryOnPath() {
      return Stream.of( System.getenv( "PATH" ).split( Pattern.quote( File.pathSeparator ) ) )
            .map( Paths::get )
            .anyMatch( path -> Files.exists( path.resolve( "native-image" ) ) );
   }
}
