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
import java.util.Map;

import org.eclipse.esmf.aspectmodel.generator.docu.AspectModelDocumentationGenerator;
import org.eclipse.esmf.aspectmodel.generator.docu.AspectModelDocumentationGenerator.HtmlGenerationOption;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.metamodel.AspectModel;
// end:imports[]

import org.junit.jupiter.api.Test;

public class GenerateHtml extends AbstractGenerator {
   @Test
   public void generate() throws IOException {
      // tag::generate[]
      // AspectModel as returned by the AspectModelLoader
      final AspectModel aspectModel = // ...
            // end::generate[]
            new AspectModelLoader().load(
                  new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" ) );
      // tag::generate[]
      final AspectModelDocumentationGenerator generator = // <1>
            new AspectModelDocumentationGenerator( aspectModel.aspect() );

      final Map<HtmlGenerationOption, String> options = Map.of(); // <2>
      generator.generate( this::outputStreamForName, options );
      // end::generate[]
   }
}
