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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.openmanufacturing.sds.AbstractCommand;
import io.openmanufacturing.sds.ExternalResolverMixin;
import io.openmanufacturing.sds.LoggingMixin;
import io.openmanufacturing.sds.aspect.AspectToCommand;
import io.openmanufacturing.sds.aspectmodel.generator.openapi.AspectModelOpenApiGenerator;
import io.openmanufacturing.sds.aspectmodel.generator.openapi.PagingOption;
import io.openmanufacturing.sds.exception.CommandException;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
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

   @CommandLine.Option( names = { "--api-base-url", "-b" }, description = "The base url for the Aspect API used in the OpenAPI specification.",
         required = true )
   private String aspectApiBaseUrl = "";

   @CommandLine.Option( names = { "--json", "-j" },
         description = "Generate OpenAPI JSON specification for an Aspect Model (when not given, YAML is generated as default format)" )
   private boolean generateJsonOpenApiSpec = false;

   @CommandLine.Option( names = { "--parameter-file", "-p" }, description = "The path to a file including the parameter for the Aspect API endpoints." )
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
      if ( generateJsonOpenApiSpec ) {
         generateJson( generator, aspect, locale );
      } else {
         generateYaml( generator, aspect, locale );
      }
   }

   private void generateYaml( final AspectModelOpenApiGenerator generator, final Aspect aspect, final Locale locale ) {
      try {
         final String yamlSpec = generator.applyForYaml( aspect, useSemanticApiVersion, aspectApiBaseUrl, Optional.ofNullable( aspectResourcePath ),
               getFileAsString( aspectParameterFile ), includeQueryApi, getPaging(), locale );
         final OutputStream out = getStreamForFile( outputFilePath );
         out.write( yamlSpec.getBytes() );
         out.close();
      } catch ( final IOException exception ) {
         throw new CommandException( "Could not generate OpenAPI yaml specification.", exception );
      }
   }

   private void generateJson( final AspectModelOpenApiGenerator generator, final Aspect aspect, final Locale locale ) {
      JsonNode result = JsonNodeFactory.instance.objectNode();
      final Optional<String> res = getFileAsString( aspectParameterFile );
      final ObjectMapper objectMapper = new ObjectMapper();
      if ( res.isPresent() ) {
         try {
            result = objectMapper.readTree( res.get() );
         } catch ( final JsonProcessingException e ) {
            throw new CommandException( "Could not parse the file to JSON.", e );
         }
      }
      final JsonNode jsonSpec = generator.applyForJson( aspect, useSemanticApiVersion, aspectApiBaseUrl, Optional.ofNullable( aspectResourcePath ),
            Optional.of( result ), includeQueryApi, getPaging(), locale );

      final OutputStream out = getStreamForFile( outputFilePath );
      try {
         objectMapper.writerWithDefaultPrettyPrinter().writeValue( out, jsonSpec );
         out.close();
      } catch ( final IOException exception ) {
         throw new CommandException( "Could not format OpenApi Json.", exception );
      }
   }

   private Optional<String> getFileAsString( final String filePath ) {
      if ( filePath == null || filePath.isEmpty() ) {
         return Optional.empty();
      }
      final File f = new File( filePath );
      if ( f.exists() && !f.isDirectory() ) {
         try {
            final InputStream inputStream = new FileInputStream( filePath );
            return Optional.of( IOUtils.toString( inputStream, StandardCharsets.UTF_8.name() ) );
         } catch ( final IOException e ) {
            throw new CommandException( String.format( "Could not load file %s.", filePath ), e );
         }
      }
      throw new CommandException( String.format( "File does not exist %s.", filePath ) );
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
