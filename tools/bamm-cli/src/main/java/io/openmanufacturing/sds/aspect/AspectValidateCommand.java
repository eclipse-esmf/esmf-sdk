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
import java.util.List;

import org.topbraid.shacl.util.FailureLog;

import io.openmanufacturing.sds.AbstractCommand;
import io.openmanufacturing.sds.ExternalResolverMixin;
import io.openmanufacturing.sds.ViolationFormatter;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;
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

   @CommandLine.Mixin
   private ExternalResolverMixin customResolver;

   @Override
   public void run() {
      FailureLog.set( new FailureLog() {
         @Override
         public void logFailure( final String message ) {
            // Do not log SHACL-internal errors
         }
      } );

      final Try<VersionedModel> versionedModel = loadAndResolveModel( new File( parentCommand.getInput() ), customResolver );
      final AspectModelValidator validator = new AspectModelValidator();

      final List<Violation> violations = validator.validateModel( versionedModel );
      System.out.println( new ViolationFormatter().apply( violations ) );

      if ( !violations.isEmpty() ) {
         System.exit( 1 );
      }
   }
}
