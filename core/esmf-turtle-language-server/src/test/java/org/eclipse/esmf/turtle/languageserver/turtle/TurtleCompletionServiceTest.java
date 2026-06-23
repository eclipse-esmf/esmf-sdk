/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.turtle.languageserver.turtle;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

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
                  "typing a known SAMM prefix returns vocabulary completions",
                  """
                     @prefix samm-e: <urn:samm:org.eclipse.esmf.samm:entity:2.2.0#> .

                     samm-e:""",
                  new Position( 2, 6 ),
                  List.of( "TimeSeriesEntity", "Point3d", "FileResource", "Quantity", "resource", "mimeType", "timestamp", "value",
                        "x", "y", "z", "unit" )
            ),
            Arguments.of(
                  "typing inside the local-name part for a SAMM prefix still resolves by namespace",
                  """
                     @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
                     @prefix samm-e: <urn:samm:org.eclipse.esmf.samm:entity:2.2.0#> .

                     samm-e:Tim""",
                  new Position( 3, 8 ),
                  List.of( "TimeSeriesEntity", "Point3d", "FileResource", "Quantity", "resource", "mimeType", "timestamp", "value",
                        "x", "y", "z", "unit" )
            ),
            Arguments.of(
                  "typing partial object with default prefix returns local names from the document",
                  """
                     @prefix : <http://example.org/> .

                     :subject :predicate :object .
                     :subject :otherPredicate :object .
                     :subject :predicate :obj""",
                  new Position( 4, 21 ),
                  List.of( "subject", "predicate", "object", "otherPredicate", "obj" )
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
