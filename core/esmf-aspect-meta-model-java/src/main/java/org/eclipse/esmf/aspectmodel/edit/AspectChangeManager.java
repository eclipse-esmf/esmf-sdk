/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.aspectmodel.serializer.SerializationException;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.impl.DefaultAspectModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AspectChangeManager is the central place to to changes/edits/refactorings of an {@link AspectModel}. The AspectChangeManager
 * wraps an AspectModel and allows applying instances of the {@link Change} class using the {@link #applyChange(Change)} method.
 * Calling this method returns a {@link ChangeReport} that describes the performed changes in a structured way. Use the
 * {@link ChangeReportFormatter} to render the ChangeReport to a structured string representation.
 * <br/>
 * Note the following points:
 * <ul>
 *    <li>Only one AspectChangeManager must wrap a given AspectModel at any time</li>
 *    <li>All changes are done <i>in-memory</i>. In order to write them to the file system, use {@link #writeChangesToDisk(WriteConfig)}
 *    </li>
 *    <li>After performing an {@link #applyChange(Change)}, {@link #undoChange()} or {@link #redoChange()} operation, and until the
 *    next call of one of them, the methods {@link #modifiedFiles()}, {@link #createdFiles()} and {@link #removedFiles()} indicate
 *    corresponding changes in the AspectModel's files.
 * </ul>
 */
public class AspectChangeManager implements ChangeContext {
   private static final Logger LOG = LoggerFactory.getLogger( AspectChangeManager.class );

   private final Deque<Change> undoStack = new ArrayDeque<>();
   private final Deque<Change> redoStack = new ArrayDeque<>();
   private final DefaultAspectModel aspectModel;
   private final AspectChangeManagerConfig config;
   private final Map<AspectModelFile, FileState> fileState = new HashMap<>();

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
      final ChangeReport result = change.fire( this );
      updateAspectModelAfterChange();
      undoStack.offerLast( change.reverse() );
      return result;
   }

   public synchronized void undoChange() {
      if ( undoStack.isEmpty() ) {
         return;
      }
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

   /**
    * Syncs all queued changes to the file system. This is the operation that acutally performs operations such as deleting, creating and
    * writing files.
    */
   public synchronized WriteResult writeChangesToDisk( final WriteConfig config ) {
      final WriteResult writeResult = checkFileSystemConsistency( config );
      if ( writeResult instanceof WriteResult.PreconditionsNotMet ) {
         return writeResult;
      }

      final WriteResult result = performFileSystemWrite();
      if ( result instanceof WriteResult.Success ) {
         resetFileStates();
      }
      return result;
   }

   protected WriteResult performFileSystemWrite() {
      final List<String> messages = new ArrayList<>();
      removedFiles()
            .map( fileToRemove -> Paths.get( fileToRemove.sourceLocation().orElseThrow() ).toFile() )
            .forEach( file -> {
               try {
                  Files.delete( file.toPath() );
               } catch ( final IOException exception ) {
                  messages.add( "Could not delete file: " + file );
               }
            } );

      createdFiles().forEach( fileToCreate -> {
         final File file = Paths.get( fileToCreate.sourceLocation().orElseThrow() ).toFile();
         if ( !file.getParentFile().exists() && !file.getParentFile().mkdirs() ) {
            messages.add( "Target path to write file could not be created: " + file );
         } else {
            try {
               AspectSerializer.INSTANCE.write( fileToCreate );
            } catch ( final SerializationException exception ) {
               messages.add( exception.getMessage() );
            }
         }
      } );

      modifiedFiles().forEach( aspectModelFile -> {
         try {
            AspectSerializer.INSTANCE.write( aspectModelFile );
         } catch ( final SerializationException exception ) {
            messages.add( exception.getMessage() );
         }
      } );

      return messages.isEmpty()
            ? new WriteResult.Success()
            : new WriteResult.WriteFailure( messages );
   }

   protected WriteResult checkFileSystemConsistency( final WriteConfig config ) {
      final List<String> messages = new ArrayList<>();
      final boolean[] canBeFixedByOverwriting = new boolean[1];
      removedFiles().map( AspectSerializer.INSTANCE::aspectModelFileUrl ).forEach( url -> {
         if ( !url.getProtocol().equals( "file" ) ) {
            messages.add( "File should be removed, but it is not identified by a file: URL: " + url );
         }
         final File file = new File( URI.create( url.toString() ) );
         if ( !file.exists() ) {
            messages.add( "File should be removed, but it does not exist: " + file );
         }
      } );

      createdFiles().map( AspectSerializer.INSTANCE::aspectModelFileUrl ).forEach( url -> {
         if ( !url.getProtocol().equals( "file" ) ) {
            messages.add( "New file should be written, but it is not identified by a file: URL: " + url );
         }
         final File file = new File( URI.create( url.toString() ) );
         if ( file.exists() && !config.forceOverwrite() ) {
            messages.add( "New file should be written, but it already exists: " + file );
            canBeFixedByOverwriting[0] = true;
         }
         if ( file.exists() && config.forceOverwrite() && !file.canWrite() ) {
            messages.add( "New file should be written, but it is not writable: " + file );
         }
      } );

      modifiedFiles().map( AspectSerializer.INSTANCE::aspectModelFileUrl ).forEach( url -> {
         if ( !url.getProtocol().equals( "file" ) ) {
            messages.add( "File should be modified, but it is not identified by a file: URL: " + url );
         }
         final File file = new File( URI.create( url.toString() ) );
         if ( !file.exists() ) {
            messages.add( "File should be modified, but it does not exist: " + file );
         }
         if ( !file.canWrite() ) {
            messages.add( "File should be modified, but it is not writable: " + file );
         }
         if ( !file.isFile() ) {
            messages.add( "File should be modified, but it is not a regular file: " + file );
         }
      } );

      return messages.isEmpty()
            ? new WriteResult.Success()
            : new WriteResult.PreconditionsNotMet( messages, canBeFixedByOverwriting[0] );
   }
}
