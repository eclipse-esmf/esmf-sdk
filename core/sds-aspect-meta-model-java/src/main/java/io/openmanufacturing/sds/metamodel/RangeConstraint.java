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

package io.openmanufacturing.sds.metamodel;

import java.util.Optional;

import io.openmanufacturing.sds.metamodel.impl.BoundDefinition;

/**
 * Restricts the value of a Property to a specific set of possible values.
 *
 * @since BAMM 1.0.0
 */
public interface RangeConstraint extends Constraint {

   /**
    * @return the lower bound of the range. The type of the values is determined by the {@link Type} returned by
    *       {@link RangeConstraint#getDataType()}.
    */
   Optional<ScalarValue> getMinValue();

   /**
    * @return the upper bound of the range. The type of the values is determined by the {@link Type} returned by
    *       {@link RangeConstraint#getDataType()}.
    */
   Optional<ScalarValue> getMaxValue();

   /**
    * @return the definition of how the lower bound of the range is to be interpreted. Possible values are
    *       'OPEN', 'AT_LEAST' and 'GREATER_THAN'
    *
    * @since BAMM 1.0.0
    */
   BoundDefinition getLowerBoundDefinition();

   /**
    * @return the definition of how the upper bound of the range is to be interpreted. Possible values are
    *       'OPEN', 'AT_MOST' and 'LESS_THAN'
    *
    * @since BAMM 1.0.0
    */
   BoundDefinition getUpperBoundDefinition();
}
