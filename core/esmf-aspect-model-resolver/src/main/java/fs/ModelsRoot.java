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

package fs;

import java.io.File;
import java.nio.file.Path;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

/**
 * Represents the root directory of the directory hierarchy in which Aspect Models are organized.
 * The directory is assumed to contain a file system hierarchy as follows: {@code N/V/X.ttl} where N is the namespace,
 * V is the version of the namespace and X is the name of the model element (Aspect, Characteristic, ...).
 * <pre>
 * models   <-- this is the modelsRoot
 * └── com.example
 *     ├── 1.0.0
 *     │   ├── ExampleAspect.ttl
 *     │   ├── exampleProperty.ttl
 *     │   └── ExampleCharacteristic.ttl
 *     └── 1.1.0
 *         └── ...
 * </pre>
 */
public class ModelsRoot {
   private final Path root;

   public ModelsRoot( final Path path ) {
      root = path;
   }

   public Path rootPath() {
      return root;
   }

   public File determineOutputFile( final AspectModelUrn urn ) {
      return directoryForNamespace( urn ).resolve( urn.getName() + ".ttl" ).toFile();
   }

   public Path directoryForNamespace( final AspectModelUrn urn ) {
      return root.resolve( urn.getNamespace() ).resolve( urn.getVersion() );
   }
}
