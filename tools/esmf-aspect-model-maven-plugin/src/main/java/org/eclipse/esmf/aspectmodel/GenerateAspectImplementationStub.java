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

import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.generator.openapi.AspectModelOpenApiGenerator;
import org.eclipse.esmf.aspectmodel.generator.openapi.OpenApiSchemaArtifact;
import org.eclipse.esmf.aspectmodel.generator.openapi.OpenApiSchemaGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.openapi.OpenApiSchemaGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.generator.openapi.PagingOption;
import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.java.QualifiedName;
import org.eclipse.esmf.aspectmodel.java.metamodel.StaticMetaModelJavaGenerator;
import org.eclipse.esmf.aspectmodel.java.pojo.AspectModelJavaGenerator;
import org.eclipse.esmf.aspectmodel.visitor.AspectStreamTraversalVisitor;
import org.eclipse.esmf.functions.ThrowingFunction;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.ModelElement;

import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twdata.maven.mojoexecutor.MojoExecutor;

@Mojo( name = GenerateAspectImplementationStub.MAVEN_GOAL,
      defaultPhase = LifecyclePhase.GENERATE_SOURCES,
      requiresDependencyCollection = ResolutionScope.TEST
)
public class GenerateAspectImplementationStub extends CodeGenerationMojo {
   public static final String MAVEN_GOAL = "generateAspectImplementationStub";
   private static final Logger LOG = LoggerFactory.getLogger( GenerateAspectImplementationStub.class );

   @Parameter( defaultValue = "false" )
   private boolean disableJacksonAnnotations;

   @Parameter( defaultValue = "deduction" )
   protected String jsonTypeInfo;

   @Parameter
   private MavenProject mavenProject;

   @Component
   @SuppressWarnings( "deprecation" ) // @Component annotation is required for successful injection of dependency with no alternative
   private BuildPluginManager pluginManager;

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

   @Parameter( defaultValue = "en" )
   private String language;

   @Parameter
   private String openApiGeneratorVersion;

   @Parameter
   private String openApiGeneratorName;

   @Parameter
   private String openApiGeneratorSchemaMappings;

   @Parameter
   private Map<?, ?> openApiGeneratorConfigOptions;

   @Parameter
   private String templateFilePath;

   @Override
   public void executeGeneration() throws MojoExecutionException {
      final Set<Aspect> aspects = loadAspects();
      for ( final Aspect aspect : aspects ) {
         final File templateLibFile = Path.of( templateFile ).toFile();
         validateParameters( templateLibFile );

         // Generation order is important:
         // 1. OpenAPI spec, because it's required by OpenAPI generator
         // 2. Run OpenAPI generator
         // 3. ESMF Java classes generation, because it will overwrite certain (DTO) classes, which is necessary because
         //    some types (of fields and methods) differ from what OpenAPI generator creates
         // 4. ESMF Java static classes (which have references to the ESMF Java classes)
         final File schemaFile = getOutFile( aspect.getName() + ".oai.yaml",
               Path.of( outputDirectory ).resolve( "main" ).resolve( "resources" ).toFile().getAbsolutePath() );
         final OpenApiSchemaGenerationConfig openApiSchemaGenerationConfig = buildOpenApiGenerationConfig( aspect );
         generateOpenApi( aspect, openApiSchemaGenerationConfig, schemaFile );

         runOpenApiGenerator( schemaFile, buildOpenApiGeneratorGenerationConfig( aspect ) );

         final ThrowingFunction<QualifiedName, OutputStream, MojoExecutionException> javaNameMapper =
               javaFileNameMapper( Path.of( outputDirectory ).resolve( "main" ).resolve( "java" ).toFile().getAbsolutePath() );
         final JavaCodeGenerationConfig codeGenerationConfig = buildCodeGenerationConfig( aspect, templateLibFile );
         generateJavaClasses( aspect, codeGenerationConfig, javaNameMapper );
         generateStaticJavaClasses( aspect, codeGenerationConfig, javaNameMapper );
      }
   }

   private OpenApiGeneratorGenerationConfig buildOpenApiGeneratorGenerationConfig( final Aspect aspect ) {
      // For EntityInstances (which appear in Enumerations that have an Entity as their type),
      // create empty mappings for corresponding schemas, effectively skipping generation for these classes.
      // OpenAPI generator templates such as "spring" have a different approach for the generation of
      // "oneOf" lists of schemas (namely creating an interface) which is incompatible with the code generated by
      // ESMF. The problem is resolved by not generating these classes.
      final Map<String, String> implicitSchemaMappings = aspect.accept( new AspectStreamTraversalVisitor(), null )
            .filter( EntityInstance.class::isInstance )
            .map( ModelElement::getName )
            .collect( Collectors.toMap( Function.identity(), element -> "" ) );

      return OpenApiGeneratorGenerationConfigBuilder.builder()
            .openApiGeneratorVersion( openApiGeneratorVersion )
            .generatorName( openApiGeneratorName )
            .implicitSchemaMappings( implicitSchemaMappings )
            .build();
   }

