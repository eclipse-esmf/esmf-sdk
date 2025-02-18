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

package org.eclipse.esmf.namespacepackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.ResolverConfigurationMixin;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeManagerConfig;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeManagerConfigBuilder;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeGroup;
import org.eclipse.esmf.aspectmodel.edit.change.AddAspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.Download;
import org.eclipse.esmf.aspectmodel.resolver.NamespacePackage;
import org.eclipse.esmf.aspectmodel.resolver.fs.ModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFileBuilder;
import org.eclipse.esmf.exception.SubCommandException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

/**
 * Command to import a Namespace Package (remote or local) into a models root directory
 */
@CommandLine.Command(
      name = PackageImportCommand.COMMAND_NAME,
      description = "Import Namespace Packages",
      subcommands = {
            CommandLine.HelpCommand.class
      },
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class PackageImportCommand extends AbstractCommand {
   private static final Logger LOG = LoggerFactory.getLogger( PackageImportCommand.class );
   public static final String COMMAND_NAME = "import";

   @CommandLine.ParentCommand
   public PackageCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ResolverConfigurationMixin resolverConfiguration;

   @CommandLine.Option(
         names = { "--dry-run" },
         description = "Emulate import operation and print a report of changes that would be performed"
   )
   private boolean dryRun;

   @CommandLine.Option(
         names = { "--details" },
         description = "When using --dry-run, print details about files that are added"
   )
   private boolean details;

   @CommandLine.Option(
         names = { "--force" },
         description = "Force creation/overwriting of existing files"
   )
   private boolean force;

   @Override
   public void run() {
      if ( resolverConfiguration.modelsRoots == null || resolverConfiguration.modelsRoots.isEmpty() ) {
         throw new SubCommandException(
               "I don't now where to import the package to, please set a models root using -mr or --models-root" );
      }

      final File modelsRootLocation = new File( resolverConfiguration.modelsRoots.get( 0 ) );
      if ( modelsRootLocation.exists() && !modelsRootLocation.isDirectory() ) {
         throw new SubCommandException( "Given models root is not a directory: " + modelsRootLocation );
      }
      mkdirs( modelsRootLocation );

      // Load the Namespace Package and create a "add file" change for each file in it
      final ModelsRoot modelsRoot = new StructuredModelsRoot( modelsRootLocation.toPath() );
      final Change changes = new ChangeGroup( loadNamespacePackageFromInput().loadContents().map( file -> {
               // Set the destination inside the target models root for each Aspect Model File
               final URI targetLocation = modelsRoot.directoryForNamespace( file.namespaceUrn() )
                     .resolve( file.filename().orElseThrow() )
                     .toUri();
               return RawAspectModelFileBuilder.builder()
                     .sourceModel( file.sourceModel() )
                     .sourceLocation( Optional.of( targetLocation ) )
                     .headerComment( file.headerComment() )
                     .build();
            } )
            .<Change> map( AddAspectModelFile::new )
            .toList() );

      final AspectChangeManagerConfig config = AspectChangeManagerConfigBuilder.builder()
            .detailedChangeReport( details )
            .build();
      performRefactoring( new AspectModelLoader().emptyModel(), changes, config, dryRun, force );
   }

   private NamespacePackage loadNamespacePackageFromInput() {
      final String path = parentCommand.getInput();
      final File packageFile = new File( path );
      if ( packageFile.exists() ) {
         if ( !packageFile.isFile() ) {
            throw new SubCommandException( "Given input namespace package path is not a file: " + packageFile );
         }
         try {
            return new NamespacePackage( IOUtils.toByteArray( new FileInputStream( packageFile ) ), packageFile.toURI() );
         } catch ( final IOException exception ) {
            throw new SubCommandException( "Could not read namespace package file: " + packageFile );
         }
      }

      try {
         final URL url = new URL( path );
         LOG.debug( "Trying to download {}", url );
         final Map<String, String> headers = Map.of( "Accept", "application/zip" );
         final HttpResponse<byte[]> httpResponse = new Download().downloadFileAsResponse( url, headers );
         if ( httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300 ) {
            return new NamespacePackage( httpResponse.body(), url.toURI() );
         }
         throw new SubCommandException( "Could not download file (status code: " + httpResponse.statusCode() + ")" );
      } catch ( final MalformedURLException exception ) {
         throw new SubCommandException( "Given input namespace package is neither a valid file nor URL: " + path );
      } catch ( final URISyntaxException exception ) {
         throw new SubCommandException( "Can not download files from URLs that are not also valid URIs" );
      }
   }

   private void mkdirs( final File directory ) {
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
}
