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

package org.eclipse.esmf.aspectmodel.scanner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ModelElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemScanner implements AspectModelScanner {

   private static final Logger LOG = LoggerFactory.getLogger( FileSystemScanner.class );

   protected final String searchDirectory;

   public FileSystemScanner( final String searchDirectory ) {
      this.searchDirectory = searchDirectory;
   }

   @Override
   public List<AspectModelFile> find( final String aspectModelFileName ) {
      List<AspectModelFile> result = new ArrayList<>();

      final AspectModel searchAspectModel = new AspectModelLoader().load( new File( aspectModelFileName ) );

      final Path directory = Paths.get( searchDirectory );
      final List<File> files = Arrays.stream( Optional.ofNullable( directory.toFile().listFiles() ).orElse( new File[] {} ) )
            .filter( file -> file.isFile() && file.getName().endsWith( ".ttl" ) && !file.getName()
                  .equals( Paths.get( aspectModelFileName ).getFileName().toString() ) )
            .sorted()
            .toList();

      if ( files.isEmpty() ) {
         LOG.info( "No .ttl files found in the directory '{}'", directory );
         return result;
      }

      final List<AspectModelUrn> modelUrns = searchAspectModel.elements().stream().map( ModelElement::urn ).toList();

      for ( final File file : files ) {
         result.addAll( processFile( file, modelUrns ) );
      }

      return result;
   }

   private List<AspectModelFile> processFile( final File inputFile, final List<AspectModelUrn> modelUrns ) {
      final List<AspectModelFile> aspectModelFiles = new ArrayList<>();
      final File absoluteFile = inputFile.isAbsolute()
            ? inputFile
            : Path.of( System.getProperty( "user.dir" ) ).resolve( inputFile.toPath() ).toFile().getAbsoluteFile();

      final AspectModelFile aspectModelFile = AspectModelFileLoader.load( absoluteFile );

      final Set<AspectModelUrn> urnsAspectModelFile = AspectModelLoader.getAllUrnsInModel( aspectModelFile.sourceModel() );

      for ( final AspectModelUrn aspectModelUrn : modelUrns ) {
         if ( urnsAspectModelFile.contains( aspectModelUrn ) ) {
            aspectModelFiles.add( aspectModelFile );
         }
      }

      return aspectModelFiles;
   }
}
