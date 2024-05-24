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

import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.ModelFile;
import org.eclipse.esmf.metamodel.ModelInput;
import org.eclipse.esmf.metamodel.ModelNamespace;

import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;

public class DefaultModelFile implements ModelFile {
   private final Model sourceModel;
   private final List<String> headerComment;
   private final Optional<URI> sourceLocation;

   public DefaultModelFile( final Model sourceModel, final List<String> headerComment, final Optional<URI> sourceLocation ) {
      this.sourceModel = sourceModel;
      this.headerComment = headerComment;
      this.sourceLocation = sourceLocation;
   }

   public static DefaultModelFile fromInput( final ModelInput modelInput ) {
      final Try<Model> tryModel = TurtleLoader.loadTurtle( modelInput.contentProvider().get() );
      final Model model = tryModel.getOrElseThrow( exception -> new RuntimeException( exception ) );
      final List<String> comments = List.of(); // TODO load from input
      return new DefaultModelFile( model, comments, modelInput.location() );
   }

   @Override
   public List<ModelElement> elements() {
      // TODO
      return null;
   }

   @Override
   public Model sourceModel() {
      return sourceModel;
   }

   @Override
   public List<String> headerComment() {
      return headerComment;
   }

   @Override
   public Optional<URI> sourceLocation() {
      return sourceLocation;
   }

   @Override
   public ModelNamespace namespace() {
      // TODO
      return null;
   }
}
