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
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.generator.LanguageCollector;
import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.EitherStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ExternalResolverStrategy;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.DetailedViolationFormatter;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationFormatter;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;

import io.vavr.control.Either;
import org.apache.commons.io.FilenameUtils;

public abstract class AbstractCommand implements Runnable {
   protected Path modelsRootForFile( final File file ) {
      return file.toPath().getParent().getParent().getParent();
   }

   protected AspectModel loadAspectModelOrFail( final String modelFileName, final ExternalResolverMixin resolverConfig ) {
      return loadAspectModelOrFail( modelFileName, resolverConfig, false );
   }

   protected File getInputFile( final String modelFileName ) {
      final File inputFile = new File( modelFileName );
      return inputFile.isAbsolute()
            ? inputFile
            : Path.of( System.getProperty( "user.dir" ) ).resolve( inputFile.toPath() ).toFile().getAbsoluteFile();
   }

   protected AspectModel loadAspectModelOrFail( final String modelFileName, final ExternalResolverMixin resolverConfig,
         final boolean details ) {
      final File absoluteFile = getInputFile( modelFileName );

      final ResolutionStrategy resolveFromWorkspace = new FileSystemStrategy( modelsRootForFile( absoluteFile ) );
      final ResolutionStrategy resolveFromCurrentDirectory = AspectModelLoader.DEFAULT_STRATEGY.get();
      final ResolutionStrategy resolutionStrategy = resolverConfig.commandLine.isBlank()
            ? new EitherStrategy( resolveFromWorkspace, resolveFromCurrentDirectory )
            : new ExternalResolverStrategy( resolverConfig.commandLine );

      final Either<List<Violation>, AspectModel> validModelOrViolations = new AspectModelValidator().loadModel( () ->
            new AspectModelLoader( resolutionStrategy ).load( absoluteFile ) );
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

   protected Aspect loadAspectOrFail( final String modelFileName, final ExternalResolverMixin resolverConfig ) {
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
         final String languageTag, final ExternalResolverMixin resolverConfig ) throws IOException {
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
}
