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

package org.eclipse.esmf.metamodel;

import java.util.List;

public interface ModelElementGroup {
   List<ModelElement> elements();

   /**
    * Convenience method to get the Aspects in this namespace
    *
    * @return the list of aspects
    */
   default List<Aspect> aspects() {
      return elements().stream()
            .filter( element -> element.is( Aspect.class ) )
            .map( element -> element.as( Aspect.class ) )
            .toList();
   }
}
