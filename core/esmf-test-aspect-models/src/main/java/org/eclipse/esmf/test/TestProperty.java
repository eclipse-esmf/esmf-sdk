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

package org.eclipse.esmf.test;

import org.apache.commons.text.CaseUtils;

public enum TestProperty implements TestModel {
   PROPERTY_WITH_EXAMPLE_VALUE,
   PROPERTY_WITH_MULTIPLE_SEE_ATTRIBUTES,
   PROPERTY_WITHOUT_EXAMPLE_VALUE,
   PROPERTY_WITHOUT_SEE_ATTRIBUTE,
   PROPERTY_WITH_SEE_ATTRIBUTE,
   SHARED_PROPERTY_WITH_SEE_ATTRIBUTE;

   @Override
   public String getName() {
      return CaseUtils.toCamelCase( toString().toLowerCase(), false, '_' );
   }
}
