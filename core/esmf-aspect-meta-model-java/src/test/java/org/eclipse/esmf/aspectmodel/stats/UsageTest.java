/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.stats;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.ClasspathStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.test.TestModel;

import org.junit.jupiter.api.Test;

public class UsageTest {
   @Test
   void testUsage() {
      final ResolutionStrategy strategy = new ClasspathStrategy( "valid" );
      final AspectModelLoader aspectModelLoader = new AspectModelLoader( strategy );

      final Usage usage = new Usage( aspectModelLoader );
      assertThat( usage.referencesTo( AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "testProperty" ) ) )
            .map( Reference::pointerSource )
            .map( file -> file.sourceLocation().get().toString() )
            .anyMatch( location -> location.substring( location.lastIndexOf( '/' ) + 1 ).equals( "AspectWithProperty.ttl" ) );
   }
}
