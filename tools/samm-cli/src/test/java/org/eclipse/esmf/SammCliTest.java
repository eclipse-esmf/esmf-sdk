/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.resolver.process.ProcessLauncher;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.InvalidTestAspect;
import org.eclipse.esmf.test.OrderingTestAspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestModel;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.apache.jena.shared.LockMRSW;
import org.apache.jena.sparql.core.mem.PMapQuadTable;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;

/**
 * The tests for the CLI that are executed by Maven Surefire. They work using the {@link MainClassProcessLauncher}, i.e. directly call
 * the main function in {@link SammCli}.
 */
@ExtendWith( LogExtension.class )
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
class SammCliTest {
   protected ProcessLauncher<?> sammCli;
   private final TestModel testModel = TestAspect.ASPECT_WITH_ENTITY;
   private final String defaultInputFile = inputFile( testModel ).getAbsolutePath();
   private final String defaultInputUrn = testModel.getUrn().toString();
   private final String defaultModelsRoot = inputFile( testModel ).toPath().getParent().getParent().getParent().toFile().getAbsolutePath();

   Path outputDirectory;

   @BeforeEach
   void beforeEach() throws IOException {
      sammCli = new MainClassProcessLauncher( SammCli.class, List.of(),
            argument -> !"-XX:ThreadPriorityPolicy=1".equals( argument ) && !argument.startsWith( "-agentlib:jdwp" )
      );
      outputDirectory = Files.createTempDirectory( "junit" );

      final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
      Stream.of( LockMRSW.class, PMapQuadTable.class ).forEach( loggerClass -> {
         final Logger logger = loggerContext.getLogger( loggerClass );
         logger.setLevel( Level.OFF );
      } );
   }

   @AfterEach
   void afterEach() {
      if ( null != outputDirectory ) {
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
            } catch ( final IOException exception ) {
               throw new RuntimeException( exception );
            }
         }
      }
   }



   /**
    * Returns the File object for a test namespace package file
    */
   private static File inputFile( final String filename ) {
      final String resourcePath = String.format( "%s/../../core/esmf-test-aspect-models/src/main/resources/packages/%s",
            System.getProperty( "user.dir" ), filename );
      try {
         return new File( resourcePath ).getCanonicalFile();
      } catch ( final IOException exception ) {
         throw new RuntimeException( exception );
      }
   }

   /**
    * Returns the File object for a test model file
    */
   private static File inputFile( final TestModel testModel ) {
      final boolean isValid = !( testModel instanceof InvalidTestAspect );
      final boolean isOrdering = testModel instanceof OrderingTestAspect;

      final String resourcePath;
      if ( isOrdering ) {
         resourcePath = String.format(
               "%s/../../core/esmf-test-aspect-models/src/main/resources/valid/org.eclipse.esmf.test.ordering/1.0.0/%s.ttl",
               System.getProperty( "user.dir" ), testModel.getName() );
      } else {
         resourcePath = String.format(
               "%s/../../core/esmf-test-aspect-models/src/main/resources/%s/org.eclipse.esmf.test/1.0.0/%s.ttl",
               System.getProperty( "user.dir" ), isValid ? "valid" : "invalid", testModel.getName() );
      }

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

   private static void writeToFile( final File file, final String content ) {
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

   private static MediaType contentType( final File file ) {
      try {
         return new TikaConfig().getDetector().detect( new BufferedInputStream( new FileInputStream( file ) ), new Metadata() );
      } catch ( final IOException | TikaException exception ) {
         throw new RuntimeException( exception );
      }
   }

   private static String resolverCommand() {
      // Note that the following code must not use .class/.getClass()/.getClassLoader() but only operate on the file system level,
      // since otherwise it will break when running the test suite from the maven build (where tests are run from the jar and resources
      // are not resolved to the file system but to the jar)
      try {
         final String resolverScript = new File(
               System.getProperty( "user.dir" ) + "/target/test-classes/model_resolver" + ( OS.WINDOWS.isCurrentOs()
                     ? ".bat" : ".sh" ) ).getCanonicalPath();
         final String modelsRoot = new File( System.getProperty( "user.dir" ) + "/target/classes/valid" ).getCanonicalPath();
         final String metaModelVersion = KnownVersion.getLatest().toString().toLowerCase();
         return resolverScript + ' ' + modelsRoot + ' ' + metaModelVersion;
      } catch ( final IOException exception ) {
         throw new RuntimeException( exception );
      }
   }
}
