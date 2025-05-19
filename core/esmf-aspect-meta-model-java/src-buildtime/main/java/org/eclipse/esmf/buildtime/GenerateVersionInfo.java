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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Generates the static version info class
 */
public class GenerateVersionInfo extends BuildtimeCodeGenerator {
   private final Properties appProperties;
   private final Properties gitProperties;

   protected GenerateVersionInfo( final Path srcBuildTimePath, final Path srcGenPath ) {
      super( srcBuildTimePath, srcGenPath, "VersionInfo", "org.eclipse.esmf.aspectmodel" );

      appProperties = loadProperties( "app.properties" );
      gitProperties = loadProperties( "git.properties" );
   }

   private Properties loadProperties( final String filename ) {
      try ( final InputStream stream = GenerateVersionInfo.class.getClassLoader().getResourceAsStream( filename ) ) {
         if ( stream == null ) {
            throw new RuntimeException();
         }
         final Properties properties = new Properties();
         properties.load( stream );
         return properties;
      } catch ( final Exception exception ) {
         throw new RuntimeException( "Could not load app.properties" );
      }
   }

   @Override
   protected String interpolateVariable( final String variableName ) {
      return switch ( variableName ) {
         case "esmfSdkVersion" -> appProperties.getProperty( "project-version" );
         case "aspectMetaModelVersion" -> appProperties.getProperty( "aspect-meta-model-version" );
         case "buildDate" -> new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( new Date() );
         case "commitId" -> gitProperties.getProperty( "git.commit.id" );
         case "generator" -> getClass().getCanonicalName();
         default -> throw new RuntimeException( "Unexpected variable in template: " + variableName );
      };
   }
}
