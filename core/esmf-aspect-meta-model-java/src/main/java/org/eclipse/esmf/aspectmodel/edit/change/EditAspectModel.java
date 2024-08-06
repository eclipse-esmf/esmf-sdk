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
   public record ModelChanges( Model add, Model remove, String description ) {
      public static final ModelChanges NONE = new ModelChanges( null, null, "" );
   }

   protected Map<AspectModelFile, ModelChanges> changesPerFile = null;

   synchronized protected void prepare( final ChangeContext changeContext ) {
      if ( changesPerFile == null ) {
         changesPerFile = changeContext.aspectModelFiles().stream()
               .map( file -> new AbstractMap.SimpleEntry<>( file, calculateChangesForFile( file ) ) )
               .filter( entry -> entry.getValue() != ModelChanges.NONE )
               .collect( Collectors.toMap( AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue ) );
      }
   }

   abstract protected ModelChanges calculateChangesForFile( AspectModelFile aspectModelFile );

   @Override
   public ChangeReport fire( final ChangeContext changeContext ) {
      prepare( changeContext );
      changesPerFile.forEach( ( file, modelChanges ) -> {
         if ( changeContext.aspectModelFiles().contains( file ) ) {
            file.sourceModel().add( modelChanges.add() );
            file.sourceModel().remove( modelChanges.remove() );
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
                  }
            ).toList()
      );
   }
}
