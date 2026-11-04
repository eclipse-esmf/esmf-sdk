/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.turtle.languageserver.turtle;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import org.eclipse.lsp4j.InlineCompletionContext;
import org.eclipse.lsp4j.InlineCompletionItem;
import org.eclipse.lsp4j.InlineCompletionParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * The cursor is marked with {@code |} in the scenarios. An empty expected text means that no inline
 * completion is suggested.
 */
class TurtleInlineCompletionServiceTest {
   private static final String SAMM_CHARACTERISTIC_IRI = SammNs.SAMM.characteristic().getURI();
   private static final String HEADER = """
      @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
      @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
      @prefix : <urn:test#> .

      """;

   private TurtleInlineCompletionService inlineService;
   private TreeSitterTurtleParserService parserService;

   @BeforeEach
   void setUp() {
      inlineService = new TurtleInlineCompletionService();
      parserService = new TreeSitterTurtleParserService();
   }

   static Stream<Arguments> inlineScenarios() {
      return Stream.of(
            // Predicate with a single object: the predicate is completed together with its object
            Arguments.of(
                  "predicate with a single characteristic in the document auto-completes with its object",
                  HEADER + """
                     :Speed a samm:Characteristic .
                     :Aspect samm:chara|""",
                  "samm:characteristic :Speed"
            ),
            Arguments.of(
                  "predicate with several possible objects is not auto-completed",
                  HEADER + """
                     :Speed a samm:Characteristic .
                     :Warning a samm:Characteristic .
                     :Aspect samm:chara|""",
                  ""
            ),
            Arguments.of(
                  "samm:properties is never auto-completed",
                  HEADER + """
                     :Name a samm:Property .
                     :Aspect samm:propert|""",
                  ""
            ),
            Arguments.of(
                  "samm:dataType predicate alone is never auto-completed",
                  HEADER + """
                     :Aspect samm:data|""",
                  ""
            ),
            // Object completion: a single candidate of the expected class
            Arguments.of(
                  "object with a single matching characteristic is auto-completed",
                  HEADER + """
                     :Speed a samm:Characteristic .
                     :Aspect samm:characteristic :Sp|""",
                  ":Speed"
            ),
            Arguments.of(
                  "object matching several characteristics is not auto-completed",
                  HEADER + """
                     :Speed a samm:Characteristic .
                     :SpeedWarning a samm:Characteristic .
                     :Aspect samm:characteristic :Sp|""",
                  ""
            ),
            Arguments.of(
                  "exact name that is also a prefix of another candidate is not auto-completed",
                  HEADER + """
                     :Speed a samm:Characteristic .
                     :SpeedWarning a samm:Characteristic .
                     :Aspect samm:characteristic :Speed|""",
                  ""
            ),
            Arguments.of(
                  "typing one more symbol selects the longer candidate",
                  HEADER + """
                     :Speed a samm:Characteristic .
                     :SpeedWarning a samm:Characteristic .
                     :Aspect samm:characteristic :SpeedW|""",
                  ":SpeedWarning"
            ),
            Arguments.of(
                  "no completion in the middle of an existing name",
                  HEADER + """
                     :Speed a samm:Characteristic .
                     :Aspect samm:characteristic :S|peed""",
                  ""
            ),
            // samm:dataType: data types (xsd, rdf, samm) and entities
            Arguments.of(
                  "partial XSD data type is completed to the single match",
                  HEADER + """
                     :Speed samm:dataType xsd:boolea|""",
                  "xsd:boolean"
            ),
            Arguments.of(
                  "complete XSD data type is not extended to a longer one",
                  HEADER + """
                     :Speed samm:dataType xsd:int|""",
                  ""
            ),
            Arguments.of(
                  "partial XSD data type matching several types is not completed",
                  HEADER + """
                     :Speed samm:dataType xsd:in|""",
                  ""
            ),
            Arguments.of(
                  "entity name for samm:dataType is completed",
                  HEADER + """
                     :SpatialEntity a samm:Entity .
                     :Speed samm:dataType :Sp|""",
                  ":SpatialEntity"
            )
      );
   }

   @ParameterizedTest( name = "{0}" )
   @MethodSource( "inlineScenarios" )
   void testInlineCompletion( final String scenarioName, final String markedContent, final String expectedText ) {
      final String content = markedContent.replace( "|", "" );
      final Position position = cursorPosition( markedContent );
      final ParsedDocument parsedDocument = parserService.apply( new Document( "test.ttl", content ) );
      final InlineCompletionParams params = new InlineCompletionParams(
            new TextDocumentIdentifier( "test.ttl" ), position, new InlineCompletionContext() );

      final List<InlineCompletionItem> items = inlineService.inlineComplete( parsedDocument, params );

      if ( expectedText.isEmpty() ) {
         assertThat( items ).isEmpty();
      } else {
         assertThat( items ).singleElement()
               .satisfies( item -> assertThat( item.getInsertText().getLeft() ).isEqualTo( expectedText ) );
      }
   }

   @Test
   void singlePredicateIriIsReturnedOnlyForUniqueMatch() {
      assertThat( TurtleInlineCompletionService.singlePredicateIri( "samm:", "characteristi" )
            .flatMap( AbstractTurtleCompletionService::prefixedName ) ).contains( "samm:characteristic" );
      assertThat( TurtleInlineCompletionService.singlePredicateIri( "samm:", "" ) ).isEmpty();
   }

   @Test
   void singleObjectIsEmptyForSeveralSubjects() {
      final String content = HEADER + """
         :Speed a samm:Characteristic .
         :Warning a samm:Characteristic .
         """;
      final ParsedDocument parsedDocument = parserService.apply( new Document( "test.ttl", content ) );

      assertThat( inlineService.singleObject( parsedDocument.turtleSyntaxTree(), SAMM_CHARACTERISTIC_IRI, "" ) ).isEmpty();
   }

   @Test
   void singleObjectIsReturnedForSingleSubject() {
      final String content = HEADER + """
         :Speed a samm:Characteristic .
         """;
      final ParsedDocument parsedDocument = parserService.apply( new Document( "test.ttl", content ) );

      assertThat( inlineService.singleObject( parsedDocument.turtleSyntaxTree(), SAMM_CHARACTERISTIC_IRI, "" ) )
            .contains( ":Speed" );
   }

   @Test
   void singleObjectIsNeverReturnedForDataTypeOrProperties() {
      final String content = HEADER + """
         :Property a samm:Property .
         """;
      final ParsedDocument parsedDocument = parserService.apply( new Document( "test.ttl", content ) );

      assertThat( inlineService.singleObject( parsedDocument.turtleSyntaxTree(), SammNs.SAMM.dataType().getURI(), "" ) ).isEmpty();
      assertThat( inlineService.singleObject( parsedDocument.turtleSyntaxTree(), SammNs.SAMM.properties().getURI(), "" ) ).isEmpty();
   }

   private static Position cursorPosition( final String markedContent ) {
      final String beforeCursor = markedContent.substring( 0, markedContent.indexOf( '|' ) );
      final String[] lines = beforeCursor.split( "\n", -1 );
      return new Position( lines.length - 1, lines[lines.length - 1].length() );
   }
}
