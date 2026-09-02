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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

class AspectModelDiagramNavigationMetadataTest {
   private static final Pattern HEADER_ID_PATTERN = Pattern.compile( "gv-header-[a-z0-9]{16,32}" );
   private static final URI SOURCE_URI = URI.create( "file:/diagram-navigation.ttl" );
   private static final URI PNG_SOURCE_URI = URI.create( "file:/diagram-navigation-png.ttl" );
   private static final String PNG_MODEL = """
      @prefix : <urn:samm:org.eclipse.esmf.test:2.2.0#> .
      @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

      :AspectForPng a samm:Aspect ;
         samm:properties () ;
         samm:operations () .
      """;
   private static final String ASPECT_URN = "urn:samm:org.eclipse.esmf.diagram:1.0.0#AspectWithDiagramNavigation";
   private static final String ENTITY_URN = "urn:samm:org.eclipse.esmf.diagram:1.0.0#NamedEntity";
   private static final String MODEL = """
      @prefix : <urn:samm:org.eclipse.esmf.diagram:1.0.0#> .
      @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
      @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
      @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

      :AspectWithDiagramNavigation a samm:Aspect ;
         samm:preferredName "Visible <escaped> & \\"quoted\\""@en ;
         samm:description "Description <escaped> & \\"quoted\\""@en ;
         samm:properties ( :namedProperty :anonymousEntityProperty :ambiguousEitherProperty ) ;
         samm:operations ( ) .

      :namedProperty a samm:Property ;
         samm:preferredName "Same displayed row"@en ;
         samm:characteristic samm-c:Text .

      :anonymousEntityProperty a samm:Property ;
         samm:characteristic [
            a samm-c:SingleEntity ;
            samm:dataType :NamedEntity
         ] .

      :ambiguousEitherProperty a samm:Property ;
         samm:characteristic [
            a samm-c:Either ;
            samm-c:left :LeftCharacteristic ;
            samm-c:right :RightCharacteristic
         ] .

      :NamedEntity a samm:Entity ;
         samm:properties ( :entityProperty ) .

      :entityProperty a samm:Property ;
         samm:characteristic samm-c:Text .

      :LeftCharacteristic a samm:Characteristic ;
         samm:dataType xsd:string .

      :RightCharacteristic a samm:Characteristic ;
         samm:dataType xsd:string .
      """;

   @Test
   void namedBoxMarkerIsOnTitleRowOnlyAndSidecarContainsOwnUrn() throws Exception {
      final DiagramNavigationResult result = renderWithNavigation( loadAspect( MODEL, SOURCE_URI ) );
      final Document svg = parseSvg( result.svg() );
      assertOneToOneMarkers( svg, result.navigationTargets() );

      final String marker = markerContainingText( svg, result.navigationTargets().keySet(), "AspectWithDiagramNavigation" );
      assertThat( result.navigationTargets() ).containsEntry( marker, ASPECT_URN );
      final Element markerGroup = elementWithId( svg, marker );
      assertThat( markerGroup.getTextContent() ).contains( "AspectWithDiagramNavigation" );
      assertThat( markerGroup.getTextContent() ).doesNotContain( "«Aspect»" );
      assertThat( markerTexts( svg, result.navigationTargets().keySet() ) ).noneMatch( text -> text.contains( "«Aspect»" ) );
   }

   @Test
   void eligibleAnonymousBoxMarkerIsOnPrototypeRowOnlyAndTargetsNamedReference() throws Exception {
      final DiagramNavigationResult result = renderWithNavigation( loadAspect( MODEL, SOURCE_URI ) );
      final Document svg = parseSvg( result.svg() );
      assertOneToOneMarkers( svg, result.navigationTargets() );

      final String marker = markerContainingTargetAndText( svg, result.navigationTargets(), ENTITY_URN, "«SingleEntity»" );
      assertThat( result.navigationTargets() ).containsEntry( marker, ENTITY_URN );
      final Element markerGroup = elementWithId( svg, marker );
      assertThat( markerGroup.getTextContent() ).contains( "«SingleEntity»" );
      assertThat( markerGroup.getTextContent() ).doesNotContain( "NamedEntity" );
   }

