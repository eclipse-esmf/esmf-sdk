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

import java.util.Map;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class RemoveElementDefinition extends EditAspectModel {
   private final AspectModelUrn elementUrn;
   private AspectModelFile fileWithOriginalDefinition;
   private Model definition;

   public RemoveElementDefinition( final AspectModelUrn elementUrn ) {
      this.elementUrn = elementUrn;
   }

   @Override
   protected ModelChanges calculateChangesForFile( final AspectModelFile aspectModelFile ) {
      final Model model = aspectModelFile.sourceModel();
      final Resource elementResource = model.createResource( elementUrn.toString() );
      if ( !model.contains( elementResource, RDF.type, (RDFNode) null ) ) {
         return ModelChanges.NONE;
      }

      fileWithOriginalDefinition = aspectModelFile;
      final Model add = ModelFactory.createDefaultModel();
      definition = RdfUtil.getModelElementDefinition( elementResource );
      return new ModelChanges( add, definition );
   }

   @Override
   public Change reverse() {
      return new EditAspectModel() {
         @Override
         protected ModelChanges calculateChangesForFile( final AspectModelFile aspectModelFile ) {
            return aspectModelFile.sourceLocation().equals( fileWithOriginalDefinition.sourceLocation() )
                  ? new ModelChanges( definition, ModelFactory.createDefaultModel() )
                  : ModelChanges.NONE;
         }

         @Override
         public Change reverse() {
            return RemoveElementDefinition.this;
         }

         @Override
         public ChangeReport report( final ChangeContext changeContext ) {
            return new ChangeReport.EntryWithDetails(
                  "Add back definition of " + elementUrn + " to " + show( fileWithOriginalDefinition ),
                  Map.of( "model content to add", definition ) );
         }
      };
   }

   @Override
   public ChangeReport report( final ChangeContext changeContext ) {
      changesPerFile( changeContext );
      return new ChangeReport.EntryWithDetails(
            "Remove definition of " + elementUrn + " from " + show( fileWithOriginalDefinition ),
            Map.of( "model content to remove", definition ) );
   }
}
