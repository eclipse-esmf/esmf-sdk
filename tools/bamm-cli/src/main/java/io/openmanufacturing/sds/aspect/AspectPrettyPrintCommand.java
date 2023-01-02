/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspect;

import java.io.File;
import java.io.PrintWriter;

import io.openmanufacturing.sds.AbstractCommand;
import io.openmanufacturing.sds.LoggingMixin;
import io.openmanufacturing.sds.aspectmodel.serializer.PrettyPrinter;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
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

   @CommandLine.Option( names = { "--output", "-o" }, description = "Output file path (default: stdout)" )
   private String outputFilePath = "-";

   @CommandLine.ParentCommand
   private AspectCommand parentCommand;

   @Override
   public void run() {
      final PrintWriter printWriter = new PrintWriter( getStreamForFile( outputFilePath ) );
      final File file = new File( parentCommand.getInput() );
      final File inputFile = file.getAbsoluteFile();
      final AspectModelUrn aspectModelUrn = fileToUrn( inputFile );
      loadButNotResolveModel( inputFile ).forEach( versionedModel -> {
         new PrettyPrinter( versionedModel, aspectModelUrn, printWriter ).print();
         printWriter.flush();
         printWriter.close();
      } );
   }
}
