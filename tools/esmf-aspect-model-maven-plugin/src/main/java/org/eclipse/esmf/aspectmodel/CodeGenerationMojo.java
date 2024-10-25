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

import org.eclipse.esmf.aspectmodel.java.QualifiedName;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class CodeGenerationMojo extends AspectModelMojo {

   @Parameter
   protected String packageName = "";

   @Parameter
   protected String templateFile = "";

   @Parameter( defaultValue = "false" )
   protected boolean executeLibraryMacros;

   @Parameter
   protected String stripNamespace = "";

   @Parameter
   protected String aspectPrefix = "";

   @Parameter
   protected String aspectPostfix = "";

   protected void validateParameters( final File templateLibFile ) throws MojoExecutionException {
      if ( executeLibraryMacros && !templateLibFile.exists() ) {
         throw new MojoExecutionException( "Missing configuration. Valid path to velocity template library file must be provided." );
      }
      super.validateParameters();
   }

   protected final Function<QualifiedName, OutputStream> nameMapper = artifact -> {
      final String path = artifact.getPackageName();
      final String fileName = artifact.getClassName();
      final String outputDirectoryForArtifact = outputDirectory + File.separator + path.replace( '.', File.separatorChar );
      final String artifactName = fileName + ".java";
      return getOutputStreamForFile( artifactName, outputDirectoryForArtifact );
   };

   protected String determinePackageName( final Aspect aspect ) {
      if ( packageName == null || packageName.isEmpty() ) {
         return aspect.urn().getNamespaceMainPart();
      }

      final AspectModelUrn urn = aspect.urn();
      final VersionNumber versionNumber = VersionNumber.parse( urn.getVersion() );
      final String interpolated = packageName.replace( "{{namespace}}", urn.getNamespaceMainPart() )
            .replace( "{{majorVersion}}", "" + versionNumber.getMajor() )
            .replace( "{{minorVersion}}", "" + versionNumber.getMinor() )
            .replace( "{{microVersion}}", "" + versionNumber.getMicro() );
      return stripNamespace != null && !stripNamespace.isEmpty()
            ? interpolated.replaceAll( stripNamespace, "" )
            : interpolated;
   }
}
