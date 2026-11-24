/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.turtle.languageserver.turtle;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings( { "HttpUrlsUsage" } )
class TurtleCompletionServiceTest {
   private TurtleCompletionService completionService;
   private TreeSitterTurtleParserService parserService;

   @BeforeEach
   void setUp() {
      completionService = new TurtleCompletionService();
      parserService = new TreeSitterTurtleParserService();
   }

   static Stream<Arguments> completionScenarios() {
      return Stream.of(
            Arguments.of(
                  "typing a known SAMM prefix returns vocabulary completions",
                  """
                     @prefix samm-e: <urn:samm:org.eclipse.esmf.samm:entity:2.2.0#> .

                     samm-e:""",
                  new Position( 2, 6 ),
                  List.of( "samm-e:TimeSeriesEntity", "samm-e:Point3d", "samm-e:FileResource", "samm-e:Quantity", "samm-e:resource",
                        "samm-e:mimeType", "samm-e:timestamp", "samm-e:value", "samm-e:x", "samm-e:y", "samm-e:z", "samm-e:unit" )
            ),
            Arguments.of(
                  "typing inside the local-name part for a SAMM prefix still resolves by namespace",
                  """
                     @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
                     @prefix samm-e: <urn:samm:org.eclipse.esmf.samm:entity:2.2.0#> .

                     samm-e:Tim""",
                  new Position( 3, 8 ),
                  List.of( "samm-e:TimeSeriesEntity", "samm-e:Point3d", "samm-e:FileResource", "samm-e:Quantity", "samm-e:resource",
                        "samm-e:mimeType", "samm-e:timestamp", "samm-e:value", "samm-e:x", "samm-e:y", "samm-e:z", "samm-e:unit" )
            ),
            Arguments.of(
                  "typing partial object with default prefix returns local names from the document",
                  """
                     @prefix : <http://example.org/> .

                     :subject :predicate :object .
                     :subject :otherPredicate :object .
                     :subject :predicate :obj""",
                  new Position( 4, 21 ),
                  List.of( "predicate", "object", "otherPredicate" )
            ),
            Arguments.of(
                  "typing colon after samm-c:constraint returns the constraint instances",
                  """
                     @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
                     @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
                     @prefix : <urn:test#> .

                     :Speed a samm:Characteristic .
                     :Range a samm:Constraint .
                     :Aspect samm-c:constraint :""",
                  new Position( 6, 27 ),
                  List.of( "Range" )
            )
      );
   }

   @ParameterizedTest( name = "{0}" )
   @MethodSource( "completionScenarios" )
   void testCompletionReturnsExpectedLocalNames( final String scenarioName,
         final String content,
         final Position position,
         final List<String> expectedLabels ) {
      assertThat( completionLabels( content, position ) ).containsExactlyInAnyOrder( expectedLabels.toArray( String[]::new ) );
   }

   @Test
   void typingPartialEntityAfterDataTypeIncludesMatchingEntity() {
      assertThat( completionLabels( """
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix : <urn:test#> .

         :SpatialEntity a samm:Entity .
         :Speed samm:dataType :Sp""", new Position( 4, 24 ) ) ).contains( ":SpatialEntity" );
   }

   @Test
   void typingColonAfterCharacteristicOffersCharacteristicInstancesAndTypesOnly() {
      assertThat( completionLabels( """
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix : <urn:test#> .

         :Speed a samm:Characteristic .
         :Range a samm:Constraint .
         :Aspect samm:characteristic :""", new Position( 5, 29 ) ) )
            .contains( ":Speed", "samm-c:Text" )
            .doesNotContain( ":Range" );
   }

   @Test
   void typingColonAfterFinishedStatementDoesNotUseItsPredicateExpectedClasses() {
      assertThat( completionLabels( """
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix : <urn:test#> .

         :Characteristic a samm:Characteristic .
         :weight a samm:Property ;
            samm:see <https://schema.org/weight> ;
            samm:characteristic :Weight .
         :""", new Position( 7, 1 ) ) ).contains( "Characteristic", "weight", "Weight" );
   }

