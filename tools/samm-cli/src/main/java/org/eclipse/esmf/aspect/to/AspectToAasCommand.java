/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspect.to;

import java.io.File;
import java.io.IOException;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.ExternalResolverMixin;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aspect.AspectToCommand;
import org.eclipse.esmf.aspectmodel.aas.AasFileFormat;
import org.eclipse.esmf.aspectmodel.aas.AspectModelAasGenerator;
import org.eclipse.esmf.metamodel.Aspect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(
      name = AspectToAasCommand.COMMAND_NAME,
      description = "Generate Asset Administration Shell (AAS) submodel template for an Aspect Model",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class AspectToAasCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "aas";
   private static final Logger LOG = LoggerFactory.getLogger( AspectToAasCommand.class );

   @CommandLine.Option(
         names = { "--output", "-o" },
         description = "Output file path" )
   private String outputFilePath = "-";

   @CommandLine.Option( names = { "--format", "-f" },
         description = "The file format the AAS is to be generated in. Valid options are \"${COMPLETION-CANDIDATES}\". Default is "
               + "\"${DEFAULT-VALUE}\"." )
   private AasFileFormat format = AasFileFormat.XML;

   @CommandLine.Option( names = { "--aspect-data", "-a" },
         description = "A file containing Aspect JSON data." )
   private File aspectData = null;

   @CommandLine.ParentCommand
   private AspectToCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ExternalResolverMixin customResolver;

   private JsonNode loadAspectData() {
      if ( aspectData == null ) {
         return null;
      }

      final ObjectMapper objectMapper = new ObjectMapper();
      try {
         return objectMapper.readTree( aspectData );
      } catch ( final IOException exception ) {
         LOG.error( "Could not read Aspect JSON data from file: {}", aspectData );
         return null;
      }
   }

   @Override
   public void run() {
      final Aspect aspect = loadAspectOrFail( parentCommand.parentCommand.getInput(), customResolver );
      final JsonNode loadedAspectData = loadAspectData();
      // we intentionally override the name of the generated artifact here to the name explicitly
      // desired by the user (outputFilePath), as opposed to what the model thinks it should be
      // called (name)
      new AspectModelAasGenerator().generate( format, aspect, loadedAspectData, name -> getStreamForFile( outputFilePath ) );
   }
}
