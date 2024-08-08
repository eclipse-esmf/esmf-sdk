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

import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.metamodel.AspectModel;
// end::imports[]

import org.junit.jupiter.api.Test;

public class SerializeAspectModel {
   @Test
   public void serializeAspectModel() {
      // tag::serialize[]
      // AspectModel as returned by the AspectModelLoader
      final AspectModel aspectModel = // ...
            // end::generate[]
            new AspectModelLoader().load(
                  new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" ) );
      // tag::serialize[]

      // A String that contains the pretty printed Aspect Model
      final String aspectString =
          AspectSerializer.INSTANCE.aspectToString( aspectModel.aspect() );
      // end::serialize[]
      assertThat( aspectString ).contains( ":Movement a samm:Aspect" );
      assertThat( aspectString ).contains( ":isMoving a samm:Property" );
   }
}
