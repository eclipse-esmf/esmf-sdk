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

package examples;

// tag::imports[]
import org.junit.jupiter.api.Test;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
// end::imports[]

public class ParseAspectModelUrn {
   @Test
   public void parseAspectModelUrn() {
      // tag::parseAspectModelUrn[]
      final AspectModelUrn urn = AspectModelUrn.fromUrn( "urn:samm:com.example:1.0.0#Example" );
      final String namespace = urn.getNamespace(); // com.example
      final String name = urn.getName();           // Example
      final String version = urn.getVersion();     // 1.0.0
      final String urnPrefix = urn.getUrnPrefix(); // urn:samm:com.example:1.0.0#
      // end::parseAspectModelUrn[]
   }
}
