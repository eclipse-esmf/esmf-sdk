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

import java.io.File;

import org.topbraid.shacl.util.FailureLog;

import io.openmanufacturing.sds.AbstractCommand;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationReport;
import io.openmanufacturing.sds.aspectmodel.validation.services.AspectModelValidator;
import io.vavr.control.Try;
import picocli.CommandLine;

@CommandLine.Command( name = AspectValidateCommand.COMMAND_NAME,
      description = "Validate input Aspect Model",
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AspectValidateCommand extends AbstractCommand {

   public static final String COMMAND_NAME = "validate";

   @CommandLine.ParentCommand
   private AspectCommand parentCommand;

   @Override
   public void run() {
      FailureLog.set( new FailureLog() {
         @Override
         public void logFailure( final String message ) {
            // Do not log SHACL-internal errors
         }
      } );

      final Try<VersionedModel> versionedModel = loadAndResolveModel( new File( parentCommand.getInput() ) );
      final AspectModelValidator validator = new AspectModelValidator();
      final ValidationReport report = validator.validate( versionedModel );

      if ( LOG.isWarnEnabled() ) {
         LOG.warn( report.toString() );
      }

      if ( !report.conforms() ) {
         System.exit( 1 );
      }
   }
}
