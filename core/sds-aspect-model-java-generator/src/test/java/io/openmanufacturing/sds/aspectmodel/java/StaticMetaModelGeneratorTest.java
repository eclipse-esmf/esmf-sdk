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

import java.io.File;
import java.util.Collection;
import java.util.List;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.java.metamodel.StaticMetaModelJavaGenerator;
import io.openmanufacturing.sds.aspectmodel.java.pojo.AspectModelJavaGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestResources;
import io.openmanufacturing.sds.test.TestSharedAspect;
import io.openmanufacturing.sds.test.TestSharedModel;

abstract class StaticMetaModelGeneratorTest extends MetaModelVersions {

   Collection<JavaGenerator> getGenerators( final TestAspect aspect, final KnownVersion version, final boolean executeLibraryMacros,
         final File templateLibFile ) {
      final VersionedModel model = TestResources.getModel( aspect, version ).get();
      final JavaGenerator pojoGenerator = new AspectModelJavaGenerator( model, false, executeLibraryMacros, templateLibFile );
      final JavaGenerator staticGenerator = new StaticMetaModelJavaGenerator( model, executeLibraryMacros, templateLibFile );
      return List.of( pojoGenerator, staticGenerator );
   }

   Collection<JavaGenerator> getGenerators( final TestAspect aspect, final KnownVersion version ) {
      final VersionedModel model = TestResources.getModel( aspect, version ).get();
      final JavaGenerator pojoGenerator = new AspectModelJavaGenerator( model, false, false, null );
      final JavaGenerator staticGenerator = new StaticMetaModelJavaGenerator( model, false, null );
      return List.of( pojoGenerator, staticGenerator );
   }
   Collection<JavaGenerator> getGenerators( final TestSharedAspect aspect, final KnownVersion version ) {
      final VersionedModel model = TestResources.getModel( aspect, version ).get();
      final JavaGenerator pojoGenerator = new AspectModelJavaGenerator( model, false, false, null );
      final JavaGenerator staticGenerator = new StaticMetaModelJavaGenerator( model, false, null );
      return List.of( pojoGenerator, staticGenerator );
   }
}
