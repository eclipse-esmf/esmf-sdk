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

package org.eclipse.esmf.constraint;

import java.math.BigInteger;
import java.util.Optional;

import org.eclipse.esmf.metamodel.Constraint;

/**
 * Restricts a value to a specific length.
 *
 * @since SAMM 1.0.0
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
