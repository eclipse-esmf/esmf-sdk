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

package org.eclipse.esmf.buildtime;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Generates the static version info class
 */
public class GenerateVersionInfo extends BuildtimeCodeGenerator {
   private final Properties properties;

   protected GenerateVersionInfo( final Path srcBuildTimePath, final Path srcGenPath ) {
      super( srcBuildTimePath, srcGenPath, "VersionInfo", "org.eclipse.esmf.aspectmodel" );

      try ( final InputStream stream = GenerateVersionInfo.class.getClassLoader().getResourceAsStream( "app.properties" ) ) {
         if ( stream == null ) {
            throw new RuntimeException();
         }
         properties = new Properties();
         properties.load( stream );
      } catch ( final Exception exception ) {
         throw new RuntimeException( "Could not load app.properties" );
      }
   }

   @Override
   protected String interpolateVariable( final String variableName ) {
      return switch ( variableName ) {
         case "esmfSdkVersion" -> properties.getProperty( "project-version" );
         case "aspectMetaModelVersion" -> properties.getProperty( "aspect-meta-model-version" );
         case "generator" -> getClass().getCanonicalName();
         default -> throw new RuntimeException( "Unexpected variable in template: " + variableName );
      };
   }
}
