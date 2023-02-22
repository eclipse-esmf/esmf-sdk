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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.eclipse.esmf.samm.KnownVersion;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;

public class Operation2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "operation2boxmodel.sparql";
   private final String boxSelectorStatement = ":testOperationOperation a :Box";
   private final String entriesSelectorStatement = ":testOperationOperation :entries *";
   private final int totalNumberOfExpectedEntries = 3;
   private final int indexOfSeeValueEntry = 2;
   private final String expectedSeeEntryTitle = "see";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_OPERATION_WITH_SEE_ATTRIBUTE,
            metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle, "http://example.com/omp" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributesArePresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_OPERATION_WITH_MULTIPLE_SEE_ATTRIBUTES,
            metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle,
            "http://example.com/me, http://example.com/omp" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsNotPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_OPERATION_WITHOUT_SEE_ATTRIBUTE,
            metaModelVersion );
      context.executeAttributeIsNotPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry );
   }
}
