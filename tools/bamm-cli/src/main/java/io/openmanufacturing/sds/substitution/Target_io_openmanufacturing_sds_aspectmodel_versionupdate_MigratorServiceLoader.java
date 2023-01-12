/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.substitution;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

import io.openmanufacturing.sds.aspectmodel.versionupdate.MigratorService;
import io.openmanufacturing.sds.aspectmodel.versionupdate.MigratorServiceLoader;

/**
 * This is a <a href="https://build-native-java-apps.cc/developer-guide/substitution/">GraalVM substitution class</a>
 * for {@link MigratorServiceLoader}.
 * Reason: The original class uses the classgraph library, which does runtime classpath scanning. This is not supported as such
 * in GraalVM. If in the future a known MigratorFactory should be supported by the CLI, this can be added in the substitution method.
 */
@TargetClass( MigratorServiceLoader.class )
@SuppressWarnings( {
      "unused",
      "squid:S00101" // Class name uses GraalVM substitution class naming schema, see
      // https://github.com/oracle/graal/tree/master/substratevm/src/com.oracle.svm.core/src/com/oracle/svm/core/jdk
} )
public final class Target_io_openmanufacturing_sds_aspectmodel_versionupdate_MigratorServiceLoader {
   @Alias
   private MigratorService migratorService;

   private Target_io_openmanufacturing_sds_aspectmodel_versionupdate_MigratorServiceLoader() {
   }

   @Substitute
   private void loadMigratorService() {
      migratorService = new MigratorService();
   }
}
