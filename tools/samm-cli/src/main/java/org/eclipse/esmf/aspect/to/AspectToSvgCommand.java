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

package org.eclipse.esmf.aspect.to;

import java.io.IOException;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.ResolverConfigurationMixin;
import org.eclipse.esmf.aspect.AspectToCommand;
import org.eclipse.esmf.aspectmodel.generator.diagram.DiagramGenerationConfig;
import org.eclipse.esmf.exception.CommandException;

import picocli.CommandLine;

@CommandLine.Command(
      name = AspectToSvgCommand.COMMAND_NAME,
      description = "Generate SVG diagram for an Aspect Model",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class AspectToSvgCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "svg";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--output", "-o" },
         description = "Output file path" )
   private String outputFilePath = "-";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--language", "-l" },
         description = "The language from the model for which the diagram should be generated (default: en)" )
   private String language = "en";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--details" },
         description = "Print detailed reports on errors" )
   private boolean details = false;

   @CommandLine.ParentCommand
   private AspectToCommand parentCommand;

   @CommandLine.Mixin
   private ResolverConfigurationMixin resolverConfiguration;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @Override
   public void run() {
      setResolverConfig( resolverConfiguration );
      setResolverConfig( resolverConfiguration );

      try {
         generateDiagram( parentCommand.parentCommand.getInput(), DiagramGenerationConfig.Format.SVG, outputFilePath, language );
      } catch ( final IOException e ) {
         throw new CommandException( e );
      }
   }
}
