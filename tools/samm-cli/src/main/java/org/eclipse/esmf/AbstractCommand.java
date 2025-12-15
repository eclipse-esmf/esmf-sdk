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

package org.eclipse.esmf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.edit.AspectChangeManager;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.edit.ChangeReportFormatter;
import org.eclipse.esmf.aspectmodel.edit.WriteConfig;
import org.eclipse.esmf.aspectmodel.edit.WriteConfigBuilder;
import org.eclipse.esmf.aspectmodel.edit.WriteResult;
import org.eclipse.esmf.aspectmodel.generator.LanguageCollector;
import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import org.eclipse.esmf.aspectmodel.generator.diagram.DiagramGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.diagram.DiagramGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.resolver.fs.ModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.aspectmodel.validation.ValidatorConfig;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.exception.SubCommandException;
import org.eclipse.esmf.metamodel.Aspect;

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
      return new FileInputHandler( input.getAbsolutePath(), resolverConfig, details,
            new ValidatorConfig.Builder().disableValidation( true ).build() );
   }

   protected InputHandler getInputHandler( final String input ) {
      return getInputHandler( input, new ValidatorConfig.Builder().disableValidation( true ).build() );
   }

   protected InputHandler getInputHandler( final String input, final ValidatorConfig validatorConfig ) {
      if ( FileInputHandler.appliesToInput( input ) ) {
         return new FileInputHandler( input, resolverConfig, details, validatorConfig );
      } else if ( AspectModelUrnInputHandler.appliesToInput( input ) ) {
         return new AspectModelUrnInputHandler( input, resolverConfig, details, validatorConfig );
      } else if ( GitHubUrlInputHandler.appliesToInput( input ) ) {
         return new GitHubUrlInputHandler( input, resolverConfig, details, validatorConfig );
      }
      throw new CommandException( "File not found: " + input );
   }

   protected void generateDiagram( final String input, final DiagramGenerationConfig.Format targetFormat,
         final String outputFileName, final String languageTag ) throws IOException {
      final Aspect aspect = getInputHandler( input, new ValidatorConfig.Builder().disableValidation( true ).build() ).loadAspect();
      final Set<Locale> languagesUsedInModel = LanguageCollector.collectUsedLanguages( aspect );
      if ( !languagesUsedInModel.contains( Locale.forLanguageTag( languageTag ) ) ) {
         throw new CommandException( String.format( "The model does not contain the desired language: %s.", languageTag ) );
      }
      // we intentionally override the name of the generated artifact here to the name explicitly desired by the user (outputFileName),
      // as opposed to what the model thinks it should be called (name)
      final DiagramGenerationConfig config = DiagramGenerationConfigBuilder.builder()
            .format( targetFormat )
            .language( Locale.forLanguageTag( languageTag ) )
            .build();
      final AspectModelDiagramGenerator generator = new AspectModelDiagramGenerator( aspect, config );
      generator.generate( name -> getStreamForFile( outputFileName ) );
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

   protected void performRefactoring( final AspectChangeManager changeContext, final Change change,
         final boolean dryRun, final boolean forceOverwrite ) {
      final ChangeReport changeReport = changeContext.applyChange( change );
      if ( dryRun ) {
         System.out.println( "Changes to be performed" );
         System.out.println( "=======================" );
         System.out.println( ChangeReportFormatter.INSTANCE.apply( changeReport, changeContext.config() ) );
         return;
      }

      final WriteConfig writeConfig = WriteConfigBuilder.builder().forceOverwrite( forceOverwrite ).build();
      final WriteResult writeResult = changeContext.writeChangesToDisk( writeConfig );
      System.err.print( writeResult.accept( new WriteResult.Visitor<String>() {
         @Override
         public String visitSuccess( final WriteResult.Success success ) {
            return "";
         }

         @Override
         public String visitWriteFailure( final WriteResult.WriteFailure failure ) {
            return "Writing failed:\n"
                  + failure.errorMessages().stream()
                  .map( message -> "- " + message )
                  .collect( Collectors.joining( "\n" ) ) + "\n";
         }

         @Override
         public String visitPreconditionsNotMet( final WriteResult.PreconditionsNotMet preconditionsNotMet ) {
            final StringBuilder builder = new StringBuilder();
            builder.append( "Encountered problems, cancelling writing:\n" );
            preconditionsNotMet.errorMessages().stream()
                  .map( message -> "- " + message + "\n" )
                  .forEach( builder::append );
            if ( preconditionsNotMet.canBeFixedByOverwriting() ) {
               builder.append( "Add --force to force overwriting existing files.\n" );
            }
            return builder.toString();
         }
      } ) );

      if ( writeResult instanceof WriteResult.Success ) {
         return;
      }
      System.exit( 1 );
   }

   protected void mkdirs( final File directory ) {
      if ( directory.exists() ) {
         if ( directory.isDirectory() ) {
            return;
         }
         throw new SubCommandException( "Could not create directory " + directory + ": It already exists but is not a directory" );
      }
      if ( !directory.mkdirs() ) {
         throw new SubCommandException( "Could not create directory: " + directory );
      }
   }

   protected ModelsRoot createModelsRoot( final File modelsRootLocation ) {
      if ( modelsRootLocation.exists() && !modelsRootLocation.isDirectory() ) {
         throw new SubCommandException( "Given models root is not a directory: " + modelsRootLocation );
      }
      mkdirs( modelsRootLocation );

      // Load the Namespace Package and create a "add file" change for each file in it
      return new StructuredModelsRoot( modelsRootLocation.toPath() );
   }
}
