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

package org.eclipse.esmf.aspect.edit;

import java.io.File;
import java.util.Optional;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.ResolverConfigurationMixin;
import org.eclipse.esmf.aspect.AspectEditCommand;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeManagerConfig;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeManagerConfigBuilder;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.change.CopyFileWithIncreasedNamespaceVersion;
import org.eclipse.esmf.aspectmodel.edit.change.CopyNamespaceWithIncreasedVersion;
import org.eclipse.esmf.aspectmodel.edit.change.IncreaseVersion;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.Namespace;

import picocli.CommandLine;

@CommandLine.Command( name = AspectEditNewVersionCommand.COMMAND_NAME,
      description = "Create a new version of a file or namespace",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class AspectEditNewVersionCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "newversion";

   @CommandLine.ParentCommand
   private AspectEditCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ResolverConfigurationMixin resolverConfiguration;

   @CommandLine.Option(
         names = { "--dry-run" },
         description = "Emulate edit operation and print a report of changes that would be performed"
   )
   private boolean dryRun;

   @CommandLine.Option(
         names = { "--details" },
         description = "When using --dry-run, print details about RDF statements that are added and removed"
   )
   private boolean details;

   @CommandLine.ArgGroup( multiplicity = "1" )
   private VersionPart versionPart;

   static class VersionPart {
      @CommandLine.Option(
            names = "--major",
            required = true,
            description = "Increase the major version"
      )
      boolean increaseMajor;

      @CommandLine.Option(
            names = "--minor",
            required = true,
            description = "Increase the minor version"
      )
      boolean increaseMinor;

      @CommandLine.Option(
            names = "--micro",
            required = true,
            description = "Increase the micro version"
      )
      boolean increaseMicro;
   }

   @CommandLine.Option(
         names = { "--force" },
         description = "Force creation/overwriting of existing files"
   )
   private boolean force;

   @Override
   public void run() {
      setDetails( details );
      setResolverConfig( resolverConfiguration );

      final IncreaseVersion increaseVersion;
      if ( versionPart.increaseMajor ) {
         increaseVersion = IncreaseVersion.MAJOR;
      } else if ( versionPart.increaseMinor ) {
         increaseVersion = IncreaseVersion.MINOR;
      } else {
         increaseVersion = IncreaseVersion.MICRO;
      }

      final String input = parentCommand.parentCommand.getInput();
      final Optional<File> inputFile = Optional.of( new File( input ) ).filter( File::exists );
      final AspectModelLoader aspectModelLoader = getInputHandler( input ).aspectModelLoader();

      final AspectModel aspectModel = inputFile.map( aspectModelLoader::load ).orElseGet( () -> {
         final AspectModelUrn urn = AspectModelUrn.from( input ).getOrElseThrow( () ->
               new CommandException( "Target is no valid namespace URN: " + input ) );
         return aspectModelLoader.loadAspectModelFiles( aspectModelLoader.loadContentsForNamespace( urn ).toList() );
      } );

      final Change copy;
      if ( inputFile.isPresent() ) {
         final AspectModelFile targetFile = aspectModel.files().stream()
               .filter( file -> file.sourceLocation().equals( inputFile.map( File::toURI ) ) )
               .findFirst()
               .orElseThrow( () -> new CommandException( "Could not determine the file to copy" ) );

         copy = new CopyFileWithIncreasedNamespaceVersion( targetFile, increaseVersion );
      } else {
         final Namespace targetNamespace = aspectModel.files().stream()
               .map( AspectModelFile::namespace )
               .filter( namespace -> namespace.urn().toString().equals( input ) )
               .findFirst()
               .orElseThrow( () -> new CommandException( "Could not determine the namespace to copy" ) );
         copy = new CopyNamespaceWithIncreasedVersion( targetNamespace, increaseVersion );
      }

      final AspectChangeManagerConfig config = AspectChangeManagerConfigBuilder.builder()
            .detailedChangeReport( details )
            .build();
      performRefactoring( aspectModel, copy, config, dryRun ).ifPresent( changeContext -> {
         // Check & write changes to file system
         checkFilesystemConsistency( changeContext, force );
         performFileSystemWrite( changeContext );
      } );
   }
}
