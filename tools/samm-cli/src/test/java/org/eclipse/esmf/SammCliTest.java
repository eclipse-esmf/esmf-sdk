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

package org.eclipse.esmf;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.ProcessLauncher.ExecutionResult;
import org.eclipse.esmf.aspect.AspectValidateCommand;
import org.eclipse.esmf.aspectmodel.shacl.violation.InvalidSyntaxViolation;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.InvalidTestAspect;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestModel;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * The tests for the CLI that are executed by Maven Surefire. They work using the {@link MainClassProcessLauncher}, i.e. directly call
 * the main function in {@link SammCli}.
 */
@ExtendWith( LogExtension.class )
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
public class SammCliTest extends MetaModelVersions {
   protected ProcessLauncher sammCli;
   private final TestModel testModel = TestAspect.ASPECT_WITH_ENTITY;
   private final String defaultInputFile = inputFile( testModel ).getAbsolutePath();

   Path outputDirectory = null;

   @BeforeEach
   public void beforeEach() throws IOException {
      sammCli = new MainClassProcessLauncher( SammCli.class );
      outputDirectory = Files.createTempDirectory( "junit" );
   }

   @AfterEach
   public void afterEach() {
      if ( outputDirectory != null ) {
         final File outputDir = outputDirectory.toFile();
         if ( outputDir.exists() && outputDir.isDirectory() ) {
            // Recursively delete temporary directory
            try ( final Stream<Path> paths = Files.walk( outputDirectory ) ) {
               paths.sorted( Comparator.reverseOrder() )
                     .map( Path::toFile )
                     .forEach( file -> {
                        if ( !file.delete() ) {
                           System.err.println( "Could not delete file " + file );
                        }
                     } );
            } catch ( final IOException e ) {
               throw new RuntimeException( e );
            }
         }
      }
   }

