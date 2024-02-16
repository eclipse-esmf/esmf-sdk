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

package examples;

// tag::imports[]
import java.io.File;
import java.io.IOException;

import org.eclipse.esmf.aspectmodel.aas.AspectModelAasGenerator;
import org.eclipse.esmf.aspectmodel.aas.AasFileFormat;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
// end::imports[]

import org.junit.jupiter.api.Test;

public class GenerateAas extends AbstractGenerator {
   @Test
   public void generate() throws IOException {
      final File modelFile = new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" );

      // tag::generate[]
      // Aspect as created by the AspectModelLoader
      final Aspect aspect = // ...
            // end::generate[]
            // exclude the actual loading from the example to reduce noise
            AspectModelResolver.loadAndResolveModel( modelFile ).flatMap( AspectModelLoader::getSingleAspect ).get();
      // tag::generate[]

      final AspectModelAasGenerator generator = new AspectModelAasGenerator();

      // Generate AAS .aasx for input Aspect
      generator.generate( AasFileFormat.AASX, aspect, this::outputStreamForName );
      // Generate AAS .xml for input Aspect
      generator.generate( AasFileFormat.XML, aspect, this::outputStreamForName );
      // Generate AAS .json for input Aspect
      generator.generate( AasFileFormat.JSON, aspect, this::outputStreamForName );
      // end::generate[]
   }
}
