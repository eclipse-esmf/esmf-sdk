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

package io.openmanufacturing.sds;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import io.openmanufacturing.sds.ProcessLauncher.ExecutionResult;
import io.openmanufacturing.sds.aspect.AspectValidateCommand;
import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.InvalidSyntaxViolation;
import io.openmanufacturing.sds.test.InvalidTestAspect;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestModel;

/**
 * The tests for the CLI that are executed by Maven Surefire. They work using the {@link MainClassProcessLauncher}, i.e. directly call
 * the main function in {@link BammCli}.
 */
@ExtendWith( LogExtension.class )
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
public class BammCliTest extends MetaModelVersions {
   protected ProcessLauncher bammCli;
   private final String defaultInputFile = inputFile( TestAspect.ASPECT_WITH_ENTITY ).getAbsolutePath();

   @TempDir
   Path outputDirectory;

   @BeforeAll
   public void setup() {
      bammCli = new MainClassProcessLauncher( BammCli.class );
   }

   @BeforeEach
   public void beforeEach() throws IOException {
      // Recursively delete temporary directory
      try {
         Files.walk( outputDirectory )
               .sorted( Comparator.reverseOrder() )
               .map( Path::toFile )
               .forEach( file -> {
                  if ( !file.delete() ) {
                     throw new RuntimeException();
                  }
               } );
      } catch ( final IOException e ) {
         throw new RuntimeException( e );
      }
      // Create a new empty temporary directory
      if ( !outputDirectory.toFile().mkdirs() ) {
         throw new RuntimeException();
      }
   }

   @Test
   public void testNoArgs() {
      final ExecutionResult result = bammCli.runAndExpectSuccess();
      assertThat( result.stdout() ).contains( "Usage:" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectWithoutSubcommand() {
      final ExecutionResult result = bammCli.apply( "aspect" );
      assertThat( result.exitStatus() ).isEqualTo( 2 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "Missing required parameter" );
   }

   @Test
   public void testWrongArgs() {
      final ExecutionResult result = bammCli.apply( "-i", "doesnotexist" );
      assertThat( result.exitStatus() ).isEqualTo( 2 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "Unknown options" );
   }

   @Test
   public void testHelp() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "help" );
      assertThat( result.stdout() ).contains( "Usage:" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testVerboseOutput() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "validate", "-vvv" );
      assertThat( result.stdout() ).contains( "Input model is valid" );
      assertThat( result.stderr() ).contains( "DEBUG " + AspectValidateCommand.class.getName() );
   }

