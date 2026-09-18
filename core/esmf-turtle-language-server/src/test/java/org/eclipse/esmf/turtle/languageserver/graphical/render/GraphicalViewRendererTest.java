/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.turtle.languageserver.graphical.render;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewAttributeTarget;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderWarning;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewTarget;
import org.eclipse.esmf.turtle.languageserver.graphical.source.GraphicalViewSourceSnapshot;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

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
      assertPassiveSvg( result.svg() );
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

   @Test
   void activeContentShapedModelValuesRemainPassiveText() {
      final var result = renderer().render( snapshot( activeContentModel() ), URI_VALUE, true );

      assertThat( result.warnings() ).isEmpty();
      assertThat( result.svg() ).contains( "&lt;/text&gt;&lt;script&gt;alert(1)&lt;/script&gt;",
            "javascript:alert(4)", "command:workbench.action.openSettings", "https://attacker.invalid/image.svg" );
      assertPassiveSvg( result.svg() );
   }

   @Test
   void xmlForbiddenControlCharacterRemainsDeferredGeneratorDefect() {
      final String model = """
         @prefix : <urn:samm:example.svg-control:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         :XmlControlCharacter a samm:Aspect ;
            samm:preferredName "Control character probe"@en ;
            samm:description "before-%s-after"@en ;
            samm:properties () ; samm:operations () .
         """.formatted( "\\" + "u0001" );

      final var result = renderer().render( snapshot( model ), URI_VALUE, true );

      assertThat( result.warnings() ).isEmpty();
      assertThat( result.svg() ).contains( "before-&#1;-after" );
      assertThat( result.targets() ).isNotEmpty();
   }

   @Test
   void validXmlWhitespacePrintableAndSupplementaryUnicodeRemainSuccessful() {
      final String model = """
         @prefix : <urn:samm:example.svg-unicode:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         :Unicode a samm:Aspect ;
            samm:preferredName "Quotes \\" ampersand & angle < > bicycle 🚲"@en ;
            samm:description "line one\\nline two\\tend"@en ;
            samm:properties () ; samm:operations () .
         """;

      final var result = renderer().render( snapshot( model ), URI_VALUE, true );

      assertThat( result.warnings() ).isEmpty();
      assertThat( result.svg() ).contains( "bicycle", "🚲" );
      assertPassiveSvg( result.svg() );
   }

   private static GraphicalViewRenderer renderer() {
      return new GraphicalViewRenderer( new ResolutionStrategyService() );
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

   private static String activeContentModel() {
      return """
         @prefix : <urn:samm:example.svg-security:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
         :SvgSecuritySpike a samm:Aspect ;
            samm:preferredName "</text><script>alert(1)</script>"@en ;
            samm:description "</text><foreignObject><body onload='alert(2)'>HTML</body></foreignObject><text style='fill:url(https://attacker.invalid/fill)' onclick='alert(3)'>"@en ;
            samm:see <javascript:alert(4)>, <command:workbench.action.openSettings>, <file:///tmp/secret.txt>,
               <https://attacker.invalid/image.svg> ;
            samm:properties ( :PayloadProperty ) ; samm:operations () .
         :PayloadProperty a samm:Property ;
            samm:preferredName "<a href='javascript:alert(5)'>click</a>"@en ;
            samm:description "url(data:text/html,attack) & <image href='https://attacker.invalid/leak'>"@en ;
            samm:characteristic samm-c:Text .
         """;
   }

   private static void assertPassiveSvg( final String svg ) {
      try {
         final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setNamespaceAware( true );
         factory.setFeature( "http://apache.org/xml/features/disallow-doctype-decl", true );
         factory.setFeature( "http://xml.org/sax/features/external-general-entities", false );
         factory.setFeature( "http://xml.org/sax/features/external-parameter-entities", false );
         factory.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false );
         try {
            factory.setAttribute( XMLConstants.ACCESS_EXTERNAL_DTD, "" );
            factory.setAttribute( XMLConstants.ACCESS_EXTERNAL_SCHEMA, "" );
         } catch ( final IllegalArgumentException unsupportedProperty ) {
            // The module may supply a parser that implements the feature flags but not these JAXP properties.
         }
         final var document = factory.newDocumentBuilder().parse( new InputSource( new StringReader( svg ) ) );
         assertThat( document.getDocumentElement().getNamespaceURI() ).isEqualTo( "http://www.w3.org/2000/svg" );
         final Set<String> allowedElements = Set.of( "svg", "g", "polygon", "path", "text", "title" );
         final Set<String> markerIds = new HashSet<>();
         final var elements = document.getElementsByTagName( "*" );
         for ( int index = 0; index < elements.getLength(); index++ ) {
            final Element element = (Element) elements.item( index );
            assertThat( allowedElements ).contains( element.getLocalName() );
            for ( int attributeIndex = 0; attributeIndex < element.getAttributes().getLength(); attributeIndex++ ) {
               final var attribute = element.getAttributes().item( attributeIndex );
               final String name = attribute.getNodeName();
               final String value = attribute.getNodeValue().toLowerCase();
               assertThat( name ).doesNotStartWith( "on" );
               assertThat( name ).isNotIn( "href", "xlink:href", "style" );
               if ( !name.startsWith( "xmlns" ) ) {
                  assertThat( value ).doesNotContain( "data:", "file:", "command:", "javascript:", "http://", "https://" );
               }
            }
            final String id = element.getAttribute( "id" );
            if ( id.matches( "gv-(?:header|attribute)-[a-z0-9]{16,32}" ) ) {
               assertThat( markerIds.add( id ) ).isTrue();
            }
         }
         assertThat( svg ).doesNotContain( "<style", " style=" );
      } catch ( final Exception exception ) {
         throw new AssertionError( "Graphical View SVG must be secure XML with the passive inventory", exception );
      }
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
