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

package org.eclipse.esmf.aspectmodel.generator.ts;

import java.util.Collection;
import java.util.List;

import org.eclipse.esmf.aspectmodel.generator.ts.metamodel.StaticMetaModelTsGenerator;
import org.eclipse.esmf.aspectmodel.generator.ts.pojo.AspectModelTsGenerator;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

public abstract class TsGeneratorTestBase {

   protected final ClassLoader classLoader = getClass().getClassLoader();

   protected Collection<TsGenerator> getGenerators( final TestAspect testAspect ) {
      final AspectModel aspectModel = TestResources.load( testAspect );
      final Aspect aspect = aspectModel.aspect();
      final TsCodeGenerationConfig config = TsCodeGenerationConfigBuilder.builder()
            .executeLibraryMacros( false )
            .packageName( aspect.urn().getNamespaceMainPart() )
            .disablePrettierFormatter( true ) // disable formatting for faster tests
            .build();
      final TsGenerator pojoGenerator = new AspectModelTsGenerator( aspect, config );
      return List.of( pojoGenerator );
   }

   protected Collection<TsGenerator> getStaticGenerators( final TestAspect testAspect ) {
      final AspectModel aspectModel = TestResources.load( testAspect );
      final Aspect aspect = aspectModel.aspect();
      final TsCodeGenerationConfig config = TsCodeGenerationConfigBuilder.builder()
            .executeLibraryMacros( false )
            .packageName( aspect.urn().getNamespaceMainPart() )
            .disablePrettierFormatter( true ) // disable formatting for faster tests
            .build();
      final TsGenerator staticGenerator = new StaticMetaModelTsGenerator( aspect, config );
      final TsGenerator pojoGenerator = new AspectModelTsGenerator( aspect, config );
      return List.of( pojoGenerator, staticGenerator );
   }
}
