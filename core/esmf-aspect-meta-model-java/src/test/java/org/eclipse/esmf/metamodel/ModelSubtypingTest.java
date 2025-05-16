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

package org.eclipse.esmf.metamodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.esmf.metamodel.DataTypes.*;

import org.junit.jupiter.api.Test;

class ModelSubtypingTest {
   @Test
   void testScalarCasting() {
      assertThat( xsd.byte_.isTypeOrSubtypeOf( xsd.integer ) ).isTrue();
      assertThat( xsd.byte_.isTypeOrSubtypeOf( xsd.string ) ).isFalse();
   }
}
