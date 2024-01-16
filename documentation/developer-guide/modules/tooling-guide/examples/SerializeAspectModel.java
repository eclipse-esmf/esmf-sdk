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

import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
// end::imports[]

import org.junit.jupiter.api.Test;

public class SerializeAspectModel {
   @Test
   public void serializeAspectModel() {
      final File modelFile = new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" );

      // tag::serialize[]
      // Aspect as created by the AspectModelLoader
      final Aspect aspect = // ...
      // end::serialize[]
            // exclude the actual loading from the example to reduce noise
            AspectModelResolver.loadAndResolveModel( modelFile ).flatMap( AspectModelLoader::getSingleAspect ).get();
      // tag::serialize[]

      // A String that contains the pretty printed Aspect Model
      String aspectString = AspectSerializer.INSTANCE.apply( aspect );
      // end::serialize[]
      assertThat( aspectString ).contains( ":Movement a samm:Aspect" );
      assertThat( aspectString ).contains( ":isMoving a samm:Property" );
   }
}
