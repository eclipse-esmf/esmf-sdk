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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.AbstractCommand;
import io.openmanufacturing.sds.ExternalResolverMixin;
import io.openmanufacturing.sds.JAnsiRdfSyntaxHighlighter;
import io.openmanufacturing.sds.LoggingMixin;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;
import io.openmanufacturing.sds.aspectmodel.validation.services.AspectModelValidator;
import io.openmanufacturing.sds.aspectmodel.validation.services.DetailedViolationFormatter;
import io.openmanufacturing.sds.aspectmodel.validation.services.ViolationRustLikeFormatter;
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
   private static final Logger LOG = LoggerFactory.getLogger( AspectValidateCommand.class );

   @CommandLine.ParentCommand
   private AspectCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ExternalResolverMixin customResolver;

   @CommandLine.Option( names = { "--details", "-d" }, description = "Print detailed reports about violations" )
   private boolean details = false;

   @Override
   public void run() {
      final Try<VersionedModel> versionedModel = loadAndResolveModel( new File( parentCommand.getInput() ), customResolver );
      final AspectModelValidator validator = new AspectModelValidator();

      final List<Violation> violations = validator.validateModel( versionedModel );
      if ( details ) {
         LOG.debug( "Printing detailed validation results" );
         System.out.println( new DetailedViolationFormatter().apply( violations ) );
      } else {
         LOG.debug( "Printing regular validation results" );
         System.out.println( new ViolationRustLikeFormatter( versionedModel.get().getRawModel(), new JAnsiRdfSyntaxHighlighter() ).apply( violations ) );
      }

      if ( !violations.isEmpty() ) {
         System.exit( 1 );
      }
   }
}
