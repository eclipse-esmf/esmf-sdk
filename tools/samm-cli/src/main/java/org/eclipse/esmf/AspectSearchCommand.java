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

package org.eclipse.esmf;

import java.io.File;
import java.util.List;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.fs.FlatModelsRoot;
import org.eclipse.esmf.aspectmodel.scanner.AspectModelScanner;
import org.eclipse.esmf.aspectmodel.scanner.FileSystemScanner;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ModelElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command( name = AspectSearchCommand.COMMAND_NAME,
      description = "Search separate Properties files used by Aspect Model",
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AspectSearchCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "search";

   private static final Logger LOG = LoggerFactory.getLogger( AspectSearchCommand.class );

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ExternalResolverMixin customResolver;

   @CommandLine.Parameters( paramLabel = "INPUT", description = "Input file name of the Aspect Model .ttl file", arity = "1", index = "0" )
   private String input;

   @CommandLine.Option( names = { "--models-path", "-msp" }, description = "Path to Models, where have to search" )
   private String pathToModels = "-";

   @CommandLine.Option( names = { "--github", "-gh" }, description = "Use this flag to introduce what has to be scanned" )
   private boolean searchInGithub = false;

   @CommandLine.Option( names = { "--github-name", "-ghn" }, description = "Provide GitHub name. Example: eclipse-esmf/esmf-sdk." )
   private String gitHubName = "";

   @CommandLine.Option( names = { "--github-directory", "-ghd" }, description = "Provide GitHub directory for scanning" )
   private String gitHubDirectory = "";

   @CommandLine.Option( names = { "--github-branch", "-ghb" }, description = "Provide GitHub branch for scanning" )
   private String gitHubBranch = "";

   public String getInput() {
      return input;
   }

   @Override
   public void run() {
      final AspectModelScanner aspectModelScanner;
      final List<AspectModelFile> aspectModelFiles;

      if ( searchInGithub && (gitHubName.isBlank() || gitHubDirectory.isBlank()) ) {
         System.out.println( "Parameters '--github-url' and '--github-directory' have to be provided!" );
         return;
      } else if ( !searchInGithub ) {
         aspectModelScanner = new FileSystemScanner( pathToModels );
         aspectModelFiles = aspectModelScanner.find( input );
      } else {
         aspectModelScanner = new GitHubScanner( gitHubName, gitHubBranch.isBlank() ? "main" : gitHubBranch );
         aspectModelFiles = aspectModelScanner.find( input );
      }

      String header = String.format( "| %-60s | %-100s | %-50s |",
            "URN of the element", "File location", "Model source" );
      String separator = new String( new char[header.length()] ).replace( "\0", "-" );

      // Print Table header
      System.out.println( separator );
      System.out.println( header );
      System.out.println( separator );

      final AspectModel aspectModel = new AspectModelLoader().load( new File( input ) );

      if ( !aspectModelFiles.isEmpty() ) {
         for ( final AspectModelFile aspectModelFile : aspectModelFiles ) {
            processFile( aspectModelFile, aspectModel.elements().stream().map( ModelElement::urn ).toList() );
         }
      }
   }

   private void processFile( final AspectModelFile aspectModelFile, final List<AspectModelUrn> modelUrns ) {
      if ( aspectModelFile.sourceLocation().isPresent() ) {
         final File file = new File( aspectModelFile.sourceLocation().get() );

         final AspectModelLoader aspectModelLoader;
         final AspectModel aspectModel;

         if ( searchInGithub ) {
            final ResolutionStrategy resolutionStrategy = new GitHubStrategy( gitHubName, gitHubDirectory );
            aspectModelLoader = new AspectModelLoader( resolutionStrategy );

            aspectModel = aspectModelLoader.load( file );
         } else {
            final ResolutionStrategy resolutionStrategy = new FileSystemStrategy(
                  new FlatModelsRoot( file.toPath().getParent() ) );
            aspectModelLoader = new AspectModelLoader( resolutionStrategy );

            aspectModel = aspectModelLoader.load( file );
         }

         for ( final AspectModelUrn modelUrn : modelUrns ) {
            final List<AspectModelFile> modelFiles = aspectModel.files();
            for ( final AspectModelFile modelFile : modelFiles ) {
               if ( aspectModelLoader.containsDefinition( modelFile, modelUrn ) ) {
                  System.out.printf( "| %-60s | %-100s | %-50s |%n", modelUrn, file.getPath(), file.getName(), "" );
               }
            }
         }
      }
   }
}
