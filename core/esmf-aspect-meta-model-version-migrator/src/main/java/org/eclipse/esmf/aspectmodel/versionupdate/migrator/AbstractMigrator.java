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

package org.eclipse.esmf.aspectmodel.versionupdate.migrator;

import java.util.Optional;

import org.eclipse.esmf.aspectmodel.VersionNumber;

/**
 * Abstract migration function that is used to apply a change to all URIs in a model
 */
public abstract class AbstractMigrator implements Migrator {
   private final VersionNumber sourceVersion;
   private final VersionNumber targetVersion;
   private final int order;

   protected AbstractMigrator( final VersionNumber sourceVersion, final VersionNumber targetVersion, final int order ) {
      this.sourceVersion = sourceVersion;
      this.targetVersion = targetVersion;
      this.order = order;
   }

   protected AbstractMigrator( final VersionNumber sourceVersion, final VersionNumber targetVersion ) {
      this( sourceVersion, targetVersion, 100 );
   }

   @Override
   public int order() {
      return order;
   }

   @Override
   public VersionNumber targetVersion() {
      return targetVersion;
   }

   @Override
   public VersionNumber sourceVersion() {
      return sourceVersion;
   }

   @Override
   public Optional<String> getDescription() {
      return Optional.empty();
   }
}
