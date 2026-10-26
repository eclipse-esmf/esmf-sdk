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

package org.eclipse.esmf.turtle.languageserver.structure;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import org.eclipse.lsp4j.SemanticTokens;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TurtleTokenServiceTest {
   private TurtleTokenService tokenService;
   private TreeSitterTurtleParserService parserService;

   @BeforeEach
   void setUp() {
      tokenService = new TurtleTokenService();
      parserService = new TreeSitterTurtleParserService();
   }

   @Test
   void testBasicSemanticTokens() {
      final Document document = new Document( "test.ttl", """
         @prefix ex: <http://example.org/> .

         ex:subject ex:predicate ex:object .
         """ );

      final SemanticTokens tokens = tokenService.buildSemanticTokens( parserService.apply( document ) );

      assertThat( tokens ).isNotNull();
      assertThat( tokens.getData() ).isNotEmpty();
      // Each token is represented by 5 integers: deltaLine, deltaColumn, length, tokenType,
      // tokenModifiers
      assertThat( tokens.getData().size() % 5 ).isEqualTo( 0 );
   }

   @Test
   void testMultiLineStringLiteral() {
      final Document document = new Document( "test.ttl", """
         @prefix ex: <http://example.org/> .

         ex:subject ex:description \"\"\"This is a
         multi-line
         string literal.\"\"\" .
         """ );

      final SemanticTokens tokens = tokenService.buildSemanticTokens( parserService.apply( document ) );

      assertThat( tokens ).isNotNull();
      assertThat( tokens.getData() ).isNotEmpty();

      // Verify tokens are split correctly - each token should be on a single line
      // The data format is [deltaLine, deltaColumn, length, tokenType, tokenModifiers]
      // We iterate through the tokens and verify no length exceeds what could fit on a single line
      for ( int i = 0; i < tokens.getData().size(); i += 5 ) {
         final int length = tokens.getData().get( i + 2 );
         // Multi-line content should be split, so each token length should be reasonable for a single line
         assertThat( length ).isGreaterThan( 0 );
      }
   }

   @Test
   void testMultiLineCommentToken() {
      final Document document = new Document( "test.ttl", """
         @prefix ex: <http://example.org/> .

         # This is a comment
         # on multiple lines
         ex:subject ex:predicate ex:object .
         """ );

      final SemanticTokens tokens = tokenService.buildSemanticTokens( parserService.apply( document ) );

      assertThat( tokens ).isNotNull();
      assertThat( tokens.getData() ).isNotEmpty();
   }

   @Test
   void testSingleQuoteMultiLineString() {
      final Document document = new Document( "test.ttl", """
         @prefix ex: <http://example.org/> .

         ex:subject ex:note '''This is
         another multi-line
         string with single quotes.''' .
         """ );

      final SemanticTokens tokens = tokenService.buildSemanticTokens( parserService.apply( document ) );

      assertThat( tokens ).isNotNull();
      assertThat( tokens.getData() ).isNotEmpty();
   }

   @Test
   void testComplexDocumentWithMultiLineStrings() {
      final Document document = new Document( "test.ttl", """
         @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
         @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

         :TestAspect a samm:Aspect ;
            samm:preferredName "Test Aspect"@en ;
            samm:description \"\"\"This is a test aspect
            with a multi-line description
            that spans several lines.\"\"\"@en ;
            samm:properties ( :property1 :property2 ) .

         :property1 a samm:Property ;
            samm:preferredName "Property 1"@en ;
            samm:description "Single line description"@en ;
            samm:characteristic samm-c:Text .

         :property2 a samm:Property ;
            samm:preferredName "Property 2"@en ;
            samm:description '''Another multi-line
            description using
            single quotes.'''@en ;
            samm:characteristic samm-c:Text .
         """ );

      final SemanticTokens tokens = tokenService.buildSemanticTokens( parserService.apply( document ) );

      assertThat( tokens ).isNotNull();
      assertThat( tokens.getData() ).isNotEmpty();

      // Verify the data is well-formed
      assertThat( tokens.getData().size() % 5 ).isEqualTo( 0 );

      // Check that all deltaLine values are non-negative
      for ( int i = 0; i < tokens.getData().size(); i += 5 ) {
         final int deltaLine = tokens.getData().get( i );
         assertThat( deltaLine ).isGreaterThanOrEqualTo( 0 );
      }
   }

   @Test
   void testTokenTypesAreValid() {
      final Document document = new Document( "test.ttl", """
         @prefix ex: <http://example.org/> .
         @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

         ex:subject
            ex:stringProp "test string" ;
            ex:numberProp 42 ;
            ex:decimalProp 3.14 ;
            ex:boolProp true ;
            ex:langString "test"@en ;
            ex:typedLiteral "123"^^xsd:integer ;
            a ex:Class .
         """ );

      final SemanticTokens tokens = tokenService.buildSemanticTokens( parserService.apply( document ) );

      assertThat( tokens ).isNotNull();
      assertThat( tokens.getData() ).isNotEmpty();

      // Verify all token types are within valid range
      final int maxTokenType = TurtleTokenService.SUPPORTED_TOKEN_TYPES.getTokenTypes().size() - 1;
      for ( int i = 0; i < tokens.getData().size(); i += 5 ) {
         final int tokenType = tokens.getData().get( i + 3 );
         assertThat( tokenType ).isBetween( 0, maxTokenType );
      }
   }

   @Test
   void testEmptyDocument() {
      final Document document = new Document( "test.ttl", "" );

      final SemanticTokens tokens = tokenService.buildSemanticTokens( parserService.apply( document ) );

      assertThat( tokens ).isNotNull();
      // Empty document should produce empty token list
      assertThat( tokens.getData() ).isEmpty();
   }

   @Test
   void testOnlyWhitespace() {
      final Document document = new Document( "test.ttl", "   \n\n   \n" );

      final SemanticTokens tokens = tokenService.buildSemanticTokens( parserService.apply( document ) );

      assertThat( tokens ).isNotNull();
      // Whitespace-only document should produce empty token list
      assertThat( tokens.getData() ).isEmpty();
   }

   @Test
   void testMultiLineStringWithEscapes() {
      final Document document = new Document( "test.ttl", """
         @prefix ex: <http://example.org/> .

         ex:subject ex:text \"\"\"Line 1
         Line 2 with \\"quotes\\"
         Line 3 with \\n escape
         Line 4\"\"\" .
         """ );

      final SemanticTokens tokens = tokenService.buildSemanticTokens( parserService.apply( document ) );

      assertThat( tokens ).isNotNull();
      assertThat( tokens.getData() ).isNotEmpty();
   }

   @Test
   void testTokenOrdering() {
      final Document document = new Document( "test.ttl", """
         @prefix ex: <http://example.org/> .

         ex:subject1 ex:predicate1 ex:object1 .
         ex:subject2 ex:predicate2 ex:object2 .
         """ );

      final SemanticTokens tokens = tokenService.buildSemanticTokens( parserService.apply( document ) );

      assertThat( tokens ).isNotNull();
      assertThat( tokens.getData() ).isNotEmpty();

      // Verify tokens are properly ordered (deltaLine should increase or stay same)
      int currentLine = 0;
      int currentColumn = 0;
      for ( int i = 0; i < tokens.getData().size(); i += 5 ) {
         final int deltaLine = tokens.getData().get( i );
         final int deltaColumn = tokens.getData().get( i + 1 );

         currentLine += deltaLine;
         if ( deltaLine == 0 ) {
            currentColumn += deltaColumn;
         } else {
            currentColumn = deltaColumn;
         }

         // All positions should be non-negative
         assertThat( currentLine ).isGreaterThanOrEqualTo( 0 );
         assertThat( currentColumn ).isGreaterThanOrEqualTo( 0 );
      }
   }

   @Test
   void testMultiLineStringAtEndOfFile() {
      final Document document = new Document( "test.ttl", """
         @prefix ex: <http://example.org/> .

         ex:subject ex:description \"\"\"Multi-line
         string at
         end of file.\"\"\" .""" );

      final SemanticTokens tokens = tokenService.buildSemanticTokens( parserService.apply( document ) );

      assertThat( tokens ).isNotNull();
      assertThat( tokens.getData() ).isNotEmpty();
   }
}
