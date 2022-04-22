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

package io.openmanufacturing.sds.aspect;

import io.openmanufacturing.sds.AbstractCommand;
import io.openmanufacturing.sds.aspect.to.AspectToAasCommand;
import io.openmanufacturing.sds.aspect.to.AspectToDotCommand;
import io.openmanufacturing.sds.aspect.to.AspectToHtmlCommand;
import io.openmanufacturing.sds.aspect.to.AspectToJavaCommand;
import io.openmanufacturing.sds.aspect.to.AspectToJsonCommand;
import io.openmanufacturing.sds.aspect.to.AspectToJsonSchemaCommand;
import io.openmanufacturing.sds.aspect.to.AspectToOpenapiCommand;
import io.openmanufacturing.sds.aspect.to.AspectToPngCommand;
import io.openmanufacturing.sds.aspect.to.AspectToSvgCommand;
import io.openmanufacturing.sds.exeption.SubCommandException;
import picocli.CommandLine;

@CommandLine.Command( name = AspectToCommand.COMMAND_NAME, description = "Transforms an Aspect Model into another format",
      subcommands = {
            CommandLine.HelpCommand.class,
            AspectToDotCommand.class,
            AspectToHtmlCommand.class,
            AspectToJavaCommand.class,
            AspectToJsonCommand.class,
            AspectToOpenapiCommand.class,
            AspectToPngCommand.class,
            AspectToJsonSchemaCommand.class,
            AspectToSvgCommand.class,
            AspectToAasCommand.class
      },
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AspectToCommand extends AbstractCommand {

   public static final String COMMAND_NAME = "to";

   @CommandLine.ParentCommand
   public AspectCommand parentCommand;

   @Override
   public void run() {
      throw new SubCommandException( COMMAND_NAME );
   }
}
