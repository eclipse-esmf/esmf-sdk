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

public class AbstractEntity2BoxModelTest extends MetaModelVersions {
   private final String sparqlQueryFileName = "abstractentity2boxmodel.sparql";

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testEntityExtendingAbstractEntity( final KnownVersion metaModelVersion ) {
      testAbstractEntity( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY, metaModelVersion );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testAspectWithAbstractSingleEntityExpectSuccess( final KnownVersion metaModelVersion ) {
      testAbstractEntity( TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY, metaModelVersion );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testCollectionWithAbstractEntityExpectSuccess( final KnownVersion metaModelVersion ) {
      testAbstractEntity( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY, metaModelVersion );
   }

   private void testAbstractEntity( final TestAspect testAspect, final KnownVersion metaModelVersion ) {
      final TestContext context = new TestContext( testAspect, metaModelVersion );
      
      final Model queryResult = context.executeQuery( sparqlQueryFileName );

      assertThat( queryResult.listStatements( context.selector( ":AbstractTestEntityAbstractEntity a :Box" ) ).toList() ).hasSize( 1 );
      assertThat( queryResult.listStatements( context.selector( ":AbstractTestEntityAbstractEntity :title AbstractTestEntity" ) ).toList() ).hasSize( 1 );
   }
}
