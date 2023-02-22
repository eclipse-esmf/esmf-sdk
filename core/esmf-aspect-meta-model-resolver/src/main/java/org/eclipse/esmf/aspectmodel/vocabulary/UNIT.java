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

package org.eclipse.esmf.aspectmodel.vocabulary;

import org.eclipse.esmf.samm.KnownVersion;

public class UNIT implements Namespace {
   private final KnownVersion bammVersion;
   private final BAMM bamm;

   public UNIT( final KnownVersion bammVersion, final BAMM bamm ) {
      this.bammVersion = bammVersion;
      this.bamm = bamm;
   }

   @Override
   public String getUri() {
      return bamm.getBaseUri() + "unit:" + bammVersion.toVersionString();
   }
}
