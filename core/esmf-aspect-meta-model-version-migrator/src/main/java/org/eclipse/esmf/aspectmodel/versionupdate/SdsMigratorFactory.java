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

import java.util.List;

import org.eclipse.esmf.aspectmodel.resolver.AspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.versionupdate.migrator.Migrator;

import com.google.common.collect.ImmutableList;

import org.eclipse.esmf.samm.KnownVersion;

import org.eclipse.esmf.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.versionupdate.migrator.SdsMetaModelVersionUriRewriter;
import org.eclipse.esmf.aspectmodel.versionupdate.migrator.SdsRemoveBammNameMigrator;
import org.eclipse.esmf.aspectmodel.versionupdate.migrator.UnitInBammNamespaceMigrator;

/**
 * Includes all SDS migrators
 */
public class SdsMigratorFactory {
   private final SdsAspectMetaModelResourceResolver metaModelResourceResolver = new SdsAspectMetaModelResourceResolver();
   private final List<Migrator> migrators = ImmutableList.<Migrator> builder()
         .add( new SdsMetaModelVersionUriRewriter( KnownVersion.SAMM_1_0_0, KnownVersion.SAMM_2_0_0 ) )
         .add( new SdsRemoveBammNameMigrator( KnownVersion.SAMM_1_0_0, KnownVersion.SAMM_2_0_0 ) )
         .add( new UnitInBammNamespaceMigrator() )
         .build();

   public List<Migrator> createMigrators() {
      return migrators;
   }

   public AspectMetaModelResourceResolver createAspectMetaModelResourceResolver() {
      return metaModelResourceResolver;
   }
}