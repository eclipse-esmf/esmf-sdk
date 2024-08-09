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
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.ExternalResolverMixin;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aspect.AspectEditCommand;
import org.eclipse.esmf.aspectmodel.AspectModelBuilder;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeContext;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeContextConfig;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeContextConfigBuilder;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.edit.ChangeReportFormatter;
import org.eclipse.esmf.aspectmodel.edit.change.MoveElementToExistingFile;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ModelElement;

import picocli.CommandLine;

@SuppressWarnings( "UseOfSystemOutOrSystemErr" )
@CommandLine.Command( name = AspectEditMoveCommand.COMMAND_NAME,
      description = "Move elements to other files or namespaces",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class AspectEditMoveCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "move";

   @CommandLine.ParentCommand
   private AspectEditCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ExternalResolverMixin customResolver;

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
      final File file = new File( targetFile );
      if ( file.exists() ) {
         if ( targetNamespace == null ) {
            moveElementToExistingFile();
         } else {
            moveElementToOtherNamespaceExistingFile();
         }
      } else {
         if ( targetNamespace == null ) {
            moveElementToNewFile();
         } else {
            moveElementToOtherNamespaceNewFile();
         }
      }
   }

   /**
    * Supports the case {@code samm aspect Aspect.ttl edit move :MyAspect newFile.ttl}
    */
   private void moveElementToNewFile() {
      // TODO
   }

   /**
    * Supports the case {@code samm aspect Aspect.ttl edit move :MyAspect existingFile.ttl urn:samm:com.example.othernamespace:1.0.0}
    */
   private void moveElementToOtherNamespaceExistingFile() {
      // TODO
   }

   /**
    * Supports the case {@code samm aspect Aspect.ttl edit move :MyAspect newFile.ttl urn:samm:com.example.othernamespace:1.0.0}
    */
   private void moveElementToOtherNamespaceNewFile() {
      // TODO
   }

   /**
    * Support the case {@code samm aspect Aspect.ttl edit move MyAspect existingFile.ttl}
    */
   private void moveElementToExistingFile() {
      final AspectModel sourceAspectModel = loadAspectModelOrFail( parentCommand.parentCommand.getInput(), customResolver );
      final AspectModel targetAspectModel = loadAspectModelOrFail( targetFile, customResolver );

      // Create a consistent in-memory representation of both the source and target models.
      // On this Aspect Model we can perform the refactoring operation
      final AspectModel aspectModel = AspectModelBuilder.merge( sourceAspectModel, targetAspectModel );

      // Find the loaded AspectModelFile that corresponds to the input targetFile
      final URI targetFileUri = getInputFile( targetFile ).toURI();
      final AspectModelFile targetAspectModelFile = aspectModel.files().stream()
            .filter( file -> file.sourceLocation().map( uri -> uri.equals( targetFileUri ) ).orElse( false ) )
            .findFirst()
            .orElseThrow( () -> new CommandException( "Could not determine target file" ) );

      // Determine the URN for the element to move
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

      // Perform the refactoring
      final AspectChangeContextConfig config = AspectChangeContextConfigBuilder.builder()
            .detailedChangeReport( details )
            .build();
      final AspectChangeContext changeContext = new AspectChangeContext( config, aspectModel );
      final ModelElement modelElement = potentialElements.get( 0 );
      final Change move = new MoveElementToExistingFile( modelElement, targetAspectModelFile );
      final ChangeReport changeReport = changeContext.applyChange( move );
      if ( dryRun ) {
         System.out.println( "Changes to be performed" );
         System.out.println( "=======================" );
         System.out.println( ChangeReportFormatter.INSTANCE.apply( changeReport, config ) );
         System.exit( 0 );
      }

      checkFilesystemConsistency( changeContext );
      performFileSystemWrite( changeContext );
   }

   private void performFileSystemWrite( final AspectChangeContext changeContext ) {
      for ( final AspectModelFile fileToRemove : changeContext.removedFiles() ) {
         final File file = Paths.get( fileToRemove.sourceLocation().orElseThrow() ).toFile();
         if ( !file.delete() ) {
            throw new CommandException( "Could not delete file: " + file );
         }
      }
      for ( final AspectModelFile fileToCreate : changeContext.createdFiles() ) {
         final File file = Paths.get( fileToCreate.sourceLocation().orElseThrow() ).toFile();
         file.getParentFile().mkdirs();
         AspectSerializer.INSTANCE.write( fileToCreate );
      }
      for ( final AspectModelFile fileToModify : changeContext.modifiedFiles() ) {
         AspectSerializer.INSTANCE.write( fileToModify );
      }
   }

   private void checkFilesystemConsistency( final AspectChangeContext changeContext ) {
      final List<String> messages = new ArrayList<>();
      for ( final AspectModelFile fileToRemove : changeContext.removedFiles() ) {
         final URL url = AspectSerializer.INSTANCE.aspectModelFileUrl( fileToRemove );
         if ( !url.getProtocol().equals( "file" ) ) {
            messages.add( "File should be removed, but it is not identified by a file: URL: " + url );
         }
         final File file = new File( URI.create( url.toString() ) );
         if ( !file.exists() ) {
            messages.add( "File should be removed, but it does not exist: " + file );
         }
      }

      for ( final AspectModelFile fileToCreate : changeContext.createdFiles() ) {
         final URL url = AspectSerializer.INSTANCE.aspectModelFileUrl( fileToCreate );
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
      }

      for ( final AspectModelFile fileToModify : changeContext.modifiedFiles() ) {
         final URL url = AspectSerializer.INSTANCE.aspectModelFileUrl( fileToModify );
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
      }

      if ( !messages.isEmpty() ) {
         System.out.println( "Encountered problems, canceling writing." );
         messages.forEach( message -> System.out.println( "- " + message ) );
         System.exit( 1 );
      }
   }
}
