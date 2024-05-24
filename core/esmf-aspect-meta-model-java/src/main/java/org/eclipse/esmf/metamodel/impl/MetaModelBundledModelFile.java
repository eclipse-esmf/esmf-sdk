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

package org.eclipse.esmf.metamodel.impl;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.ModelFile;
import org.eclipse.esmf.metamodel.ModelNamespace;

import org.apache.jena.rdf.model.Model;

public class MetaModelBundledModelFile implements ModelFile {
   @Override
   public List<ModelElement> elements() {
      return null;
   }

   @Override
   public Model sourceModel() {
      return null;
   }

   @Override
   public List<String> headerComment() {
      return null;
   }

   @Override
   public Optional<URI> sourceLocation() {
      return Optional.empty();
   }

   @Override
   public ModelNamespace namespace() {
      return null;
   }
}
