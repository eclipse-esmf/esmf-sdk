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
import org.eclipse.esmf.test.TestAspect;

class Constraint2BoxModelTest extends AbstractConstraint2BoxModelTest {
   private final static String EXPECTED_VALUE_ENTRY_TITLE = "value";
   private final static String EXPECTED_SEE_ENTRY_TITLE = "see";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testOnlyUsedConstraintsAreProcessedExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_USED_AND_UNUSED_CONSTRAINT, metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertThat( queryResult.listStatements( context.selector( "* a :Box" ) ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( "* :text *" ) ).toList() ).hasSize( 5 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testSeeAttributesArePresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final String constraintIdentifier = "*";
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_CONSTRAINT_WITH_MULTIPLE_SEE_ATTRIBUTES, metaModelVersion );
      context.executeAttributeIsPresentTest(
            sparqlQueryFileName, boxSelectorStatement( constraintIdentifier, true ),
            entriesSelectorStatement( constraintIdentifier, true ),
            totalNumberOfExpectedEntriesPerMetaModelVersion.get( metaModelVersion ),
            5,
            EXPECTED_SEE_ENTRY_TITLE,
            "http://example.com/, http://example.com/me" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testSeeAttributeIsNotPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final String constraintIdentifier = "*";
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_CONSTRAINT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      context.executeAttributeIsNotPresentTest( sparqlQueryFileName, boxSelectorStatement( constraintIdentifier, true ),
            entriesSelectorStatement( constraintIdentifier, true ),
            totalNumberOfExpectedEntriesPerMetaModelVersion.get( metaModelVersion ), 5 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testRegularExpressionConstraintExpectSuccess( final KnownVersion metaModelVersion ) {
      final String constraintIdentifier = "*";
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_NUMERIC_REGULAR_EXPRESSION_CONSTRAINT, metaModelVersion );
      context.executeAttributeIsPresentTest(
            sparqlQueryFileName, boxSelectorStatement( constraintIdentifier, true ),
            entriesSelectorStatement( constraintIdentifier, true ),
            totalNumberOfExpectedEntriesPerMetaModelVersion.get( metaModelVersion ),
            4,
            EXPECTED_VALUE_ENTRY_TITLE, "\\\\d*\\|x" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testFixedPointConstraintExpectSuccess( final KnownVersion metaModelVersion ) {
      final String constraintIdentifier = "*";
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_FIXED_POINT_CONSTRAINT, metaModelVersion );
      context.executeAttributeIsPresentTest(
            sparqlQueryFileName, boxSelectorStatement( constraintIdentifier, true ),
            entriesSelectorStatement( constraintIdentifier, true ),
            totalNumberOfExpectedEntriesPerMetaModelVersion.get( metaModelVersion ),
            8, "scale", "5" );
      context.executeAttributeIsPresentTest(
            sparqlQueryFileName, boxSelectorStatement( constraintIdentifier, true ),
            entriesSelectorStatement( constraintIdentifier, true ),
            totalNumberOfExpectedEntriesPerMetaModelVersion.get( metaModelVersion ),
            9, "integer", "3" );
   }
}
