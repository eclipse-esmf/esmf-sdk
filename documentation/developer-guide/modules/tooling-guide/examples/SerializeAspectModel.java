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

import static org.assertj.core.api.Assertions.assertThat;

// tag::imports[]
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.jena.rdf.model.Model;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.serializer.PrettyPrinter;
import org.eclipse.esmf.aspectmodel.serializer.RdfModelCreatorVisitor;
import org.eclipse.esmf.aspectmodel.vocabulary.Namespace;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
// end::imports[]
import org.junit.jupiter.api.Test;

public class SerializeAspectModel {
   @Test
   public void serializeAspectModel() {
      final File modelFile = new File( "aspect-models/io.openmanufacturing.examples.movement/1.0.0/Movement.ttl" );

      // tag::serialize[]
      // Aspect as created by the AspectModelLoader
      final Aspect aspect = // ...
      // end::serialize[]
            // exclude the actual loading from the example to reduce noise
            AspectModelResolver.loadAndResolveModel( modelFile ).flatMap( AspectModelLoader::getSingleAspect ).get();
      // tag::serialize[]

      // First step: Turn Java Aspect model into a Jena RDF model
      // We will use the Aspect's namespace as the model's default namespace.
      final Namespace aspectNamespace = () -> aspect.getAspectModelUrn().get().getUrnPrefix();
      final RdfModelCreatorVisitor rdfCreator = new RdfModelCreatorVisitor(
            aspect.getMetaModelVersion(), aspectNamespace );
      final Model model = rdfCreator.visitAspect( aspect, null ).getModel();

      // At this point, you can manipulate the RDF model, if required

      // Second step: Serialize RDF model into nicely formatted Turtle
      final StringWriter stringWriter = new StringWriter();
      final PrintWriter printWriter = new PrintWriter( stringWriter );
      final VersionedModel versionedModel = new SdsAspectMetaModelResourceResolver()
            .mergeMetaModelIntoRawModel( model, aspect.getMetaModelVersion() ).get();
      final PrettyPrinter prettyPrinter = new PrettyPrinter(
            versionedModel, aspect.getAspectModelUrn().get(), printWriter );
      prettyPrinter.print();
      printWriter.close();

      final String result = stringWriter.toString();
      // end::serialize[]
      assertThat( result ).contains( ":Movement a bamm:Aspect" );
      assertThat( result ).contains( ":isMoving a bamm:Property" );
   }
}
