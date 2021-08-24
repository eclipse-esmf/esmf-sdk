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

package io.openmanufacturing.sds;

import java.security.Permission;

/**
 * Custom {@link SecurityManager} which prevents the call to System.exit to stop the JVM.
 * It is used in the unit tests to be able to execute assertions after the call to System.exit
 */
public class TestSecurityManager extends SecurityManager {

   @Override
   public void checkPermission( final Permission perm ) {
   }

   @Override
   public void checkPermission( final Permission perm, final Object context ) {
   }

   @Override
   public void checkExit( final int status ) {
      throw new RuntimeException( "Do not stop JVM." );
   }
}
