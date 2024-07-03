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
import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;
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
      final Try<VersionedModel> versionedModel = loadAndResolveModel( new File( input ), customResolver );

      if ( versionedModel.isSuccess() && !pathToModels.isEmpty() ) {
         final Path directory = Paths.get( pathToModels );

         final File[] files = Arrays.stream( Optional.ofNullable( directory.toFile().listFiles() ).orElse( new File[] {} ) )
               .filter( file -> file.isFile() && file.getName().endsWith( ".ttl" ) )
               .sorted()
               .toArray( File[]::new );

         final List<AspectModelUrn> modelUrns = AspectModelResolver.getAllUrnsInModel( versionedModel.get().getModel() ).stream()
               .map( AspectModelUrn::fromUrn )
               .toList();

         for ( final File file : files ) {
            final Try<VersionedModel> modelTry = loadAndResolveModel( file, customResolver );

            if ( modelTry.isFailure() ) {
               LOG.debug( "Could not load model from {}", file, modelTry.getCause() );
            } else {
               final Model model = modelTry.get().getModel();

               for ( final AspectModelUrn modelUrn : modelUrns ) {
                  if ( AspectModelResolver.containsDefinition( model, modelUrn ) ) {
                     LOG.info( "Contain Definition {} in Model {}", modelUrn, file.getName() );
                  } else {
                     LOG.debug( "File {} does not contain {}", file, modelUrn );
                  }
               }
            }
         }
      } else {
         LOG.info( "Provided Aspect Model '{}' can't be loaded or doesn't exists", input );
      }
   }
}
