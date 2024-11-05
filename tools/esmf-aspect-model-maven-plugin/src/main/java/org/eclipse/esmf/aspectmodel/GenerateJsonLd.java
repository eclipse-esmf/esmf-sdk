/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

import java.io.IOException;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.generator.jsonld.AspectModelToJsonLdGenerator;
import org.eclipse.esmf.metamodel.Aspect;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = "generateJsonLd", defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateJsonLd extends AspectModelMojo {
   private static final Logger LOG = LoggerFactory.getLogger( GenerateJsonLd.class );

   @Override
   public void executeGeneration() throws MojoExecutionException, MojoFailureException {
      validateParameters();

      final Set<Aspect> aspects = loadAspects();
      for ( final Aspect context : aspects ) {
         final AspectModelToJsonLdGenerator generator = new AspectModelToJsonLdGenerator( context );
         generator.generate( name -> getOutputStreamForFile( name + ".jsonld", outputDirectory ) );
      }
      LOG.info( "Successfully generated JSON-LD for Aspect Models." );
   }
}
