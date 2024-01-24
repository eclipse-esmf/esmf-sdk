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

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.util.ReflectionHelper;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.hosted.Feature;

/**
 * Registers all classes in the AAS model and default implementation packages for reflection in native image builds.
 */
@Platforms( Platform.HOSTED_ONLY.class )
public class AasReflection extends RegisterReflection implements Feature {
   @Override
   public void beforeAnalysis( final BeforeAnalysisAccess access ) {
      registerClassesInPackage( ReflectionHelper.MODEL_PACKAGE_NAME );
      registerClassesInPackage( ReflectionHelper.DEFAULT_IMPLEMENTATION_PACKAGE_NAME );
   }
}
