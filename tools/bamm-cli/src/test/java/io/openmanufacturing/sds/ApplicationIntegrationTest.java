/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.java.exception.CodeGenerationException;
import io.openmanufacturing.sds.exception.CommandException;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import picocli.CommandLine;

public class ApplicationIntegrationTest extends MetaModelVersions {

   private static class ExceptionHandler implements CommandLine.IExecutionExceptionHandler {

      private Exception thrownException;

      @Override
      public int handleExecutionException( final Exception ex, final CommandLine commandLine, final CommandLine.ParseResult parseResult ) {
         thrownException = ex;
         return 2;
      }

      public Exception getException() {
         return thrownException;
      }
   }

   private static final SecurityManager SECURITY_MANAGER = System.getSecurityManager();

   // JAnsi replaces the System.out with its own stream and caches the result, so make sure the debug streams
   // remain the same throughout the execution of all tests so as to not conflict with this JAnsi setup.
   static private final ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
   static private final PrintStream testOut = new PrintStream( stdoutBuffer );
   static private final PrintStream originalStdout = System.out;

   @TempDir
   Path outputDirectory;

   private final BammCli bammCli = new BammCli();

   @BeforeAll
   public static void startup() {
      System.setOut( testOut );
      System.setSecurityManager( new TestSecurityManager() );
   }

   @AfterAll
   public static void shutdown() {
      System.setSecurityManager( SECURITY_MANAGER );
      System.setOut( originalStdout );
   }

   @BeforeEach
   public void beforeEach() throws IOException {
      Files.walk( outputDirectory )
            .sorted( Comparator.reverseOrder() )
            .map( Path::toFile )
            .forEach( file -> {
               if ( !file.delete() ) {
                  throw new RuntimeException();
               }
            } );
      if ( !outputDirectory.toFile().mkdirs() ) {
         throw new RuntimeException();
      }
      stdoutBuffer.reset();
   }

   @Test
   public void testNoArgs() {
      assertThat( bammCli.run( new String[0] ) ).isEqualTo( 0 );
   }

