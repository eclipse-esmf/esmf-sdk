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

package org.eclipse.esmf.aspectmodel.loader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.HasDescription;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.Test;

class AspectModelLoaderTest {
   @Test
   void testOfAbstractEntityCyclomaticCreation() {
      final Map<String, ComplexType> entities =
            TestResources.load( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_SAME_EXTEND ).elements().stream()
                  .filter( ComplexType.class::isInstance )
                  .map( ComplexType.class::cast )
                  .collect( Collectors.toMap( ComplexType::getName, Function.identity() ) );

      assertThat( entities ).extracting( "AbstractTestEntity" ).isInstanceOf( AbstractEntity.class );
      final AbstractEntity abstractEntity = (AbstractEntity) entities.get( "AbstractTestEntity" );
      assertThat( entities ).extracting( "testEntityOne" ).isInstanceOfSatisfying( ComplexType.class, type ->
            assertThat( type ).extracting( ComplexType::getExtends ).extracting( Optional::get ).isSameAs( abstractEntity ) );
      assertThat( entities ).extracting( "testEntityTwo" ).isInstanceOfSatisfying( ComplexType.class, type ->
            assertThat( type ).extracting( ComplexType::getExtends ).extracting( Optional::get ).isSameAs( abstractEntity ) );
   }

   @Test
   void testLoadAspectModelFromZipArchiveFile() {
      final Path archivePath = getPackage( "namespaces.zip" );
      final AspectModel aspectModel = new AspectModelLoader().loadNamespacePackage( new File( archivePath.toString() ) );

      assertThat( aspectModel.namespaces() ).hasSize( 2 );
      assertThat( aspectModel.namespaces() ).map( HasDescription::getName ).contains( "urn:samm:org.eclipse.examples:1.1.0" );
      assertThat( aspectModel.namespaces() ).map( HasDescription::getName ).contains( "urn:samm:org.eclipse.examples:1.0.0" );

      final List<String> aspectsNames = List.of( "Movement2", "Movement3", "Movement", "SimpleAspect" );

      assertThat( aspectModel.files() ).hasSize( 4 );
      assertThat( aspectModel.files() )
            .anySatisfy( aspectModelFile -> {
               assertThat( aspectsNames ).contains( aspectModelFile.aspect().getName() );
            } );
   }

   @Test
   void testLoadAspectModelFromZipArchiveInputStream() throws FileNotFoundException {
      final Path archivePath = getPackage( "namespaces.zip" );
      final AspectModel aspectModel = new AspectModelLoader().loadNamespacePackage( new FileInputStream( archivePath.toString() ) );

      assertThat( aspectModel.namespaces() ).hasSize( 2 );
      assertThat( aspectModel.namespaces() ).map( HasDescription::getName ).contains( "urn:samm:org.eclipse.examples:1.1.0" );
      assertThat( aspectModel.namespaces() ).map( HasDescription::getName ).contains( "urn:samm:org.eclipse.examples:1.0.0" );

      final List<String> aspectsNames = List.of( "Movement2", "Movement3", "Movement", "SimpleAspect" );

      assertThat( aspectModel.files() ).hasSize( 4 );
      assertThat( aspectModel.files() )
            .anySatisfy( aspectModelFile -> {
               assertThat( aspectsNames ).contains( aspectModelFile.aspect().getName() );
            } );
   }

   /**
    * Test migration to the latest version of Aspect Model in Archive
    */
   @Test
   void testLoadAspectModelFromZipArchive2_0_0() throws FileNotFoundException {
      final Path archivePath = getPackage( "namespaces_with_old_version.zip" );
      final AspectModel aspectModel = new AspectModelLoader().loadNamespacePackage( new FileInputStream( archivePath.toString() ) );

      assertThat( aspectModel.namespaces() ).hasSize( 2 );
      assertThat( aspectModel.namespaces() ).map( HasDescription::getName ).contains( "urn:samm:org.eclipse.examples:1.1.0" );
      assertThat( aspectModel.namespaces() ).map( HasDescription::getName ).contains( "urn:samm:org.eclipse.examples:1.0.0" );

      final List<String> aspectsNames = List.of( "Movement2", "Movement3", "Movement4", "Movement", "SimpleAspect" );

      assertThat( aspectModel.files() ).hasSize( 5 );
      assertThat( aspectModel.files() )
            .anySatisfy( aspectModelFile -> {
               assertThat( aspectsNames ).contains( aspectModelFile.aspect().getName() );
            } );
   }

