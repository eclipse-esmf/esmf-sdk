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
import io.openmanufacturing.sds.test.TestProperty;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

public class Property2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "property2boxmodel.sparql";
   private final int totalNumberOfExpectedEntries = 4;
   private final int indexOfSeeValueEntry = 2;
   private final String expectedSeeEntryTitle = "see";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestProperty.PROPERTY_WITH_SEE_ATTRIBUTE, metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, ":propertyWithSeeAttributeProperty a :Box",
            ":propertyWithSeeAttributeProperty :entries *",
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle, "http://example.com/omp" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributesArePresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestProperty.PROPERTY_WITH_MULTIPLE_SEE_ATTRIBUTES,
            metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, ":propertyWithMultipleSeeAttributesProperty a :Box",
            ":propertyWithMultipleSeeAttributesProperty :entries *",
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle,
            "http://example.com/me, http://example.com/omp" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsNotPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestProperty.PROPERTY_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      context.executeAttributeIsNotPresentTest( sparqlQueryFileName, ":propertyWithoutSeeAttributeProperty a :Box",
            ":propertyWithoutSeeAttributeProperty :entries *",
            totalNumberOfExpectedEntries, indexOfSeeValueEntry );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsPresentOnSharedPropertyExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestProperty.SHARED_PROPERTY_WITH_SEE_ATTRIBUTE, metaModelVersion );

      final Query query = QueryFactory.create( context.getInputStreamAsString( sparqlQueryFileName ) );

      final Model queryResult = ModelFactory.createDefaultModel();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, context.model() ) ) {
         qexec.execConstruct( queryResult );
      }

      assertThat(
            queryResult.listStatements( context.selector( ":sharedPropertyWithSeeAttributeProperty a :Box" ) )
                       .toList() )
            .hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( "bamm-e:sharedPropertyProperty a :Box" ) ).toList() )
            .hasSize( 0 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testExampleValueIsPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestProperty.PROPERTY_WITH_EXAMPLE_VALUE, metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, ":propertyWithExampleValueProperty a :Box",
            ":propertyWithExampleValueProperty :entries *",
            totalNumberOfExpectedEntries, 3, "exampleValue", "foo" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testExampleValueIsNotPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestProperty.PROPERTY_WITHOUT_EXAMPLE_VALUE, metaModelVersion );
      context.executeAttributeIsNotPresentTest( sparqlQueryFileName, ":propertyWithoutExampleValueProperty a :Box",
            ":propertyWithoutExampleValueProperty :entries *",
            totalNumberOfExpectedEntries, 3 );
   }
}
