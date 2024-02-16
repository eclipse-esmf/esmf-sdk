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

import org.eclipse.esmf.characteristic.Trait;
import org.eclipse.esmf.constraint.FixedPointConstraint;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.TestAspect;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class FixedPointConstraintInstantiatorTest extends MetaModelInstantiatorTest {

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testFixedPointConstraintInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_FIXED_POINT, metaModelVersion );
      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final FixedPointConstraint fixedPointConstraint = (FixedPointConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( fixedPointConstraint,
            "Test Fixed Point",
            "This is a test fixed point constraint.",
            "http://example.com/" );

      assertThat( fixedPointConstraint.getScale() ).isEqualTo( Integer.valueOf( 5 ) );
      assertThat( fixedPointConstraint.getInteger() ).isEqualTo( Integer.valueOf( 3 ) );
   }
}
