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

package org.eclipse.esmf.aspectmodel.resolver.process;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ProcessLauncher} that executes an executable jar.
 */
public class ExecutableJarLauncher extends OsProcessLauncher {
   public ExecutableJarLauncher( final File executableJar ) {
      this( executableJar, List.of(), true );
   }

   public ExecutableJarLauncher( final File executableJar, final List<String> jvmArguments, final boolean disableWarning ) {
      super( buildCommand( executableJar, jvmArguments, disableWarning ) );
   }

   private static List<String> buildCommand( final File executableJar, final List<String> jvmArguments, final boolean disableWarning ) {
      final List<String> commandWithArguments = new ArrayList<>();
      commandWithArguments.add( ProcessHandle.current().info().command().orElse( "java" ) );
      if ( disableWarning ) {
         // Temporary disable warning messages from the output error stream until https://github.com/oracle/graal/issues/12623 is resolved
         // Delete these two arguments in github actions too
         commandWithArguments.add( "--enable-native-access=ALL-UNNAMED" );
         commandWithArguments.add( "--sun-misc-unsafe-memory-access=allow" );
      }
      commandWithArguments.addAll( jvmArguments );
      commandWithArguments.add( "-jar" );
      commandWithArguments.add( executableJar.getAbsolutePath() );
      return commandWithArguments;
   }
}
