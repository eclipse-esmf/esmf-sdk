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
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.vocabulary.BAMM;
import io.vavr.control.Try;
// end::imports[]
import org.junit.jupiter.api.Test;

public class LoadAspectModelRdf {
   @Test
   public void loadAndResolveFromFile() {
      // tag::loadAndResolveFromFile[]
      final File modelFile = new File( "aspect-models/io.openmanufacturing.examples.movement/1.0.0/Movement.ttl" ); // <1>
      final Try<VersionedModel> tryModel = AspectModelResolver.loadAndResolveModel( modelFile ); // <2>

      // Let's do something with the loaded model on RDF level
      tryModel.forEach( versionedModel -> { // <3>
         final BAMM bamm = new BAMM( KnownVersion.fromVersionString( versionedModel.getMetaModelVersion().toString() ).get() );
         final Model rdfModel = versionedModel.getModel();
         final List<Statement> result = rdfModel.listStatements( null, RDF.type, bamm.Aspect() ).toList();// <4>
      } );
      // end::loadAndResolveFromFile[]
   }

   @Test
   public void loadAndResolveFromUrn() {
      // tag::loadAndResolveFromUrn[]
      // The directory containing the models folder structure,
      // see [.underline]#xref:tooling-guide:bamm-cli.adoc#models-directory-structure[models directory structure]#
      final Path modelsRoot = Paths.get( "aspect-models" );
      final ResolutionStrategy fileSystemStrategy = new FileSystemStrategy( modelsRoot );
      final Try<VersionedModel> tryModel = new AspectModelResolver().resolveAspectModel( fileSystemStrategy,
            AspectModelUrn.fromUrn( "urn:bamm:io.openmanufacturing.examples.movement:1.0.0#Movement" ) );
      // end::loadAndResolveFromUrn[]
   }
}
