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

import java.math.BigInteger;
import java.util.Optional;

/**
 * Restricts a value to a specific length.
 *
 * @since BAMM 1.0.0
 */
public interface LengthConstraint extends Constraint {

   /**
    * @return the lower bound of the constraint.
    */
   Optional<BigInteger> getMinValue();

   /**
    * @return the upper bound of the constraint.
    */
   Optional<BigInteger> getMaxValue();
}
