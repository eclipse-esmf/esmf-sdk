/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.generator.LanguageCollector;
import io.openmanufacturing.sds.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.AspectModelResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.FileSystemStrategy;
import io.openmanufacturing.sds.aspectmodel.resolver.ModelResolutionException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.services.TurtleLoader;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationReport;
import io.openmanufacturing.sds.aspectmodel.validation.services.AspectModelValidator;
import io.openmanufacturing.sds.exception.CommandException;
import io.vavr.CheckedFunction1;
import io.vavr.control.Option;
import io.vavr.control.Try;

public abstract class AbstractCommand implements Runnable {

   protected static final Logger LOG = LoggerFactory.getLogger( AbstractCommand.class );

   protected Try<VersionedModel> loadAndResolveModel( final File input ) {
      final File inputFile = input.getAbsoluteFile();
      final AspectModelUrn urn = fileToUrn( inputFile );
      return getModelRoot( inputFile ).flatMap( modelsRoot ->
            new AspectModelResolver().resolveAspectModel( new FileSystemStrategy( modelsRoot ), urn ) );
   }

   protected Try<VersionedModel> loadAndResolveModel( final File input, final ExternalResolverMixin resolverConfig ) {
      if ( resolverConfig.commandLine.isBlank() ) {
         return loadAndResolveModel( input );
      }
      final File inputFile = input.getAbsoluteFile();
      final AspectModelUrn urn = fileToUrn( inputFile );
      return new AspectModelResolver().resolveAspectModel( new ExternalResolverStrategy( resolverConfig.commandLine ), urn );
   }

   protected Try<Path> getModelRoot( final File inputFile ) {
      return Option.of( Paths.get( inputFile.getParent(), "..", ".." ) )
            .map( Path::toFile )
            .flatMap( file -> CheckedFunction1.lift( File::getCanonicalFile ).apply( file ) )
            .map( File::toPath )
            .filter( path -> path.toFile().exists() && path.toFile().isDirectory() )
            .toTry( () -> new ModelResolutionException( "Could not locate models root directory" ) );
   }

   protected AspectModelUrn fileToUrn( final File inputFile ) {
      final File versionDirectory = inputFile.getParentFile();
      if ( versionDirectory == null ) {
         throw new CommandException( "Could not determine parent directory of " + inputFile );
      }

      final String version = versionDirectory.getName();
      final File namespaceDirectory = versionDirectory.getParentFile();
      if ( namespaceDirectory == null ) {
         throw new CommandException( "Could not determine parent directory of " + versionDirectory );
      }

      final String namespace = namespaceDirectory.getName();
      final String aspectName = FilenameUtils.removeExtension( inputFile.getName() );
      final String urn = String.format( "urn:bamm:%s:%s#%s", namespace, version, aspectName );
      return new SdsAspectMetaModelResourceResolver().getAspectModelUrn( urn ).getOrElse( () -> {
         throw new CommandException( "The URN constructed from the input file path is invalid: " + urn );
      } );
   }

   protected Try<VersionedModel> loadButNotResolveModel( final File inputFile ) {
      try ( final InputStream inputStream = new FileInputStream( inputFile ) ) {
         final SdsAspectMetaModelResourceResolver metaModelResourceResolver = new SdsAspectMetaModelResourceResolver();
         return TurtleLoader.loadTurtle( inputStream ).flatMap( model ->
               metaModelResourceResolver.getBammVersion( model ).flatMap( metaModelVersion ->
                     metaModelResourceResolver.mergeMetaModelIntoRawModel( model, metaModelVersion ) ) );
      } catch ( final IOException exception ) {
         return Try.failure( exception );
      }
   }

   protected VersionedModel loadModelOrFail( final String modelFileName, final ExternalResolverMixin resolverConfig ) {
      final File inputFile = new File( modelFileName );
      final Try<VersionedModel> versionedModel = loadAndResolveModel( inputFile, resolverConfig );
      return versionedModel.recover( throwable -> {
         // Model can not be loaded, root cause e.g. File not found
         if ( throwable instanceof IllegalArgumentException ) {
            throw new CommandException( "Can not open file for reading: " + modelFileName, throwable );
         }

         if ( throwable instanceof ModelResolutionException ) {
            throw new CommandException( "Could not resolve all model elements", throwable );
         }

         // Another exception, e.g. syntax error. Let the validator handle this
         final AspectModelValidator validator = new AspectModelValidator();
         final ValidationReport report = validator.validate( versionedModel );

         if ( LOG.isWarnEnabled() ) {
            LOG.warn( report.toString() );
         }
         System.exit( 1 );
         return null;
      } ).get();
   }

   protected void generateDiagram( final String inputFileName, final AspectModelDiagramGenerator.Format targetFormat, final String outputFileName,
         final String languageTag, final ExternalResolverMixin resolverConfig ) throws IOException {
      final VersionedModel model = loadModelOrFail( inputFileName, resolverConfig );
      final AspectModelDiagramGenerator generator = new AspectModelDiagramGenerator( model );
      final Set<AspectModelDiagramGenerator.Format> targetFormats = new HashSet<>();
      targetFormats.add( targetFormat );
      final Set<Locale> languagesUsedInModel = LanguageCollector.collectUsedLanguages( model.getModel() );
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
      try ( final OutputStream stream = "-".equals( outputFileName ) ?
            new ProtectedOutputStream( System.out ) :
            new FileOutputStream( outputFileName ) ) {
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
