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

package io.openmanufacturing.sds.aspectmodel;

import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationReport;
import io.openmanufacturing.sds.aspectmodel.validation.services.AspectModelValidator;
import io.vavr.control.Try;

@Mojo( name = "validate", defaultPhase =  LifecyclePhase.VALIDATE )
public class Validate extends AspectModelMojo {

   private final Logger logger = LoggerFactory.getLogger( Validate.class );
   private final AspectModelValidator validator = new AspectModelValidator();

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      final Set<Try<VersionedModel>> resolvedModels = loadAndResolveModels();
      for ( final Try<VersionedModel> versionedModel : resolvedModels ) {
         final ValidationReport report = validator.validate( versionedModel );
         if ( !report.conforms() ) {
            throw new MojoFailureException( report.toString() );
         }
      }
      logger.info( "Aspect Models are valid." );
   }
}
