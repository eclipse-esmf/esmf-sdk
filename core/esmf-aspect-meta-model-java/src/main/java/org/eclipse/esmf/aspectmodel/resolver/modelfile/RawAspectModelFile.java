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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import org.eclipse.esmf.aspectmodel.AspectLoadingException;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * An implementation of an {@link AspectModelFile} that knows about the RDF model content and source
 * location, but has not instantiated the model elements yet. Calling {@link #elements()} on this
 * file will throw an exception. It is intended to represent the intermediary result of loading an
 * Aspect Model file, possibly from a remote location.
 *
 * @param sourceModel the source RDF model
 * @param headerComment the header comment
 * @param sourceUri the source location
 */
@SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
@RecordBuilder
public record RawAspectModelFile(
      String sourceRepresentation,
      Model sourceModel,
      List<String> headerComment,
      URI sourceUri
) implements AspectModelFile {
   public RawAspectModelFile {
      if ( sourceModel == null ) {
         sourceModel = ModelFactory.createDefaultModel();
      }
      if ( headerComment == null ) {
         headerComment = List.of();
      }
      if ( sourceUri == null ) {
         throw new AspectLoadingException( "Can not create Aspect Model file without source location" );
      }
   }

   @Override
   public String toString() {
      return humanReadableLocation();
   }

   @Override
   public AspectModelUrn namespaceUrn() {
      return AspectModelUrn.from( sourceModel().getNsPrefixMap().get( "" ).replace( "#", "" ) )
            .getOrElseThrow( () -> new ModelResolutionException( "Aspect Model File " + this + " does not contain a '@prefix :'" ) );
   }
}
