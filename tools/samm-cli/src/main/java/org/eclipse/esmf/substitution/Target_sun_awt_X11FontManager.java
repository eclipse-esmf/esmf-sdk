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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.velocity.runtime.parser.node.ASTMethod;

/**
 * This is a <a href="https://build-native-java-apps.cc/developer-guide/substitution/">GraalVM substitution class</a>
 * for {@link ASTMethod}.
 * Reason: In Linux/X11, AWT internal will try to resolve font configuration files from the JDK directory (java.home)
 * which will be null in the GraalVM binary. This substitution will hardcode the code path that does not check for those configs.
 * Unfortunately, since the GraalVM compiler itself is tripped up by sun.awt classes in the resolution graph, we need to
 * trick it by accessing the relevant code via reflection. This in turn means that we need to put X11FontManager & friends into
 * reflection-config.json
 */
@TargetClass( className = "sun.awt.X11FontManager" )
@SuppressWarnings( {
      "unused",
      "squid:S00101" // Class name uses GraalVM substitution class naming schema, see
      // https://github.com/oracle/graal/tree/master/substratevm/src/com.oracle.svm.core/src/com/oracle/svm/core/jdk
      , "NewClassNamingConvention" } )
public final class Target_sun_awt_X11FontManager {
   /**
    * This method actually returns a sun.awt.FontConfiguration.
    * @return
    */
   @SuppressWarnings( "ProtectedMemberInFinalClass" )
   @Substitute
   protected Object createFontConfiguration() {
      try {
         final Class<?> clazz = Class.forName( "sun.font.FcFontConfiguration" );
         final Class<?> sunFontManagerClazz = Class.forName( "sun.font.SunFontManager" );
         final Constructor<?> constructor = clazz.getConstructor( sunFontManagerClazz );
         final Object fcFontConfig = constructor.newInstance( this );
         final Method init = clazz.getMethod( "init" );
         // ignore result
         init.invoke( fcFontConfig );
         return fcFontConfig;
      } catch ( final ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                      IllegalAccessException e ) {
         throw new RuntimeException( e );
      }
   }
}
