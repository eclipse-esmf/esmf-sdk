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
      final Query query = QueryFactory.create( context.getInputStreamAsString( sparqlQueryFileName ) );

      final Model queryResult = ModelFactory.createDefaultModel();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, context.model() ) ) {
         qexec.execConstruct( queryResult );
      }

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
