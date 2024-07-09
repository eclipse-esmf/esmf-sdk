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

package org.eclipse.esmf.aspectmodel.resolver.fs;

import java.nio.file.Path;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

/**
 * A models root directory that assumes all model files are in the same directory.
 */
public class FlatModelsRoot extends ModelsRoot {
   public FlatModelsRoot( final Path root ) {
      super( root );
   }

   @Override
   public Path directoryForNamespace( final AspectModelUrn urn ) {
      return rootPath();
   }

   @Override
   public String toString() {
      return "FlatModelsRoot(rootPath=" + rootPath() + ")";
   }
}
