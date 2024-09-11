/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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
import org.eclipse.esmf.aspectmodel.generator.jsonld.AspectModelToJsonLdGenerator;
import org.eclipse.esmf.exception.CommandException;

import picocli.CommandLine;

@CommandLine.Command( name = AspectToJsonLdCommand.COMMAND_NAME,
      description = "Generate JSON-LD representation of an Aspect Model",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class AspectToJsonLdCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "jsonld";

   @CommandLine.Option( names = { "--output", "-o" }, description = "Output file path" )
   private String outputFilePath = "-";

   @CommandLine.ParentCommand
   private AspectToCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ResolverConfigurationMixin resolverConfiguration;

   @Override
   public void run() {
      final AspectModelToJsonLdGenerator generator = new AspectModelToJsonLdGenerator(
            loadAspectOrFail( parentCommand.parentCommand.getInput(), resolverConfiguration ) );
      try {
         // we intentionally override the name of the generated artifact here to the name explicitly desired by the user (outputFilePath),
         // as opposed to what the model thinks it should be called (name)
         generator.generateJsonLd( name -> getStreamForFile( outputFilePath ) );
      } catch ( final IOException e ) {
         throw new CommandException( e );
      }
   }
}
