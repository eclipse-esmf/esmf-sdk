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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectContext;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class AspectModelDiagramGeneratorTest extends MetaModelVersions {
   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   void testGen( final TestAspect testAspect ) {
      final VersionedModel versionedModel = TestResources.getModel( testAspect, KnownVersion.getLatest() ).get();
      final Aspect aspect = AspectModelLoader.getSingleAspect( versionedModel ).getOrElseThrow( () -> new RuntimeException() );
      final AspectContext context = new AspectContext( versionedModel, aspect );
      final AspectModelDiagramGenerator generator = new AspectModelDiagramGenerator( context );
      assertThatCode( () -> {
         final ByteArrayOutputStream out = new ByteArrayOutputStream();
         generator.generateDiagram( AspectModelDiagramGenerator.Format.SVG, Locale.ENGLISH, out );
      } ).doesNotThrowAnyException();
   }
}
