/* Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0 */
package org.eclipse.esmf.turtle.languageserver.graphical.render;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewAttributeTarget;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderWarning;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewTarget;
import org.eclipse.esmf.turtle.languageserver.graphical.source.GraphicalViewSourceSnapshot;
import org.eclipse.esmf.turtle.languageserver.graphical.validation.GraphicalViewSvgSidecarValidator;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;

import org.junit.jupiter.api.Test;

class GraphicalViewRendererTest {
   private static final String URI_VALUE = "file:///tmp/graphical/graphical-renderer.ttl";
   private static final String SEE = "urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#see";

   @Test
   void rendersHeadersLocalizedRowsAndWrappedAggregatedTargetsFromSnapshot() {
      final var result = renderer().render( snapshot( model() ), URI_VALUE, true );

      assertThat( result.warnings() ).isEmpty();
      assertThat( result.svg() ).contains( "preferredName&#160;[de]:&#160;Darstellung",
            "preferredName&#160;[en]:&#160;Render", "description&#160;[de]:&#160;Beschreibung",
            "description&#160;[en]:&#160;Description" );
      assertThat( result.targets() ).anyMatch( GraphicalViewTarget.class::isInstance )
            .anyMatch( GraphicalViewAttributeTarget.class::isInstance );

      final List<GraphicalViewAttributeTarget> seeTargets = result.targets().stream()
            .filter( GraphicalViewAttributeTarget.class::isInstance ).map( GraphicalViewAttributeTarget.class::cast )
            .filter( target -> SEE.equals( target.predicateUrn() ) ).toList();
      assertThat( seeTargets ).hasSizeGreaterThan( 1 ).extracting( GraphicalViewAttributeTarget::id ).doesNotHaveDuplicates();
      assertThat( seeTargets ).allMatch( target -> target.ownerUrn().equals( "urn:samm:example.render:1.0.0#Render" )
            && target.selection().equals( "predicateStart" ) && target.language() == null );
   }

   @Test
   void omittedAttributeRowsKeepAllLocalizedContentButReturnHeaderTargetsOnly() {
      final var result = renderer().render( snapshot( model() ), URI_VALUE, false );

      assertThat( result.warnings() ).isEmpty();
      assertThat( result.svg() ).contains( "preferredName&#160;[de]:&#160;Darstellung",
            "preferredName&#160;[en]:&#160;Render" ).doesNotContain( "gv-attribute-" );
      assertThat( result.targets() ).isNotEmpty().allMatch( GraphicalViewTarget.class::isInstance );
   }

   @Test
   void oneThousandOneBoxesReturnExplicitModelTooLargeWithoutSvg() {
      final var result = renderer().render( snapshot( modelWithBoxes( 1_001 ) ), URI_VALUE, false );

      assertThat( result.svg() ).isNull();
      assertThat( result.targets() ).isEmpty();
      assertThat( result.warnings() ).containsExactly( GraphicalViewRenderWarning.MODEL_TOO_LARGE );
   }

   private static GraphicalViewRenderer renderer() {
      return new GraphicalViewRenderer( new ResolutionStrategyService(), new GraphicalViewSvgSidecarValidator() );
   }

   private static GraphicalViewSourceSnapshot snapshot( final String content ) {
      return new GraphicalViewSourceSnapshot( URI.create( URI_VALUE ), content );
   }

   private static String model() {
      return """
            @prefix : <urn:samm:example.render:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            :Render a samm:Aspect ;
               samm:preferredName "Render"@en ;
               samm:preferredName "Darstellung"@de ;
               samm:description "Description"@en ;
               samm:description "Beschreibung"@de ;
               samm:see <https://example.test/reference/with/a/very/long/path/that/wraps>,
                  <urn:irdi:0173:1:02:AAO677:003> ;
               samm:properties () ;
               samm:operations () .
            """;
   }

   private static String modelWithBoxes( final int boxCount ) {
      final StringBuilder properties = new StringBuilder();
      final StringBuilder definitions = new StringBuilder();
      for ( int index = 1; index <= boxCount - 2; index++ ) {
         properties.append( " :property" ).append( index );
         definitions.append( ":property" ).append( index )
               .append( " a samm:Property ; samm:characteristic samm-c:Text .\n" );
      }
      return """
            @prefix : <urn:samm:example.render.limit:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
            :Limit a samm:Aspect ; samm:properties (%s ); samm:operations () .
            %s
            """.formatted( properties, definitions );
   }
}
