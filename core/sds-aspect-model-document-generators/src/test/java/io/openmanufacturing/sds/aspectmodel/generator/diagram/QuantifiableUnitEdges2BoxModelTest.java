/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;

public class QuantifiableUnitEdges2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "quantifiable-unit-edges2boxmodel.sparql";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testQuantifiableHasNoUnitExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_QUANTIFIABLE_WITHOUT_UNIT, metaModelVersion );
      final Model queryResult = context.executeQuery( sparqlQueryFileName );
      assertQuantifiableUnitEdgeModel( context, queryResult, 0 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testQuantifiableHasUnitExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_QUANTIFIABLE_WITH_UNIT, metaModelVersion );
      final Model queryResult = context.executeQuery( sparqlQueryFileName );
      assertQuantifiableUnitEdgeModel( context, queryResult, 1 );
   }

   private void assertQuantifiableUnitEdgeModel( final TestContext context, final Model queryResult,
         final int expectedOccurance ) {
      assertThat( queryResult
            .listStatements( context.selector( ":TestQuantifiableCharacteristic_To_PercentUnit :to :PercentUnit" ) )
            .toList() )
            .hasSize( expectedOccurance );
      assertThat( queryResult.listStatements(
            context.selector(
                  ":TestQuantifiableCharacteristic_To_PercentUnit :from :TestQuantifiableCharacteristic" ) ).toList() )
            .hasSize( expectedOccurance );
      assertThat( queryResult
            .listStatements( context.selector( ":TestQuantifiableCharacteristic_To_PercentUnit :title *" ) ).toList() )
            .hasSize( expectedOccurance );
      assertThat( queryResult
            .listStatements( context.selector( ":TestQuantifiableCharacteristic_To_PercentUnit rdf:type :Edge" ) )
            .toList() )
            .hasSize( expectedOccurance );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testMeasurementHasUnitExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_MEASUREMENT_WITH_UNIT, metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertThat( queryResult
            .listStatements( context.selector( ":TestMeasurementCharacteristic_To_PercentUnit :to :PercentUnit" ) )
            .toList() )
            .hasSize( 1 );
      assertThat( queryResult.listStatements( context
                  .selector( ":TestMeasurementCharacteristic_To_PercentUnit :from :TestMeasurementCharacteristic" ) )
            .toList() )
            .hasSize( 1 );
      assertThat( queryResult
            .listStatements( context.selector( ":TestMeasurementCharacteristic_To_PercentUnit :title *" ) )
            .toList() )
            .hasSize( 1 );
      assertThat( queryResult
            .listStatements( context.selector( ":TestMeasurementCharacteristic_To_PercentUnit rdf:type :Edge" ) )
            .toList() )
            .hasSize( 1 );
   }
}
