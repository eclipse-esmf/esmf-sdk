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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = GenerateStaticJavaClasses.MAVEN_GOAL, defaultPhase = LifecyclePhase.GENERATE_SOURCES )
public class GenerateStaticJavaClasses extends CodeGenerationMojo {
   public static final String MAVEN_GOAL = "generateStaticJavaClasses";
   private static final Logger LOG = LoggerFactory.getLogger( GenerateStaticJavaClasses.class );

   @Parameter( defaultValue = "false" )
   protected boolean enableSetters;

   @Parameter( defaultValue = "standard" )
   protected String setterStyle;

   @Override
   public void executeGeneration() throws MojoExecutionException {
      final Set<Aspect> aspects = loadAspects();
      for ( final Aspect aspect : aspects ) {
         final File templateLibFile = Path.of( templateFile ).toFile();
         validateParameters( templateLibFile );
         final JavaCodeGenerationConfig config = JavaCodeGenerationConfigBuilder.builder()
               .packageName( determinePackageName( aspect ) )
               .executeLibraryMacros( executeLibraryMacros )
               .templateLibFile( templateLibFile )
               .namePrefix( namePrefix )
               .namePostfix( namePostfix )
               .enableSetters( enableSetters )
               .setterStyle( getEnumConstant( JavaCodeGenerationConfig.SetterStyle.class, setterStyle, "STANDARD" ) )
               .build();
         new StaticMetaModelJavaGenerator( aspect, config ).generateThrowing( javaFileNameMapper( outputDirectory ) );
      }
      LOG.info( "Successfully generated static Java classes for Aspect Models." );
   }
}
