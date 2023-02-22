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
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;

public class EnumerationEntityInstance2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "enumeration-entityinstance2boxmodel.sparql";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEdgeSetForEnumeration( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENUMERATION_WITHOUT_SCALAR_VARIABLE,
            metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertThat( queryResult.listStatements(
            context.selector( ":EvaluationResultsCharacteristic_To_ResultGoodEntityInstance a :Edge" ) ).toList() )
            .hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector(
                  ":EvaluationResultsCharacteristic_To_ResultGoodEntityInstance :to :ResultGoodEntityInstance" ) )
            .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector(
                  ":EvaluationResultsCharacteristic_To_ResultGoodEntityInstance :from :EvaluationResultsCharacteristic" ) )
            .toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testNoEdgeSetForEnumerationWithScalarVariables( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENUMERATION_WITH_SCALAR_VARIABLE,
            metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      // Edge may not be present, as this is a scalar value.
      assertThat( queryResult
            .listStatements( context.selector( ":TestScalarEnumerationCharacteristic_To_EntityInstance a :Edge" ) )
            .toList() ).hasSize( 0 );
   }
}
