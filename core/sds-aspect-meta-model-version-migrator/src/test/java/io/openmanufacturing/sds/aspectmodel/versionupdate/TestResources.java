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

package io.openmanufacturing.sds.aspectmodel.versionupdate;

import java.io.InputStream;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.VersionNumber;
import io.openmanufacturing.sds.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.services.TurtleLoader;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.test.TestAspect;

public class TestResources {
   public static VersionedModel getModelWithoutResolution( final TestAspect model, final KnownVersion knownVersion ) {
      final SdsAspectMetaModelResourceResolver metaModelResourceResolver = new SdsAspectMetaModelResourceResolver();
      final String path = String.format( "valid/%s/%s/%s/%s.ttl", knownVersion.toString().toLowerCase(),
            model.getUrn().getNamespace(), model.getUrn().getVersion(), model.getName() );
      final InputStream inputStream = TestResources.class.getClassLoader().getResourceAsStream( path );
      return TurtleLoader.loadTurtle( inputStream ).flatMap( rawModel ->
            metaModelResourceResolver.mergeMetaModelIntoRawModel( rawModel, VersionNumber.parse( knownVersion.toVersionString() ) ) ).get();
   }
}
