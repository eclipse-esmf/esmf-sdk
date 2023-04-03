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

public class Collection2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "collection2boxmodel.sparql";
   private final String boxSelectorStatement = "test:TestCollectionCharacteristic a :Box";
   private final String entriesSelectorStatement = "test:TestCollectionCharacteristic :entries *";
   private final int totalNumberOfExpectedEntries = 6;
   private final int indexOfSeeValueEntry = 5;
   private final String expectedSeeEntryTitle = "see";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testOnlyUsedCollectionsAreProcessedExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_USED_AND_UNUSED_COLLECTION, metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertThat( queryResult.listStatements( context.selector( "test:UsedTestCollectionCharacteristic a :Box" ) ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( "test:UnusedTestCollectionCharacteristic a :Box" ) ).toList() ).hasSize( 0 );
      assertThat( queryResult.listStatements( context.selector( "* :text *" ) ).toList() ).hasSize( totalNumberOfExpectedEntries );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_COLLECTION_WITH_SEE_ATTRIBUTE, metaModelVersion );
      context.executeAttributeIsPresentTest(
            sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement, totalNumberOfExpectedEntries,
            indexOfSeeValueEntry, expectedSeeEntryTitle, "http://example.com/" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributesArePresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_COLLECTION_WITH_MULTIPLE_SEE_ATTRIBUTES, metaModelVersion );
      context.executeAttributeIsPresentTest(
            sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement, totalNumberOfExpectedEntries,
            indexOfSeeValueEntry, expectedSeeEntryTitle, "http://example.com/, http://example.com/me" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsNotPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_COLLECTION_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      context.executeAttributeIsNotPresentTest(
            sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement, totalNumberOfExpectedEntries,
            indexOfSeeValueEntry );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testCollectionWithElementCharacteristicExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_COLLECTIONS_WITH_ELEMENT_CHARACTERISTIC_AND_SIMPLE_DATA_TYPE, metaModelVersion );

      final Model queryResult = context.executeQuery( "collection-elementcharacteristic-edges2boxmodel.sparql" );

      assertThat( queryResult.listStatements( context.selector( "test:TestCollectionTwoCharacteristic_To_TextCharacteristic a :Edge" ) ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( "test:TestCollectionTwoCharacteristic_To_TextCharacteristic :to samm-c:TextCharacteristic" ) )
            .toList() ).hasSize( 1 );
      assertThat(
            queryResult.listStatements( context.selector( "test:TestCollectionTwoCharacteristic_To_TextCharacteristic :from test:TestCollectionTwoCharacteristic" ) )
                  .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( "test:TestCollectionTwoCharacteristic_To_TextCharacteristic :title element Characteristic" ) )
            .toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testCollectionWithAbstractEntityExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY, metaModelVersion );

      final Model queryResult = context.executeQuery( "characteristic-entity-edges2boxmodel.sparql" );

      assertThat( queryResult
            .listStatements( context.selector( "test:EntityCollectionCharacteristicCharacteristic_To_AbstractTestEntityAbstractEntity a :Edge" ) )
            .toList() ).hasSize( 1 );
      assertThat( queryResult
            .listStatements( context.selector(
                  "test:EntityCollectionCharacteristicCharacteristic_To_AbstractTestEntityAbstractEntity :from test:EntityCollectionCharacteristicCharacteristic" ) )
            .toList() ).hasSize( 1 );
      assertThat( queryResult
            .listStatements( context.selector(
                  "test:EntityCollectionCharacteristicCharacteristic_To_AbstractTestEntityAbstractEntity :to test:AbstractTestEntityAbstractEntity" ) )
            .toList() ).hasSize( 1 );
   }
}