   @Test
   public void testNoArgs() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color" );
      assertThat( result.stdout() ).contains( "Usage:" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectWithoutSubcommand() {
      final ExecutionResult result = sammCli.apply( "--disable-color", "aspect" );
      assertThat( result.exitStatus() ).isEqualTo( 2 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "Missing required parameter" );
   }

   @Test
   public void testWrongArgs() {
      final ExecutionResult result = sammCli.apply( "--disable-color", "-i", "doesnotexist" );
      assertThat( result.exitStatus() ).isEqualTo( 2 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "Unknown options" );
   }

   @Test
   public void testHelp() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "help" );
      assertThat( result.stdout() ).contains( "Usage:" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testVerboseOutput() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "validate", "-vvv" );
      assertThat( result.stdout() ).contains( "Input model is valid" );
      assertThat( result.stderr() ).contains( "DEBUG " + AspectValidateCommand.class.getName() );
   }

   @Test
   public void testAspectMigrateToFile() {
      final File targetFile = outputFile( "output.ttl" );
      final ExecutionResult result =
            sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "migrate", "-o", targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "@prefix" );
   }

   @Test
   public void testAspectMigrateToStdout() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "migrate" );
      assertThat( result.stdout() ).contains( "@prefix" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectPrettyPrintToFile() {
      final File targetFile = outputFile( "output.ttl" );
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "prettyprint", "-o",
            targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "@prefix" );
   }

   @Test
   public void testAspectPrettyPrintToStdout() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "prettyprint" );
      assertThat( result.stdout() ).contains( "@prefix" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectValidateValidModel() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "validate" );
      assertThat( result.stdout() ).contains( "Input model is valid" );
      assertThat( result.stderr() ).isEmpty();
   }

   @ParameterizedTest
   @EnumSource( TestAspect.class )
   @EnabledIfSystemProperty( named = "packaging-type", matches = "native" )
   public void testAspectValidateValidModelAllTestFiles( final TestModel aspect ) {
      final String input = inputFile( aspect ).getAbsolutePath();
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", input, "validate" );
      assertThat( result.stdout() ).contains( "Input model is valid" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectValidateWithRelativePath() {
      final File workingDirectory = new File( defaultInputFile ).getParentFile();
      final String relativeFileName = "." + File.separator + new File( defaultInputFile ).getName();
      final ProcessLauncher.ExecutionContext executionContext = new ProcessLauncher.ExecutionContext(
            List.of( "--disable-color", "aspect", relativeFileName, "validate" ), Optional.empty(), workingDirectory );

      final ExecutionResult result = sammCli.apply( executionContext );
      assertThat( result.stdout() ).contains( "Input model is valid" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   @DisabledOnOs( OS.WINDOWS )
   public void testAspectValidateInvalidModel() {
      final File invalidModel = inputFile( InvalidTestAspect.INVALID_SYNTAX );
      final ExecutionResult result = sammCli.apply( "--disable-color", "aspect", invalidModel.getAbsolutePath(), "validate" );
      assertThat( result.exitStatus() ).isEqualTo( 1 );
      assertThat( result.stderr() ).isEmpty();
      assertThat( result.stdout() ).contains( "Triples not terminated by DOT" );
   }

   @Test
   public void testAspectValidateWithDetails() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "validate", "--details" );
      assertThat( result.stdout() ).contains( "Input model is valid" );
      assertThat( result.stderr() ).isEmpty();

      final File invalidModel = inputFile( InvalidTestAspect.INVALID_SYNTAX );
      final ExecutionResult result2 = sammCli.apply( "--disable-color", "aspect", invalidModel.getAbsolutePath(), "validate", "--details" );
      assertThat( result2.exitStatus() ).isEqualTo( 1 );
      assertThat( result2.stderr() ).isEmpty();
      assertThat( result2.stdout() ).contains( InvalidSyntaxViolation.ERROR_CODE );
   }

   @Test
   public void testAspectValidateWithCustomResolver() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "validate",
            "--custom-resolver", resolverCommand() );
      assertThat( result.stdout() ).contains( "Input model is valid" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToAasXmlToFile() {
      final File targetFile = outputFile( "output.xml" );
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "aas", "--format",
            "xml", "-o", targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().startsWith( "<?xml" );
   }

   @ParameterizedTest
   @EnumSource( TestAspect.class )
   @EnabledIfSystemProperty( named = "packaging-type", matches = "native" )
   public void testAspectToAasXmlToFileAllTestFiles( final TestModel aspect ) {
      final String input = inputFile( aspect ).getAbsolutePath();
      final File targetFile = outputFile( "output.xml" );
      assertThat( targetFile ).doesNotExist();
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", input, "to", "aas", "--format",
            "xml", "-o", targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().startsWith( "<?xml" );
   }

   @Test
   public void testAspectToAasXmlToStdout() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "aas", "--format",
            "xml" );
      assertThat( result.stdout() ).startsWith( "<?xml" );
      assertThat( result.stderr() ).isEmpty();
   }

   @ParameterizedTest
   @EnumSource( TestAspect.class )
   @EnabledIfSystemProperty( named = "packaging-type", matches = "native" )
   public void testAspectToAasXmlToStdoutAllTestFiles( final TestModel aspect ) {
      final String input = inputFile( aspect ).getAbsolutePath();
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", input, "to", "aas", "--format",
            "xml" );
      assertThat( result.stdout() ).startsWith( "<?xml" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToAasAasxToFile() {
      final File targetFile = outputFile( "output.aasx" );
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "aas", "--format",
            "aasx", "-o", targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( contentType( targetFile ) ).isEqualTo( MediaType.application( "x-tika-ooxml" ) );
   }

   @ParameterizedTest
   @EnumSource( TestAspect.class )
   @EnabledIfSystemProperty( named = "packaging-type", matches = "native" )
   public void testAspectToAasAasxToFileAllTestFiles( final TestModel aspect ) {
      final String input = inputFile( aspect ).getAbsolutePath();
      final File targetFile = outputFile( "output.aasx" );
      assertThat( targetFile ).doesNotExist();
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", input, "to", "aas", "--format",
            "aasx", "-o", targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( contentType( targetFile ) ).isEqualTo( MediaType.application( "x-tika-ooxml" ) );
   }

   @Test
   public void testAspectToAasAasxToStdout() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "aas", "--format",
            "aasx" );
      assertThat( result.stderr() ).isEmpty();
      assertThat( contentType( result.stdoutRaw() ) ).isEqualTo( MediaType.application( "x-tika-ooxml" ) );
   }

   @Test
   public void testAspectToAasJsonToFile() {
      final File targetFile = outputFile( "output.json" );
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "aas", "--format",
            "json", "-o",
            targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( contentType( targetFile ) ).isEqualTo( MediaType.text( "plain" ) );
   }

   @ParameterizedTest
   @EnumSource( TestAspect.class )
   @EnabledIfSystemProperty( named = "packaging-type", matches = "native" )
   public void testAspectToAasJsonToFileAllTestFiles( final TestModel aspect ) {
      final String input = inputFile( aspect ).getAbsolutePath();
      final File targetFile = outputFile( "output.json" );
      assertThat( targetFile ).doesNotExist();
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", input, "to", "aas", "--format",
            "json", "-o",
            targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( contentType( targetFile ) ).isEqualTo( MediaType.text( "plain" ) );
   }

   @Test
   public void testAspectToAasJsonToStdout() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "aas", "--format",
            "json" );
      assertThat( result.stderr() ).isEmpty();
      assertThat( contentType( result.stdoutRaw() ) ).isEqualTo( MediaType.text( "plain" ) );
   }

   @ParameterizedTest
   @EnumSource( TestAspect.class )
   @EnabledIfSystemProperty( named = "packaging-type", matches = "native" )
   public void testAspectToAasJsonToStdoutAllTestFiles( final TestModel aspect ) {
      final String input = inputFile( aspect ).getAbsolutePath();
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", input, "to", "aas", "--format",
            "json" );
      assertThat( result.stderr() ).isEmpty();
      assertThat( contentType( result.stdoutRaw() ) ).isEqualTo( MediaType.text( "plain" ) );
   }

   @Test
   public void testAasToAspectModel() {
      // First create the AAS XML file we want to read
      final File aasXmlFile = outputFile( "output.xml" );
      final ExecutionResult generateAasXmlResult = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "aas",
            "--format", "xml", "-o", aasXmlFile.getAbsolutePath() );
      assertThat( generateAasXmlResult.stdout() ).isEmpty();
      assertThat( generateAasXmlResult.stderr() ).isEmpty();

      // Now the actual test, convert it to an Aspect Model
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aas", aasXmlFile.getAbsolutePath(), "to", "aspect",
            "--output-directory", outputDir.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = outputDirectory.resolve( testModel.getUrn().getNamespace() )
            .resolve( testModel.getUrn().getVersion() )
            .toFile();
      assertThat( directory ).exists();
      final String expectedAspectModelFileName = testModel.getName() + ".ttl";
      assertThat( directory ).isDirectoryContaining( file -> file.getName().equals( expectedAspectModelFileName ) );

      final File sourceFile = directory.toPath().resolve( expectedAspectModelFileName ).toFile();
      assertThat( sourceFile ).content().contains( ":" + testModel.getName() + " a samm:Aspect" );
   }

   @Test
   public void testAasListSubmodels() {
      final File aasXmlFile = outputFile( "output.xml" );
      final ExecutionResult generateAasXmlResult = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "aas",
            "--format", "xml", "-o", aasXmlFile.getAbsolutePath() );
      assertThat( generateAasXmlResult.stdout() ).isEmpty();
      assertThat( generateAasXmlResult.stderr() ).isEmpty();

      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aas", aasXmlFile.getAbsolutePath(), "list" );
      assertThat( result.stdout() ).isNotEmpty();
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAasToAspectModelWithSelectedSubmodels() {
      // First create the AAS XML file we want to read
      final File aasXmlFile = outputFile( "output.xml" );
      final ExecutionResult generateAasXmlResult = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "aas",
            "--format", "xml", "-o", aasXmlFile.getAbsolutePath() );
      assertThat( generateAasXmlResult.stdout() ).isEmpty();
      assertThat( generateAasXmlResult.stderr() ).isEmpty();

      // Now the actual test, convert it to an Aspect Model
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aas", aasXmlFile.getAbsolutePath(), "to", "aspect",
            "--output-directory", outputDir.getAbsolutePath(), "-s", "1" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = outputDirectory.resolve( testModel.getUrn().getNamespace() )
            .resolve( testModel.getUrn().getVersion() )
            .toFile();
      assertThat( directory ).exists();
      final String expectedAspectModelFileName = testModel.getName() + ".ttl";
      assertThat( directory ).isDirectoryContaining( file -> file.getName().equals( expectedAspectModelFileName ) );

      final File sourceFile = directory.toPath().resolve( expectedAspectModelFileName ).toFile();
      assertThat( sourceFile ).content().contains( ":" + testModel.getName() + " a samm:Aspect" );
   }

   @Test
   public void testAspectToHtmlWithDefaultLanguageToFile() {
      final File targetFile = outputFile( "output.html" );
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "html", "-o",
            targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "<html" );
   }

   @Test
   public void testAspectToHtmlWithGivenLanguageToFile() {
      final File targetFile = outputFile( "output.html" );
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "html", "-o",
            targetFile.getAbsolutePath(), "--language",
            "en" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "<html" );
   }

   @Test
   public void testAspectToHtmlToStdout() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "html" );
      assertThat( result.stdout() ).contains( "<html" );
      assertThat( result.stdout() ).doesNotContainPattern( "$[a-zA-Z]" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToHtmlWithCustomCss() {
      final String customCss = "h1 { color: #123456; }";
      final File customCssFile = outputFile( "custom.css" );
      writeToFile( customCssFile, customCss );
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "html", "--css",
            customCssFile.getAbsolutePath() );
      assertThat( result.stdout() ).contains( "<html" );
      assertThat( result.stdout() ).doesNotContainPattern( "$[a-zA-Z]" );
      assertThat( result.stdout() ).contains( customCss );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToJavaWithDefaultPackageName() {
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "java",
            "--output-directory",
            outputDir.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "org", "eclipse", "esmf", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining(
            file -> file.getName().equals( "AspectWithEntity.java" ) || file.getName().equals( "TestEntity.java" ) );

      final File sourceFile = directory.toPath().resolve( "AspectWithEntity.java" ).toFile();
      assertThat( sourceFile ).content().contains( "@JsonCreator" );
      assertThat( sourceFile ).content().contains( "import com.fasterxml.jackson.annotation.JsonCreator;" );
   }

   @Test
   public void testAspectToJavaWithCustomPackageName() {
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "java",
            "--output-directory",
            outputDir.getAbsolutePath(), "--package-name", "com.example.foo" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "com", "example", "foo" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining(
            file -> file.getName().equals( "AspectWithEntity.java" ) || file.getName().equals( "TestEntity.java" ) );
   }

   @Test
   public void testAspectToJavaWithoutJacksonAnnotations() {
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "java",
            "--output-directory",
            outputDir.getAbsolutePath(), "--no-jackson" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "org", "eclipse", "esmf", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining(
            file -> file.getName().equals( "AspectWithEntity.java" ) || file.getName().equals( "TestEntity.java" ) );

      final File sourceFile = directory.toPath().resolve( "AspectWithEntity.java" ).toFile();
      assertThat( sourceFile ).content().doesNotContain( "@JsonCreator" );
      assertThat( sourceFile ).content().doesNotContain( "import com.fasterxml.jackson.annotation.JsonCreator;" );
   }

   @Test
   public void testAspectToJavaWithDefaultPackageNameWithCustomResolver() {
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "java",
            "--output-directory", outputDir.getAbsolutePath(), "--custom-resolver", resolverCommand() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "org", "eclipse", "esmf", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining(
            file -> file.getName().equals( "AspectWithEntity.java" ) || file.getName().equals( "TestEntity.java" ) );

      final File sourceFile = directory.toPath().resolve( "AspectWithEntity.java" ).toFile();
      assertThat( sourceFile ).content().contains( "@JsonCreator" );
      assertThat( sourceFile ).content().contains( "import com.fasterxml.jackson.annotation.JsonCreator;" );
   }

   @Test
   public void testAspectToJavaWithCustomFileHeader() {
      final File outputDir = outputDirectory.toFile();
      final File templateLibraryFile = new File(
            System.getProperty( "user.dir" ) + "/../../core/esmf-aspect-model-java-generator/templates/test-macro-lib.vm" );

      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "java",
            "--output-directory",
            outputDir.getAbsolutePath(), "--execute-library-macros", "--template-library-file", templateLibraryFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "org", "eclipse", "esmf", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining(
            file -> file.getName().equals( "AspectWithEntity.java" ) || file.getName().equals( "TestEntity.java" ) );

      final String expectedCopyright = String.format( "Copyright (c) %s Test Inc. All rights reserved", LocalDate.now().getYear() );
      final File sourceFile = directory.toPath().resolve( "AspectWithEntity.java" ).toFile();
      assertThat( sourceFile ).content().contains( expectedCopyright );
   }

   @Test
   public void testAspectToJavaCustomMacroLibraryValidation() {
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = sammCli.apply( "--disable-color", "aspect", defaultInputFile, "to", "java", "--output-directory",
            outputDir.getAbsolutePath(), "--execute-library-macros" );
      assertThat( result.exitStatus() ).isEqualTo( 1 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "Missing configuration. Please provide path to velocity template library file." );
   }

   @Test
   public void testAspectToJavaStaticWithDefaultPackageName() {
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "java",
            "--output-directory", outputDir.getAbsolutePath(), "--static" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "org", "eclipse", "esmf", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining(
            file -> file.getName().equals( "MetaAspectWithEntity.java" ) || file.getName().equals( "MetaTestEntity.java" ) );

      final File sourceFile = directory.toPath().resolve( "MetaAspectWithEntity.java" ).toFile();
      assertThat( sourceFile ).content().contains( "package org.eclipse.esmf.test;" );
      assertThat( sourceFile ).content().contains( "class MetaAspectWithEntity implements StaticMetaClass" );
   }

   @Test
   public void testAspectToJavaStaticWithCustomPackageName() {
      final File outputDir = outputDirectory.toFile();
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "java",
            "--output-directory", outputDir.getAbsolutePath(), "--static", "--package-name", "com.example.foo" );
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
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "java",
            "--output-directory", outputDir.getAbsolutePath(), "--static", "--custom-resolver", resolverCommand() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "org", "eclipse", "esmf", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining(
            file -> file.getName().equals( "MetaAspectWithEntity.java" ) || file.getName().equals( "MetaTestEntity.java" ) );

      final File sourceFile = directory.toPath().resolve( "MetaAspectWithEntity.java" ).toFile();
      assertThat( sourceFile ).content().contains( "package org.eclipse.esmf.test;" );
      assertThat( sourceFile ).content().contains( "class MetaAspectWithEntity implements StaticMetaClass" );
   }

   @Test
   public void testAspectToJavaStaticWithCustomFileHeader() {
      final File outputDir = outputDirectory.toFile();
      final File templateLibraryFile = new File(
            System.getProperty( "user.dir" ) + "/../../core/esmf-aspect-model-java-generator/templates/test-macro-lib.vm" );
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "java",
            "--output-directory", outputDir.getAbsolutePath(), "--static", "--execute-library-macros", "--template-library-file",
            templateLibraryFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();

      final File directory = Paths.get( outputDir.getAbsolutePath(), "org", "eclipse", "esmf", "test" ).toFile();
      assertThat( directory ).exists();
      assertThat( directory ).isDirectoryContaining(
            file -> file.getName().equals( "MetaAspectWithEntity.java" ) || file.getName().equals( "MetaTestEntity.java" ) );

      final File sourceFile = directory.toPath().resolve( "MetaAspectWithEntity.java" ).toFile();
      final String expectedCopyright = String.format( "Copyright (c) %s Test Inc. All rights reserved", LocalDate.now().getYear() );
      assertThat( sourceFile ).content().contains( expectedCopyright );
   }

   @Test
   public void testAspectToJsonToFile() {
      final File targetFile = outputFile( "output.json" );
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "json", "-o",
            targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "\"entityProperty\" :" );
   }

   @Test
   public void testAspectToJsonToStdout() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "json" );
      assertThat( result.stdout() ).contains( "\"entityProperty\" :" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToJsonToStdoutWithCustomResolver() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "json",
            "--custom-resolver", resolverCommand() );
      assertThat( result.stdout() ).contains( "\"entityProperty\" :" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToJsonSchemaToFile() {
      final File targetFile = outputFile( "output.schema.json" );
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "schema", "-o",
            targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "\"entityProperty\" :" );
   }

   @Test
   public void testAspectToJsonSchemaToStdout() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "schema" );
      assertThat( result.stdout() ).contains( "$schema" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToJsonSchemaToFileWithCustomResolver() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "schema",
            "--custom-resolver", resolverCommand() );
      assertThat( result.stdout() ).contains( "$schema" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToOpenapiWithoutBaseUrl() {
      final ExecutionResult result = sammCli.apply( "--disable-color", "aspect", defaultInputFile, "to", "openapi", "--json" );
      assertThat( result.exitStatus() ).isEqualTo( 2 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "Missing required option: '--api-base-url" );
   }

   @Test
   public void testAspectToOpenapiWithoutResourcePath() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "openapi", "--json",
            "--api-base-url", "https://test.example.com" );
      assertThat( result.stdout() ).contains( "\"openapi\" : \"3.0.3\"" );
      assertThat( result.stdout() ).contains( "\"url\" : \"https://test.example.com/api/v1\"" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToOpenApiWithResourcePath() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "openapi", "--json",
            "--api-base-url", "https://test.example.com", "--resource-path", "my-aspect" );
      assertThat( result.stdout() ).contains( "\"openapi\" : \"3.0.3\"" );
      assertThat( result.stdout() ).contains( "\"url\" : \"https://test.example.com/api/v1\"" );
      assertThat( result.stdout() ).contains( "\"/my-aspect\"" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToOpenApiWithResourcePathAndCustomResolver() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "openapi", "--json",
            "--api-base-url", "https://test.example.com", "--resource-path", "my-aspect", "--custom-resolver", resolverCommand() );
      assertThat( result.stdout() ).contains( "\"openapi\" : \"3.0.3\"" );
      assertThat( result.stdout() ).contains( "\"url\" : \"https://test.example.com/api/v1\"" );
      assertThat( result.stdout() ).contains( "\"/my-aspect\"" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToOpenApiWithSeparateJsonFiles() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "openapi", "--json",
            "--api-base-url", "https://test.example.com", "--separate-files", "--output", outputDirectory.toString() );

      assertThat( outputDirectory.resolve( testModel.getName() + ".oai.json" ) ).exists();
      assertThat( outputDirectory.resolve( "TestEntity.json" ) ).exists();
      assertThat( outputDirectory.resolve( "TestEntity.json" ).toFile() ).content().contains( "This is a test entity" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToOpenApiWithSeparateYamlFiles() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "openapi",
            "--api-base-url", "https://test.example.com", "--separate-files", "--output", outputDirectory.toString() );

      assertThat( outputDirectory.resolve( testModel.getName() + ".oai.yaml" ) ).exists();
      assertThat( outputDirectory.resolve( "TestEntity.yaml" ) ).exists();
      assertThat( outputDirectory.resolve( "TestEntity.yaml" ).toFile() ).content().contains( "This is a test entity" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToPngWithDefaultLanguage() {
      final File targetFile = outputFile( "output.png" );
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "png", "-o",
            targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( contentType( targetFile ) ).isEqualTo( MediaType.image( "png" ) );
   }

   @Test
   public void testAspectToPngWithGivenLanguage() {
      final File targetFile = outputFile( "output.png" );
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "png", "-o",
            targetFile.getAbsolutePath(), "--language", "en" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( contentType( targetFile ) ).isEqualTo( MediaType.image( "png" ) );
   }

   @Test
   public void testAspectToPngWithNonExistentLanguage() {
      final File targetFile = outputFile( "output.png" );
      final ExecutionResult result = sammCli.apply( "--disable-color", "aspect", defaultInputFile, "to", "png", "-o",
            targetFile.getAbsolutePath(), "--language", "de" );
      assertThat( result.exitStatus() ).isEqualTo( 1 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "The model does not contain the desired language" );
      assertThat( targetFile ).doesNotExist();
   }

   @Test
   public void testAspectToPngToStdout() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "png" );
      assertThat( result.stderr() ).isEmpty();
      assertThat( contentType( result.stdoutRaw() ) ).isEqualTo( MediaType.image( "png" ) );
   }

   @Test
   public void testAspectToPngWithCustomResolver() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "png",
            "--custom-resolver", resolverCommand() );
      assertThat( result.stderr() ).isEmpty();
      assertThat( contentType( result.stdoutRaw() ) ).isEqualTo( MediaType.image( "png" ) );
   }

   @Test
   public void testAspectToSvgWithDefaultLanguage() {
      final File targetFile = outputFile( "output.svg" );
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "svg", "-o",
            targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "<svg" );
   }

   @Test
   public void testAspectToSvgWithGivenLanguage() {
      final File targetFile = outputFile( "output.svg" );
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "svg", "-o",
            targetFile.getAbsolutePath(), "--language", "en" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().contains( "<svg" );
   }

   @Test
   public void testAspectToSvgWithNonExistentLanguage() {
      final File targetFile = outputFile( "output.svg" );
      final ExecutionResult result = sammCli.apply( "--disable-color", "aspect", defaultInputFile, "to", "svg", "-o",
            targetFile.getAbsolutePath(), "--language", "de" );
      assertThat( result.exitStatus() ).isEqualTo( 1 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "The model does not contain the desired language" );
      assertThat( targetFile ).doesNotExist();
   }

   @Test
   public void testAspectToSvgToStdout() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "svg" );
      assertThat( result.stdout() ).contains( "<svg" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectToSvgWithCustomResolver() {
      final ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "to", "svg",
            "--custom-resolver", resolverCommand() );
      assertThat( result.stdout() ).contains( "<svg" );
      assertThat( result.stderr() ).isEmpty();
   }

   /**
    * Returns the File object for a test model file
    */
   private File inputFile( final TestModel testModel ) {
      final KnownVersion metaModelVersion = KnownVersion.getLatest();
      final boolean isValid = !( testModel instanceof InvalidTestAspect );
      final String resourcePath = String.format(
            "%s/../../core/esmf-test-aspect-models/src/main/resources/%s/%s/org.eclipse.esmf.test/1.0.0/%s.ttl",
            System.getProperty( "user.dir" ), isValid ? "valid" : "invalid", metaModelVersion.toString().toLowerCase(),
            testModel.getName() );

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
      // Note that the following code must not use .class/.getClass()/.getClassLoader() but only operate on the file system level,
      // since otherwise it will break when running the test suite from the maven build (where tests are run from the jar and resources
      // are not resolved to the file system but to the jar)
      try {
         final String resolverScript = new File(
               System.getProperty( "user.dir" ) + "/target/test-classes/model_resolver" + ( OS.WINDOWS.isCurrentOs()
                     ? ".bat" : ".sh" ) ).getCanonicalPath();
         final String modelsRoot = new File( System.getProperty( "user.dir" ) + "/target/classes/valid" ).getCanonicalPath();
         final String metaModelVersion = KnownVersion.getLatest().toString().toLowerCase();
         return resolverScript + " " + modelsRoot + " " + metaModelVersion;
      } catch ( final IOException exception ) {
         throw new RuntimeException( exception );
      }
   }
}
