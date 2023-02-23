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
import org.eclipse.esmf.test.TestAspect;

public class RangeConstraint2BoxModelTest extends AbstractConstraint2BoxModelTest {

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testBoundDefinitionAttributeIsPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final String constraintIdentifier = "RangeConstraintecd22a3";
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITH_BOUND_DEFINITION_ATTRIBUTES, metaModelVersion );
      context.executeAttributeIsPresentTest(
            sparqlQueryFileName, boxSelectorStatement( metaModelVersion, constraintIdentifier ),
            entriesSelectorStatement( metaModelVersion, constraintIdentifier ),
            totalNumberOfExpectedEntriesPerMetaModelVersion.get( metaModelVersion ),
            6, "lowerBoundDefinition", "GREATER_THAN" );

      context.executeAttributeIsPresentTest(
            sparqlQueryFileName, boxSelectorStatement( metaModelVersion, constraintIdentifier ),
            entriesSelectorStatement( metaModelVersion, constraintIdentifier ),
            totalNumberOfExpectedEntriesPerMetaModelVersion.get( metaModelVersion ),
            7, "upperBoundDefinition", "LESS_THAN" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRangeConstraintWithOnlyLowerBoundDefinitionAndBothValuesExpectSuccess(
         final KnownVersion metaModelVersion ) {
      final String constraintIdentifier = "RangeConstrainta825f71";
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_LOWER_BOUND_DEFINITION_AND_BOTH_VALUES, metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement( metaModelVersion, constraintIdentifier ),
            entriesSelectorStatement( metaModelVersion, constraintIdentifier ), totalNumberOfExpectedEntriesPerMetaModelVersion.get( metaModelVersion ),
            6, "lowerBoundDefinition", "GREATER_THAN" );

      context.executeAttributeIsPresentTest(
            sparqlQueryFileName, boxSelectorStatement( metaModelVersion, constraintIdentifier ),
            entriesSelectorStatement( metaModelVersion, constraintIdentifier ), totalNumberOfExpectedEntriesPerMetaModelVersion.get( metaModelVersion ),
            7, "upperBoundDefinition", "AT_MOST" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRangeConstraintWithOnlyMinValue( final KnownVersion metaModelVersion ) {
      final String constraintIdentifier = "RangeConstraintdf1bc0b";
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_MIN_VALUE, metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement( metaModelVersion, constraintIdentifier ),
            entriesSelectorStatement( metaModelVersion, constraintIdentifier ), totalNumberOfExpectedEntriesPerMetaModelVersion.get( metaModelVersion ),
            6, "lowerBoundDefinition", "GREATER_THAN" );

      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement( metaModelVersion, constraintIdentifier ),
            entriesSelectorStatement( metaModelVersion, constraintIdentifier ), totalNumberOfExpectedEntriesPerMetaModelVersion.get( metaModelVersion ),
            7, "upperBoundDefinition", "OPEN" );

      context.executeAttributeIsNotPresentTest(
            sparqlQueryFileName, boxSelectorStatement( metaModelVersion, constraintIdentifier ),
            entriesSelectorStatement( metaModelVersion, constraintIdentifier ), totalNumberOfExpectedEntriesPerMetaModelVersion.get( metaModelVersion ),
            8 );
   }
}
