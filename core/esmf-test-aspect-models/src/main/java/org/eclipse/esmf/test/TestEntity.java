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

public enum TestEntity implements TestModel {
   ENTITY_WITH_MULTIPLE_SEE_ATTRIBUTES,
   ENTITY_WITH_OPTIONAL_AND_NOT_IN_PAYLOAD_PROPERTY,
   ENTITY_WITHOUT_SEE_ATTRIBUTE,
   ENTITY_WITH_SEE_ATTRIBUTE,
   SHARED_ENTITY_WITH_SEE_ATTRIBUTE,
   ENTITY_WITH_OPTIONAL_PROPERTY,
   ENTITY_WITH_OPTIONAL_PROPERTY_WITH_PAYLOAD_NAME,
   ENTITY_WITH_PROPERTY_WITH_PAYLOAD_NAME;

   @Override
   public String getName() {
      return CaseUtils.toCamelCase( toString().toLowerCase(), true, '_' );
   }
}
