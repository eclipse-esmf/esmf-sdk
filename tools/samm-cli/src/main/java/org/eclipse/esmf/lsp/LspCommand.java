/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.lsp;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.turtle.languageserver.TurtleLanguageServer;

import picocli.CommandLine;

@CommandLine.Command(
   name = LspCommand.COMMAND_NAME,
   description = "Launch Turtle Language Server",
   subcommands = {
         CommandLine.HelpCommand.class
   },
   headerHeading = "@|bold Usage|@:%n%n",
   descriptionHeading = "%n@|bold Description|@:%n%n",
   parameterListHeading = "%n@|bold Parameters|@:%n",
   optionListHeading = "%n@|bold Options|@:%n" )
public class LspCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "lsp";

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Option(
      names = { "-p", "--port" },
      description = "Port to listen on when using socket communication (default: ${DEFAULT-VALUE})" )
   int port = TurtleLanguageServer.DEFAULT_PORT;

   @CommandLine.Option(
      names = { "-s", "--stdio" },
      description = "Use standard input/output for communication instead of sockets" )
   boolean useStdio = false;

   @Override
   public void run() {
      if ( useStdio ) {
         TurtleLanguageServer.launchForStdio();
         return;
      }
      TurtleLanguageServer.launchForSocket( port );
   }
}
