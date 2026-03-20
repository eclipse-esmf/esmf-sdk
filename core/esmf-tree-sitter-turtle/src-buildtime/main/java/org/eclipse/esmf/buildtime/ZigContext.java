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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.ArchUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.arch.Processor;

public class ZigContext {
   protected final OperatingSystem currentOs;
   protected final Architecture currentArchitecture;
   protected final Path cacheLocation;
   protected final String zigVersion;

   protected enum OperatingSystem {
      WINDOWS, MACOS, LINUX;

      @Override
      public String toString() {
         return super.toString().toLowerCase();
      }
   }

   protected enum Architecture {
      X86_64, AARCH64;

      @Override
      public String toString() {
         return super.toString().toLowerCase();
      }
   }

   protected void mkdir( final Path path ) {
      try {
         Files.createDirectories( path );
      } catch ( final IOException exception ) {
         throw new BuildTimeException( exception );
      }
   }


   public ZigContext( final Path cacheLocation, final String zigVersion ) {
      this.cacheLocation = cacheLocation;
      this.zigVersion = zigVersion;

      final Processor processor = ArchUtils.getProcessor();
      if ( processor.isX86() && processor.is64Bit() ) {
         currentArchitecture = Architecture.X86_64;
      } else if ( processor.isAarch64() && processor.is64Bit() ) {
         currentArchitecture = Architecture.AARCH64;
      } else {
         throw new BuildTimeException( "Unsupported architecture: " + processor.getType().getLabel()
               + "/" + processor.getArch().getLabel() );
      }

      if ( SystemUtils.IS_OS_WINDOWS ) {
         currentOs = OperatingSystem.WINDOWS;
         if ( !processor.isX86() ) {
            throw new BuildTimeException( "Unsupported architecture: " + processor.getType().getLabel()
                  + "/" + processor.getArch().getLabel() );
         }
      } else if ( SystemUtils.IS_OS_MAC ) {
         currentOs = OperatingSystem.MACOS;
      } else if ( SystemUtils.IS_OS_LINUX ) {
         currentOs = OperatingSystem.LINUX;
      } else {
         throw new BuildTimeException( "Unsupported operating system: " + SystemUtils.OS_NAME );
      }
   }

   protected Path zigDir() {
      return cacheLocation.resolve( "zig" );
   }

   protected File zigExe() {
      final Path zigExecutablePath = switch ( currentOs ) {
         case WINDOWS -> Path.of( "zig-x86_64-windows-" + zigVersion, "zig.exe" );
         case MACOS, LINUX -> Path.of( "zig-%s-%s-%s".formatted( currentArchitecture, currentOs, zigVersion ), "zig" );
      };
      return zigDir().resolve( zigExecutablePath ).toFile();
   }
}
