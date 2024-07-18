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

package org.eclipse.esmf.aspectmodel.generator;

import java.io.IOException;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.Test;

public class AspectModelZipGeneratorTest {

   @Test
   public void testAspectModelZipGeneration() throws IOException {
//      final AspectModelFile aspectModelFile_1 = AspectModelFileLoader.load(  )
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_SAME_EXTEND );

      AspectModelZipGenerator.generateZipAspectModelArchive( aspectModel, "/Users/Yauheni_Kapliarchuk/Downloads/testzip.zip" );
   }
}
