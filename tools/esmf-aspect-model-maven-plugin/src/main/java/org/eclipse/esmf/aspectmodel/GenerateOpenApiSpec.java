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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.generator.openapi.AspectModelOpenApiGenerator;
import org.eclipse.esmf.aspectmodel.generator.openapi.OpenApiSchemaArtifact;
import org.eclipse.esmf.aspectmodel.generator.openapi.OpenApiSchemaGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.openapi.OpenApiSchemaGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.generator.openapi.PagingOption;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.vavr.control.Try;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = "generateOpenApiSpec", defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateOpenApiSpec extends AspectModelMojo {
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
   private static final ObjectMapper YAML_MAPPER = new YAMLMapper().enable( YAMLGenerator.Feature.MINIMIZE_QUOTES );
   private static final Logger LOG = LoggerFactory.getLogger( GenerateOpenApiSpec.class );

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
   private boolean includeFullCrud;

   @Parameter( defaultValue = "false" )
   private boolean includePost;

   @Parameter( defaultValue = "false" )
   private boolean includePut;

   @Parameter( defaultValue = "false" )
   private boolean includePatch;

   @Parameter( defaultValue = "false" )
   private boolean excludePaging;

   @Parameter( defaultValue = "false" )
   private boolean aspectCursorBasedPaging;

   @Parameter( defaultValue = "false" )
   private boolean aspectOffsetBasedPaging;

   @Parameter( defaultValue = "false" )
   private boolean aspectTimeBasedPaging;

   @Parameter( defaultValue = "false" )
   private boolean separateFiles;

   @Parameter( required = true )
   private String outputFormat = "";

   @Parameter( defaultValue = "en" )
   private String language;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      validateParameters();

      final Set<AspectContext> aspectModels = loadModelsOrFail();
      final Locale locale = Optional.ofNullable( language ).map( Locale::forLanguageTag ).orElse( Locale.ENGLISH );
      final ApiFormat format = Try.of( () -> ApiFormat.valueOf( outputFormat.toUpperCase() ) )
            .getOrElseThrow( () -> new MojoExecutionException( "Invalid output format." ) );
      for ( final AspectContext context : aspectModels ) {
         final Aspect aspect = context.aspect();
         final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
               .useSemanticVersion( useSemanticApiVersion )
               .baseUrl( aspectApiBaseUrl )
               .resourcePath( aspectResourcePath )
               .properties( readAspectParameterFile() )
               .includeQueryApi( includeQueryApi )
               .includeCrud( includeFullCrud )
               .includePost( includePost )
               .includePut( includePut )
               .includePatch( includePatch )
               .pagingOption( getPagingFromArgs() )
               .locale( locale )
               .build();

         final OpenApiSchemaArtifact openApiSpec = generator.apply( aspect, config );
         try {
            if ( separateFiles ) {
               writeSchemaWithSeparateFiles( format, openApiSpec );
            } else {
               writeSchemaWithInOneFile( aspect.getName() + ".oai." + format.toString().toLowerCase(), format, openApiSpec );
            }
         } catch ( final IOException exception ) {
            throw new MojoExecutionException( "Could not generate OpenAPI specification.", exception );
         }
      }
      LOG.info( "Successfully generated OpenAPI specification for Aspect Models." );
   }

   private void writeSchemaWithInOneFile( final String schemaFileName, final ApiFormat format, final OpenApiSchemaArtifact openApiSpec )
         throws IOException {
      try ( final OutputStream out = getOutputStreamForFile( schemaFileName, outputDirectory ) ) {
         if ( format == ApiFormat.JSON ) {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue( out, openApiSpec.getContent() );
         } else {
            out.write( openApiSpec.getContentAsYaml().getBytes( StandardCharsets.UTF_8 ) );
         }
      }
   }

   private void writeSchemaWithSeparateFiles( final ApiFormat format, final OpenApiSchemaArtifact openApiSpec ) throws IOException {
      final Path root = Path.of( outputDirectory );
      if ( format == ApiFormat.JSON ) {
         for ( final Map.Entry<Path, JsonNode> entry : openApiSpec.getContentWithSeparateSchemasAsJson().entrySet() ) {
            try ( final OutputStream out = new FileOutputStream( root.resolve( entry.getKey() ).toFile() ) ) {
               OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue( out, entry.getValue() );
            }
         }
      } else {
         for ( final Map.Entry<Path, String> entry : openApiSpec.getContentWithSeparateSchemasAsYaml().entrySet() ) {
            try ( final OutputStream out = new FileOutputStream( root.resolve( entry.getKey() ).toFile() ) ) {
               out.write( entry.getValue().getBytes( StandardCharsets.UTF_8 ) );
            }
         }
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

   private ObjectNode readAspectParameterFile() throws MojoExecutionException {
      if ( aspectParameterFile == null || aspectParameterFile.isEmpty() ) {
         return null;
      }
      final String extension = FilenameUtils.getExtension( aspectParameterFile ).toUpperCase();
      final Try<String> fileData = Try.of( () -> getFileAsString( aspectParameterFile ) ).mapTry( Optional::get );
      return switch ( extension ) {
         case "YAML", "YML" -> (ObjectNode) fileData
               .mapTry( data -> YAML_MAPPER.readValue( data, Object.class ) )
               .mapTry( OBJECT_MAPPER::writeValueAsString )
               .mapTry( OBJECT_MAPPER::readTree )
               .get();
         case "JSON" -> (ObjectNode) fileData
               .mapTry( OBJECT_MAPPER::readTree )
               .get();
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

   private PagingOption getPagingFromArgs() {
      if ( excludePaging ) {
         return PagingOption.NO_PAGING;
      }
      if ( aspectCursorBasedPaging ) {
         return PagingOption.CURSOR_BASED_PAGING;
      }
      if ( aspectOffsetBasedPaging ) {
         return PagingOption.OFFSET_BASED_PAGING;
      }
      if ( aspectTimeBasedPaging ) {
         return PagingOption.TIME_BASED_PAGING;
      }
      return null;
   }
}
