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

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aspect.edit.AspectEditMoveCommand;
import org.eclipse.esmf.aspect.edit.AspectEditNewVersionCommand;
import org.eclipse.esmf.exception.SubCommandException;

import picocli.CommandLine;

@CommandLine.Command( name = AspectEditCommand.COMMAND_NAME,
      description = "Edit (refactor) an Aspect Model",
      subcommands = {
            CommandLine.HelpCommand.class,
            AspectEditMoveCommand.class,
            AspectEditNewVersionCommand.class
      },
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class AspectEditCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "edit";

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.ParentCommand
   public AspectCommand parentCommand;

   @Override
   public void run() {
      throw new SubCommandException( AspectCommand.COMMAND_NAME + " " + COMMAND_NAME );
   }
}
