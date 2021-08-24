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

/**
 * Defines the scale as well as the number of integral digits for a fixed point number.
 *
 * @since BAMM 1.0.0
 */
public interface FixedPointConstraint extends Constraint {

   Integer getScale();

   Integer getInteger();
}
