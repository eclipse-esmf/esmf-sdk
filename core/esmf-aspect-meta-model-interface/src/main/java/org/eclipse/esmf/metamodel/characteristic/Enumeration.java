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
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;

/**
 * A list of possible values.
 *
 * @since SAMM 1.0.0
 */
public interface Enumeration extends Characteristic {

   /**
    * @return a {@link List} of values defined in this Enumeration. The type of the values is determined by the
    *       {@link Type} returned by {@link Enumeration#getDataType()}.
    */
   List<Value> getValues();
}
