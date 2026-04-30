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

package org.eclipse.esmf.treesitterturtle;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.treesitter.TSLanguage;
import org.treesitter.TSNode;
import org.treesitter.TSParser;
import org.treesitter.TSTree;

@SuppressWarnings( "HttpUrlsUsage" )
public class TreeSitterTurtleTest {
   private TSParser parser;

   @BeforeEach
   void setUp() {
      parser = new TSParser();
      final TSLanguage turtle = new TreeSitterTurtle();
      parser.setLanguage( turtle );
   }

   @Test
   void testBasicPrefixAndTriple() {
      final String content = """
         @prefix : <http://example.com> .
         :a a :b .
         """;
      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();
      assertThat( print( rootNode, content ) ).doesNotContain( "ERROR" );
      assertThat( rootNode.hasError() ).isFalse();
      assertThat( rootNode.getChild( 0 ).getChild( 0 ).getChild( 0 ).getGrammarType() ).isEqualTo( "@prefix" );
   }

   @Test
   void testNumericLiterals() {
      final String content = """
         @prefix ex: <http://example.org/> .

         ex:entity ex:intValue 42 .
         ex:entity ex:decimalValue 3.14 .
         ex:entity ex:doubleValue 1.23e10 .
         ex:entity ex:negativeInt -100 .
         ex:entity ex:positiveDouble +2.5 .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      assertThat( rootNode.hasError() ).isFalse();
      assertThat( print( rootNode, content ) ).doesNotContain( "ERROR" );

      // Verify the document parses successfully with numeric literals
      final String treeString = print( rootNode, content );
      assertThat( treeString ).contains( "42" );
      assertThat( treeString ).contains( "3.14" );
      assertThat( treeString ).contains( "1.23e10" );
   }

   @Test
   void testStringLiterals() {
      final String content = """
         @prefix ex: <http://example.org/> .

         ex:entity ex:simpleString "Hello World" .
         ex:entity ex:stringWithEscapes "Line 1\\nLine 2\\tTabbed" .
         ex:entity ex:multilineString \"\"\"This is a
         multiline
         string literal\"\"\" .
         ex:entity ex:singleQuote 'Single quoted string' .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      assertThat( rootNode.hasError() ).isFalse();
      assertThat( print( rootNode, content ) ).doesNotContain( "ERROR" );
   }

   @Test
   void testBooleanLiterals() {
      final String content = """
         @prefix ex: <http://example.org/> .

         ex:entity ex:isTrue true .
         ex:entity ex:isFalse false .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      assertThat( rootNode.hasError() ).isFalse();
      assertThat( print( rootNode, content ) ).doesNotContain( "ERROR" );
      assertThat( print( rootNode, content ) ).contains( "true" );
      assertThat( print( rootNode, content ) ).contains( "false" );
   }

   @Test
   void testLanguageTaggedLiterals() {
      final String content = """
         @prefix ex: <http://example.org/> .
         @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .

         ex:entity rdfs:label "Hello"@en .
         ex:entity rdfs:label "Hallo"@de .
         ex:entity rdfs:label "Bonjour"@fr .
         ex:entity rdfs:label "こんにちは"@ja .
         ex:entity rdfs:comment "Multi-word label"@en-US .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      assertThat( rootNode.hasError() ).isFalse();
      assertThat( print( rootNode, content ) ).doesNotContain( "ERROR" );
      assertThat( print( rootNode, content ) ).contains( "@en" );
      assertThat( print( rootNode, content ) ).contains( "@de" );
      assertThat( print( rootNode, content ) ).contains( "@fr" );
   }

