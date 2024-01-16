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
import java.io.OutputStream;
import java.util.function.Function;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import org.eclipse.esmf.aspectmodel.java.QualifiedName;

public abstract class CodeGenerationMojo extends AspectModelMojo {

   @Parameter
   protected String packageName = "";

   @Parameter
   protected String templateFile = "";

   @Parameter( defaultValue = "false" )
   protected boolean executeLibraryMacros;

   protected void validateParameters( final File templateLibFile ) throws MojoExecutionException {
      if ( executeLibraryMacros && !templateLibFile.exists() ) {
         throw new MojoExecutionException( "Missing configuration. Valid path to velocity template library file must be provided." );
      }
      super.validateParameters();
   }

   protected final Function<QualifiedName, OutputStream> nameMapper = artifact -> {
      final String path = artifact.getPackageName();
      final String fileName = artifact.getClassName();
      final String outputDirectoryForArtefact = outputDirectory + File.separator + path.replace( '.', File.separatorChar );
      final String artefactName = fileName + ".java";
      return getOutputStreamForFile( artefactName, outputDirectoryForArtefact );
   };

}
