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

package org.eclipse.esmf.substitution;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.esmf.SammCli;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

/**
 * This is a <a href="https://build-native-java-apps.cc/developer-guide/substitution/">GraalVM substitution class</a>
 * for sun.awt.FontConfiguration.
 * Reason: In Windows, AWT internal will try to load a font config file that is only available at runtime and will be
 * looked for - hard coded - in $JAVA_HOME. This substitution redirects this to read the config from the resources instead.
 * This only works in conjuction with a build setup that makes the font config available in the build time class path,
 * see copy-fontconfig-bfc in pom.xml; as well as a corresponding entry in resource-config.json.
 */
@TargetClass( className = "sun.awt.FontConfiguration", onlyWith = IsWindows.class )
@SuppressWarnings( {
      "unused",
      "squid:S00101", // Class name uses GraalVM substitution class naming schema, see
      // https://github.com/oracle/graal/tree/master/substratevm/src/com.oracle.svm.core/src/com/oracle/svm/core/jdk
      "NewClassNamingConvention",
      "checkstyle:TypeName"
} )
public final class Target_sun_awt_FontConfiguration {
   @Substitute
   private void readFontConfigFile( final File f ) {
      try ( final InputStream inputStream = SammCli.class.getResourceAsStream( "/fontconfig.bfc" ) ) {
         loadBinary( inputStream );
      } catch ( final IOException e ) {
         throw new RuntimeException( e );
      }
   }

   // Reference to the original loadBinary() method, so that we can call it from readFontConfigFile
   @Alias
   public static native void loadBinary( InputStream inStream ) throws IOException;
}

