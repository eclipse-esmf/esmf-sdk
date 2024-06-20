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

package org.eclipse.esmf.metamodel.loader;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import io.vavr.control.Try;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

class AspectModelLoaderTest extends MetaModelVersions {
   @ParameterizedTest
   @EnumSource( TestAspect.class )
   void testModelCanBeInstantiated( final TestAspect testAspect ) {
      final Try<List<ModelElement>> elements = TestResources.getModel( testAspect, KnownVersion.getLatest() )
            .flatMap( AspectModelLoader::getElements );
      assertThat( elements.isSuccess() ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testOfAbstractEntityCyclomaticCreation( final KnownVersion version ) {
      final Map<String, ComplexType> entities = TestResources.getModel( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_SAME_EXTEND, version )
            .flatMap( AspectModelLoader::getElements )
            .get()
            .stream()
            .filter( ComplexType.class::isInstance )
            .map( ComplexType.class::cast )
            .collect( Collectors.toMap( ComplexType::getName, Function.identity() ) );

      assertThat( entities ).extracting( "AbstractTestEntity" ).isInstanceOf( AbstractEntity.class );
      final AbstractEntity abstractEntity = (AbstractEntity) entities.get( "AbstractTestEntity" );
      assertThat( entities ).extracting( "testEntityOne" ).isInstanceOfSatisfying( ComplexType.class,
            type -> assertThat( type ).extracting( ComplexType::getExtends ).extracting( Optional::get ).isSameAs( abstractEntity ) );
      assertThat( entities ).extracting( "testEntityTwo" ).isInstanceOfSatisfying( ComplexType.class,
            type -> assertThat( type ).extracting( ComplexType::getExtends ).extracting( Optional::get ).isSameAs( abstractEntity ) );
   }
}