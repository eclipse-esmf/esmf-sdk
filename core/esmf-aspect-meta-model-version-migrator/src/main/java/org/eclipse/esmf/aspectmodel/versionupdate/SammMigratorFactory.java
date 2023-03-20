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

import org.eclipse.esmf.aspectmodel.resolver.services.SammAspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.versionupdate.migrator.SammMetaModelVersionUriRewriter;
import org.eclipse.esmf.aspectmodel.versionupdate.migrator.SammRemoveSammNameMigrator;
import org.eclipse.esmf.aspectmodel.versionupdate.migrator.UnitInSammNamespaceMigrator;

/**
 * Includes all SAMM migrators
 */
public class SammMigratorFactory {
   private final SammAspectMetaModelResourceResolver metaModelResourceResolver = new SammAspectMetaModelResourceResolver();
   private final List<Migrator> migrators = ImmutableList.<Migrator> builder()
         .add( new SammMetaModelVersionUriRewriter( KnownVersion.SAMM_1_0_0, KnownVersion.SAMM_2_0_0 ) )
         .add( new SammRemoveSammNameMigrator( KnownVersion.SAMM_1_0_0, KnownVersion.SAMM_2_0_0 ) )
         .add( new UnitInSammNamespaceMigrator() )
         .build();

   public List<Migrator> createMigrators() {
      return migrators;
   }

   public AspectMetaModelResourceResolver createAspectMetaModelResourceResolver() {
      return metaModelResourceResolver;
   }
}
