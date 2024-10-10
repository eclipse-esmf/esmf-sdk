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

package org.eclipse.esmf.aspectmodel.edit.change;

import java.util.function.UnaryOperator;

import org.eclipse.esmf.aspectmodel.VersionNumber;

public enum IncreaseVersion implements UnaryOperator<VersionNumber> {
   MAJOR,
   MINOR,
   MICRO;

   @Override
   public VersionNumber apply( final VersionNumber previous ) {
      return switch ( this ) {
         case MAJOR -> previous.nextMajor();
         case MINOR -> previous.nextMinor();
         case MICRO -> previous.nextMicro();
      };
   }
}
