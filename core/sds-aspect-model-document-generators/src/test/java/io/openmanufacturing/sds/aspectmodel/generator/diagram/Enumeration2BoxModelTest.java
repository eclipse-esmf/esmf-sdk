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

public class Enumeration2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "enumeration2boxmodel.sparql";
   private final int totalNumberOfExpectedEntries = 6;
   private final int indexOfSeeValueEntry = 5;
   private final String expectedSeeEntryTitle = "see";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testOnlyUsedEnumerationsAreProcessedExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_USED_AND_UNUSED_ENUMERATION,
            metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertThat(
            queryResult.listStatements( context.selector( ":UsedTestEnumerationCharacteristic a :Box" ) ).toList() )
            .hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( ":UnusedTestEnumerationCharacteristic a :Box" ) )
            .toList() )
            .hasSize( 0 );
      assertThat( queryResult.listStatements( context.selector( "* :text *" ) ).toList() ).hasSize( 5 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final String characteristicIdentifier = "TestEnumeration";
      final String boxSelectorStatement = getBoxSelectorStatement( characteristicIdentifier );
      final String entriesSelectorStatement = getEntriesSelectorStatement( characteristicIdentifier );
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENUMERATION_WITH_SEE_ATTRIBUTE,
            metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle, "http://example.com/omp" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributesArePresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final String characteristicIdentifier = metaModelVersion.isNewerThan( KnownVersion.BAMM_1_0_0 ) ? "Enumeration365b407" : "TestEnumeration";
      final String boxSelectorStatement = getBoxSelectorStatement( characteristicIdentifier );
      final String entriesSelectorStatement = getEntriesSelectorStatement( characteristicIdentifier );
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENUMERATION_WITH_MULTIPLE_SEE_ATTRIBUTES,
            metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle,
            "http://example.com/me, http://example.com/omp" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsNotPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final String characteristicIdentifier = "TestEnumeration";
      final String boxSelectorStatement = getBoxSelectorStatement( characteristicIdentifier );
      final String entriesSelectorStatement = getEntriesSelectorStatement( characteristicIdentifier );
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENUMERATION_WITHOUT_SEE_ATTRIBUTE,
            metaModelVersion );
      context.executeAttributeIsNotPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testOnlyRightSeeAttributeIsSelected( final KnownVersion metaModelVersion ) {
      // See attribute was rendered also on elements on which it was not declared:
      // https://github.com/OpenManufacturingPlatform/sds-sdk/issues/196
      String characteristicIdentifier = "Enum1";
      String boxSelectorStatement = getBoxSelectorStatement( characteristicIdentifier );
      String entriesSelectorStatement = getEntriesSelectorStatement( characteristicIdentifier );
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENUM_ONLY_ONE_SEE, metaModelVersion );
      context.executeAttributeIsNotPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry );

      characteristicIdentifier = "Enum2";
      boxSelectorStatement = getBoxSelectorStatement( characteristicIdentifier );
      entriesSelectorStatement = getEntriesSelectorStatement( characteristicIdentifier );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle, "https://test.com" );
   }

   private String getBoxSelectorStatement( final String characteristicIdentifier ) {
      return String.format( ":%sCharacteristic a :Box", characteristicIdentifier );
   }

   private String getEntriesSelectorStatement( final String characteristicIdentifier ) {
      return String.format( ":%sCharacteristic :entries *", characteristicIdentifier );
   }

}