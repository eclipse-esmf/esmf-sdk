/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.aspectmodel.generator.diagram;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator.DiagramBoxLimitExceededException;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;

import org.junit.jupiter.api.Test;

class AspectModelDiagramBoxLimitTest {
   @Test
   void exactlyOneThousandNavigationBoxesReachGraphperWithTheCountedDiagramInstance() {
      final AspectModelDiagramGenerator generator = generatorWithBoxes( 1_000 );
      final AtomicInteger graphperCalls = new AtomicInteger();
      final AtomicReference<Diagram> renderedDiagram = new AtomicReference<>();

      generator.generateSvgWithNavigationMetadata( 1_000, diagram -> {
         graphperCalls.incrementAndGet();
         renderedDiagram.set( diagram );
         return new DiagramNavigationResult( "<svg/>", Map.of() );
      } );

      assertThat( graphperCalls ).hasValue( 1 );
      assertThat( renderedDiagram.get().getBoxes() ).hasSize( 1_000 );
      assertThat( renderedDiagram.get().getBoxes() )
            .allMatch( box -> box.getHeaderMarkerId().isPresent() && box.getNavigationTargetUrn().isPresent() );
   }

   @Test
   void oneThousandOneNavigationBoxesFailBeforeGraphper() {
      final AspectModelDiagramGenerator generator = generatorWithBoxes( 1_001 );
      final AtomicInteger graphperCalls = new AtomicInteger();

      assertThatThrownBy( () -> generator.generateSvgWithNavigationMetadata( 1_000, diagram -> {
         graphperCalls.incrementAndGet();
         return new DiagramNavigationResult( "<svg/>", Map.of() );
      } ) ).isInstanceOf( DiagramBoxLimitExceededException.class );
      assertThat( graphperCalls ).hasValue( 0 );
   }

   private static AspectModelDiagramGenerator generatorWithBoxes( final int boxCount ) {
      final StringBuilder properties = new StringBuilder();
      final StringBuilder definitions = new StringBuilder();
      // The diagram consists of the Aspect, one shared Characteristic and the generated Properties.
      for ( int index = 1; index <= boxCount - 2; index++ ) {
         properties.append( " :property" ).append( index );
         definitions.append( ":property" ).append( index )
               .append( " a samm:Property ; samm:characteristic samm-c:Text .\n" );
      }
      final String model = """
            @prefix : <urn:samm:org.eclipse.esmf.diagram.limit:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .

            :LimitAspect a samm:Aspect ;
               samm:properties (%s ) ;
               samm:operations () .

            %s
            """.formatted( properties, definitions );
      final var aspect = new AspectModelLoader().load( model, URI.create( "file:/diagram-limit.ttl" ) ).aspect();
      return new AspectModelDiagramGenerator( aspect,
            DiagramGenerationConfigBuilder.builder().format( DiagramGenerationConfig.Format.SVG ).build() );
   }
}
