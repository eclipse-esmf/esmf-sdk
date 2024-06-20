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

import org.eclipse.esmf.metamodel.vocabulary.Namespace;

import org.apache.jena.rdf.model.Model;

public interface ModelFile {
   Model sourceModel();

   List<String> headerComment();

   Optional<URI> sourceLocation();

   Namespace namespace();

   default ModelFile withModel( final Model model ) {
      return new ModelFile() {
         @Override
         public Model sourceModel() {
            return model;
         }

         @Override
         public List<String> headerComment() {
            return ModelFile.this.headerComment();
         }

         @Override
         public Optional<URI> sourceLocation() {
            return ModelFile.this.sourceLocation();
         }

         @Override
         public Namespace namespace() {
            return ModelFile.this.namespace();
         }
      };
   }
}
