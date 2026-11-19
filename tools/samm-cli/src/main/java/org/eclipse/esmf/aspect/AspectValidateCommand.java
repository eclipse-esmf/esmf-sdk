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

package org.eclipse.esmf.aspect;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.ResolverConfigurationMixin;

import picocli.CommandLine;

@CommandLine.Command(
   name = AspectValidateCommand.COMMAND_NAME,
   description = "Validate input Aspect Models",
   headerHeading = "@|bold Usage|@:%n%n",
   descriptionHeading = "%n@|bold Description|@:%n%n",
   parameterListHeading = "%n@|bold Parameters|@:%n",
   optionListHeading = "%n@|bold Options|@:%n" )
@SuppressWarnings( "UseOfSystemOutOrSystemErr" )
public class AspectValidateCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "validate";

   @CommandLine.ParentCommand
   private AspectCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ResolverConfigurationMixin resolverConfiguration;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
      names = { "--details" },
      description = "Print detailed reports about errors and violations" )
   private boolean details = false;

   @Override
   public void run() {
      setDetails( details );
      setResolverConfig( resolverConfiguration );
      for ( final String input : parentCommand.getInputs() ) {
         System.out.printf( "Validating %s - ", input );
         // loadAspectModel already loads + validates aspect model
         // prints violations + exit(1) on violation failure
         getInputHandler( input, true ).loadAspectModel();
         System.out.printf( "✔%n" );
      }
   }
}
