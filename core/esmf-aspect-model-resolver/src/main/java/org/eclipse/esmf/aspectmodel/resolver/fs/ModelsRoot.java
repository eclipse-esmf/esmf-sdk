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

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

public abstract class ModelsRoot {
   private final URI root;

   protected ModelsRoot( final URI root ) {
      this.root = root;
   }

   public URI rootPath() {
      return root;
   }

   public abstract URI directoryForNamespace( final AspectModelUrn urn );

   public URI determineAspectModelFile( final AspectModelUrn urn ) {
      return directoryForNamespace( urn ).resolve( urn.getName() + ".ttl" );
   }
}
