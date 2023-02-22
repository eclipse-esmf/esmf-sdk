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
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;

public class StructuredValue2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "structuredvalue2boxmodel.sparql";
   private final String boxSelectorStatement = ":StructuredDateCharacteristic a :Box";
   private final String entriesSelectorStatement = ":StructuredDateCharacteristic :entries *";
   private final int totalNumberOfExpectedEntries = 6;

   private final int indexOfdeconstructionRuleEntry = 4;
   private final String expectedDeconstructionRuleEntryTitle = "deconstructionRule";

   private final int indexOfElementsEntry = 5;
   private final String expectedElementsEntryTitle = "elements";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testBoxEntryIsPresent( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_STRUCTURED_VALUE, metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertThat( queryResult.listStatements( context.selector( boxSelectorStatement ) ).toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAttributesArePresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_STRUCTURED_VALUE, metaModelVersion );
      context.executeAttributeIsPresentTest(
            sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement, totalNumberOfExpectedEntries,
            indexOfdeconstructionRuleEntry, expectedDeconstructionRuleEntryTitle,
            "(\\\\d{4})-(\\\\d{2})-(\\\\d{2})" );

      context.executeAttributeIsPresentTest(
            sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement, totalNumberOfExpectedEntries,
            indexOfElementsEntry, expectedElementsEntryTitle, "year '-' month '-' day" );
   }
}
