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

package io.openmanufacturing.sds.aspectmodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.openmanufacturing.sds.aspectmodel.generator.openapi.AspectModelOpenApiGenerator;
import io.openmanufacturing.sds.aspectmodel.generator.openapi.PagingOption;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;

@Mojo( name = "generateOpenApiSpec", defaultPhase =  LifecyclePhase.GENERATE_RESOURCES )
public class GenerateOpenApiSpec extends AspectModelMojo {

   private final Logger logger = LoggerFactory.getLogger( GenerateOpenApiSpec.class );

   private final AspectModelOpenApiGenerator generator = new AspectModelOpenApiGenerator();
   private final ObjectMapper objectMapper = new ObjectMapper();

   @Parameter( required = true )
   private String aspectApiBaseUrl = "";

   @Parameter
   private String aspectParameterFile;

   @Parameter( defaultValue = "false" )
   private boolean useSemanticApiVersion;

   @Parameter
   private String aspectResourcePath;

   @Parameter( defaultValue = "false" )
   private boolean includeQueryApi;

   @Parameter( defaultValue = "false" )
   private boolean excludePaging;

   @Parameter( defaultValue = "false" )
   private boolean aspectCursorBasedPaging;

   @Parameter( defaultValue = "false" )
   private boolean aspectOffsetBasedPaging;

   @Parameter( defaultValue = "false" )
   private boolean aspectTimeBasedPaging;

   @Parameter( required = true )
   private String outputFormat = "";

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      try {
         final OpenApiFormat format = OpenApiFormat.valueOf( outputFormat.toUpperCase() );
         if ( format.equals( OpenApiFormat.JSON ) ) {
            generateOpenApiSpecJson();
            return;
         }
         generateOpenApiSpecYaml();
         logger.info( "Successfully generated OpenAPI specification for Aspect model." );
      } catch ( final IllegalArgumentException exception ) {
         throw new MojoExecutionException( "Invalid output format.", exception );
      }
   }

   private void generateOpenApiSpecJson() throws MojoExecutionException {
      final Aspect aspect = AspectModelLoader.fromVersionedModelUnchecked( loadModelOrFail( aspectModelFilePath ) );
      JsonNode result = JsonNodeFactory.instance.objectNode();
      final Optional<String> aspectParameterDefinitionFile = getFileAsString( aspectParameterFile );
      if ( aspectParameterDefinitionFile.isPresent() ) {
         try {
            result = objectMapper.readTree( aspectParameterDefinitionFile.get() );
         } catch ( final JsonProcessingException e ) {
            throw new MojoExecutionException( "Could not parse the file to JSON.", e );
         }
      }
      final JsonNode jsonSpec = generator.applyForJson( aspect, useSemanticApiVersion, aspectApiBaseUrl, Optional.ofNullable( aspectResourcePath ),
            Optional.of( result ), includeQueryApi, getPagingFromArgs() );

      final OutputStream out = getStreamForFile( aspect.getName() + ".oai.json", outputDirectory );
      try {
         objectMapper.writerWithDefaultPrettyPrinter().writeValue( out, jsonSpec );
         out.flush();
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not format OpenAPI JSON.", exception );
      }
   }

   private void generateOpenApiSpecYaml() throws MojoExecutionException {
      final Aspect aspect = AspectModelLoader.fromVersionedModelUnchecked( loadModelOrFail( aspectModelFilePath ) );
      try {
         final String yamlSpec = generator.applyForYaml( aspect, useSemanticApiVersion, aspectApiBaseUrl, Optional.ofNullable( aspectResourcePath ),
               getFileAsString( aspectParameterFile ), includeQueryApi, getPagingFromArgs() );
         final OutputStream out = getStreamForFile( aspect.getName() + ".oai.yaml", outputDirectory );
         out.write( yamlSpec.getBytes() );
         out.flush();
         out.close();
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not generate OpenAPI YAML specification.", exception );
      }
   }

   private static Optional<String> getFileAsString( final String filePath ) throws MojoExecutionException {
      if ( filePath == null || filePath.isEmpty() ) {
         return Optional.empty();
      }
      final File f = new File( filePath );
      if ( f.exists() && !f.isDirectory() ) {
         try {
            final InputStream inputStream = new FileInputStream( filePath );
            return Optional.of( IOUtils.toString( inputStream, StandardCharsets.UTF_8.name() ) );
         } catch ( final IOException e ) {
            final String errorMessage = String.format( "Could not load file %s.", filePath );
            throw new MojoExecutionException( errorMessage, e );
         }
      }
      final String errorMessage = String.format( "File does not exist %s.", filePath );
      throw new MojoExecutionException( errorMessage );
   }

   private Optional<PagingOption> getPagingFromArgs() {
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
