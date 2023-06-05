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

import io.vavr.Tuple;
import io.vavr.collection.List;

import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AspectModelLoaderTest extends MetaModelVersions {
   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testOfAbstractEntityCyclomaticCreation( final KnownVersion version ) {
      var entities = TestResources.getModel( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_SAME_EXTEND, version ).flatMap( AspectModelLoader::getElements )
            .map( List::ofAll ).get().filter( e -> e instanceof ComplexType ).map( e -> (ComplexType) e ).toMap( e -> Tuple.of( e.getName(), e ) );

      assertThat( entities.getOrElse( "AbstractTestEntity", null ) ).isNotNull().isInstanceOf( AbstractEntity.class );
      assertThat( entities.getOrElse( "AbstractSecondTestEntity", null ) ).isNotNull().isInstanceOf( AbstractEntity.class );

      var abstractEntity = (AbstractEntity) entities.get( "AbstractTestEntity" ).get();
      var abstractSecondEntity = (AbstractEntity) entities.get( "AbstractSecondTestEntity" ).get();

      assertThat( entities.getOrElse( "testEntityOne", null ) ).extracting( ComplexType::getExtends ).extracting( Optional::get ).isSameAs( abstractEntity );
      assertThat( entities.getOrElse( "testEntityTwo", null ) ).extracting( ComplexType::getExtends ).extracting( Optional::get ).isSameAs( abstractEntity );
      assertThat( entities.getOrElse( "testEntityThree", null ) ).extracting( ComplexType::getExtends ).extracting( Optional::get )
            .isSameAs( abstractSecondEntity );
   }
}