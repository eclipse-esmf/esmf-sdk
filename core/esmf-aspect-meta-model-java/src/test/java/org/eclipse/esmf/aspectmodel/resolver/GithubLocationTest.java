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

package org.eclipse.esmf.aspectmodel.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class GithubLocationTest {
   @ParameterizedTest
   @MethodSource
   void testParseUrl( final String url, final GitHubFileLocation parseResult ) {
      assertThat( GitHubFileLocation.parse( url ) ).contains( parseResult );
   }

   static Stream<Arguments> testParseUrl() {
      final GithubRepository esmfSdkOnMain = new GithubRepository( "eclipse-esmf", "esmf-sdk", new GithubRepository.Branch( "main" ) );
      final GitHubFileLocation aspectWithEntityOnMain = new GitHubFileLocation( esmfSdkOnMain,
            "core/esmf-test-aspect-models/src/main/resources/valid",
            "org.eclipse.esmf.test", "1.0.0", "AspectWithEntity.ttl" );

      final GithubRepository esmfSdkOnTag = new GithubRepository( "eclipse-esmf", "esmf-sdk", new GithubRepository.Tag( "v2.9.0" ) );
      final GitHubFileLocation aspectWithEntityOnTag = new GitHubFileLocation( esmfSdkOnTag,
            "core/esmf-test-aspect-models/src/main/resources/valid",
            "org.eclipse.esmf.test", "1.0.0", "AspectWithEntity.ttl" );

      final GithubRepository cxOnMain = new GithubRepository(
            "eclipse-tractusx", "sldt-semantic-models", new GithubRepository.Branch( "main" ) );
      final GitHubFileLocation cxBatteryPassOnMain = new GitHubFileLocation( cxOnMain, "", "io.catenax.battery.battery_pass", "6.0.0",
            "BatteryPass.ttl" );

      final GithubRepository cxOnTag = new GithubRepository(
            "eclipse-tractusx", "sldt-semantic-models", new GithubRepository.Tag( "v24.05" ) );
      final GitHubFileLocation cxBatteryPassOnTag = new GitHubFileLocation( cxOnTag, "", "io.catenax.battery.battery_pass", "6.0.0",
            "BatteryPass.ttl" );

      return Stream.of(
            // Regular file on branch with directory
            Arguments.arguments(
                  "https://github.com/eclipse-esmf/esmf-sdk/blob/main/core/esmf-test-aspect-models/src/main/resources/valid/"
                        + "org.eclipse.esmf.test/1.0.0/AspectWithEntity.ttl", aspectWithEntityOnMain
            ),
            // Regular file on tag with directory
            Arguments.arguments(
                  "https://github.com/eclipse-esmf/esmf-sdk/blob/v2.9.0/core/esmf-test-aspect-models/src/main/resources/valid/org.eclipse"
                        + ".esmf.test/1.0.0/AspectWithEntity.ttl", aspectWithEntityOnTag
            ),
            // Raw file reference on branch with directory
            Arguments.arguments(
                  "https://github.com/eclipse-esmf/esmf-sdk/raw/refs/heads/main/core/esmf-test-aspect-models/src/main/resources/valid/org"
                        + ".eclipse.esmf.test/1.0.0/AspectWithEntity.ttl", aspectWithEntityOnMain
            ),
            // Raw file reference on tag with directory
            Arguments.arguments(
                  "https://github.com/eclipse-esmf/esmf-sdk/raw/refs/tags/v2.9"
                        + ".0/core/esmf-test-aspect-models/src/main/resources/valid/org.eclipse.esmf.test/1.0.0/AspectWithEntity.ttl",
                  aspectWithEntityOnTag
            ),

            // Regular file on branch without directory
            Arguments.arguments(
                  "https://github.com/eclipse-tractusx/sldt-semantic-models/blob/main/io.catenax.battery.battery_pass/6.0.0/BatteryPass"
                        + ".ttl", cxBatteryPassOnMain
            ),
            // Regular file on tag without directory
            Arguments.arguments(
                  "https://github.com/eclipse-tractusx/sldt-semantic-models/blob/v24.05/io.catenax.battery.battery_pass/6.0.0/BatteryPass"
                        + ".ttl", cxBatteryPassOnTag
            ),
            // Raw file reference on branch without directory
            Arguments.arguments(
                  "https://github.com/eclipse-tractusx/sldt-semantic-models/raw/refs/heads/main/io.catenax.battery.battery_pass/6.0"
                        + ".0/BatteryPass.ttl", cxBatteryPassOnMain
            ),
            // Raw file reference on tag without directory
            Arguments.arguments(
                  "https://github.com/eclipse-tractusx/sldt-semantic-models/raw/refs/tags/v24.05/io.catenax.battery.battery_pass/6.0"
                        + ".0/BatteryPass.ttl", cxBatteryPassOnTag
            )
      );
   }
}
