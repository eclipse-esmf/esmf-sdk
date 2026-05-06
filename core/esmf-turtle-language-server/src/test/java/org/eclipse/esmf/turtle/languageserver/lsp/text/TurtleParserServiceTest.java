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

package org.eclipse.esmf.turtle.languageserver.lsp.text;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.treesitter.TSNode;
import org.treesitter.TSTree;

@SuppressWarnings( "HttpUrlsUsage" )
class TurtleParserServiceTest {
   private TreeSitterTurtleParserService parserService;

   @BeforeEach
   void setUp() {
      parserService = new TreeSitterTurtleParserService();
   }

   @Test
   void testInitialParsing() {
      final String content = """
         @prefix ex: <http://example.org/> .

         ex:subject ex:predicate ex:object .
         """;

      final Document document = new Document( "test.ttl", content );
      final TSTree tree = parserService.getAbstractSyntaxTree( document );

      assertThat( tree ).isNotNull();
      final TSNode rootNode = tree.getRootNode();
      assertThat( rootNode ).isNotNull();
      assertThat( rootNode.getType() ).isEqualTo( "document" );
      assertThat( rootNode.hasError() ).isFalse();

      final TSNode prefixDeclaration = rootNode.getChild( 0 );
      assertThat( prefixDeclaration ).isNotNull();
      assertThat( prefixDeclaration.getType() ).isEqualTo( "directive" );
      assertThat( prefixDeclaration.hasError() ).isFalse();

      final TSNode prefixId = prefixDeclaration.getChild( 0 );
      assertThat( prefixId ).isNotNull();
      assertThat( prefixId.getType() ).isEqualTo( "prefix_id" );
      assertThat( prefixId.hasError() ).isFalse();

      final TSNode namespace = prefixId.getChild( 0 );
      assertThat( namespace ).isNotNull();
      assertThat( namespace.getType() ).isEqualTo( "@prefix" );
      assertThat( namespace.hasError() ).isFalse();
   }

   @Test
   void testEmptyDocument() {
      final Document document = new Document( "empty.ttl", "" );
      final TSTree tree = parserService.getAbstractSyntaxTree( document );

      assertThat( tree ).isNotNull();
      final TSNode rootNode = tree.getRootNode();
      assertThat( rootNode ).isNotNull();
      assertThat( rootNode.getChildCount() ).isEqualTo( 0 );
   }

   @Test
   void testSingleLineInsertion() {
      final String initialContent = """
         @prefix ex: <http://example.org/> .
         """;

      final Document document = new Document( "test.ttl", initialContent );
      final TSTree initialTree = parserService.getAbstractSyntaxTree( document );
      assertThat( initialTree.getRootNode().hasError() ).isFalse();

      final String newText = "\nex:subject ex:predicate ex:object .";
      applyChange( document, pos( 1, 0 ), pos( 1, 0 ), newText );

      final TSTree updatedTree = parserService.getAbstractSyntaxTree( document );
      assertThat( updatedTree ).isNotNull();
      assertThat( updatedTree.getRootNode().hasError() ).isFalse();

      assertThat( document.getContent() ).contains( "ex:subject ex:predicate ex:object" );
   }

   @Test
   void testMultiLineInsertion() {
      final String initialContent = "@prefix ex: <http://example.org/> .";
      final Document document = new Document( "test.ttl", initialContent );

      final String newText = """

         ex:subject1 ex:predicate1 ex:object1 .
         ex:subject2 ex:predicate2 ex:object2 .
         ex:subject3 ex:predicate3 ex:object3 .""";

      applyChange( document, pos( 0, initialContent.length() ), pos( 0, initialContent.length() ), newText );

      final TSTree tree = parserService.getAbstractSyntaxTree( document );
      assertThat( tree.getRootNode().hasError() ).isFalse();
      assertThat( document.getContent() )
            .contains( "ex:subject1" )
            .contains( "ex:subject2" )
            .contains( "ex:subject3" );
   }

   @Test
   void testDeletion() {
      final String initialContent = """
         @prefix ex: <http://example.org/> .

         ex:subject ex:predicate ex:object .
         ex:ToDelete ex:willBeDeleted ex:Value .
         """;

      final Document document = new Document( "test.ttl", initialContent );
      parserService.getAbstractSyntaxTree( document );

      applyChange( document, pos( 3, 0 ), pos( 3, 38 ), "" );

      final TSTree tree = parserService.getAbstractSyntaxTree( document );
      assertThat( tree.getRootNode().hasError() ).isFalse();
      assertThat( document.getContent() ).doesNotContain( "ex:ToDelete" );
      assertThat( document.getContent() ).contains( "ex:subject ex:predicate ex:object" );
   }

