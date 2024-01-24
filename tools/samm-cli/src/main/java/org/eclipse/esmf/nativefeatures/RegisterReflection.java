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
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.hosted.RuntimeReflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class that provides facilities for registering classes and packages for reflection in native image builds.
 */
@Platforms( Platform.HOSTED_ONLY.class )
public abstract class RegisterReflection {
   private static final Logger LOG = LoggerFactory.getLogger( RegisterReflection.class );

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
}
