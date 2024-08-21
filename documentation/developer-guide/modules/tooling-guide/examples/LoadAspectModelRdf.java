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
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import org.apache.jena.vocabulary.RDF;
// end::imports[]

import org.junit.jupiter.api.Test;

public class LoadAspectModelRdf {
   @Test
   public void loadAndResolveFromFile() {
      // tag::loadAndResolveFromFile[]
      final AspectModel aspectModel = new AspectModelLoader().load(
            // a File, InputStream or AspectModelUrn
            // end::loadAndResolveFromFile[]
            new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" )
            // tag::loadAndResolveFromFile[]
      );

      // Do something with the Aspect Model on the RDF level.
      // Example: List the URNs of all samm:Entitys
      aspectModel.mergedModel().listStatements( null, RDF.type, SammNs.SAMM.Entity() )
            .forEachRemaining( statement -> System.out.println( statement.getSubject().getURI() ) );
      // end::loadAndResolveFromFile[]
   }

   @Test
   public void loadAndResolveFromUrn() {
      // tag::loadAndResolveFromUrn[]
      // The directory containing the models folder structure,
      // see [.underline]#xref:tooling-guide:samm-cli.adoc#models-directory-structure[models directory structure]#
      final Path modelsRoot = Paths.get( "aspect-models" );
      final ResolutionStrategy fileSystemStrategy = new FileSystemStrategy( modelsRoot );
      final AspectModelUrn urn = AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.examples.movement:1.0.0#Movement" );
      final AspectModel result = new AspectModelLoader( fileSystemStrategy ).load( urn );
      // end::loadAndResolveFromUrn[]
   }
}
