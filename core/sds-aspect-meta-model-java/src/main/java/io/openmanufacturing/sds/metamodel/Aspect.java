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

import java.util.List;

/**
 * An Aspect encapsulates a number of properties and operations that define one functional facet of a Digital Twin.
 *
 * @since BAMM 1.0.0
 */
public interface Aspect extends StructureElement {
   /**
    * @return the {@link Operation}s defined in the scope of this Aspect.
    */
   List<Operation> getOperations();
}
