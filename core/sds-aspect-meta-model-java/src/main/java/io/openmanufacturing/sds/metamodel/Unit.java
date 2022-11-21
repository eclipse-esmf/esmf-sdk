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
import java.util.Set;

/**
 * A physical unit.
 *
 * @since BAMM 1.0.0
 */
public interface Unit extends NamedElement {
   /**
    * Returns the unit's symbol
    */
   Optional<String> getSymbol();

   /**
    * Returns the unit's common code, as described in UNECE Recommendation 20
    */
   Optional<String> getCode();

   /**
    * Return the unit's reference unit for unit conversions
    */
   Optional<String> getReferenceUnit();

   /**
    * Return the unit's conversion factor for unit conversions
    */
   Optional<String> getConversionFactor();

   /**
    * Return the unit's quantity kinds
    */
   Set<QuantityKind> getQuantityKinds();
}
