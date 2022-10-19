/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.generator.diagram;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;

public class PropertyCharacteristicEdges2BoxModelTest extends MetaModelVersions {

   private final String sparqlQueryFileName = "property-characteristic-edges2boxmodel.sparql";

   @ParameterizedTest
   @MethodSource( value = "versionsUpToIncluding1_0_0" )
   void testPropertyToCharacteristicEdgeFromSharedNamespaceNotPresent( final KnownVersion metaModelVersion ) {
      // v1.0 sparql queries were not properly filtering out some properties from shared bamm-e namespace
      // https://github.com/OpenManufacturingPlatform/sds-sdk/issues/196
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENUM_ONLY_ONE_SEE, metaModelVersion );
      final Query query = QueryFactory.create( context.getInputStreamAsString( sparqlQueryFileName ) );

      final Model queryResult = ModelFactory.createDefaultModel();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, context.model() ) ) {
         qexec.execConstruct( queryResult );
      }

      assertThat( queryResult.listStatements( context.selector( ":timestampProperty_To_TimestampCharacteristic a :Edge" ) ).toList() ).isEmpty();
   }
}
