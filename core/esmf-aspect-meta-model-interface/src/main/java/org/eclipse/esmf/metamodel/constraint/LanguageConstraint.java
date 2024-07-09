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

package org.eclipse.esmf.metamodel.constraint;

import java.util.Locale;

import org.eclipse.esmf.metamodel.Constraint;

/**
 * Restricts a value to a specific language.
 *
 * @since SAMM 1.0.0
 */
public interface LanguageConstraint extends Constraint {

   /**
    * @return an ISO 639-1 language code for the language of the value of the constrained Property.
    */
   Locale getLanguageCode();
}
