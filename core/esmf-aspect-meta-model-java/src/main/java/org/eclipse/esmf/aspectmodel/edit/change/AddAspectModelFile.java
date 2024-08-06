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
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.edit.ModelChangeException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class AddAspectModelFile extends AbstractChange {
   private final AspectModelFile newFile;

   public AddAspectModelFile( final AspectModelFile newFile ) {
      this.newFile = newFile;
   }

   @Override
   public ChangeReport fire( final ChangeContext changeContext ) {
      changeContext.aspectModelFiles().add( newFile );
      final Model contentToAdd = ModelFactory.createDefaultModel();
      contentToAdd.add( newFile.sourceModel() );
      return new ChangeReport.EntryWithDetails( "Add file " + show( newFile ),
            Map.of( "Model content to add", contentToAdd ) );
   }

   @Override
   public Change reverse() {
      return new Change() {
         @Override
         public ChangeReport fire( final ChangeContext changeContext ) {
            final AspectModelFile file = fileToRemove( changeContext );
            changeContext.aspectModelFiles().remove( file );
            return new ChangeReport.EntryWithDetails( "Remove file " + show( file ),
                  Map.of( "model content to remove", file.sourceModel() ) );
         }

         @Override
         public Change reverse() {
            return AddAspectModelFile.this;
         }

         private AspectModelFile fileToRemove( final ChangeContext changeContext ) {
            return changeContext.aspectModelFiles().stream()
                  .filter( file -> file.sourceLocation().equals( newFile.sourceLocation() ) )
                  .findFirst()
                  .orElseThrow( () -> new ModelChangeException( "Unable to remove Aspect Model File" ) );
         }
      };
   }
}
