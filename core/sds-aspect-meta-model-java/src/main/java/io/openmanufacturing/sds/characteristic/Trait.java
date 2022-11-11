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

package io.openmanufacturing.sds.characteristic;

import java.util.List;

import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Constraint;

/**
 * A characterstic that can specify Constraints on another Characteristic.
 *
 * @since BAMM 1.0.0
 */
public interface Trait extends Characteristic {
   /**
    * @return the {@link Characteristic} on which Constraints are enforced.
    */
   Characteristic getBaseCharacteristic();

   /**
    * The Constraints to enforce on the base Characteristic
    *
    * @return the collection of Constraints
    */
   List<Constraint> getConstraints();
}
