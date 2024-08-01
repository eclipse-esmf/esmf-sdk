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

package org.eclipse.esmf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.scanner.AspectModelScanner;

import org.junit.jupiter.api.Test;

class GitHubScannerTest {

   @Test
   void testGithubScannerExpectSuccess() {
      final String aspectModelFileName = "core/esmf-test-aspect-models/src/main/resources/valid/org.eclipse.esmf.test/1.0"
            + ".0/propertyWithoutExampleValue.ttl";
      final String repositoryUrl = "eclipse-esmf/esmf-sdk";
      final String branch = "main";

      final AspectModelScanner aspectModelScanner = new GitHubScanner( repositoryUrl, branch );
      final List<AspectModelFile> aspectModelFiles = aspectModelScanner.find( aspectModelFileName );

      assertThat( aspectModelFiles ).hasSize( 1 );
   }

   @Test
   void testGithubScannerWithInvalidFileUrl() {
      final String aspectModelFileName = "core/esmf-test-aspect-models/src/main/resources/valid/org.eclipse.esmf.test/1.0"
            + ".0/test-test-test.ttl";
      final String repositoryUrl = "eclipse-esmf/esmf-sdk";
      final String branch = "main";

      final AspectModelScanner aspectModelScanner = new GitHubScanner( repositoryUrl, branch );

      final RuntimeException exception = assertThrows( RuntimeException.class, () -> {
         aspectModelScanner.find( aspectModelFileName );
      } );

      assertEquals(
            "java.io.IOException: File core/esmf-test-aspect-models/src/main/resources/valid/org.eclipse.esmf.test/1.0.0/test-test-test"
                  + ".ttl can't found in https://api.github.com/repos/eclipse-esmf/esmf-sdk repository.",
            exception.getMessage() );
   }

   @Test
   void testGithubScannerWithInvalidRepositoryName() {
      final String aspectModelFileName = "core/esmf-test-aspect-models/src/main/resources/valid/org.eclipse.esmf.test/1.0"
            + ".0/test-test-test.ttl";
      final String repositoryUrl = "eclipse-esmf/esmf-sdk2";
      final String branch = "main";

      final AspectModelScanner aspectModelScanner = new GitHubScanner( repositoryUrl, branch );

      assertThrows( RuntimeException.class, () -> {
         aspectModelScanner.find( aspectModelFileName );
      } );
   }
}
