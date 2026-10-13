/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.java.performance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategySupport;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.samm.KnownVersion;

import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;
import org.quickperf.annotation.ExpectMaxExecutionTime;
import org.quickperf.junit5.QuickPerfTest;
import org.quickperf.jvm.allocation.AllocationUnit;
import org.quickperf.jvm.annotations.ExpectMaxHeapAllocation;
import org.quickperf.jvm.annotations.MeasureHeapAllocation;

@QuickPerfTest
@MeasureHeapAllocation
class EsmfPerformanceTest {
   private static final int ASPECTS = 20;
   private static final int PROPERTIES = 2000;
   private static final String NAMESPACE = "urn:samm:org.eclipse.esmf.performance:1.0.0#";
   private static final URI SHARED_FILE = URI.create( "inmemory:/performance/shared.ttl" );

   @Test
   @ExpectMaxHeapAllocation( value = 3,
      unit = AllocationUnit.GIGA_BYTE )
   @ExpectMaxExecutionTime( seconds = 20 )
   void testLoadingCurrentSammVersion() {
      loadAndVerify( KnownVersion.getLatest() );
   }

   @Test
   @ExpectMaxHeapAllocation( value = 3,
      unit = AllocationUnit.GIGA_BYTE )
   @ExpectMaxExecutionTime( seconds = 20 )
   void testLoadingOldSammVersionWithMigration() {
      loadAndVerify( KnownVersion.SAMM_2_1_0 );
   }

   private void loadAndVerify( final KnownVersion version ) {
      final LargeModelFixture fixture = new LargeModelFixture( version );
      final AspectModel model = new AspectModelLoader( fixture ).loadUrns( fixture.aspectUrns );

      assertThat( model.aspects() ).hasSize( ASPECTS );
      assertThat( model.elements() ).hasSize( ASPECTS + PROPERTIES + 1 );
      assertThat( model.files() ).hasSize( ASPECTS + 1 );
      assertThat( fixture.resolutionCounts ).hasSize( ASPECTS + 1 );
      assertThat( fixture.resolutionCounts.values() ).containsOnly( 1 );
      assertThat( fixture.resolutionCounts.get( SHARED_FILE ) ).isEqualTo( 1 );

      final List<Property> sharedProperties = model.aspect().getProperties();
      assertThat( sharedProperties ).hasSize( PROPERTIES );
      for ( final Aspect aspect : model.aspects() ) {
         assertThat( aspect.getProperties() ).hasSize( PROPERTIES );
         for ( int i = 0; i < PROPERTIES; i++ ) {
            assertSame( sharedProperties.get( i ), aspect.getProperties().get( i ),
                  "Shared properties must not be instantiated again for each Aspect" );
         }
      }

      final String currentMetaModel = metaModelNamespace( KnownVersion.getLatest() );
      for ( final AspectModelFile file : model.files() ) {
         assertThat( file.sourceModel().getNsPrefixURI( "samm" ) ).isEqualTo( currentMetaModel );
      }
      assertThat( model.mergedModel().contains( model.mergedModel().createResource( NAMESPACE + "Aspect0" ),
            RDF.type, model.mergedModel().createResource( currentMetaModel + "Aspect" ) ) ).isTrue();
      if ( version != KnownVersion.getLatest() ) {
         assertThat( model.mergedModel().containsResource(
               model.mergedModel().createResource( metaModelNamespace( version ) + "Aspect" ) ) ).isFalse();
      }
   }

   private static String metaModelNamespace( final KnownVersion version ) {
      return "urn:samm:org.eclipse.esmf.samm:meta-model:" + version.toVersionString() + "#";
   }

   private record ModelSource(
         URI location, String turtle
   ) {}

   private static class LargeModelFixture implements ResolutionStrategyWithDefaultModelSource {
      private final Map<AspectModelUrn, ModelSource> sources = new HashMap<>();
      private final List<AspectModelUrn> aspectUrns = new ArrayList<>();
      private final Map<URI, Integer> resolutionCounts = new HashMap<>();

      private LargeModelFixture( final KnownVersion version ) {
         final String prefixes = """
            @prefix : <urn:samm:org.eclipse.esmf.performance:1.0.0#> .
            @prefix samm: <%s> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            """.formatted( metaModelNamespace( version ) );
         final StringBuilder shared = new StringBuilder( prefixes );
         shared.append( ":Text a samm:Characteristic ; samm:dataType xsd:string .\n" );
         final StringBuilder propertyList = new StringBuilder();
         for ( int i = 0; i < PROPERTIES; i++ ) {
            shared.append( ":p" ).append( i ).append( " a samm:Property ; samm:characteristic :Text .\n" );
            propertyList.append( " :p" ).append( i );
         }
         final ModelSource sharedSource = new ModelSource( SHARED_FILE, shared.toString() );
         sources.put( AspectModelUrn.fromUrn( NAMESPACE + "Text" ), sharedSource );
         for ( int i = 0; i < PROPERTIES; i++ ) {
            sources.put( AspectModelUrn.fromUrn( NAMESPACE + "p" + i ), sharedSource );
         }
         for ( int i = 0; i < ASPECTS; i++ ) {
            final AspectModelUrn urn = AspectModelUrn.fromUrn( NAMESPACE + "Aspect" + i );
            final String turtle = prefixes + ":Aspect" + i + " a samm:Aspect ; samm:properties (" + propertyList
                  + " ) ; samm:operations () ; samm:events () .\n";
            sources.put( urn, new ModelSource( URI.create( "inmemory:/performance/Aspect" + i + ".ttl" ), turtle ) );
            aspectUrns.add( urn );
         }
      }

      @Override
      public AspectModelFile apply( final AspectModelUrn urn, final ResolutionStrategySupport support ) {
         final ModelSource source = sources.get( urn );
         if ( source == null ) {
            throw new ResourceDefinitionNotFoundException( getClass().getSimpleName(), urn.toString() );
         }
         resolutionCounts.merge( source.location(), 1, Integer::sum );
         // Deliberately do not cache: repeated resolution must be prevented by the loader itself.
         return AspectModelFileLoader.load( source.turtle(), source.location() );
      }
   }
}
