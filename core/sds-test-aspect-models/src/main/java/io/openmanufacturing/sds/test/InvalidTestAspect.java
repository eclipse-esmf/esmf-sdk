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

package io.openmanufacturing.sds.test;

import org.apache.commons.text.CaseUtils;

public enum InvalidTestAspect implements TestModel {
   ACTUALLY_JSON,
   ASPECT_MISSING_NAME_AND_PROPERTIES,
   ASPECT_MISSING_PROPERTIES,
   ASPECT_WITH_FALSE_POSITIVE_VALIDATION,
   ASPECT_WITH_INVALID_VERSION,
   ASPECT_WITH_RECURSIVE_PROPERTY,
   INVALID_SYNTAX,
   MISSING_ASPECT_DECLARATION,
   INVALID_DATATYPE;

   @Override
   public String getName() {
      return CaseUtils.toCamelCase( toString().toLowerCase(), true, '_' );
   }
}