   @Test
   void testLoadAspectModelFromZipArchiveAspectModelsRoot() throws FileNotFoundException {
      final Path archivePath = getPackage( "namespaces-aspect-models-root.zip" );
      final AspectModel aspectModel = new AspectModelLoader().loadNamespacePackage( new FileInputStream( archivePath.toString() ) );

      assertThat( aspectModel.namespaces() ).hasSize( 2 );
      assertThat( aspectModel.namespaces() ).map( HasDescription::getName ).contains( "urn:samm:org.eclipse.examples:1.1.0" );
      assertThat( aspectModel.namespaces() ).map( HasDescription::getName ).contains( "urn:samm:org.eclipse.examples:1.0.0" );

      final List<String> aspectsNames = List.of( "Movement2", "Movement3", "Movement", "SimpleAspect" );

      assertThat( aspectModel.files() ).hasSize( 4 );
      assertThat( aspectModel.files() )
            .anySatisfy( aspectModelFile -> {
               assertThat( aspectsNames ).contains( aspectModelFile.aspect().getName() );
            } );
   }

   @Test
   void testLoadAspectModelFromZipArchiveAspectModelsSubfolder() throws FileNotFoundException {
      final Path archivePath = getPackage( "namespaces-aspect-models-subfolder.zip" );
      final AspectModel aspectModel = new AspectModelLoader().loadNamespacePackage( new FileInputStream( archivePath.toString() ) );

      assertThat( aspectModel.namespaces() ).hasSize( 2 );
      assertThat( aspectModel.namespaces() ).map( HasDescription::getName ).contains( "urn:samm:org.eclipse.examples:1.1.0" );
      assertThat( aspectModel.namespaces() ).map( HasDescription::getName ).contains( "urn:samm:org.eclipse.examples:1.0.0" );

      final List<String> aspectsNames = List.of( "Movement2", "Movement3", "Movement", "SimpleAspect" );

      assertThat( aspectModel.files() ).hasSize( 4 );
      assertThat( aspectModel.files() )
            .anySatisfy( aspectModelFile -> {
               assertThat( aspectsNames ).contains( aspectModelFile.aspect().getName() );
            } );
   }

   @Test
   void testLoadAspectModelFromZipArchiveWithSharedProperty() throws FileNotFoundException, URISyntaxException {
      final Path archivePath = getPackage( "namespace-with-shared-property.zip" );

      final File aspectModelsRootDirectory = new File(
            AspectModelLoaderTest.class.getClassLoader()
                  .getResource( KnownVersion.getLatest().toString().toLowerCase() )
                  .toURI().getPath() );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );

      final AspectModel aspectModel = new AspectModelLoader( urnStrategy ).loadNamespacePackage(
            new FileInputStream( archivePath.toString() ) );

      assertThat( aspectModel.namespaces() ).hasSize( 1 );
      assertThat( aspectModel.namespaces().get( 0 ).getName() ).contains( "urn:samm:org.eclipse.esmf.test:1.0.0" );

      final List<String> aspectsNames = List.of( "Movement" );

      assertThat( aspectModel.files() ).hasSize( 2 );
      assertThat( aspectModel.files() )
            .anySatisfy( aspectModelFile -> {
               if ( !aspectModelFile.aspects().isEmpty() ) {
                  assertThat( aspectsNames ).contains( aspectModelFile.aspect().getName() );
               }
            } );
   }

   @Test
   void testMergeAspectModels() {
      final AspectModel a1 = TestResources.load( TestAspect.ASPECT );
      final AspectModel a2 = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY );
      assertThat( a1.aspects() ).hasSize( 1 );
      assertThat( a2.aspects() ).hasSize( 1 );
      final AspectModel merged = new AspectModelLoader().merge( a1, a2 );
      assertThat( merged.aspects() ).hasSize( 2 );
      assertThat( merged.elements().size() ).isEqualTo( a1.elements().size() + a2.elements().size() );
   }

   /**
    * Returns the File object for a test model file
    */
   private Path getPackage( final String packageName ) {
      return Paths.get( String.format(
            "%s/../../core/esmf-test-aspect-models/src/main/resources/packages/%s",
            System.getProperty( "user.dir" ), packageName ) );
   }
}