   @Test
   void testReplacement() {
      final String initialContent = """
         @prefix ex: <http://example.org/> .

         ex:subject ex:predicate ex:OldObject .
         """;

      final Document document = new Document( "test.ttl", initialContent );
      parserService.getAbstractSyntaxTree( document );

      final String replacement = "NewObject";
      applyChange( document, pos( 2, 27 ), pos( 2, 35 ), replacement );

      final TSTree tree = parserService.getAbstractSyntaxTree( document );
      if ( tree.getRootNode().hasError() ) {
         printDocumentAndTree( document, tree );
      }
      assertThat( tree.getRootNode().hasError() ).isFalse();
      assertThat( document.getContent() ).doesNotContain( "OldObject" );
      assertThat( document.getContent() ).contains( "NewObject" );
   }

   @Test
   void testMultipleSequentialEdits() {
      final Document document = new Document( "test.ttl", "" );

      final String text1 = "@prefix ex: <http://example.org/> .";
      applyChange( document, pos( 0, 0 ), pos( 0, 0 ), text1 );

      TSTree tree = parserService.getAbstractSyntaxTree( document );
      assertThat( tree.getRootNode().hasError() ).isFalse();

      final String text2 = "\n\nex:subject1 ex:predicate1 ex:object1 .";
      applyChange( document, pos( 0, 35 ), pos( 0, 35 ), text2 );

      tree = parserService.getAbstractSyntaxTree( document );
      assertThat( tree.getRootNode().hasError() ).isFalse();
      assertThat( document.getContent() ).contains( "ex:subject1" );

      final String text3 = "\nex:subject2 ex:predicate2 ex:object2 .";
      applyChange( document, pos( 2, 38 ), pos( 2, 38 ), text3 );

      tree = parserService.getAbstractSyntaxTree( document );
      assertThat( tree.getRootNode().hasError() ).isFalse();
      assertThat( document.getContent() ).contains( "ex:subject1" )
            .contains( "ex:subject2" );

      applyChange( document, pos( 2, 0 ), pos( 2, 38 ), "" );

      tree = parserService.getAbstractSyntaxTree( document );
      assertThat( tree.getRootNode().hasError() ).isFalse();
      assertThat( document.getContent() ).doesNotContain( "ex:subject1" );
      assertThat( document.getContent() ).contains( "ex:subject2" );
   }

   @Test
   void testEditAcrossMultipleLines() {
      final String initialContent = """
         @prefix ex: <http://example.org/> .

         ex:subject
           ex:predicate1 ex:object1 ;
           ex:predicate2 ex:object2 .
         """;

      final Document document = new Document( "test.ttl", initialContent );
      parserService.getAbstractSyntaxTree( document );

      final String replacement = "ex:newPredicate";
      applyChange( document, pos( 3, 2 ), pos( 4, 14 ), replacement );

      final TSTree tree = parserService.getAbstractSyntaxTree( document );
      System.out.println( TreeSitterUtil.print( tree, document ) );
      assertThat( tree.getRootNode().hasError() ).isFalse();
      assertThat( document.getContent() ).contains( "ex:newPredicate" );
      assertThat( document.getContent() ).doesNotContain( "ex:predicate1" );
   }

   @Test
   void testParseValidSyntax() {
      final String initialContent = """
         # Document top comment
         @prefix ex: <http://example.org/> .

         # Comment on subject
         ex:subject
           ex:predicate1 ex:object1 ;
           ex:predicate2 123 ;
           ex:predicate3 true ;
           # comment on string
           ex:predicate4 "some string" ;
           ex:predicate5 "some langString"@en ;
           ex:predicate6 "123"^^xsd:decimal ;
           ex:predicate7 <http://example.org/fulluri> .
         
         <http://example.com/anotherSubject> a rdf:type .
         """;

      final Document document = new Document( "test.ttl", initialContent );
      final TSTree tree = parserService.getAbstractSyntaxTree( document );
      System.out.println( TreeSitterUtil.print( tree, document ) );
      assertThat( tree.getRootNode().hasError() ).isFalse();
   }

   @Test
   void testPrefixAddition() {
      final String initialContent = "ex:subject ex:predicate ex:object .";

      final Document document = new Document( "test.ttl", initialContent );
      parserService.getAbstractSyntaxTree( document );

      final String prefix = "@prefix ex: <http://example.org/> .\n\n";
      applyChange( document, pos( 0, 0 ), pos( 0, 0 ), prefix );

      final TSTree tree = parserService.getAbstractSyntaxTree( document );
      assertThat( tree.getRootNode().hasError() ).isFalse();
      assertThat( document.getContent() ).startsWith( "@prefix ex:" );
   }

