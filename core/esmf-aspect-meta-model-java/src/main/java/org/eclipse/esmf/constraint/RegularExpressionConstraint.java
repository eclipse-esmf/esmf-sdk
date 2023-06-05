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

package org.eclipse.esmf.constraint;

import org.eclipse.esmf.metamodel.Constraint;

/**
 * Restricts a string value to a regular expression as defined by XQuery 1.0 and XPath 2.0 Functions and Operators.
 *
 * @since SAMM 1.0.0
 */
public interface RegularExpressionConstraint extends Constraint {

   /**
    * @return the regular expression defined by this Constraint.
    */
   String getValue();
}
