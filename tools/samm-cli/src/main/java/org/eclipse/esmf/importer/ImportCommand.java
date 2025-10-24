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

package org.eclipse.esmf.importer;

import java.io.File;
import java.net.URI;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.AspectModelUrnConverter;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeManager;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeManagerConfig;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeManagerConfigBuilder;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.change.AddAspectModelFile;
import org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporterConfig;
import org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporterConfigBuilder;
import org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaToAspect;
import org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaToEntity;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.resolver.fs.ModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.exception.SubCommandException;
import org.eclipse.esmf.metamodel.StructureElement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import picocli.CommandLine;

@CommandLine.Command(
      name = ImportCommand.COMMAND_NAME,
      description = "Imports other formats to Aspect Models. Supported types: *.schema.json",
      subcommands = {
            CommandLine.HelpCommand.class
      },
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class ImportCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "import";

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Parameters(
         paramLabel = "INPUT",
         description = "Input file",
         arity = "1",
         index = "0" )
   private String input;

   @CommandLine.Option(
         names = { "-js", "--json-schema" },
         description = "Interpret the input file as JSON Schema"
   )
   private boolean jsonSchema;

   @CommandLine.Parameters(
         paramLabel = "URN",
         description = "Aspect Model URN of the Aspect Model element to create",
         arity = "1",
         index = "1",
         converter = AspectModelUrnConverter.class
   )
   private AspectModelUrn elementUrn;

   @CommandLine.Option(
         names = { "--output-directory", "-d" },
         description = "Output directory to write files to"
   )
   private String outputPath = ".";

   @CommandLine.Option(
         names = { "--dry-run" },
         description = "Emulate import operation and print a report of changes that would be performed"
   )
   private boolean dryRun;

   @CommandLine.Option(
         names = { "--force" },
         description = "Force creation/overwriting of existing files"
   )
   private boolean force;

   @CommandLine.Option(
         names = { "-e", "--entity" },
         description = "Create a samm:Entity instead of a samm:Aspect"
   )
   private boolean entityInsteadOfAspect;

   @Override
   public void run() {
      if ( !input.endsWith( ".schema.json" ) && !jsonSchema ) {
         throw new CommandException(
               "Do not know how to process input. File name must end with .schema.json or --json-schema switch must be used." );
      }

      final File file = new File( input );
      final ObjectMapper objectMapper = new ObjectMapper();
      try {
         final JsonNode jsonSchema = objectMapper.readTree( file );
         final JsonSchemaImporterConfig config = JsonSchemaImporterConfigBuilder.builder()
               .aspectModelUrn( elementUrn )
               .addTodo( true )
               .build();
         final StructureElement modelElement = entityInsteadOfAspect
               ? new JsonSchemaToEntity( jsonSchema, config ).generate().findFirst().orElseThrow().getContent()
               : new JsonSchemaToAspect( jsonSchema, config ).generate().findFirst().orElseThrow().getContent();

         final String modelSource = AspectSerializer.INSTANCE.modelElementToString( modelElement );

         final File modelsRootLocation = new File( outputPath );
         if ( modelsRootLocation.exists() && !modelsRootLocation.isDirectory() ) {
            throw new SubCommandException( "Given models root is not a directory: " + modelsRootLocation );
         }
         mkdirs( modelsRootLocation );
         final ModelsRoot modelsRoot = new StructuredModelsRoot( modelsRootLocation.toPath() );
         final Change change = new AddAspectModelFile( AspectModelFileLoader.load( modelSource, URI.create( "inmemory" ) ) );
         final AspectChangeManagerConfig changeConfig = AspectChangeManagerConfigBuilder.builder().build();
         final AspectChangeManager changeContext = new AspectChangeManager( changeConfig );
         performRefactoring( changeContext, change, dryRun, force );
      } catch ( final Exception exception ) {
         throw new CommandException( exception );
      }
   }

   private void mkdirs( final File directory ) {
      if ( directory.exists() ) {
         if ( directory.isDirectory() ) {
            return;
         }
         throw new SubCommandException( "Could not create directory " + directory + ": It already exists but is not a directory" );
      }
      if ( !directory.mkdirs() ) {
         throw new SubCommandException( "Could not create directory: " + directory );
      }
   }
}
