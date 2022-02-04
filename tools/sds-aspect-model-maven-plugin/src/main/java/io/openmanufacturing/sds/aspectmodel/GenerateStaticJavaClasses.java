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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.java.metamodel.StaticMetaModelJavaGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;

@Mojo( name = "generateStaticJavaClasses", defaultPhase =  LifecyclePhase.GENERATE_SOURCES )
public class GenerateStaticJavaClasses extends CodeGenerationMojo {

   private final Logger logger = LoggerFactory.getLogger( GenerateStaticJavaClasses.class );

   @Override
   public void execute() throws MojoExecutionException {
      final Set<VersionedModel> aspectModels = loadModelsOrFail();
      for ( final VersionedModel aspectModel : aspectModels ) {
         final File templateLibFile = Path.of( templateFile ).toFile();
         validateVelocityTemplateMacroFilePathAndName( templateLibFile );

         final StaticMetaModelJavaGenerator staticMetaModelJavaGenerator = packageName.isEmpty() ?
               new StaticMetaModelJavaGenerator( aspectModel, executeLibraryMacros, templateLibFile ) :
               new StaticMetaModelJavaGenerator( aspectModel, packageName, executeLibraryMacros, templateLibFile );
         staticMetaModelJavaGenerator.generate( nameMapper );
      }
      logger.info( "Successfully generated static Java classes for Aspect Models." );
   }
}
