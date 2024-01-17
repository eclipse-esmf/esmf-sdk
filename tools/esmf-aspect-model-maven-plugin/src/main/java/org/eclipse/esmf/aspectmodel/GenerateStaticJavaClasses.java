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

import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.java.metamodel.StaticMetaModelJavaGenerator;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectContext;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = "generateStaticJavaClasses", defaultPhase = LifecyclePhase.GENERATE_SOURCES )
public class GenerateStaticJavaClasses extends CodeGenerationMojo {

   private final Logger logger = LoggerFactory.getLogger( GenerateStaticJavaClasses.class );

   @Override
   public void execute() throws MojoExecutionException {
      final Set<AspectContext> aspectModels = loadModelsOrFail();
      for ( final AspectContext context : aspectModels ) {
         final Aspect aspect = context.aspect();
         final File templateLibFile = Path.of( templateFile ).toFile();
         validateParameters( templateLibFile );
         final JavaCodeGenerationConfig config = JavaCodeGenerationConfigBuilder.builder()
               .packageName( determinePackageName( aspect ) )
               .executeLibraryMacros( executeLibraryMacros )
               .templateLibFile( templateLibFile )
               .build();
         new StaticMetaModelJavaGenerator( context.aspect(), config ).generate( nameMapper );
      }
      logger.info( "Successfully generated static Java classes for Aspect Models." );
   }
}
