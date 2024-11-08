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
import java.util.Optional;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.java.pojo.AspectModelJavaGenerator;
import org.eclipse.esmf.metamodel.Aspect;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = "generateJavaClasses", defaultPhase = LifecyclePhase.GENERATE_SOURCES )
public class GenerateJavaClasses extends CodeGenerationMojo {
   private static final Logger LOG = LoggerFactory.getLogger( GenerateJavaClasses.class );

   @Parameter( defaultValue = "false" )
   private boolean disableJacksonAnnotations;

   @Parameter( defaultValue = "deduction" )
   protected String jsonTypeInfo;

   @Override
   public void executeGeneration() throws MojoExecutionException {
      final Set<Aspect> aspects = loadAspects();
      for ( final Aspect aspect : aspects ) {
         final File templateLibFile = Path.of( templateFile ).toFile();
         validateParameters( templateLibFile );
         try {
            final JavaCodeGenerationConfig config = JavaCodeGenerationConfigBuilder.builder()
                  .enableJacksonAnnotations( !disableJacksonAnnotations )
                  .jsonTypeInfo( JavaCodeGenerationConfig.JsonTypeInfoType.valueOf(
                        Optional.ofNullable( jsonTypeInfo ).map( String::toUpperCase ).orElse( "DEDUCTION" ) ) )
                  .packageName( determinePackageName( aspect ) )
                  .executeLibraryMacros( executeLibraryMacros )
                  .templateLibFile( templateLibFile )
                  .namePrefix( namePrefix )
                  .namePostfix( namePostfix )
                  .build();
            new AspectModelJavaGenerator( aspect, config ).generate( nameMapper );
         } catch ( final Exception exception ) {
            throw new MojoExecutionException( "Could not generate Java classes for Aspect Models", exception );
         }
      }
      LOG.info( "Successfully generated Java classes for Aspect Models." );
   }
}
