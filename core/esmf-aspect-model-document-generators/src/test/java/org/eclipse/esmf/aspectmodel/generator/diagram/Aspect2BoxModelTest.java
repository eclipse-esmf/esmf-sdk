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

public class Aspect2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "aspect2boxmodel.sparql";
   private final int totalNumberOfExpectedEntries = 3;
   private final int indexOfSeeValueEntry = 2;
   private final String expectedSeeEntryTitle = "see";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_SEE_ATTRIBUTE, metaModelVersion );
      final String boxSelectorStatement = "test:AspectWithSeeAttributeAspect a :Box";
      final String entriesSelectorStatement = "test:AspectWithSeeAttributeAspect :entries *";
      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle, "http://example.com/" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributesArePresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_MULTIPLE_SEE_ATTRIBUTES, metaModelVersion );
      final String boxSelectorStatement = "test:AspectWithMultipleSeeAttributesAspect a :Box";
      final String entriesSelectorStatement = "test:AspectWithMultipleSeeAttributesAspect :entries *";
      context.executeAttributeIsPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle,
            "http://example.com/, http://example.com/me" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsNotPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final String boxSelectorStatement = "test:AspectWithoutSeeAttributeAspect a :Box";
      final String entriesSelectorStatement = "test:AspectWithoutSeeAttributeAspect :entries *";
      context.executeAttributeIsNotPresentTest( sparqlQueryFileName, boxSelectorStatement, entriesSelectorStatement,
            totalNumberOfExpectedEntries, indexOfSeeValueEntry );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithProperty( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_PROPERTY, metaModelVersion );

      final Model queryResult = context.executeQuery( "aspect-property-edges2boxmodel.sparql" );

      assertThat( queryResult.listStatements( context.selector( "test:AspectWithPropertyAspect_To_testPropertyProperty a :Edge" ) ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( "test:AspectWithPropertyAspect_To_testPropertyProperty :title property" ) ).toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithOptionalProperty( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTY, metaModelVersion );

      final Model queryResult = context.executeQuery( "aspect-property-edges2boxmodel.sparql" );

      assertThat( queryResult.listStatements( context.selector( "test:AspectWithOptionalPropertyAspect_To_testPropertyProperty a :Edge" ) )
            .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( "test:AspectWithOptionalPropertyAspect_To_testPropertyProperty :title property (optional)" ) )
            .toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithPropertyWithPayloadName( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME, metaModelVersion );

      final Model queryResult = context.executeQuery( "aspect-property-edges2boxmodel.sparql" );

      assertThat( queryResult.listStatements( context.selector( "test:AspectWithPropertyWithPayloadNameAspect_To_testPropertyProperty a :Edge" ) )
            .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( "test:AspectWithPropertyWithPayloadNameAspect_To_testPropertyProperty :title property (test)" ) )
            .toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithOptionalPropertyWithPayloadName( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTY_WITH_PAYLOAD_NAME, metaModelVersion );

      final Model queryResult = context.executeQuery( "aspect-property-edges2boxmodel.sparql" );

      assertThat( queryResult.listStatements( context.selector( "test:AspectWithOptionalPropertyWithPayloadNameAspect_To_testPropertyProperty a :Edge" ) )
            .toList() ).hasSize( 1 );
      assertThat(
            queryResult.listStatements(
                        context.selector( "test:AspectWithOptionalPropertyWithPayloadNameAspect_To_testPropertyProperty :title property (optional) (test)" ) )
                  .toList() ).hasSize( 1 );
   }
}
