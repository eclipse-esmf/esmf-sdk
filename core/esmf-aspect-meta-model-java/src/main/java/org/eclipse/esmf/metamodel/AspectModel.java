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

public interface AspectModel extends ModelElementGroup {
   default List<ModelNamespace> namespaces() {
      return files().stream().map( ModelFile::namespace ).toList();
   }

   List<ModelFile> files();

   @Override
   default List<ModelElement> elements() {
      return files().stream().flatMap( file -> file.elements().stream() ).toList();
   }
}
