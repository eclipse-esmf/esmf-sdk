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

package org.eclipse.esmf.aspect;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.ResolverConfigurationMixin;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.exception.CommandException;

import picocli.CommandLine;

@CommandLine.Command(
      name = AspectPrettyPrintCommand.COMMAND_NAME,
      description = "Pretty print (format) Aspect Model",
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class AspectPrettyPrintCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "prettyprint";

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ResolverConfigurationMixin resolverConfiguration;

   @CommandLine.Option(
         names = { "--output", "-o" },
         description = "Output file path (default: stdout)" )
   String outputFilePath = "-";

   @CommandLine.Option(
         names = { "--overwrite", "-w" },
         description = "Overwrite the input file" )
   boolean overwrite;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--details" },
         description = "Print detailed reports on errors" )
   private boolean details = false;

   @CommandLine.ParentCommand
   private AspectCommand parentCommand;

   @SuppressWarnings( "UseOfSystemOutOrSystemErr" )
   @Override
   public void run() {
      setDetails( details );
      setResolverConfig( resolverConfiguration );

      final String input = parentCommand.getInput();
      final AspectModelFile aspectModelFile = getInputHandler( input ).loadAspectModelFile();

      if ( outputFilePath.equals( "-" ) && !overwrite ) {
         final String formattedModel = AspectSerializer.INSTANCE.aspectModelFileToString( aspectModelFile );
         System.out.println( formattedModel );
      } else if ( overwrite ) {
         AspectSerializer.INSTANCE.write( aspectModelFile );
      } else {
         final File inputFile = absoluteFile( new File( aspectModelFile.sourceLocation().orElseThrow() ) );
         final File outputFile = absoluteFile( new File( outputFilePath ) );
         if ( inputFile.equals( outputFile ) ) {
            throw new CommandException( "Can't overwrite existing file. To force overwrite, use --overwrite." );
         }

         final String formattedModel = AspectSerializer.INSTANCE.aspectModelFileToString( aspectModelFile );
         try ( final OutputStream out = getStreamForFile( outputFilePath ) ) {
            out.write( formattedModel.getBytes( StandardCharsets.UTF_8 ) );
         } catch ( final IOException exception ) {
            throw new CommandException( "Could not write to output file" );
         }
      }
   }
}
