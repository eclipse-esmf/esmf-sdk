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
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.ResolverConfigurationMixin;
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

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--no-jackson", "-nj" },
         description = "Disable Jackson annotation generation in generated Java classes." )
   private boolean disableJacksonAnnotations = false;

   @CommandLine.Option(
         names = { "--no-jackson-jsonformat-shape", "-njjs" },
         description = "Disable Jackson annotation JsonFormat.Shape object generation in generated Java classes." )
   private boolean disableJacksonAnnotationJsonFormatShapeObject = false;

   @CommandLine.Option(
         names = { "--json-type-info", "-jti" },
         description = "If Jackson annotations are enabled, determines the value of JsonTypeInfo.Id. Default: DEDUCTION",
         converter = JsonTypeInfoConverter.class
   )
   private JavaCodeGenerationConfig.JsonTypeInfoType jsonTypeInfo;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--template-library-file", "-tlf" },
         description = "The path and name of the Velocity template file containing the macro library." )
   private String templateLib = "";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--package-name", "-pn" },
         description = "Package to use for generated Java classes" )
   private String packageName = "";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--execute-library-macros", "-elm" },
         description = "Execute the macros provided in the Velocity macro library." )
   private boolean executeLibraryMacros = false;

   @CommandLine.Option(
         names = { "--output-directory", "-d" },
         description = "Output directory to write files to" )
   private String outputPath = ".";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--static", "-s" },
         description = "Generate Java domain classes for a Static Meta Model" )
   private boolean generateStaticMetaModelJavaClasses = false;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option( names = { "--name-prefix", "-namePrefix" },
         description = "Name prefix for generated Aspect, Entity Java classes" )
   private String namePrefix = "";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option( names = { "--name-postfix", "-namePostfix" },
         description = "Name postfix for generated Aspect, Entity Java classes" )
   private String namePostfix = "";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
         names = { "--details" },
         description = "Print detailed reports on errors" )
   private boolean details = false;

   @CommandLine.ParentCommand
   private AspectToCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ResolverConfigurationMixin resolverConfiguration;

   static class JsonTypeInfoConverter implements CommandLine.ITypeConverter<JavaCodeGenerationConfig.JsonTypeInfoType> {
      @Override
      public JavaCodeGenerationConfig.JsonTypeInfoType convert( final String value ) throws Exception {
         return value == null
               ? JavaCodeGenerationConfig.JsonTypeInfoType.DEDUCTION
               : JavaCodeGenerationConfig.JsonTypeInfoType.valueOf( value.toUpperCase() );
      }
   }

   @Override
   public void run() {
      setDetails( details );
      setResolverConfig( resolverConfiguration );

      final Aspect aspect = getInputHandler( parentCommand.parentCommand.getInput() ).loadAspect();
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
            .jsonTypeInfo( jsonTypeInfo )
            .templateLibFile( templateLibFile )
            .enableJacksonAnnotations( !disableJacksonAnnotations )
            .enableJacksonAnnotationJsonFormatShapeObject(!disableJacksonAnnotationJsonFormatShapeObject)
            .packageName( pkgName )
            .namePrefix( namePrefix )
            .namePostfix( namePostfix )
            .build();
   }

   private JavaGenerator getStaticModelGenerator( final Aspect aspect ) {
      return new StaticMetaModelJavaGenerator( aspect, buildConfig( aspect ) );
   }

   private JavaGenerator getModelGenerator( final Aspect aspect ) {
      return new AspectModelJavaGenerator( aspect, buildConfig( aspect ) );
   }
}
