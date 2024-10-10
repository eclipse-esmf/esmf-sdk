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

package org.eclipse.esmf.test;

import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.ClasspathStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.samm.KnownVersion;

public class TestResources {
   public static AspectModel load( final TestAspect model ) {
      final KnownVersion metaModelVersion = KnownVersion.getLatest();
      final String path = String.format( "valid/%s/%s/%s.ttl", model.getUrn().getNamespaceMainPart(), model.getUrn().getVersion(),
            model.getName() );
      final InputStream inputStream = TestResources.class.getClassLoader().getResourceAsStream( path );
      final ResolutionStrategy testModelsResolutionStrategy = new ClasspathStrategy(
            "valid/" + metaModelVersion.toString().toLowerCase() );
      return new AspectModelLoader( testModelsResolutionStrategy ).load( inputStream, Optional.of( URI.create( "testmodel:" + path ) ) );
   }

   public static AspectModel load( final InvalidTestAspect model ) {
      final KnownVersion metaModelVersion = KnownVersion.getLatest();
      final String path = String.format( "invalid/%s/%s/%s.ttl", model.getUrn().getNamespaceMainPart(), model.getUrn().getVersion(),
            model.getName() );
      final InputStream inputStream = TestResources.class.getClassLoader().getResourceAsStream( path );
      final ResolutionStrategy testModelsResolutionStrategy = new ClasspathStrategy(
            "invalid/" + metaModelVersion.toString().toLowerCase() );
      return new AspectModelLoader( testModelsResolutionStrategy ).load( inputStream, Optional.of( URI.create( "testmodel:" + path ) ) );
   }
}
