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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.assertj.core.api.Assertions;
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.Streams;

import org.eclipse.esmf.samm.KnownVersion;

import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;

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

      Assertions.assertThat( rewrittenModel.getMetaModelVersion() ).isEqualTo( VersionNumber.parse( KnownVersion.getLatest().toVersionString() ) );
      final Model model = rewrittenModel.getRawModel();
      assertThat( model.getNsPrefixURI( "bamm" ) ).contains( KnownVersion.getLatest().toVersionString() );

      if ( metaModelVersion.equals( KnownVersion.getLatest() ) ) {
         return;
      }

      final String metaModelNameSpace = String.format( "urn:bamm:io.openmanufacturing:meta-model:%s", metaModelVersion.toVersionString() );
      assertThat( getAllUris( model ) ).noneMatch( uri -> uri.contains( metaModelNameSpace ) );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testUriRewritingDoesNotChangeCustomNamespaces( final KnownVersion metaModelVersion ) {
      final VersionedModel versionedModel = TestResources.getModelWithoutResolution( TestAspect.ASPECT_WITH_CUSTOM_NAMESPACE, metaModelVersion );
      final VersionedModel rewrittenModel = migratorService.updateMetaModelVersion( versionedModel ).get();

      assertThat( rewrittenModel.getRawModel().getNsPrefixMap() ).containsKey( "custom" );
   }

   @Test
   public void testMigrateUnitsToBammNamespace() {
      final VersionedModel oldModel = TestResources.getModelWithoutResolution( TestAspect.ASPECT_WITH_CUSTOM_UNIT, KnownVersion.SAMM_1_0_0 );
      final Model rewrittenModel = migratorService.updateMetaModelVersion( oldModel ).get().getRawModel();
      final SAMM SAMM = new SAMM( KnownVersion.SAMM_2_0_0 );

      assertThat( rewrittenModel.contains( null, RDF.type, SAMM.Unit() ) ).isTrue();
      assertThat( rewrittenModel.contains( null, SAMM.symbol(), (RDFNode) null ) ).isTrue();
      assertThat( rewrittenModel.contains( null, SAMM.quantityKind(), (RDFNode) null ) ).isTrue();
      final Set<String> uris = getAllUris( rewrittenModel );
      assertThat( uris ).noneMatch( uri -> uri.contains( "urn:bamm:io.openmanufacturing:unit:2.0.0#Unit" ) );
      assertThat( uris ).noneMatch( uri -> uri.contains( "urn:bamm:io.openmanufacturing:unit:2.0.0#symbol" ) );
      assertThat( uris ).noneMatch( uri -> uri.contains( "urn:bamm:io.openmanufacturing:unit:2.0.0#quantityKind" ) );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testRemoveBammName( final KnownVersion metaModelVersion ) {
      final SAMM SAMM = new SAMM( metaModelVersion );
      final VersionedModel versionedModel = TestResources.getModelWithoutResolution( TestAspect.ASPECT, metaModelVersion );
      final VersionedModel rewrittenModel = migratorService.updateMetaModelVersion( versionedModel ).get();

      final String bammNameUrn = SAMM.getNamespace() + "name";
      final List<Statement> bammNameStatements = rewrittenModel.getModel().listStatements().toList().stream()
            .filter( statement -> statement.getPredicate().getURI().equals( bammNameUrn ) )
            .collect( Collectors.toList() );
      assertThat( bammNameStatements ).isEmpty();
   }

   private Set<String> getAllUris( final Model model ) {
      return Streams.stream( model.listStatements() ).flatMap( statement -> {
         final Stream<String> subjectUri = Stream.of( statement.getSubject().getURI() );
         final Stream<String> predicateUri = Stream.of( statement.getPredicate().getURI() );
         final Stream<String> objectUri = statement.getObject().isURIResource() ?
               Stream.of( statement.getObject().asResource().getURI() ) : Stream.empty();
         return Stream.of( subjectUri, predicateUri, objectUri ).flatMap( Function.identity() );
      } ).filter( Objects::nonNull ).collect( Collectors.toSet() );
   }
}
