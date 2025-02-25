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

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.InputHandler;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.ResolverConfigurationMixin;
import org.eclipse.esmf.aspectmodel.resolver.NamespacePackage;
import org.eclipse.esmf.exception.CommandException;

import picocli.CommandLine;

/**
 * Command to export a Namespace Package from a given Namespace or an Aspect Model with its transitive dependencies
 */
@CommandLine.Command(
      name = PackageExportCommand.COMMAND_NAME,
      description = "Export a Namespace or an Aspect Model with its dependencies into a Namespace Package",
      subcommands = {
            CommandLine.HelpCommand.class
      },
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class PackageExportCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "export";

   @CommandLine.ParentCommand
   public PackageCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ResolverConfigurationMixin resolverConfiguration;

   @CommandLine.Option(
         names = { "--output", "-o" },
         description = "Output file path (default: stdout; as ZIP is a binary format, it is strongly recommended to output the result to "
               + "a file by using the -o option or the console redirection operator '>')" )
   private String outputFilePath = "-";

   @CommandLine.Option(
         names = { "--details" },
         description = "Print detailed reports on errors" )
   private boolean details = false;

   @Override
   public void run() {
      setDetails( details );
      setResolverConfig( resolverConfiguration );

      final String input = parentCommand.getInput();
      final InputHandler inputHandler = getInputHandler( input );
      final NamespacePackage namespacePackage = new NamespacePackage( inputHandler.loadAspectModel() );

      try ( final OutputStream out = getStreamForFile( outputFilePath ) ) {
         out.write( namespacePackage.serialize() );
      } catch ( final IOException exception ) {
         throw new CommandException( "Could not write to output file" );
      }
   }
}
