/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.aspectmodel.generator.diagram;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import org.junit.jupiter.api.Test;

class AspectModelDiagramMultilingualRowsTest {
   private static final String PREFIXES = """
         @prefix : <urn:samm:org.eclipse.esmf.multilingual:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
         @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
         """;

   @Test
   void graphicalRowsContainEveryLocalizedValueInDeterministicLanguageOrder() {
      final DiagramNavigationResult result = graphicalResult( PREFIXES + """
            :MultilingualAspect a samm:Aspect ;
               samm:description "English description"@en ;
               samm:preferredName "US name"@en-US ;
               samm:preferredName "Deutscher Name"@de ;
               samm:description "Deutsche Beschreibung"@de ;
               samm:preferredName "English name"@en ;
               samm:description "US description"@en-US ;
               samm:properties () ;
               samm:operations () .
            """ );

      assertInOrder( result.svg(),
            "preferredName&#160;[de]:&#160;Deutscher&#160;Name",
            "preferredName&#160;[en]:&#160;English&#160;name",
            "preferredName&#160;[en-US]:&#160;US&#160;name",
            "description&#160;[de]:&#160;Deutsche&#160;Beschreibung",
            "description&#160;[en]:&#160;English&#160;description",
            "description&#160;[en-US]:&#160;US&#160;description" );
      assertThat( localizedTargets( result ) ).extracting( DiagramAttributeNavigationTarget::language )
            .containsExactly( "de", "en", "en-us", "de", "en", "en-us" );
   }

   @Test
   void graphicalRowsDoNotRequireEnglishAndIgnoreSourceCollectionOrder() {
      final String first = PREFIXES + """
            :MultilingualAspect a samm:Aspect ;
               samm:preferredName "中文名称"@zh-Hans ;
               samm:preferredName "Deutscher Name"@de ;
               samm:description "Beschreibung"@de ;
               samm:properties () ;
               samm:operations () .
            """;
      final String reordered = PREFIXES + """
            :MultilingualAspect a samm:Aspect ;
               samm:description "Beschreibung"@de ;
               samm:preferredName "Deutscher Name"@de ;
               samm:preferredName "中文名称"@zh-Hans ;
               samm:properties () ;
               samm:operations () .
            """;

      assertInOrder( graphicalResult( first ).svg(), "preferredName&#160;[de]:&#160;Deutscher&#160;Name",
            "preferredName&#160;[zh-Hans]:&#160;中文名称", "description&#160;[de]:&#160;Beschreibung" );
      assertInOrder( graphicalResult( reordered ).svg(), "preferredName&#160;[de]:&#160;Deutscher&#160;Name",
            "preferredName&#160;[zh-Hans]:&#160;中文名称", "description&#160;[de]:&#160;Beschreibung" );
   }

   @Test
   void truncationUsesUnicodeCodePointsAtTheExactBoundaries() {
      final String value255 = "a".repeat( 255 );
      final String value256 = "b".repeat( 256 );
      final String value257 = "c".repeat( 257 );
      final String supplementaryBoundary = "d".repeat( 252 ) + "🚲" + "e".repeat( 4 );

      assertThat( DiagramVisitor.truncateLocalizedValue( value255 ) ).isEqualTo( value255 );
      assertThat( DiagramVisitor.truncateLocalizedValue( value256 ) ).isEqualTo( value256 );
      assertThat( DiagramVisitor.truncateLocalizedValue( value257 ) ).isEqualTo( "c".repeat( 253 ) + "..." );
      assertThat( DiagramVisitor.truncateLocalizedValue( supplementaryBoundary ) )
            .isEqualTo( "d".repeat( 252 ) + "🚲..." )
            .matches( value -> value.codePointCount( 0, value.length() ) == 256 );
   }

   @Test
   void truncationPrecedesWrappingAndEveryPhysicalRowKeepsOneLanguageLocator() {
      final String value = "localized value ".repeat( 40 ).strip();
      final DiagramNavigationResult result = graphicalResult( PREFIXES + """
            :MultilingualAspect a samm:Aspect ;
               samm:description "%s"@en ;
               samm:properties () ;
               samm:operations () .
            """.formatted( value ) );

      final List<DiagramAttributeNavigationTarget> descriptionRows = localizedTargets( result ).stream()
            .filter( target -> target.predicateUrn().equals( SammNs.SAMM.description().getURI() ) ).toList();
      assertThat( descriptionRows ).hasSizeGreaterThan( 1 );
      assertThat( descriptionRows ).extracting( DiagramAttributeNavigationTarget::id ).doesNotHaveDuplicates();
      assertThat( descriptionRows ).allMatch( target -> target.ownerUrn().endsWith( "#MultilingualAspect" )
            && target.predicateUrn().equals( SammNs.SAMM.description().getURI() )
            && target.selection().equals( "singleOccurrence" ) && "en".equals( target.language() ) );
      assertThat( result.svg() ).doesNotContain( value );
      assertThat( descriptionRows ).allSatisfy( target -> assertThat( result.svg() ).contains( "id=\"" + target.id() + "\"" ) );
   }

