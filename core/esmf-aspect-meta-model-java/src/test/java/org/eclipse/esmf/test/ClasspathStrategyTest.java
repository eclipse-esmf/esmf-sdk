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

package org.eclipse.esmf.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.aspectmodel.resolver.ClasspathStrategy;
import org.eclipse.esmf.samm.KnownVersion;

import org.junit.jupiter.api.Test;

public class ClasspathStrategyTest {
   @Test
   void testListContents() {
      final String directoryName = KnownVersion.getLatest().toString().toLowerCase();
      final ClasspathStrategy classpathStrategy = new ClasspathStrategy( directoryName );
      assertThat( classpathStrategy.listContents() ).isNotEmpty();
   }
}
