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

import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.metamodel.vocabulary.Namespace;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;

public class LazyModelFile extends AbstractModelFile {
   @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
   private final Optional<URI> sourceLocation;
   private final ModelInput input;

   private volatile Model sourceModel;
   private volatile List<String> headerComment;
   private volatile Namespace namespace;

   public LazyModelFile( final ModelInput input ) {
      this.input = input;
      this.sourceLocation = input.location();
   }

   @Override
   public Model sourceModel() {
      loadModel();
      return sourceModel;
   }

   @Override
   public List<String> headerComment() {
      loadModel();
      return headerComment;
   }

   @Override
   public Optional<URI> sourceLocation() {
      return sourceLocation;
   }

   @Override
   public Namespace namespace() {
      loadModel();
      return namespace;
   }

   private void loadModel() {
      if ( sourceModel == null ) {
         synchronized ( this ) {
            if ( sourceModel == null ) {
               final List<String> modelContent = loadContent( input );
               headerComment = getHeader( modelContent );
               final Model model = TurtleLoader.loadTurtle( StringUtils.join( modelContent, "\n" ) ).get();
               namespace = getNamespace( model );
               sourceModel = model;
            }
         }
      }
   }
}
