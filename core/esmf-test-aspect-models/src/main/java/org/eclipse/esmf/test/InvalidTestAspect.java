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

public enum InvalidTestAspect implements TestModel {
   ACTUALLY_JSON,
   ASPECT_MISSING_NAME_AND_PROPERTIES,
   ASPECT_MISSING_PROPERTIES,
   ASPECT_WITH_INVALID_VERSION,
   ASPECT_WITH_BAMM_NAMESPACE_FOR_CUSTOM_UNIT,
   ASPECT_WITH_RECURSIVE_PROPERTY,
   INVALID_SYNTAX,
   MISSING_ASPECT_DECLARATION,
   INVALID_EXAMPLE_VALUE_DATATYPE,
   INVALID_PREFERRED_NAME_DATATYPE,
   RANGE_CONSTRAINT_WITH_WRONG_TYPE;

   @Override
   public String getName() {
      return CaseUtils.toCamelCase( toString().toLowerCase(), true, '_' );
   }
}
