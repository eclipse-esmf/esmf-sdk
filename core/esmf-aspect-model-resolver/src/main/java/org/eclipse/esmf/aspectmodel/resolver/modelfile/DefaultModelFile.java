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
import java.util.Objects;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.metamodel.vocabulary.Namespace;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;

@SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
public class DefaultModelFile extends AbstractModelFile {
   private final Model sourceModel;
   private final List<String> headerComment;
   private final Optional<URI> sourceLocation;
   private final Namespace namespace;

   public DefaultModelFile( final Model sourceModel, final List<String> headerComment, final Optional<URI> sourceLocation ) {
      Objects.requireNonNull( sourceModel, "" );
      this.sourceModel = sourceModel;
      this.headerComment = headerComment;
      this.sourceLocation = sourceLocation;
      this.namespace = getNamespace( sourceModel );
   }

   public DefaultModelFile( final ModelInput input ) {
      final List<String> modelContent = loadContent( input );
      this.headerComment = getHeader( modelContent );
      final Model model = TurtleLoader.loadTurtle( StringUtils.join( modelContent, "\n" ) ).get();
      this.namespace = getNamespace( model );
      this.sourceModel = model;
      this.sourceLocation = input.location();
   }

   public DefaultModelFile( final Model model ) {
      this( model, List.of(), Optional.empty() );
   }

   public DefaultModelFile( final Model model, final URI sourceLocation ) {
      this( model, List.of(), Optional.ofNullable( sourceLocation ) );
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
   public Namespace namespace() {
      return namespace;
   }
}
