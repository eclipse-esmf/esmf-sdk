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

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ModelChangeException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

/**
 * Removes the definition of a model element from an AspectModelFile. The definition is given as a set of RDF statements (a {@link Model}).
 */
public class RemoveElementDefinition extends EditAspectModel {
   private final AspectModelUrn elementUrn;
   private AspectModelFile fileWithOriginalDefinition;
   private Model definition;
   private final URI targetLocation;

   public RemoveElementDefinition( final AspectModelUrn elementUrn ) {
      this( elementUrn, (URI) null );
   }

   public RemoveElementDefinition( final AspectModelUrn elementUrn, final URI targetLocation ) {
      this.elementUrn = elementUrn;
      this.targetLocation = targetLocation;
   }

   public RemoveElementDefinition( final AspectModelUrn elementUrn, final AspectModelFile targetFile ) {
      this( elementUrn, targetFile.sourceLocation().orElseThrow( () ->
            new ModelChangeException( "Can remove element defintion only from named file" ) ) );
   }

   @Override
   protected ModelChanges calculateChangesForFile( final AspectModelFile aspectModelFile ) {
      if ( targetLocation != null && !aspectModelFile.sourceLocation().map( targetLocation::equals ).orElse( false ) ) {
         return ModelChanges.NONE;
      }

      final Model model = aspectModelFile.sourceModel();
      final Resource elementResource = model.createResource( elementUrn.toString() );
      if ( !model.contains( elementResource, RDF.type, (RDFNode) null ) ) {
         return ModelChanges.NONE;
      }

      fileWithOriginalDefinition = aspectModelFile;
      final Model add = ModelFactory.createDefaultModel();
      definition = RdfUtil.getModelElementDefinition( elementResource );
      return new ModelChanges( "Remove definition of " + elementUrn, add, definition );
   }

   @Override
   public Change reverse() {
      return new EditAspectModel() {
         @Override
         protected ModelChanges calculateChangesForFile( final AspectModelFile aspectModelFile ) {
            return aspectModelFile.sourceLocation().equals( fileWithOriginalDefinition.sourceLocation() )
                  ? new ModelChanges( "Add back definition of " + elementUrn,
                  definition, ModelFactory.createDefaultModel() )
                  : ModelChanges.NONE;
         }

         @Override
         public Change reverse() {
            return RemoveElementDefinition.this;
         }
      };
   }
}
