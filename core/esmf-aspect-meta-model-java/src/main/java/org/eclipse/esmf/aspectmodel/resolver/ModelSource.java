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

import java.net.URI;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

/**
 * A model source is one place where {@link AspectModelFile}s are stored and can be retrieved from.
 */
public interface ModelSource {
   /**
    * Lists all URIs of Aspect Model files in this model source
    *
    * @return the URIs
    */
   Stream<URI> listContents();

   /**
    * Lists all URIs of Aspect Model files for a given Namespace in this model source
    *
    * @param namespace the namespace
    * @return the URIs
    */
   Stream<URI> listContentsForNamespace( AspectModelUrn namespace );

   /**
    * Loads all Aspect Model files available in this model source
    *
    * @return the Aspect Model files
    */
   Stream<AspectModelFile> loadContents();

   /**
    * Loads all Aspect Model files for a given namespace from this model source
    *
    * @param namespace the namespacd
    * @return the Aspect Model files
    */
   Stream<AspectModelFile> loadContentsForNamespace( AspectModelUrn namespace );
}
