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

package org.eclipse.esmf.aspectmodel.java;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ImportTrackerTest {
   @Test
   void testType() {
      final ImportTracker importTracker = new ImportTracker();
      final String type = "java.lang.Long";
      importTracker.trackPotentiallyParameterizedType( type );
      assertThat( importTracker.getUsedImports() ).containsExactlyInAnyOrder( "java.lang.Long" );
   }

   @Test
   void testGenericType() {
      final ImportTracker importTracker = new ImportTracker();
      final String type = "java.util.LinkedHashSet<java.lang.Long>>";
      importTracker.trackPotentiallyParameterizedType( type );
      assertThat( importTracker.getUsedImports() ).containsExactlyInAnyOrder( "java.lang.Long", "java.util.LinkedHashSet" );
   }

   @Test
   void testOptionalGenericType() {
      final ImportTracker importTracker = new ImportTracker();
      final String type = "java.util.Optional<java.util.LinkedHashSet<java.lang.Long>>";
      importTracker.trackPotentiallyParameterizedType( type );
      assertThat( importTracker.getUsedImports() ).containsExactlyInAnyOrder( "java.util.Optional", "java.util.LinkedHashSet",
            "java.lang.Long" );
   }

   @Test
   void testDoubleGenericType() {
      final ImportTracker importTracker = new ImportTracker();
      final String type = "test.importTracker.StaticContainerProperty<test.importTracker.TestEntity,java.util.Optional<test.importTracker"
            + ".TestEntity>>";
      importTracker.trackPotentiallyParameterizedType( type );
      assertThat( importTracker.getUsedImports() ).containsExactlyInAnyOrder( "test.importTracker.StaticContainerProperty",
            "test.importTracker.TestEntity",
            "java.util.Optional" );
   }
}
