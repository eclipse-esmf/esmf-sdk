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

import java.lang.reflect.InvocationTargetException;

import org.graalvm.nativeimage.hosted.Feature;
import org.junit.jupiter.api.Test;

public class EsmfFeatureTest {
   /**
    * This method can not test if registration of classes/methods/resources succeeds at native compilation time, but it can make sure
    * that all classes that are instantiated from features at runtime using Class.forName, as well as fields, do in fact exist.
    */
   @Test
   void testClassInstantiation() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
      final Feature esmf = new EsmfFeature();
      esmf.beforeAnalysis( null );
      for ( final Class<? extends Feature> feature : esmf.getRequiredFeatures() ) {
         feature.getDeclaredConstructor().newInstance().beforeAnalysis( null );
      }
   }
}
