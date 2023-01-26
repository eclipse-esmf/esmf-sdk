/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspect.to;

import java.io.IOException;

import io.openmanufacturing.sds.AbstractCommand;
import io.openmanufacturing.sds.ExternalResolverMixin;
import io.openmanufacturing.sds.aspect.AspectToCommand;
import io.openmanufacturing.sds.aspectmodel.aas.AspectModelAASGenerator;
import io.openmanufacturing.sds.exception.CommandException;
import io.openmanufacturing.sds.metamodel.Aspect;
import picocli.CommandLine;

@CommandLine.Command(
      name = AspectToAasCommand.COMMAND_NAME,
      description = "Generate Asset Administration Shell (AAS) submodel template for an Aspect Model",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true )
public class AspectToAasCommand extends AbstractCommand {

   public static final String COMMAND_NAME = "aas";
   public static final String AASX = "aasx";
   public static final String XML = "xml";

   @CommandLine.Option(
         names = { "--output", "-o" },
         description = "Output file path" )
   private String outputFilePath = "-";

   @CommandLine.Option(
         names = { "--format", "-f" },
         description = "The file format the AAS is to be generated. Valid options are \"" + AASX + "\" and \"" + XML + "\". Default is \"" + XML + "\"." )
   private String format = XML;

   @CommandLine.ParentCommand
   private AspectToCommand parentCommand;

   @CommandLine.Mixin
   private ExternalResolverMixin customResolver;

   @Override
   public void run() {
      final AspectModelAASGenerator generator = new AspectModelAASGenerator();
      final Aspect aspect = loadModelOrFail( parentCommand.parentCommand.getInput(), customResolver ).aspect();
      try {
         // we intentionally override the name of the generated artifact here to the name explicitly
         // desired by the user (outputFilePath), as opposed to what the model thinks it should be
         // called (name)
         if ( format.equals( AASX ) ) {
            generator.generateAASXFile( aspect, name -> getStreamForFile( outputFilePath ) );
         } else {
            generator.generateAasXmlFile( aspect, name -> getStreamForFile( outputFilePath ) );
         }
      } catch ( final IOException e ) {
         throw new CommandException( e );
      }
   }
}
