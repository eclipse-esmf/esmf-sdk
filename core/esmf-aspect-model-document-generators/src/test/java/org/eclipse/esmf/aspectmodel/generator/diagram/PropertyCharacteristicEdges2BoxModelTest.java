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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;

public class PropertyCharacteristicEdges2BoxModelTest extends MetaModelVersions {

   private final String sparqlQueryFileName = "property-characteristic-edges2boxmodel.sparql";

   @ParameterizedTest
   @MethodSource( value = "versionsUpToIncluding1_0_0" )
   void testPropertyToCharacteristicEdgeFromSharedNamespaceNotPresent( final KnownVersion metaModelVersion ) {
      // v1.0 sparql queries were not properly filtering out some properties from shared bamm-e namespace
      // https://github.com/OpenManufacturingPlatform/sds-sdk/issues/196
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENUM_ONLY_ONE_SEE, metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertThat( queryResult.listStatements( context.selector( ":timestampProperty_To_TimestampCharacteristic a :Edge" ) ).toList() ).isEmpty();
   }
}
