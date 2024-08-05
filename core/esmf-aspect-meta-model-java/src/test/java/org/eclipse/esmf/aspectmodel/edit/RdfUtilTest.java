/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.edit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class RdfUtilTest {
   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   void testEveryElementDefinitionContainsATypeAssertion( final TestAspect aspect ) {
      final AspectModel aspectModel = TestResources.load( aspect );
      aspectModel.elements().stream()
            .filter( element -> !element.isAnonymous() )
            .map( element -> element.getSourceFile().sourceModel().createResource( element.urn().toString() ) )
            .forEach( elementResource -> {
               final Model definition = RdfUtil.getModelElementDefinition( elementResource );
               assertThat( definition.listStatements( null, RDF.type, (RDFNode) null ).toList() ).hasSizeGreaterThan( 0 );
               assertThat( definition.listStatements( elementResource, RDF.type, (RDFNode) null ).toList() ).hasSize( 1 );
            } );
   }

   @Test
   void testGetElementDefinitionForFlatDefinition() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      final Model sourceModel = aspectModel.aspect().getSourceFile().sourceModel();
      final Resource aspectResource = sourceModel.createResource( aspectModel.aspect().urn().toString() );
      final Model definition = RdfUtil.getModelElementDefinition( aspectResource );
      assertThat( definition.size() ).isEqualTo( sourceModel.size() );
   }

   @Test
   void testGetElementDefinitionForNestedDefinition() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY );
      final Model sourceModel = aspectModel.aspect().getSourceFile().sourceModel();
      final Resource aspectResource = sourceModel.createResource( aspectModel.aspect().urn().toString() );
      final Model definition = RdfUtil.getModelElementDefinition( aspectResource );
      assertThat( definition.listStatements( null, RDF.type, (RDFNode) null ).toList() ).hasSize( 1 );
      final List<RDFNode> propertiesList = definition.listStatements( aspectResource, SammNs.SAMM.properties(), (RDFNode) null )
            .nextStatement()
            .getObject().as( RDFList.class ).asJavaList();
      assertThat( propertiesList ).hasSize( 1 );
   }

   @Test
   void testGetElementDefinitionForTraitWithConstraints() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT_WITH_CONSTRAINTS );
      final Model sourceModel = aspectModel.aspect().getSourceFile().sourceModel();

      final Resource traitResource = sourceModel.createResource( TestAspect.TEST_NAMESPACE + "TestRegularExpressionConstraint" );
      final Model definition = RdfUtil.getModelElementDefinition( traitResource );
      final Resource constraint = definition.getResource( traitResource.getURI() ).getProperty( SammNs.SAMMC.constraint() ).getObject()
            .asResource();
      assertThat( constraint.getProperty( RDF.type ).getObject().asResource() ).isEqualTo( SammNs.SAMMC.RegularExpressionConstraint() );
      assertThat( constraint.getProperty( SammNs.SAMM.value() ).getObject().asLiteral().getLexicalForm() ).contains( "a-z" );
   }
}
