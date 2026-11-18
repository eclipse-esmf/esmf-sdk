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

import java.util.List;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.exception.SubCommandException;

import picocli.CommandLine;

@CommandLine.Command(
   name = AspectCommand.COMMAND_NAME,
   description = "Validate and transform Aspect Models",
   subcommands = {
         CommandLine.HelpCommand.class,
         AspectToCommand.class,
         AspectPrettyPrintCommand.class,
         AspectValidateCommand.class,
         AspectEditCommand.class,
         AspectUsageCommand.class
   },
   headerHeading = "@|bold Usage|@:%n%n",
   descriptionHeading = "%n@|bold Description|@:%n%n",
   parameterListHeading = "%n@|bold Parameters|@:%n",
   optionListHeading = "%n@|bold Options|@:%n" )
public class AspectCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "aspect";

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Parameters(
      paramLabel = "INPUT",
      description = "Input Aspect Model file, URN or URL; multiple files are supported by 'validate' only",
      arity = "1..*",
      index = "0..*" )
   private List<String> inputs;

   public String getInput() {
      if ( inputs.size() != 1 ) {
         throw new CommandException( "Multiple inputs are only supported by 'validate'" );
      }
      return inputs.getFirst();
   }

   public List<String> getInputs() {
      return inputs;
   }

   @Override
   public void run() {
      throw new SubCommandException( COMMAND_NAME );
   }
}
