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

import static org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver.fileToUrn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.esmf.aspectmodel.generator.LanguageCollector;
import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.aspectmodel.resolver.ExternalResolverStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationFormatter;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.AspectContext;
import org.eclipse.esmf.metamodel.ExtendedAspectContext;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Try;

public abstract class AbstractCommand implements Runnable {
   protected static final Logger LOG = LoggerFactory.getLogger( AbstractCommand.class );

   protected Try<VersionedModel> loadAndResolveModel( final File input, final ExternalResolverMixin resolverConfig ) {
      final Try<VersionedModel> versionedModel;
      if ( resolverConfig.commandLine.isBlank() ) {
         versionedModel = AspectModelResolver.loadAndResolveModel( input );
      } else {
         final AspectModelUrn urn = fileToUrn( input.getAbsoluteFile() );
         versionedModel = new AspectModelResolver().resolveAspectModel( new ExternalResolverStrategy( resolverConfig.commandLine ), urn );
      }
      return versionedModel;
   }

   protected AspectContext loadModelOrFail( final String modelFileName, final ExternalResolverMixin resolverConfig ) {
      final File inputFile = new File( modelFileName );
      final Try<VersionedModel> versionedModel = loadAndResolveModel( inputFile, resolverConfig );
      final Try<AspectContext> context = versionedModel.flatMap( model -> {
         final AspectModelUrn urn = fileToUrn( inputFile.getAbsoluteFile() );
         return AspectModelLoader.getSingleAspect( model, aspect -> aspect.getName().equals( urn.getName() ) )
               .map( aspect -> new AspectContext( model, aspect ) );
      } );

      return context.recover( throwable -> {
         // Model can not be loaded, root cause e.g. File not found
         if ( throwable instanceof IllegalArgumentException ) {
            throw new CommandException( "Can not open file for reading: " + modelFileName, throwable );
         }

         if ( throwable instanceof ModelResolutionException ) {
            throw new CommandException( "Could not resolve all model elements", throwable );
         }

         // Another exception, e.g. syntax error. Let the validator handle this
         final List<Violation> violations = new AspectModelValidator().validateModel( context.map( AspectContext::rdfModel ) );
         System.out.println( new ViolationFormatter().apply( violations ) );

         System.exit( 1 );
         return null;
      } ).get();
   }

   protected ExtendedAspectContext loadModelWithContext( final String modelFileName, final ExternalResolverMixin resolverConfig ) {
      final File inputFile = new File( modelFileName );
      final Try<VersionedModel> versionedModel = loadAndResolveModel( inputFile, resolverConfig );
      final Try<ExtendedAspectContext> context = versionedModel.flatMap( model -> {
         final AspectModelUrn urn = fileToUrn( inputFile.getAbsoluteFile() );
         return AspectModelLoader.getSingleAspectWithContext( model, aspect -> aspect.getName().equals( urn.getName() ) );
      } );

      return context.recover( throwable -> {
         // Model can not be loaded, root cause e.g. File not found
         if ( throwable instanceof IllegalArgumentException ) {
            throw new CommandException( "Can not open file for reading: " + modelFileName, throwable );
         }

         if ( throwable instanceof ModelResolutionException ) {
            throw new CommandException( "Could not resolve all model elements", throwable );
         }

         // Another exception, e.g. syntax error. Let the validator handle this
         final List<Violation> violations = new AspectModelValidator().validateModel( context.map( ExtendedAspectContext::rdfModel ) );
         System.out.println( new ViolationFormatter().apply( violations ) );

         System.exit( 1 );
         return null;
      } ).get();
   }

   protected void generateDiagram( final String inputFileName, final AspectModelDiagramGenerator.Format targetFormat, final String outputFileName,
         final String languageTag, final ExternalResolverMixin resolverConfig ) throws IOException {
      final AspectContext context = loadModelOrFail( inputFileName, resolverConfig );
      final AspectModelDiagramGenerator generator = new AspectModelDiagramGenerator( context.rdfModel() );
      final Set<AspectModelDiagramGenerator.Format> targetFormats = new HashSet<>();
      targetFormats.add( targetFormat );
      final Set<Locale> languagesUsedInModel = LanguageCollector.collectUsedLanguages( context.rdfModel().getModel() );
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
