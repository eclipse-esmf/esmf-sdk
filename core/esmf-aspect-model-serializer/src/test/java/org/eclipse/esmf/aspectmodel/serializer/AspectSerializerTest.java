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

package org.eclipse.esmf.aspectmodel.serializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.esmf.aspectmodel.RdfUtil.createModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ChangeGroup;
import org.eclipse.esmf.aspectmodel.edit.change.AddAspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.change.MoveRenameAspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFileBuilder;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AspectSerializerTest {
   Path outputDirectory = null;

   @BeforeEach
   void beforeEach() throws IOException {
      outputDirectory = Files.createTempDirectory( "junit" );
   }

   @AfterEach
   void afterEach() {
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
   void testWriteAspectModelFileToFileSystem() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );

      // Change the loaded test model's source location to a file system location
      final AspectChangeContext changeContext = new AspectChangeContext( aspectModel );
      final Path filePath = outputDirectory.resolve( "Aspect.ttl" );
      changeContext.applyChange( new MoveRenameAspectModelFile( aspectModel.files().iterator().next(), filePath.toUri() ) );

      // Serialize the model file content to the file system
      AspectSerializer.INSTANCE.write( aspectModel.files().iterator().next() );

      final File writtenFile = filePath.toFile();
      assertThat( writtenFile ).content().contains( ":Aspect a samm:Aspect" );
   }

   @Test
   void testWriteAspectModelToFileSystem() {
      // Construct Aspect Model with two files from scratch
      final AspectModel aspectModel = new AspectModelLoader().emptyModel();
      final Path file1Path = outputDirectory.resolve( "Aspect1.ttl" );
      final AspectModelFile file1 = RawAspectModelFileBuilder.builder()
            .sourceLocation( Optional.of( file1Path.toUri() ) )
            .sourceModel( createModel( """
                  @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#> .
                  @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
                  @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .

                  :Aspect1 a samm:Aspect ;
                     samm:description "This is a test description"@en ;
                     samm:properties ( ) ;
                     samm:operations ( ) .
                  """
            ) )
            .build();
      final Path file2Path = outputDirectory.resolve( "Aspect2.ttl" );
      final AspectModelFile file2 = RawAspectModelFileBuilder.builder()
            .sourceLocation( Optional.of( file2Path.toUri() ) )
            .sourceModel( createModel( """
                  @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#> .
                  @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
                  @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .

                  :Aspect2 a samm:Aspect ;
                     samm:description "This is a test description"@en ;
                     samm:properties ( ) ;
                     samm:operations ( ) .
                  """
            ) )
            .build();
      final AspectChangeContext changeContext = new AspectChangeContext( aspectModel );
      changeContext.applyChange( new ChangeGroup(
            new AddAspectModelFile( file1 ),
            new AddAspectModelFile( file2 )
      ) );

      // Serialize the model to the file system
      AspectSerializer.INSTANCE.write( aspectModel );

      assertThat( file1Path.toFile() ).content().contains( ":Aspect1 a samm:Aspect" );
      assertThat( file2Path.toFile() ).content().contains( ":Aspect2 a samm:Aspect" );
   }
}
