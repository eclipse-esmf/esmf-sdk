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

package org.eclipse.esmf.turtle.languageserver.turtle.completion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammSnippetCatalog.MatchScore;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammSnippetCatalog.SnippetDefinition;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SammSnippetCatalogTest {
   private static final String HEADER = """
      @prefix : <urn:samm:example.test:1.0.0#> .
      @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
      @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
      @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
      @prefix unit: <urn:samm:org.eclipse.esmf.samm:unit:2.2.0#> .
      """;

   private final TreeSitterTurtleParserService parser = new TreeSitterTurtleParserService();
   private final SammSnippetCatalog catalog = new SammSnippetCatalog();

   @AfterEach
   void close() {
      parser.close();
   }

   @ParameterizedTest
   @MethodSource( "candidateScores" )
   void scoresCandidates( final String candidate, final String query, final MatchScore expected ) {
      assertThat( SammSnippetCatalog.scoreCandidate( candidate, query, 7 ) ).isEqualTo( expected );
   }

   private static Stream<Arguments> candidateScores() {
      return Stream.of(
            Arguments.of( "Property", "PROPERTY", new MatchScore( 0, 0, 0, 7 ) ),
            Arguments.of( "Property", "pro", new MatchScore( 1, 5, 0, 7 ) ),
            Arguments.of( "AbstractProperty", "prop", new MatchScore( 2, 12, 8, 7 ) ),
            Arguments.of( "SAMM Property", "prop", new MatchScore( 2, 9, 5, 7 ) ),
            Arguments.of( "XMLParser", "parser", new MatchScore( 2, 3, 3, 7 ) ),
            Arguments.of( "description", "rip", new MatchScore( 3, 8, 4, 7 ) ),
            Arguments.of( "Property", "unknown", null ),
            Arguments.of( "Property", "PropertyLonger", null ) );
   }

   @Test
   void normalizesMissingSubjectTypesAndLoadsSingleAndMultipleTypeConstraints() {
      assertThat( catalog.allSnippets() ).allSatisfy( snippet -> assertThat( snippet.requiredSubjectTypes() ).isNotNull() );
      assertThat( catalog.allSnippets().stream().filter( snippet -> snippet.id().equals( "samm-description" ) ).findFirst().orElseThrow()
            .requiredSubjectTypes() ).isEmpty();
      assertThat( catalog.allSnippets().stream().filter( snippet -> snippet.id().equals( "samm-example-value" ) ).findFirst().orElseThrow()
            .requiredSubjectTypes() ).containsExactly( "Property" );
      assertThat( catalog.allSnippets().stream().filter( snippet -> snippet.id().equals( "samm-anon-property-not-in-payload" ) )
            .findFirst().orElseThrow().requiredSubjectTypes() ).containsExactly( "Entity", "AbstractEntity" );
   }

   @Test
   void comparesScoresByTierThenLengthThenMatchIndexThenCatalogOrder() {
      final MatchScore exact = new MatchScore( 0, 0, 0, 20 );
      final MatchScore prefix = new MatchScore( 1, 20, 0, 10 );
      final MatchScore shorter = new MatchScore( 2, 4, 8, 9 );
      final MatchScore earlierMatch = new MatchScore( 2, 5, 2, 8 );
      final MatchScore earlierCatalogEntry = new MatchScore( 2, 5, 3, 6 );
      final MatchScore laterCatalogEntry = new MatchScore( 2, 5, 3, 7 );
      final MatchScore substring = new MatchScore( 3, 0, 0, 0 );
      assertThat( Stream.of( substring, laterCatalogEntry, earlierCatalogEntry, earlierMatch, shorter, prefix, exact ).sorted().toList() )
            .containsExactly( exact, prefix, shorter, earlierMatch, earlierCatalogEntry, laterCatalogEntry, substring );
      assertThat( exact.compareTo( new MatchScore( 0, 0, 0, 20 ) ) ).isZero();
   }

   @ParameterizedTest
   @MethodSource( "snippetMatches" )
   void preservesMatchingOrderAndSubjectEligibility( final String markedBody, final List<String> expectedIds ) {
      final String markedSource = HEADER + markedBody;
      final int cursor = markedSource.indexOf( '|' );
      final String source = markedSource.substring( 0, cursor ) + markedSource.substring( cursor + 1 );
      final ParsedDocument document = parser.apply( new Document( "file:///test.ttl", source ) );
      final SammCompletionContext context = new SammSuggestionContextDetector()
            .detect( document, SammSuggestionContextDetector.positionAt( source, cursor ) ).orElseThrow();
      assertThat( catalog.matchingSnippets( context ) ).extracting( SnippetDefinition::id ).containsExactlyElementsOf( expectedIds );
   }

   private static Stream<Arguments> snippetMatches() {
      return Stream.of(
            Arguments.of( ":p a samm:Property ;\n   | .",
                  List.of( "samm-description", "samm-preferred-name", "samm-see", "samm-example-value" ) ),
            Arguments.of( ":A a samm:Aspect ;\n   | .", List.of( "samm-description", "samm-preferred-name", "samm-see" ) ),
            Arguments.of( ":p a samm:Property ; samm:exampleValue \"value\" ;\n   | .",
                  List.of( "samm-description", "samm-preferred-name", "samm-see" ) ),
            Arguments.of( ":A a samm:Aspect ;\n   samm:p| .", List.of( "samm-preferred-name", "samm-description" ) ),
            Arguments.of( "\nPROP|", List.of( "samm-property", "samm-abstract-property" ) ),
            Arguments.of( "\nentity|", List.of( "samm-entity", "samm-c-single-entity", "samm-abstract-entity" ) ),
            Arguments.of( ":E a samm:Entity ; samm:properties ( [not|] ) .", List.of( "samm-anon-property-not-in-payload" ) ),
            Arguments.of( ":E a samm:AbstractEntity ; samm:properties ( [not|] ) .", List.of( "samm-anon-property-not-in-payload" ) ),
            Arguments.of( ":A a samm:Aspect ; samm:properties ( [not|] ) .", List.of() ),
            Arguments.of( ":T a samm-c:Trait ; samm-c:constraint [len|] .", List.of( "samm-anon-c-length-constraint" ) ),
            Arguments.of( "\nnoSuchSnippet|", List.of() ) );
   }
}
