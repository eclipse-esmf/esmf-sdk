/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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
package io.openmanufacturing.sds.aspectmodel.versionupdate;

import java.util.Collections;
import java.util.List;

import io.openmanufacturing.sds.aspectmodel.resolver.AspectMetaModelResourceResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import io.openmanufacturing.sds.aspectmodel.versionupdate.migrator.Migrator;

/**
 * Includes all DSDS migrators
 */
public class SdsMigratorFactory {

   private final SdsAspectMetaModelResourceResolver metaModelResourceResolver = new SdsAspectMetaModelResourceResolver();
   private final List<Migrator> migrators = Collections.emptyList();

   public List<Migrator> createMigrators() {
      return migrators;
   }

   public AspectMetaModelResourceResolver createAspectMetaModelResourceResolver() {
      return metaModelResourceResolver;
   }
}
