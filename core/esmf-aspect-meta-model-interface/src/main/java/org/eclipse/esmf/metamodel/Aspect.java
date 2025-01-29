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

import org.eclipse.esmf.metamodel.characteristic.Collection;

/**
 * An Aspect encapsulates a number of properties and operations that define one functional facet of a Digital Twin.
 *
 * @since SAMM 1.0.0
 */
public interface Aspect extends StructureElement {
   /**
    * @return the {@link Operation}s defined in the scope of this Aspect.
    */
   List<Operation> getOperations();

   /**
    * @return the {@link Event}s defined in the scope of this Aspect.
    * @since SAMM 2.0.0
    */
   List<Event> getEvents();

   default boolean isCollectionAspect() {
      return getProperties().stream()
            .map( Property::getCharacteristic )
            .flatMap( Optional::stream )
            .filter( Collection.class::isInstance ).count() == 1;
   }
}
