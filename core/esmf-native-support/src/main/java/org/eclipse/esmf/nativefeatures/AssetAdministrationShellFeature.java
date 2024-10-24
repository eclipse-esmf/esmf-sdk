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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.esmf.buildtime.Aas4jClassSetup;
import org.eclipse.esmf.substitution.AdminShellConfig;

import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.hosted.Feature;

/**
 * Registers all classes in the AAS model and default implementation packages for reflection in native image builds.
 */
@Platforms( Platform.HOSTED_ONLY.class )
public class AssetAdministrationShellFeature implements Feature {
   /**
    * The properties file that provides information about AAS features that can otherwise only be retrieved using reflection.
    * See {@link AdminShellConfig} for more information.
    */
   public static final String ADMINSHELL_PROPERTIES = "adminshell.properties";

   @Override
   public void beforeAnalysis( final BeforeAnalysisAccess access ) {
      final Properties adminShellProperties = new Properties();
      final InputStream propertiesResource = AssetAdministrationShellFeature.class.getClassLoader()
            .getResourceAsStream( ADMINSHELL_PROPERTIES );
      try {
         adminShellProperties.load( propertiesResource );
      } catch ( final IOException exception ) {
         throw new RuntimeException( "Failed to load " + ADMINSHELL_PROPERTIES );
      }
      final AdminShellConfig adminShellConfig = AdminShellConfig.fromProperties( adminShellProperties );

      /*
       * The following commands register AAS4J model classes for reflection. Note that this is done using the pre-calculated
       * list of classes that we load from adminshell.properties: This prevents the dependency on the classgraph-library at
       * native image compile time.
       */
      adminShellConfig.classesInModelPackage.forEach( cls -> Native.forClass( cls ).registerEverythingForReflection() );
      adminShellConfig.classesInDefaultImplementationPackage.forEach( cls -> Native.forClass( cls ).registerEverythingForReflection() );
      adminShellConfig.classesInJsonMixinsPackage.forEach( cls -> Native.forClass( cls ).registerEverythingForReflection() );
      adminShellConfig.classesInXmlMixinsPackage.forEach( cls -> Native.forClass( cls ).registerEverythingForReflection() );

      Native.forClass( LangStringTextType[].class )
            .registerEverythingForReflection();
   }
}
