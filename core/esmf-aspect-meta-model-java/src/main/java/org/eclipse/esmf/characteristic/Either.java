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

import org.eclipse.esmf.metamodel.Characteristic;

/**
 * Describes a Property whose value can have one of two possible types (a disjoint union).
 *
 * @since SAMM 1.0.0
 */
public interface Either extends Characteristic {

   /**
    * @return the {@link Characteristic} which describes the left side value of the disjoint union.
    */
   Characteristic getLeft();

   /**
    * @return the {@link Characteristic} which describes the right side value of the disjoint union.
    */
   Characteristic getRight();
}
