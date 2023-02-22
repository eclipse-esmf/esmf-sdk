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

package org.eclipse.esmf.aspectmodel.versionupdate;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MigratorServiceLoaderTest {
   private final MigratorService migratorService = MigratorServiceLoader.getInstance().getMigratorService();

   @Test
   public void testLoadService() {
      assertThat( migratorService.getMigratorFactory().get() ).isInstanceOf( TestMigratorFactory1.class );
      Assertions.assertThat( migratorService.getMigratorFactory().get().createMigrators() ).hasSize( 4 );
   }
}
