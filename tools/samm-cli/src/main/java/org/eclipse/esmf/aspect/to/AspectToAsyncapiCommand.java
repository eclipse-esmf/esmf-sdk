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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.ResolverConfigurationMixin;
import org.eclipse.esmf.aspect.AspectToCommand;
import org.eclipse.esmf.aspectmodel.generator.asyncapi.AspectModelAsyncApiGenerator;
import org.eclipse.esmf.aspectmodel.generator.asyncapi.AsyncApiSchemaArtifact;
import org.eclipse.esmf.aspectmodel.generator.asyncapi.AsyncApiSchemaGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.asyncapi.AsyncApiSchemaGenerationConfigBuilder;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.Aspect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command( name = AspectToAsyncapiCommand.COMMAND_NAME,
      description = "Generate AsyncAPI specification for an Aspect Model",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class AspectToAsyncapiCommand extends AbstractCommand {

   public static final String COMMAND_NAME = "asyncapi";

   private static final Logger LOG = LoggerFactory.getLogger( AspectToAsyncapiCommand.class );
   private static final ObjectMapper YAML_MAPPER = new YAMLMapper().enable( YAMLGenerator.Feature.MINIMIZE_QUOTES );
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

   @CommandLine.Option(
         names = { "--json", "-j" },
         description = "Generate AsyncAPI JSON specification for an Aspect Model (when not given, YAML is generated as default format)" )
   private boolean generateJsonAsyncApiSpec = false;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--separate-files", "-sf" },
         description = "Write separate files for the root document and referenced schemas." )
   private boolean writeSeparateFiles = false;

   @CommandLine.Option(
         names = { "--output", "-o" },
         description = "Output path; if --separate-files is given, this must be a directory." )
   private String outputFilePath = "-";

   @CommandLine.Option(
         names = { "--application-id", "-ai" },
         description = "Use this param for provide application id." )
   private String applicationId;

   @CommandLine.Option(
         names = { "--channel-address", "-ca" },
         description = "Use this param make possible provide channel address." )
   private String channelAddress;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--semantic-version", "-sv" },
         description = "Use the full semantic version from the Aspect Model as the version for the Aspect API." )
   private boolean useSemanticApiVersion = false;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--language", "-l" },
         description = "The language from the model for which the AsyncAPI specification should be generated (default: en)" )
   private String language = "en";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--details" },
         description = "Print detailed reports on errors" )
   private boolean details = false;

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

      final Locale locale = Optional.ofNullable( language ).map( Locale::forLanguageTag ).orElse( Locale.ENGLISH );
      final Aspect aspect = getInputHandler( parentCommand.parentCommand.getInput() ).loadAspect();
      final AsyncApiSchemaGenerationConfig config = AsyncApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( useSemanticApiVersion )
            .applicationId( applicationId )
            .channelAddress( channelAddress )
            .locale( locale )
            .build();
      final AsyncApiSchemaArtifact asyncApiSpec = new AspectModelAsyncApiGenerator( aspect, config ).singleResult();

      try {
         if ( writeSeparateFiles ) {
            writeSchemaWithSeparateFiles( asyncApiSpec );
         } else {
            writeSchemaWithInOneFile( asyncApiSpec );
         }
      } catch ( final IOException exception ) {
         throw new CommandException( "Could not generate AsyncAPI specification.", exception );
      }
   }

   private void writeSchemaWithInOneFile( final AsyncApiSchemaArtifact asyncApiSpec ) throws IOException {
      try ( final OutputStream out = getStreamForFile( outputFilePath ) ) {
         if ( generateJsonAsyncApiSpec ) {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue( out, asyncApiSpec.getContent() );
         } else {
            out.write( asyncApiSpec.getContentAsYaml().getBytes( StandardCharsets.UTF_8 ) );
         }
      }
   }

   private void writeSchemaWithSeparateFiles( final AsyncApiSchemaArtifact asyncApiSpec ) throws IOException {
      final Path root = outputFilePath == null || outputFilePath.equals( "-" ) ? Path.of( "." ) : new File( outputFilePath ).toPath();
      if ( generateJsonAsyncApiSpec ) {
         final Map<Path, JsonNode> separateFilesContent = asyncApiSpec.getContentWithSeparateSchemasAsJson();
         for ( final Map.Entry<Path, JsonNode> entry : separateFilesContent.entrySet() ) {
            try ( final OutputStream out = new FileOutputStream( root.resolve( entry.getKey() ).toFile() ) ) {
               OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue( out, entry.getValue() );
            }
         }
      } else {
         final Map<Path, String> separateFilesContentAsYaml = asyncApiSpec.getContentWithSeparateSchemasAsYaml();
         for ( final Map.Entry<Path, String> entry : separateFilesContentAsYaml.entrySet() ) {
            try ( final OutputStream out = new FileOutputStream( root.resolve( entry.getKey() ).toFile() ) ) {
               out.write( entry.getValue().getBytes( StandardCharsets.UTF_8 ) );
            }
         }
      }
   }
}
