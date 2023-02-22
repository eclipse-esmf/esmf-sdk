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

import org.eclipse.esmf.aspectmodel.VersionNumber;

import org.eclipse.esmf.samm.KnownVersion;

/**
 * Abstract migration function that is used to apply a change to all URIs in a model
 */
public abstract class AbstractSdsMigrator extends AbstractMigrator {
   private final KnownVersion sourceKnownVersion;
   private final KnownVersion targetKnownVersion;

   protected AbstractSdsMigrator( final KnownVersion sourceKnownVersion, final KnownVersion targetKnownVersion, final int order ) {
      super( VersionNumber.parse( sourceKnownVersion.toVersionString() ), VersionNumber.parse( targetKnownVersion.toVersionString() ), order );
      this.sourceKnownVersion = sourceKnownVersion;
      this.targetKnownVersion = targetKnownVersion;
   }

   public KnownVersion getTargetKnownVersion() {
      return targetKnownVersion;
   }

   public KnownVersion getSourceKnownVersion() {
      return sourceKnownVersion;
   }
}
