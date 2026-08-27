/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.aspectmodel.generator.diagram;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

class AspectModelDiagramAttributeNavigationTest {
   private static final String MODEL_NAMESPACE = "urn:samm:org.eclipse.esmf.attribute:1.0.0#";
   private static final String SAMM = "urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#";
   private static final String SAMMC = "urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#";
   private static final String MODEL = """
         @prefix : <urn:samm:org.eclipse.esmf.attribute:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
         @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

         :AttributeAspect a samm:Aspect ;
            samm:preferredName "Attribute Aspect"@en ;
            samm:preferredName "Attributaspekt"@de ;
            samm:description "A deliberately long English description that wraps into more than one physical diagram row for navigation"@en ;
            samm:see <urn:irdi:0173:1:02:AAO677:002>, <urn:irdi:0173:1:02:AAO677:003> ;
            samm:properties ( :enumProperty :stateProperty :measurementProperty :lengthProperty ) ;
            samm:operations () .

         :enumProperty a samm:Property ; samm:characteristic :Enumeration .
         :Enumeration a samm-c:Enumeration ; samm:dataType xsd:string ; samm-c:values ( "ON" "OFF" ) .

         :stateProperty a samm:Property ; samm:characteristic :State .
         :State a samm-c:State ; samm:dataType xsd:string ;
            samm-c:values ( "ON" "OFF" ) ; samm-c:defaultValue "ON" .

         :measurementProperty a samm:Property ; samm:characteristic :Measurement .
         :Measurement a samm-c:Measurement ; samm:dataType xsd:decimal ; samm-c:unit :CustomUnit .
         :CustomUnit a samm:Unit ; samm:symbol "widgets" .

         :lengthProperty a samm:Property ; samm:characteristic :LengthTrait .
         :LengthTrait a samm-c:Trait ; samm-c:baseCharacteristic samm-c:Text ; samm-c:constraint :NamedLengthConstraint .
         :NamedLengthConstraint a samm-c:LengthConstraint ;
            samm-c:minValue "1"^^xsd:nonNegativeInteger ;
            samm-c:maxValue "5"^^xsd:nonNegativeInteger .
         """;

   @Test
   void optInRowsCarryDirectLanguageAggregatedAndWrappedLocatorsOnWholeCells() throws Exception {
      final DiagramNavigationResult result = generator().generateSvgWithNavigationMetadata( true );
      final Document svg = parseSvg( result.svg() );

      assertThat( result.attributeNavigationTargets() ).isNotEmpty();
      assertThat( result.attributeNavigationTargets() ).extracting( DiagramAttributeNavigationTarget::id )
            .allMatch( id -> id.matches( "gv-attribute-[a-z0-9]{16,32}" ) ).doesNotHaveDuplicates();
      assertThat( markerIds( svg ) ).containsExactlyInAnyOrderElementsOf(
            result.attributeNavigationTargets().stream().map( DiagramAttributeNavigationTarget::id ).toList() );
      result.attributeNavigationTargets().forEach( target -> assertWholeCellGroup( svg, target.id() ) );

      assertOne( result, target -> target.ownerUrn().equals( MODEL_NAMESPACE + "AttributeAspect" )
            && target.predicateUrn().equals( SAMM + "preferredName" )
            && target.selection().equals( "singleOccurrence" ) && "en".equals( target.language() ) );
      assertThat( result.attributeNavigationTargets() ).noneMatch( target -> "de".equals( target.language() ) );
      assertOne( result, target -> target.ownerUrn().equals( MODEL_NAMESPACE + "Enumeration" )
            && target.predicateUrn().equals( SAMM + "dataType" ) && target.selection().equals( "singleOccurrence" ) );
      assertOne( result, target -> target.ownerUrn().equals( MODEL_NAMESPACE + "State" )
            && target.predicateUrn().equals( SAMMC + "defaultValue" ) && target.selection().equals( "singleOccurrence" ) );
      assertOne( result, target -> target.ownerUrn().equals( MODEL_NAMESPACE + "CustomUnit" )
            && target.predicateUrn().equals( SAMM + "symbol" ) && target.selection().equals( "singleOccurrence" ) );
      assertOne( result, target -> target.ownerUrn().equals( MODEL_NAMESPACE + "NamedLengthConstraint" )
            && target.predicateUrn().equals( SAMMC + "minValue" ) && target.selection().equals( "singleOccurrence" ) );

      final List<DiagramAttributeNavigationTarget> wrappedSee = matching( result,
            target -> target.ownerUrn().equals( MODEL_NAMESPACE + "AttributeAspect" )
                  && target.predicateUrn().equals( SAMM + "see" ) );
      assertThat( wrappedSee ).hasSizeGreaterThan( 1 ).extracting( DiagramAttributeNavigationTarget::id ).doesNotHaveDuplicates();
      assertThat( wrappedSee )
            .allMatch( target -> target.selection().equals( "predicateStart" ) && target.language() == null );
      assertThat( wrappedSee ).extracting( target -> List.of( target.ownerUrn(), target.predicateUrn(), target.selection() ) )
            .containsOnly( List.of( MODEL_NAMESPACE + "AttributeAspect", SAMM + "see", "predicateStart" ) );
      assertThat( matching( result, target -> target.ownerUrn().equals( MODEL_NAMESPACE + "Enumeration" )
            && target.predicateUrn().equals( SAMMC + "values" ) ) )
            .allMatch( target -> target.selection().equals( "predicateStart" ) );

      final List<DiagramAttributeNavigationTarget> wrapped = matching( result,
            target -> target.ownerUrn().equals( MODEL_NAMESPACE + "AttributeAspect" )
                  && target.predicateUrn().equals( SAMM + "description" ) );
      assertThat( wrapped ).hasSizeGreaterThan( 1 ).extracting( DiagramAttributeNavigationTarget::id ).doesNotHaveDuplicates();
      assertThat( wrapped ).extracting( target -> List.of( target.ownerUrn(), target.predicateUrn(), target.selection(), target.language() ) )
            .containsOnly( List.of( MODEL_NAMESPACE + "AttributeAspect", SAMM + "description", "singleOccurrence", "en" ) );
   }

