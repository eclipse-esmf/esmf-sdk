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
import org.eclipse.esmf.aspectmodel.generator.docu.AspectModelDocumentationGenerator;
import org.eclipse.esmf.aspectmodel.generator.docu.DocumentationGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.docu.DocumentationGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.metamodel.AspectModel;
// end::imports[]

import java.io.File;
import org.junit.jupiter.api.Test;

public class GenerateHtml extends AbstractGenerator {
   @Test
   public void generate() {
      // tag::generate[]
      // AspectModel as returned by the AspectModelLoader
      final AspectModel aspectModel = // ...
            // end::generate[]
            new AspectModelLoader().load(
                  new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" ) );
      // tag::generate[]
      final DocumentationGenerationConfig config = DocumentationGenerationConfigBuilder.builder() // <1>
            /* .stylesheet( customCss ) if required */
            .build();
      final AspectModelDocumentationGenerator generator = // <2>
            new AspectModelDocumentationGenerator( aspectModel.aspect(), config );
      generator.generate( this::outputStreamForName );
      // end::generate[]
   }
}
