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
import io.openmanufacturing.sds.test.TestEntity;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

public class Entity2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "entity2boxmodel.sparql";
   private final int totalNumberOfExpectedEntries = 3;
   private final int indexOfSeeValueEntry = 2;
   private final String expectedSeeEntryTitle = "see";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.ENTITY_WITH_SEE_ATTRIBUTE, metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, ":EntityWithSeeAttributeEntity a :Box",
            ":EntityWithSeeAttributeEntity :entries *",
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle, "http://example.com/omp" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributesArePresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.ENTITY_WITH_MULTIPLE_SEE_ATTRIBUTES, metaModelVersion );
      context.executeAttributeIsPresentTest( sparqlQueryFileName, ":EntityWithMultipleSeeAttributesEntity a :Box",
            ":EntityWithMultipleSeeAttributesEntity :entries *",
            totalNumberOfExpectedEntries, indexOfSeeValueEntry, expectedSeeEntryTitle,
            "http://example.com/me, http://example.com/omp" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsNotPresentExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.ENTITY_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      context.executeAttributeIsNotPresentTest( sparqlQueryFileName, ":EntityWithoutSeeAttributeEntity a :Box",
            ":EntityWithoutSeeAttributeEntity :entries *",
            totalNumberOfExpectedEntries, indexOfSeeValueEntry );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSeeAttributeIsPresentOnSharedEntityExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.SHARED_ENTITY_WITH_SEE_ATTRIBUTE, metaModelVersion );

      final Query query = QueryFactory.create( context.getInputStreamAsString( sparqlQueryFileName ) );

      final Model queryResult = ModelFactory.createDefaultModel();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, context.model() ) ) {
         qexec.execConstruct( queryResult );
      }

      assertThat(
            queryResult.listStatements( context.selector( ":SharedEntityWithSeeAttributeEntity a :Box" ) ).toList() )
            .hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( "bamm-e:SharedEntityEntity a :Box" ) ).toList() )
            .hasSize( 0 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEntityWithOptionalAndNotInPayloadProperty( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.ENTITY_WITH_OPTIONAL_AND_NOT_IN_PAYLOAD_PROPERTY,
            metaModelVersion );

      final Query query = QueryFactory
            .create( context.getInputStreamAsString( "aspect-property-edges2boxmodel.sparql" ) );

      final Model queryResult = ModelFactory.createDefaultModel();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, context.model() ) ) {
         qexec.execConstruct( queryResult );
      }

      assertThat(
            queryResult.listStatements( context.selector(
                  ":EntityWithOptionalAndNotInPayloadPropertyEntity_To_testPropertyOneProperty a :Edge" ) )
                       .toList() ).hasSize( 1 );
      assertThat( queryResult
            .listStatements( context.selector(
                  ":EntityWithOptionalAndNotInPayloadPropertyEntity_To_testPropertyOneProperty :title property" ) )
            .toList() ).hasSize( 1 );

      assertThat(
            queryResult.listStatements( context.selector(
                  ":EntityWithOptionalAndNotInPayloadPropertyEntity_To_testPropertyTwoProperty a :Edge" ) )
                       .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
            context.selector(
                  ":EntityWithOptionalAndNotInPayloadPropertyEntity_To_testPropertyTwoProperty :title property (optional)" ) )
                             .toList() ).hasSize( 1 );

      assertThat( queryResult
            .listStatements( context.selector(
                  ":EntityWithOptionalAndNotInPayloadPropertyEntity_To_testPropertyThreeProperty a :Edge" ) )
            .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
            context.selector(
                  ":EntityWithOptionalAndNotInPayloadPropertyEntity_To_testPropertyThreeProperty :title property (not in payload)" ) )
                             .toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEntityWithOptionalProperty( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.ENTITY_WITH_OPTIONAL_PROPERTY, metaModelVersion );

      final Query query = QueryFactory
            .create( context.getInputStreamAsString( "aspect-property-edges2boxmodel.sparql" ) );

      final Model queryResult = ModelFactory.createDefaultModel();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, context.model() ) ) {
         qexec.execConstruct( queryResult );
      }

      assertThat(
            queryResult.listStatements( context.selector(
                  ":EntityWithOptionalPropertyEntity_To_testPropertyProperty a :Edge" ) )
                       .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
            context.selector(
                  ":EntityWithOptionalPropertyEntity_To_testPropertyProperty :title property (optional)" ) )
                             .toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEntityWithOptionalPropertyWithPayloadName( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.ENTITY_WITH_OPTIONAL_PROPERTY_WITH_PAYLOAD_NAME,
            metaModelVersion );

      final Query query = QueryFactory
            .create( context.getInputStreamAsString( "aspect-property-edges2boxmodel.sparql" ) );

      final Model queryResult = ModelFactory.createDefaultModel();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, context.model() ) ) {
         qexec.execConstruct( queryResult );
      }

      assertThat(
            queryResult.listStatements( context.selector(
                  ":EntityWithOptionalPropertyWithPayloadNameEntity_To_testPropertyProperty a :Edge" ) )
                       .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
            context.selector(
                  ":EntityWithOptionalPropertyWithPayloadNameEntity_To_testPropertyProperty :title property (optional) (test)" ) )
                             .toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEntityWithPropertyWithPayloadName( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestEntity.ENTITY_WITH_PROPERTY_WITH_PAYLOAD_NAME,
            metaModelVersion );

      final Query query = QueryFactory
            .create( context.getInputStreamAsString( "aspect-property-edges2boxmodel.sparql" ) );

      final Model queryResult = ModelFactory.createDefaultModel();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, context.model() ) ) {
         qexec.execConstruct( queryResult );
      }

      assertThat(
            queryResult.listStatements( context.selector(
                  ":EntityWithPropertyWithPayloadNameEntity_To_testPropertyProperty a :Edge" ) )
                       .toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements(
            context.selector(
                  ":EntityWithPropertyWithPayloadNameEntity_To_testPropertyProperty :title property (test)" ) )
                             .toList() ).hasSize( 1 );
   }
}