   @Test
   void partialXsdDataTypeAfterDataTypeReturnsAllXsdDataTypes() {
      assertThat( completionLabels( """
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
         @prefix : <urn:test#> .

         :Speed samm:dataType xsd:in""", new Position( 4, 27 ) ) ).contains( "xsd:int", "xsd:integer", "xsd:float" );
   }

   @Test
   void completeXsdDataTypeAfterDataTypeStillReturnsAllXsdDataTypesForDropdown() {
      assertThat( completionLabels( """
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
         @prefix : <urn:test#> .

         :Speed samm:dataType xsd:int""", new Position( 4, 28 ) ) ).contains( "xsd:int", "xsd:integer", "xsd:string" );
   }

   @Test
   void partialEntityAfterDataTypeReturnsAllEntities() {
      assertThat( completionLabels( """
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix : <urn:test#> .

         :SpatialEntity a samm:Entity .
         :Other a samm:Entity .
         :Speed samm:dataType :Sp""", new Position( 5, 24 ) ) ).contains( ":SpatialEntity", ":Other" );
   }

   @Test
   void partialRdfDataTypeAfterDataTypeReturnsAllRdfDataTypes() {
      assertThat( completionLabels( """
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
         @prefix : <urn:test#> .

         :Speed samm:dataType rdf:langS""", new Position( 4, 30 ) ) ).contains( "rdf:langString" );
   }

   @Test
   void unitPrefixReturnsAllUnitNames() {
      assertThat( completionLabels( """
         @prefix unit: <urn:samm:org.eclipse.esmf.samm:unit:2.2.0#> .
         @prefix : <urn:test#> .

         :Speed samm-c:unit unit:pi""", new Position( 3, 26 ) ) ).contains( "unit:metre", "unit:gram" );
   }

   @Test
   void unitPrefixDeclaredAsAliasReturnsPrefixedUnitItems() {
      final List<CompletionItem> items = completionItems( """
         @prefix samm-u: <urn:samm:org.eclipse.esmf.samm:unit:2.2.0#> .

         samm-u:pi""", new Position( 2, 9 ) );

      final CompletionItem metre = items.stream().filter( item -> "samm-u:metre".equals( item.getLabel() ) ).findFirst().orElseThrow();
      assertThat( metre.getKind() ).isEqualTo( CompletionItemKind.Reference );
      assertThat( metre.getDetail() ).isEqualTo( SAMM.UNIT );
      assertThat( metre.getTextEdit().getLeft().getNewText() ).isEqualTo( "samm-u:metre" );
      assertThat( metre.getTextEdit().getLeft().getRange() ).isEqualTo( new Range( new Position( 2, 0 ), new Position( 2, 9 ) ) );
   }

   @Test
   void colonFallbackReturnsDescribedLocalNamesWithPrefixedFilterText() {
      final List<CompletionItem> items = completionItems( """
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix ex: <http://example.org/> .
         @prefix : <urn:test#> .

         :Speed a samm:Characteristic .
         :Aspect ex:knows :""", new Position( 5, 18 ) );

      final CompletionItem speed = items.stream().filter( item -> "Speed".equals( item.getLabel() ) ).findFirst().orElseThrow();
      assertThat( speed.getKind() ).isEqualTo( CompletionItemKind.Reference );
      assertThat( speed.getDetail() ).isEqualTo( "Local Characteristic" );
      assertThat( speed.getFilterText() ).isEqualTo( ":Speed" );
      assertThat( speed.getTextEdit().getLeft().getNewText() ).isEqualTo( ":Speed" );
   }

   @Test
   void colonFallbackNamesMultiWordSammTypesLikeTheAttributeValueService() {
      final List<CompletionItem> items = completionItems( """
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix ex: <http://example.org/> .
         @prefix : <urn:test#> .

         :Parent a samm:AbstractEntity .
         :Aspect ex:knows :""", new Position( 5, 18 ) );

      final CompletionItem parent = items.stream().filter( item -> "Parent".equals( item.getLabel() ) ).findFirst().orElseThrow();
      assertThat( parent.getDetail() ).isEqualTo( "Local Abstract Entity" );
   }

