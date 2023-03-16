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
      final String boxSelectorStatement = getBoxSelectorStatement( characteristicIdentifier, false );
      final String entriesSelectorStatement = getEntriesSelectorStatement( characteristicIdentifier, false );
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENUMERATION_WITH_SEE_ATTRIBUTE,
            metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle, "http://example.com/" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributesArePresentExpectSuccess( final KnownVersion metaModelVersion ) {
      boolean newerThanSamm1 = metaModelVersion.isNewerThan( KnownVersion.SAMM_1_0_0 );
      final String characteristicIdentifier = newerThanSamm1 ? "Enumeration14c1cbd" : "TestEnumeration";
      final String boxSelectorStatement = getBoxSelectorStatement( characteristicIdentifier, newerThanSamm1 );
      final String entriesSelectorStatement = getEntriesSelectorStatement( characteristicIdentifier, newerThanSamm1 );
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENUMERATION_WITH_MULTIPLE_SEE_ATTRIBUTES,
            metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle,
            "http://example.com/, http://example.com/me" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsNotPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final String characteristicIdentifier = "TestEnumeration";
      final String boxSelectorStatement = getBoxSelectorStatement( characteristicIdentifier, false );
      final String entriesSelectorStatement = getEntriesSelectorStatement( characteristicIdentifier, false );
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENUMERATION_WITHOUT_SEE_ATTRIBUTE,
            metaModelVersion );
      context.executeAttributeIsNotPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testOnlyRightSeeAttributeIsSelected( final KnownVersion metaModelVersion ) {
      // See attribute was rendered also on elements on which it was not declared:
      // https://github.com/eclipse-esmf/esmf-sdk/issues/196
      String characteristicIdentifier = "Enum1";
      String boxSelectorStatement = getBoxSelectorStatement( characteristicIdentifier, false );
      String entriesSelectorStatement = getEntriesSelectorStatement( characteristicIdentifier, false );
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENUM_ONLY_ONE_SEE, metaModelVersion );
      context.executeAttributeIsNotPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry );

      characteristicIdentifier = "Enum2";
      boxSelectorStatement = getBoxSelectorStatement( characteristicIdentifier, false );
      entriesSelectorStatement = getEntriesSelectorStatement( characteristicIdentifier, false );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle, "https://test.com" );
   }

   private String getBoxSelectorStatement( final String characteristicIdentifier, boolean isAnonymous ) {
      return String.format( "%s:%sCharacteristic a :Box",isAnonymous?"":"test", characteristicIdentifier );
   }

   private String getEntriesSelectorStatement( final String characteristicIdentifier, boolean isAnonymous ) {
      return String.format( "%s:%sCharacteristic :entries *",isAnonymous?"":"test", characteristicIdentifier );
   }
}
