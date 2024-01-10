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

package examples;

// tag::imports[]

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.esmf.aspectmodel.aas.AasFileFormat;
import org.eclipse.esmf.aspectmodel.aas.AasToAspectModelGenerator;
import org.eclipse.esmf.aspectmodel.aas.AspectModelAASGenerator;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

public class GenerateAspectFromAas extends AbstractGenerator {
   private OutputStream outputStream( final Path directory, final String aspectName ) {
      try {
         return new FileOutputStream( directory.resolve( aspectName + ".aasx" ).toFile() );
      } catch ( final FileNotFoundException exception ) {
         throw new RuntimeException( exception );
      }
   }

   @Test
   public void generate() throws IOException {
      final Path outputDirectory = Files.createTempDirectory( "junit" );
      new AspectModelAASGenerator().generate( AasFileFormat.AASX,
            AspectModelResolver.loadAndResolveModel( new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" ) )
                  .flatMap( AspectModelLoader::getSingleAspect ).get(),
            name -> outputStream( outputDirectory, name ) );

      // tag::generate[]
      final File file = // an AAS file that ends in .aasx, .xml or .json
            // end::generate[]
            outputDirectory.resolve( "Movement.aasx" ).toFile();
      // tag::generate[]

      // Multiple "from" methods are available: fromFile (which checks the file extension),
      // fromAasJson, fromAasXml, fromAasx as well as fromEnvironment (for an AAS4J AAS environment)
      final AasToAspectModelGenerator generator = AasToAspectModelGenerator.fromFile( file );
      final List<Aspect> aspects = generator.generateAspects();
      aspects.forEach( aspect -> {
         // do something with the generated aspect
      } );
      // end::generate[]

      FileUtils.deleteDirectory( outputDirectory.toFile() );
   }
}
