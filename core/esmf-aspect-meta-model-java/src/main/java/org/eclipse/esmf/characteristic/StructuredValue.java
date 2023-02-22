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

import java.util.List;

import org.eclipse.esmf.metamodel.Characteristic;

/**
 * @since BAMM 1.0.0
 */
public interface StructuredValue extends Characteristic {
   /**
    * @return the regular expression to deconstruct the value into the Properties given in {@link #getElements()}.
    */
   String getDeconstructionRule();

   /**
    * @return a {@link java.util.List} of elements where each element is either a Property or a String.
    */
   List<Object> getElements();
}

