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

package org.eclipse.esmf.aspectmodel.resolver.services;

import static org.assertj.vavr.api.VavrAssertions.assertThat;

import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.assertj.vavr.api.VavrAssertions;
import org.eclipse.esmf.aspectmodel.MissingMetaModelVersionException;
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;
import io.vavr.control.Try;

public class SammAspectMetaModelResourceResolverTest extends MetaModelVersions {
   private final SammAspectMetaModelResourceResolver aspectMetaModelResourceResolver = new SammAspectMetaModelResourceResolver();

   private Model getModel( final String resource, final KnownVersion version ) {
      final InputStream aspectModel =
            SammAspectMetaModelResourceResolverTest.class.getClassLoader().getResourceAsStream(
                  version.toString().toLowerCase() + "/" + resource );
      return TurtleLoader.loadTurtle( aspectModel ).get();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGetMetaModelVersionExpectSuccess( final KnownVersion metaModelVersion ) {
      final Model model = getModel( "valid_aspect.ttl", metaModelVersion );
      VavrAssertions.assertThat( aspectMetaModelResourceResolver.getMetaModelVersion( model ) ).isSuccess();
      VavrAssertions.assertThat( aspectMetaModelResourceResolver.getMetaModelVersion( model ) )
            .contains( VersionNumber.parse( metaModelVersion.toVersionString() ) );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGetMetaModelVersionInvalidPrefixExpectFailure( final KnownVersion metaModelVersion ) {
      final Model model = getModel( "invalid_aspect_urn_prefix.ttl", metaModelVersion );
      final Try<VersionNumber> modelVersion = aspectMetaModelResourceResolver.getMetaModelVersion( model );
      assertThat( modelVersion ).isFailure();
      assertThat( modelVersion ).failBecauseOf( MissingMetaModelVersionException.class );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGetMetaModelVersionInvalidUrnExpectFailure( final KnownVersion metaModelVersion ) {
      final Model model = getModel( "invalid_aspect_urn.ttl", metaModelVersion );
      final Try<VersionNumber> modelVersion = aspectMetaModelResourceResolver.getMetaModelVersion( model );
      assertThat( modelVersion ).isFailure();
      assertThat( modelVersion ).failBecauseOf( MissingMetaModelVersionException.class );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGetMetaModelVersionInvalidMetaModelUrnElementExpectFailure( final KnownVersion metaModelVersion ) {
      final Model model = getModel( "invalid_aspect_meta_model_urn_element.ttl", metaModelVersion );
      final Try<VersionNumber> modelVersion = aspectMetaModelResourceResolver.getMetaModelVersion( model );
      assertThat( modelVersion ).isFailure();
      assertThat( modelVersion ).failBecauseOf( MissingMetaModelVersionException.class );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGetAspectModelUrnExpectSuccess( final KnownVersion metaModelVersion ) {
      final Try<AspectModelUrn> aspectModelUrn = AspectModelUrn.from(
            "urn:samm:org.eclipse.esmf.samm:meta-model:" + metaModelVersion.toVersionString() + "#Aspect" );
      assertThat( aspectModelUrn ).isSuccess();
      org.assertj.core.api.Assertions.assertThat( aspectModelUrn.get().getUrn().toString() )
            .isEqualTo( "urn:samm:org.eclipse.esmf.samm:meta-model:" + metaModelVersion
                  .toVersionString() + "#Aspect" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGetAspectModelUrnInvalidUrnExpectFailure( final KnownVersion metaModelVersion ) {
      final Try<AspectModelUrn> aspectModelUrn = AspectModelUrn.from( "urn:foo:org.eclipse.esmf.samm:meta-model:" + metaModelVersion.toVersionString() );
      assertThat( aspectModelUrn ).isFailure();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGetKnownVersionExpectSuccess( final KnownVersion metaModelVersion ) {
      final KnownVersion parsedVersion = KnownVersion.fromVersionString( metaModelVersion.toVersionString() ).get();
      org.assertj.core.api.Assertions.assertThat( parsedVersion ).isEqualTo( metaModelVersion );
   }

   @Test
   public void testGetUnknownVersionExpectFailure() {
      final Model model = ModelFactory.createDefaultModel();
      model.setNsPrefix( "samm", "urn:samm:org.eclipse.esmf.samm:meta-model:5.0.0#" );
      final Try<VersionNumber> metaModelVersion = aspectMetaModelResourceResolver.getMetaModelVersion( model );
      assertThat( metaModelVersion ).isFailure();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testLoadMetaModelExpectSuccess( final KnownVersion metaModelVersion ) {
      final Model model = aspectMetaModelResourceResolver.loadMetaModel( metaModelVersion ).get();

      org.assertj.core.api.Assertions.assertThat( model.contains( ResourceFactory.createResource(
                  "urn:samm:org.eclipse.esmf.samm:meta-model:" + metaModelVersion.toVersionString() + "#value" ),
            RDF.type, (RDFNode) null ) ).isTrue();
   }
}
