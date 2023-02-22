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

import java.util.List;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.eclipse.esmf.samm.KnownVersion;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;

public class EntityInstance2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "entityinstance2boxmodel.sparql";
   private final String entityInstance2EntityInstanceEdgesSparqlQueryFileName = "entityinstance-nestedentityinstance-edges2boxmodel.sparql";
   private final String entityInstance2EntityEdgesSparqlQueryFileName = "entityinstance-entity-edges2boxmodel.sparql";
   private final String boxSelectorStatement = ":TestEntityInstanceEntityInstance a :Box";
   private final String entriesSelectorStatement = ":TestEntityInstanceEntityInstance :entry *";
   private final String codePropertyTitleSelectorStatement = "* :title code";
   private final String codePropertyTestSelectorStatement = "* :text 3";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEntityInstanceWithScalarProperties2BoxModelExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENTITY_INSTANCE_WITH_SCALAR_PROPERTIES,
            metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertTestEntityInstanceBox( queryResult, context, 2 );
      assertThat( queryResult.listStatements( context.selector( "* :title description" ) ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( "* :text foo" ) ).toList() ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEntityInstanceWithScalarListProperty2BoxModelExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENTITY_INSTANCE_WITH_SCALAR_LIST_PROPERTY,
            metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertTestEntityInstanceBox( queryResult, context, 2 );
      assertThat( queryResult.listStatements( context.selector( "* :title testList" ) ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( "* :text *" ) )
            .mapWith( Statement::getObject )
            .filterKeep( RDFNode::isLiteral )
            .mapWith( RDFNode::asLiteral )
            .mapWith( Literal::getString )
            .toList() ).contains( "3" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEntityInstanceWithNestedEntityListProperty2BoxModelExpectSuccess(
         final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext(
            TestAspect.ASPECT_WITH_ENTITY_INSTANCE_WITH_NESTED_ENTITY_LIST_PROPERTY, metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertTestEntityInstanceBox( queryResult, context, 1 );

      assertNestedEntityInstanceBox( queryResult, context, "NestedEntityInstanceEntityInstance", "bar" );
      assertNestedEntityInstanceBox( queryResult, context, "NestedEntityInstanceTwoEntityInstance", "baz" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEntityInstanceWithNestedEntityProperty2BoxModelExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENTITY_INSTANCE_WITH_NESTED_ENTITY_PROPERTY,
            metaModelVersion );

      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertTestEntityInstanceBox( queryResult, context, 1 );
      assertNestedEntityInstanceBox( queryResult, context, "NestedEntityInstanceEntityInstance", "bar" );
   }

   private void assertTestEntityInstanceBox( final Model queryResult, final TestContext context,
         final int expectedNumberOfEntries ) {
      assertThat( queryResult.listStatements( context.selector( boxSelectorStatement ) ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( entriesSelectorStatement ) ).toList() )
            .hasSize( expectedNumberOfEntries );
      assertThat( queryResult.listStatements( context.selector( codePropertyTitleSelectorStatement ) ).toList() )
            .hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( codePropertyTestSelectorStatement ) ).toList() )
            .hasSize( 1 );
   }

   private void assertNestedEntityInstanceBox( final Model queryResult, final TestContext context,
         final String expectedInstanceBoxName, final String expectedPropertyValue ) {
      final StmtIterator nestedEntityBoxStatement = queryResult
            .listStatements( context.selector( ":" + expectedInstanceBoxName + " a :Box" ) );
      assertThat( nestedEntityBoxStatement.toList() ).hasSize( 1 );

      final List<Statement> nestedEntityEntries = queryResult
            .listStatements( context.selector( ":" + expectedInstanceBoxName + " :entry *" ) ).toList();
      assertThat( nestedEntityEntries ).hasSize( 1 );

      final RDFNode entryNode = nestedEntityEntries.get( 0 ).getObject();
      context.assertEntry( List.of( entryNode ), 1, 0, "nestedEntityProperty", expectedPropertyValue );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEntityInstanceToEntityInstanceEdgesWithoutListPropertyExpectSuccess(
         final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENTITY_INSTANCE_WITH_NESTED_ENTITY_PROPERTY,
            metaModelVersion );

      final Model queryResult = context.executeQuery( entityInstance2EntityInstanceEdgesSparqlQueryFileName );

      assertEdge( queryResult, context, "TestEntityInstanceEntityInstance", "NestedEntityInstanceEntityInstance",
            "nestedEntity value" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEntityInstanceToEntityInstanceEdgesWithListPropertyExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext(
            TestAspect.ASPECT_WITH_ENTITY_INSTANCE_WITH_NESTED_ENTITY_LIST_PROPERTY, metaModelVersion );

      final Model queryResult = context.executeQuery( entityInstance2EntityInstanceEdgesSparqlQueryFileName );

      assertEdge( queryResult, context, "TestEntityInstanceEntityInstance", "NestedEntityInstanceEntityInstance",
            "testList value" );
      assertEdge( queryResult, context, "TestEntityInstanceEntityInstance", "NestedEntityInstanceTwoEntityInstance",
            "testList value" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEntityInstanceToEntityEdgesWithoutListPropertyExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_ENTITY_INSTANCE_WITH_NESTED_ENTITY_PROPERTY,
            metaModelVersion );

      final Model queryResult = context.executeQuery( entityInstance2EntityEdgesSparqlQueryFileName );

      assertEdge( queryResult, context, "TestEntityInstanceEntityInstance", "TestEntityEntity", "is a" );
      assertEdge( queryResult, context, "NestedEntityInstanceEntityInstance", "NestedEntityEntity", "is a" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEntityInstanceToEntityEdgesWithListPropertyExpectSuccess( final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext(
            TestAspect.ASPECT_WITH_ENTITY_INSTANCE_WITH_NESTED_ENTITY_LIST_PROPERTY, metaModelVersion );

      final Model queryResult = context.executeQuery( entityInstance2EntityEdgesSparqlQueryFileName );

      assertEdge( queryResult, context, "TestEntityInstanceEntityInstance", "TestEntityEntity", "is a" );
      assertEdge( queryResult, context, "NestedEntityInstanceEntityInstance", "NestedEntityEntity", "is a" );
      assertEdge( queryResult, context, "NestedEntityInstanceTwoEntityInstance", "NestedEntityEntity", "is a" );
   }

   private void assertEdge( final Model queryResult, final TestContext context, final String fromBoxName,
         final String toBoxName, final String title ) {
      final String edgeName = fromBoxName + "_To_" + toBoxName;

      assertThat( queryResult.listStatements( context.selector( ":" + edgeName + " a :Edge" ) ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( ":" + edgeName + " :to :" + toBoxName ) ).toList() )
            .hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( ":" + edgeName + " :from :" + fromBoxName ) ).toList() )
            .hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( ":" + edgeName + " :title " + title ) ).toList() )
            .hasSize( 1 );
   }
}
