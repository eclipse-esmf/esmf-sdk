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

import static org.eclipse.esmf.FileInputHandler.modelsRootForFile;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.ResolverConfigurationMixin;
import org.eclipse.esmf.aspect.AspectEditCommand;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeManagerConfig;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeManagerConfigBuilder;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.change.MoveElementToExistingFile;
import org.eclipse.esmf.aspectmodel.edit.change.MoveElementToNewFile;
import org.eclipse.esmf.aspectmodel.edit.change.MoveElementToOtherNamespaceExistingFile;
import org.eclipse.esmf.aspectmodel.edit.change.MoveElementToOtherNamespaceNewFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Namespace;
import org.eclipse.esmf.metamodel.impl.DefaultNamespace;

import picocli.CommandLine;

@CommandLine.Command( name = AspectEditMoveCommand.COMMAND_NAME,
      description = "Move elements to other files or namespaces",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
@SuppressWarnings( "UseOfSystemOutOrSystemErr" )
public class AspectEditMoveCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "move";

   @CommandLine.ParentCommand
   private AspectEditCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ResolverConfigurationMixin resolverConfiguration;

   @CommandLine.Parameters(
         paramLabel = "ELEMENT",
         description = "Full URN or local name of the model element to move",
         arity = "1",
         index = "0"
   )
   private String elementName;

   @CommandLine.Parameters(
         paramLabel = "TARGETFILE",
         description = "Target file to move the element to",
         arity = "1",
         index = "1"
   )
   private String targetFile;

   @CommandLine.Parameters(
         paramLabel = "TARGETNAMESPACE",
         description = "Target namespace to move the element to",
         arity = "0..1",
         index = "2"
   )
   private String targetNamespace;

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

   @CommandLine.Option(
         names = { "--copy-file-header" },
         description = "If set, newly created files will contain the same file header (e.g., copyright notice) as the source file of the "
               + "move operation"
   )
   private boolean copyHeader;

   @CommandLine.Option(
         names = { "--force" },
         description = "Force creation/overwriting of existing files"
   )
   private boolean force;

   @Override
   public void run() {
      setDetails( details );
      setResolverConfig( resolverConfiguration );

      final String input = parentCommand.parentCommand.getInput();
      if ( !inputIsFile( input ) ) {
         throw new CommandException( "The edit command only works with local files." );
      }
      final File inputFile = absoluteFile( new File( input ) );

      // Move to other/new file in same namespace
      if ( targetNamespace == null ) {
         final File targetFileRelativeToInput = inputFile.toPath().getParent().resolve( targetFile ).toFile();
         if ( targetFileRelativeToInput.exists() ) {
            moveElementToExistingFile( targetFileRelativeToInput );
         } else {
            moveElementToNewFile( inputFile );
         }
         return;
      }

      // Move to other/new file in other namespace
      final AspectModelUrn targetNamespaceUrn = AspectModelUrn.from( targetNamespace )
            .getOrElseThrow( () -> new CommandException( "Target namespace is invalid: " + targetNamespace ) );
      final File targetFileInOtherNamespace = modelsRootForFile( inputFile )
            .resolve( targetNamespaceUrn.getNamespaceMainPart() )
            .resolve( targetNamespaceUrn.getVersion() )
            .resolve( targetFile )
            .toFile();
      if ( targetFileInOtherNamespace.exists() ) {
         moveElementToOtherNamespaceExistingFile( targetNamespaceUrn, targetFileInOtherNamespace );
      } else {
         moveElementToOtherNamespaceNewFile( targetNamespaceUrn, targetFileInOtherNamespace );
      }
   }

   /**
    * Supports the case {@code samm aspect Aspect.ttl edit move MyAspect newFile.ttl}
    */
   private void moveElementToNewFile( final File inputFile ) {
      final String input = parentCommand.parentCommand.getInput();
      final AspectModel aspectModel = getInputHandler( input ).loadAspectModel();

      // Do refactoring
      final ModelElement modelElement = determineModelElementToMove( aspectModel );
      if ( targetFile.contains( File.separator ) ) {
         throw new CommandException( "The target file name should not contain a path; only a file name." );
      }
      final URI targetFileUri = inputFile.toPath().getParent().resolve( targetFile ).toUri();
      final List<String> headerCommentForNewFile = copyHeader
            ? modelElement.getSourceFile().headerComment()
            : List.of();
      final Change move = new MoveElementToNewFile( modelElement, headerCommentForNewFile, targetFileUri );
      final AspectChangeManagerConfig config = AspectChangeManagerConfigBuilder.builder()
            .detailedChangeReport( details )
            .build();
      performRefactoring( aspectModel, move, config, dryRun ).ifPresent( changeContext -> {
         // Check & write changes to file system
         checkFilesystemConsistency( changeContext, force );
         performFileSystemWrite( changeContext );
      } );
   }

   /**
    * Supports the case {@code samm aspect Aspect.ttl edit move MyAspect newFile.ttl urn:samm:com.example.othernamespace:1.0.0}
    */
   private void moveElementToOtherNamespaceNewFile( final AspectModelUrn targetNamespaceUrn, final File targetFileInNewNamespace ) {
      final String input = parentCommand.parentCommand.getInput();
      final AspectModel aspectModel = getInputHandler( input ).loadAspectModel();

      // Do refactoring
      final ModelElement modelElement = determineModelElementToMove( aspectModel );
      if ( targetFile.contains( File.separator ) ) {
         throw new CommandException( "The target file name should not contain a path; only a file name: " + targetFile );
      }

      final Namespace namespace = new DefaultNamespace( targetNamespaceUrn, List.of(), Optional.empty() );
      final List<String> headerCommentForNewFile = copyHeader
            ? modelElement.getSourceFile().headerComment()
            : List.of();

      final Change move = new MoveElementToOtherNamespaceNewFile( modelElement, namespace, headerCommentForNewFile,
            targetFileInNewNamespace.toURI() );
      final AspectChangeManagerConfig config = AspectChangeManagerConfigBuilder.builder()
            .detailedChangeReport( details )
            .build();
      performRefactoring( aspectModel, move, config, dryRun ).ifPresent( changeContext -> {
         // Check & write changes to file system
         checkFilesystemConsistency( changeContext, force );
         performFileSystemWrite( changeContext );
      } );
   }

   /**
    * Support the case {@code samm aspect Aspect.ttl edit move MyAspect existingFile.ttl}
    */
   private void moveElementToExistingFile( final File targetFileRelativeToInput ) {
      final AspectModel sourceAspectModel = getInputHandler( parentCommand.parentCommand.getInput() ).loadAspectModel();
      final AspectModel targetAspectModel = getInputHandler( targetFileRelativeToInput ).loadAspectModel();

      // Create a consistent in-memory representation of both the source and target models.
      // On this Aspect Model we can perform the refactoring operation
      final AspectModel aspectModel = new AspectModelLoader().merge( sourceAspectModel, targetAspectModel );

      // Find the loaded AspectModelFile that corresponds to the input targetFile
      final ModelElement modelElement = determineModelElementToMove( aspectModel );
      final AspectModelFile targetAspectModelFile = determineTargetAspectModelFile( aspectModel, modelElement.getSourceFile().namespace() );

      // Do refactoring
      final Change move = new MoveElementToExistingFile( modelElement, targetAspectModelFile );
      final AspectChangeManagerConfig config = AspectChangeManagerConfigBuilder.builder()
            .detailedChangeReport( details )
            .build();
      performRefactoring( aspectModel, move, config, dryRun ).ifPresent( changeContext -> {
         // Check & write changes to file system
         checkFilesystemConsistency( changeContext, force );
         performFileSystemWrite( changeContext );
      } );
   }

   /**
    * Supports the case {@code samm aspect Aspect.ttl edit move MyAspect existingFile.ttl urn:samm:com.example.othernamespace:1.0.0}
    */
   private void moveElementToOtherNamespaceExistingFile( final AspectModelUrn targetNamespaceUrn, final File targetFileInOtherNamespace ) {
      final AspectModel sourceAspectModel = getInputHandler( parentCommand.parentCommand.getInput() ).loadAspectModel();
      final AspectModel targetAspectModel = getInputHandler( targetFileInOtherNamespace ).loadAspectModel();

      // Create a consistent in-memory representation of both the source and target models.
      // On this Aspect Model we can perform the refactoring operation
      final AspectModel aspectModel = new AspectModelLoader().merge( sourceAspectModel, targetAspectModel );

      // Find the loaded AspectModelFile that corresponds to the input targetFile
      final Namespace namespace = new DefaultNamespace( targetNamespaceUrn, List.of(), Optional.empty() );
      final AspectModelFile targetAspectModelFile = determineTargetAspectModelFile( aspectModel, namespace );

      // Do refactoring
      final ModelElement modelElement = determineModelElementToMove( aspectModel );
      final Change move = new MoveElementToOtherNamespaceExistingFile( modelElement, targetAspectModelFile, namespace );
      final AspectChangeManagerConfig config = AspectChangeManagerConfigBuilder.builder()
            .detailedChangeReport( details )
            .build();
      performRefactoring( aspectModel, move, config, dryRun ).ifPresent( changeContext -> {
         // Check & write changes to file system
         checkFilesystemConsistency( changeContext, force );
         performFileSystemWrite( changeContext );
      } );
   }

   private AspectModelFile determineTargetAspectModelFile( final AspectModel aspectModel, final Namespace targetNamespace ) {
      return aspectModel.files().stream()
            .filter( file -> file.namespace().urn().equals( targetNamespace.urn() )
                  && file.sourceLocation().map( uri -> Paths.get( uri ).toFile().getName().equals( targetFile ) ).orElse( false ) )
            .findFirst()
            .orElseThrow( () -> new CommandException( "Could not determine target file" ) );
   }

   private ModelElement determineModelElementToMove( final AspectModel aspectModel ) {
      final List<ModelElement> potentialElements = aspectModel.elements().stream()
            .filter( element -> !element.isAnonymous() )
            .filter( element -> element.urn().toString().endsWith( elementName ) )
            .toList();
      if ( potentialElements.isEmpty() ) {
         System.out.println( "Could not find element to move: " + elementName );
         System.exit( 1 );
      }
      if ( potentialElements.size() > 1 ) {
         System.out.println( "Found more than one element identified by " + elementName + ": "
               + potentialElements.stream()
               .map( element -> element.urn().toString() ).collect(
                     Collectors.joining( ", ", "[", "]" ) )
               + "\nPlease use the element's full URN." );
         System.exit( 1 );
      }
      return potentialElements.get( 0 );
   }
}
