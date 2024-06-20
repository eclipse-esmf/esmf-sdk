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
import java.util.List;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import io.vavr.collection.Stream;
// end::imports[]
import org.junit.jupiter.api.Test;

public class LoadAspectModelObjects {
   @Test
   public void loadModel() {
      // tag::loadModel[]
      final File modelFile = new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" );
      final List<String> result = AspectModelResolver.loadAndResolveModel( modelFile )
            .flatMap( AspectModelLoader::getElements )
            .toStream()
            .flatMap( Stream::ofAll )
            .map( modelElement -> String.format( "Model element: %s has URN %s%n",
                  modelElement.getName(), modelElement.urn() ) )
            .toJavaList();
      // end::loadModel[]
   }
}
