/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.turtle.languageserver.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategySupport;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.samm.KnownVersion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MetaModelStrategyTest {
   private static final ResolutionStrategySupport RESOLUTION_SUPPORT = new AspectModelLoader();
   private MetaModelStrategy strategy;

   @BeforeEach
   void setUp() {
      strategy = new MetaModelStrategy();
   }

   @Test
   void testResolvesAspectDefinition() {
      final AspectModelUrn urn = AspectModelUrn.fromUrn( SammNs.SAMM.Aspect().getURI() );

      final AspectModelFile result = strategy.apply( urn, RESOLUTION_SUPPORT );

      assertThat( result ).isNotNull();
      assertThat( result.sourceLocation() ).isPresent();
      final Path resultPath = Path.of( result.sourceLocation().get() );
      assertThat( resultPath ).exists();
      assertThat( result.sourceRepresentation() ).isNotBlank();
      assertThat( result.sourceRepresentation() ).contains( "samm:Aspect rdfs:subClassOf mmm:NamedConcept, mmm:ConceptWithProperties ;" );
   }

   @Test
   void testExtractedFileIsReadableFromDisk() throws Exception {
      final AspectModelUrn urn = AspectModelUrn.fromUrn( SammNs.SAMM.Property().getURI() );
      final AspectModelFile result = strategy.apply( urn, RESOLUTION_SUPPORT );

      final Path tempFile = Path.of( result.sourceLocation().get() );
      assertThat( tempFile ).exists();
      final String content = Files.readString( tempFile );
      assertThat( content ).contains( "samm:Property rdfs:subClassOf mmm:NamedConcept ;" );
   }

   @Test
   void testSameFileReturnedForMultipleCallsOnSameNamespace() {
      final AspectModelUrn urnAspect = AspectModelUrn.fromUrn( SammNs.SAMM.Aspect().getURI() );
      final AspectModelUrn urnProperty = AspectModelUrn.fromUrn( SammNs.SAMM.Property().getURI() );

      final AspectModelFile result1 = strategy.apply( urnAspect, RESOLUTION_SUPPORT );
      final AspectModelFile result2 = strategy.apply( urnProperty, RESOLUTION_SUPPORT );

      assertThat( result1.sourceLocation() ).isEqualTo( result2.sourceLocation() );
   }

   @Test
   void testResolvesCharacteristicDefinition() {
      final AspectModelUrn urn = AspectModelUrn.fromUrn( SammNs.SAMMC.SingleEntity().getURI() );
      final AspectModelFile result = strategy.apply( urn, RESOLUTION_SUPPORT );

      assertThat( result.sourceLocation() ).isPresent();
      assertThat( result.sourceRepresentation() ).contains( "samm-c:SingleEntity rdfs:subClassOf samm:Characteristic ;" );
   }

   @Test
   void testThrowsForNonSammUrn() {
      final AspectModelUrn urn = AspectModelUrn.fromUrn( "urn:samm:com.example:1.0.0#MyClass" );
      assertThatThrownBy( () -> strategy.apply( urn, RESOLUTION_SUPPORT ) )
            .isInstanceOf( ModelResolutionException.class );
   }

   @Test
   void testThrowsForUnknownSammElement() {
      final AspectModelUrn urn = AspectModelUrn.fromUrn( SammNs.SAMM.resource( "NonExistentElement99" ).getURI() );
      assertThatThrownBy( () -> strategy.apply( urn, RESOLUTION_SUPPORT ) )
            .isInstanceOf( ModelResolutionException.class );
   }

   @Test
   void testListContentsReturnsEmpty() {
      assertThat( strategy.listContents() ).isEmpty();
   }

   @Test
   void testResolvesOlderSammVersion() {
      final AspectModelUrn urn = AspectModelUrn.fromUrn( new SAMM( KnownVersion.SAMM_2_1_0 ).Aspect().getURI() );
      final AspectModelFile result = strategy.apply( urn, RESOLUTION_SUPPORT );

      assertThat( result.sourceLocation() ).isPresent();
      final Path resultPath = Path.of( result.sourceLocation().get() );
      assertThat( resultPath ).exists();
      assertThat( resultPath.getFileName().toString() ).contains( "2.1.0" );
      assertThat( result.sourceRepresentation() ).contains( "@prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#> ." );
      assertThat( result.sourceRepresentation() ).contains( "samm:Aspect rdfs:subClassOf mmm:NamedConcept, mmm:ConceptWithProperties ;" );
   }

   @Test
   void testDifferentVersionsGetDifferentTempFiles() {
      final AspectModelUrn urn210 = AspectModelUrn.fromUrn( new SAMM( KnownVersion.SAMM_2_1_0 ).Aspect().getURI() );
      final AspectModelUrn urn220 = AspectModelUrn.fromUrn( SammNs.SAMM.Aspect().getURI() );

      final AspectModelFile result210 = strategy.apply( urn210, RESOLUTION_SUPPORT );
      final AspectModelFile result220 = strategy.apply( urn220, RESOLUTION_SUPPORT );

      assertThat( result210.sourceLocation() ).isNotEqualTo( result220.sourceLocation() );
   }
}
