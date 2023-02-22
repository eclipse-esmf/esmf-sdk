/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.test;

import java.io.InputStream;

import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.aspectmodel.resolver.ClasspathStrategy;
import org.eclipse.esmf.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import io.vavr.control.Try;

public class TestResources {
   private TestResources() {
   }

   public static VersionedModel getModelWithoutResolution( final TestModel model, final KnownVersion knownVersion ) {
      final String baseDirectory = model instanceof InvalidTestAspect ? "invalid" : "valid";
      final SdsAspectMetaModelResourceResolver metaModelResourceResolver = new SdsAspectMetaModelResourceResolver();
      final String path = String.format( "%s/%s/%s/%s/%s.ttl", baseDirectory, knownVersion.toString().toLowerCase(),
            model.getUrn().getNamespace(), model.getUrn().getVersion(), model.getName() );
      final InputStream inputStream = TestResources.class.getClassLoader().getResourceAsStream( path );
      return TurtleLoader.loadTurtle( inputStream ).flatMap( rawModel ->
            metaModelResourceResolver
                  .mergeMetaModelIntoRawModel( rawModel, VersionNumber.parse( knownVersion.toVersionString() ) ) )
                         .get();
   }

   public static Try<VersionedModel> getModel( final TestModel model, final KnownVersion knownVersion ) {
      final String baseDirectory = model instanceof InvalidTestAspect ? "invalid" : "valid";
      final String modelsRoot = baseDirectory + "/" + knownVersion.toString().toLowerCase();
      return new AspectModelResolver().resolveAspectModel( new ClasspathStrategy( modelsRoot ), model.getUrn() );
   }

   public static Try<VersionedModel> getModel( final TestSharedModel model, final KnownVersion knownVersion ) {
      final String modelsRoot = "valid/" + knownVersion.toString().toLowerCase();
      return new AspectModelResolver().resolveAspectModel( new ClasspathStrategy( modelsRoot ), model.getUrn() );
   }
}
