/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.namespacepackage;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.exception.SubCommandException;

import picocli.CommandLine;

/**
 * Top-level command for working with Namespace Packages
 */
@CommandLine.Command(
      name = PackageCommand.COMMAND_NAME,
      description = "Import and export Namespace Packages",
      subcommands = {
            CommandLine.HelpCommand.class,
            PackageImportCommand.class,
            PackageExportCommand.class
      },
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class PackageCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "package";

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Parameters(
         paramLabel = "INPUT",
         description = "Input Namespace Package file, URL or Aspect Model URN",
         arity = "1",
         index = "0"
   )
   private String input;

   @Override
   public void run() {
      throw new SubCommandException( COMMAND_NAME );
   }

   @SuppressWarnings( { "LombokGetterMayBeUsed", "RedundantSuppression" } )
   public String getInput() {
      return input;
   }
}
