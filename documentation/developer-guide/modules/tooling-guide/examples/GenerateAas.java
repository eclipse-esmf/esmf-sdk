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
import org.eclipse.esmf.aspectmodel.aas.AasGenerationConfig;
import org.eclipse.esmf.aspectmodel.aas.AasGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.aas.AspectModelAasGenerator;
import org.eclipse.esmf.aspectmodel.aas.AasFileFormat;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
// end::imports[]

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

public class GenerateAas extends AbstractGenerator {
   @Test
   public void generate() {
      // tag::generate[]
      // AspectModel as returned by the AspectModelLoader
      final AspectModel aspectModel = // ...
            // end::generate[]
            new AspectModelLoader().load(
                  new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" ) );
      final Aspect aspect = aspectModel.aspect();
      // tag::generate[]

      final AasGenerationConfig config = AasGenerationConfigBuilder.builder()
            .format( AasFileFormat.AASX )
            .aspectData( null ) // Optional: Provide a JsonNode representing Aspect data
            .propertyMappers( List.of() ) // Optional: Customize SAMM->AAS property mapping
            .build();
      new AspectModelAasGenerator( aspect, config ).generate( this::outputStreamForName );
      // end::generate[]
   }
}
