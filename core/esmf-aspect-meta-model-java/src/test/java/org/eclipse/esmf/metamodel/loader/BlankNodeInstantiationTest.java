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

package org.eclipse.esmf.metamodel.loader;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.characteristic.List;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.TestAspect;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class BlankNodeInstantiationTest extends MetaModelInstantiatorTest {

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testBlankNodeInstantiationAndSyntheticNameCalculation( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.MODEL_WITH_BLANK_AND_ADDITIONAL_NODES, metaModelVersion );
      assertThat( aspect.getProperties() ).hasSize( 1 );
      final List list = (List) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final Characteristic characteristic = list.getElementCharacteristic().get();
      assertThat( characteristic.isAnonymous() ).isTrue();
      assertThat( characteristic.getName() ).startsWith( "x" );
   }
}