   @Test
   void testComplexEditSequence() {
      final Document document = new Document( "test.ttl", "" );
      TSTree tree;

      applyChange( document, pos( 0, 0 ), pos( 0, 0 ),
            "@base <http://example.org/> .\n@prefix ex: <http://example.org/> .\n\n" );
      tree = parserService.getAbstractSyntaxTree( document );
      assertThat( tree.getRootNode().hasError() ).isFalse();

      applyChange( document, pos( 3, 0 ), pos( 3, 0 ),
            "ex:subject ex:predicate ex:object ." );
      tree = parserService.getAbstractSyntaxTree( document );
      assertThat( tree.getRootNode().hasError() ).isFalse();

      applyChange( document, pos( 3, 33 ), pos( 3, 34 ),
            " ;\n  ex:predicate2 ex:object2 ;\n  ex:predicate3 ex:object3 ." );
      tree = parserService.getAbstractSyntaxTree( document );
      assertThat( tree.getRootNode().hasError() ).isFalse();

      final String content = document.getContent();
      final int predicateStart = content.indexOf( "ex:predicate2" );
      final int line = content.substring( 0, predicateStart ).split( "\n" ).length - 1;
      final int col = predicateStart - content.lastIndexOf( '\n', predicateStart ) - 1;

      applyChange( document, pos( line, col ), pos( line, col + 12 ), "ex:modified" );
      tree = parserService.getAbstractSyntaxTree( document );
      assertThat( tree.getRootNode().hasError() ).isFalse();
      assertThat( document.getContent() ).contains( "ex:modified" );
      assertThat( document.getContent() ).doesNotContain( "ex:predicate2" );

      assertThat( document.getContent() )
            .contains( "@base" )
            .contains( "@prefix" )
            .contains( "ex:subject" )
            .contains( "ex:predicate" )
            .contains( "ex:predicate3" )
            .contains( "ex:modified" );
   }

   @Test
   void testInvalidSyntaxHandling() {
      final String initialContent = "@prefix ex: <http://example.org/> .";
      final Document document = new Document( "test.ttl", initialContent );

      TSTree tree = parserService.getAbstractSyntaxTree( document );
      assertThat( tree.getRootNode().hasError() ).isFalse();

      final String invalidText = "\n\nthis is not valid turtle syntax @#$%";
      applyChange( document, pos( 0, 35 ), pos( 0, 35 ), invalidText );

      tree = parserService.getAbstractSyntaxTree( document );
      assertThat( tree.getRootNode() ).isNotNull();
      // Tree-sitter should still parse it, but with errors
      assertThat( tree.getRootNode().hasError() ).isTrue();
   }

   @Test
   void testFullDocumentChange() {
      final String initialContent = "@prefix ex: <http://example.org/> .";
      final Document document = new Document( "test.ttl", initialContent );

      parserService.getAbstractSyntaxTree( document );
      final String newContent = """
         @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
         @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .

         rdf:type rdf:type rdf:Property .
         """;

      final TextDocumentContentChangeEvent change = new TextDocumentContentChangeEvent();
      change.setText( newContent );
      final Document newDocument = new Document( "test.ttl", newContent );
      parserService.onChange( newDocument, change );

      final TSTree tree = parserService.getAbstractSyntaxTree( newDocument );
      assertThat( tree.getRootNode().hasError() ).isFalse();
      assertThat( newDocument.getContent() ).contains( "rdfs:" );
   }

   @Test
   void testWhitespaceOnlyChanges() {
      final String initialContent = "ex:subject ex:predicate ex:object.";
      final Document document = new Document( "test.ttl", initialContent );
      final TSTree oldTree = parserService.getAbstractSyntaxTree( document );
      final Range range = new Range( pos( 0, 33 ), pos( 0, 33 ) );
      final TextDocumentContentChangeEvent change = new TextDocumentContentChangeEvent( range, " " );
      document.update( range, " " );
      parserService.onChange( document, change );

      final TSTree tree = parserService.getAbstractSyntaxTree( document );
      assertThat( tree.getRootNode().hasError() ).isFalse();
      assertThat( document.getContent() ).endsWith( "object ." );
      assertThat( oldTree.getRootNode().toString() ).isEqualTo( tree.getRootNode().toString() );
   }

   void printDocumentAndTree( final Document document, final TSTree tree ) {
      System.out.println( "Document" );
      System.out.println( "--------" );
      System.out.println( document.getContent() );
      System.out.println();
      System.out.println( "Tree" );
      System.out.println( "----" );
      System.out.println( TreeSitterUtil.print( tree ) );
   }

   private Position pos( final int line, final int character ) {
      return new Position( line, character );
   }

   private void applyChange( final Document document, final Position start, final Position end, final String text ) {
      final Range range = new Range( start, end );
      final TextDocumentContentChangeEvent change = new TextDocumentContentChangeEvent( range, text );
      document.update( range, text );
      parserService.onChange( document, change );
   }
}


