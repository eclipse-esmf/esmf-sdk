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

package org.eclipse.esmf.aspect.to;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.ResolverConfigurationMixin;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aspect.AspectToCommand;
import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.java.JavaGenerator;
import org.eclipse.esmf.aspectmodel.java.metamodel.StaticMetaModelJavaGenerator;
import org.eclipse.esmf.aspectmodel.java.pojo.AspectModelJavaGenerator;
import org.eclipse.esmf.metamodel.Aspect;

import picocli.CommandLine;

@CommandLine.Command( name = AspectToJavaCommand.COMMAND_NAME,
      description = "Generate Java domain classes for an Aspect Model",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class AspectToJavaCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "java";

   @CommandLine.Option( names = { "--no-jackson",
         "-nj" }, description = "Disable Jackson annotation generation in generated Java classes." )
   private boolean disableJacksonAnnotations = false;

   @CommandLine.Option( names = { "--template-library-file", "-tlf" },
         description = "The path and name of the Velocity template file containing the macro library." )
   private String templateLib = "";

   @CommandLine.Option( names = { "--package-name", "-pn" }, description = "Package to use for generated Java classes" )
   private String packageName = "";

   @CommandLine.Option( names = { "--execute-library-macros",
         "-elm" }, description = "Execute the macros provided in the Velocity macro library." )
   private boolean executeLibraryMacros = false;

   @CommandLine.Option( names = { "--output-directory", "-d" }, description = "Output directory to write files to" )
   private String outputPath = ".";

   @CommandLine.Option( names = { "--static", "-s" }, description = "Generate Java domain classes for a Static Meta Model" )
   private boolean generateStaticMetaModelJavaClasses = false;

   @CommandLine.ParentCommand
   private AspectToCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ResolverConfigurationMixin resolverConfiguration;

   @Override
   public void run() {
      final Aspect aspect = loadAspectOrFail( parentCommand.parentCommand.getInput(), resolverConfiguration );
      final JavaGenerator javaGenerator = generateStaticMetaModelJavaClasses
            ? getStaticModelGenerator( aspect )
            : getModelGenerator( aspect );
      javaGenerator.generate( artifact -> {
         final String path = artifact.getPackageName();
         final String fileName = artifact.getClassName();
         return getStreamForFile( path.replace( '.', File.separatorChar ), fileName + ".java", outputPath );
      } );
   }

   private JavaCodeGenerationConfig buildConfig( final Aspect aspect ) {
      final File templateLibFile = Path.of( templateLib ).toFile();
      final String pkgName = Optional.ofNullable( packageName )
            .flatMap( pkg -> pkg.isBlank() ? Optional.empty() : Optional.of( pkg ) )
            .orElseGet( () -> aspect.urn().getNamespaceMainPart() );
      return JavaCodeGenerationConfigBuilder.builder()
            .executeLibraryMacros( executeLibraryMacros )
            .templateLibFile( templateLibFile )
            .enableJacksonAnnotations( !disableJacksonAnnotations )
            .packageName( pkgName )
            .build();
   }

   private JavaGenerator getStaticModelGenerator( final Aspect aspect ) {
      return new StaticMetaModelJavaGenerator( aspect, buildConfig( aspect ) );
   }

   private JavaGenerator getModelGenerator( final Aspect aspect ) {
      return new AspectModelJavaGenerator( aspect, buildConfig( aspect ) );
   }
}
