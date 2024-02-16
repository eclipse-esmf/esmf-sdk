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

package org.eclipse.esmf.aspectmodel.java;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.eclipse.esmf.aspectmodel.java.metamodel.StaticMetaModelJavaGenerator;
import org.eclipse.esmf.aspectmodel.java.pojo.AspectModelJavaGenerator;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;
import org.eclipse.esmf.test.TestSharedAspect;

abstract class StaticMetaModelGeneratorTest extends MetaModelVersions {
   Collection<JavaGenerator> getGenerators( final TestAspect testAspect, final KnownVersion version, final boolean executeLibraryMacros,
         final File templateLibFile ) {
      final VersionedModel model = TestResources.getModel( testAspect, version ).get();
      final Aspect aspect = AspectModelLoader.getSingleAspectUnchecked( model );
      final JavaCodeGenerationConfig config = JavaCodeGenerationConfigBuilder.builder()
            .enableJacksonAnnotations( false )
            .executeLibraryMacros( executeLibraryMacros )
            .templateLibFile( templateLibFile )
            .packageName( aspect.getAspectModelUrn().map( AspectModelUrn::getNamespace ).get() )
            .build();
      final JavaGenerator pojoGenerator = new AspectModelJavaGenerator( aspect, config );
      final JavaGenerator staticGenerator = new StaticMetaModelJavaGenerator( aspect, config );
      return List.of( pojoGenerator, staticGenerator );
   }

   Collection<JavaGenerator> getGenerators( final TestAspect testAspect, final KnownVersion version ) {
      final VersionedModel model = TestResources.getModel( testAspect, version ).get();
      final Aspect aspect = AspectModelLoader.getSingleAspectUnchecked( model );
      final JavaCodeGenerationConfig config = JavaCodeGenerationConfigBuilder.builder()
            .enableJacksonAnnotations( false )
            .executeLibraryMacros( false )
            .packageName( aspect.getAspectModelUrn().map( AspectModelUrn::getNamespace ).get() )
            .build();
      final JavaGenerator pojoGenerator = new AspectModelJavaGenerator( aspect, config );
      final JavaGenerator staticGenerator = new StaticMetaModelJavaGenerator( aspect, config );
      return List.of( pojoGenerator, staticGenerator );
   }

   Collection<JavaGenerator> getGenerators( final TestSharedAspect testAspect, final KnownVersion version ) {
      final VersionedModel model = TestResources.getModel( testAspect, version ).get();
      final Aspect aspect = AspectModelLoader.getSingleAspectUnchecked( model );
      final JavaCodeGenerationConfig config = JavaCodeGenerationConfigBuilder.builder()
            .enableJacksonAnnotations( false )
            .executeLibraryMacros( false )
            .packageName( aspect.getAspectModelUrn().map( AspectModelUrn::getNamespace ).get() )
            .build();
      final JavaGenerator pojoGenerator = new AspectModelJavaGenerator( aspect, config );
      final JavaGenerator staticGenerator = new StaticMetaModelJavaGenerator( aspect, config );
      return List.of( pojoGenerator, staticGenerator );
   }
}
