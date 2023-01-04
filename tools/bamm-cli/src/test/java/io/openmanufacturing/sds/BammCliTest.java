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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
      final MediaType mediaType = new TikaConfig().getDetector().detect( new BufferedInputStream( new FileInputStream( targetFile ) ), new Metadata() );
      assertThat( mediaType ).isIn( MediaType.application( "x-tika-ooxml" ), MediaType.application( "zip" ) );
   }

   @Test
   public void testAspectToAasAasxToStdout() throws TikaException, IOException {
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "aas", "--format", "aasx" );
      assertThat( result.stderr() ).isEmpty();
      final MediaType mediaType = new TikaConfig().getDetector()
            .detect( new BufferedInputStream( new ByteArrayInputStream( result.stdout().getBytes() ) ), new Metadata() );
      assertThat( mediaType ).isIn( MediaType.application( "x-tika-ooxml" ), MediaType.application( "zip" ) );
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
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "dot", "-o", targetFile.getAbsolutePath(), "-l", "en" );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().startsWith( "digraph AspectModel" );
   }

   @Test
   public void testAspectToDotWithNonExistentLanguage() {
      final File targetFile = outputFile( "output.dot" );
      final ExecutionResult result = bammCli.apply( "aspect", defaultInputFile, "to", "dot", "-o", targetFile.getAbsolutePath(), "-l", "de" );
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
      final ExecutionResult result = bammCli.runAndExpectSuccess( "aspect", defaultInputFile, "to", "html", "-o", targetFile.getAbsolutePath(), "-l", "en" );
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

   }

   @Test
   public void testAspectToJavaWithDefaultPackageName() {

   }

   @Test
   public void testAspectToJavaWithCustomPackageName() {

   }

   @Test
   public void testAspectToJavaWithoutJacksonAnnotations() {

   }

   @Test
   public void testAspectToJavaWithDefaultPackageNameWithCustomResolver() {

   }

   @Test
   public void testAspectToJavaWithoutFileHeader() {

   }

   @Test
   public void testAspectToJavaWithCustomFileHeader() {

   }

   @Test
   public void testAspectToJavaWithCustomMacroLibrary() {

   }

   @Test
   public void testAspectToJavaStaticWithDefaultPackageName() {

   }

   @Test
   public void testAspectToJavaStaticWithCustomPackageName() {

   }

   @Test
   public void testAspectToJavaStaticWithoutJacksonAnnotations() {

   }

   @Test
   public void testAspectToJavaStaticWithDefaultPackageNameWithCustomResolver() {

   }

   @Test
   public void testAspectToJavaStaticWithoutFileHeader() {

   }

   @Test
   public void testAspectToJavaStaticWithCustomFileHeader() {

   }

   @Test
   public void testAspectToJsonToFile() {

   }

   @Test
   public void testAspectToJsonToStdout() {

   }

   @Test
   public void testAspectToJsonToFileWithCustomResolver() {

   }

   @Test
   public void testAspectToJsonSchemaToFile() {

   }

   @Test
   public void testAspectToJsonSchemaToStdout() {

   }

   @Test
   public void testAspectToJsonSchemaToFileWithCustomResolver() {

   }

   @Test
   public void testAspectToOpenapiWithoutBaseUrl() {

   }

   @Test
   public void testAspectToOpenapiWithoutResourcePath() {

   }

   @Test
   public void testAspectToOpenApiWithResourcePath() {

   }

   @Test
   public void testAspectToOpenApiWithResourcePathAndCustomResolver() {

   }

   @Test
   public void testAspectToPngWithDefaultLanguage() {

   }

   @Test
   public void testAspectToPngWithGivenLanguage() {

   }

   @Test
   public void testAspectToPngWithNonExistentLanguage() {

   }

   @Test
   public void testAspectToPngToStdout() {

   }

   @Test
   public void testAspectToPngWithCustomResolver() {

   }

   @Test
   public void testAspectToSvgWithDefaultLanguage() {

   }

   @Test
   public void testAspectToSvgWithGivenLanguage() {

   }

   @Test
   public void testAspectToSvgWithNonExistentLanguage() {

   }

   @Test
   public void testAspectToSvgToStdout() {

   }

   @Test
   public void testAspectToSvgWithCustomResolver() {

   }

   /**
    * Returns the File object for a test model file
    */
   private File inputFile( final TestModel testModel ) {
      final KnownVersion metaModelVersion = KnownVersion.getLatest();
      final boolean isValid = !(testModel instanceof InvalidTestAspect);
      final String resourcePath = String.format( "%s/%s/io.openmanufacturing.test/1.0.0/%s.ttl",
            isValid ? "valid" : "invalid", metaModelVersion.toString().toLowerCase(), testModel.getName() );
      final URL resource = TestModel.class.getClassLoader().getResource( resourcePath );
      assertThat( resource.getProtocol() ).isEqualTo( "file" );
      return new File( resource.getFile() );
   }

   /**
    * Given a file name, returns the File object for this file inside the temporary directory
    */
   private File outputFile( final String filename ) {
      return outputDirectory.toAbsolutePath().resolve( filename ).toFile();
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
