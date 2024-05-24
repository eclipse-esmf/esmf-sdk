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

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;

public class MetaModelFiles {
   // TODO: Adjust code generation for Units.java to have each unit's "sourceFile" refer to MetaModelFiles.UNITS

   // TODO: Implement model file that statically represents units.ttl
   public static final ModelFile UNITS = new ModelFile() {
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

      @Override
      public List<ModelElement> elements() {
         return null;
      }
   };

   // TODO: Add static ModelFile instances for SAMM-provided ttl files containing model elements (for Characteristics/Constraints and
   //  Entities)
}
