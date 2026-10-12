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

package org.eclipse.esmf.test;

import org.apache.commons.text.CaseUtils;

public enum InvalidTestAspect implements TestModel {
   ACTUALLY_JSON,
   ASPECT_WITH_INVALID_ANONYMOUS_REGEX_CONSTRAINT,
   ASPECT_WITH_INVALID_BLANK_STRUCTURED_VALUE,
   ASPECT_WITH_INVALID_REGEX_CONSTRAINT,
   ASPECT_WITH_INVALID_VERSION,
   ASPECT_WITH_RECURSIVE_PROPERTY,
   FIXED_POINT_CONSTRAINT_WITH_INVALID_DATATYPE_OLD_META_MODEL_VERSION,
   INVALID_SYNTAX,
   INVALID_ENCODING,
   INVALID_EXAMPLE_VALUE_DATATYPE,
   INVALID_PREFERRED_NAME_DATATYPE,
   INVALID_CHARACTERISTIC_DATATYPE,
   INVALID_URI,
   RANGE_CONSTRAINT_WITH_WRONG_TYPE,
   MODEL_WITH_CYCLES,
   MIXED_META_MODEL_VERSIONS_NEW_SAMM,
   MIXED_META_MODEL_VERSIONS_OLD_SAMM,
   TERM_NOT_IN_DECLARED_VERSION,
   TERM_NOT_IN_DECLARED_VERSION_ENTITY,

   INVALID_ASPECT_WITH_TWO_ASPECTS;

   @Override
   public String getName() {
      return CaseUtils.toCamelCase( toString().toLowerCase(), true, '_' );
   }
}
