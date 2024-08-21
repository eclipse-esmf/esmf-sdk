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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.ResolverConfigurationMixin;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.serializer.PrettyPrinter;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.AspectModel;

import picocli.CommandLine;

@CommandLine.Command( name = AspectPrettyPrintCommand.COMMAND_NAME,
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

   @CommandLine.Option( names = { "--output", "-o" }, description = "Output file path (default: stdout)" )
   String outputFilePath = "-";

   @CommandLine.Option( names = { "--overwrite", "-w" }, description = "Overwrite the input file" )
   boolean overwrite;

   @CommandLine.ParentCommand
   private AspectCommand parentCommand;

   @Override
   public void run() {
      final File inputFile = new File( parentCommand.getInput() ).getAbsoluteFile();
      final AspectModel aspectModel = loadAspectModelOrFail( parentCommand.getInput(), resolverConfiguration );

      for ( final AspectModelFile sourceFile : aspectModel.files() ) {
         if ( !sourceFile.sourceLocation().map( uri -> uri.equals( inputFile.toURI() ) ).orElse( false ) ) {
            continue;
         }

         OutputStream outputStream = null;
         if ( overwrite ) {
            final URI fileUri = sourceFile.sourceLocation().orElseThrow();
            try {
               outputStream = new FileOutputStream( new File( fileUri ) );
            } catch ( final FileNotFoundException exception ) {
               throw new CommandException( "Can not write to " + fileUri );
            }
         }
         if ( outputStream == null ) {
            outputStream = getStreamForFile( outputFilePath );
         }

         try ( final PrintWriter printWriter = new PrintWriter( outputStream ) ) {
            new PrettyPrinter( sourceFile, printWriter ).print();
            printWriter.flush();
         }
      }
   }
}
