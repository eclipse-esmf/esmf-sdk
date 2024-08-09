/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.Test;

public class AspectModelBuilderTest {
   @Test
   void testMergeAspectModels() {
      final AspectModel a1 = TestResources.load( TestAspect.ASPECT );
      final AspectModel a2 = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY );
      assertThat( a1.aspects() ).hasSize( 1 );
      assertThat( a2.aspects() ).hasSize( 1 );
      final AspectModel merged = AspectModelBuilder.merge( a1, a2 );
      assertThat( merged.aspects() ).hasSize( 2 );
      assertThat( merged.elements().size() ).isEqualTo( a1.elements().size() + a2.elements().size() );
   }
}