   @Test
   void colonFallbackDoesNotOfferTheSubjectOfTheCurrentStatement() {
      final List<String> labels = completionLabels( """
         @prefix ex: <http://example.org/> .
         @prefix : <urn:test#> .

         :Speed ex:name "Speed" .
         :Aspect ex:knows :""", new Position( 4, 18 ) );

      assertThat( labels ).contains( "Speed" ).doesNotContain( "Aspect" );
   }

   @Test
   void vocabularyItemsCarryClassAndAttributeDetail() {
      final List<CompletionItem> items = completionItems( """
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

         samm:""", new Position( 2, 5 ) );

      final CompletionItem entity = items.stream().filter( item -> "samm:Entity".equals( item.getLabel() ) ).findFirst().orElseThrow();
      assertThat( entity.getKind() ).isEqualTo( CompletionItemKind.Class );
      assertThat( entity.getDetail() ).isEqualTo( "SAMM Class" );

      final CompletionItem properties =
            items.stream().filter( item -> "samm:properties".equals( item.getLabel() ) ).findFirst().orElseThrow();
      assertThat( properties.getKind() ).isEqualTo( CompletionItemKind.Property );
      assertThat( properties.getDetail() ).isEqualTo( "SAMM Attribute" );
   }

   @Test
   void olderSammVersionPrefixReturnsLatestVersionVocabulary() {
      final List<String> labels = completionLabels( """
         @prefix samm-e: <urn:samm:org.eclipse.esmf.samm:entity:2.0.0#> .

         samm-e:""", new Position( 2, 7 ) );

      assertThat( labels ).contains( "samm-e:TimeSeriesEntity" );
   }

   private List<CompletionItem> completionItems( final String content, final Position position ) {
      final Document document = new Document( "test.ttl", content );
      final ParsedDocument parsedDocument = parserService.apply( document );
      final CompletionParams params = new CompletionParams( new TextDocumentIdentifier( "test.ttl" ), position );
      return completionService.complete( parsedDocument, params );
   }

   private List<String> completionLabels( final String content, final Position position ) {
      final Document document = new Document( "test.ttl", content );
      final ParsedDocument parsedDocument = parserService.apply( document );
      final CompletionParams params = new CompletionParams( new TextDocumentIdentifier( "test.ttl" ), position );
      return completionService.complete( parsedDocument, params ).stream().map( CompletionItem::getLabel ).toList();
   }

   static Stream<Arguments> nonEmptyKnownPrefixScenarios() {
      return Stream.of(
            Arguments.of(
                  "samm prefix returns at least one completion",
                  """
                     @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

                     samm:""",
                  new Position( 2, 5 )
            ),
            Arguments.of(
                  "samm-c prefix returns at least one completion",
                  """
                     @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .

                     samm-c:""",
                  new Position( 2, 7 )
            ),
            Arguments.of(
                  "unit prefix returns at least one completion",
                  """
                     @prefix unit: <urn:samm:org.eclipse.esmf.samm:unit:2.2.0#> .

                     unit:""",
                  new Position( 2, 5 )
            ),
            Arguments.of(
                  "sh prefix returns at least one completion",
                  """
                     @prefix sh: <http://www.w3.org/ns/shacl#> .

                     sh:""",
                  new Position( 2, 3 )
            )
      );
   }

   @ParameterizedTest( name = "{0}" )
   @MethodSource( "nonEmptyKnownPrefixScenarios" )
   void testCompletionIsNotEmptyForKnownPrefixes( final String scenarioName,
         final String content,
         final Position position ) {
      final Document document = new Document( "test.ttl", content );
      final ParsedDocument parsedDocument = parserService.apply( document );
      final CompletionParams params = new CompletionParams( new TextDocumentIdentifier( "test.ttl" ), position );

      final List<CompletionItem> completions = completionService.complete( parsedDocument, params );

      assertThat( completions ).isNotEmpty();
   }

