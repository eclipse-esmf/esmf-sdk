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

package org.eclipse.esmf.aspectmodel.resolver.modelfile;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.ModelFile;
import org.eclipse.esmf.metamodel.vocabulary.Namespace;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public final class ModelFiles {

   private static final ModelFile EMPTY = new Empty();

   private ModelFiles() {
   }

   public static ModelFile fromInput( final ModelInput modelInput ) {
      return new DefaultModelFile( modelInput );
   }

   public static ModelFile fromInputLazy( final ModelInput modelInput ) {
      return new LazyModelFile( modelInput );
   }

   public static ModelFile fromModel( final Model model, final URI sourceLocation ) {
      return new DefaultModelFile( model, sourceLocation );
   }

   public static ModelFile fromModel( final Model model ) {
      return new DefaultModelFile( model );
   }

   public static ModelFile empty() {
      return EMPTY;
   }

   public static class Empty implements ModelFile {
      public Model sourceModel() {
         return ModelFactory.createDefaultModel();
      }

      public List<String> headerComment() {
         return List.of();
      }

      public Optional<URI> sourceLocation() {
         return Optional.empty();
      }

      public Namespace namespace() {
         return null;
      }
   }
}
