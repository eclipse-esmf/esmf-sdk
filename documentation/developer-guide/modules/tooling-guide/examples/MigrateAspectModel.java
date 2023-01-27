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
import io.openmanufacturing.sds.aspectmodel.resolver.AspectModelResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.FileSystemStrategy;
import io.openmanufacturing.sds.aspectmodel.resolver.ResolutionStrategy;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.versionupdate.MigratorService;
import io.vavr.control.Try;
// end::imports[]
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class MigrateAspectModel {
   @Test
   public void migrate() {
      final ResolutionStrategy strategy = new FileSystemStrategy( Paths.get( "aspect-models" ) );
      final AspectModelUrn targetAspect
            = AspectModelUrn.fromUrn( "urn:bamm:io.openmanufacturing.examples.movement:1.0.0#Movement" );
      // tag::migrate[]
      // Try<VersionedModel> as returned by the AspectModelResolver
      final Try<VersionedModel> tryModel = // ...
            // end::migrate[]
            new AspectModelResolver().resolveAspectModel( strategy, targetAspect );
      // tag::migrate[]

      final Try<VersionedModel> tryMigratedModel = tryModel.flatMap( versionedModel ->
            new MigratorService().updateMetaModelVersion( versionedModel ) );
      final VersionedModel migratedModel = tryMigratedModel.getOrElseThrow( () ->
            new RuntimeException( tryMigratedModel.getCause() ) );
      // end::migrate[]
   }
}