   @Test
   void defaultLocaleRowsAndOtherLangStringScalarValuesRemainUnchanged() {
      final String model = PREFIXES + """
            :MultilingualAspect a samm:Aspect ;
               samm:preferredName "English name"@en ;
               samm:preferredName "Deutscher Name"@de ;
               samm:properties ( :enumProperty ) ;
               samm:operations () .
            :enumProperty a samm:Property ; samm:characteristic :Enumeration .
            :Enumeration a samm-c:Enumeration ;
               samm:dataType rdf:langString ;
               samm-c:values ( "One"@en "Eins"@de ) .
            """;
      final Aspect aspect = load( model );
      final Diagram defaultDiagram = aspect.accept( new DiagramVisitor( Locale.ENGLISH ), Optional.empty() );
      final DiagramNavigationResult graphical = generator( aspect ).generateSvgWithNavigationMetadata( true );

      assertThat( defaultDiagram.getFocusBox().getEntries() ).containsExactly( "preferredName: English name" );
      assertThat( graphical.svg() ).contains( "values:&#160;&quot;One&quot;@en,&#160;&quot;Eins&quot;@de" );
   }

   @Test
   void publicDefaultSdkSvgPreservesLocaleAndSemanticLangStringsWithoutNavigationMetadata() {
      final String semanticValue = "🚲".repeat( 257 );
      final Aspect aspect = load( isolationModel( semanticValue ) );
      final DiagramGenerationConfig config = DiagramGenerationConfigBuilder.builder()
            .format( DiagramGenerationConfig.Format.SVG ).language( Locale.ENGLISH ).build();

      final String svg = new String( new AspectModelDiagramGenerator( aspect, config ).getContent(), StandardCharsets.UTF_8 );

      assertThat( svg ).contains( "preferredName:&#160;English&#160;name", "description:&#160;English&#160;description",
            "Semantic&#160;English", "Semantisch&#160;Deutsch" )
            .doesNotContain( "Deutscher&#160;Name", "Deutsche&#160;Beschreibung", "preferredName&#160;[en]",
                  "description&#160;[en]", "gv-header-", "gv-attribute-" );
      assertThat( svg.codePoints().filter( codePoint -> codePoint == "🚲".codePointAt( 0 ) ).count() ).isEqualTo( 257 );
   }

   @Test
   void localizedRowsOwnedByAnonymousElementsRemainNonInteractive() {
      final DiagramNavigationResult result = graphicalResult( PREFIXES + """
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            :MultilingualAspect a samm:Aspect ;
               samm:properties ( :anonymousProperty ) ;
               samm:operations () .
            :anonymousProperty a samm:Property ;
               samm:characteristic [
                  a samm:Characteristic ;
                  samm:preferredName "Anonymous name"@en ;
                  samm:dataType xsd:string
               ] .
            """ );

      assertThat( result.svg() ).contains( "preferredName&#160;[en]:&#160;Anonymous&#160;name" );
      assertThat( result.attributeNavigationTargets() ).noneMatch( target -> target.predicateUrn()
            .equals( SammNs.SAMM.preferredName().getURI() ) );
   }

   private static DiagramNavigationResult graphicalResult( final String model ) {
      return generator( load( model ) ).generateSvgWithNavigationMetadata( true );
   }

   private static AspectModelDiagramGenerator generator( final Aspect aspect ) {
      return new AspectModelDiagramGenerator( aspect, DiagramGenerationConfigBuilder.builder()
            .format( DiagramGenerationConfig.Format.SVG ).language( Locale.ENGLISH ).build() );
   }

   private static List<DiagramAttributeNavigationTarget> localizedTargets( final DiagramNavigationResult result ) {
      return result.attributeNavigationTargets().stream()
            .filter( target -> target.predicateUrn().equals( SammNs.SAMM.preferredName().getURI() )
                  || target.predicateUrn().equals( SammNs.SAMM.description().getURI() ) )
            .toList();
   }

   private static void assertInOrder( final String svg, final String... rows ) {
      int previous = -1;
      for ( final String row : rows ) {
         final int current = svg.indexOf( row );
         assertThat( current ).as( row ).isGreaterThan( previous );
         previous = current;
      }
   }

   private static Aspect load( final String model ) {
      return new AspectModelLoader().load( model, URI.create( "file:///multilingual-rows.ttl" ) ).aspect();
   }

   static String isolationModel( final String semanticValue ) {
      return PREFIXES + """
            :MultilingualAspect a samm:Aspect ;
               samm:preferredName "English name"@en ;
               samm:preferredName "Deutscher Name"@de ;
               samm:description "English description"@en ;
               samm:description "Deutsche Beschreibung"@de ;
               samm:properties ( :enumProperty ) ;
               samm:operations () .
            :enumProperty a samm:Property ; samm:characteristic :Enumeration .
            :Enumeration a samm-c:Enumeration ;
               samm:dataType rdf:langString ;
               samm-c:values ( "Semantic English"@en "Semantisch Deutsch"@de "%s"@en ) .
            """.formatted( semanticValue );
   }
}
