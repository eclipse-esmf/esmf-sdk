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

package org.eclipse.esmf.test;

import java.io.InputStream;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.ClasspathStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.samm.KnownVersion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.vavr.control.Try;

public class TestResources {
   public static AspectModel load( final InvalidTestAspect model ) {
      final String path = String.format( "invalid/%s/%s/%s/%s.ttl", KnownVersion.getLatest().toString().toLowerCase(),
            model.getUrn().getNamespace(), model.getUrn().getVersion(), model.getName() );
      final InputStream inputStream = TestResources.class.getClassLoader().getResourceAsStream( path );
      final ResolutionStrategy testModelsResolutionStrategy = new ClasspathStrategy(
            "invalid/" + KnownVersion.getLatest().toString().toLowerCase() );
      return new AspectModelLoader( testModelsResolutionStrategy ).load( inputStream );
   }

   public static AspectModel load( final TestModel model ) {
      return load( model, KnownVersion.getLatest() );
   }

   public static AspectModel load( final TestModel model, final KnownVersion metaModelVersion ) {
      final String path = String.format( "valid/%s/%s/%s/%s.ttl", metaModelVersion.toString().toLowerCase(),
            model.getUrn().getNamespace(), model.getUrn().getVersion(), model.getName() );
      final InputStream inputStream = TestResources.class.getClassLoader().getResourceAsStream( path );
      final ResolutionStrategy testModelsResolutionStrategy = new ClasspathStrategy( "valid/" + metaModelVersion.toString().toLowerCase() );
      return new AspectModelLoader( testModelsResolutionStrategy ).load( inputStream );
   }

   public static Try<JsonNode> loadPayload( final TestModel model ) {
      final String baseDirectory = "payloads/" + (model instanceof InvalidTestAspect ? "invalid" : "valid");
      final String modelsRoot = baseDirectory + "/" + KnownVersion.getLatest().toString().toLowerCase();
      return Try.of( () -> new ObjectMapper().readTree( Resources.getResource( modelsRoot + "/" + model.getName() + ".json" ) ) );
   }
}
