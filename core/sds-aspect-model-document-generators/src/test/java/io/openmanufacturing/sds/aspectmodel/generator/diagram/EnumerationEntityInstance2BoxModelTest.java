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

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

public class EnumerationEntityInstance2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "enumeration-entityinstance2boxmodel.sparql";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEdgeSetForEnumeration( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENUMERATION_WITHOUT_SCALAR_VARIABLE,
            metaModelVersion );

      final Query query = QueryFactory.create( context.getInputStreamAsString( sparqlQueryFileName ) );

      final Model queryResult = ModelFactory.createDefaultModel();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, context.model() ) ) {
         qexec.execConstruct( queryResult );
      }

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

      final Query query = QueryFactory.create( context.getInputStreamAsString( sparqlQueryFileName ) );

      final Model queryResult = ModelFactory.createDefaultModel();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, context.model() ) ) {
         qexec.execConstruct( queryResult );
      }

      // Edge may not be present, as this is a scalar value.
      assertThat( queryResult
            .listStatements( context.selector( ":TestScalarEnumerationCharacteristic_To_EntityInstance a :Edge" ) )
            .toList() ).hasSize( 0 );
   }
}
