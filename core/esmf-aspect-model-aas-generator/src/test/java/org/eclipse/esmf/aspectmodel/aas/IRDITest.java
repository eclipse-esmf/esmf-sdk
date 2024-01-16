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

package org.eclipse.esmf.aspectmodel.aas;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class IRDITest {
   @Test
   void testIrdiConstruction() {
      // valid
      assertThat( IRDI.from( "0173-1#01-AFX533#004" ) ).isNotEmpty();
      assertThat( IRDI.from( "0173-1-A.B.C:X.Y.Z-A-0#01-AFX533#004" ) ).isNotEmpty();
      // ICD (first section) too long
      assertThat( IRDI.from( "01730-1#01-AFX533#004" ) ).isEmpty();
      // ICD (first section) missing
      assertThat( IRDI.from( "-1#01-AFX533#004" ) ).isEmpty();
      // ICD (first section) no number
      assertThat( IRDI.from( "A-1#01-AFX533#004" ) ).isEmpty();
      // OI (second section) too long
      assertThat( IRDI.from( "0173-123456789012345678901234567890123456#01-AFX533#004" ) ).isEmpty();
      // OI (second section) missing
      assertThat( IRDI.from( "0173-#01-AFX533#004" ) ).isEmpty();
      // OI (second section) no safe character
      assertThat( IRDI.from( "0173-/#01-AFX533#004" ) ).isEmpty();
   }
}
