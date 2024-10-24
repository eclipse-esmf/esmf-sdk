/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.esmf.aspectmodel.edit.AspectChangeManager;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeManagerConfig;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.edit.ChangeReportFormatter;
import org.eclipse.esmf.aspectmodel.generator.LanguageCollector;
import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;

@SuppressWarnings( "UseOfSystemOutOrSystemErr" )
public abstract class AbstractCommand implements Runnable {
   private boolean details;
   private ResolverConfigurationMixin resolverConfig;

   protected void setDetails( final boolean details ) {
      this.details = details;
   }

   protected void setResolverConfig( final ResolverConfigurationMixin resolverConfig ) {
      this.resolverConfig = resolverConfig;
   }

   protected boolean inputIsFile( final String input ) {
      return new File( input ).exists();
   }

   protected File absoluteFile( final File inputFile ) {
      return inputFile.isAbsolute()
            ? inputFile
            : Path.of( System.getProperty( "user.dir" ) ).resolve( inputFile.toPath() ).toFile().getAbsoluteFile();
   }

   protected InputHandler getInputHandler( final File input ) {
      return new FileInputHandler( input.getAbsolutePath(), resolverConfig, details );
   }

   protected InputHandler getInputHandler( final String input ) {
      if ( FileInputHandler.appliesToInput( input ) ) {
         return new FileInputHandler( input, resolverConfig, details );
      } else if ( AspectModelUrnInputHandler.appliesToInput( input ) ) {
         return new AspectModelUrnInputHandler( input, resolverConfig, details );
      } else if ( GitHubUrlInputHandler.appliesToInput( input ) ) {
         return new GitHubUrlInputHandler( input, resolverConfig, details );
      }
      throw new CommandException( "Can not find file: " + input );
   }

   protected void generateDiagram( final String input, final AspectModelDiagramGenerator.Format targetFormat,
         final String outputFileName, final String languageTag ) throws IOException {
      final Aspect aspect = getInputHandler( input ).loadAspect();
      final AspectModelDiagramGenerator generator = new AspectModelDiagramGenerator( aspect );
      final Set<AspectModelDiagramGenerator.Format> targetFormats = new HashSet<>();
      targetFormats.add( targetFormat );
      final Set<Locale> languagesUsedInModel = LanguageCollector.collectUsedLanguages( aspect );
      if ( !languagesUsedInModel.contains( Locale.forLanguageTag( languageTag ) ) ) {
         throw new CommandException( String.format( "The model does not contain the desired language: %s.", languageTag ) );
      }
      // we intentionally override the name of the generated artifact here to the name explicitly desired by the user (outputFileName),
      // as opposed to what the model thinks it should be called (name)
      generator.generateDiagrams( targetFormats, Locale.forLanguageTag( languageTag ), name -> getStreamForFile( outputFileName ) );
   }

   protected FileOutputStream getStreamForFile( final String artifactPath, final String artifactName, final String baseOutputPath ) {
      try {
         final File directory = new File( baseOutputPath + File.separator + artifactPath );
         directory.mkdirs();
         final File file = new File( directory.getPath() + File.separator + artifactName );
         return new FileOutputStream( file );
      } catch ( final FileNotFoundException exception ) {
         throw new CommandException( "Can not open file for reading: " + artifactName, exception );
      }
   }

   protected OutputStream getStreamForFile( final String outputFileName ) {
      final boolean isOutputToFile = !"-".equals( outputFileName );
      if ( isOutputToFile ) {
         ensureDirectoryExists( outputFileName );
         try {
            return new FileOutputStream( outputFileName );
         } catch ( final FileNotFoundException exception ) {
            throw new CommandException( exception );
         }
      } else {
         return new ProtectedOutputStream( System.out );
      }
   }

   protected void withOutputStream( final String outputFileName, final Consumer<OutputStream> worker ) {
      try ( final OutputStream stream = "-".equals( outputFileName )
            ? new ProtectedOutputStream( System.out )
            : new FileOutputStream( outputFileName ) ) {
         worker.accept( stream );
      } catch ( final IOException exception ) {
         throw new CommandException( exception );
      }
   }

   private void ensureDirectoryExists( final String filePath ) {
      final String parentDirectory = getParentDirectory( filePath );
      if ( null == parentDirectory ) {
         return;
      }
      final File directory = new File( parentDirectory );
      directory.mkdirs();
   }

   private String getParentDirectory( final String filePath ) {
      return new File( filePath ).getParent();
   }

   protected Optional<AspectChangeManager> performRefactoring( final AspectModel aspectModel, final Change change,
         final AspectChangeManagerConfig config, final boolean dryRun ) {
      final AspectChangeManager changeContext = new AspectChangeManager( config, aspectModel );
      final ChangeReport changeReport = changeContext.applyChange( change );
      if ( dryRun ) {
         System.out.println( "Changes to be performed" );
         System.out.println( "=======================" );
         System.out.println( ChangeReportFormatter.INSTANCE.apply( changeReport, config ) );
         return Optional.empty();
      }
      return Optional.of( changeContext );
   }

   protected void performFileSystemWrite( final AspectChangeManager changeContext ) {
      changeContext.removedFiles()
            .map( fileToRemove -> Paths.get( fileToRemove.sourceLocation().orElseThrow() ).toFile() )
            .filter( file -> !file.delete() )
            .forEach( file -> {
               throw new CommandException( "Could not delete file: " + file );
            } );
      changeContext.createdFiles().forEach( fileToCreate -> {
         final File file = Paths.get( fileToCreate.sourceLocation().orElseThrow() ).toFile();
         file.getParentFile().mkdirs();
         AspectSerializer.INSTANCE.write( fileToCreate );
      } );
      changeContext.modifiedFiles().forEach( AspectSerializer.INSTANCE::write );
   }

   protected void checkFilesystemConsistency( final AspectChangeManager changeContext, final boolean force ) {
      final List<String> messages = new ArrayList<>();
      changeContext.removedFiles().map( AspectSerializer.INSTANCE::aspectModelFileUrl ).forEach( url -> {
         if ( !url.getProtocol().equals( "file" ) ) {
            messages.add( "File should be removed, but it is not identified by a file: URL: " + url );
         }
         final File file = new File( URI.create( url.toString() ) );
         if ( !file.exists() ) {
            messages.add( "File should be removed, but it does not exist: " + file );
         }
      } );

      changeContext.createdFiles().map( AspectSerializer.INSTANCE::aspectModelFileUrl ).forEach( url -> {
         if ( !url.getProtocol().equals( "file" ) ) {
            messages.add( "New file should be written, but it is not identified by a file: URL: " + url );
         }
         final File file = new File( URI.create( url.toString() ) );
         if ( file.exists() && !force ) {
            messages.add(
                  "New file should be written, but it already exists: " + file + ". Use the --force flag to force overwriting." );
         }
         if ( file.exists() && force && !file.canWrite() ) {
            messages.add( "New file should be written, but it is not writable:" + file );
         }
      } );

      changeContext.modifiedFiles().map( AspectSerializer.INSTANCE::aspectModelFileUrl ).forEach( url -> {
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

      if ( !messages.isEmpty() ) {
         System.out.println( "Encountered problems, canceling writing." );
         messages.forEach( message -> System.out.println( "- " + message ) );
         System.exit( 1 );
      }
   }
}
