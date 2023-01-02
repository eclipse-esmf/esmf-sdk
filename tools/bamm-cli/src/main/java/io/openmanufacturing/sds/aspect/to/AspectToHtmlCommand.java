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

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import io.openmanufacturing.sds.AbstractCommand;
import io.openmanufacturing.sds.ExternalResolverMixin;
import io.openmanufacturing.sds.LoggingMixin;
import io.openmanufacturing.sds.aspect.AspectToCommand;
import io.openmanufacturing.sds.aspectmodel.generator.docu.AspectModelDocumentationGenerator;
import io.openmanufacturing.sds.exception.CommandException;
import picocli.CommandLine;

@CommandLine.Command( name = AspectToHtmlCommand.COMMAND_NAME,
      description = "Generate HTML documentation for an Aspect Model",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AspectToHtmlCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "html";

   @CommandLine.Option( names = { "--output", "-o" }, description = "Output file path (default: stdout)" )
   private String outputFilePath = "-";

   @CommandLine.Option( names = { "--css", "-c" }, description = "CSS file with custom styles to be included in the generated HTML documentation" )
   private String customCSSFile;

   @CommandLine.Option( names = { "--language", "-l" }, description = "The language from the model for which the html should be generated (default: en)" )
   private String language = "en";

   @CommandLine.ParentCommand
   private AspectToCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ExternalResolverMixin customResolver;

   @Override
   public void run() {
      try {
         final AspectModelDocumentationGenerator generator = new AspectModelDocumentationGenerator(
               loadModelOrFail( parentCommand.parentCommand.getInput(), customResolver ) );
         final Map<AspectModelDocumentationGenerator.HtmlGenerationOption, String> generationArgs = new HashMap<>();
         generationArgs.put( AspectModelDocumentationGenerator.HtmlGenerationOption.STYLESHEET, "" );
         if ( customCSSFile != null ) {
            final String css = FileUtils.readFileToString( new File( customCSSFile ), "UTF-8" );
            generationArgs.put( AspectModelDocumentationGenerator.HtmlGenerationOption.STYLESHEET, css );
         }
         generator.generate( artifact -> getStreamForFile( outputFilePath ), generationArgs, Locale.forLanguageTag( language ) );
      } catch ( final Exception e ) {
         throw new CommandException( e );
      }
   }
}
