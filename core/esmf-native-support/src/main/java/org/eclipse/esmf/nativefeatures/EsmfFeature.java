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

import java.util.List;

import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfig;

import org.graalvm.nativeimage.hosted.Feature;

/**
 * Build time initialization for esmf-sdk in native-image context
 */
public class EsmfFeature implements Feature {
   @Override
   public void beforeAnalysis( final BeforeAnalysisAccess access ) {
      Native.forClass( com.google.common.collect.Range.class )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.tools.javac.file.BaseFileManager" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.tools.javac.file.JavacFileManager" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.tools.javac.file.Locations" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.tools.javac.util.Context" )
            .initializeAtBuildTime();
      Native.forClass( "jdk.internal.net.http.HttpClientImpl" )
            .initializeAtBuildTime();
      Native.forClass( "jdk.internal.net.http.HttpClientImpl$SelectorManager" )
            .initializeAtBuildTime();
      Native.forClass( "jdk.internal.net.http.common.Utils" )
            .initializeAtBuildTime();

      Native.addResourceBundle( "com.sun.tools.javac.resources.compiler" );
      Native.addResourceBundle( "com.sun.tools.javac.resources.javac" );

      Native.forClass( "sun.util.resources.LocaleData$LocaleDataStrategy" )
            .registerEverythingForReflection();
      Native.forClass( "sun.util.resources.LocaleData" )
            .registerEverythingForReflection();

      Native.forClass( java.util.concurrent.atomic.AtomicBoolean.class )
            .registerFieldsForReflection( "value" );
      Native.forClass( org.eclipse.esmf.metamodel.impl.DefaultScalarValue.class )
            .registerEverythingForReflection();
      Native.forClass( org.eclipse.esmf.metamodel.impl.DefaultProperty.class )
            .registerEverythingForReflection();
      Native.forClass( org.eclipse.esmf.aspectmodel.loader.DefaultPropertyWrapper.class )
            .registerEverythingForReflection();
      Native.forClass( JavaCodeGenerationConfig.class )
            .registerEverythingForReflection();

      Native.forClass( java.lang.Thread.class )
            .registerEverythingForReflection()
            .registerEverythingForJni();
      Native.forClass( java.lang.Boolean.class )
            .registerMethodForReflection( "getBoolean", String.class );
      Native.forClass( java.lang.System.class )
            .registerMethodForReflection( "load", String.class )
            .registerMethodForReflection( "setProperty", String.class, String.class );
      Native.forClass( java.lang.String.class )
            .registerMethodForReflection( "toLowerCase", java.util.Locale.class );
      Native.forClass( java.util.ArrayList.class )
            .registerConstructorForReflection( int.class )
            .registerMethodForReflection( "add", Object.class );
      Native.forClass( java.util.HashMap.class )
            .registerMethodForReflection( "containsKey", Object.class )
            .registerMethodForReflection( "put", Object.class, Object.class );

      // JDK and GraalVM config
      Native.addResourcesPattern( "java.base:\\Qjdk/internal/icu/impl/data/icudt67b/ubidi.icu\\E" );
      Native.addResource( "META-INF/services/com.oracle.truffle.api.TruffleLanguage$Provider" );
      Native.addResource( "META-INF/services/com.oracle.truffle.api.instrumentation.TruffleInstrument$Provider" );
      Native.addResource( "META-INF/services/javax.script.ScriptEngineFactory" );
      Native.addResource( "com/oracle/truffle/nfi/impl/NFILanguageImpl.class" );

      // ESMF artifacts generation config
      Native.addResource( "openapi/*.json" );
      Native.addResource( "docu/static/*.*" );
      Native.addResource( "docu/styles/*.*" );
      Native.addResource( "docu/aspect-model.properties" );

      // SAMM artifacts
      Native.addResourcesPattern( "samm/[^/]*/([^/]*/)?[a-zA-Z0-9-]+\\.(ttl|js)" );
   }

   @Override
   public List<Class<? extends Feature>> getRequiredFeatures() {
      return List.of(
            AssetAdministrationShellFeature.class,
            LogbackFeature.class,
            ShaclJsFeature.class,
            JenaFeature.class,
            DiagramFeature.class
      );
   }
}
