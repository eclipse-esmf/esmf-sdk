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

public class Aspect2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "aspect2boxmodel.sparql";
   private final int totalNumberOfExpectedEntries = 3;
   private final int indexOfSeeValueEntry = 2;
   private final String expectedSeeEntryTitle = "see";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_SEE_ATTRIBUTE, metaModelVersion );
      final String boxSelectorStatement = ":AspectWithSeeAttributeAspect a :Box";
      final String entriesSelectorStatement = ":AspectWithSeeAttributeAspect :entries *";
      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle, "http://example.com/omp" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributesArePresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_MULTIPLE_SEE_ATTRIBUTES, metaModelVersion );
      final String boxSelectorStatement = ":AspectWithMultipleSeeAttributesAspect a :Box";
      final String entriesSelectorStatement = ":AspectWithMultipleSeeAttributesAspect :entries *";
      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle,
            "http://example.com/me, http://example.com/omp" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsNotPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final String boxSelectorStatement = ":AspectWithoutSeeAttributeAspect a :Box";
      final String entriesSelectorStatement = ":AspectWithoutSeeAttributeAspect :entries *";
      context.executeAttributeIsNotPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithProperty( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_PROPERTY, metaModelVersion );

      final Model queryResult = context.executeQuery( "aspect-property-edges2boxmodel.sparql" );

      assertThat( queryResult.listStatements( context.selector( ":AspectWithPropertyAspect_To_testPropertyProperty a :Edge" ) ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( ":AspectWithPropertyAspect_To_testPropertyProperty :title property" ) ).toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithOptionalProperty( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTY, metaModelVersion );

      final Model queryResult = context.executeQuery( "aspect-property-edges2boxmodel.sparql" );

      assertThat( queryResult.listStatements( context.selector( ":AspectWithOptionalPropertyAspect_To_testPropertyProperty a :Edge" ) )
            .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( ":AspectWithOptionalPropertyAspect_To_testPropertyProperty :title property (optional)" ) )
            .toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithPropertyWithPayloadName( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME, metaModelVersion );

      final Model queryResult = context.executeQuery( "aspect-property-edges2boxmodel.sparql" );

      assertThat( queryResult.listStatements( context.selector( ":AspectWithPropertyWithPayloadNameAspect_To_testPropertyProperty a :Edge" ) )
            .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( ":AspectWithPropertyWithPayloadNameAspect_To_testPropertyProperty :title property (test)" ) )
            .toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithOptionalPropertyWithPayloadName( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTY_WITH_PAYLOAD_NAME, metaModelVersion );

      final Model queryResult = context.executeQuery( "aspect-property-edges2boxmodel.sparql" );

      assertThat( queryResult.listStatements( context.selector( ":AspectWithOptionalPropertyWithPayloadNameAspect_To_testPropertyProperty a :Edge" ) )
            .toList() ).hasSize( 1 );
      assertThat(
            queryResult.listStatements(
                        context.selector( ":AspectWithOptionalPropertyWithPayloadNameAspect_To_testPropertyProperty :title property (optional) (test)" ) )
                  .toList() ).hasSize( 1 );
   }
}
