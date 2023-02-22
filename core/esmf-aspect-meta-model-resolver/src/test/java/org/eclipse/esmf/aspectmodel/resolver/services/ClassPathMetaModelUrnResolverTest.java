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

package org.eclipse.esmf.aspectmodel.resolver.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.test.MetaModelVersions;

public class ClassPathMetaModelUrnResolverTest extends MetaModelVersions {
   @Test
   public void testUrlConstruction() {
      for ( final KnownVersion version : KnownVersion.getVersions() ) {
         final Optional<URL> url = MetaModelUrls.url( "meta-model", version, "aspect-meta-model-definitions.ttl" );
         assertThat( url ).isNotEmpty();
      }
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testContainsEntityDefinitions( final KnownVersion metaModelVersion ) {
      assertThat( MetaModelUrls.url( "entity", metaModelVersion, "Point3d.ttl" ) ).isNotEmpty();
      assertThat( MetaModelUrls.url( "entity", metaModelVersion, "FileResource.ttl" ) ).isNotEmpty();
      assertThat( MetaModelUrls.url( "entity", metaModelVersion, "TimeSeriesEntity.ttl" ) ).isNotEmpty();
   }
}
