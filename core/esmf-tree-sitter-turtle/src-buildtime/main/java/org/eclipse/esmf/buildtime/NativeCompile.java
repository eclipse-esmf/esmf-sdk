/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.buildtime;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.resolver.process.BinaryLauncher;
import org.eclipse.esmf.aspectmodel.resolver.process.ProcessLauncher;

public class NativeCompile extends ZigContext {
   private static final String LIB_NAME = "tree-sitter-turtle";

   private final Path libOutputDirectory;
   private final Path nativeSourcesDirectory;

   public NativeCompile( final Path cacheLocation, final String zigVersion, final Path libOutputDirectory,
         final Path nativeSourcesDirectory ) {
      super( cacheLocation, zigVersion );
      this.libOutputDirectory = libOutputDirectory;
      this.nativeSourcesDirectory = nativeSourcesDirectory;
   }

   private record Target(
         OperatingSystem operatingSystem,
         Architecture architecture
   ) {
      public String getTargetName() {
         return operatingSystem == OperatingSystem.LINUX
               ? "%s-linux-gnu".formatted( architecture.toString() )
               : "%s-%s".formatted( architecture().toString(), operatingSystem().toString() );
      }
   }

   private List<Target> getTargets() {
      return List.of(
            new Target( OperatingSystem.WINDOWS, Architecture.X86_64 ),
            new Target( OperatingSystem.MACOS, Architecture.X86_64 ),
            new Target( OperatingSystem.MACOS, Architecture.AARCH64 ),
            new Target( OperatingSystem.LINUX, Architecture.X86_64 ),
            new Target( OperatingSystem.LINUX, Architecture.AARCH64 ) );
   }

   private String libNameForTarget( final Target target ) {
      return switch ( target.operatingSystem() ) {
         case WINDOWS -> "%s-%s.dll".formatted( target.getTargetName(), LIB_NAME );
         case LINUX -> "%s-%s.so".formatted( target.getTargetName(), LIB_NAME );
         case MACOS -> "%s-%s.dylib".formatted( target.getTargetName(), LIB_NAME );
      };
   }

   private void runZig() {
      final File zigExe = zigExe();

      for ( final Target target : getTargets() ) {
         final List<String> args = new ArrayList<>();
         final File libFilename = libOutputDirectory.resolve( libNameForTarget( target ) ).toFile();

         if ( libFilename.exists() ) {
            continue;
         }
         args.add( "c++" );
         args.add( "-g0" );
         args.add( "-fno-sanitize=undefined" );
         args.add( "-shared" );
         args.add( "-target" );
         args.add( target.getTargetName() );
         args.add( "-I" );
         args.add( zigDir().resolve( "lib" ).resolve( "include" ).toString() );
         args.add( "-I" );
         args.add( nativeSourcesDirectory.toString() );
         args.add( "-I" );
         args.add( nativeSourcesDirectory.resolve( "include" ).toString() );
         args.add( "-I" );
         args.add( nativeSourcesDirectory.resolve( "include" ).resolve( currentOs.toString() ).toString() );
         args.add( "-o" );
         args.add( libFilename.getAbsolutePath() );
         args.add( nativeSourcesDirectory.resolve( "parser.c" ).toString() );
         args.add( nativeSourcesDirectory.resolve( "org_eclipse_esmf_treesitterturtle_TreeSitterTurtle.c" ).toString() );

         System.out.println( zigExe().getAbsolutePath() + " " + String.join( " ", args ) );
         mkdir( libOutputDirectory );
         final ProcessLauncher.ExecutionResult zigExecutionResult =
               new BinaryLauncher( zigExe ).apply( args, Optional.empty(), libOutputDirectory.toFile() );
         if ( zigExecutionResult.exitStatus() != 0 ) {
            System.err.println( zigExecutionResult.stderr() );
            throw new BuildTimeException( "Compilation of native lib failed" );
         }
         System.out.println( "Native lib compiled: " + libFilename.getAbsolutePath() );
      }
   }

   static void main( final String[] args ) {
      final Path cacheLocation = Path.of( args[0] );
      final String zigVersion = args[1];
      final Path libOutputDirectory = Path.of( args[2] );
      final Path nativeSourcesDirectory = Path.of( args[3] );
      final NativeCompile nativeCompile = new NativeCompile( cacheLocation, zigVersion, libOutputDirectory, nativeSourcesDirectory );
      nativeCompile.runZig();
   }
}
