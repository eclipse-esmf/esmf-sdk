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

package org.eclipse.esmf.turtle.languageserver.turtle.navigation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings( { "HttpUrlsUsage" } )
class TurtleDefinitionServiceTest {
   private TurtleDefinitionService definitionService;
   private TreeSitterTurtleParserService parserService;

   @BeforeEach
   void setUp() {
      definitionService = new TurtleDefinitionService();
      parserService = new TreeSitterTurtleParserService();
   }

   static Stream<Arguments> prefixDefinitionScenarios() {
      return Stream.of(
            Arguments.of(
                  "basic prefix in subject position",
                  """
                     @prefix ex: <http://example.org/> .
                     @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .

                     ex:subject ex:predicate ex:object .
                     """,
                  new Position( 3, 0 ),
                  0,
                  8
            ),
            Arguments.of(
                  "prefix with multiple prefixes",
                  """
                     @prefix ex: <http://example.org/> .
                     @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                     @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .

                     ex:test rdf:type rdf:Property .
                     """,
                  new Position( 4, 0 ),
                  0,
                  8
            ),
            Arguments.of(
                  "prefix in predicate position",
                  """
                     @prefix ex: <http://example.org/> .
                     @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .

                     ex:subject ex:predicate ex:object .
                     """,
                  new Position( 3, 11 ),
                  0,
                  8
            ),
            Arguments.of(
                  "prefix in object position",
                  """
                     @prefix ex: <http://example.org/> .

                     ex:subject ex:predicate ex:object .
                     """,
                  new Position( 2, 24 ),
                  0,
                  8
            ),
            Arguments.of(
                  "multiple prefixes on same line - ns prefix",
                  """
                     @prefix ex: <http://example.org/> .
                     @prefix ns: <http://namespace.org/> .

                     ex:subject ns:predicate ex:object .
                     """,
                  new Position( 3, 11 ),
                  1,
                  8
            )
      );
   }

   @ParameterizedTest( name = "{0}" )
   @MethodSource( "prefixDefinitionScenarios" )
   void testFindPrefixDefinition( final String scenarioName,
         final String content,
         final Position position,
         final int expectedLine,
         final int expectedCharacter ) {
      final Document document = new Document( "test.ttl", content );
      final ParsedDocument parsedDocument = parserService.apply( document );

      final Optional<Location> definition = definitionService.findDefinition( parsedDocument, position );

      assertThat( definition ).isPresent();
      final Location location = definition.get();
      assertThat( location.getUri() ).isEqualTo( "test.ttl" );
      assertThat( location.getRange().getStart().getLine() ).isEqualTo( expectedLine );
      assertThat( location.getRange().getStart().getCharacter() ).isEqualTo( expectedCharacter );
   }

   static Stream<Arguments> elementDefinitionScenarios() {
      return Stream.of(
            Arguments.of(
                  "type definition with 'a' shorthand",
                  """
                     @prefix ex: <http://example.org/> .

                     ex:MyClass a ex:Type .

                     ex:instance a ex:MyClass .
                     """,
                  new Position( 4, 17 ),
                  2,
                  3
            ),
            Arguments.of(
                  "explicit rdf:type",
                  """
                     @prefix ex: <http://example.org/> .
                     @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .

                     ex:MyClass rdf:type ex:Type .

                     ex:instance rdf:type ex:MyClass .
                     """,
                  new Position( 5, 24 ),
                  3,
                  3
            ),
            Arguments.of(
                  "complex document with class hierarchy",
                  """
                     @prefix ex: <http://example.org/> .
                     @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                     @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .

                     ex:Class1 a rdfs:Class ;
                        rdfs:label "Class 1" ;
                        rdfs:comment "A sample class" .

                     ex:Class2 a rdfs:Class ;
                        rdfs:subClassOf ex:Class1 .

                     ex:instance1 a ex:Class1 ;
                        ex:property "value" .

                     ex:instance2 a ex:Class2 ;
                        ex:property "another value" .
                     """,
                  new Position( 11, 18 ),
                  4,
                  3
            )
      );
   }

   @ParameterizedTest( name = "{0}" )
   @MethodSource( "elementDefinitionScenarios" )
   void testFindElementDefinition( final String scenarioName,
         final String content,
         final Position position,
         final int expectedLine,
         final int expectedCharacter ) {
      final Document document = new Document( "test.ttl", content );
      final ParsedDocument parsedDocument = parserService.apply( document );
      final Optional<Location> definition = definitionService.findDefinition( parsedDocument, position );

      assertThat( definition ).isPresent();
      final Location location = definition.get();
      assertThat( location.getUri() ).isEqualTo( "test.ttl" );
      assertThat( location.getRange().getStart().getLine() ).isEqualTo( expectedLine );
      assertThat( location.getRange().getStart().getCharacter() ).isEqualTo( expectedCharacter );
   }

   static Stream<Arguments> noDefinitionFoundScenarios() {
      return Stream.of(
            Arguments.of(
                  "no matching token",
                  """
                     @prefix ex: <http://example.org/> .

                     ex:subject ex:predicate ex:object .
                     """,
                  "test.ttl",
                  new Position( 1, 0 ),
                  true
            ),
            Arguments.of(
                  "non-existent prefix position",
                  """
                     @prefix ex: <http://example.org/> .

                     ex:subject ex:predicate ex:object .
                     """,
                  "test.ttl",
                  new Position( 10, 0 ),
                  true
            ),
            Arguments.of(
                  "empty document",
                  "",
                  "empty.ttl",
                  new Position( 0, 0 ),
                  true
            ),
            Arguments.of(
                  "unsupported token type (string literal)",
                  """
                     @prefix ex: <http://example.org/> .

                     ex:subject ex:predicate "string literal" .
                     """,
                  "test.ttl",
                  new Position( 2, 24 ),
                  true
            ),
            Arguments.of(
                  "element without type definition",
                  """
                     @prefix ex: <http://example.org/> .

                     ex:subject ex:predicate ex:object .
                     """,
                  "test.ttl",
                  new Position( 2, 30 ),
                  true
            )
      );
   }

   @ParameterizedTest( name = "{0}" )
   @MethodSource( "noDefinitionFoundScenarios" )
   void testFindDefinitionReturnsEmptyForInvalidScenarios( final String scenarioName,
         final String content,
         final String fileName,
         final Position position,
         final boolean buildSyntaxTree ) {
      final Document document = new Document( fileName, content );
      final ParsedDocument parsedDocument = parserService.apply( document );

      if ( buildSyntaxTree && !content.isEmpty() ) {
         final Optional<Location> definition = definitionService.findDefinition( parsedDocument, position );
         assertThat( definition ).isEmpty();
      } else {
         // For empty document, just verify the parser can handle it
         assertThat( parsedDocument ).isNotNull();
         assertThat( parsedDocument.concreteSyntaxTree() ).isNotNull();
      }
   }
}