   @Test
   void testTypedLiterals() {
      final String content = """
         @prefix ex: <http://example.org/> .
         @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

         ex:entity ex:dateValue "2026-04-30"^^xsd:date .
         ex:entity ex:intValue "42"^^xsd:integer .
         ex:entity ex:boolValue "true"^^xsd:boolean .
         ex:entity ex:customType "custom value"^^ex:MyType .
         ex:entity ex:fullUri "value"^^<http://example.org/types#CustomType> .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      assertThat( rootNode.hasError() ).isFalse();
      assertThat( print( rootNode, content ) ).doesNotContain( "ERROR" );
      assertThat( print( rootNode, content ) ).contains( "^^" );
   }

   @Test
   void testRdfListSyntax() {
      final String content = """
         @prefix ex: <http://example.org/> .
         @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .

         ex:entity ex:emptyList () .
         ex:entity ex:numberList (1 2 3 4 5) .
         ex:entity ex:stringList ("a" "b" "c") .
         ex:entity ex:mixedList (1 "two" 3.0 true ex:resource) .
         ex:entity ex:nestedList (1 (2 3) 4) .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      assertThat( rootNode.hasError() ).isFalse();
      assertThat( print( rootNode, content ) ).doesNotContain( "ERROR" );
      assertThat( print( rootNode, content ) ).contains( "(" );
      assertThat( print( rootNode, content ) ).contains( ")" );
   }

   @Test
   void testAnonymousNodes() {
      final String content = """
         @prefix ex: <http://example.org/> .

         ex:entity ex:hasBlankNode [
            ex:property1 "value1" ;
            ex:property2 "value2"
         ] .

         ex:entity ex:simpleBlank [ ex:prop "val" ] .

         ex:entity ex:nestedBlank [
            ex:inner [
               ex:nested "deeply"
            ]
         ] .

         # Blank node with multiple predicates and objects
         ex:person [
            ex:firstName "John" ;
            ex:lastName "Doe" ;
            ex:age 30 ;
            ex:knows [
               ex:firstName "Jane" ;
               ex:lastName "Smith"
            ]
         ] .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      assertThat( rootNode.hasError() ).isFalse();
      assertThat( print( rootNode, content ) ).doesNotContain( "ERROR" );
      assertThat( print( rootNode, content ) ).contains( "[" );
      assertThat( print( rootNode, content ) ).contains( "]" );
   }

   @Test
   void testComplexDocument() {
      final String content = """
         @prefix ex: <http://example.org/> .
         @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
         @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
         @base <http://example.org/base/> .

         ex:Person a ex:Class ;
            ex:name "Person"@en ;
            ex:properties (
               ex:firstName
               ex:lastName
               ex:age
            ) .

         ex:john a ex:Person ;
            ex:firstName "John" ;
            ex:lastName "Doe" ;
            ex:age "30"^^xsd:integer ;
            ex:active true ;
            ex:salary 50000.50 ;
            ex:address [
               ex:street "123 Main St" ;
               ex:city "Anytown" ;
               ex:country "USA"@en
            ] ;
            ex:hobbies ("reading" "coding" "music") .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      assertThat( rootNode.hasError() ).isFalse();
      assertThat( print( rootNode, content ) ).doesNotContain( "ERROR" );
   }

   @Test
   void testBrokenSyntaxMissingDot() {
      final String content = """
         @prefix ex: <http://example.org/>

         ex:entity ex:property "value" .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      // Should have errors due to missing dot after prefix declaration
      assertThat( rootNode.hasError() ).isTrue();
      assertThat( print( rootNode, content ) ).contains( "ERROR" );
   }

   @Test
   void testBrokenSyntaxInvalidUri() {
      final String content = """
         @prefix ex: <http://example.org/> .

         ex:entity ex:property <invalid uri with spaces> .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      // Should have errors due to invalid URI
      assertThat( rootNode.hasError() ).isTrue();
      assertThat( print( rootNode, content ) ).contains( "ERROR" );
   }

