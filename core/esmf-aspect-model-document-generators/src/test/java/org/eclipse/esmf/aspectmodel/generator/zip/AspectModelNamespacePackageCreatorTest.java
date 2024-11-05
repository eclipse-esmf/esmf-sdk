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

package org.eclipse.esmf.aspectmodel.generator.zip;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AspectModelNamespacePackageCreatorTest {
   Path outputDirectory = null;

   @BeforeEach
   void beforeEach() throws IOException {
      outputDirectory = Files.createTempDirectory( "junit" );
   }

   @Test
   void testAspectModelArchiveGeneration() throws IOException {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY );
      final String outputFileName = String.format( "%s/%s", outputDirectory.toString(), "/test_zip.zip" );

      AspectModelNamespacePackageCreator.INSTANCE.accept( aspectModel, new FileOutputStream( Paths.get( outputFileName ).toFile() ), "" );

      assertThat( new File( outputFileName ) ).exists();

      final AspectModel aspectModelResult = new AspectModelLoader().loadNamespacePackage( new File( outputFileName ) );
      assertThat( aspectModelResult.namespaces() ).hasSize( 1 );
      assertThat( aspectModelResult.files() ).hasSize( 1 );
      assertThat( aspectModelResult.files().get( 0 ).aspect() ).isEqualTo( aspectModel.aspect() );
   }
}