   @Test
   void omittedOrFalseAttributeOptInAndDefaultOutputsRemainAttributeMetadataFree() {
      final DiagramNavigationResult omitted = generator().generateSvgWithNavigationMetadata();
      final DiagramNavigationResult explicitFalse = generator().generateSvgWithNavigationMetadata( false );
      final String defaultSvg = new String( generator().getContent(), StandardCharsets.UTF_8 );

      assertThat( omitted.attributeNavigationTargets() ).isEmpty();
      assertThat( explicitFalse.attributeNavigationTargets() ).isEmpty();
      assertThat( omitted.svg() ).doesNotContain( "gv-attribute-" );
      assertThat( explicitFalse.svg() ).doesNotContain( "gv-attribute-" );
      assertThat( defaultSvg ).doesNotContain( "gv-header-", "gv-attribute-" );
   }

   @Test
   void entityInstanceAssertionsUsePropertyPredicatesAndAggregateCollectionsUsePredicateStart() {
      final DiagramNavigationResult scalar = generator(
            TestResources.load( TestAspect.ASPECT_WITH_ENTITY_INSTANCE_WITH_SCALAR_PROPERTIES ).aspect() )
                  .generateSvgWithNavigationMetadata( true );
      final DiagramNavigationResult collection = generator(
            TestResources.load( TestAspect.ASPECT_WITH_ENTITY_INSTANCE_WITH_SCALAR_LIST_PROPERTY ).aspect() )
                  .generateSvgWithNavigationMetadata( true );
      final String namespace = "urn:samm:org.eclipse.esmf.test:1.0.0#";

      assertOne( scalar, target -> target.ownerUrn().equals( namespace + "TestEntityInstance" )
            && target.predicateUrn().equals( namespace + "code" ) && target.selection().equals( "singleOccurrence" ) );
      assertOne( scalar, target -> target.ownerUrn().equals( namespace + "TestEntityInstance" )
            && target.predicateUrn().equals( namespace + "description" ) && target.selection().equals( "singleOccurrence" ) );
      assertOne( collection, target -> target.ownerUrn().equals( namespace + "TestEntityInstance" )
            && target.predicateUrn().equals( namespace + "testList" ) && target.selection().equals( "predicateStart" ) );
   }

   @Test
   void anonymousConstraintRowsRemainInert() {
      final DiagramNavigationResult result = generator( TestResources.load( TestAspect.ASPECT_WITH_LENGTH_CONSTRAINT ).aspect() )
            .generateSvgWithNavigationMetadata( true );

      assertThat( result.attributeNavigationTargets() )
            .noneMatch( target -> target.predicateUrn().equals( SAMMC + "minValue" )
                  || target.predicateUrn().equals( SAMMC + "maxValue" ) );
   }

   @Test
   void entryCellIdsIntroduceNoLinksLayoutOrPngDelta() throws Exception {
      final AspectModelDiagramGenerator generator = generator( """
            @prefix : <urn:samm:org.eclipse.esmf.attribute:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

            :AttributeAspect a samm:Aspect ;
               samm:preferredName "Attribute Aspect"@en ;
               samm:description "A deliberately long English description that wraps into more than one physical diagram row for navigation"@en ;
               samm:properties () ;
               samm:operations () .
            """ );
      final String headerOnly = generator.generateSvgWithNavigationMetadata( false ).svg();
      final String withRows = generator.generateSvgWithNavigationMetadata( true ).svg();
      assertThat( withRows ).doesNotContain( "href=", "xlink:href=" );
      assertThat( normalizedStructure( parseSvg( withRows ) ) ).isEqualTo( normalizedStructure( parseSvg( headerOnly ) ) );
      assertThat( transcodePng( withRows ) ).isEqualTo( transcodePng( headerOnly ) );
   }

