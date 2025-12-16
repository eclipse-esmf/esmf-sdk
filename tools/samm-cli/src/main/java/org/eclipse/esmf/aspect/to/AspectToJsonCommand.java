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

package org.eclipse.esmf.aspect.to;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.ResolverConfigurationMixin;
import org.eclipse.esmf.aspect.AspectToCommand;
import org.eclipse.esmf.aspectmodel.generator.json.AspectModelJsonPayloadGenerator;
import org.eclipse.esmf.aspectmodel.generator.json.JsonPayloadGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.json.JsonPayloadGenerationConfigBuilder;

import picocli.CommandLine;

@CommandLine.Command(
      name = AspectToJsonCommand.COMMAND_NAME,
      description = "Generate example JSON payload data for an Aspect Model",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class AspectToJsonCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "json";

   @CommandLine.Option(
         names = { "--output", "-o" },
         description = "Output file path" )
   private String outputFilePath = "-";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--add-type-attribute", "-ta" },
         description = "Add @type attribute for inherited Entities" )
   private boolean addTypeAttribute = false;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--details" },
         description = "Print detailed reports on errors" )
   private boolean details = false;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--fail-on-empty-example-value" },
         description = "Throw ERR_EMPTY_EXAMPLE_VALUE if an example value cannot be generated for a regular expression constraint, "
               + "instead of returning an empty value." )
   private boolean failOnEmptyExampleValue = false;

   @CommandLine.ParentCommand
   private AspectToCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ResolverConfigurationMixin resolverConfiguration;

   @Override
   public void run() {
      setDetails( details );
      setResolverConfig( resolverConfiguration );

      final JsonPayloadGenerationConfig config = JsonPayloadGenerationConfigBuilder.builder()
            .addTypeAttributeForEntityInheritance( addTypeAttribute )
            .build();

      final AspectModelJsonPayloadGenerator generator = new AspectModelJsonPayloadGenerator(
            getInputHandler( parentCommand.parentCommand.getInput() ).loadAspect(), config );
      // we intentionally override the name of the generated artifact here to the name explicitly desired by the user (outputFilePath),
      // as opposed to what the model thinks it should be called (name)
      generator.generate( name -> getStreamForFile( outputFilePath ) );
   }
}
