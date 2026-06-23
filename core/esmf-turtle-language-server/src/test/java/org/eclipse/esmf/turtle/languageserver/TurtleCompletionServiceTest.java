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

package org.eclipse.esmf.turtle.languageserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleCompletionService;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.junit.jupiter.api.BeforeEach;
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
                  "typing partial object in new incomplete triple – names collected from whole document",
                  """
                     @prefix ex: <http://example.org/> .

                     ex:subject ex:predicate ex:object .
                     ex:newSubject ex:predicate ex:obj""",
                  // ex:newSubject(0-12) ' '(13) ex:predicate(14-25) ' '(26) ex:obj(27-32)
                  new Position( 3, 33 ), // cursor after 'j'; char-1=32 is within ex:obj [27,33)
                  List.of( "subject", "predicate", "object", "newSubject", "obj" )
            ),
            Arguments.of(
                  "multiple prefixes active – only names for the typed prefix are returned",
                  """
                     @prefix ex: <http://example.org/> .
                     @prefix ns: <http://namespace.org/> .

                     ex:subject ns:predicate ex:object .
                     ex:subject ns:predicate ns:""",
                  // ex:subject(0-9) ' '(10) ns:predicate(11-22) ' '(23) ns:(24-27)
                  new Position( 4, 27 ), // cursor after 'ns:'
                  List.of( "predicate" )
            ),
            Arguments.of(
                  "deduplication – repeated local names across triples appear only once",
                  """
                     @prefix ex: <http://example.org/> .

                     ex:subject ex:predicate ex:object .
                     ex:subject ex:otherPredicate ex:object .
                     ex:subject ex:predicate ex:obj""",
                  // ex:subject(0-9) ' '(10) ex:predicate(11-22) ' '(23) ex:obj(24-29)
                  new Position( 4, 30 ), // cursor after 'j'; char-1=29 is within ex:obj [24,30)
                  List.of( "subject", "predicate", "object", "otherPredicate", "obj" )
            ),
            Arguments.of(
                  "typing partial object in semicolon-continued property list",
                  """
                     @prefix ex: <http://example.org/> .

                     ex:subject ex:predicate ex:object ;
                        ex:otherPredicate ex:""",
                  // '   '(0-2) ex:otherPredicate(3-19) ' '(20) ex:obj(21-26)
                  new Position( 3, 24 ),
                  List.of( "subject", "predicate", "object", "otherPredicate" )
            ),
            Arguments.of(
                  "typing a fresh prefixed name on a new line at the end of the document (incomplete subject)",
                  """
                     @prefix ex: <http://example.org/> .

                     ex:subject ex:predicate ex:object .
                     ex:obj""",
                  // ex:obj is the start of a new, still incomplete statement -> tree-sitter wraps it in an
                  // ERROR node; the nested prefixed_name must still be discoverable for completion.
                  new Position( 3, 3 ), // cursor after 'j'; char-1=5 is within ex:obj [0,6)
                  List.of( "subject", "predicate", "object", "obj" )
            )
      );
   }

   @ParameterizedTest( name = "{0}" )
   @MethodSource( "completionScenarios" )
   void testCompletionReturnsExpectedLocalNames( final String scenarioName,
         final String content,
         final Position position,
         final List<String> expectedLabels ) {
      final Document document = new Document( "test.ttl", content );
      final ParsedDocument parsedDocument = parserService.apply( document );
      final CompletionParams params = new CompletionParams( new TextDocumentIdentifier( "test.ttl" ), position );

      final List<CompletionItem> completions = completionService.complete( parsedDocument, params );

      final List<String> labels = completions.stream().map( CompletionItem::getLabel ).toList();
      assertThat( labels ).containsExactlyInAnyOrder( expectedLabels.toArray( String[]::new ) );
   }

   static Stream<Arguments> noCompletionScenarios() {
      return Stream.of(
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
}
