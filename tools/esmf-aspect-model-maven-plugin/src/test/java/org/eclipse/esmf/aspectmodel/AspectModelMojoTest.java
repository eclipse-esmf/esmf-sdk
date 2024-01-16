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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.After;
import org.junit.Before;

public abstract class AspectModelMojoTest extends AbstractMojoTestCase {
   // Due to the nature of how the maven plugin testing harness works, we use a temp directory relative to the project
   protected Path tempDir = Path.of( System.getProperty( "user.dir" ), "target", "test-artifacts" );

   public void setUp() throws Exception {
      super.setUp();
      FileUtils.deleteDirectory( tempDir.toFile() );
      Files.createDirectories( tempDir );
   }

   public void tearDown() throws Exception {
      super.tearDown();
      FileUtils.deleteDirectory( tempDir.toFile() );
   }

   protected Path generatedFilePath( final String... pathParts ) {
      return Path.of( tempDir.toString(), pathParts );
   }
}
