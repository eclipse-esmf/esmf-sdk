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
import io.openmanufacturing.sds.test.TestProperty;

public class Property2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "property2boxmodel.sparql";
   private final int totalNumberOfExpectedEntries = 4;
   private final int indexOfSeeValueEntry = 2;
   private final String expectedSeeEntryTitle = "see";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestProperty.PROPERTY_WITH_SEE_ATTRIBUTE, metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, ":propertyWithSeeAttributeProperty a :Box",
            ":propertyWithSeeAttributeProperty :entries *",
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle, "http://example.com/omp" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributesArePresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestProperty.PROPERTY_WITH_MULTIPLE_SEE_ATTRIBUTES, metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, ":propertyWithMultipleSeeAttributesProperty a :Box",
            ":propertyWithMultipleSeeAttributesProperty :entries *",
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle,
            "http://example.com/me, http://example.com/omp" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsNotPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestProperty.PROPERTY_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      context.executeAttributeIsNotPresentTest( sparqlQueryFileName, ":propertyWithoutSeeAttributeProperty a :Box",
            ":propertyWithoutSeeAttributeProperty :entries *",
            totalNumberOfExpectedEntries, indexOfSeeValueEntry );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsPresentOnSharedPropertyExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestProperty.SHARED_PROPERTY_WITH_SEE_ATTRIBUTE, metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertThat(
            queryResult.listStatements( context.selector( ":sharedPropertyWithSeeAttributeProperty a :Box" ) )
                  .toList() )
            .hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( "bamm-e:sharedPropertyProperty a :Box" ) ).toList() )
            .hasSize( 0 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testExampleValueIsPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestProperty.PROPERTY_WITH_EXAMPLE_VALUE, metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, ":propertyWithExampleValueProperty a :Box",
            ":propertyWithExampleValueProperty :entries *",
            totalNumberOfExpectedEntries, 3, "exampleValue", "foo" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testExampleValueIsNotPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestProperty.PROPERTY_WITHOUT_EXAMPLE_VALUE, metaModelVersion );
      context.executeAttributeIsNotPresentTest( sparqlQueryFileName, ":propertyWithoutExampleValueProperty a :Box",
            ":propertyWithoutExampleValueProperty :entries *",
            totalNumberOfExpectedEntries, 3 );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testAspectWithAbstractEntityExpectSuccess( final KnownVersion metaModelVersion ) {
      testAbstractAndExtendingEntityPropertyBoxes( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY, metaModelVersion );
      testAbstractAndExtendingEntityPropertyEdges( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY, metaModelVersion );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testAspectWithAbstractSingleEntityExpectSuccess( final KnownVersion metaModelVersion ) {
      testAbstractAndExtendingEntityPropertyBoxes( TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY, metaModelVersion );
      testAbstractAndExtendingEntityPropertyEdges( TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY, metaModelVersion );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testCollectionWithAbstractEntityExpectSuccess( final KnownVersion metaModelVersion ) {
      testAbstractAndExtendingEntityPropertyBoxes( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY, metaModelVersion );
      testAbstractAndExtendingEntityPropertyEdges( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY, metaModelVersion );
   }

   private void testAbstractAndExtendingEntityPropertyBoxes( final TestAspect testAspect, final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( testAspect, metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertThat( queryResult.listStatements( context.selector( ":entityPropertyProperty a :Box" ) ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( ":abstractTestPropertyProperty a :Box" ) ).toList() ).hasSize( 1 );
   }

   private void testAbstractAndExtendingEntityPropertyEdges( final TestAspect testAspect, final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( testAspect, metaModelVersion );

      final Model queryResult = context.executeQuery( "aspect-property-edges2boxmodel.sparql" );

      assertThat( queryResult.listStatements(
            context.selector( ":ExtendingTestEntityEntity_To_entityPropertyProperty a :Edge" )
      ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
            context.selector( ":ExtendingTestEntityEntity_To_entityPropertyProperty :from :ExtendingTestEntityEntity" )
      ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
            context.selector( ":ExtendingTestEntityEntity_To_entityPropertyProperty :to :entityPropertyProperty" )
      ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
            context.selector( ":ExtendingTestEntityEntity_To_abstractTestPropertyProperty a :Edge" )
      ).toList() ).hasSize( 0 );
      assertThat( queryResult.listStatements(
            context.selector( ":AbstractTestEntityAbstractEntity_To_entityPropertyProperty a :Edge" )
      ).toList() ).hasSize( 0 );

      assertThat( queryResult.listStatements(
            context.selector( ":AbstractTestEntityAbstractEntity_To_abstractTestPropertyProperty a :Edge" )
      ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
            context.selector( ":AbstractTestEntityAbstractEntity_To_abstractTestPropertyProperty :from :AbstractTestEntityAbstractEntity" )
      ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
            context.selector( ":AbstractTestEntityAbstractEntity_To_abstractTestPropertyProperty :to :abstractTestPropertyProperty" )
      ).toList() ).hasSize( 1 );
   }
}
