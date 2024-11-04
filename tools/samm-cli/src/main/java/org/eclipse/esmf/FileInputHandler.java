/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.AspectModel;

import org.apache.commons.io.FilenameUtils;

/**
 * The FileInputHandler knows how to load Aspect Models if the given input is a local file
 */
public class FileInputHandler extends AbstractInputHandler {
   private final File inputFile;

   FileInputHandler( final String input, final ResolverConfigurationMixin resolverConfig, final boolean details ) {
      super( input, resolverConfig, details );
      inputFile = absoluteFile( new File( input ) );
   }

   @Override
   public AspectModel loadAspectModel() {
      return applyAspectModelLoader( aspectModelLoader -> aspectModelLoader.load( inputFile ) );
   }

   @Override
   public URI inputUri() {
      return absoluteFile( new File( input ) ).toURI();
   }

   private static File absoluteFile( final File inputFile ) {
      return inputFile.isAbsolute()
            ? inputFile
            : Path.of( System.getProperty( "user.dir" ) ).resolve( inputFile.toPath() ).toFile().getAbsoluteFile();
   }

   @Override
   protected List<ResolutionStrategy> resolutionStrategies() {
      final List<ResolutionStrategy> strategies = new ArrayList<>();
      final File file = absoluteFile( inputFile );
      strategies.add( new FileSystemStrategy( modelsRootForFile( file ) ) );
      strategies.addAll( configuredStrategies() );
      return strategies;
   }

   public static Path modelsRootForFile( final File file ) {
      return file.toPath().getParent().getParent().getParent();
   }

   @Override
   protected String expectedAspectName() {
      return FilenameUtils.removeExtension( inputFile.getName() );
   }

   public static boolean appliesToInput( final String input ) {
      try {
         return absoluteFile( new File( input ) ).exists();
      } catch ( final Exception exception ) {
         // This could file with e.g. a InvalidPathException or with platform-specific exceptions when the input is indeed not a valid file
         return false;
      }
   }

   @Override
   public AspectModelFile loadAspectModelFile() {
      final AspectModel aspectModel = loadAspectModel();
      return aspectModel.files()
            .stream()
            .filter( file -> file.sourceLocation().map( location -> location.equals( inputUri() ) ).orElse( false ) )
            .findFirst()
            .orElseThrow( () -> new CommandException( "Could not load: " + inputUri() ) );
   }
}
