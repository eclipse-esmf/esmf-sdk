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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.openmanufacturing.sds.AbstractCommand;
import io.openmanufacturing.sds.aspect.AspectToCommand;
import io.openmanufacturing.sds.aspectmodel.generator.jsonschema.AspectModelJsonSchemaGenerator;
import io.openmanufacturing.sds.exception.CommandException;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import picocli.CommandLine;

@CommandLine.Command( name = AspectToJsonSchemaCommand.COMMAND_NAME,
      description = "Generate JSON schema for an Aspect Model",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AspectToJsonSchemaCommand extends AbstractCommand {

   public static final String COMMAND_NAME = "schema";

   @CommandLine.Option( names = { "--output", "-o" }, description = "Output file path (default: stdout)" )
   private String outputFilePath = "-";

   @CommandLine.ParentCommand
   private AspectToCommand parentCommand;

   @Override
   public void run() {
      final AspectModelJsonSchemaGenerator generator = new AspectModelJsonSchemaGenerator();
      final Aspect aspect = AspectModelLoader.fromVersionedModelUnchecked( loadModelOrFail( parentCommand.parentCommand.getInput() ) );
      final JsonNode schema = generator.apply( aspect );

      withOutputStream( outputFilePath, outputStream -> {
         final ObjectMapper objectMapper = new ObjectMapper();
         try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue( outputStream, schema );
         } catch ( final IOException exception ) {
            throw new CommandException( "Could not format JSON Schema", exception );
         }
      } );
   }
}
