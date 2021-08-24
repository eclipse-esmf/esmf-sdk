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

import java.util.Optional;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;

/**
 * Indicates that the model element implementing this interface can be abstract and refine another model element
 *
 * @since BAMM 1.0.0
 */
public interface CanRefine {
   /**
    * @return the URN of the model element that is refined by this element, if present
    */
   default Optional<AspectModelUrn> getRefines() {
      return Optional.empty();
   }
}