   @Test
   public void testWrongArgs() {
      assertThat( bammCli.run( "-i", "notexists" ) ).isEqualTo( 2 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHelp( final KnownVersion metaModelVersion ) {
      assertThat( bammCli.run( "help" ) ).isEqualTo( 0 );
   }

   private void validateFile( final File directory, final String fileName ) {
      final File file = directory.toPath().resolve( fileName ).toFile();
      assertThat( file ).exists();
      assertThat( file.length() ).isGreaterThan( 0 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSvgGenToFileWithDefLanguage( final KnownVersion metaModelVersion ) {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, "to", "svg", "-o", getPathForArtifact( "AspectWithEntity.svg" ) );
      validateFile( outputDir, "AspectWithEntity.svg" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSvgGenToFileWithExplicitLanguage( final KnownVersion metaModelVersion ) {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, "to", "svg", "-o", getPathForArtifact( "AspectWithEntity.svg" ), "-l", "en" );
      validateFile( outputDir, "AspectWithEntity.svg" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSvgGenToFileFailForNonexistentLanguage( final KnownVersion metaModelVersion ) {
      assertThatThrownBy( () -> executeExpectException( metaModelVersion, "to", "svg", "-o", getPathForArtifact( "AspectWithEntity.svg" ), "-l", "de" ) )
            .isInstanceOf( CommandException.class )
            .hasMessageContaining( "The model does not contain the desired language" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSvgGenToStdout( final KnownVersion metaModelVersion ) {
      createValidArgsExecution( metaModelVersion, "to", "svg" );
      assertThat( stdoutBuffer.toString( StandardCharsets.UTF_8 ) ).startsWith( "<svg" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testPngGenToFileWithDefLanguage( final KnownVersion metaModelVersion ) {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, "to", "png", "-o", getPathForArtifact( "AspectWithEntity.png" ) );
      validateFile( outputDir, "AspectWithEntity.png" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testPngGenToFileWithExplicitLanguage( final KnownVersion metaModelVersion ) {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, "to", "png", "-o", getPathForArtifact( "AspectWithEntity.png" ), "-l", "en" );
      validateFile( outputDir, "AspectWithEntity.png" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testPngGenToFileFailForNonexistentLanguage( final KnownVersion metaModelVersion ) {
      assertThatThrownBy( () -> executeExpectException( metaModelVersion, "to", "png", "-o", getPathForArtifact( "AspectWithEntity.png" ), "-l", "de" ) )
            .isInstanceOf( CommandException.class )
            .hasMessageContaining( "The model does not contain the desired language" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testDotGenToFileWithDefLanguage( final KnownVersion metaModelVersion ) {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, "to", "dot", "-o", getPathForArtifact( "AspectWithEntity.dot" ) );
      validateFile( outputDir, "AspectWithEntity.dot" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testDotGenToFileWithExplicitLanguage( final KnownVersion metaModelVersion ) {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, "to", "dot", "-o", getPathForArtifact( "AspectWithEntity.dot" ), "-l", "en" );
      validateFile( outputDir, "AspectWithEntity.dot" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testDotGenToFileFailForNonexistentLanguage( final KnownVersion metaModelVersion ) {
      assertThatThrownBy( () -> executeExpectException( metaModelVersion, "to", "dot", "-o", getPathForArtifact( "AspectWithEntity.dot" ), "-l", "de" ) )
            .isInstanceOf( CommandException.class )
            .hasMessageContaining( "The model does not contain the desired language" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testDotGenToStdout( final KnownVersion metaModelVersion ) {
      createValidArgsExecution( metaModelVersion, "to", "dot" );
      assertThat( stdoutBuffer.toString( StandardCharsets.UTF_8 ) ).startsWith( "digraph AspectModel" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testJsonToFile( final KnownVersion metaModelVersion ) {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, "to", "json", "-o", getPathForArtifact( "AspectWithEntity.json" ) );
      validateFile( outputDir, "AspectWithEntity.json" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testJsonToStdout( final KnownVersion metaModelVersion ) {
      createValidArgsExecution( metaModelVersion, "to", "json" );
      assertThat( stdoutBuffer.toString( StandardCharsets.UTF_8 ) ).startsWith( "{" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testJsonSchemaToFile( final KnownVersion metaModelVersion ) {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, "to", "schema", "-o", getPathForArtifact( "AspectWithEntity.schema.json" ) );
      validateFile( outputDir, "AspectWithEntity.schema.json" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testJsonSchemaToStdout( final KnownVersion metaModelVersion ) {
      createValidArgsExecution( metaModelVersion, "to", "schema" );
      assertThat( stdoutBuffer.toString( StandardCharsets.UTF_8 ) ).startsWith( "{" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateAspectModelJavaClassWithDefaultPackageName( final KnownVersion metaModelVersion ) {
      final File outputDir = outputDirectory.toFile();

      createValidArgsExecution( metaModelVersion, "to", "java", "-d", outputDirectory.toString() );
      final File directory = Paths.get( outputDir.getAbsolutePath(), "io", "openmanufacturing", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectory();

      validateFile( directory, "AspectWithEntity.java" );
      validateFile( directory, "TestEntity.java" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelJavaClassWithDefaultPackageName( final KnownVersion metaModelVersion )
         throws Throwable {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, "to", "java", "-d", outputDirectory.toString(), "-s" );

      final File directory = Paths.get( outputDir.getAbsolutePath(), "io", "openmanufacturing", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectory();

      validateFile( directory, "MetaAspectWithEntity.java" );
      validateFile( directory, "MetaTestEntity.java" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testValidation( final KnownVersion metaModelVersion ) {
      createValidArgsExecution( metaModelVersion, "validate" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testPrettyPrintingToFile( final KnownVersion metaModelVersion ) {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, "prettyprint", "-o", getPathForArtifact( "PrettyPrinted.ttl" ) );
      validateFile( outputDir, "PrettyPrinted.ttl" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testPrettyPrintingToStdout( final KnownVersion metaModelVersion ) {
      createValidArgsExecution( metaModelVersion, "prettyprint" );
      assertThat( stdoutBuffer.toString( StandardCharsets.UTF_8 ) ).contains( "@prefix" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testVersionMigrationToFile( final KnownVersion metaModelVersion ) {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, "migrate", "-o", getPathForArtifact( "Migrated.ttl" ) );
      validateFile( outputDir, "Migrated.ttl" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testVersionMigrationToStdout( final KnownVersion metaModelVersion ) {
      createValidArgsExecution( metaModelVersion, "migrate" );
      assertThat( stdoutBuffer.toString( StandardCharsets.UTF_8 ) ).contains( "@prefix" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateJavaClassWithCustomPackageName( final KnownVersion metaModelVersion ) {
      final File outputDir = outputDirectory.toFile();

      createValidArgsExecution( metaModelVersion, "to", "java", "-d", outputDirectory.toString(), "-pn", "io.openmanufacturing.sds" );

      final File directory = Paths.get( outputDir.getAbsolutePath(), "io", "openmanufacturing", "sds" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectory();

      validateFile( directory, "AspectWithEntity.java" );
      validateFile( directory, "TestEntity.java" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateJavaClassWithJacksonAnnotations( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();

      createValidArgsExecution( metaModelVersion, "to", "java", "-d", outputDirectory.toString(), "-pn", "io.openmanufacturing" );
      final byte[] testAspectRaw = Files.readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/AspectWithEntity.java" ) );
      final String testAspect = new String( testAspectRaw );
      assertThat( testAspect ).contains( "JsonProperty" );

      final byte[] testEntityRaw = Files.readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/TestEntity.java" ) );
      final String testEntity = new String( testEntityRaw );
      assertThat( testEntity ).contains( "@JsonProperty" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateJavaClassWithoutJacksonAnnotations( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();

      createValidArgsExecution( metaModelVersion, "to", "java", "-d", outputDirectory.toString(), "-nj", "-pn", "io.openmanufacturing" );
      final byte[] testAspectRaw = Files.readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/AspectWithEntity.java" ) );
      final String testAspect = new String( testAspectRaw );
      assertThat( testAspect ).doesNotContain( "@com.fasterxml.jackson.annotation.JsonProperty" );

      final byte[] testEntityRaw = Files.readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/TestEntity.java" ) );
      final String testEntity = new String( testEntityRaw );
      assertThat( testEntity ).doesNotContain( "@com.fasterxml.jackson.annotation.JsonProperty" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateOpenApiSpecWithoutBaseUrl( final KnownVersion metaModelVersion ) {
      final List<String> argumentsList = createArgumentList( metaModelVersion, "to", "openapi", "-j" );
      assertThat( bammCli.run( argumentsList.toArray( new String[0] ) ) ).isEqualTo( 2 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateOpenApiSpecWithoutResourcePath( final KnownVersion metaModelVersion ) {
      createValidArgsExecution( metaModelVersion, "to", "openapi", "-j", "-b", "https://test.example.com" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateOpenApiSpecWithResourcePath( final KnownVersion metaModelVersion ) {
      createValidArgsExecution( metaModelVersion, "to", "openapi", "-j", "-b", "https://test.example.com", "-r", "my-aspect" );
   }

   private void createValidArgsExecution( final KnownVersion testedVersion, final String... args ) {
      assertThat( executeCommand( testedVersion, args ) ).isEqualTo( 0 );
   }

   private int executeCommand( final KnownVersion testedVersion, final String... args ) {
      final List<String> argumentsList = createArgumentList( testedVersion, args );
      return bammCli.run( argumentsList.toArray( new String[0] ) );
   }

   private void executeExpectException( final KnownVersion testedVersion, final String... args ) throws Exception {
      final ExceptionHandler eh = new ExceptionHandler();
      final List<String> argumentsList = createArgumentList( testedVersion, args );
      bammCli.runWithExceptionHandler( eh, argumentsList.toArray( new String[0] ) );
      throw eh.getException();
   }

   private List<String> createArgumentList( final KnownVersion testedVersion, final String... args ) {
      final String aspectName = TestAspect.ASPECT_WITH_ENTITY.getName();
      final String resourcePath = String.format( "valid/%s/io.openmanufacturing.test/1.0.0/%s.ttl", testedVersion.toString().toLowerCase(), aspectName );
      final URL resource = TestAspect.class.getClassLoader().getResource( resourcePath );
      assertThat( resource.getProtocol() ).isEqualTo( "file" );
      final String aspectModelPath = resource.getFile();

      final List<String> argumentsList = new ArrayList<>( 20 );
      argumentsList.add( "aspect" );
      argumentsList.add( aspectModelPath );
      argumentsList.addAll( Arrays.asList( args ) );
      return argumentsList;
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateJavaClassWithoutFileHeader( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();

      createValidArgsExecution( metaModelVersion, "to", "java", "-d", outputDirectory.toString() );
      final byte[] testAspectRaw = Files.readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/AspectWithEntity.java" ) );
      final String testAspect = new String( testAspectRaw );
      assertThat( testAspect ).doesNotContain( "Copyright" );

      final byte[] testEntityRaw = Files.readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/TestEntity.java" ) );
      final String testEntity = new String( testEntityRaw );
      assertThat( testEntity ).doesNotContain( "Copyright" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateJavaClassWithFileHeader( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();
      final String currentWorkingDirectory = System.getProperty( "user.dir" );
      final String templateLibFile = currentWorkingDirectory + "/../../core/sds-aspect-model-java-generator/templates/test-macro-lib.vm";
      final int currentYear = LocalDate.now().getYear();
      final String expectedCopyright = String.format( "Copyright (c) %s OMP Test Inc. All rights reserved", currentYear );

      createValidArgsExecution( metaModelVersion, "to", "java", "-d", outputDirectory.toString(), "-elm", "-tlf", templateLibFile );
      final byte[] testAspectRaw = Files.readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/AspectWithEntity.java" ) );
      final String testAspect = new String( testAspectRaw );
      assertThat( testAspect ).contains( expectedCopyright );

      final byte[] testEntityRaw = Files.readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/TestEntity.java" ) );
      final String testEntity = new String( testEntityRaw );
      assertThat( testEntity ).contains( expectedCopyright );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticJavaClassWithoutFileHeader( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();

      createValidArgsExecution( metaModelVersion, "to", "java", "-d", outputDirectory.toString(), "-s" );
      final byte[] metaTestAspectRaw = Files.readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/MetaAspectWithEntity.java" ) );
      final String metaTestAspect = new String( metaTestAspectRaw );
      assertThat( metaTestAspect ).doesNotContain( "Copyright" );

      final byte[] metaTestEntityRaw = Files.readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/MetaTestEntity.java" ) );
      final String metaTestEntity = new String( metaTestEntityRaw );
      assertThat( metaTestEntity ).doesNotContain( "Copyright" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticJavaClassWithFileHeader( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();
      final String currentWorkingDirectory = System.getProperty( "user.dir" );
      final String templateLibFile = currentWorkingDirectory + "/../../core/sds-aspect-model-java-generator/templates/test-macro-lib.vm";
      final int currentYear = LocalDate.now().getYear();
      final String expectedCopyright = String.format( "Copyright (c) %s OMP Test Inc. All rights reserved", currentYear );

      createValidArgsExecution( metaModelVersion, "to", "java", "-d", outputDirectory.toString(), "-s", "-elm", "-tlf", templateLibFile );
      final byte[] metaTestAspectRaw = Files.readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/MetaAspectWithEntity.java" ) );
      final String metaTestAspect = new String( metaTestAspectRaw );
      assertThat( metaTestAspect ).contains( expectedCopyright );

      final byte[] metaTestEntityRaw = Files.readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/MetaTestEntity.java" ) );
      final String metaTestEntity = new String( metaTestEntityRaw );
      assertThat( metaTestEntity ).contains( expectedCopyright );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testValidateVelocityTemplateMacroArgumentValidation( final KnownVersion metaModelVersion ) {
      assertThatThrownBy( () -> executeExpectException( metaModelVersion, "to", "java", "-elm" ) )
            .isInstanceOf( CodeGenerationException.class )
            .hasMessageContaining( "Missing configuration. Please provide path to velocity template library file." );

      assertThatThrownBy( () -> executeExpectException( metaModelVersion, "to", "java", "-s", "-elm" ) )
            .isInstanceOf( CodeGenerationException.class )
            .hasMessageContaining( "Missing configuration. Please provide path to velocity template library file." );
   }

   private String getPathForArtifact( final String artifactName ) {
      return outputDirectory.toFile().getAbsolutePath() + File.separator + artifactName;
   }
}