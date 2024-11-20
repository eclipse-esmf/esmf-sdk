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

import java.io.FileOutputStream;
import java.io.IOException;
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

import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.control.Try;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = GenerateOpenApiSpec.MAVEN_GOAL, defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateOpenApiSpec extends AspectModelMojo {
   public static final String MAVEN_GOAL = "generateOpenApiSpec";
   private static final Logger LOG = LoggerFactory.getLogger( GenerateOpenApiSpec.class );

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

   @Parameter
   private String templateFilePath;

   @Override
   public void executeGeneration() throws MojoExecutionException, MojoFailureException {
      validateParameters();

      final Set<Aspect> aspects = loadAspects();
      final Locale locale = Optional.ofNullable( language ).map( Locale::forLanguageTag ).orElse( Locale.ENGLISH );
      final ApiFormat format = Try.of( () -> ApiFormat.valueOf( outputFormat.toUpperCase() ) )
            .getOrElseThrow( () -> new MojoExecutionException( "Invalid output format." ) );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( useSemanticApiVersion )
            .baseUrl( aspectApiBaseUrl )
            .resourcePath( aspectResourcePath )
            .properties( readFile( aspectParameterFile ) )
            .template( readFile( templateFilePath ) )
            .includeQueryApi( includeQueryApi )
            .includeCrud( includeFullCrud )
            .includePost( includePost )
            .includePut( includePut )
            .includePatch( includePatch )
            .pagingOption( getPagingFromArgs() )
            .locale( locale )
            .build();

      for ( final Aspect aspect : aspects ) {
         final OpenApiSchemaArtifact openApiSpec = new AspectModelOpenApiGenerator( aspect, config ).singleResult();
         if ( separateFiles ) {
            writeSchemaWithSeparateFiles( format, openApiSpec );
         } else {
            writeSchemaWithInOneFile( aspect.getName() + ".oai." + format.toString().toLowerCase(), format, openApiSpec );
         }
      }
      LOG.info( "Successfully generated OpenAPI specification for Aspect Models." );
   }

   private void writeSchemaWithInOneFile( final String schemaFileName, final ApiFormat format, final OpenApiSchemaArtifact openApiSpec )
         throws MojoExecutionException {
      try ( final OutputStream out = getOutputStreamForFile( schemaFileName, outputDirectory ) ) {
         if ( format == ApiFormat.JSON ) {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue( out, openApiSpec.getContent() );
         } else {
            out.write( openApiSpec.getContentAsYaml().getBytes( StandardCharsets.UTF_8 ) );
         }
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not write OpenAPI schema", exception );
      }
   }

   private void writeSchemaWithSeparateFiles( final ApiFormat format, final OpenApiSchemaArtifact openApiSpec )
         throws MojoExecutionException {
      final Path root = Path.of( outputDirectory );
      if ( format == ApiFormat.JSON ) {
         for ( final Map.Entry<Path, JsonNode> entry : openApiSpec.getContentWithSeparateSchemasAsJson().entrySet() ) {
            try ( final OutputStream out = new FileOutputStream( root.resolve( entry.getKey() ).toFile() ) ) {
               OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue( out, entry.getValue() );
            } catch ( final IOException exception ) {
               throw new MojoExecutionException( "Could not write OpenAPI schema", exception );
            }
         }
      } else {
         for ( final Map.Entry<Path, String> entry : openApiSpec.getContentWithSeparateSchemasAsYaml().entrySet() ) {
            try ( final OutputStream out = new FileOutputStream( root.resolve( entry.getKey() ).toFile() ) ) {
               out.write( entry.getValue().getBytes( StandardCharsets.UTF_8 ) );
            } catch ( final IOException exception ) {
               throw new MojoExecutionException( "Could not write OpenAPI schema", exception );
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