   @Test
   public void testAspectMigrateToFile() {
      final File targetFile = outputFile( "output.ttl" );
      final ExecutionResult result =
            bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "migrate", "-o", targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "@prefix" );
   }

   @Test
   public void testAspectMigrateToStdout() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "migrate" );
      assertThat( result.stdout() ).contains( "@prefix" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectPrettyPrintToFile() {
      final File targetFile = outputFile( "output.ttl" );
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "prettyprint", "-o", targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "@prefix" );
   }

   @Test
   public void testAspectPrettyPrintToStdout() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "prettyprint" );
      assertThat( result.stdout() ).contains( "@prefix" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectValidate() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "validate" );
      assertThat( result.stdout() ).contains( "Input model is valid" );
      assertThat( result.stderr() ).isEmpty();

      final File invalidModel = inputFile( InvalidTestAspect.INVALID_SYNTAX );
      final ExecutionResult result2 = bammCli.apply( "aspect", invalidModel.getAbsolutePath(), "validate" );
      assertThat( result2.exitStatus() ).isEqualTo( 1 );
      assertThat( result2.stderr() ).isEmpty();
      assertThat( result2.stdout() ).contains( "Syntax error" );
   }

   @Test
   public void testAspectValidateWithDetails() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "validate", "--details" );
      assertThat( result.stdout() ).contains( "Input model is valid" );
      assertThat( result.stderr() ).isEmpty();

      final File invalidModel = inputFile( InvalidTestAspect.INVALID_SYNTAX );
      final ExecutionResult result2 = bammCli.apply( "aspect", invalidModel.getAbsolutePath(), "validate", "--details" );
      assertThat( result2.exitStatus() ).isEqualTo( 1 );
      assertThat( result2.stderr() ).isEmpty();
      assertThat( result2.stdout() ).contains( InvalidSyntaxViolation.ERROR_CODE );
   }

   @Test
   public void testAspectValidateWithCustomResolver() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "validate", "--custom-resolver", resolverCommand() );
      assertThat( result.stdout() ).contains( "Input model is valid" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToAasXmlToFile() {
      final File targetFile = outputFile( "output.xml" );
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "aas", "--format", "xml", "-o",
            targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().startsWith( "<?xml" );
   }

   @Test
   public void testAspectToAasXmlToStdout() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "aas", "--format", "xml" );
      assertThat( result.stdout() ).startsWith( "<?xml" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToAasAasxToFile() throws TikaException, IOException {
      final File targetFile = outputFile( "output.aasx" );
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "aas", "--format", "aasx", "-o",
            targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( contentType( targetFile ) ).isEqualTo( MediaType.application( "x-tika-ooxml" ) );
   }

   @Test
   public void testAspectToAasAasxToStdout() throws TikaException, IOException {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "aas", "--format", "aasx" );
      assertThat( result.stderr() ).isEmpty();
      assertThat( contentType( result.stdoutRaw() ) ).isEqualTo( MediaType.application( "x-tika-ooxml" ) );
   }

   @Test
   public void testAspectToDotWithDefaultLanguage() {
      final File targetFile = outputFile( "output.dot" );
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "dot", "-o", targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "digraph AspectModel" );
   }

   @Test
   public void testAspectToDotWithGivenLanguage() {
      final File targetFile = outputFile( "output.dot" );
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "dot", "-o", targetFile.getAbsolutePath(), "--language",
            "en" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().startsWith( "digraph AspectModel" );
   }

   @Test
   public void testAspectToDotWithNonExistentLanguage() {
      final File targetFile = outputFile( "output.dot" );
      final ExecutionResult result = bammCli.apply( "aspect", defaultInputFile, "to", "dot", "-o", targetFile.getAbsolutePath(), "--language", "de" );
      assertThat( result.exitStatus() ).isEqualTo( 1 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "The model does not contain the desired language" );
      assertThat( targetFile ).doesNotExist();
   }

   @Test
   public void testAspectToDotToStdout() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "dot" );
      assertThat( result.stdout() ).startsWith( "digraph AspectModel" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToDotWithCustomResolver() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "dot", "--custom-resolver", resolverCommand() );
      assertThat( result.stdout() ).startsWith( "digraph AspectModel" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToHtmlWithDefaultLanguageToFile() {
      final File targetFile = outputFile( "output.html" );
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "html", "-o", targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "<html" );
   }

   @Test
   public void testAspectToHtmlWithGivenLanguageToFile() {
      final File targetFile = outputFile( "output.html" );
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "html", "-o", targetFile.getAbsolutePath(), "--language",
            "en" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "<html" );
   }

   @Test
   public void testAspectToHtmlToStdout() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "html" );
      assertThat( result.stdout() ).contains( "<html" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToHtmlWithCustomCss() {
      final String customCss = "h1 { color: #123456; }";
      final File customCssFile = outputFile( "custom.css" );
      writeToFile( customCssFile, customCss );
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "html", "--css", customCssFile.getAbsolutePath() );
      assertThat( result.stdout() ).contains( "<html" );
      assertThat( result.stdout() ).contains( customCss );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToJavaWithDefaultPackageName() {
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "java", "--output-directory",
            outputDir.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "io", "openmanufacturing", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining( file -> file.getName().equals( "AspectWithEntity.java" ) || file.getName().equals( "TestEntity.java" ) );

      final File sourceFile = directory.toPath().resolve( "AspectWithEntity.java" ).toFile();
      assertThat( sourceFile ).content().contains( "@JsonCreator" );
      assertThat( sourceFile ).content().contains( "import com.fasterxml.jackson.annotation.JsonCreator;" );
   }

   @Test
   public void testAspectToJavaWithCustomPackageName() {
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "java", "--output-directory",
            outputDir.getAbsolutePath(), "--package-name", "com.example.foo" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "com", "example", "foo" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining( file -> file.getName().equals( "AspectWithEntity.java" ) || file.getName().equals( "TestEntity.java" ) );
   }

   @Test
   public void testAspectToJavaWithoutJacksonAnnotations() {
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "java", "--output-directory",
            outputDir.getAbsolutePath(), "--no-jackson" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "io", "openmanufacturing", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining( file -> file.getName().equals( "AspectWithEntity.java" ) || file.getName().equals( "TestEntity.java" ) );

      final File sourceFile = directory.toPath().resolve( "AspectWithEntity.java" ).toFile();
      assertThat( sourceFile ).content().doesNotContain( "@JsonCreator" );
      assertThat( sourceFile ).content().doesNotContain( "import com.fasterxml.jackson.annotation.JsonCreator;" );
   }

   @Test
   public void testAspectToJavaWithDefaultPackageNameWithCustomResolver() {
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "java", "--output-directory",
            outputDir.getAbsolutePath(), "--custom-resolver", resolverCommand() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "io", "openmanufacturing", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining( file -> file.getName().equals( "AspectWithEntity.java" ) || file.getName().equals( "TestEntity.java" ) );

      final File sourceFile = directory.toPath().resolve( "AspectWithEntity.java" ).toFile();
      assertThat( sourceFile ).content().contains( "@JsonCreator" );
      assertThat( sourceFile ).content().contains( "import com.fasterxml.jackson.annotation.JsonCreator;" );
   }

   @Test
   public void testAspectToJavaWithCustomFileHeader() {
      final File outputDir = outputDirectory.toFile();
      final File templateLibraryFile = new File(
            System.getProperty( "user.dir" ) + "/../../core/sds-aspect-model-java-generator/templates/test-macro-lib.vm" );

      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "java", "--output-directory",
            outputDir.getAbsolutePath(), "--execute-library-macros", "--template-library-file", templateLibraryFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "io", "openmanufacturing", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining( file -> file.getName().equals( "AspectWithEntity.java" ) || file.getName().equals( "TestEntity.java" ) );

      final String expectedCopyright = String.format( "Copyright (c) %s OMP Test Inc. All rights reserved", LocalDate.now().getYear() );
      final File sourceFile = directory.toPath().resolve( "AspectWithEntity.java" ).toFile();
      assertThat( sourceFile ).content().contains( expectedCopyright );
   }

   @Test
   public void testAspectToJavaCustomMacroLibraryValidation() {
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = bammCli.apply( "aspect", defaultInputFile, "to", "java", "--output-directory",
            outputDir.getAbsolutePath(), "--execute-library-macros" );
      assertThat( result.exitStatus() ).isEqualTo( 1 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "Missing configuration. Please provide path to velocity template library file." );
   }

   @Test
   public void testAspectToJavaStaticWithDefaultPackageName() {
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "java", "--output-directory",
            outputDir.getAbsolutePath(), "--static" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "io", "openmanufacturing", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining(
            file -> file.getName().equals( "MetaAspectWithEntity.java" ) || file.getName().equals( "MetaTestEntity.java" ) );

      final File sourceFile = directory.toPath().resolve( "MetaAspectWithEntity.java" ).toFile();
      assertThat( sourceFile ).content().contains( "package io.openmanufacturing.test;" );
      assertThat( sourceFile ).content().contains( "class MetaAspectWithEntity implements StaticMetaClass" );
   }

   @Test
   public void testAspectToJavaStaticWithCustomPackageName() {
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "java", "--output-directory",
            outputDir.getAbsolutePath(), "--static", "--package-name", "com.example.foo" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "com", "example", "foo" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining(
            file -> file.getName().equals( "MetaAspectWithEntity.java" ) || file.getName().equals( "MetaTestEntity.java" ) );

      final File sourceFile = directory.toPath().resolve( "MetaAspectWithEntity.java" ).toFile();
      assertThat( sourceFile ).content().contains( "package com.example.foo;" );
      assertThat( sourceFile ).content().contains( "class MetaAspectWithEntity implements StaticMetaClass" );
   }

   @Test
   public void testAspectToJavaStaticWithDefaultPackageNameWithCustomResolver() {
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "java", "--output-directory",
            outputDir.getAbsolutePath(), "--static", "--custom-resolver", resolverCommand() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "io", "openmanufacturing", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining(
            file -> file.getName().equals( "MetaAspectWithEntity.java" ) || file.getName().equals( "MetaTestEntity.java" ) );

      final File sourceFile = directory.toPath().resolve( "MetaAspectWithEntity.java" ).toFile();
      assertThat( sourceFile ).content().contains( "package io.openmanufacturing.test;" );
      assertThat( sourceFile ).content().contains( "class MetaAspectWithEntity implements StaticMetaClass" );
   }

   @Test
   public void testAspectToJavaStaticWithCustomFileHeader() {
      final File outputDir = outputDirectory.toFile();
      final File templateLibraryFile = new File(
            System.getProperty( "user.dir" ) + "/../../core/sds-aspect-model-java-generator/templates/test-macro-lib.vm" );
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "java", "--output-directory",
            outputDir.getAbsolutePath(), "--static", "--execute-library-macros", "--template-library-file", templateLibraryFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "io", "openmanufacturing", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining(
            file -> file.getName().equals( "MetaAspectWithEntity.java" ) || file.getName().equals( "MetaTestEntity.java" ) );

      final File sourceFile = directory.toPath().resolve( "MetaAspectWithEntity.java" ).toFile();
      final String expectedCopyright = String.format( "Copyright (c) %s OMP Test Inc. All rights reserved", LocalDate.now().getYear() );
      assertThat( sourceFile ).content().contains( expectedCopyright );
   }

   @Test
   public void testAspectToJsonToFile() {
      final File targetFile = outputFile( "output.json" );
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "json", "-o", targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "\"entityProperty\" :" );
   }

   @Test
   public void testAspectToJsonToStdout() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "json" );
      assertThat( result.stdout() ).contains( "\"entityProperty\" :" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToJsonToStdoutWithCustomResolver() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "json", "--custom-resolver", resolverCommand() );
      assertThat( result.stdout() ).contains( "\"entityProperty\" :" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToJsonSchemaToFile() {
      final File targetFile = outputFile( "output.schema.json" );
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "schema", "-o", targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "\"entityProperty\" :" );
   }

   @Test
   public void testAspectToJsonSchemaToStdout() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "schema" );
      assertThat( result.stdout() ).contains( "$schema" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToJsonSchemaToFileWithCustomResolver() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "schema", "--custom-resolver", resolverCommand() );
      assertThat( result.stdout() ).contains( "$schema" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToOpenapiWithoutBaseUrl() {
      final ExecutionResult result = bammCli.apply( "aspect", defaultInputFile, "to", "openapi", "--json" );
      assertThat( result.exitStatus() ).isEqualTo( 2 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "Missing required option: '--api-base-url" );
   }

   @Test
   public void testAspectToOpenapiWithoutResourcePath() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "openapi", "--json", "--api-base-url",
            "https://test.example.com" );
      assertThat( result.stdout() ).contains( "\"openapi\" : \"3.0.3\"" );
      assertThat( result.stdout() ).contains( "\"url\" : \"https://test.example.com/api/v1\"" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToOpenApiWithResourcePath() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "openapi", "--json", "--api-base-url",
            "https://test.example.com", "--resource-path", "my-aspect" );
      assertThat( result.stdout() ).contains( "\"openapi\" : \"3.0.3\"" );
      assertThat( result.stdout() ).contains( "\"url\" : \"https://test.example.com/api/v1\"" );
      assertThat( result.stdout() ).contains( "\"/my-aspect\"" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToOpenApiWithResourcePathAndCustomResolver() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "openapi", "--json", "--api-base-url",
            "https://test.example.com", "--resource-path", "my-aspect", "--custom-resolver", resolverCommand() );
      assertThat( result.stdout() ).contains( "\"openapi\" : \"3.0.3\"" );
      assertThat( result.stdout() ).contains( "\"url\" : \"https://test.example.com/api/v1\"" );
      assertThat( result.stdout() ).contains( "\"/my-aspect\"" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToPngWithDefaultLanguage() throws TikaException, IOException {
      final File targetFile = outputFile( "output.png" );
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "png", "-o", targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( contentType( targetFile ) ).isEqualTo( MediaType.image( "png" ) );
   }

   @Test
   public void testAspectToPngWithGivenLanguage() throws TikaException, IOException {
      final File targetFile = outputFile( "output.png" );
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "png", "-o", targetFile.getAbsolutePath(),
            "--language", "en" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( contentType( targetFile ) ).isEqualTo( MediaType.image( "png" ) );
   }

   @Test
   public void testAspectToPngWithNonExistentLanguage() {
      final File targetFile = outputFile( "output.png" );
      final ExecutionResult result = bammCli.apply( "aspect", defaultInputFile, "to", "png", "-o", targetFile.getAbsolutePath(), "--language", "de" );
      assertThat( result.exitStatus() ).isEqualTo( 1 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "The model does not contain the desired language" );
      assertThat( targetFile ).doesNotExist();
   }

   @Test
   public void testAspectToPngToStdout() throws TikaException, IOException {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "png" );
      assertThat( result.stderr() ).isEmpty();
      assertThat( contentType( result.stdoutRaw() ) ).isEqualTo( MediaType.image( "png" ) );
   }

   @Test
   public void testAspectToPngWithCustomResolver() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "png", "--custom-resolver", resolverCommand() );
      assertThat( result.stderr() ).isEmpty();
      assertThat( contentType( result.stdoutRaw() ) ).isEqualTo( MediaType.image( "png" ) );
   }

   @Test
   public void testAspectToSvgWithDefaultLanguage() {
      final File targetFile = outputFile( "output.svg" );
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "svg", "-o", targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "<svg" );
   }

   @Test
   public void testAspectToSvgWithGivenLanguage() {
      final File targetFile = outputFile( "output.svg" );
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "svg", "-o", targetFile.getAbsolutePath(), "--language",
            "en" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "<svg" );
   }

   @Test
   public void testAspectToSvgWithNonExistentLanguage() {
      final File targetFile = outputFile( "output.svg" );
      final ExecutionResult result = bammCli.apply( "aspect", defaultInputFile, "to", "svg", "-o", targetFile.getAbsolutePath(), "--language",
            "de" );
      assertThat( result.exitStatus() ).isEqualTo( 1 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "The model does not contain the desired language" );
      assertThat( targetFile ).doesNotExist();
   }

   @Test
   public void testAspectToSvgToStdout() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "svg" );
      assertThat( result.stdout() ).contains( "<svg" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToSvgWithCustomResolver() {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "svg", "--custom-resolver", resolverCommand() );
      assertThat( result.stdout() ).contains( "<svg" );
      assertThat( result.stderr() ).isEmpty();
   }

   /**
    * Returns the File object for a test model file
    */
   private File inputFile( final TestModel testModel ) {
      final KnownVersion metaModelVersion = KnownVersion.getLatest();
      final boolean isValid = !(testModel instanceof InvalidTestAspect);
      final String resourcePath = String.format( "%s/../../core/sds-test-aspect-models/src/main/resources/%s/%s/io.openmanufacturing.test/1.0.0/%s.ttl",
            System.getProperty( "user.dir" ), isValid ? "valid" : "invalid", metaModelVersion.toString().toLowerCase(), testModel.getName() );

      try {
         return new File( resourcePath ).getCanonicalFile();
      } catch ( final IOException e ) {
         throw new RuntimeException( e );
      }
   }

   /**
    * Given a file name, returns the File object for this file inside the temporary directory
    */
   private File outputFile( final String filename ) {
      return outputDirectory.toAbsolutePath().resolve( filename ).toFile();
   }

   private void writeToFile( final File file, final String content ) {
      try {
         final BufferedWriter writer = new BufferedWriter( new FileWriter( file ) );
         writer.write( content );
         writer.close();
      } catch ( final IOException e ) {
         throw new RuntimeException( e );
      }
   }

   private MediaType contentType( final byte[] input ) {
      try {
         return new TikaConfig().getDetector().detect( new BufferedInputStream( new ByteArrayInputStream( input ) ), new Metadata() );
      } catch ( final IOException | TikaException exception ) {
         throw new RuntimeException( exception );
      }
   }

   private MediaType contentType( final File file ) {
      try {
         return new TikaConfig().getDetector().detect( new BufferedInputStream( new FileInputStream( file ) ), new Metadata() );
      } catch ( final IOException | TikaException exception ) {
         throw new RuntimeException( exception );
      }
   }

   private String resolverCommand() {
      try {
         final Path targetDirectory = Paths.get( getClass().getResource( "/" ).toURI() ).getParent();
         final Path testClasses = Paths.get( targetDirectory.toString(), "test-classes" );
         final Path modelsRoot = Path.of( TestModel.class.getClassLoader().getResource( "valid" ).toURI() );
         final String scriptName = OS.WINDOWS.isCurrentOs() ? "\\model_resolver.bat" : "/model_resolver.sh";
         return testClasses + scriptName + " " + modelsRoot + " " + KnownVersion.getLatest().toString().toLowerCase();
      } catch ( final URISyntaxException e ) {
         throw new RuntimeException( e );
      }
   }
}
