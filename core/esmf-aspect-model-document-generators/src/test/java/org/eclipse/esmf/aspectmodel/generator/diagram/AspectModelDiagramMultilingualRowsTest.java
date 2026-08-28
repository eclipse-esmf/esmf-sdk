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
      final Diagram.Box box = graphicalBox( PREFIXES + """
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

      assertThat( box.getEntries() ).containsExactly(
            "preferredName [de]: Deutscher Name",
            "preferredName [en]: English name",
            "preferredName [en-US]: US name",
            "description [de]: Deutsche Beschreibung",
            "description [en]: English description",
            "description [en-US]: US description" );
      assertThat( box.getEntryNavigation() ).allMatch( Optional::isPresent );
      assertThat( box.getEntryNavigation() ).extracting( row -> row.orElseThrow().locator().language() )
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

      assertThat( graphicalBox( first ).getEntries() ).containsExactly(
            "preferredName [de]: Deutscher Name", "preferredName [zh-Hans]: 中文名称", "description [de]: Beschreibung" )
            .isEqualTo( graphicalBox( reordered ).getEntries() );
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
      final Diagram.Box box = graphicalBox( PREFIXES + """
            :MultilingualAspect a samm:Aspect ;
               samm:description "%s"@en ;
               samm:properties () ;
               samm:operations () .
            """.formatted( value ) );

      final List<Integer> descriptionRows = java.util.stream.IntStream.range( 0, box.getEntries().size() )
            .filter( index -> box.getEntryNavigation().get( index ).stream()
                  .anyMatch( row -> row.locator().predicateUrn().equals( SammNs.SAMM.description().getURI() ) ) )
            .boxed().toList();
      assertThat( descriptionRows ).hasSizeGreaterThan( 1 );
      assertThat( descriptionRows.stream().map( index -> box.getEntryNavigation().get( index ).orElseThrow().markerId() ) )
            .doesNotHaveDuplicates();
      assertThat( descriptionRows.stream().map( index -> box.getEntryNavigation().get( index ).orElseThrow().locator() ) )
            .allMatch( locator -> locator.ownerUrn().endsWith( "#MultilingualAspect" )
                  && locator.predicateUrn().equals( SammNs.SAMM.description().getURI() )
                  && locator.selection() == DiagramAttributeNavigation.Selection.SINGLE_OCCURRENCE
                  && "en".equals( locator.language() ) );

      final String reconstructed = descriptionRows.stream().map( index -> box.getEntries().get( index ) )
            .map( row -> row.startsWith( "   " ) ? row.substring( 3 ) : row )
            .collect( java.util.stream.Collectors.joining( " " ) );
      assertThat( reconstructed ).isEqualTo( "description [en]: " + DiagramVisitor.truncateLocalizedValue( value ) );
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
      final Diagram graphicalDiagram = aspect.accept( graphicalVisitor(), Optional.empty() );

      assertThat( defaultDiagram.getFocusBox().getEntries() ).containsExactly( "preferredName: English name" );
      assertThat( graphicalDiagram.getBoxes().stream().filter( box -> "Enumeration".equals( box.getTitle() ) ).findFirst().orElseThrow()
               .getEntries() ).contains( "values: \"One\"@en, \"Eins\"@de" );
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
      final Diagram diagram = load( PREFIXES + """
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
            """ ).accept( graphicalVisitor(), Optional.empty() );
      final Diagram.Box anonymous = diagram.getBoxes().stream()
            .filter( box -> box.getEntries().contains( "preferredName [en]: Anonymous name" ) ).findFirst().orElseThrow();

      assertThat( anonymous.getEntryNavigation() ).allMatch( Optional::isEmpty );
   }

   private static Diagram.Box graphicalBox( final String model ) {
      return load( model ).accept( graphicalVisitor(), Optional.empty() ).getFocusBox();
   }

   private static DiagramVisitor graphicalVisitor() {
      return new DiagramVisitor( Locale.ENGLISH, DiagramHeaderNavigation.enabled(), DiagramAttributeNavigation.enabled(), true );
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
