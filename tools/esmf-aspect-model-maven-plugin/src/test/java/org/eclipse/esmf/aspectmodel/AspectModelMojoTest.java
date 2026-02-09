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

package org.eclipse.esmf.aspectmodel;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class AspectModelMojoTest {
   // Due to the nature of how the maven plugin testing harness works, we use a temp directory relative to the project
   protected static final Path TEMP_DIR = Path.of( System.getProperty( "user.dir" ), "target", "test-artifacts" );
   protected static final String GITHUB_SERVER_CONFIG_ID = "test-github-config";

   @BeforeEach
   public void setUp() throws Exception {
      FileUtils.deleteDirectory( TEMP_DIR.toFile() );
      Files.createDirectories( TEMP_DIR );
   }

   @AfterEach
   public void tearDown() throws Exception {
      FileUtils.deleteDirectory( TEMP_DIR.toFile() );
   }

   protected Path generatedFilePath( final String... pathParts ) {
      return Path.of( TEMP_DIR.toString(), pathParts );
   }
}
