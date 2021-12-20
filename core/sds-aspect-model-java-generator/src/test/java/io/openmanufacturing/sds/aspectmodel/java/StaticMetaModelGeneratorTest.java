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

package io.openmanufacturing.sds.aspectmodel.java;

import java.util.Collection;
import java.util.List;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.java.metamodel.StaticMetaModelJavaGenerator;
import io.openmanufacturing.sds.aspectmodel.java.pojo.AspectModelJavaGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestResources;

abstract class StaticMetaModelGeneratorTest extends MetaModelVersions {

   Collection<JavaGenerator> getGenerators( final TestAspect aspect, final KnownVersion version, final boolean executeLibraryMacros,
         final String templateLibPath, final String templateLibFileName ) {
      final VersionedModel model = TestResources.getModel( aspect, version ).get();
      final JavaGenerator pojoGenerator = new AspectModelJavaGenerator( model, false, executeLibraryMacros, templateLibPath,
            templateLibFileName );
      final JavaGenerator staticGenerator = new StaticMetaModelJavaGenerator( model, executeLibraryMacros, templateLibPath,
            templateLibFileName );
      return List.of( pojoGenerator, staticGenerator );
   }

   Collection<JavaGenerator> getGenerators( final TestAspect aspect, final KnownVersion version ) {
      final VersionedModel model = TestResources.getModel( aspect, version ).get();
      final JavaGenerator pojoGenerator = new AspectModelJavaGenerator( model, false, false, "", "" );
      final JavaGenerator staticGenerator = new StaticMetaModelJavaGenerator( model, false, "", "" );
      return List.of( pojoGenerator, staticGenerator );
   }
}
