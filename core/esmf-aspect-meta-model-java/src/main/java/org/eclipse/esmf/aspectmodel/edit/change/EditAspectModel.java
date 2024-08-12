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

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;

import org.apache.jena.rdf.model.Model;

public abstract class EditAspectModel extends AbstractChange {
   protected record ModelChanges( Model add, Model remove, String description ) {
      public static final ModelChanges NONE = new ModelChanges( null, null, "" );
   }

   abstract protected ModelChanges calculateChangesForFile( AspectModelFile aspectModelFile );

   @Override
   public ChangeReport fire( final ChangeContext changeContext ) {
      final Map<AspectModelFile, ModelChanges> changesPerFile = changeContext.aspectModelFiles()
            .map( file -> Map.entry( file, calculateChangesForFile( file ) ) )
            .filter( entry -> entry.getValue() != ModelChanges.NONE )
            .collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) );

      changesPerFile.forEach( ( file, modelChanges ) -> {
         if ( changeContext.aspectModelFiles().anyMatch( file::equals ) ) {
            file.sourceModel().add( modelChanges.add() );
            file.sourceModel().remove( modelChanges.remove() );

            if ( !modelChanges.add().isEmpty() || !modelChanges.remove().isEmpty() ) {
               changeContext.indicateFileHasChanged( file );
            }
         }
      } );

      return new ChangeReport.MultipleEntries(
            changesPerFile.entrySet().stream().<ChangeReport> map( entry -> {
               final AspectModelFile file = entry.getKey();
               final ModelChanges modelChanges = entry.getValue();
               return new ChangeReport.EntryWithDetails( modelChanges.description(), Map.of(
                           "Add content in " + show( file ), modelChanges.add(),
                           "Remove content from " + show( file ), modelChanges.remove() )
                     .entrySet().stream()
                     .filter( descriptionEntry -> !descriptionEntry.getValue().isEmpty() )
                     .collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) ) );
            } ).toList()
      );
   }
}
