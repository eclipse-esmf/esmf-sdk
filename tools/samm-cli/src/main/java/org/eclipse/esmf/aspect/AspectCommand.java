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

package org.eclipse.esmf.aspect;

import java.io.File;
import java.net.URI;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.exception.SubCommandException;

import picocli.CommandLine;

@CommandLine.Command( name = AspectCommand.COMMAND_NAME,
      description = "Validate and transform Aspect Models",
      subcommands = {
            CommandLine.HelpCommand.class,
            AspectToCommand.class,
            AspectMigrateCommand.class,
            AspectPrettyPrintCommand.class,
            AspectValidateCommand.class
      },
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AspectCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "aspect";

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Parameters( paramLabel = "INPUT", description = "Input file name of the Aspect Model .ttl file or URI", arity = "1", index = "0" )
   private String input;
   private URI inputUri;

   public URI getInput() {
      if ( inputUri == null ) {
         var file = new File( input );
         if ( file.exists() )
            inputUri = file.toURI();
         else {
            try {
               inputUri = URI.create( input );
            } catch ( final IllegalArgumentException e ) {
               throw new SubCommandException( "The file does not exist or invalid input URI: " + input );
            }
         }
      }

      return inputUri;
   }

   @Override
   public void run() {
      throw new SubCommandException( COMMAND_NAME );
   }
}
