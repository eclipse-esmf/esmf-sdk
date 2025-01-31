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

package org.eclipse.esmf.buildtime;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.jena.riot.RIOT;

/*
 * Main class that is executed during build time and that runs build-time code generators.
 * args[0]: Path to src-gen directory
 * args[1]: Path to src-buildtime directory
 */
public class GenerateBuildtimeCode {
   public static void main( final String[] args ) {
      RIOT.init();
      final Path srcGenPath = Path.of( args[0] );
      final Path srcBuildtimePath = Path.of( args[1] );

      Stream.of(
            new GenerateUnits( srcBuildtimePath, srcGenPath ),
            new GenerateQuantityKinds( srcBuildtimePath, srcGenPath )
      ).forEach( BuildtimeCodeGenerator::writeGeneratedFile );
   }
}
