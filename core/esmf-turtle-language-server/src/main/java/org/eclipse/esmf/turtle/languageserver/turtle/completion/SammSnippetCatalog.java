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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammCompletionContext.TargetPosition;

import org.jspecify.annotations.Nullable;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

public final class SammSnippetCatalog {
   private static final String SNIPPETS_RESOURCE_PATH = "/snippets/samm-snippets.json";
   private static final Pattern IDENTIFIER = Pattern.compile( "[A-Za-z_][A-Za-z0-9_]*" );
   private static final List<SnippetDefinition> SNIPPETS = loadSnippets();

   public record MatchScore(
         int tier,
         int lengthDiff,
         int matchIndex,
         int catalogIndex
   ) implements Comparable<MatchScore> {
      private static final Comparator<MatchScore> ORDER = Comparator.comparingInt( MatchScore::tier )
            .thenComparingInt( MatchScore::lengthDiff )
            .thenComparingInt( MatchScore::matchIndex )
            .thenComparingInt( MatchScore::catalogIndex );

      @Override
      public int compareTo( final MatchScore other ) {
         return ORDER.compare( this, other );
      }
   }

   public record SnippetDefinition(
         String id,
         String label,
         String defaultFilterText,
         TargetPosition targetPosition,
         boolean allowMultiple,
         List<String> requiredSubjectTypes,
         @Nullable String requiredSubjectPredicate,
         List<String> body
   ) {
      public SnippetDefinition {
         requiredSubjectTypes = requiredSubjectTypes == null ? List.of() : List.copyOf( requiredSubjectTypes );
      }

      public boolean isApplicable( final SammCompletionContext context ) {
         if ( context.targetPosition() != targetPosition ) {
            return false;
         }
         return switch ( targetPosition ) {
            case DOCUMENT_HEADER -> isEligibleDocumentHeader( context );
            case TOP_LEVEL_SUBJECT -> isEligibleTopLevel( context );
            case PREDICATE -> isEligiblePredicate( context );
            case OBJECT_PROPERTIES_LIST, OBJECT_CONSTRAINT, OBJECT_CHARACTERISTIC -> isEligibleAnonymousObject( context );
            case OBJECT_VALUE -> false;
         };
      }

      public String render( final SammCompletionContext context ) {
         final String sammPrefix =
               targetPosition == TargetPosition.PREDICATE ? context.resolvedSammPrefix() : context.namespaces().sammPrefix();
         final String charPrefix = context.namespaces().characteristicPrefix().orElse( "" );
         final String xsdPrefix = context.namespaces().xsdPrefix().orElse( "" );
         final String unitPrefix = context.namespaces().unitPrefix().orElse( "" );
         final String latestVersion = context.namespaces().version().toVersionString();
         final String baseIndent = targetPosition.isAnonymousObject() ? "" : context.edit().indentation();
         return String.join( "\n", body )
               .replace( "{{modelPrefix}}", context.namespaces().modelPrefix().orElse( "" ) )
               .replace( "{{sammPrefix}}", sammPrefix )
               .replace( "{{sammCPrefix}}", charPrefix )
               .replace( "{{xsdPrefix}}", xsdPrefix )
               .replace( "{{unitPrefix}}", unitPrefix )
               .replace( "{{sammVersion}}", latestVersion )
               .replace( "{{separator}}", context.predicateSeparator().orElse( "" ) )
               .replace( "\r\n", "\n" )
               .replace( "\n", context.edit().lineEnding() + baseIndent );
      }

      private boolean usesPlaceholder( final String placeholder ) {
         return body.stream().anyMatch( line -> line.contains( placeholder ) );
      }

      private boolean hasRequiredNamespaces( final SammCompletionContext context ) {
         if ( usesPlaceholder( "{{modelPrefix}}" ) && context.namespaces().modelPrefix().isEmpty() ) {
            return false;
         }
         if ( usesPlaceholder( "{{sammCPrefix}}" ) && context.namespaces().characteristicPrefix().isEmpty() ) {
            return false;
         }
         if ( usesPlaceholder( "{{xsdPrefix}}" ) && context.namespaces().xsdPrefix().isEmpty() ) {
            return false;
         }
         return !usesPlaceholder( "{{unitPrefix}}" ) || context.namespaces().unitPrefix().isPresent();
      }

      private boolean hasRequiredSubjectConstraints( final SammCompletionContext context ) {
         if ( !requiredSubjectTypes.isEmpty()
               && requiredSubjectTypes.stream().noneMatch( context::hasSubjectType ) ) {
            return false;
         }
         return requiredSubjectPredicate == null || context.hasSubjectPredicate( requiredSubjectPredicate );
      }

      private boolean isEligibleDocumentHeader( final SammCompletionContext context ) {
         if ( !context.edit().blankRestOfLine() ) {
            return false;
         }
         final String typed = context.edit().typedText().toLowerCase( Locale.ROOT );
         return typed.isEmpty()
               ? context.namespaces().declarations().isEmpty()
               : "@prefix".startsWith( typed ) || "prefix".startsWith( typed );
      }

      private boolean isEligibleTopLevel( final SammCompletionContext context ) {
         if ( !hasRequiredNamespaces( context ) || !context.edit().blankRestOfLine() ) {
            return false;
         }
         final String typed = context.edit().typedText();
         return typed.isEmpty() || IDENTIFIER.matcher( typed ).matches();
      }

      private boolean isEligiblePredicate( final SammCompletionContext context ) {
         if ( !hasRequiredNamespaces( context )
               || !context.hasAnySammSubjectType()
               || !hasRequiredSubjectConstraints( context )
               || !allowMultiple && context.hasSubjectPredicate( defaultFilterText )
               || context.predicateSeparator().isEmpty() ) {
            return false;
         }
         final String typed = context.edit().typedText();
         if ( typed.isEmpty() ) {
            return true;
         }
         final String actualPrefix = context.resolvedSammPrefix();
         return typed.startsWith( actualPrefix )
               && context.namespaces().sammNamespace().equals( context.namespaces().declarations().get( actualPrefix ) );
      }

      private boolean isEligibleAnonymousObject( final SammCompletionContext context ) {
         if ( !hasRequiredNamespaces( context ) || !hasRequiredSubjectConstraints( context ) ) {
            return false;
         }
         final String typed = context.edit().typedText();
         if ( !typed.startsWith( "[" ) ) {
            return false;
         }
         final String innerTyped = typed.substring( 1 ).trim();
         return innerTyped.isEmpty() || IDENTIFIER.matcher( innerTyped ).matches();
      }
   }

