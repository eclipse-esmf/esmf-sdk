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

import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The CLI integration tests can either run against the executable jar (using the {@link ExecutableJarLauncher} or against the native image (binary)
 * (using the {@link BinaryLauncher)}. Which one is executed is determined using the system property "packaging-type" which can be "jar" or "native".
 */
@ExtendWith( LogExtension.class )
public class BammCliIntegrationTest extends SharedTestCode {
   public BammCliIntegrationTest() {
      super( Optional.ofNullable( System.getProperty( "packaging-type" ) ).orElse( "jar" ).equals( "jar" ) ?
            new ExecutableJarLauncher() :
            new BinaryLauncher() );
   }
}
