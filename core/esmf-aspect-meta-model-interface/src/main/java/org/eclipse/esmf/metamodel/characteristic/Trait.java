/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.metamodel.characteristic;

import java.util.List;

import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Constraint;

/**
 * A characterstic that can specify Constraints on another Characteristic.
 *
 * @since SAMM 1.0.0
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
