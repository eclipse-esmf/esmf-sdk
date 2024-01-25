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

import static org.graalvm.nativeimage.hosted.RuntimeClassInitialization.initializeAtBuildTime;
import static org.graalvm.nativeimage.impl.ConfigurationCondition.alwaysTrue;

import java.util.List;

import com.oracle.svm.core.configure.ResourcesRegistry;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.Feature;

/**
 * Build time initialization for samm-cli
 */
public class SammCliFeatures extends AbstractSammCliFeature {
   @Override
   public void beforeAnalysis( final BeforeAnalysisAccess access ) {
      initializeAtBuildTime( com.google.common.collect.Range.class );
      initializeAtBuildTime( getClass( "com.sun.tools.javac.file.BaseFileManager" ) );
      initializeAtBuildTime( getClass( "com.sun.tools.javac.file.JavacFileManager" ) );
      initializeAtBuildTime( getClass( "com.sun.tools.javac.file.Locations" ) );
      initializeAtBuildTime( getClass( "com.sun.tools.javac.util.Context" ) );
      initializeAtBuildTime( getClass( "jdk.internal.net.http.HttpClientImpl" ) );
      initializeAtBuildTime( getClass( "jdk.internal.net.http.HttpClientImpl$SelectorManager" ) );
      initializeAtBuildTime( getClass( "jdk.internal.net.http.common.Utils" ) );

      final ResourcesRegistry resourcesRegistry = ImageSingletons.lookup( ResourcesRegistry.class );
      resourcesRegistry.addResourceBundles( alwaysTrue(), "com.sun.tools.javac.resources.compiler" );
      resourcesRegistry.addResourceBundles( alwaysTrue(), "com.sun.tools.javac.resources.javac" );
   }

   @Override
   public List<Class<? extends Feature>> getRequiredFeatures() {
      return List.of(
            AasReflection.class,
            LogbackFeature.class,
            ShaclJsFeature.class,
            ClassGraphFeature.class,
            JenaFeature.class
      );
   }
}
