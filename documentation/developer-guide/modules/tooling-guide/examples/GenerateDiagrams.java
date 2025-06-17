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

import java.io.File;
import java.util.Locale;

// tag::imports[]
import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import org.eclipse.esmf.aspectmodel.generator.diagram.DiagramGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.diagram.DiagramGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.metamodel.AspectModel;
// end::imports[]

import org.junit.jupiter.api.Test;

public class GenerateDiagrams extends AbstractGenerator {
   @Test
   public void generateDiagram() {
      // tag::generate[]
      // AspectModel as returned by the AspectModelLoader
      final AspectModel aspectModel = // ...
            // end::generate[]
            new AspectModelLoader().load(
                  new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" ) );
      // tag::generate[]

      final DiagramGenerationConfig config = DiagramGenerationConfigBuilder.builder()
            .format( DiagramGenerationConfig.Format.SVG )
            .language( Locale.ENGLISH )
            .build();
      new AspectModelDiagramGenerator( aspectModel.aspect(), config ) // <1>
            .generate( this::outputStreamForName ); // <2>
      // end::generate[]
   }
}
