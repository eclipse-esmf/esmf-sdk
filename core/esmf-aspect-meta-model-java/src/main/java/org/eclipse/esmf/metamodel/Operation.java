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

import java.util.List;
import java.util.Optional;

/**
 * Defines a piece of functionality of an Asset which can be triggered by an external factor, e.g. by a user, service,
 * or machine.
 *
 * @since SAMM 1.0.0
 */
public interface Operation extends NamedElement {

   /**
    * @return a {@link List} of {@link Property}(ies) which define the input parameters for the Operation.
    */
   List<Property> getInput();

   /**
    * @return an {@link Optional} which may contain a {@link Property} in case the Operation defines output.
    */
   Optional<Property> getOutput();
}
