/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.ResolverConfigurationMixin;
import org.eclipse.esmf.aspect.AspectToCommand;
import org.eclipse.esmf.aspectmodel.generator.ts.TsCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.ts.TsCodeGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.generator.ts.TsGenerator;
import org.eclipse.esmf.aspectmodel.generator.ts.metamodel.StaticMetaModelTsGenerator;
import org.eclipse.esmf.aspectmodel.generator.ts.pojo.AspectModelTsGenerator;
import org.eclipse.esmf.metamodel.Aspect;

import picocli.CommandLine;

@CommandLine.Command(
      name = AspectToTsCommand.COMMAND_NAME,
      description = "Generate Ts domain classes for an Aspect Model",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n"
)
public class AspectToTsCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "ts";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--template-library-file", "-tlf" },
         description = "The path and name of the Velocity template file containing the macro library." )
   private final String templateLib = "";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--package-name", "-pn" },
         description = "Package to use for generated TS classes" )
   private final String packageName = "";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--execute-library-macros", "-elm" },
         description = "Execute the macros provided in the Velocity macro library." )
   private final boolean executeLibraryMacros = false;

   @CommandLine.Option(
         names = { "--output-directory", "-d" },
         description = "Output directory to write files to" )
   private final String outputPath = ".";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--static", "-s" },
         description = "Generate TS domain classes for a Static Meta Model" )
   private final boolean generateStaticMetaModelClasses = false;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option( names = { "--name-prefix", "-namePrefix" },
         description = "Name prefix for generated Aspect, Entity TS classes" )
   private final String namePrefix = "";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option( names = { "--name-postfix", "-namePostfix" },
         description = "Name postfix for generated Aspect, Entity TS classes" )
   private final String namePostfix = "";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--details" },
         description = "Print detailed reports on errors" )
   private final boolean details = false;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--disable-prettier" },
         description = "Disables Prettier formatting. The generated script will be created without applying Prettier formatting." )
   private final boolean disablePrettierFormatter = false;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--prettier-config" },
         description = "Specifies the path to a custom Prettier configuration file."
               + " If provided, this configuration will be used during the formatting step instead of the default setting" )
   private final String prettierConfigPath = "";

   @CommandLine.ParentCommand
   private AspectToCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ResolverConfigurationMixin resolverConfiguration;

   @Override
   public void run() {
      setDetails( details );
      setResolverConfig( resolverConfiguration );

      final Aspect aspect = getInputHandler( parentCommand.parentCommand.getInput() ).loadAspect();
      final TsGenerator tsGenerator = generateStaticMetaModelClasses
            ? getStaticModelGenerator( aspect )
            : getModelGenerator( aspect );
      tsGenerator.generate( artifact -> {
         final String path = artifact.modulePath();
         final String fileName = artifact.fileName();
         return getStreamForFile( path.replace( '.', File.separatorChar ), fileName + ".ts", outputPath );
      } );
   }

   private TsCodeGenerationConfig buildConfig( final Aspect aspect ) {
      final File templateLibFile = Path.of( templateLib ).toFile();
      final String pkgName = Optional.ofNullable( packageName )
            .flatMap( pkg -> pkg.isBlank() ? Optional.empty() : Optional.of( pkg ) )
            .orElseGet( () -> aspect.urn().getNamespaceMainPart() );
      return TsCodeGenerationConfigBuilder.builder()
            .executeLibraryMacros( executeLibraryMacros )
            .templateLibFile( templateLibFile )
            .packageName( pkgName )
            .namePrefix( namePrefix )
            .namePostfix( namePostfix )
            .disablePrettierFormatter( disablePrettierFormatter )
            .prettierConfigPath( prettierConfigPath )
            .build();
   }

   private TsGenerator getStaticModelGenerator( final Aspect aspect ) {
      return new StaticMetaModelTsGenerator( aspect, buildConfig( aspect ) );
   }

   private TsGenerator getModelGenerator( final Aspect aspect ) {
      return new AspectModelTsGenerator( aspect, buildConfig( aspect ) );
   }
}
