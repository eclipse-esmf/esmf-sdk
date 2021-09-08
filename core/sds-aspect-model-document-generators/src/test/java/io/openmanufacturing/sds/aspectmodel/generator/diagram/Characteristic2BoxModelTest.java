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

public class Characteristic2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "characteristic2boxmodel.sparql";
   private final String boxSelectorStatement = ":TestCharacteristicCharacteristic a :Box";
   private final String entriesSelectorStatement = ":TestCharacteristicCharacteristic :entries *";
   private final int totalNumberOfExpectedEntries = 4;
   private final int indexOfSeeValueEntry = 3;
   private final String expectedSeeEntryTitle = "see";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testOnlyUsedCharacteristicsAreProcessedExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_USED_AND_UNUSED_CHARACTERISTIC,
            metaModelVersion );
      final Query query = QueryFactory.create( context.getInputStreamAsString( sparqlQueryFileName ) );

      final Model queryResult = ModelFactory.createDefaultModel();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, context.model() ) ) {
         qexec.execConstruct( queryResult );
      }

      assertThat( queryResult.listStatements( context.selector( ":UsedTestCharacteristicCharacteristic a :Box" ) )
                             .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( ":UnusedTestCharacteristicCharacteristic a :Box" ) )
                             .toList() ).hasSize( 0 );
      assertThat( queryResult.listStatements( context.selector( "* :text *" ) ).toList() )
            .hasSize( totalNumberOfExpectedEntries );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_CHARACTERISTIC_WITH_SEE_ATTRIBUTE,
            metaModelVersion );
      context.executeAttributeIsPresentTest(
            sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement, totalNumberOfExpectedEntries,
            indexOfSeeValueEntry, expectedSeeEntryTitle, "http://example.com/omp" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributesArePresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_CHARACTERISTIC_WITH_MULTIPLE_SEE_ATTRIBUTES,
            metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle,
            "http://example.com/me, http://example.com/omp" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsNotPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_CHARACTERISTIC_WITHOUT_SEE_ATTRIBUTE,
            metaModelVersion );
      context.executeAttributeIsNotPresentTest(
            sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement, totalNumberOfExpectedEntries,
            indexOfSeeValueEntry );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testAspectWithAbstractEntityExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext(
            TestAspect.ASPECT_WITH_ABSTRACT_ENTITY, metaModelVersion );

      final Query query = QueryFactory
            .create( context.getInputStreamAsString( "characteristic-entity-edges2boxmodel.sparql" ) );

      final Model queryResult = ModelFactory.createDefaultModel();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, context.model() ) ) {
         qexec.execConstruct( queryResult );
      }

      assertThat( queryResult
            .listStatements( context.selector( ":EntityCharacteristicCharacteristic_To_ExtendingTestEntityEntity a :Edge" ) )
            .toList() ).hasSize( 1 );
      assertThat( queryResult
            .listStatements( context.selector( ":EntityCharacteristicCharacteristic_To_ExtendingTestEntityEntity :from :EntityCharacteristicCharacteristic" ) )
            .toList() ).hasSize( 1 );
      assertThat( queryResult
            .listStatements( context.selector( ":EntityCharacteristicCharacteristic_To_ExtendingTestEntityEntity :to :ExtendingTestEntityEntity" ) )
            .toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testAspectWithAbstractSingleEntityExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext(
            TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY, metaModelVersion );

      final Query query = QueryFactory
            .create( context.getInputStreamAsString( "characteristic-entity-edges2boxmodel.sparql" ) );

      final Model queryResult = ModelFactory.createDefaultModel();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, context.model() ) ) {
         qexec.execConstruct( queryResult );
      }

      assertThat( queryResult
            .listStatements( context.selector( ":EntityCharacteristicCharacteristic_To_AbstractTestEntityAbstractEntity a :Edge" ) )
            .toList() ).hasSize( 1 );
      assertThat( queryResult
            .listStatements( context.selector( ":EntityCharacteristicCharacteristic_To_AbstractTestEntityAbstractEntity :from :EntityCharacteristicCharacteristic" ) )
            .toList() ).hasSize( 1 );
      assertThat( queryResult
            .listStatements( context.selector( ":EntityCharacteristicCharacteristic_To_AbstractTestEntityAbstractEntity :to :AbstractTestEntityAbstractEntity" ) )
            .toList() ).hasSize( 1 );
   }
}