   private JavaCodeGenerationConfig buildCodeGenerationConfig( final Aspect aspect, final File templateLibFile ) {
      return JavaCodeGenerationConfigBuilder.builder()
            .enableJacksonAnnotations( !disableJacksonAnnotations )
            .jsonTypeInfo( JavaCodeGenerationConfig.JsonTypeInfoType.valueOf(
                  Optional.ofNullable( jsonTypeInfo ).map( String::toUpperCase ).orElse( "DEDUCTION" ) ) )
            .packageName( determinePackageName( aspect ) )
            .executeLibraryMacros( executeLibraryMacros )
            .templateLibFile( templateLibFile )
            .namePrefix( namePrefix )
            .namePostfix( namePostfix )
            .build();
   }

   private OpenApiSchemaGenerationConfig buildOpenApiGenerationConfig( final Aspect aspect ) throws MojoExecutionException {
      final Locale locale = Optional.ofNullable( language ).map( Locale::forLanguageTag ).orElse( Locale.ENGLISH );

      PagingOption pagingOption = PagingOption.NO_PAGING;
      if ( aspectCursorBasedPaging ) {
         pagingOption = PagingOption.CURSOR_BASED_PAGING;
      }
      if ( aspectOffsetBasedPaging ) {
         pagingOption = PagingOption.OFFSET_BASED_PAGING;
      }
      if ( aspectTimeBasedPaging ) {
         pagingOption = PagingOption.TIME_BASED_PAGING;
      }
      return OpenApiSchemaGenerationConfigBuilder.builder()
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
            .pagingOption( pagingOption )
            .locale( locale )
            .build();
   }

   private void generateJavaClasses( final Aspect aspect, final JavaCodeGenerationConfig config,
         final ThrowingFunction<QualifiedName, OutputStream, MojoExecutionException> javaNameMapper ) {
      new AspectModelJavaGenerator( aspect, config ).generateThrowing( javaNameMapper );
      LOG.info( "Successfully generated Java classes for Aspect Models." );
   }

   private void generateStaticJavaClasses( final Aspect aspect, final JavaCodeGenerationConfig config,
         final ThrowingFunction<QualifiedName, OutputStream, MojoExecutionException> javaNameMapper ) {
      new StaticMetaModelJavaGenerator( aspect, config ).generateThrowing( javaNameMapper );
      LOG.info( "Successfully generated static Java classes for Aspect Models." );
   }

   private void generateOpenApi( final Aspect aspect, final OpenApiSchemaGenerationConfig config, final File outputFile )
         throws MojoExecutionException {
      final OpenApiSchemaArtifact openApiSpec = new AspectModelOpenApiGenerator( aspect, config ).singleResult();
      try ( final OutputStream out = new FileOutputStream( outputFile ) ) {
         out.write( openApiSpec.getContentAsYaml().getBytes( StandardCharsets.UTF_8 ) );
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not write schema file " + outputFile.getAbsolutePath(), exception );
      }
   }

   private void runOpenApiGenerator( final File schemaFile, final OpenApiGeneratorGenerationConfig config ) throws MojoExecutionException {
      // Build the <configOptions><foo>...</foo><bar>...</bar></configOptions> part
      final MojoExecutor.Element configOptions = Optional.ofNullable( openApiGeneratorConfigOptions )
            .or( () -> Optional.of( Map.of() ) )
            .map( map -> element( name( "configOptions" ),
                  map.entrySet().stream()
                        .map( entry -> element( name( entry.getKey().toString() ), entry.getValue().toString() ) )
                        .toArray( MojoExecutor.Element[]::new ) ) )
            .orElseThrow();

      // Build the <schemaMappings>a=b,c=d</schemaMappings> part
      final Map<String, String> schemaMappings = new HashMap<>( config.implicitSchemaMappings() );
      Optional.ofNullable( openApiGeneratorSchemaMappings )
            .stream()
            .map( entry -> entry.split( "," ) )
            .flatMap( Arrays::stream )
            .map( entry -> entry.split( "=" ) )
            .forEach( entry -> schemaMappings.put( entry[0], entry[1] ) );
      final String schemaMappingsString = schemaMappings.entrySet().stream()
            .map( entry -> entry.getKey() + "=" + entry.getValue() )
            .collect( Collectors.joining( "," ) );

      try {
         executeMojo(
               plugin( "org.openapitools", "openapi-generator-maven-plugin", config.openApiGeneratorVersion() ),
               goal( "generate" ),
               configuration(
                     element( name( "schemaMappings" ), schemaMappingsString ),
                     element( name( "output" ), outputDirectory ),
                     element( name( "inputSpec" ), schemaFile.getAbsolutePath() ),
                     element( name( "generatorName" ), config.generatorName() ),
                     configOptions
               ),
               executionEnvironment( mavenProject, mavenSession, pluginManager )
         );
      } catch ( final Throwable exception ) {
         throw new MojoExecutionException( "Could not run OpenAPI Generator", exception );
      }
      LOG.info( "Successfully generated code from OpenAPI specification." );
   }
}
