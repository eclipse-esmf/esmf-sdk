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
import org.eclipse.esmf.test.TestEntity;

class Entity2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "entity2boxmodel.sparql";
   private final int totalNumberOfExpectedEntries = 3;
   private final int indexOfSeeValueEntry = 2;
   private final String expectedSeeEntryTitle = "see";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testSeeAttributeIsPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.ENTITY_WITH_SEE_ATTRIBUTE, metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, "test:EntityWithSeeAttributeEntity a :Box",
            "test:EntityWithSeeAttributeEntity :entries *",
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle, "http://example.com/" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testSeeAttributesArePresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.ENTITY_WITH_MULTIPLE_SEE_ATTRIBUTES, metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, "test:EntityWithMultipleSeeAttributesEntity a :Box",
            "test:EntityWithMultipleSeeAttributesEntity :entries *",
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle,
            "http://example.com/, http://example.com/me" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testSeeAttributeIsNotPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.ENTITY_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      context.executeAttributeIsNotPresentTest( sparqlQueryFileName, "test:EntityWithoutSeeAttributeEntity a :Box",
            "test:EntityWithoutSeeAttributeEntity :entries *",
            totalNumberOfExpectedEntries, indexOfSeeValueEntry );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testSeeAttributeIsPresentOnSharedEntityExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.SHARED_ENTITY_WITH_SEE_ATTRIBUTE, metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertThat( queryResult.listStatements( context.selector( "test:SharedEntityWithSeeAttributeEntity a :Box" ) ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( "samm-e:SharedEntityEntity a :Box" ) ).toList() )
            .hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testEntityWithOptionalAndNotInPayloadProperty( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.ENTITY_WITH_OPTIONAL_AND_NOT_IN_PAYLOAD_PROPERTY, metaModelVersion );

      final Model queryResult = context.executeQuery( "aspect-property-edges2boxmodel.sparql" );

      assertThat(
            queryResult.listStatements( context.selector(
                        "test:EntityWithOptionalAndNotInPayloadPropertyEntity_To_testPropertyOneProperty a :Edge" ) )
                  .toList() ).hasSize( 1 );
      assertThat( queryResult
            .listStatements( context.selector(
                  "test:EntityWithOptionalAndNotInPayloadPropertyEntity_To_testPropertyOneProperty :title property" ) )
            .toList() ).hasSize( 1 );

      assertThat(
            queryResult.listStatements( context.selector(
                        "test:EntityWithOptionalAndNotInPayloadPropertyEntity_To_testPropertyTwoProperty a :Edge" ) )
                  .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
                  context.selector(
                        "test:EntityWithOptionalAndNotInPayloadPropertyEntity_To_testPropertyTwoProperty :title property (optional)" ) )
            .toList() ).hasSize( 1 );

      assertThat( queryResult
            .listStatements( context.selector(
                  "test:EntityWithOptionalAndNotInPayloadPropertyEntity_To_testPropertyThreeProperty a :Edge" ) )
            .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
                  context.selector(
                        "test:EntityWithOptionalAndNotInPayloadPropertyEntity_To_testPropertyThreeProperty :title property (not in payload)" ) )
            .toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testEntityWithOptionalProperty( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.ENTITY_WITH_OPTIONAL_PROPERTY, metaModelVersion );

      final Model queryResult = context.executeQuery( "aspect-property-edges2boxmodel.sparql" );

      assertThat(
            queryResult.listStatements( context.selector(
                        "test:EntityWithOptionalPropertyEntity_To_testPropertyProperty a :Edge" ) )
                  .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
                  context.selector(
                        "test:EntityWithOptionalPropertyEntity_To_testPropertyProperty :title property (optional)" ) )
            .toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testEntityWithOptionalPropertyWithPayloadName( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.ENTITY_WITH_OPTIONAL_PROPERTY_WITH_PAYLOAD_NAME, metaModelVersion );

      final Model queryResult = context.executeQuery( "aspect-property-edges2boxmodel.sparql" );

      assertThat(
            queryResult.listStatements( context.selector(
                        "test:EntityWithOptionalPropertyWithPayloadNameEntity_To_testPropertyProperty a :Edge" ) )
                  .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
                  context.selector(
                        "test:EntityWithOptionalPropertyWithPayloadNameEntity_To_testPropertyProperty :title property (optional) (test)" ) )
            .toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testEntityWithPropertyWithPayloadName( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.ENTITY_WITH_PROPERTY_WITH_PAYLOAD_NAME, metaModelVersion );

      final Model queryResult = context.executeQuery( "aspect-property-edges2boxmodel.sparql" );

      assertThat(
            queryResult.listStatements( context.selector(
                        "test:EntityWithPropertyWithPayloadNameEntity_To_testPropertyProperty a :Edge" ) )
                  .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
                  context.selector(
                        "test:EntityWithPropertyWithPayloadNameEntity_To_testPropertyProperty :title property (test)" ) )
            .toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   void testAspectWithAbstractEntityExpectSuccess( final KnownVersion metaModelVersion ) {
      testExtendingEntity( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY, metaModelVersion );
      testExtendingEntityEdges( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY, metaModelVersion );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   void testAspectWithAbstractSingleEntityExpectSuccess( final KnownVersion metaModelVersion ) {
      testExtendingEntity( TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY, metaModelVersion );
      testExtendingEntityEdges( TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY, metaModelVersion );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   void testCollectionWithAbstractEntityExpectSuccess( final KnownVersion metaModelVersion ) {
      testExtendingEntity( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY, metaModelVersion );
      testExtendingEntityEdges( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY, metaModelVersion );
   }

   private void testExtendingEntity( final TestAspect testAspect, final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( testAspect, metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertThat( queryResult.listStatements( context.selector( "test:ExtendingTestEntityEntity a :Box" ) ).toList() ).hasSize( 1 );
   }

   private void testExtendingEntityEdges( final TestAspect testAspect, final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( testAspect, metaModelVersion );

      final Model queryResult = context.executeQuery( "entity-abstractentity-edges2boxmodel.sparql" );

      assertThat( queryResult.listStatements(
            context.selector( "test:ExtendingTestEntityEntity_To_AbstractTestEntityAbstractEntity a :Edge" )
      ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
            context.selector( "test:ExtendingTestEntityEntity_To_AbstractTestEntityAbstractEntity :from test:ExtendingTestEntityEntity" )
      ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
            context.selector( "test:ExtendingTestEntityEntity_To_AbstractTestEntityAbstractEntity :to test:AbstractTestEntityAbstractEntity" )
      ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
            context.selector( "test:ExtendingTestEntityEntity_To_AbstractTestEntityAbstractEntity :title extends" )
      ).toList() ).hasSize( 1 );
   }
}
