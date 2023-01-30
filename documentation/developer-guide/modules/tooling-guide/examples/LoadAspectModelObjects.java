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
import io.openmanufacturing.sds.aspectmodel.resolver.AspectModelResolver;
import io.openmanufacturing.sds.metamodel.NamedElement;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import io.vavr.collection.Stream;
// end::imports[]
import org.junit.jupiter.api.Test;

public class LoadAspectModelObjects {
   @Test
   public void loadModel() {
      // tag::loadModel[]
      final File modelFile = new File( "aspect-models/io.openmanufacturing.examples.movement/1.0.0/Movement.ttl" );
      final List<String> result = AspectModelResolver.loadAndResolveModel( modelFile )
            .flatMap( AspectModelLoader::getElements )
            .toStream()
            .flatMap( Stream::ofAll )
            .filter( element -> element.is( NamedElement.class ) )
            .map( element -> element.as( NamedElement.class ) )
            .map( modelElement -> String.format( "Model element: %s has URN %s%n",
                  modelElement.getName(), modelElement.getAspectModelUrn() ) )
            .toJavaList();
      // end::loadModel[]
   }
}
