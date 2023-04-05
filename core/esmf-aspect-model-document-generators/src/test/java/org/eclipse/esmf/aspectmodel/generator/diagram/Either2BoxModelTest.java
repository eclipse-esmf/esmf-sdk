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

public class Either2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "either2boxmodel.sparql";
   private final String boxSelectorStatement = "test:TestEitherCharacteristic a :Box";
   private final String entriesSelectorStatement = "test:TestEitherCharacteristic :entries *";
   private final int totalNumberOfExpectedEntries = 3;
   private final int indexOfSeeValueEntry = 2;
   private final String expectedSeeEntryTitle = "see";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testOnlyUsedCharacteristicsAreProcessedExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_USED_AND_UNUSED_EITHER, metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertThat( queryResult.listStatements( context.selector( "test:UsedTestEitherCharacteristic a :Box" ) ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( "test:UnusedTestEitherCharacteristic a :Box" ) ).toList() )
            .hasSize( 0 );
      assertThat( queryResult.listStatements( context.selector( "* :text *" ) ).toList() )
            .hasSize( totalNumberOfExpectedEntries );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_EITHER_WITH_SEE_ATTRIBUTE, metaModelVersion );
      context.executeAttributeIsPresentTest(
            sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement, totalNumberOfExpectedEntries,
            indexOfSeeValueEntry, expectedSeeEntryTitle, "http://example.com/" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributesArePresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_EITHER_WITH_MULTIPLE_SEE_ATTRIBUTES,
            metaModelVersion );
      context.executeAttributeIsPresentTest(
            sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement, totalNumberOfExpectedEntries,
            indexOfSeeValueEntry, expectedSeeEntryTitle, "http://example.com/, http://example.com/me" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsNotPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_EITHER_WITHOUT_SEE_ATTRIBUTE,
            metaModelVersion );
      context.executeAttributeIsNotPresentTest(
            sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement, totalNumberOfExpectedEntries,
            indexOfSeeValueEntry );
   }
}