   private static AspectModelDiagramGenerator generator() {
      return generator( MODEL );
   }

   private static AspectModelDiagramGenerator generator( final String model ) {
      final Aspect aspect = new AspectModelLoader().load( model, URI.create( "file:///attribute-navigation.ttl" ) ).aspect();
      return generator( aspect );
   }

   private static AspectModelDiagramGenerator generator( final Aspect aspect ) {
      final DiagramGenerationConfig config = DiagramGenerationConfigBuilder.builder()
            .format( DiagramGenerationConfig.Format.SVG ).language( Locale.ENGLISH ).build();
      return new AspectModelDiagramGenerator( aspect, config );
   }

   private static void assertOne( final DiagramNavigationResult result, final Predicate<DiagramAttributeNavigationTarget> predicate ) {
      assertThat( matching( result, predicate ) ).hasSize( 1 );
   }

   private static List<DiagramAttributeNavigationTarget> matching( final DiagramNavigationResult result,
         final Predicate<DiagramAttributeNavigationTarget> predicate ) {
      return result.attributeNavigationTargets().stream().filter( predicate ).toList();
   }

   private static Document parseSvg( final String svg ) throws Exception {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setFeature( "http://apache.org/xml/features/disallow-doctype-decl", true );
      factory.setFeature( "http://xml.org/sax/features/external-general-entities", false );
      factory.setFeature( "http://xml.org/sax/features/external-parameter-entities", false );
      factory.setAttribute( XMLConstants.ACCESS_EXTERNAL_DTD, "" );
      factory.setAttribute( XMLConstants.ACCESS_EXTERNAL_SCHEMA, "" );
      return factory.newDocumentBuilder().parse( new InputSource( new StringReader( svg ) ) );
   }

   private static Set<String> markerIds( final Document svg ) {
      final Set<String> result = new java.util.LinkedHashSet<>();
      final NodeList nodes = svg.getElementsByTagName( "*" );
      for ( int i = 0; i < nodes.getLength(); i++ ) {
         final String id = ( (Element) nodes.item( i ) ).getAttribute( "id" );
         if ( id.matches( "gv-attribute-[a-z0-9]{16,32}" ) ) result.add( id );
      }
      return result;
   }

   private static void assertWholeCellGroup( final Document svg, final String id ) {
      final Element group = elementWithId( svg, id );
      assertThat( group.getTagName() ).isEqualTo( "g" );
      assertThat( childNames( group ) ).contains( "polygon", "text" ).doesNotContain( "a", "path" );
   }

   private static Element elementWithId( final Document svg, final String id ) {
      final NodeList nodes = svg.getElementsByTagName( "*" );
      for ( int i = 0; i < nodes.getLength(); i++ ) {
         final Element element = (Element) nodes.item( i );
         if ( id.equals( element.getAttribute( "id" ) ) ) return element;
      }
      throw new AssertionError( "Missing marker " + id );
   }

   private static Set<String> childNames( final Element element ) {
      final Set<String> names = new java.util.LinkedHashSet<>();
      for ( int i = 0; i < element.getChildNodes().getLength(); i++ ) {
         if ( element.getChildNodes().item( i ) instanceof final Element child ) names.add( child.getTagName() );
      }
      return names;
   }

   private static String normalizedStructure( final Document document ) {
      final StringBuilder result = new StringBuilder();
      appendNormalized( document.getDocumentElement(), result );
      return result.toString();
   }

   private static void appendNormalized( final Node node, final StringBuilder result ) {
      result.append( '<' ).append( node.getNodeName() );
      if ( node.getAttributes() != null ) {
         for ( int i = 0; i < node.getAttributes().getLength(); i++ ) {
            final Node attribute = node.getAttributes().item( i );
            if ( !"id".equals( attribute.getNodeName() ) ) {
               result.append( ' ' ).append( attribute.getNodeName() ).append( '=' ).append( attribute.getNodeValue() );
            }
         }
      }
      result.append( '>' ).append( node.getNodeValue() );
      for ( int i = 0; i < node.getChildNodes().getLength(); i++ ) appendNormalized( node.getChildNodes().item( i ), result );
   }

   private static byte[] transcodePng( final String svg ) throws Exception {
      final ByteArrayOutputStream output = new ByteArrayOutputStream();
      new PNGTranscoder().transcode( new TranscoderInput( new StringReader( svg ) ), new TranscoderOutput( output ) );
      return output.toByteArray();
   }
}
