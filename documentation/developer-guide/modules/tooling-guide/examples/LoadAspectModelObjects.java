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

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ModelElement;
// end::imports[]

import org.junit.jupiter.api.Test;

public class LoadAspectModelObjects {
   @Test
   public void loadModel() {
      // tag::loadModel[]
      final AspectModel aspectModel = new AspectModelLoader().load(
            // a File, InputStream or AspectModelUrn
            // end::generate[]
            new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" )
            // tag::loadModel[]
      );

      // Do something with the elements
      for ( final ModelElement element : aspectModel.elements() ) {
         System.out.printf( "Model element: %s has URN %s%n", element.getName(), element.urn() );
      }

      // Directly work with the Aspect, if the AspectModel happens to contain
      // exactly one Aspect. If there are 0 or >1 Aspects, this will throw an exception.
      System.out.println( "Aspect URN: " + aspectModel.aspect().urn() );
      // end::loadModel[]
   }
}
