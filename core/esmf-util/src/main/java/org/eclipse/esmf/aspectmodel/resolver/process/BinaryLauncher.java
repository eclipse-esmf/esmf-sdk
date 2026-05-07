/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.resolver.process;

import java.io.File;
import java.util.List;

/**
 * A {@link ProcessLauncher} that executes a native binary.
 */
public class BinaryLauncher extends OsProcessLauncher {
   public BinaryLauncher( final File binary ) {
      super( List.of( binary.getAbsolutePath() ) );
   }
}