   @Test
   void anonymousElementWithoutUnambiguousNamedTargetHasNoMarker() throws Exception {
      final DiagramNavigationResult result = renderWithNavigation( loadAspect( MODEL, SOURCE_URI ) );
      final Document svg = parseSvg( result.svg() );
      assertOneToOneMarkers( svg, result.navigationTargets() );

      assertThat( markerTexts( svg, result.navigationTargets().keySet() ) )
            .doesNotContain( "«Either»" );
   }

   @Test
   void attributesSeparatorsEdgesArrowheadsAndEdgeLabelsHaveNoMarkersOrLinks() throws Exception {
      final DiagramNavigationResult result = renderWithNavigation( loadAspect( MODEL, SOURCE_URI ) );
      final Document svg = parseSvg( result.svg() );
      assertOneToOneMarkers( svg, result.navigationTargets() );

      assertNoHrefAttributes( svg );
      assertHeaderMarkerGroups( svg, result.navigationTargets().keySet() );
      assertThat( markerTexts( svg, result.navigationTargets().keySet() ) )
            .noneMatch( text -> text.contains( "preferredName" ) )
            .noneMatch( text -> text.contains( "description" ) )
            .noneMatch( text -> text.contains( "characteristic" ) )
            .noneMatch( text -> text.contains( "dataType" ) )
            .noneMatch( text -> text.contains( "left" ) )
            .noneMatch( text -> text.contains( "right" ) );
   }

   @Test
   void duplicateVisibleTitlesReceiveDistinctOpaqueMarkers() throws Exception {
      final Aspect aspect = loadMultiFileAspect();

      final DiagramNavigationResult result = renderWithNavigation( aspect );
      final Document svg = parseSvg( result.svg() );
      assertOneToOneMarkers( svg, result.navigationTargets() );

      final List<String> sharedMarkers = result.navigationTargets().keySet().stream()
            .filter( marker -> elementWithId( svg, marker ).getTextContent().contains( "SharedCharacteristic" ) )
            .toList();
      assertThat( sharedMarkers ).hasSize( 2 ).doesNotHaveDuplicates();
      assertThat( sharedMarkers.stream().map( result.navigationTargets()::get ) )
            .containsExactlyInAnyOrder(
                  "urn:samm:org.eclipse.esmf.diagram.one:1.0.0#SharedCharacteristic",
                  "urn:samm:org.eclipse.esmf.diagram.two:1.0.0#SharedCharacteristic" );
   }

   @Test
   void markersFollowGrammarAndAreUniqueAcrossRenders() {
      final Aspect aspect = loadAspect( MODEL, SOURCE_URI );

      final DiagramNavigationResult first = renderWithNavigation( aspect );
      final DiagramNavigationResult second = renderWithNavigation( aspect );

      assertThat( first.navigationTargets().keySet() ).allMatch( marker -> HEADER_ID_PATTERN.matcher( marker ).matches() );
      assertThat( second.navigationTargets().keySet() ).allMatch( marker -> HEADER_ID_PATTERN.matcher( marker ).matches() );
      assertThat( first.navigationTargets().keySet() ).doesNotContainAnyElementsOf( second.navigationTargets().keySet() );
   }

   @Test
   void xmlEscapingForModelControlledTextIsPreserved() throws Exception {
      final DiagramNavigationResult result = renderWithNavigation( loadAspect( MODEL, SOURCE_URI ) );
      final Document svg = parseSvg( result.svg() );

      assertThat( result.svg() ).contains( "Visible&#160;&lt;escaped&gt;&#160;&amp;&#160;&quot;quoted&quot;" );
      assertThat( result.svg() ).contains( "Description&#160;&lt;escaped&gt;&#160;&amp;&#160;&quot;quoted&quot;" );
      final String textContent = svg.getDocumentElement().getTextContent().replace( '\u00a0', ' ' );
      assertThat( textContent ).contains( "Visible <escaped> & \"quoted\"" );
      assertThat( textContent ).contains( "Description <escaped> & \"quoted\"" );
   }

