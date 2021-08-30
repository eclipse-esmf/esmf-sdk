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

package io.openmanufacturing.sds.metamodel.loader;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.VersionNumber;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.versionupdate.MigratorService;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Base;
import io.openmanufacturing.sds.metamodel.IsDescribed;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestResources;
import io.vavr.control.Try;

public abstract class MetaModelInstantiatorTest extends MetaModelVersions {

   Aspect loadAspect( final TestAspect aspectName, final KnownVersion version ) {
      final VersionedModel versionedModel = TestResources.getModel( aspectName, version ).get();

      final MigratorService migratorService = new MigratorService();

      final Try<Aspect> aspect = migratorService
            .updateMetaModelVersion( versionedModel )
            .flatMap( AspectModelLoader::fromVersionedModel );
      return aspect.getOrElseThrow( () -> new RuntimeException( aspect.getCause() ) );
   }

   <T extends Base & IsDescribed> void assertBaseAttributes( final T base,
         final AspectModelUrn expectedAspectModelUrn,
         final String expectedName, final String expectedPreferredName, final String expectedDescription,
         final String... expectedSee ) {

      assertThat( base.getName() ).isEqualTo( expectedName );
      assertThat( base.getAspectModelUrn().get() ).isEqualTo( expectedAspectModelUrn );
      assertBaseAttributes( base, expectedPreferredName, expectedDescription, expectedSee );
   }

   <T extends Base & IsDescribed> void assertBaseAttributes( final T base,
         final String expectedPreferredName, final String expectedDescription,
         final String... expectedSee ) {

      assertThat( base.getPreferredName( Locale.ENGLISH ) ).isEqualTo( expectedPreferredName );
      assertThat( base.getDescription( Locale.ENGLISH ) ).isEqualTo( expectedDescription );
      assertThat( base.getSee() ).containsOnly( expectedSee );
      assertThat( base.getMetaModelVersion() ).isEqualTo( KnownVersion.getLatest() );
   }
}
