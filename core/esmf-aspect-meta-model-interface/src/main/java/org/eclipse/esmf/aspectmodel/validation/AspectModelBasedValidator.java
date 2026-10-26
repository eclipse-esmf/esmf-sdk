/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.validation;

import org.eclipse.esmf.aspectmodel.ViolationReport;
import org.eclipse.esmf.metamodel.AspectModel;

/**
 * Generic validator for Aspect Models on an already loaded Aspect Model
 */
public interface AspectModelBasedValidator {
   /**
    * Validates a loaded Aspect Model
    *
    * @param aspectModel the Aspect Model
    * @return the validation report
    */
   ViolationReport validateModel( AspectModel aspectModel );
}
