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
package org.eclipse.esmf.aspectmodel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import org.eclipse.esmf.aspectmodel.generator.asyncapi.AspectModelAsyncApiGenerator;
import org.eclipse.esmf.aspectmodel.generator.asyncapi.AsyncApiGenerationFeature;
import org.eclipse.esmf.aspectmodel.generator.asyncapi.AsyncApiSchemaArtifact;
import org.eclipse.esmf.aspectmodel.generator.asyncapi.AsyncApiSchemaGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.asyncapi.AsyncApiSchemaGenerationConfigBuilder;
import org.eclipse.esmf.metamodel.Aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Try;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLFactory;
import tools.jackson.dataformat.yaml.YAMLMapper;
import tools.jackson.dataformat.yaml.YAMLWriteFeature;

@Mojo( name = GenerateAsyncApiSpec.MAVEN_GOAL,
   defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateAsyncApiSpec extends AspectModelMojo {
   public static final String MAVEN_GOAL = "generateAsyncApiSpec";
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
   private static final ObjectMapper YAML_MAPPER =
         new YAMLMapper( YAMLFactory.builder().enable( YAMLWriteFeature.MINIMIZE_QUOTES ).build() );
   private static final Logger LOG = LoggerFactory.getLogger( GenerateAsyncApiSpec.class );

   @Parameter( required = true )
   private String outputFormat = "";

   @Parameter( defaultValue = "false" )
   private boolean separateFiles;

   @Parameter
   private String applicationId;

   @Parameter
   private String channelAddress;

   @Parameter( defaultValue = "false" )
   private boolean useSemanticApiVersion;

   @Parameter( defaultValue = "en" )
   private String language;

   @Parameter
   private List<String> featureFlags;

   @Override
   public void executeGeneration() throws MojoExecutionException, MojoFailureException {
      final Set<Aspect> aspects = loadAspects();
      final Locale locale = Optional.ofNullable( language ).map( Locale::forLanguageTag ).orElse( Locale.ENGLISH );
      final ApiFormat format = Try.of( () -> ApiFormat.valueOf( outputFormat.toUpperCase() ) )
            .getOrElseThrow( () -> new MojoExecutionException( "Invalid output format." ) );
      final Set<AsyncApiGenerationFeature> features = parseFeatureFlags();
      for ( final Aspect aspect : aspects ) {
         final AsyncApiSchemaGenerationConfig config = AsyncApiSchemaGenerationConfigBuilder.builder()
               .useSemanticVersion( useSemanticApiVersion )
               .applicationId( applicationId )
               .channelAddress( channelAddress )
               .features( features )
               .locale( locale )
               .build();

         final AsyncApiSchemaArtifact asyncApiSpec = new AspectModelAsyncApiGenerator( aspect, config ).singleResult();
         try {
            if ( separateFiles ) {
               writeSchemaWithSeparateFiles( format, asyncApiSpec );
            } else {
               writeSchemaWithInOneFile( aspect.getName() + ".aai." + format.toString().toLowerCase(), format, asyncApiSpec );
            }
         } catch ( final IOException exception ) {
            throw new MojoExecutionException( "Could not generate AsyncAPI specification.", exception );
         }
      }
   }

   private void writeSchemaWithInOneFile( final String schemaFileName, final ApiFormat format, final AsyncApiSchemaArtifact asyncApiSpec )
         throws MojoExecutionException {
      try ( final OutputStream out = getOutputStreamForFile( schemaFileName, outputDirectory ) ) {
         if ( format == ApiFormat.JSON ) {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue( out, asyncApiSpec.getContent() );
         } else {
            out.write( jsonToYaml( asyncApiSpec.getContent() ).getBytes( StandardCharsets.UTF_8 ) );
         }
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not write AsyncAPI schema", exception );
      }
   }

   private void writeSchemaWithSeparateFiles( final ApiFormat format, final AsyncApiSchemaArtifact asyncApiSpec ) throws IOException {
      final Path root = Path.of( outputDirectory );
      if ( format == ApiFormat.JSON ) {
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

   private Set<AsyncApiGenerationFeature> parseFeatureFlags() throws MojoExecutionException {
      if ( featureFlags == null || featureFlags.isEmpty() ) {
         return Set.of();
      }
      try {
         return featureFlags.stream()
               .map( String::trim )
               .map( AsyncApiGenerationFeature::fromValue )
               .collect( Collectors.toCollection( () -> EnumSet.noneOf( AsyncApiGenerationFeature.class ) ) );
      } catch ( final IllegalArgumentException exception ) {
         throw new MojoExecutionException(
               "Invalid feature flag. Valid values: " + Arrays.toString( AsyncApiGenerationFeature.values() ), exception );
      }
   }

   private String jsonToYaml( final JsonNode json ) {
      try {
         return YAML_MAPPER.writeValueAsString( json );
      } catch ( final Exception exception ) {
         LOG.error( "JSON could not be converted to YAML", exception );
         return json.toString();
      }
   }
}
