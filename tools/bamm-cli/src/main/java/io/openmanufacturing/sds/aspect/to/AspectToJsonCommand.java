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
import io.openmanufacturing.sds.aspect.AspectToCommand;
import io.openmanufacturing.sds.aspectmodel.generator.json.AspectModelJsonPayloadGenerator;
import io.openmanufacturing.sds.exeption.CommandException;
import picocli.CommandLine;

@CommandLine.Command( name = AspectToJsonCommand.COMMAND_NAME,
      description = "Generate OpenAPI JSON specification for an Aspect Model",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AspectToJsonCommand extends AbstractCommand {

   public static final String COMMAND_NAME = "json";

   @CommandLine.Option( names = { "--output", "-o" }, description = "Output file path" )
   private String outputFilePath = "-";

   @CommandLine.ParentCommand
   private AspectToCommand parentCommand;

   @Override
   public void run() {
      final AspectModelJsonPayloadGenerator generator = new AspectModelJsonPayloadGenerator( loadModelOrFail( parentCommand.parentCommand.getInput() ) );
      try {
         // we intentionally override the name of the generated artifact here to the name explicitly desired by the user (outputFilePath),
         // as opposed to what the model thinks it should be called (name)
         generator.generateJsonPretty( name -> getStreamForFile( outputFilePath ) );
      } catch ( final IOException e ) {
         throw new CommandException( e );
      }
   }
}
