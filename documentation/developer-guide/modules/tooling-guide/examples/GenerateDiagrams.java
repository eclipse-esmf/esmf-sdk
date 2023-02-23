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
import java.io.OutputStream;
import java.util.Locale;
import java.util.Set;
import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator.Format;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
// end::imports[]
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class GenerateDiagrams extends AbstractGenerator {
   @Test
   public void generateDiagram() throws IOException {
      // tag::generate[]
      // VersionedModel as returned by the AspectModelResolver
      final VersionedModel model = // ...
            // end::generate[]
            AspectModelResolver.loadAndResolveModel(
                  new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" ) ).get();
      // tag::generate[]

      final AspectModelDiagramGenerator generator = new AspectModelDiagramGenerator( model ); // <1>

      // Variant 1: Generate a diagram in SVG format using @en descriptions and preferredNames from the model
      final OutputStream output = outputStreamForName( "diagram.svg" );
      generator.generateDiagram( Format.SVG, Locale.ENGLISH, output ); // <2>
      output.close();

      // Variant 2: Generate diagrams in multiple formats, for all languages that are present in the model.
      generator.generateDiagrams( Set.of( Format.PNG, Format.SVG ), this::outputStreamForName ); // <3>
      // end::generate[]
   }
}
