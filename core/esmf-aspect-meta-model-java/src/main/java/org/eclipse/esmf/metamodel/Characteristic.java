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

package org.eclipse.esmf.metamodel;

import java.util.Optional;

/**
 * Description of a Property in the scope of an Aspect that determines the Property's data type and other qualities.
 *
 * @since SAMM 1.0.0
 */
public interface Characteristic extends NamedElement {

   /**
    * The data type of the {@link Property} described by this {@link Characteristic}.
    *
    * @return the {@link Type}
    */
   Optional<Type> getDataType();
}
