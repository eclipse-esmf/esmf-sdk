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

package org.eclipse.esmf.aspectmodel.resolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolution strategy to resolve Aspect models by URN from a well-defined directory structure from the class path.
 */
public class ClasspathStrategy implements ResolutionStrategy {
   private static final Logger LOG = LoggerFactory.getLogger( ClasspathStrategy.class );
   private final String modelsRoot;

   /**
    * Initialize the ClasspathStrategy with an empty root path for models. The classpath
    * is assumed to contain a file system hierarchy as follows: {@code N/V/X.ttl} where N is the namespace,
    * V is the version of the namespace and X is the name of the model element (Aspect, Characteristic, ...).
    * Example:
    * <pre>
    * {@code
    * models   <-- should be configured as modelsRoot
    * └── com.example
    *     ├── 1.0.0
    *     │   ├── ExampleAspect.ttl
    *     │   ├── exampleProperty.ttl
    *     │   └── ExampleCharacteristic.ttl
    *     └── 1.1.0
    *         └── ...
    * }
    * </pre>
    */
   public ClasspathStrategy() {
      modelsRoot = "";
   }

   /**
    * Initialize the ClasspathStrategy with the root path of models. The directory
    * is assumed to contain a file system hierarchy as follows: {@code N/V/X.ttl} where N is the namespace,
    * V is the version of the namespace and X is the name of the model element (Aspect, Characteristic, ...).
    * Example:
    * <pre>
    * {@code
    * models   <-- should be configured as modelsRoot
    * └── com.example
    *     ├── 1.0.0
    *     │   ├── ExampleAspect.ttl
    *     │   ├── exampleProperty.ttl
    *     │   └── ExampleCharacteristic.ttl
    *     └── 1.1.0
    *         └── ...
    * }
    * </pre>
    *
    * @param modelsRoot The root directory for model files
    */
   public ClasspathStrategy( final String modelsRoot ) {
      this.modelsRoot = modelsRoot;
   }

   protected URL resourceUrl( final String directory, final String filename ) {
      return getClass().getClassLoader().getResource( directory + "/" + filename );
   }

   protected Stream<String> filesInDirectory( final String directory ) {
      try {
         final Optional<File> file = getDirectoryFile( directory );
         if ( file.isPresent() && file.get().isFile() ) {
            return getFilesFromJar( directory, file.get() );
         } else {
            return getFilesFromResources( directory );
         }
      } catch ( final IOException exception ) {
         LOG.warn( "Could not list files in classpath directory {}", directory, exception );
         return Stream.empty();
      }
   }

   private Optional<File> getDirectoryFile( final String directory ) {
      // The incoming URL will look like this:  jar:file:/pathToJar/o.jar/packageName/className
      // In case we run the code from a jar. Because of that we need to deconstruct the path to get the path to the jar only and remove
      // the unwanted part of the URL.
      final URL url = getClass().getClassLoader().getResource( directory );
      if ( url == null ) {
         return Optional.empty();
      }
      final String urlString = url.toString();
      final int jarIndex = urlString.indexOf( ".jar" );
      final String path = jarIndex > 0 ? urlString.substring( 0, jarIndex + 4 ).replace( "jar:file:", "" ) : urlString;
      return Optional.of( new File( path ) );
   }

   private Stream<String> getFilesFromResources( final String directory ) throws IOException {
      final InputStream directoryUrl = getClass().getClassLoader().getResourceAsStream( directory );
      if ( directoryUrl == null ) {
         LOG.warn( "No such classpath directory {}", directory );
         return Stream.empty();
      }
      return Arrays.stream( IOUtils.toString( directoryUrl, StandardCharsets.UTF_8 ).split( "\\n" ) );
   }

   private Stream<String> getFilesFromJar( final String directory, final File jarFile ) throws IOException {
      final List<String> fileList = new ArrayList<>();
      final JarFile jar = new JarFile( jarFile );
      final Enumeration<JarEntry> entries = jar.entries();
      final String dir = directory.endsWith( "/" ) ? directory : directory + "/";
      while ( entries.hasMoreElements() ) {
         final String name = entries.nextElement().getName();
         if ( name.startsWith( dir ) ) {
            final String fileName = name.replace( dir, "" );
            if ( StringUtils.isNotEmpty( fileName ) ) {
               fileList.add( fileName );
            }
         }
      }
      jar.close();
      return fileList.stream();
   }

   @Override
   public AspectModelFile apply( final AspectModelUrn aspectModelUrn, final ResolutionStrategySupport resolutionStrategySupport ) {
      final String modelsRootTrailingSlash = modelsRoot.isEmpty() ? "" : "/";
      final String directory = String.format( "%s%s%s/%s", modelsRoot, modelsRootTrailingSlash,
            aspectModelUrn.getNamespace(), aspectModelUrn.getVersion() );
      final URL namedResourceFile = resourceUrl( directory, aspectModelUrn.getName() + ".ttl" );

      if ( namedResourceFile != null ) {
         return AspectModelFileLoader.load( namedResourceFile );
      }

      LOG.warn( "Looking for {}, but no {}.ttl was found. Inspecting files in {}", aspectModelUrn.getName(),
            aspectModelUrn.getName(), directory );

      return filesInDirectory( directory )
            .filter( name -> name.endsWith( ".ttl" ) )
            .map( name -> resourceUrl( directory, name ) )
            .sorted( Comparator.comparing( URL::getPath ) )
            .map( AspectModelFileLoader::load )
            .filter( aspectModelFile -> resolutionStrategySupport.containsDefinition( aspectModelFile, aspectModelUrn ) )
            .findFirst()
            .orElseThrow( () -> new ModelResolutionException(
                  "No model file containing " + aspectModelUrn + " could be found in directory: " + directory ) );
   }
}
