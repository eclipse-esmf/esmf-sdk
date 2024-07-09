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

package org.eclipse.esmf.aspectmodel;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.ModelElementGroup;

import org.apache.jena.rdf.model.Model;

public interface AspectModelFile extends ModelElementGroup {
   Model sourceModel();

   default List<String> headerComment() {
      return List.of();
   }

   Optional<URI> sourceLocation();

   @Override
   default List<ModelElement> elements() {
      throw new UnsupportedOperationException( "Uninitialized Aspect Model" );
   }

   //   boolean isAutoMigrated();
}