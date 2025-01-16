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

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aspect.to.AspectToAasCommand;
import org.eclipse.esmf.aspect.to.AspectToAsyncapiCommand;
import org.eclipse.esmf.aspect.to.AspectToHtmlCommand;
import org.eclipse.esmf.aspect.to.AspectToJavaCommand;
import org.eclipse.esmf.aspect.to.AspectToJsonCommand;
import org.eclipse.esmf.aspect.to.AspectToJsonLdCommand;
import org.eclipse.esmf.aspect.to.AspectToJsonSchemaCommand;
import org.eclipse.esmf.aspect.to.AspectToOpenapiCommand;
import org.eclipse.esmf.aspect.to.AspectToPngCommand;
import org.eclipse.esmf.aspect.to.AspectToSqlCommand;
import org.eclipse.esmf.aspect.to.AspectToSvgCommand;
import org.eclipse.esmf.exception.SubCommandException;

import picocli.CommandLine;

@CommandLine.Command( name = AspectToCommand.COMMAND_NAME,
      description = "Transforms an Aspect Model into another format",
      subcommands = {
            CommandLine.HelpCommand.class,
            AspectToHtmlCommand.class,
            AspectToJavaCommand.class,
            AspectToJsonCommand.class,
            AspectToJsonLdCommand.class,
            AspectToOpenapiCommand.class,
            AspectToAsyncapiCommand.class,
            AspectToPngCommand.class,
            AspectToJsonSchemaCommand.class,
            AspectToSvgCommand.class,
            AspectToAasCommand.class,
            AspectToSqlCommand.class
      },
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class AspectToCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "to";

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.ParentCommand
   public AspectCommand parentCommand;

   @Override
   public void run() {
      throw new SubCommandException( AspectCommand.COMMAND_NAME + " " + COMMAND_NAME );
   }
}
