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

package org.eclipse.esmf.nativefeatures;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for native compilation features
 */
public abstract class AbstractSammCliFeature implements Feature {
   private static final Logger LOG = LoggerFactory.getLogger( AbstractSammCliFeature.class );

   /**
    * Registers all classes in the given package for reflection.
    *
    * @param packageName the package name
    */
   protected void registerClassesInPackage( final String packageName ) {
      try ( final ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages( packageName ).scan() ) {
         scanResult.getAllClasses().loadClasses().forEach( this::register );
      }
   }

   /**
    * Registers the given class for reflection.
    *
    * @param cls the class
    */
   protected void register( final Class<?> cls ) {
      LOG.debug( "Registering {} for reflection", cls );
      RuntimeReflection.register( cls );
      RuntimeReflection.register( cls.getDeclaredConstructors() );
      RuntimeReflection.register( cls.getDeclaredMethods() );
      RuntimeReflection.register( cls.getDeclaredFields() );
   }

   /**
    * Returns the class for the given class name.
    *
    * @param className the class name
    * @return the class
    */
   protected Class<?> getClass( final String className ) {
      try {
         return Class.forName( className );
      } catch ( final ClassNotFoundException exception ) {
         throw new RuntimeException( "Could not access class for reflection registry", exception );
      }
   }
}
