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

import java.io.File;
import java.nio.file.Path;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfig;
import io.openmanufacturing.sds.aspectmodel.java.JavaCodeGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.java.pojo.AspectModelJavaGenerator;

import org.eclipse.esmf.metamodel.AspectContext;

@Mojo( name = "generateJavaClasses", defaultPhase = LifecyclePhase.GENERATE_SOURCES )
public class GenerateJavaClasses extends CodeGenerationMojo {

   private final Logger logger = LoggerFactory.getLogger( GenerateJavaClasses.class );

   @Parameter( defaultValue = "false" )
   private boolean disableJacksonAnnotations;

   @Override
   public void execute() throws MojoExecutionException {
      final Set<AspectContext> aspectModels = loadModelsOrFail();
      for ( final AspectContext context : aspectModels ) {
         final File templateLibFile = Path.of( templateFile ).toFile();
         validateParameters( templateLibFile );
         final String pkg = packageName == null || packageName.isEmpty()
               ? context.aspect().getAspectModelUrn().map( AspectModelUrn::getNamespace ).orElseThrow()
               : packageName;
         final JavaCodeGenerationConfig config = JavaCodeGenerationConfigBuilder.builder()
               .enableJacksonAnnotations( !disableJacksonAnnotations )
               .packageName( pkg )
               .executeLibraryMacros( executeLibraryMacros )
               .templateLibFile( templateLibFile )
               .build();
         new AspectModelJavaGenerator( context.aspect(), config ).generate( nameMapper );
      }
      logger.info( "Successfully generated Java classes for Aspect Models." );
   }
}
