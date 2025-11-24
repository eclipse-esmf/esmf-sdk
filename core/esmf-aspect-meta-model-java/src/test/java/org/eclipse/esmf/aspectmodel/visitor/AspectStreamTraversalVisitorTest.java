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

package org.eclipse.esmf.aspectmodel.visitor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AspectStreamTraversalVisitorTest {
   @ParameterizedTest
   @Execution( ExecutionMode.CONCURRENT )
   @EnumSource( value = TestAspect.class )
   void testLoadAspectExpectSuccess( final TestAspect testAspect ) {
      final AspectModel aspectModel = TestResources.load( testAspect );
      assertThat( aspectModel.files() ).hasSize( 1 );
      final AspectModelFile file = aspectModel.files().iterator().next();
      final Model model = file.sourceModel();
      assertThat( file.aspects() ).hasSize( 1 );
      final Aspect aspect = file.aspects().iterator().next();

      final Set<String> modelElementUris = Streams.stream( model.listStatements( null, RDF.type, (RDFNode) null ) )
            .map( Statement::getSubject )
            .filter( RDFNode::isURIResource )
            .map( RDFNode::asResource )
            .map( Resource::getURI )
            .collect( Collectors.toSet() );

      final Set<String> urisFromVisitor = aspect.accept( new AspectStreamTraversalVisitor(), null )
            .flatMap( modelElement -> modelElement.isAnonymous() ? Stream.empty() : Stream.of( modelElement.urn() ) )
            .filter( urn -> !urn.isSammUrn() )
            .map( AspectModelUrn::toString )
            .collect( Collectors.toSet() );

      assertThat( modelElementUris ).containsAll( urisFromVisitor );
   }
}
