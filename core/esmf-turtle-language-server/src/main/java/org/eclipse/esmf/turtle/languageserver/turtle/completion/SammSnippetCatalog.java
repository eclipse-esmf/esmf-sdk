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

import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammSuggestionContextDetector.CursorContext;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammSuggestionContextDetector.TargetPosition;

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
      @Override
      public int compareTo( final MatchScore other ) {
         int cmp = Integer.compare( this.tier, other.tier );
         if ( cmp != 0 ) {
            return cmp;
         }
         cmp = Integer.compare( this.lengthDiff, other.lengthDiff );
         if ( cmp != 0 ) {
            return cmp;
         }
         cmp = Integer.compare( this.matchIndex, other.matchIndex );
         if ( cmp != 0 ) {
            return cmp;
         }
         return Integer.compare( this.catalogIndex, other.catalogIndex );
      }
   }

   public record SnippetDefinition(
         String id,
         String label,
         String defaultFilterText,
         TargetPosition targetPosition,
         boolean requiresModelPrefix,
         boolean requiresCharacteristicPrefix,
         boolean requiresXsdPrefix,
         boolean requiresUnitPrefix,
         boolean allowMultiple,
         @Nullable String requiredSubjectType,
         @Nullable List<String> requiredSubjectTypes,
         @Nullable String requiredSubjectPredicate,
         @Nullable String targetPredicate,
         List<String> body
   ) {
      public boolean isApplicable( final CursorContext context ) {
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

      public String render( final CursorContext context ) {
         final String sammPrefix = targetPosition == TargetPosition.PREDICATE ? context.resolvedSammPrefix() : context.sammPrefix();
         final String charPrefix = context.characteristicPrefix().orElse( "samm-c:" );
         final String xsdPrefix = context.xsdPrefix().orElse( "xsd:" );
         final String unitPrefix = context.unitPrefix().orElse( "unit:" );
         final String latestVersion = context.version().toVersionString();
         return String.join( "\n", body )
               .replace( "{{modelPrefix}}", context.modelPrefix().orElse( "" ) )
               .replace( "{{sammPrefix}}", sammPrefix )
               .replace( "{{sammCPrefix}}", charPrefix )
               .replace( "{{xsdPrefix}}", xsdPrefix )
               .replace( "{{unitPrefix}}", unitPrefix )
               .replace( "{{sammVersion}}", latestVersion )
               .replace( "{{separator}}", context.predicateSeparator().orElse( "" ) )
               .replace( "\n", context.lineEnding() + context.indentation() );
      }

      private boolean hasRequiredNamespaces( final CursorContext context ) {
         if ( requiresModelPrefix && context.modelPrefix().isEmpty() ) {
            return false;
         }
         if ( requiresCharacteristicPrefix && context.characteristicPrefix().isEmpty() ) {
            return false;
         }
         if ( requiresXsdPrefix && context.xsdPrefix().isEmpty() ) {
            return false;
         }
         return !requiresUnitPrefix || context.unitPrefix().isPresent();
      }

      private boolean hasRequiredSubjectConstraints( final CursorContext context ) {
         if ( requiredSubjectType != null && !context.hasSubjectType( requiredSubjectType ) ) {
            return false;
         }
         if ( requiredSubjectTypes != null && !requiredSubjectTypes.isEmpty()
               && requiredSubjectTypes.stream().noneMatch( context::hasSubjectType ) ) {
            return false;
         }
         return requiredSubjectPredicate == null || context.hasSubjectPredicate( requiredSubjectPredicate );
      }

      private boolean isEligibleDocumentHeader( final CursorContext context ) {
         if ( !context.blankRestOfLine() ) {
            return false;
         }
         final String typed = context.typedText().toLowerCase( Locale.ROOT );
         return typed.isEmpty()
               ? context.namespaces().isEmpty()
               : "@prefix".startsWith( typed ) || "prefix".startsWith( typed );
      }

      private boolean isEligibleTopLevel( final CursorContext context ) {
         if ( !hasRequiredNamespaces( context ) || !context.blankRestOfLine() ) {
            return false;
         }
         final String typed = context.typedText();
         return typed.isEmpty() || IDENTIFIER.matcher( typed ).matches();
      }

      private boolean isEligiblePredicate( final CursorContext context ) {
         if ( !hasRequiredNamespaces( context )
               || !context.hasAnySammSubjectType()
               || !hasRequiredSubjectConstraints( context )
               || targetPredicate != null && !allowMultiple && context.hasSubjectPredicate( targetPredicate )
               || context.predicateSeparator().isEmpty() ) {
            return false;
         }
         final String typed = context.typedText();
         if ( typed.isEmpty() ) {
            return true;
         }
         final String actualPrefix = context.resolvedSammPrefix();
         return typed.startsWith( actualPrefix )
               && context.sammNamespace().equals( context.namespaces().get( actualPrefix ) );
      }

      private boolean isEligibleAnonymousObject( final CursorContext context ) {
         if ( !hasRequiredNamespaces( context ) || !hasRequiredSubjectConstraints( context ) ) {
            return false;
         }
         final String typed = context.typedText();
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

   public List<SnippetDefinition> matchingSnippets( final CursorContext context ) {
      final List<SnippetDefinition> applicable = new ArrayList<>();
      final List<Integer> catalogIndices = new ArrayList<>();
      for ( int i = 0; i < SNIPPETS.size(); i++ ) {
         final SnippetDefinition snippet = SNIPPETS.get( i );
         if ( snippet.isApplicable( context ) ) {
            applicable.add( snippet );
            catalogIndices.add( i );
         }
      }

      final String query = extractQuery( context );
      if ( query.isEmpty() ) {
         return applicable;
      }

      record ScoredSnippet( SnippetDefinition snippet, MatchScore score ) {}

      final List<ScoredSnippet> scored = new ArrayList<>();
      for ( int i = 0; i < applicable.size(); i++ ) {
         final SnippetDefinition snippet = applicable.get( i );
         final int catalogIndex = catalogIndices.get( i );
         final MatchScore score = calculateBestScore( snippet, query, catalogIndex, context );
         if ( score != null ) {
            scored.add( new ScoredSnippet( snippet, score ) );
         }
      }

      return scored.stream()
            .sorted( Comparator.comparing( ScoredSnippet::score ) )
            .map( ScoredSnippet::snippet )
            .toList();
   }

   public static String extractQuery( final CursorContext context ) {
      final String typed = context.typedText();
      return switch ( context.targetPosition() ) {
         case DOCUMENT_HEADER -> typed.trim();
         case TOP_LEVEL_SUBJECT -> typed.trim();
         case PREDICATE -> {
            if ( typed.isEmpty() ) {
               yield "";
            }
            final String actualPrefix = context.resolvedSammPrefix();
            if ( typed.startsWith( actualPrefix ) ) {
               yield typed.substring( actualPrefix.length() ).trim();
            }
            final int colon = typed.indexOf( ':' );
            if ( colon >= 0 ) {
               yield typed.substring( colon + 1 ).trim();
            }
            yield typed.trim();
         }
         case OBJECT_PROPERTIES_LIST, OBJECT_CONSTRAINT, OBJECT_CHARACTERISTIC -> {
            if ( typed.startsWith( "[" ) ) {
               yield typed.substring( 1 ).trim();
            }
            yield typed.trim();
         }
         case OBJECT_VALUE -> typed.trim();
      };
   }

   private static @Nullable MatchScore calculateBestScore(
         final SnippetDefinition snippet,
         final String query,
         final int catalogIndex,
         final CursorContext context ) {
      final List<String> candidates = new ArrayList<>();
      if ( context.targetPosition() == TargetPosition.DOCUMENT_HEADER ) {
         candidates.add( "@prefix" );
         candidates.add( "prefix" );
      }
      if ( snippet.targetPredicate() != null ) {
         candidates.add( snippet.targetPredicate() );
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
