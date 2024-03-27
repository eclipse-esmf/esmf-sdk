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

package org.eclipse.esmf.aspectmodel;

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.esmf.aspectmodel.generator.openapi.AspectModelOpenApiGenerator;
import org.eclipse.esmf.aspectmodel.generator.openapi.PagingOption;
import org.eclipse.esmf.metamodel.AspectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import io.vavr.control.Try;

@Mojo( name = "generateOpenApiSpec", defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateOpenApiSpec extends AspectModelMojo {

   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
   private static final ObjectMapper YAML_MAPPER = new YAMLMapper().enable( YAMLGenerator.Feature.MINIMIZE_QUOTES );

   private final Logger logger = LoggerFactory.getLogger( GenerateOpenApiSpec.class );

   private final AspectModelOpenApiGenerator generator = new AspectModelOpenApiGenerator();

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

   @Parameter( defaultValue = "en" )
   private String language;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      validateParameters();

      final Set<AspectContext> aspectModels = loadModelsOrFail();
      final Locale locale = Optional.ofNullable( language ).map( Locale::forLanguageTag ).orElse( Locale.ENGLISH );
      try {
         final boolean isJson = OpenApiFormat.JSON.equals( OpenApiFormat.valueOf( outputFormat.toUpperCase() ) );
         for ( final AspectContext context : aspectModels ) {
            final JsonNode spec = generator.applyForJson( context.aspect(), useSemanticApiVersion, aspectApiBaseUrl,
                  Optional.ofNullable( aspectResourcePath ),
                  readAspectParameterFile(), includeQueryApi, getPagingFromArgs(), locale );
            if ( isJson ) {
               saveJson( spec, context.aspect().getName() );
            } else {
               saveYaml( spec, context.aspect().getName() );
            }
         }
         logger.info( "Successfully generated OpenAPI specification for Aspect Models." );
      } catch ( final IllegalArgumentException exception ) {
         throw new MojoExecutionException( "Invalid output format.", exception );
      }
   }

   @Override
   protected void validateParameters() throws MojoExecutionException {
      if ( aspectApiBaseUrl.isEmpty() ) {
         throw new MojoExecutionException( "Missing configuration. Please provide the Aspect API base URL." );
      }
      if ( outputFormat.isEmpty() ) {
         throw new MojoExecutionException( "Missing configuration. Please provide an output format." );
      }
      super.validateParameters();
   }

   private void saveJson( JsonNode spec, String aspect ) throws MojoExecutionException {
      try ( final OutputStream out = getOutputStreamForFile( aspect + ".oai.json", outputDirectory ) ) {
         OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue( out, spec );
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not format OpenApi JSON specification for aspect " + aspect , exception );
      }
   }

   private void saveYaml( JsonNode spec, String aspect ) throws MojoExecutionException {
      try ( final OutputStream out = getOutputStreamForFile( aspect + ".oai.yaml", outputDirectory ) ) {
         YAML_MAPPER.writerWithDefaultPrettyPrinter().writeValue( out, spec );
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not format OpenApi YAML specification for aspect " + aspect , exception );
      }
   }

   private Optional<JsonNode> readAspectParameterFile() throws MojoExecutionException {
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
         default -> throw new MojoExecutionException( format( "File extension [%s] not supported.", extension ) );
      };
   }

   private static Optional<String> getFileAsString( final String filePath ) throws MojoExecutionException {
      if ( filePath == null || filePath.isEmpty() ) {
         return Optional.empty();
      }
      final File f = new File( filePath );
      if ( f.exists() && !f.isDirectory() ) {
         try ( final InputStream inputStream = new FileInputStream( filePath ) ) {
            return Optional.of( IOUtils.toString( inputStream, StandardCharsets.UTF_8 ) );
         } catch ( final IOException e ) {
            throw new MojoExecutionException( format( "Could not load file %s.", filePath ), e );
         }
      }
      throw new MojoExecutionException( format( "File does not exist %s.", filePath ) );
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
