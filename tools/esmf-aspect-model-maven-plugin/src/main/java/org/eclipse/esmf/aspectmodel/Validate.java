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

package org.eclipse.esmf.aspectmodel;

import java.util.List;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationFormatter;
import org.eclipse.esmf.metamodel.AspectModel;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = "validate", defaultPhase = LifecyclePhase.VALIDATE )
public class Validate extends AspectModelMojo {
   private static final Logger LOG = LoggerFactory.getLogger( Validate.class );
   private final AspectModelValidator validator = new AspectModelValidator();

   @Override
   public void executeGeneration() throws MojoExecutionException, MojoFailureException {
      validateParameters();

      final Set<AspectModel> aspectModels = loadModels();
      final List<Violation> violations = aspectModels.stream()
            .flatMap( aspectModel -> validator.validateModel( aspectModel ).stream() ).toList();

      if ( !violations.isEmpty() ) {
         throw new MojoFailureException( new ViolationFormatter().apply( violations ) );
      }

      LOG.info( "Aspect Models are valid." );
   }
}