   @Test
   void defaultGenerationHasNoNavigationMetadataOrLinksOrSourceData() throws Exception {
      final Aspect aspect = loadAspect( MODEL, SOURCE_URI );
      final byte[] content = new AspectModelDiagramGenerator( aspect, svgConfig() ).getContent();
      final byte[] png = new AspectModelDiagramGenerator( aspect, pngConfig() ).getContent();
      final String svg = new String( content, StandardCharsets.UTF_8 );
      final String pngBytes = new String( png, StandardCharsets.ISO_8859_1 );
      final Document document = parseSvg( svg );

      assertThat( svg ).doesNotContain( "gv-header-" )
            .doesNotContain( SOURCE_URI.toString() )
            .doesNotContain( ASPECT_URN )
            .doesNotContain( ENTITY_URN );
      assertThat( pngBytes ).doesNotContain( "gv-header-" )
            .doesNotContain( ASPECT_URN )
            .doesNotContain( SOURCE_URI.toString() );
      assertNoHrefAttributes( document );
   }

   @Test
   void defaultAndOptInSvgDifferOnlyByNavigationIdsAndProduceIdenticalPng() throws Exception {
      final Aspect aspect = loadAspect( PNG_MODEL, PNG_SOURCE_URI );
      final String defaultSvg = new String( new AspectModelDiagramGenerator( aspect, svgConfig() ).getContent(),
            StandardCharsets.UTF_8 );
      final String optInSvg = renderWithNavigation( aspect ).svg();

      assertThat( defaultSvg ).doesNotContain( "gv-header-" );
      assertThat( optInSvg ).contains( "gv-header-" );
      assertEquivalentExceptNavigationMarkerIds( parseSvg( defaultSvg ), parseSvg( optInSvg ) );
      assertThat( transcodePng( optInSvg ) ).isEqualTo( transcodePng( defaultSvg ) );
   }

   private static DiagramNavigationResult renderWithNavigation( final Aspect aspect ) {
      return new AspectModelDiagramGenerator( aspect, svgConfig() ).generateSvgWithNavigationMetadata();
   }

   private static DiagramGenerationConfig svgConfig() {
      return DiagramGenerationConfigBuilder.builder()
            .format( DiagramGenerationConfig.Format.SVG )
            .language( Locale.ENGLISH )
            .build();
   }

   private static DiagramGenerationConfig pngConfig() {
      return DiagramGenerationConfigBuilder.builder()
            .format( DiagramGenerationConfig.Format.PNG )
            .language( Locale.ENGLISH )
            .build();
   }

   private static Aspect loadAspect( final String model, final URI sourceUri ) {
      return new AspectModelLoader().load( model, sourceUri ).aspect();
   }

   private static Aspect loadMultiFileAspect() {
      final AspectModelFile main = AspectModelFileLoader.load( """
         @prefix : <urn:samm:org.eclipse.esmf.diagram.main:1.0.0#> .
         @prefix one: <urn:samm:org.eclipse.esmf.diagram.one:1.0.0#> .
         @prefix two: <urn:samm:org.eclipse.esmf.diagram.two:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

         :AspectWithDuplicateTitles a samm:Aspect ;
            samm:properties ( :propertyOne :propertyTwo ) ;
            samm:operations ( ) .

         :propertyOne a samm:Property ;
            samm:characteristic one:SharedCharacteristic .

         :propertyTwo a samm:Property ;
            samm:characteristic two:SharedCharacteristic .
         """, URI.create( "file:/diagram-navigation-main.ttl" ) );
      final AspectModelFile one = AspectModelFileLoader.load( """
         @prefix : <urn:samm:org.eclipse.esmf.diagram.one:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
         @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

         :SharedCharacteristic a samm:Characteristic ;
            samm:dataType xsd:string .
         """, URI.create( "file:/diagram-navigation-one.ttl" ) );
      final AspectModelFile two = AspectModelFileLoader.load( """
         @prefix : <urn:samm:org.eclipse.esmf.diagram.two:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
         @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

         :SharedCharacteristic a samm:Characteristic ;
            samm:dataType xsd:string .
         """, URI.create( "file:/diagram-navigation-two.ttl" ) );
      final AspectModel aspectModel = new AspectModelLoader().loadAspectModelFiles( List.of( main, one, two ) );
      return aspectModel.aspect();
   }

