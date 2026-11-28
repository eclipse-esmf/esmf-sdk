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

package org.eclipse.esmf.aspectmodel.generator.ts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.commons.io.IOUtils;

import org.eclipse.esmf.aspectmodel.generator.exception.CodeGenerationException;

public class TsFormatPrettier {
   /**
    * Formats TypeScript code using Prettier with an optional custom configuration file.
    *
    * @param code The TypeScript code as a string.
    * @param prettierConfigPath The file path to the Prettier configuration file (e.g., `.prettier`).
    *        If null or empty, Prettier's default settings will be used.
    * @return The formatted TypeScript code as a string.
    */
   public static String applyFormatter( final String code, final String prettierConfigPath ) {
      if ( !isNpxInstalled() ) {
         throw new CodeGenerationException(
               "`npx` is not installed or not available in PATH. Please install Node.js and ensure `npx` is accessible." );
      }

      try {
         final Path tempFile = Files.createTempFile( "typescriptTempFile", ".ts" );
         Files.writeString( tempFile, code, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING );
         final ProcessBuilder processBuilder = getProcessBuilder( prettierConfigPath, tempFile );
         final Process process = processBuilder.start();

         final StringWriter writer = new StringWriter();
         IOUtils.copy( process.getInputStream(), writer, StandardCharsets.UTF_8 );

         final int exitCode = process.waitFor();
         if ( exitCode != 0 ) {
            throw new CodeGenerationException( "Prettier formatting failed with exit code: %s".formatted( exitCode ) );
         }

         Files.deleteIfExists( tempFile );
         return writer.toString().trim();
      } catch ( final IOException | InterruptedException e ) {
         throw new CodeGenerationException( "Failed to format TypeScript code", e );
      }
   }

   /**
    * Checks if `npx` is installed and available in the system PATH.
    *
    * @return true if `npx` is available, false otherwise.
    */
   private static boolean isNpxInstalled() {
      try {
         final ProcessBuilder processBuilder = new ProcessBuilder( "npx", "--version" );
         processBuilder.redirectErrorStream( true );
         final Process process = processBuilder.start();

         try ( final BufferedReader reader =
               new BufferedReader( new InputStreamReader( process.getInputStream(), StandardCharsets.UTF_8 ) ) ) {
            final String line = reader.readLine();
            if ( line != null && !line.isEmpty() ) {
               return true;
            }
         }

         final int exitCode = process.waitFor();
         return exitCode == 0;
      } catch ( final IOException | InterruptedException e ) {
         return false;
      }
   }

   private static ProcessBuilder getProcessBuilder( final String prettierConfig, final Path tempFile ) {
      final ProcessBuilder processBuilder;
      if ( prettierConfig != null && !prettierConfig.isEmpty() ) {
         processBuilder = new ProcessBuilder(
               "npx", "prettier",
               "--parser", "typescript",
               "--config", prettierConfig,
               tempFile.toAbsolutePath().toString()
         );
      } else {
         final String[] strings = { "npx", "prettier",
               "--parser", "typescript",
               tempFile.toAbsolutePath().toString() };
         processBuilder = new ProcessBuilder(
               strings
         );
      }
      return processBuilder;
   }
}
