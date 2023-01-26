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

package io.openmanufacturing.sds.aspectmodel.resolver.services;

import static org.assertj.vavr.api.VavrAssertions.assertThat;

import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.MissingMetaModelVersionException;
import io.openmanufacturing.sds.aspectmodel.VersionNumber;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.vavr.control.Try;

public class SdsAspectMetaModelResourceResolverTest extends MetaModelVersions {
   private final SdsAspectMetaModelResourceResolver aspectMetaModelResourceResolver = new SdsAspectMetaModelResourceResolver();

   private Model getModel( final String resource, final KnownVersion version ) {
      final InputStream aspectModel =
            SdsAspectMetaModelResourceResolverTest.class.getClassLoader().getResourceAsStream(
                  version.toString().toLowerCase() + "/" + resource );
      return TurtleLoader.loadTurtle( aspectModel ).get();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGetBammVersionExpectSuccess( final KnownVersion metaModelVersion ) {
      final Model model = getModel( "valid_aspect.ttl", metaModelVersion );
      assertThat( aspectMetaModelResourceResolver.getBammVersion( model ) ).isSuccess();
      assertThat( aspectMetaModelResourceResolver.getBammVersion( model ) )
            .contains( VersionNumber.parse( metaModelVersion.toVersionString() ) );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGetBammVersionInvalidPrefixExpectFailure( final KnownVersion metaModelVersion ) {
      final Model model = getModel( "invalid_aspect_urn_prefix.ttl", metaModelVersion );
      final Try<VersionNumber> modelVersion = aspectMetaModelResourceResolver.getBammVersion( model );
      assertThat( modelVersion ).isFailure();
      assertThat( modelVersion ).failBecauseOf( MissingMetaModelVersionException.class );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGetBammVersionInvalidUrnExpectFailure( final KnownVersion metaModelVersion ) {
      final Model model = getModel( "invalid_aspect_urn.ttl", metaModelVersion );
      final Try<VersionNumber> modelVersion = aspectMetaModelResourceResolver.getBammVersion( model );
      assertThat( modelVersion ).isFailure();
      assertThat( modelVersion ).failBecauseOf( MissingMetaModelVersionException.class );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGetBammVersionInvalidMetaModelUrnElementExpectFailure( final KnownVersion metaModelVersion ) {
      final Model model = getModel( "invalid_aspect_meta_model_urn_element.ttl", metaModelVersion );
      final Try<VersionNumber> modelVersion = aspectMetaModelResourceResolver.getBammVersion( model );
      assertThat( modelVersion ).isFailure();
      assertThat( modelVersion ).failBecauseOf( MissingMetaModelVersionException.class );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGetAspectModelUrnExpectSuccess( final KnownVersion metaModelVersion ) {
      final Try<AspectModelUrn> aspectModelUrn = AspectModelUrn.from(
            "urn:bamm:io.openmanufacturing:meta-model:" + metaModelVersion.toVersionString() + "#Aspect" );
      assertThat( aspectModelUrn ).isSuccess();
      org.assertj.core.api.Assertions.assertThat( aspectModelUrn.get().getUrn().toString() )
            .isEqualTo( "urn:bamm:io.openmanufacturing:meta-model:" + metaModelVersion
                  .toVersionString() + "#Aspect" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGetAspectModelUrnInvalidUrnExpectFailure( final KnownVersion metaModelVersion ) {
      final Try<AspectModelUrn> aspectModelUrn = AspectModelUrn.from( "urn:foo:io.openmanufacturing:meta-model:" + metaModelVersion.toVersionString() );
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
      model.setNsPrefix( "bamm", "urn:bamm:io.openmanufacturing:meta-model:5.0.0#" );
      final Try<VersionNumber> bammVersion = aspectMetaModelResourceResolver.getBammVersion( model );
      assertThat( bammVersion ).isFailure();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testLoadMetaModelExpectSuccess( final KnownVersion metaModelVersion ) {
      final Model model = aspectMetaModelResourceResolver.loadMetaModel( metaModelVersion ).get();

      org.assertj.core.api.Assertions.assertThat( model.contains( ResourceFactory.createResource(
                  "urn:bamm:io.openmanufacturing:meta-model:" + metaModelVersion.toVersionString() + "#value" ),
            RDF.type, (RDFNode) null ) ).isTrue();
   }
}
