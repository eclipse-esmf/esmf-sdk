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

package org.eclipse.esmf.aspect;

import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.InputHandler;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.ResolverConfigurationMixin;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.stats.Reference;
import org.eclipse.esmf.aspectmodel.stats.Usage;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.exception.CommandException;

import io.vavr.control.Try;
import picocli.CommandLine;

@CommandLine.Command(
   name = AspectUsageCommand.COMMAND_NAME,
   description = "Shows where model elements are used in Aspect Models",
   headerHeading = "@|bold Usage|@:%n%n",
   descriptionHeading = "%n@|bold Description|@:%n%n",
   parameterListHeading = "%n@|bold Parameters|@:%n",
   optionListHeading = "%n@|bold Options|@:%n",
   mixinStandardHelpOptions = true )
@SuppressWarnings( "UseOfSystemOutOrSystemErr" )
public class AspectUsageCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "usage";

   /**
    * The list of supported aspect usage output formats
    */
   private enum AspectUsageFormat {
      TABLE, CSV;

      @Override
      public String toString() {
         return switch ( this ) {
            case TABLE -> "table";
            case CSV -> "csv";
         };
      }

      /**
       * Returns a formatted list of the valid formats
       *
       * @return the list of formats
       */
      public static String allValues() {
         return String.join( ", ", Stream.of( values() ).map( AspectUsageFormat::toString ).toList() );
      }
   }

   @CommandLine.ParentCommand
   public AspectCommand parentCommand;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
      names = { "--details" },
      description = "Print detailed reports on errors" )
   private boolean details = false;

   @CommandLine.Option(
      names = { "--format", "-f" },
      description = "Output format. Valid options are ${COMPLETION-CANDIDATES}. Default is ${DEFAULT-VALUE}." )
   private AspectUsageFormat format = AspectUsageFormat.TABLE;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ResolverConfigurationMixin resolverConfiguration;

   @Override
   public void run() {
      setDetails( details );
      setResolverConfig( resolverConfiguration );

      final String input = parentCommand.getInput();
      final InputHandler inputHandler = getInputHandler( input );
      final AspectModelLoader aspectModelLoader = inputHandler.aspectModelLoader();
      final Usage usage = new Usage( aspectModelLoader );

      final Try<AspectModelUrn> inputAspectModelUrn = AspectModelUrn.from( input );
      final List<Reference> references = inputAspectModelUrn.map( usage::referencesTo )
            .getOrElse( () -> {
               final URI targetUri = inputHandler.inputUri();
               final AspectModelFile fileToCheck = aspectModelLoader.loadContents()
                     .filter( file -> file.sourceUri().equals( targetUri ) )
                     .findFirst()
                     .orElseThrow( () -> new CommandException( "Input is no file, valid URN or any resolvable URI" ) );
               return usage.referencesToAnyElementIn( fileToCheck );
            } );

      if ( references.isEmpty() ) {
         System.out.println( "Could not find any references to " + input + "." );
         if ( inputAspectModelUrn.isSuccess()
               && resolverConfiguration != null
               && resolverConfiguration.modelsRoots.isEmpty()
               && ( resolverConfiguration.gitHubResolverOptions == null
                     || resolverConfiguration.gitHubResolverOptions.isEmpty() ) ) {
            System.out.println( "Did you forget to set a models root or GitHub resolution?" );
         }
         System.exit( 0 );
      }

      if ( AspectUsageFormat.CSV == format ) {
         printCsvOutput( references );
      } else if ( AspectUsageFormat.TABLE == format ) {
         printTableOutput( references );
      } else {
         throw new CommandException( "Invalid format: " + format + ". Valid formats are " + AspectUsageFormat.allValues() );
      }
   }

   private void printTableOutput( final List<Reference> references ) {
      final String[] headerParts = new String[] { "Element", "in file", "Points to", "in file" };
      final int[] columnWidth = Arrays.stream( headerParts ).mapToInt( String::length ).toArray();
      for ( final Reference reference : references ) {
         columnWidth[0] = Integer.max( columnWidth[0], reference.pointer().toString().length() );
         columnWidth[1] = Integer.max( columnWidth[1], reference.pointerSource().toString().length() );
         columnWidth[2] = Integer.max( columnWidth[2], reference.pointee().toString().length() );
         columnWidth[3] = Integer.max( columnWidth[3], reference.pointeeSource().toString().length() );
      }
      final String tableFormat =
            "| %-" + columnWidth[0] + "s | %-" + columnWidth[1] + "s | %-" + columnWidth[2] + "s | %-" + columnWidth[3] + "s |";
      final String header = String.format( tableFormat, (Object[]) headerParts );
      final String separator = "-".repeat( header.length() );
      System.out.println( separator );
      System.out.println( header );
      System.out.println( separator );

      references.stream().sorted( Comparator.comparing( reference -> reference.pointer().toString() ) ).forEach( reference -> {
         System.out.printf( tableFormat + "%n",
               reference.pointer(),
               reference.pointerSource(),
               reference.pointee(),
               reference.pointeeSource() );
      } );
   }

   private void printCsvOutput( final List<Reference> references ) {
      // Print CSV header
      System.out.println( "Element,in file,Points to,in file" );

      // Print data rows
      references.stream().sorted( Comparator.comparing( reference -> reference.pointer().toString() ) ).forEach( reference -> {
         // Escape CSV fields that contain commas or quotes
         final String element = StringEscapeUtils.escapeCsv( reference.pointer().toString() );
         final String inFile = StringEscapeUtils.escapeCsv( reference.pointerSource().toString() );
         final String pointsTo = StringEscapeUtils.escapeCsv( reference.pointee().toString() );
         final String pointsToFile = StringEscapeUtils.escapeCsv( reference.pointeeSource().toString() );
         System.out.printf( "%s,%s,%s,%s%n", element, inFile, pointsTo, pointsToFile );
      } );
   }
}