   public List<SnippetDefinition> allSnippets() {
      return SNIPPETS;
   }

   public List<SnippetDefinition> matchingSnippets( final SammCompletionContext context ) {
      record ScoredSnippet(
            SnippetDefinition snippet, MatchScore score
      ) {}

      final String query = extractQuery( context );
      final List<ScoredSnippet> scored = new ArrayList<>();
      for ( int i = 0; i < SNIPPETS.size(); i++ ) {
         final SnippetDefinition snippet = SNIPPETS.get( i );
         if ( !snippet.isApplicable( context ) ) {
            continue;
         }
         final MatchScore score = query.isEmpty() ? new MatchScore( 0, 0, 0, i ) : calculateBestScore( snippet, query, i, context );
         if ( score != null ) {
            scored.add( new ScoredSnippet( snippet, score ) );
         }
      }

      return scored.stream()
            .sorted( Comparator.comparing( ScoredSnippet::score ) )
            .map( ScoredSnippet::snippet )
            .toList();
   }

   public static String extractQuery( final SammCompletionContext context ) {
      final String typed = context.edit().typedText();
      return switch ( context.targetPosition() ) {
         case PREDICATE -> {
            final String actualPrefix = context.resolvedSammPrefix();
            yield typed.startsWith( actualPrefix )
                  ? typed.substring( actualPrefix.length() ).trim()
                  : typed.trim();
         }
         case OBJECT_PROPERTIES_LIST, OBJECT_CONSTRAINT, OBJECT_CHARACTERISTIC ->
            typed.startsWith( "[" ) ? typed.substring( 1 ).trim() : typed.trim();
         case DOCUMENT_HEADER, TOP_LEVEL_SUBJECT, OBJECT_VALUE -> typed.trim();
      };
   }

