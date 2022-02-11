/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.metamodel.impl;

import io.openmanufacturing.sds.metamodel.CollectionValue;
import io.openmanufacturing.sds.metamodel.EntityInstance;
import io.openmanufacturing.sds.metamodel.ScalarValue;
import io.openmanufacturing.sds.metamodel.Value;

/**
 * Base class for Value implementations
 */
public abstract class BaseValue implements Value {
   @Override
   public boolean isScalar() {
      return false;
   }

   @Override
   public boolean isEntityInstance() {
      return false;
   }

   @Override
   public boolean isCollection() {
      return false;
   }

   @Override
   public ScalarValue asScalarValue() {
      throw new UnsupportedOperationException();
   }

   @Override
   public EntityInstance asEntityInstance() {
      throw new UnsupportedOperationException();
   }

   @Override
   public CollectionValue asCollectionValue() {
      throw new UnsupportedOperationException();
   }
}
