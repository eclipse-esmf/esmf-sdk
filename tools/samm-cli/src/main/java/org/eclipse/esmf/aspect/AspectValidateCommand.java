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

import java.util.List;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.ExternalResolverMixin;
import org.eclipse.esmf.JansiRdfSyntaxHighlighter;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.DetailedViolationFormatter;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationRustLikeFormatter;
import org.eclipse.esmf.metamodel.AspectModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command( name = AspectValidateCommand.COMMAND_NAME,
      description = "Validate input Aspect Model",
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
@SuppressWarnings( "UseOfSystemOutOrSystemErr" )
public class AspectValidateCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "validate";
   private static final Logger LOG = LoggerFactory.getLogger( AspectValidateCommand.class );

   @CommandLine.ParentCommand
   private AspectCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ExternalResolverMixin customResolver;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option( names = { "--details", "-d" }, description = "Print detailed reports about violations" )
   private boolean details = false;

   @Override
   public void run() {
      final AspectModel aspectModel = loadAspectModelOrFail( parentCommand.getInput(), customResolver, details );
      final AspectModelValidator validator = new AspectModelValidator();

      final List<Violation> violations = validator.validateModel( aspectModel );
      if ( details ) {
         LOG.debug( "Printing detailed validation results" );
         System.out.println( new DetailedViolationFormatter().apply( violations ) );
      } else {
         LOG.debug( "Printing regular validation results" );
         final String message = new ViolationRustLikeFormatter( aspectModel.mergedModel(), new JansiRdfSyntaxHighlighter() ).apply(
               violations );
         System.out.println( message );
      }

      if ( !violations.isEmpty() ) {
         System.exit( 1 );
      }
   }
}
