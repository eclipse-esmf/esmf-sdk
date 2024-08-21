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

package org.eclipse.esmf.aspectmodel.stats;

import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.aspectmodel.resolver.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.ModelSource;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

public class Usage {
   private final ModelSource modelSource;

   public Usage( final ModelSource modelSource ) {
      this.modelSource = modelSource;
   }

   public List<Reference> referencesTo( final AspectModelUrn targetElement ) {
      return referencesTo( targetElement, Optional.empty() );
   }

   private List<Reference> referencesTo( final AspectModelUrn targetElement, final Optional<AspectModelFile> targetSource ) {
      final List<AspectModelFile> cachedContents = modelSource.loadContents().toList();
      return cachedContents.stream().flatMap( file -> {
         final Resource targetResource = file.sourceModel().createResource( targetElement.toString() );
         return RdfUtil.getNamedElementsReferringTo( targetResource ).stream()
               .map( Resource::getURI )
               .map( AspectModelUrn::fromUrn )
               .map( urn -> ReferenceBuilder.builder()
                     .pointee( targetElement )
                     .pointeeSource( targetSource.orElseGet( () -> fileThatContainsDefinition( targetElement, cachedContents ) ) )
                     .pointer( urn )
                     .pointerSource( file )
                     .build() );
      } ).toList();
   }

   private AspectModelFile fileThatContainsDefinition( final AspectModelUrn targetElement, final List<AspectModelFile> contents ) {
      return contents.stream()
            .filter( file -> {
               final Resource targetResource = file.sourceModel().createResource( targetElement.toString() );
               return Streams.stream( file.sourceModel().listStatements( targetResource, RDF.type, (RDFNode) null ) )
                     .map( Statement::getSubject )
                     .filter( Resource::isURIResource )
                     .map( Resource::getURI )
                     .map( AspectModelUrn::fromUrn )
                     .findFirst()
                     .isPresent();
            } ).findFirst()
            .orElseThrow( () -> new ModelResolutionException( "Could not determine file that contains " + targetElement ) );
   }

   public List<Reference> referencesToAnyElementIn( final AspectModelFile file ) {
      return Streams.stream( file.sourceModel().listStatements( null, RDF.type, (RDFNode) null ) )
            .map( Statement::getSubject )
            .filter( Resource::isURIResource )
            .map( Resource::getURI )
            .map( AspectModelUrn::fromUrn )
            .flatMap( element -> referencesTo( element, Optional.of( file ) ).stream() )
            .toList();
   }
}
