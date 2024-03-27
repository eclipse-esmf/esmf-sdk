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

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.ExternalResolverMixin;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aspect.AspectToCommand;
import org.eclipse.esmf.aspectmodel.generator.openapi.AspectModelOpenApiGenerator;
import org.eclipse.esmf.aspectmodel.generator.openapi.PagingOption;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.Aspect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import io.vavr.control.Try;
import picocli.CommandLine;

@CommandLine.Command( name = AspectToOpenapiCommand.COMMAND_NAME,
      description = "Generate OpenAPI specification for an Aspect Model",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AspectToOpenapiCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "openapi";
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
   private static final ObjectMapper YAML_MAPPER = new YAMLMapper().enable( YAMLGenerator.Feature.MINIMIZE_QUOTES );

   @CommandLine.Option( names = { "--api-base-url",
         "-b" }, description = "The base url for the Aspect API used in the OpenAPI specification.",
         required = true )
   private String aspectApiBaseUrl = "";

   @CommandLine.ArgGroup( exclusive = false )
   private JsonCommandGroup jsonCommandGroup;

   static class JsonCommandGroup {
      @CommandLine.Option( names = { "--json", "-j" }, required = true,
            description = "Generate OpenAPI JSON specification for an Aspect Model (when not given, YAML is generated as default format)" )
      boolean generateJsonOpenApiSpec = false;

      @CommandLine.Option( names = { "--comment", "-c" }, required = false,
            description = "Generate $comment OpenAPI keyword for samm:see attributes in the model." )
      boolean generateCommentForSeeAttributes = false;
   }

   @CommandLine.Option( names = { "--parameter-file",
         "-p" }, description = "The path to a file including the parameter for the Aspect API endpoints." )
   private String aspectParameterFile;

   @CommandLine.Option( names = { "--semantic-version", "-sv" },
         description = "Use the full semantic version from the Aspect Model as the version for the Aspect API." )
   private boolean useSemanticApiVersion = false;

   @CommandLine.Option( names = { "--resource-path", "-r" }, description = "The resource path for the Aspect API endpoints." )
   private String aspectResourcePath;

   @CommandLine.Option( names = { "--include-query-api", "-q" },
         description = "Include the path for the Query Aspect API Endpoint in the OpenAPI specification." )
   private boolean includeQueryApi = false;

   @CommandLine.Option( names = { "--paging-none", "-pn" },
         description = "Exclude paging information for the Aspect API Endpoint in the OpenAPI specification." )
   private boolean excludePaging = false;

   @CommandLine.Option( names = { "--paging-cursor-based", "-pc" },
         description = "In case there is more than one paging possibility, it has to be cursor based paging." )
   private boolean aspectCursorBasedPaging = false;

   @CommandLine.Option( names = { "--paging-offset-based", "-po" },
         description = "In case there is more than one paging possibility, it has to be offset based paging." )
   private boolean aspectOffsetBasedPaging = false;

   @CommandLine.Option( names = { "--paging-time-based", "-pt" },
         description = "In case there is more than one paging possibility, it has to be time based paging." )
   private boolean aspectTimeBasedPaging = false;

   @CommandLine.Option( names = { "--output", "-o" }, description = "Output file path" )
   private String outputFilePath = "-";

   @CommandLine.Option( names = { "--language", "-l" },
         description = "The language from the model for which the OpenAPI specification should be generated (default: en)" )
   private String language = "en";

   @CommandLine.ParentCommand
   private AspectToCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ExternalResolverMixin customResolver;

   @Override
   public void run() {
      final Locale locale = Optional.ofNullable( language ).map( Locale::forLanguageTag ).orElse( Locale.ENGLISH );
      final AspectModelOpenApiGenerator generator = new AspectModelOpenApiGenerator();
      final Aspect aspect = loadModelOrFail( parentCommand.parentCommand.getInput(), customResolver ).aspect();

      final JsonNode spec = generator.applyForJson( aspect, useSemanticApiVersion, aspectApiBaseUrl,
            Optional.ofNullable( aspectResourcePath ),
            readAspectParameterFile(), includeQueryApi, getPaging(), locale );

      if ( jsonCommandGroup != null && jsonCommandGroup.generateJsonOpenApiSpec ) {
         saveJson( spec );
      } else {
         saveYaml( spec );
      }
   }

   private void saveYaml( final JsonNode spec ) {

      try ( final OutputStream out = getStreamForFile( outputFilePath ) ) {
         YAML_MAPPER.writerWithDefaultPrettyPrinter().writeValue( out, spec );
      } catch ( final IOException exception ) {
         throw new CommandException( "Could not generate OpenAPI YAML specification.", exception );
      }
   }

   private void saveJson( final JsonNode spec ) {
      try ( final OutputStream out = getStreamForFile( outputFilePath ) ) {
         OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue( out, spec );
      } catch ( final IOException exception ) {
         throw new CommandException( "Could not format OpenApi JSON specification.", exception );
      }
   }

   private Optional<JsonNode> readAspectParameterFile() {
      if ( aspectParameterFile == null || aspectParameterFile.isEmpty() ) {
         return Optional.empty();
      }
      final String extension = FilenameUtils.getExtension( aspectParameterFile ).toUpperCase();
      final Try<String> fileData = Try.of( () -> getFileAsString( aspectParameterFile ) ).mapTry( Optional::get );
      return switch ( extension ) {
         case "YAML", "YML" -> fileData
               .mapTry( data -> YAML_MAPPER.readValue( data, Object.class ) )
               .mapTry( OBJECT_MAPPER::writeValueAsString )
               .mapTry( OBJECT_MAPPER::readTree )
               .toJavaOptional();
         case "JSON" -> fileData
               .mapTry( OBJECT_MAPPER::readTree )
               .toJavaOptional();
         default -> throw new CommandException( format( "File extension [%s] not supported.", extension ) );
      };
   }

   private Optional<String> getFileAsString( final String filePath ) {
      if ( filePath == null || filePath.isEmpty() ) {
         return Optional.empty();
      }
      final File f = new File( filePath );
      if ( f.exists() && !f.isDirectory() ) {
         try ( final InputStream inputStream = new FileInputStream( filePath ) ) {
            return Optional.of( IOUtils.toString( inputStream, StandardCharsets.UTF_8 ) );
         } catch ( final IOException e ) {
            throw new CommandException( format( "Could not load file %s.", filePath ), e );
         }
      }
      throw new CommandException( format( "File does not exist %s.", filePath ) );
   }

   private Optional<PagingOption> getPaging() {
      if ( excludePaging ) {
         return Optional.of( PagingOption.NO_PAGING );
      }
      if ( aspectCursorBasedPaging ) {
         return Optional.of( PagingOption.CURSOR_BASED_PAGING );
      }
      if ( aspectOffsetBasedPaging ) {
         return Optional.of( PagingOption.OFFSET_BASED_PAGING );
      }
      if ( aspectTimeBasedPaging ) {
         return Optional.of( PagingOption.TIME_BASED_PAGING );
      }
      return Optional.empty();
   }
}
