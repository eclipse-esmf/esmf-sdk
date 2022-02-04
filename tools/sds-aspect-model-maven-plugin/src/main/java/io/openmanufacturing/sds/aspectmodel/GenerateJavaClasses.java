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

import java.io.File;
import java.nio.file.Path;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.java.pojo.AspectModelJavaGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;

@Mojo( name = "generateJavaClasses", defaultPhase =  LifecyclePhase.GENERATE_SOURCES )
public class GenerateJavaClasses extends CodeGenerationMojo {

   private final Logger logger = LoggerFactory.getLogger( GenerateJavaClasses.class );

   @Parameter( defaultValue = "false" )
   private boolean disableJacksonAnnotations;

   @Override
   public void execute() throws MojoExecutionException {
      final Set<VersionedModel> aspectModels = loadModelsOrFail();
      for ( final VersionedModel aspectModel : aspectModels ) {
         final File templateLibFile = Path.of( templateFile ).toFile();
         validateParameters( templateLibFile );

         final boolean enableJacksonAnnotations = !disableJacksonAnnotations;
         final AspectModelJavaGenerator aspectModelJavaGenerator = packageName.isEmpty() ?
               new AspectModelJavaGenerator( aspectModel, enableJacksonAnnotations, executeLibraryMacros, templateLibFile ) :
               new AspectModelJavaGenerator( aspectModel, packageName, enableJacksonAnnotations, executeLibraryMacros, templateLibFile );

         aspectModelJavaGenerator.generate( nameMapper );
      }
      logger.info( "Successfully generated Java classes for Aspect Models." );
   }
}
