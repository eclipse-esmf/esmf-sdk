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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import io.openmanufacturing.sds.aspectmodel.java.pojo.AspectModelJavaGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;

@Mojo( name = "generateJavaClasses", defaultPhase =  LifecyclePhase.GENERATE_RESOURCES )
public class GenerateJavaClasses extends AspectModelMojo {

   @Parameter
   private String packageName = "";

   @Parameter( defaultValue = "false" )
   private boolean disableJacksonAnnotations;

   @Parameter
   private String templateFile = "";

   @Parameter( defaultValue = "false" )
   private boolean executeLibraryMacros;

   @Override
   public void execute() throws MojoExecutionException {
      final VersionedModel model = loadModelOrFail( aspectModelFilePath );
      final File templateLibFile = Path.of( templateFile ).toFile();
      validateVelocityTemplateMacroFilePathAndName( templateLibFile );

      final boolean enableJacksonAnnotations = !disableJacksonAnnotations;
      final AspectModelJavaGenerator aspectModelJavaGenerator = packageName.isEmpty() ?
            new AspectModelJavaGenerator( model, enableJacksonAnnotations, executeLibraryMacros, templateLibFile ) :
            new AspectModelJavaGenerator( model, packageName, enableJacksonAnnotations, executeLibraryMacros, templateLibFile );

      aspectModelJavaGenerator.generate( artifact -> {
         final String path = artifact.getPackageName();
         final String fileName = artifact.getClassName();
         final String outputDirectoryForArtefact = outputDirectory + File.separator + path.replace( '.', File.separatorChar );
         final String artefactName = fileName + ".java";
         return getStreamForFile( artefactName, outputDirectoryForArtefact );
      } );
   }

   private void validateVelocityTemplateMacroFilePathAndName( final File templateLibFile ) throws MojoExecutionException {
      if ( executeLibraryMacros && !templateLibFile.exists() ) {
         throw new MojoExecutionException( "Missing configuration. Valid path to velocity template library file must be provided." );
      }
   }
}
