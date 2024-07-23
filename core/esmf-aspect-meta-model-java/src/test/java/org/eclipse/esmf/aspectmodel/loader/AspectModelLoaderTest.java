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

import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.Test;

class AspectModelLoaderTest {
   @Test
   void testOfAbstractEntityCyclomaticCreation() {
      final Map<String, ComplexType> entities = TestResources.load( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_SAME_EXTEND ).elements()
            .stream().filter( ComplexType.class::isInstance ).map( ComplexType.class::cast )
            .collect( Collectors.toMap( ComplexType::getName, Function.identity() ) );

      assertThat( entities ).extracting( "AbstractTestEntity" ).isInstanceOf( AbstractEntity.class );
      final AbstractEntity abstractEntity = (AbstractEntity) entities.get( "AbstractTestEntity" );
      assertThat( entities ).extracting( "testEntityOne" ).isInstanceOfSatisfying( ComplexType.class,
            type -> assertThat( type ).extracting( ComplexType::getExtends ).extracting( Optional::get ).isSameAs( abstractEntity ) );
      assertThat( entities ).extracting( "testEntityTwo" ).isInstanceOfSatisfying( ComplexType.class,
            type -> assertThat( type ).extracting( ComplexType::getExtends ).extracting( Optional::get ).isSameAs( abstractEntity ) );
   }

   @Test
   void testLoadAspectModelFromZipArchiveFile() throws URISyntaxException {
      final ClassLoader classLoader = getClass().getClassLoader();
      final Path archivePath = Paths.get( classLoader.getResource( "namespaces.zip" ).toURI() );
      final AspectModel aspectModel = new AspectModelLoader().loadNamespacePackage( new File( archivePath.toString() ) );

      assertThat( aspectModel.namespaces() ).hasSize( 2 );
      assertThat( aspectModel.namespaces().get( 0 ).getName() ).contains( "urn:samm:org.eclipse.examples:1.1.0" );
      assertThat( aspectModel.namespaces().get( 1 ).getName() ).contains( "urn:samm:org.eclipse.examples:1.0.0" );

      final List<String> aspectsNames = List.of( "Movement2", "Movement3", "Movement", "SimpleAspect" );

      assertThat( aspectModel.files() ).hasSize( 4 );
      assertThat( aspectModel.files() )
            .anySatisfy( aspectModelFile -> {
               assertThat( aspectsNames ).contains( aspectModelFile.aspect().getName() );
            } );
   }

   @Test
   void testLoadAspectModelFromZipArchiveInputStream() throws URISyntaxException, FileNotFoundException {
      final ClassLoader classLoader = getClass().getClassLoader();
      final Path archivePath = Paths.get( classLoader.getResource( "namespaces.zip" ).toURI() );
      final AspectModel aspectModel = new AspectModelLoader().loadNamespacePackage( new FileInputStream( archivePath.toString() ) );

      assertThat( aspectModel.namespaces() ).hasSize( 2 );
      assertThat( aspectModel.namespaces().get( 0 ).getName() ).contains( "urn:samm:org.eclipse.examples:1.1.0" );
      assertThat( aspectModel.namespaces().get( 1 ).getName() ).contains( "urn:samm:org.eclipse.examples:1.0.0" );

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
   void testLoadAspectModelFromZipArchive2_0_0() throws URISyntaxException, FileNotFoundException {
      final ClassLoader classLoader = getClass().getClassLoader();
      final Path archivePath = Paths.get( classLoader.getResource( "namespaces_with_old_version.zip" ).toURI() );
      final AspectModel aspectModel = new AspectModelLoader().loadNamespacePackage( new FileInputStream( archivePath.toString() ) );

      assertThat( aspectModel.namespaces() ).hasSize( 2 );
      assertThat( aspectModel.namespaces().get( 0 ).getName() ).contains( "urn:samm:org.eclipse.examples:1.1.0" );
      assertThat( aspectModel.namespaces().get( 1 ).getName() ).contains( "urn:samm:org.eclipse.examples:1.0.0" );

      final List<String> aspectsNames = List.of( "Movement2", "Movement3", "Movement4", "Movement", "SimpleAspect" );

      assertThat( aspectModel.files() ).hasSize( 5 );
      assertThat( aspectModel.files() )
            .anySatisfy( aspectModelFile -> {
               assertThat( aspectsNames ).contains( aspectModelFile.aspect().getName() );
            } );
   }
}