   private static @Nullable MatchScore calculateBestScore(
         final SnippetDefinition snippet,
         final String query,
         final int catalogIndex,
         final SammCompletionContext context ) {
      final List<String> candidates = new ArrayList<>();
      if ( context.targetPosition() == TargetPosition.DOCUMENT_HEADER ) {
         candidates.add( "@prefix" );
         candidates.add( "prefix" );
      }
      candidates.add( snippet.defaultFilterText() );
      candidates.add( snippet.label() );

      MatchScore best = null;
      for ( final String candidate : candidates ) {
         final MatchScore score = scoreCandidate( candidate, query, catalogIndex );
         if ( score != null && ( best == null || score.compareTo( best ) < 0 ) ) {
            best = score;
         }
      }
      return best;
   }

   static @Nullable MatchScore scoreCandidate(
         final String candidate,
         final String query,
         final int catalogIndex ) {
      if ( query.length() > candidate.length() ) {
         return null;
      }

      // Tier 0: Exact match (case-insensitive)
      if ( candidate.equalsIgnoreCase( query ) ) {
         return new MatchScore( 0, 0, 0, catalogIndex );
      }

      // Tier 1: Prefix match (case-insensitive)
      if ( candidate.regionMatches( true, 0, query, 0, query.length() ) ) {
         return new MatchScore( 1, candidate.length() - query.length(), 0, catalogIndex );
      }

      // Tier 2: Word-boundary or CamelCase start (case-insensitive)
      int bestBoundaryIndex = -1;
      for ( int i = 1; i <= candidate.length() - query.length(); i++ ) {
         if ( isWordOrCamelBoundary( candidate, i ) && candidate.regionMatches( true, i, query, 0, query.length() ) ) {
            bestBoundaryIndex = i;
            break;
         }
      }
      if ( bestBoundaryIndex >= 0 ) {
         return new MatchScore( 2, candidate.length() - query.length(), bestBoundaryIndex, catalogIndex );
      }

      // Tier 3: Substring match (case-insensitive)
      final int substringIndex = indexOfIgnoreCase( candidate, query );
      if ( substringIndex >= 0 ) {
         return new MatchScore( 3, candidate.length() - query.length(), substringIndex, catalogIndex );
      }

      return null;
   }

   private static boolean isWordOrCamelBoundary( final String text, final int index ) {
      final char prev = text.charAt( index - 1 );
      final char curr = text.charAt( index );

      // Word boundary: separator followed by alphanumeric
      if ( ( Character.isWhitespace( prev ) || prev == '-' || prev == '_' || prev == ':' || prev == '/' || prev == '.' )
            && Character.isLetterOrDigit( curr ) ) {
         return true;
      }

      // CamelCase boundary: lowercase or digit followed by uppercase
      if ( Character.isUpperCase( curr ) && ( Character.isLowerCase( prev ) || Character.isDigit( prev ) ) ) {
         return true;
      }

      // Acronym boundary: uppercase followed by lowercase, preceded by uppercase (e.g. XMLParser -> P)
      if ( Character.isUpperCase( curr ) && Character.isUpperCase( prev )
            && index + 1 < text.length() && Character.isLowerCase( text.charAt( index + 1 ) ) ) {
         return true;
      }

      return false;
   }

   private static int indexOfIgnoreCase( final String text, final String query ) {
      final int max = text.length() - query.length();
      for ( int i = 0; i <= max; i++ ) {
         if ( text.regionMatches( true, i, query, 0, query.length() ) ) {
            return i;
         }
      }
      return -1;
   }

   private static List<SnippetDefinition> loadSnippets() {
      try ( final InputStream stream = SammSnippetCatalog.class.getResourceAsStream( SNIPPETS_RESOURCE_PATH ) ) {
         if ( stream == null ) {
            throw new IllegalStateException( "Missing snippet catalog resource: " + SNIPPETS_RESOURCE_PATH );
         }
         return List.copyOf( new ObjectMapper().readValue( stream, new TypeReference<List<SnippetDefinition>>() {} ) );
      } catch ( final IOException exception ) {
         throw new IllegalStateException( "Failed to load snippet catalog resource: " + SNIPPETS_RESOURCE_PATH, exception );
      }
   }
}
