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

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;

public class ApplicationIntegrationTest extends MetaModelVersions {

   private static final SecurityManager SECURITY_MANAGER = System.getSecurityManager();

   @TempDir
   Path outputDirectory;

   private final BammCli bammCli = new BammCli();

   @BeforeAll
   public static void startup() {
      System.setSecurityManager( new TestSecurityManager() );
   }

   @AfterAll
   public static void shutdown() {
      System.setSecurityManager( SECURITY_MANAGER );
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
   }

   @Test
   public void testNoArgs() throws Exception {
      assertThat( catchSystemExit( bammCli::run ) ).isEqualTo( 1 );
   }

   @Test
   public void testWrongArgs() throws Exception {
      assertThat( catchSystemExit( () -> bammCli.run( "-i", "notexists" ) ) ).isEqualTo( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHelp( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, outputDir, "-h" );
   }

   @Test
   public void testDocFilePathWithoutGenerateHtml() throws Exception {
      assertThat( catchSystemExit( () -> bammCli.run( "-dp", "/test" ) ) ).isEqualTo( 1 );
   }

   private void validateFile( final File directory, final String fileName ) {
      final File file = directory.toPath().resolve( fileName ).toFile();
      assertThat( file ).exists();
      assertThat( file.length() ).isGreaterThan( 0 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSvg( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, outputDir, "-svg" );

      validateFile( outputDir, "AspectWithEntity_en.svg" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testPng( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, outputDir, "-png" );

      validateFile( outputDir, "AspectWithEntity_en.png" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testDot( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, outputDir, "-dot" );

      validateFile( outputDir, "AspectWithEntity_en.dot" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testJson( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, outputDir, "-json" );
      validateFile( outputDir, "AspectWithEntity.json" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testJsonSchema( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, outputDir, "-schema" );

      validateFile( outputDir, "AspectWithEntity.schema.json" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateAspectModelJavaClassWithDefaultPackageName( final KnownVersion metaModelVersion )
         throws Throwable {
      final File outputDir = outputDirectory.toFile();

      createValidArgsExecution( metaModelVersion, outputDir, "-java" );
      final File directory = Paths.get( outputDir.getAbsolutePath(), "io", "openmanufacturing", "test" )
            .toFile();
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
      createValidArgsExecution( metaModelVersion, outputDir, "-static-java" );

      final File directory = Paths.get( outputDir.getAbsolutePath(), "io", "openmanufacturing", "test" )
            .toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectory();

      validateFile( directory, "MetaAspectWithEntity.java" );
      validateFile( directory, "MetaTestEntity.java" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testValidation( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, outputDir, "-v" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testPrettyPrinting( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, outputDir, "-p" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testVersionMigration( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, outputDir, "-m" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateJavaClassWithCustomPackageName( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();

      createValidArgsExecution( metaModelVersion, outputDir, "-java", "-pn", "io.openmanufacturing.sds" );

      final File directory = Paths.get( outputDir.getAbsolutePath(), "io", "openmanufacturing", "sds" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectory();

      validateFile( directory, "AspectWithEntity.java" );
      validateFile( directory, "TestEntity.java" );
   }

   @Test
   public void testCustomPackageNameWithoutGenerateJavaArgument() throws Exception {
      assertThat( catchSystemExit( () -> bammCli.run( "-pn", "io.openmanufacturing.sds" ) ) ).isEqualTo( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateJavaClassWithJacksonAnnotations( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();

      createValidArgsExecution( metaModelVersion, outputDir, "-java", "-pn", "io.openmanufacturing" );
      final byte[] testAspectRaw = Files
            .readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/AspectWithEntity.java" ) );
      final String testAspect = new String( testAspectRaw );
      assertThat( testAspect ).contains( "JsonProperty" );

      final byte[] testEntityRaw = Files
            .readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/TestEntity.java" ) );
      final String testEntity = new String( testEntityRaw );
      assertThat( testEntity ).contains( "@JsonProperty" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateJavaClassWithoutJacksonAnnotations( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();

      createValidArgsExecution( metaModelVersion, outputDir, "-java", "-dja", "-pn", "io.openmanufacturing" );
      final byte[] testAspectRaw = Files
            .readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/AspectWithEntity.java" ) );
      final String testAspect = new String( testAspectRaw );
      assertThat( testAspect ).doesNotContain( "@com.fasterxml.jackson.annotation.JsonProperty" );

      final byte[] testEntityRaw = Files
            .readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/TestEntity.java" ) );
      final String testEntity = new String( testEntityRaw );
      assertThat( testEntity ).doesNotContain( "@com.fasterxml.jackson.annotation.JsonProperty" );
   }

   @Test
   public void testDisableJacksonAnnotationsWithoutGenerateJavaArgument() throws Exception {
      assertThat( catchSystemExit( () -> bammCli.run( "-dja" ) ) ).isEqualTo( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateOpenApiSpecWithoutBaseUrl( final KnownVersion metaModelVersion ) throws Exception {
      assertThat( catchSystemExit( () -> {
         final File outputDir = outputDirectory.toFile();
         final List<String> argumentsList = createArgumentList( metaModelVersion, outputDir, "-oapi-json" );
         bammCli.run( argumentsList.toArray( new String[0] ) );
      } ) ).isEqualTo( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateOpenApiSpecWithoutResourcePath( final KnownVersion metaModelVersion ) throws Exception {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, outputDir, "-oapi-json", "-base-url",
            "https://test.example.com" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateOpenApiSpecWithResourcePath( final KnownVersion metaModelVersion ) throws Exception {
      final File outputDir = outputDirectory.toFile();
      createValidArgsExecution( metaModelVersion, outputDir, "-oapi-json", "-base-url", "https://test.example.com",
            "-arp", "my-aspect" );
   }

   private void createValidArgsExecution( final KnownVersion testedVersion, final File targetFolder,
         final String... args ) throws Exception {
      assertThat( catchSystemExit( () -> {
         final List<String> argumentsList = createArgumentList( testedVersion, targetFolder, args );
         bammCli.run( argumentsList.toArray( new String[0] ) );
      } ) ).isEqualTo( 0 );
   }

   private List<String> createArgumentList( final KnownVersion testedVersion, final File targetFolder,
         final String... args ) {
      final String aspectName = TestAspect.ASPECT_WITH_ENTITY.getName();
      final String resourcePath = String
            .format( "valid/%s/io.openmanufacturing.test/1.0.0/%s.ttl", testedVersion.toString().toLowerCase(),
                  aspectName );
      final URL resource = TestAspect.class.getClassLoader().getResource( resourcePath );
      assertThat( resource.getProtocol() ).isEqualTo( "file" );
      final String aspectModelPath = resource.getFile();

      final List<String> argumentsList;
      argumentsList = new ArrayList<>( 20 );
      argumentsList.add( "-d" );
      argumentsList.add( targetFolder.toString() );
      argumentsList.add( "-i" );
      argumentsList.add( aspectModelPath );
      argumentsList.addAll( Arrays.asList( args ) );
      return argumentsList;
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateJavaClassWithoutCopyright( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();

      createValidArgsExecution( metaModelVersion, outputDir, "-java" );
      final byte[] testAspectRaw = Files
            .readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/AspectWithEntity.java" ) );
      final String testAspect = new String( testAspectRaw );
      assertThat( testAspect ).doesNotContain( "Copyright" );

      final byte[] testEntityRaw = Files
            .readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/TestEntity.java" ) );
      final String testEntity = new String( testEntityRaw );
      assertThat( testEntity ).doesNotContain( "Copyright" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateJavaClassWithCopyright( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();
      final String currentWorkingDirectory = System.getProperty( "user.dir" );
      final String templateLibPath = currentWorkingDirectory + "/../../core/sds-aspect-model-java-generator/templates";
      final int currentYear = LocalDate.now().getYear();
      final String expectedCopyright = String.format( "Copyright (c) %s OMP Test Inc. All rights reserved", currentYear );

      createValidArgsExecution( metaModelVersion, outputDir, "-java", "-elm", "-tlp", templateLibPath, "-tlfn", "test-macro-lib.vm" );
      final byte[] testAspectRaw = Files
            .readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/AspectWithEntity.java" ) );
      final String testAspect = new String( testAspectRaw );
      assertThat( testAspect ).contains( expectedCopyright );

      final byte[] testEntityRaw = Files
            .readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/TestEntity.java" ) );
      final String testEntity = new String( testEntityRaw );
      assertThat( testEntity ).contains( expectedCopyright );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticJavaClassWithoutCopyright( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();

      createValidArgsExecution( metaModelVersion, outputDir, "-static-java" );
      final byte[] metaTestAspectRaw = Files
            .readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/MetaAspectWithEntity.java" ) );
      final String metaTestAspect = new String( metaTestAspectRaw );
      assertThat( metaTestAspect ).doesNotContain( "Copyright" );

      final byte[] metaTestEntityRaw = Files
            .readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/MetaTestEntity.java" ) );
      final String metaTestEntity = new String( metaTestEntityRaw );
      assertThat( metaTestEntity ).doesNotContain( "Copyright" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticJavaClassWithCopyright( final KnownVersion metaModelVersion ) throws Throwable {
      final File outputDir = outputDirectory.toFile();
      final String currentWorkingDirectory = System.getProperty( "user.dir" );
      final String templateLibPath = currentWorkingDirectory + "/../../core/sds-aspect-model-java-generator/templates";
      final int currentYear = LocalDate.now().getYear();
      final String expectedCopyright = String.format( "Copyright (c) %s OMP Test Inc. All rights reserved", currentYear );

      createValidArgsExecution( metaModelVersion, outputDir, "-static-java", "-elm", "-tlp", templateLibPath, "-tlfn", "test-macro-lib.vm" );
      final byte[] metaTestAspectRaw = Files
            .readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/MetaAspectWithEntity.java" ) );
      final String metaTestAspect = new String( metaTestAspectRaw );
      assertThat( metaTestAspect ).contains( expectedCopyright );

      final byte[] metaTestEntityRaw = Files
            .readAllBytes( outputDir.toPath().resolve( "io/openmanufacturing/test/MetaTestEntity.java" ) );
      final String metaTestEntity = new String( metaTestEntityRaw );
      assertThat( metaTestEntity ).contains( expectedCopyright );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testValidateVelocityTemplateMacroArgumentValidation( final KnownVersion metaModelVersion ) throws Exception {
      assertThat( catchSystemExit( () -> {
         final File outputDir = outputDirectory.toFile();
         final List<String> argumentsList = createArgumentList( metaModelVersion, outputDir, "-java", "-elm" );
         bammCli.run( argumentsList.toArray( new String[0] ) );
      } ) ).isEqualTo( 1 );

      assertThat( catchSystemExit( () -> {
         final File outputDir = outputDirectory.toFile();
         final List<String> argumentsList = createArgumentList( metaModelVersion, outputDir, "-static-java", "-elm" );
         bammCli.run( argumentsList.toArray( new String[0] ) );
      } ) ).isEqualTo( 1 );
   }
}