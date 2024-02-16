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

package org.eclipse.esmf.aspectmodel.versionupdate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MigratorTest extends MetaModelVersions {

   private final MigratorService migratorService = new MigratorService();

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testRawModelIsMigrated( final KnownVersion metaModelVersion ) {
      final VersionedModel versionedModel = TestResources.getModelWithoutResolution( TestAspect.ASPECT, metaModelVersion );
      final VersionedModel rewrittenModel = migratorService.updateMetaModelVersion( versionedModel ).get();
      assertThat( versionedModel.getRawModel().size() ).isEqualTo( rewrittenModel.getRawModel().size() );
      if ( metaModelVersion.equals( KnownVersion.getLatest() ) ) {
         return;
      }
      final SAMM originalSamm = new SAMM( metaModelVersion );
      final SAMM latestSamm = new SAMM( KnownVersion.getLatest() );
      assertThat( rewrittenModel.getRawModel().contains( null, RDF.type, originalSamm.Aspect() ) ).isFalse();
      assertThat( rewrittenModel.getRawModel().contains( null, RDF.type, latestSamm.Aspect() ) ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testUriRewriting( final KnownVersion metaModelVersion ) {
      final VersionedModel versionedModel = TestResources.getModelWithoutResolution( TestAspect.ASPECT, metaModelVersion );
      final VersionedModel rewrittenModel = migratorService.updateMetaModelVersion( versionedModel ).get();

      Assertions.assertThat( rewrittenModel.getMetaModelVersion() )
            .isEqualTo( VersionNumber.parse( KnownVersion.getLatest().toVersionString() ) );
      final Model model = rewrittenModel.getRawModel();
      assertThat( model.getNsPrefixURI( "samm" ) ).contains( KnownVersion.getLatest().toVersionString() );

      if ( metaModelVersion.equals( KnownVersion.getLatest() ) ) {
         return;
      }

      final String metaModelNameSpace = String.format( "urn:samm:org.eclipse.esmf.samm:meta-model:%s", metaModelVersion.toVersionString() );
      assertThat( getAllUris( model ) ).noneMatch( uri -> uri.contains( metaModelNameSpace ) );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testUriRewritingDoesNotChangeCustomNamespaces( final KnownVersion metaModelVersion ) {
      final VersionedModel versionedModel = TestResources.getModelWithoutResolution( TestAspect.ASPECT_WITH_CUSTOM_NAMESPACE,
            metaModelVersion );
      final VersionedModel rewrittenModel = migratorService.updateMetaModelVersion( versionedModel ).get();

      assertThat( rewrittenModel.getRawModel().getNsPrefixMap() ).containsKey( "custom" );
   }

   @Test
   public void testMigrateUnitsToSammNamespace() {
      final VersionedModel oldModel = TestResources.getModelWithoutResolution( TestAspect.ASPECT_WITH_CUSTOM_UNIT,
            KnownVersion.SAMM_1_0_0 );
      final Model rewrittenModel = migratorService.updateMetaModelVersion( oldModel ).get().getRawModel();
      final SAMM samm = new SAMM( KnownVersion.getLatest() );

      assertThat( rewrittenModel.contains( null, RDF.type, samm.Unit() ) ).isTrue();
      assertThat( rewrittenModel.contains( null, samm.symbol(), (RDFNode) null ) ).isTrue();
      assertThat( rewrittenModel.contains( null, samm.quantityKind(), (RDFNode) null ) ).isTrue();
      final Set<String> uris = getAllUris( rewrittenModel );
      final String sammVersion = KnownVersion.getLatest().toVersionString();
      assertThat( uris ).noneMatch( uri -> uri.contains( "urn:samm:org.eclipse.esmf.samm:unit:" + sammVersion + "#Unit" ) );
      assertThat( uris ).noneMatch( uri -> uri.contains( "urn:samm:org.eclipse.esmf.samm:unit:" + sammVersion + "#symbol" ) );
      assertThat( uris ).noneMatch( uri -> uri.contains( "urn:samm:org.eclipse.esmf.samm:unit:" + sammVersion + "#quantityKind" ) );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testRemoveSammName( final KnownVersion metaModelVersion ) {
      final SAMM samm = new SAMM( metaModelVersion );
      final VersionedModel versionedModel = TestResources.getModelWithoutResolution( TestAspect.ASPECT, metaModelVersion );
      final VersionedModel rewrittenModel = migratorService.updateMetaModelVersion( versionedModel ).get();

      final String sammNameUrn = samm.getNamespace() + "name";
      final List<Statement> sammNameStatements = rewrittenModel.getModel().listStatements().toList().stream()
            .filter( statement -> statement.getPredicate().getURI().equals( sammNameUrn ) )
            .collect( Collectors.toList() );
      assertThat( sammNameStatements ).isEmpty();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testCurieMigration( final KnownVersion metaModelVersion ) {
      final VersionedModel versionedModel = TestResources.getModelWithoutResolution( TestAspect.ASPECT_WITH_CURIE, metaModelVersion );
      final VersionedModel rewrittenModel = migratorService.updateMetaModelVersion( versionedModel ).get();
      final SAMM latestSamm = new SAMM( KnownVersion.getLatest() );
      assertThat( rewrittenModel.getRawModel().listStatements( null, latestSamm.exampleValue(), (RDFNode) null ).nextStatement().getObject()
            .asLiteral()
            .getDatatypeURI() ).isEqualTo( latestSamm.curie().getURI() );
   }

   private Set<String> getAllUris( final Model model ) {
      return Streams.stream( model.listStatements() ).flatMap( statement -> {
         final Stream<String> subjectUri = Stream.of( statement.getSubject().getURI() );
         final Stream<String> predicateUri = Stream.of( statement.getPredicate().getURI() );
         final Stream<String> objectUri = statement.getObject().isURIResource()
               ? Stream.of( statement.getObject().asResource().getURI() )
               : Stream.empty();
         return Stream.of( subjectUri, predicateUri, objectUri ).flatMap( Function.identity() );
      } ).filter( Objects::nonNull ).collect( Collectors.toSet() );
   }
}
