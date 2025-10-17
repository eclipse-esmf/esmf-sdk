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

package org.eclipse.esmf.aspectmodel.importer.jsonschema;

import com.google.common.base.CaseFormat;

public enum JsonSchemaVocabulary {
   $ANCHOR, $COMMENT, $DEFS, $REF, ANY_OF, ARRAY, BOOLEAN, DESCRIPTION, ENUM, EXAMPLES, EXCLUSIVE_MAXIMUM, EXCLUSIVE_MINIMUM, FORMAT,
   INTEGER, ITEMS, MAXIMUM, MAX_LENGTH, MINIMUM, MIN_LENGTH, NUMBER, OBJECT, ONE_OF, PATTERN, PROPERTIES, REQUIRED, STRING, TITLE, TYPE,
   UNIQUE_ITEMS;

   public String key() {
      return CaseFormat.LOWER_UNDERSCORE.to( CaseFormat.LOWER_CAMEL, toString().toLowerCase() );
   }
}
