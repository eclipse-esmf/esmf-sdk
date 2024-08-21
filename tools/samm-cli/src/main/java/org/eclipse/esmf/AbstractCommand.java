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
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.edit.AspectChangeManager;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeManagerConfig;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.edit.ChangeReportFormatter;
import org.eclipse.esmf.aspectmodel.generator.LanguageCollector;
import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.ExternalResolverStrategy;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.github.GitHubStrategy;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.DetailedViolationFormatter;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationFormatter;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;

import io.vavr.control.Either;
import org.apache.commons.io.FilenameUtils;

@SuppressWarnings( "UseOfSystemOutOrSystemErr" )
public abstract class AbstractCommand implements Runnable {
   protected Path modelsRootForFile( final File file ) {
      return file.toPath().getParent().getParent().getParent();
   }

   protected AspectModel loadAspectModelOrFail( final String input, final ResolverConfigurationMixin resolverConfig ) {
      return loadAspectModelOrFail( input, resolverConfig, false );
   }

   protected File getInputFile( final String modelFileName ) {
      final File inputFile = new File( modelFileName );
      return inputFile.isAbsolute()
            ? inputFile
            : Path.of( System.getProperty( "user.dir" ) ).resolve( inputFile.toPath() ).toFile().getAbsoluteFile();
   }

   protected AspectModelLoader getAspectModelLoader( final Optional<File> modelFile, final ResolverConfigurationMixin resolverConfig ) {
      final List<ResolutionStrategy> strategies = new ArrayList<>();
      if ( modelFile.isPresent() ) {
         strategies.add( new FileSystemStrategy( modelsRootForFile( modelFile.get().getAbsoluteFile() ) ) );
      } else {
         strategies.add( AspectModelLoader.DEFAULT_STRATEGY.get() );
      }
      for ( final String modelsRoot : resolverConfig.modelsRoots ) {
         strategies.add( new FileSystemStrategy( new StructuredModelsRoot( Path.of( modelsRoot ) ) ) );
      }
      if ( !resolverConfig.commandLine.isBlank() ) {
         strategies.add( new ExternalResolverStrategy( resolverConfig.commandLine ) );
      }
      if ( resolverConfig.gitHubResolutionOptions != null && resolverConfig.gitHubResolutionOptions.enableGitHubResolution ) {
         strategies.add( new GitHubStrategy(
               resolverConfig.gitHubResolutionOptions.gitHubName,
               resolverConfig.gitHubResolutionOptions.gitHubBranch,
               resolverConfig.gitHubResolutionOptions.gitHubDirectory ) );
      }
      return new AspectModelLoader( strategies );
   }

   protected AspectModel loadAspectModelOrFail( final File modelFile, final ResolverConfigurationMixin resolverConfig,
         final boolean details ) {
      final File absoluteFile = modelFile.getAbsoluteFile();

      final Either<List<Violation>, AspectModel> validModelOrViolations = new AspectModelValidator().loadModel( () ->
            getAspectModelLoader( Optional.of( modelFile ), resolverConfig ).load( absoluteFile ) );
      if ( validModelOrViolations.isLeft() ) {
         final List<Violation> violations = validModelOrViolations.getLeft();
         if ( details ) {
            System.out.println( new DetailedViolationFormatter().apply( violations ) );
         } else {
            System.out.println( new ViolationFormatter().apply( violations ) );
         }
         System.exit( 1 );
         return null;
      }

      return validModelOrViolations.get();
   }

   protected AspectModel loadAspectModelOrFail( final String modelFileName, final ResolverConfigurationMixin resolverConfig,
         final boolean details ) {
      return loadAspectModelOrFail( getInputFile( modelFileName ), resolverConfig, details );
   }

   protected Aspect loadAspectOrFail( final String modelFileName, final ResolverConfigurationMixin resolverConfig ) {
      final File inputFile = new File( modelFileName );
      final AspectModel aspectModel = loadAspectModelOrFail( modelFileName, resolverConfig );
      final List<Aspect> aspects = aspectModel.aspects();
      if ( aspects.isEmpty() ) {
         throw new CommandException( new ModelResolutionException( "No Aspects were found in the model" ) );
      }
      if ( aspects.size() == 1 ) {
         return aspectModel.aspect();
      }
      final String expectedAspectName = FilenameUtils.removeExtension( inputFile.getName() );
      return aspectModel.aspects().stream()
            .filter( aspect -> aspect.getName().equals( expectedAspectName ) )
            .findFirst()
            .orElseThrow( () -> new ModelResolutionException(
                  "Found multiple Aspects in the file " + inputFile.getAbsolutePath() + ", but none is called '"
                        + expectedAspectName + "': " + aspects.stream().map( Aspect::getName )
                        .collect( Collectors.joining( ", " ) ) ) );
   }

   protected void generateDiagram( final String inputFileName, final AspectModelDiagramGenerator.Format targetFormat,
         final String outputFileName,
         final String languageTag, final ResolverConfigurationMixin resolverConfig ) throws IOException {
      final Aspect aspect = loadAspectOrFail( inputFileName, resolverConfig );
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
         } catch ( final FileNotFoundException e ) {
            throw new CommandException( e );
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
