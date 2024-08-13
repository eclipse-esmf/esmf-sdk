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

package org.eclipse.esmf.aspectmodel.edit;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.impl.DefaultAspectModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspectChangeManager implements ChangeContext {
   private static final Logger LOG = LoggerFactory.getLogger( AspectChangeManager.class );

   private final Deque<Change> undoStack = new ArrayDeque<>();
   private final Deque<Change> redoStack = new ArrayDeque<>();
   private final DefaultAspectModel aspectModel;
   private final AspectChangeManagerConfig config;
   private final Map<AspectModelFile, FileState> fileState = new HashMap<>();
   private boolean isUndoOperation = false;

   private enum FileState {
      CREATED, CHANGED, REMOVED
   }

   public AspectChangeManager( final AspectChangeManagerConfig config, final AspectModel aspectModel ) {
      this.config = config;
      resetFileStates();
      if ( aspectModel instanceof final DefaultAspectModel defaultAspectModel ) {
         this.aspectModel = defaultAspectModel;
      } else {
         throw new ModelChangeException( "AspectModel must be an instance of DefaultAspectModel" );
      }
   }

   public AspectChangeManager( final AspectModel aspectModel ) {
      this( AspectChangeManagerConfigBuilder.builder().build(), aspectModel );
   }

   public synchronized ChangeReport applyChange( final Change change ) {
      resetFileStates();
      isUndoOperation = false;
      final ChangeReport result = change.fire( this );
      updateAspectModelAfterChange();
      undoStack.offerLast( change.reverse() );
      return result;
   }

   public synchronized void undoChange() {
      if ( undoStack.isEmpty() ) {
         return;
      }
      isUndoOperation = true;
      resetFileStates();
      final Change change = undoStack.pollLast();
      change.fire( this );
      updateAspectModelAfterChange();
      redoStack.offerLast( change.reverse() );
   }

   public synchronized void redoChange() {
      if ( redoStack.isEmpty() ) {
         return;
      }
      resetFileStates();
      final Change change = redoStack.pollLast();
      isUndoOperation = false;
      change.fire( this );
      updateAspectModelAfterChange();
      undoStack.offerLast( change.reverse() );
   }

   private void updateAspectModelAfterChange() {
      final AspectModel updatedModel = new AspectModelLoader().loadAspectModelFiles( aspectModel.files() );
      aspectModel.setMergedModel( updatedModel.mergedModel() );
      aspectModel.setElements( updatedModel.elements() );
      aspectModel.setFiles( updatedModel.files() );

      final Map<AspectModelFile, FileState> updatedFileState = new HashMap<>();
      for ( final Map.Entry<AspectModelFile, FileState> stateEntry : fileState.entrySet() ) {
         final AspectModelFile file = stateEntry.getKey();
         final FileState state = stateEntry.getValue();

         if ( file instanceof final RawAspectModelFile rawFile ) {
            final Optional<AspectModelFile> updatedAspectModelFile = aspectModel.files().stream()
                  .filter( f -> f.sourceLocation().isPresent() )
                  .filter( f -> f.sourceLocation().equals( file.sourceLocation() ) )
                  .findFirst();
            if ( updatedAspectModelFile.isEmpty() ) {
               continue;
            }
            updatedFileState.put( updatedAspectModelFile.get(), state );
         } else {
            updatedFileState.put( file, state );
         }
      }
      fileState.clear();
      fileState.putAll( updatedFileState );
   }

   @Override
   public Stream<AspectModelFile> aspectModelFiles() {
      return aspectModel.files().stream();
   }

   @Override
   public AspectChangeManagerConfig config() {
      return config;
   }

   @Override
   public Stream<AspectModelFile> createdFiles() {
      return fileState.entrySet().stream()
            .filter( entry -> entry.getValue() == FileState.CREATED )
            .map( Map.Entry::getKey );
   }

   @Override
   public Stream<AspectModelFile> modifiedFiles() {
      return fileState.entrySet().stream()
            .filter( entry -> entry.getValue() == FileState.CHANGED )
            .map( Map.Entry::getKey );
   }

   @Override
   public Stream<AspectModelFile> removedFiles() {
      return fileState.entrySet().stream()
            .filter( entry -> entry.getValue() == FileState.REMOVED )
            .map( Map.Entry::getKey );
   }

   @Override
   public void resetFileStates() {
      fileState.clear();
   }

   @Override
   public void indicateFileIsAdded( final AspectModelFile file ) {
      fileState.put( file, FileState.CREATED );
      aspectModel.files().add( file );
   }

   @Override
   public void indicateFileIsRemoved( final AspectModelFile file ) {
      fileState.put( file, FileState.REMOVED );
      aspectModel.files().remove( file );
   }

   @Override
   public void indicateFileHasChanged( final AspectModelFile file ) {
      // If the file was newly created, keep this state even if we now change the file content
      if ( fileState.get( file ) != FileState.CREATED ) {
         fileState.put( file, FileState.CHANGED );
      }
   }
}
