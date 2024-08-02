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
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public abstract class EditAspectModel implements Change {
   public record ModelChanges( Model add, Model remove ) {
      public static final ModelChanges NONE = new ModelChanges( null, null );
   }

   private Map<AspectModelFile, ModelChanges> changesPerFile = null;

   synchronized protected Map<AspectModelFile, ModelChanges> changesPerFile( final ChangeContext changeContext ) {
      if ( changesPerFile == null ) {
         changesPerFile = changeContext.aspectModel().files().stream()
               .map( file -> new AbstractMap.SimpleEntry<>( file, calculateChangesForFile( file ) ) )
               .filter( entry -> entry.getValue() != ModelChanges.NONE )
               .collect( Collectors.toMap( AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue ) );
      }
      return changesPerFile;
   }

   abstract protected ModelChanges calculateChangesForFile( AspectModelFile aspectModelFile );

   @Override
   public void fire( final ChangeContext changeContext ) {
      changesPerFile( changeContext ).forEach( ( file, modelChanges ) -> {
         if ( changeContext.aspectModel().files().contains( file ) ) {
            file.sourceModel().add( modelChanges.add() );
            file.sourceModel().remove( modelChanges.remove() );
         }
      } );
   }
}
