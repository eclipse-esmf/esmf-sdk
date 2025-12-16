/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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
import java.util.Collection;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.ClasspathStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.validation.Validator;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.samm.KnownVersion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.vavr.control.Either;
import io.vavr.control.Try;

public class TestResources {
   public record IdentifiedInputStream(
         URI location,
         InputStream inputStream
   ) {}

   public static IdentifiedInputStream inputStream( final InvalidTestAspect model ) {
      final String path = String.format( "invalid/%s/%s/%s.ttl", model.getUrn().getNamespaceMainPart(), model.getUrn().getVersion(),
            model.getName() );
      final InputStream inputStream = TestResources.class.getClassLoader().getResourceAsStream( path );
      return new IdentifiedInputStream( URI.create( "testmodel:" + path ), inputStream );
   }

   public static IdentifiedInputStream inputStream( final TestModel model ) {
      final String path = String.format( "valid/%s/%s/%s.ttl", model.getUrn().getNamespaceMainPart(), model.getUrn().getVersion(),
            model.getName() );
      final InputStream inputStream = TestResources.class.getClassLoader().getResourceAsStream( path );
      return new IdentifiedInputStream( URI.create( "testmodel:" + path ), inputStream );
   }

   public static AspectModel load( final InvalidTestAspect model ) {
      final IdentifiedInputStream inputStream = inputStream( model );
      final ResolutionStrategy testModelsResolutionStrategy = new ClasspathStrategy(
            "invalid/" + KnownVersion.getLatest().toString().toLowerCase() );
      return new AspectModelLoader( testModelsResolutionStrategy ).load( inputStream.inputStream(), inputStream.location() );
   }

   public static <P, C extends Collection<P>> Either<C, AspectModel> loadWithValidation( final InvalidTestAspect model,
         final Validator<P, C> validator ) {
      final IdentifiedInputStream inputStream = inputStream( model );
      final ResolutionStrategy testModelsResolutionStrategy = new ClasspathStrategy(
            "invalid/" + KnownVersion.getLatest().toString().toLowerCase() );
      return new AspectModelLoader( testModelsResolutionStrategy )
            .withValidation( validator )
            .load( inputStream.inputStream(), inputStream.location() );
   }

   public static AspectModel load( final TestModel model ) {
      final ResolutionStrategy testModelsResolutionStrategy = new ClasspathStrategy( "valid" );
      final IdentifiedInputStream inputStream = inputStream( model );
      return new AspectModelLoader( testModelsResolutionStrategy ).load( inputStream.inputStream(), inputStream.location() );
   }

   public static <P, C extends Collection<P>> Either<C, AspectModel> loadWithValidation( final TestModel model,
         final Validator<P, C> validator ) {
      final ResolutionStrategy testModelsResolutionStrategy = new ClasspathStrategy( "valid" );
      final IdentifiedInputStream inputStream = inputStream( model );
      return new AspectModelLoader( testModelsResolutionStrategy )
            .withValidation( validator )
            .load( inputStream.inputStream(), inputStream.location() );
   }

   public static Try<JsonNode> loadPayload( final TestModel model ) {
      final String modelsRoot = "payloads";
      return Try.of( () -> new ObjectMapper().readTree( Resources.getResource( modelsRoot + "/" + model.getName() + ".json" ) ) );
   }
}
