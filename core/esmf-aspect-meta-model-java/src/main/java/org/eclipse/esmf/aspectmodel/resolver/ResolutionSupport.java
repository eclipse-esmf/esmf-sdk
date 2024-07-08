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

package org.eclipse.esmf.aspectmodel.resolver;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

/**
 * An instance of this interface provides utilies for the implementation of a {@link ResolutionStrategy}.
 */
public interface ResolutionSupport {
   /**
    * Checks if a given Aspect Model File contains a given element definition.
    *
    * @param aspectModelFile the file
    * @param urn the element
    * @return true of the file contains the model element definition
    */
   boolean containsDefinition( final AspectModelFile aspectModelFile, final AspectModelUrn urn );
}
