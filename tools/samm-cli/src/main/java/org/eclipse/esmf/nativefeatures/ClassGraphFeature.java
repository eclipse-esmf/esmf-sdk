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

/**
 * Makes ClassGraph available at build time
 */
public class ClassGraphFeature extends AbstractSammCliFeature {
   @Override
   public void beforeAnalysis( final BeforeAnalysisAccess access ) {
      initializeAtBuildTime( io.github.classgraph.AnnotationInfoList.class );
      initializeAtBuildTime( io.github.classgraph.AnnotationParameterValueList.class );
      initializeAtBuildTime( io.github.classgraph.ClassGraph.class );
      initializeAtBuildTime( io.github.classgraph.ClassInfo.class );
      initializeAtBuildTime( getClass( "io.github.classgraph.ClassInfo$2" ) );
      initializeAtBuildTime( io.github.classgraph.ClassInfoList.class );
      initializeAtBuildTime( io.github.classgraph.FieldInfoList.class );
      initializeAtBuildTime( io.github.classgraph.MethodInfoList.class );
      initializeAtBuildTime( io.github.classgraph.ModulePathInfo.class );
      initializeAtBuildTime( io.github.classgraph.ScanResult.class );
      initializeAtBuildTime( nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandlerRegistry.class );
      initializeAtBuildTime( nonapi.io.github.classgraph.classpath.ClasspathOrder.class );
      initializeAtBuildTime( nonapi.io.github.classgraph.classpath.SystemJarFinder.class );
      initializeAtBuildTime( getClass( "nonapi.io.github.classgraph.classpath.SystemJarFinder$1" ) );
      initializeAtBuildTime( nonapi.io.github.classgraph.fastzipfilereader.LogicalZipFile.class );
      initializeAtBuildTime( getClass( "nonapi.io.github.classgraph.reflection.StandardReflectionDriver" ) );
      initializeAtBuildTime( nonapi.io.github.classgraph.utils.FastPathResolver.class );
      initializeAtBuildTime( nonapi.io.github.classgraph.utils.JarUtils.class );
      initializeAtBuildTime( nonapi.io.github.classgraph.utils.VersionFinder.class );
   }
}
