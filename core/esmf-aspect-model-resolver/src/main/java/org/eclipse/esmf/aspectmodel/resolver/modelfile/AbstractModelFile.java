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

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.esmf.aspectmodel.resolver.services.ModelFile;
import org.eclipse.esmf.aspectmodel.vocabulary.Namespace;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public abstract class AbstractModelFile implements ModelFile {

   protected record ModelFileNamespace( String uri ) implements Namespace {
      @Override
      public String getUri() {
         return uri;
      }
   }

   protected Namespace getNamespace( final Model model ) {
      final String ns = model.getNsPrefixURI( "" );
      if ( isNotBlank( ns ) ) {
         return new ModelFileNamespace( ns );
      }
      List<Resource> selected = model.listSubjectsWithProperty( RDF.type ).toList();
      if ( !selected.isEmpty() ) {
         return new ModelFileNamespace( selected.get( 0 ).getNameSpace() );
      }
      return null;
   }

   protected List<String> getHeader( final List<String> modelContent ) {
      return modelContent.stream().takeWhile( line -> line.startsWith( "#" ) || isBlank( line ) ).toList();
   }

   @Override
   public boolean equals( final Object obj ) {
      if ( super.equals( obj ) ) {
         return true;
      }
      if ( !( obj instanceof final ModelFile other ) ) {
         return false;
      }
      return (
            other.sourceLocation().isPresent()
                  && sourceLocation().isPresent()
                  && other.sourceLocation().equals( sourceLocation() ) )
            || (
            other.sourceLocation().isEmpty()
                  && sourceLocation().isEmpty()
                  && other.sourceModel().isIsomorphicWith( sourceModel() ) );
   }

   @Override
   public int hashCode() {
      return sourceLocation().map( URI::hashCode ).orElseGet( () -> sourceModel().hashCode() );
   }

   protected List<String> loadContent( final ModelInput input ) {
      return input.content(
            inputStream -> new BufferedReader( new InputStreamReader( inputStream, StandardCharsets.UTF_8 ) ).lines().toList() );
   }
}