   @Test
   void testBrokenSyntaxUnterminatedString() {
      final String content = """
         @prefix ex: <http://example.org/> .

         ex:entity ex:property "unterminated string .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      // Should have errors due to unterminated string
      assertThat( rootNode.hasError() ).isTrue();
      assertThat( print( rootNode, content ) ).contains( "ERROR" );
   }

   @Test
   void testBrokenSyntaxMismatchedBrackets() {
      final String content = """
         @prefix ex: <http://example.org/> .

         ex:entity ex:property [
            ex:nested "value"
         .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      // Should have errors due to mismatched brackets
      assertThat( rootNode.hasError() ).isTrue();
      assertThat( print( rootNode, content ) ).contains( "ERROR" );
   }

   @Test
   void testBrokenSyntaxInvalidPrefix() {
      final String content = """
         @prefix 123invalid: <http://example.org/> .

         123invalid:entity ex:property "value" .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      // Should have errors due to invalid prefix name (starts with number)
      assertThat( rootNode.hasError() ).isTrue();
      assertThat( print( rootNode, content ) ).contains( "ERROR" );
   }

   @Test
   void testBrokenSyntaxIncompleteTriple() {
      final String content = """
         @prefix ex: <http://example.org/> .

         ex:entity ex:property .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      // Should have errors due to missing object
      assertThat( rootNode.hasError() ).isTrue();
      assertThat( print( rootNode, content ) ).contains( "ERROR" );
   }

   @Test
   void testSemicolonAndCommaSyntax() {
      final String content = """
         @prefix ex: <http://example.org/> .

         ex:entity
            ex:prop1 "value1" ;
            ex:prop2 "value2" , "value3" , "value4" ;
            ex:prop3 "value5" .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      assertThat( rootNode.hasError() ).isFalse();
      assertThat( print( rootNode, content ) ).doesNotContain( "ERROR" );
   }

   @Test
   void testComments() {
      final String content = """
         # This is a comment
         @prefix ex: <http://example.org/> .

         # Another comment
         ex:entity ex:property "value" . # Inline comment

         # Multi-line comments
         # are also supported
         ex:entity2 ex:property2 "value2" .
         """;

      final TSTree tree = parser.parseString( null, content );
      final TSNode rootNode = tree.getRootNode();

      assertThat( rootNode.hasError() ).isFalse();
      assertThat( print( rootNode, content ) ).doesNotContain( "ERROR" );
   }

   public static String print( final TSNode node, final String source ) {
      final StringBuilder builder = new StringBuilder();
      print( node, builder, 0, source );
      return builder.toString();
   }

   private static void print( final TSNode node, final StringBuilder builder, final int indentLevel, final String source ) {
      builder.repeat( "  ", indentLevel );
      builder.append( "- '" );
      builder.append( node.getType() );
      builder.append( "'" );
      if ( node.hasError() ) {
         builder.append( " (ERROR)" );
      } else if ( node.getStartPoint().getRow() == node.getEndPoint().getRow() ) {
         final String nodeContent = getSubDocument( source, node.getStartPoint().getRow(), node.getStartPoint().getColumn(),
               node.getEndPoint().getRow(), node.getEndPoint().getColumn() );
         if ( !nodeContent.equals( node.getType() ) ) {
            builder.append( " (" );
            builder.append( nodeContent );
            builder.append( ")" );
         }
      }
      builder.append( "\n" );
      for ( int i = 0; i < node.getChildCount(); i++ ) {
         print( node.getChild( i ), builder, indentLevel + 1, source );
      }
   }

   /**
    * Extracts a substring from a multi-line string using line and column coordinates.
    *
    * @param originalString the source string
    * @param fromLine the starting line (0-based)
    * @param fromColumn the starting column (0-based)
    * @param toLine the ending line (0-based)
    * @param toColumn the ending column (0-based, exclusive)
    * @return the extracted substring
    */
   private static String getSubDocument( final String originalString, final int fromLine, final int fromColumn,
         final int toLine, final int toColumn ) {
      if ( originalString == null || originalString.isEmpty() ) {
         return "";
      }

      // Calculate the start index by navigating to the start line
      int currentLine = 0;
      int i = 0;

      while ( i < originalString.length() && currentLine < fromLine ) {
         if ( originalString.charAt( i ) == '\n' ) {
            currentLine++;
         }
         i++;
      }

      final int startIndex = i + fromColumn;

      // Calculate the end index by navigating to the end line
      currentLine = 0;
      i = 0;

      while ( i < originalString.length() && currentLine < toLine ) {
         if ( originalString.charAt( i ) == '\n' ) {
            currentLine++;
         }
         i++;
      }

      final int endIndex = i + toColumn;

      // Clamp indices to valid range
      final int clampedStart = Math.clamp( startIndex, 0, originalString.length() );
      final int clampedEnd = Math.clamp( endIndex, clampedStart, originalString.length() );

      return originalString.substring( clampedStart, clampedEnd );
   }
}
