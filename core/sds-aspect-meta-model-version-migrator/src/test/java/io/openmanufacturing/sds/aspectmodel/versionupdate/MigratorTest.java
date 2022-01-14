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

package io.openmanufacturing.sds.aspectmodel.versionupdate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.Streams;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.VersionNumber;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;

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
      final BAMM originalBamm = new BAMM( metaModelVersion );
      final BAMM latestBamm = new BAMM( KnownVersion.getLatest() );
      assertThat( rewrittenModel.getRawModel().contains( null, RDF.type, originalBamm.Aspect() ) ).isFalse();
      assertThat( rewrittenModel.getRawModel().contains( null, RDF.type, latestBamm.Aspect() ) ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testUriRewriting( final KnownVersion metaModelVersion ) {
      final VersionedModel versionedModel = TestResources.getModelWithoutResolution( TestAspect.ASPECT, metaModelVersion );
      final VersionedModel rewrittenModel = migratorService.updateMetaModelVersion( versionedModel ).get();

      assertThat( rewrittenModel.getVersion() ).isEqualTo( VersionNumber.parse( KnownVersion.getLatest().toVersionString() ) );
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
      final VersionedModel oldModel = TestResources.getModelWithoutResolution( TestAspect.ASPECT_WITH_CUSTOM_UNIT, KnownVersion.BAMM_1_0_0 );
      final Model rewrittenModel = migratorService.updateMetaModelVersion( oldModel ).get().getRawModel();
      final BAMM bamm = new BAMM( KnownVersion.BAMM_2_0_0 );

      assertThat( rewrittenModel.contains( null, RDF.type, bamm.Unit() ) ).isTrue();
      assertThat( rewrittenModel.contains( null, bamm.symbol(), (RDFNode) null ) ).isTrue();
      assertThat( rewrittenModel.contains( null, bamm.quantityKind(), (RDFNode) null ) ).isTrue();
      final Set<String> uris = getAllUris( rewrittenModel );
      assertThat( uris ).noneMatch( uri -> uri.contains( "urn:bamm:io.openmanufacturing:unit:2.0.0#Unit" ) );
      assertThat( uris ).noneMatch( uri -> uri.contains( "urn:bamm:io.openmanufacturing:unit:2.0.0#symbol" ) );
      assertThat( uris ).noneMatch( uri -> uri.contains( "urn:bamm:io.openmanufacturing:unit:2.0.0#quantityKind" ) );
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
