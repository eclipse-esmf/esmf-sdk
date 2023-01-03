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

package io.openmanufacturing.sds;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The CLI unit tests execute the {@link BammCli} main class
 */
@ExtendWith( LogExtension.class )
public class BammCliUnitTest extends SharedTestCode {
   public BammCliUnitTest() {
      super( new MainClassProcessLauncher( BammCli.class ) );
   }
}
