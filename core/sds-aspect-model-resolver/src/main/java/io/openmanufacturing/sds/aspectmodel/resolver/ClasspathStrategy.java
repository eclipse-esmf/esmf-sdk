/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.resolver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.vavr.control.Try;

/**
 * Resolution strategy to resolve Aspect models by URN from a well-defined directory structure from the class path
 */
public class ClasspathStrategy extends AbstractResolutionStrategy {
   private static final Logger LOG = LoggerFactory.getLogger( ClasspathStrategy.class );
   private final String modelsRoot;

   /**
    * Initialize the ClasspathStrategy with an empty root path for models. The classpath
    * is assumed to contain a file system hierarchy as follows: <code>N/V/X.ttl</code> where N is the namespace,
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
    * is assumed to contain a file system hierarchy as follows: <code>N/V/X.ttl</code> where N is the namespace,
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
         final InputStream directoryUrl = getClass().getResourceAsStream( directory );
         if ( directoryUrl == null ) {
            LOG.warn( "No such classpath directory {}", directory );
            return Stream.empty();
         }
         return Arrays.stream( IOUtils.toString( directoryUrl, StandardCharsets.UTF_8 ).split( "\\n" ) );
      } catch ( final IOException exception ) {
         LOG.warn( "Could not list files in classpath directory {}", directory, exception );
         return Stream.empty();
      }
   }

   @Override
   public Try<Model> apply( final AspectModelUrn aspectModelUrn ) {
      final String modelsRootTrailingSlash = modelsRoot.isEmpty() ? "" : "/";
      final String directory = String.format( "%s%s%s/%s", modelsRoot, modelsRootTrailingSlash,
            aspectModelUrn.getNamespace(), aspectModelUrn.getVersion() );
      final URL namedResourceFile = resourceUrl( directory, aspectModelUrn.getName() + ".ttl" );

      if ( namedResourceFile != null ) {
         return loadFromUrl( namedResourceFile );
      }

      LOG.warn( "Looking for {}, but no {}.ttl was found. Inspecting files in {}", aspectModelUrn.getName(),
            aspectModelUrn.getName(), directory );

      return filesInDirectory( directory )
            .filter( name -> name.endsWith( ".ttl" ) )
            .map( name -> resourceUrl( directory, name ) )
            .sorted()
            .map( this::loadFromUrl )
            .filter( tryModel -> tryModel
                  .map( model -> AspectModelResolver.containsDefinition( model, aspectModelUrn ) )
                  .getOrElse( false ) )
            .findFirst()
            .orElse( Try.failure( new FileNotFoundException(
                  "The AspectModel: " + aspectModelUrn.toString() + " could not be found in directory: " + directory ) ) );
   }
}
