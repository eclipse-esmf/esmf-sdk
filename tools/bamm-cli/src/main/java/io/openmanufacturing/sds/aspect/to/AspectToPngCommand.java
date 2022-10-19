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

package io.openmanufacturing.sds.aspect.to;

import java.io.IOException;

import io.openmanufacturing.sds.AbstractCommand;
import io.openmanufacturing.sds.ExternalResolverMixin;
import io.openmanufacturing.sds.aspect.AspectToCommand;
import io.openmanufacturing.sds.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import io.openmanufacturing.sds.exception.CommandException;
import picocli.CommandLine;

@CommandLine.Command( name = AspectToPngCommand.COMMAND_NAME,
      description = "Generate PNG diagram for an Aspect Model",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AspectToPngCommand extends AbstractCommand {

   public static final String COMMAND_NAME = "png";

   @CommandLine.Option( names = { "--output", "-o" },
         description = "Output file path (default: stdout; as PNG is a binary format, it is strongly recommended to output the result to a file by using the -o option or the console redirection operator '>')" )
   private String outputFilePath = "-";

   @CommandLine.Option( names = { "--language", "-l" }, description = "The language from the model for which the diagram should be generated (default: en)" )
   private String language = "en";

   @CommandLine.ParentCommand
   private AspectToCommand parentCommand;

   @CommandLine.Mixin
   private ExternalResolverMixin customResolver;

   @Override
   public void run() {
      try {
         generateDiagram( parentCommand.parentCommand.getInput(), AspectModelDiagramGenerator.Format.PNG, outputFilePath, language, customResolver );
      } catch ( final IOException e ) {
         throw new CommandException( e );
      }
   }
}