   private static Document parseSvg( final String svg ) throws Exception {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setFeature( "http://apache.org/xml/features/disallow-doctype-decl", true );
      factory.setFeature( "http://xml.org/sax/features/external-general-entities", false );
      factory.setFeature( "http://xml.org/sax/features/external-parameter-entities", false );
      factory.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false );
      factory.setAttribute( XMLConstants.ACCESS_EXTERNAL_DTD, "" );
      factory.setAttribute( XMLConstants.ACCESS_EXTERNAL_SCHEMA, "" );
      return factory.newDocumentBuilder().parse( new InputSource( new StringReader( svg ) ) );
   }

   private static void assertOneToOneMarkers( final Document svg, final Map<String, String> sidecar ) {
      final List<String> markerOccurrences = markerOccurrences( svg );
      assertThat( markerOccurrences ).doesNotHaveDuplicates();
      final Set<String> markerIds = new LinkedHashSet<>( markerOccurrences );
      assertThat( markerIds ).containsExactlyInAnyOrderElementsOf( sidecar.keySet() );
      assertThat( sidecar ).hasSize( markerIds.size() );
   }

   private static List<String> markerOccurrences( final Document svg ) {
      final List<String> markerIds = new ArrayList<>();
      final NodeList nodes = svg.getElementsByTagName( "*" );
      for ( int i = 0; i < nodes.getLength(); i++ ) {
         final Node id = nodes.item( i ).getAttributes().getNamedItem( "id" );
         if ( id != null && HEADER_ID_PATTERN.matcher( id.getNodeValue() ).matches() ) {
            markerIds.add( id.getNodeValue() );
         }
      }
      return markerIds;
   }

   private static void assertHeaderMarkerGroups( final Document svg, final Set<String> markers ) {
      markers.forEach( marker -> {
         final Element markerGroup = elementWithId( svg, marker );
         final Set<String> childElementNames = new LinkedHashSet<>();
         final NodeList children = markerGroup.getChildNodes();
         for ( int i = 0; i < children.getLength(); i++ ) {
            if ( children.item( i ) instanceof final Element child ) {
               childElementNames.add( child.getTagName() );
            }
         }
         assertThat( markerGroup.getTagName() ).isEqualTo( "g" );
         assertThat( childElementNames ).contains( "polygon", "text" ).doesNotContain( "path", "a" );
         assertThat( markerGroup.getTextContent() ).isNotBlank();
      } );
   }

   private static void assertEquivalentExceptNavigationMarkerIds( final Node defaultNode, final Node optInNode ) {
      assertThat( optInNode.getNodeType() ).isEqualTo( defaultNode.getNodeType() );
      assertThat( optInNode.getNodeName() ).isEqualTo( defaultNode.getNodeName() );
      assertThat( optInNode.getNodeValue() ).isEqualTo( defaultNode.getNodeValue() );

      final NamedNodeMap defaultAttributes = defaultNode.getAttributes();
      final NamedNodeMap optInAttributes = optInNode.getAttributes();
      if ( defaultAttributes != null || optInAttributes != null ) {
         final Set<String> attributeNames = new LinkedHashSet<>();
         addAttributeNames( attributeNames, defaultAttributes );
         addAttributeNames( attributeNames, optInAttributes );
         attributeNames.forEach( attributeName -> {
            final String defaultValue = attributeValue( defaultAttributes, attributeName );
            final String optInValue = attributeValue( optInAttributes, attributeName );
            if ( "id".equals( attributeName ) && ( isNavigationMarkerId( defaultValue ) || isNavigationMarkerId( optInValue ) ) ) {
               return;
            }
            assertThat( optInValue ).isEqualTo( defaultValue );
         } );
      }

      final NodeList defaultChildren = defaultNode.getChildNodes();
      final NodeList optInChildren = optInNode.getChildNodes();
      assertThat( optInChildren.getLength() ).isEqualTo( defaultChildren.getLength() );
      for ( int i = 0; i < defaultChildren.getLength(); i++ ) {
         assertEquivalentExceptNavigationMarkerIds( defaultChildren.item( i ), optInChildren.item( i ) );
      }
   }

   private static void addAttributeNames( final Set<String> attributeNames, final NamedNodeMap attributes ) {
      if ( attributes == null ) {
         return;
      }
      for ( int i = 0; i < attributes.getLength(); i++ ) {
         attributeNames.add( attributes.item( i ).getNodeName() );
      }
   }

   private static String attributeValue( final NamedNodeMap attributes, final String attributeName ) {
      if ( attributes == null || attributes.getNamedItem( attributeName ) == null ) {
         return null;
      }
      return attributes.getNamedItem( attributeName ).getNodeValue();
   }

   private static boolean isNavigationMarkerId( final String id ) {
      return id != null && id.startsWith( "gv-header-" );
   }

   private static Element elementWithId( final Document svg, final String id ) {
      final NodeList nodes = svg.getElementsByTagName( "*" );
      for ( int i = 0; i < nodes.getLength(); i++ ) {
         final Element element = (Element) nodes.item( i );
         if ( id.equals( element.getAttribute( "id" ) ) ) {
            return element;
         }
      }
      throw new AssertionError( "No SVG element found with id " + id );
   }

   private static String markerContainingText( final Document svg, final Set<String> markers, final String text ) {
      return markers.stream()
            .filter( marker -> elementWithId( svg, marker ).getTextContent().contains( text ) )
            .findFirst()
            .orElseThrow();
   }

   private static String markerContainingTargetAndText( final Document svg, final Map<String, String> sidecar, final String target,
         final String text ) {
      final List<String> matches = sidecar.entrySet().stream()
            .filter( entry -> entry.getValue().equals( target ) )
            .map( Map.Entry::getKey )
            .filter( marker -> elementWithId( svg, marker ).getTextContent().contains( text ) )
            .toList();
      assertThat( matches ).hasSize( 1 );
      return matches.getFirst();
   }

   private static List<String> markerTexts( final Document svg, final Set<String> markers ) {
      return markers.stream().map( marker -> elementWithId( svg, marker ).getTextContent() ).toList();
   }

   private static void assertNoHrefAttributes( final Document svg ) {
      final NodeList nodes = svg.getElementsByTagName( "*" );
      for ( int i = 0; i < nodes.getLength(); i++ ) {
         final NamedNodeMap attributes = nodes.item( i ).getAttributes();
         for ( int j = 0; j < attributes.getLength(); j++ ) {
            final Node attribute = attributes.item( j );
            assertThat( attribute.getNodeName() ).isNotEqualTo( "href" ).isNotEqualTo( "xlink:href" );
         }
      }
   }

   private static byte[] transcodePng( final String svg ) throws Exception {
      final ByteArrayOutputStream output = new ByteArrayOutputStream();
      final PNGTranscoder transcoder = new PNGTranscoder();
      transcoder.transcode( new TranscoderInput( new StringReader( svg ) ), new TranscoderOutput( output ) );
      return output.toByteArray();
   }
}
