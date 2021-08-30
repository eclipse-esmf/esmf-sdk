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
import java.util.Optional;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

import io.openmanufacturing.sds.aspectmodel.java.metamodel.StaticMetaModelJavaGenerator;
import io.openmanufacturing.sds.aspectmodel.java.pojo.AspectModelJavaGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestResources;

abstract class StaticMetaModelGeneratorTest extends MetaModelVersions {

   Collection<JavaGenerator> getGenerators( final TestAspect aspect, final KnownVersion version,
         final Optional<String> customJavaPackageName ) {
      final VersionedModel model = TestResources.getModel( aspect, version ).get();

      final JavaGenerator pojoGenerator = customJavaPackageName
            .map( javaPackageName -> new AspectModelJavaGenerator( model, javaPackageName, false ) )
            .orElseGet( () -> new AspectModelJavaGenerator( model, false ) );

      final JavaGenerator staticGenerator = customJavaPackageName
            .map( javaPackageName -> new StaticMetaModelJavaGenerator( model, javaPackageName ) )
            .orElseGet( () -> new StaticMetaModelJavaGenerator( model ) );

      return List.of( pojoGenerator, staticGenerator );
   }
}
