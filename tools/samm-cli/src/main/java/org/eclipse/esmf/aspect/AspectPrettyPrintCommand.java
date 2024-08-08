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
import java.io.PrintWriter;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.ExternalResolverMixin;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aspectmodel.serializer.PrettyPrinter;
import org.eclipse.esmf.metamodel.AspectModel;

import picocli.CommandLine;

@CommandLine.Command( name = AspectPrettyPrintCommand.COMMAND_NAME,
      description = "Pretty print (format) Aspect Model",
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AspectPrettyPrintCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "prettyprint";

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ExternalResolverMixin customResolver;

   @CommandLine.Option( names = { "--output", "-o" }, description = "Output file path (default: stdout)" )
   private String outputFilePath = "-";

   @CommandLine.ParentCommand
   private AspectCommand parentCommand;

   @Override
   public void run() {
      try ( final PrintWriter printWriter = new PrintWriter( getStreamForFile( outputFilePath ) ) ) {
         final File inputFile = new File( parentCommand.getInput() ).getAbsoluteFile();
         final AspectModel aspectModel = loadAspectModelOrFail( parentCommand.getInput(), customResolver );
         aspectModel.files()
               .stream()
               .filter( file -> file.sourceLocation().map( sourceLocation -> sourceLocation.equals( inputFile.toURI() ) ).orElse( false ) )
               .forEach( sourceModel -> {
                  new PrettyPrinter( sourceModel, printWriter ).print();
                  printWriter.flush();
                  printWriter.close();
               } );
      }
   }
}
