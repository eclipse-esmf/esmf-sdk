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

package org.eclipse.esmf.characteristic;

import java.util.Optional;

import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Unit;

/**
 * A value which can be quantified and may have a unit.
 *
 * @since BAMM 1.0.0
 */
public interface Quantifiable extends Characteristic {

   /**
    * @return the {@link Unit} for this Quantifiable.
    */
   Optional<Unit> getUnit();
}