   static Stream<Arguments> noCompletionScenarios() {
      return Stream.of(
            Arguments.of(
                  "typing an unknown non-SAMM prefix",
                  """
                     @prefix ex: <http://example.org/> .

                     ex:""",
                  new Position( 2, 3 )
            ),
            Arguments.of(
                  "cursor inside a string literal being typed",
                  """
                     @prefix ex: <http://example.org/> .

                     ex:subject ex:predicate ex:object .
                     ex:subject ex:predicate "typing""",
                  new Position( 3, 28 ) // char-1=27 inside "typing"
            ),
            Arguments.of(
                  "cursor on blank line after pressing Enter",
                  """
                     @prefix ex: <http://example.org/> .

                     ex:subject ex:predicate ex:object .
                     """,
                  new Position( 3, 0 )
            ),
            Arguments.of(
                  "cursor inside the IRI of a prefix declaration",
                  """
                     @prefix ex: <http://example.org/> .

                     ex:subject ex:predicate ex:obj""",
                  new Position( 0, 20 ) // char-1=19 inside <http://example.org/>
            ),
            Arguments.of(
                  "empty document",
                  "",
                  new Position( 0, 0 )
            )
      );
   }

   @Test
   void sammVocabularyCompletionDoesNotOfferEncodings() {
      final List<String> labels = labels( """
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

         samm:""", new Position( 2, 5 ) );

      assertThat( labels ).contains( "samm:Aspect", "samm:Property", "samm:characteristic" )
            .doesNotContainAnyElementsOf(
                  SammNs.SAMM.allEncodings().stream().map( encoding -> "samm:" + encoding.getLocalName() ).toList() );
   }

   @Test
   void encodingsAreStillOfferedAsTheValueOfAnEncodingConstraint() {
      final List<String> labels = labels( """
         @prefix : <urn:samm:example.test:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .

         :Encoding a samm-c:EncodingConstraint ;
            samm:value samm:UTF""", new Position( 5, 22 ) );

      assertThat( labels ).containsExactlyInAnyOrderElementsOf(
            SammNs.SAMM.allEncodings().stream().map( encoding -> "samm:" + encoding.getLocalName() ).toList() );
   }

   private List<String> labels( final String content, final Position position ) {
      final Document document = new Document( "test.ttl", content );
      final ParsedDocument parsedDocument = parserService.apply( document );
      final CompletionParams params = new CompletionParams( new TextDocumentIdentifier( "test.ttl" ), position );
      return completionService.complete( parsedDocument, params, true ).stream().map( CompletionItem::getLabel ).toList();
   }

   @ParameterizedTest( name = "{0}" )
   @MethodSource( "noCompletionScenarios" )
   void testNoCompletionWhenCursorIsNotOnPrefixedName( final String scenarioName,
         final String content,
         final Position position ) {
      assertNoCompletions( content, position );
   }

   private void assertNoCompletions( final String content, final Position position ) {
      final Document document = new Document( "test.ttl", content );
      final ParsedDocument parsedDocument = parserService.apply( document );
      final CompletionParams params = new CompletionParams( new TextDocumentIdentifier( "test.ttl" ), position );
      assertThat( completionService.complete( parsedDocument, params ) ).isEmpty();
   }

   private static List<String> expectedLabelsForPrefix( final String prefix ) {
      return SammNs.sammNamespaces()
            .filter( namespace -> ( namespace.getShortForm() + ":" ).equals( prefix ) )
            .findFirst()
            .stream()
            .flatMap( TurtleCompletionServiceTest::namespaceLabels )
            .distinct()
            .toList();
   }

   private static Stream<String> namespaceLabels( final RdfNamespace namespace ) {
      return Stream.of( "allResources", "allProperties" )
            .flatMap( methodName -> invokeCollectionMethod( namespace, methodName ).stream() )
            .map( Object::toString )
            .map( iri -> iri.substring( iri.lastIndexOf( '#' ) + 1 ) );
   }

   private static Collection<?> invokeCollectionMethod( final RdfNamespace namespace, final String methodName ) {
      try {
         final Object result = namespace.getClass().getMethod( methodName ).invoke( namespace );
         if ( result instanceof Collection<?> collection ) {
            return collection;
         }
      } catch ( final ReflectiveOperationException ignored ) {
         // Older vocabulary variants may not expose these methods for tests.
      }
      return List.of();
   }
}
