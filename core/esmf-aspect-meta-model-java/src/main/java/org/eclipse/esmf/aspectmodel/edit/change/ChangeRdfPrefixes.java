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

package org.eclipse.esmf.aspectmodel.edit.change;

import java.net.URI;
import java.util.Map;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ModelChangeException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * RDF-level refactoring operation: Change RDF prefixes in an Aspect Model file
 */
public class ChangeRdfPrefixes extends EditAspectModel {
   private final URI targetLocation;
   private final Map<String, String> addPrefixes;
   private final Map<String, String> removePrefixes;

   public ChangeRdfPrefixes( final AspectModelFile targetFile, final Map<String, String> addPrefixes ) {
      this( targetFile.sourceLocation().orElseThrow( () ->
            new ModelChangeException( "Can add prefixes only to named file" ) ), addPrefixes );
   }

   public ChangeRdfPrefixes( final AspectModelFile targetFile, final Map<String, String> addPrefixes,
         final Map<String, String> removePrefixes ) {
      this( targetFile.sourceLocation().orElseThrow( () ->
            new ModelChangeException( "Can add prefixes only to named file" ) ), addPrefixes, removePrefixes );
   }

   public ChangeRdfPrefixes( final URI targetLocation, final Map<String, String> addPrefixes ) {
      this( targetLocation, addPrefixes, Map.of() );
   }

   public ChangeRdfPrefixes( final URI targetLocation, final Map<String, String> addPrefixes,
         final Map<String, String> removePrefixes ) {
      this.targetLocation = targetLocation;
      this.addPrefixes = addPrefixes;
      this.removePrefixes = removePrefixes;
   }

   @Override
   protected ModelChanges calculateChangesForFile( final AspectModelFile aspectModelFile ) {
      if ( !aspectModelFile.sourceLocation().map( location -> location.equals( targetLocation ) ).orElse( false ) ) {
         return ModelChanges.NONE;
      }

      final Model addModel = ModelFactory.createDefaultModel();
      final Model removeModel = ModelFactory.createDefaultModel();
      addModel.setNsPrefixes( addPrefixes );
      removeModel.setNsPrefixes( removePrefixes );

      final String description = String.format( "Add/overwrite %d and remove %d RDF prefixes", addPrefixes.size(), removePrefixes.size() );
      return new ModelChanges( description, addModel, removeModel );
   }

   @Override
   public Change reverse() {
      return new ChangeRdfPrefixes( targetLocation, removePrefixes, addPrefixes );
   }
}
