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

package org.eclipse.esmf.search;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.ExternalResolverMixin;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;

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

   public String getInput() {
      return input;
   }

   @Override
   public void run() {
      final Aspect aspect = loadAspectOrFail( input, customResolver );

      final Path directory = Paths.get( pathToModels );
      final File[] files = Arrays.stream( Optional.ofNullable( directory.toFile().listFiles() ).orElse( new File[] {} ) )
            .filter( file -> file.isFile() && file.getName().endsWith( ".ttl" ) && !file.getName()
                  .equals( Paths.get( input ).getFileName().toString() ) )
            .sorted()
            .toArray( File[]::new );

      if ( files.length == 0 ) {
         LOG.info( "No .ttl files found in the directory '{}'", directory );
         return;
      }

      //      final List<AspectModelUrn> modelUrns = AspectModelResolver.getAllUrnsInModel( versionedModel.get().getRawModel() ).stream()
      //            .map( AspectModelUrn::fromUrn )
      //            .toList();

      String header = String.format( "| %-60s | %-100s | %-50s | %-60s |",
            "URN of the element", "File location", "Model source", "Target element that it is referring to" );
      String separator = new String( new char[header.length()] ).replace( "\0", "-" );

      // Print Table header
      System.out.println( separator );
      System.out.println( header );
      System.out.println( separator );

      //      for ( final File file : files ) {
      //         processFile( file, modelUrns );
      //      }
   }

   private void processFile( File file, List<AspectModelUrn> modelUrns ) {
      //      final Try<VersionedModel> modelTry = loadAndResolveModel( file, customResolver );
      //
      //      if ( modelTry.isFailure() ) {
      //         LOG.debug( "Could not load model from {}", file, modelTry.getCause() );
      //         return;
      //      }
      //
      //      final Model model = modelTry.get().getRawModel();
      //
      //      for ( final AspectModelUrn modelUrn : modelUrns ) {
      //         if ( AspectModelResolver.containsDefinition( model, modelUrn ) ) {
      //            System.out.printf( "| %-60s | %-100s | %-50s | %-60s |%n", modelUrn, file.getPath(), file.getName(), "" );
      //         }
      //      }
   }
}
