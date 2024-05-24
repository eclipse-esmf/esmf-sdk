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

package org.eclipse.esmf.metamodel.visitor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class AspectStreamTraversalVisitorTest {
   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   public void testLoadAspectExpectSuccess( final TestAspect testAspect ) {
      final KnownVersion metaModelVersion = KnownVersion.getLatest();
      final VersionedModel versionedModel = TestResources.getModelWithoutResolution( testAspect, metaModelVersion );
      final Aspect aspect = AspectModelLoader.getSingleAspectUnchecked( versionedModel );
      final Model model = versionedModel.getModel();

      final Set<String> modelElementNames = Streams.stream( model.listStatements( null, RDF.type, (RDFNode) null ) )
            .map( Statement::getSubject )
            .filter( RDFNode::isURIResource )
            .map( RDFNode::asResource )
            .map( Resource::getURI )
            .map( uri -> uri.substring( uri.indexOf( '#' ) + 1 ) )
            .collect( Collectors.toSet() );

      final Set<String> namesFromVisitor = aspect.accept( new AspectStreamTraversalVisitor(), null )
            .flatMap( modelElement -> modelElement.isAnonymous() ? Stream.empty() : Stream.of( modelElement.getName() ) )
            .filter( name -> !name.equals( "UnnamedCharacteristic" ) )
            .collect( Collectors.toSet() );

      assertThat( modelElementNames ).containsAll( namesFromVisitor );
   }
}
