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

package org.eclipse.esmf.turtle.languageserver.aspect.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.Diagnostic;
import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectDiagnosticCode;
import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectDocumentDiagnostic;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticsProvider;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleService;

public class AspectModelNamingConventionValidationService extends TurtleService implements DiagnosticsProvider {
   private static final AspectDiagnosticCode NAMING_CONVENTION_CODE = new AspectDiagnosticCode( "WARN_NAMING_CONVENTION" );
   private static final AspectDiagnosticCode NAMING_BEST_PRACTICE_CODE = new AspectDiagnosticCode( "WARN_NAMING_BEST_PRACTICE" );
   private static final Pattern UPPERCASE_ACRONYM = Pattern.compile( "\\p{Upper}{2,}" );
   private static final String SAMM_DESCRIPTION_PREDICATE = RdfUtil.curie( SammNs.SAMM.description().getURI() );
   private static final Set<String> UPPERCASE_TYPES;
   private static final Set<String> LOWERCASE_TYPES;
   private static final String NAMING_CONVENTION_DOCS =
         "https://eclipse-esmf.github.io/samm-specification/snapshot/modeling-guidelines.html#naming-rules";
   private static final String BEST_PRACTICES_DOCS =
         "https://eclipse-esmf.github.io/samm-specification/snapshot/appendix/best-practices.html";

   static {
      UPPERCASE_TYPES = Stream.concat(
            Stream.of( SammNs.SAMM.Aspect(), SammNs.SAMM.AbstractEntity(), SammNs.SAMM.Entity(), SammNs.SAMM.Event(),
                  SammNs.SAMM.Constraint(), SammNs.SAMM.Characteristic() ),
            SammNs.SAMMC.allResources().stream() )
            .map( resource -> RdfUtil.curie( resource.getURI() ) )
            .collect( Collectors.toUnmodifiableSet() );
      LOWERCASE_TYPES = Stream.of( SammNs.SAMM.Property(), SammNs.SAMM.AbstractProperty(), SammNs.SAMM.Operation() )
            .map( resource -> RdfUtil.curie( resource.getURI() ) )
            .collect( Collectors.toUnmodifiableSet() );
   }

   @Override
   public DiagnosticReport validate( final ParsedDocument parsedDocument ) {
      if ( !documentIsAspectModel( parsedDocument ) ) {
         return DiagnosticReport.EMPTY;
      }
      return new DiagnosticReport(
            parsedDocument.turtleSyntaxTree().tokens()
                  .filter( token -> ParserTokenType.TRIPLE.equals( token.type() ) )
                  .flatMap( triple -> checkNamingConvention( triple, parsedDocument ) )
                  .toList() );
   }

   private Stream<Diagnostic<?>> checkNamingConvention( final TurtleSyntaxTree.Token triple, final ParsedDocument parsedDocument ) {
      final Map<TurtleSyntaxTree.Node, TurtleSyntaxTree.Node> predicateObjectMap = predicateObjectMapForTriple( triple );
      final Stream.Builder<Diagnostic<?>> diagnostics = Stream.builder();

      subjectLocalName( triple ).ifPresent( node -> {
         final boolean isUppercaseType = objectsForPredicates( predicateObjectMap, TYPE_DEFINITION_PREDICATES )
               .map( TurtleSyntaxTree.Node::content )
               .anyMatch( UPPERCASE_TYPES::contains );
         if ( isUppercaseType ) {
            uppercaseFirstLetterDiagnostic( node, parsedDocument )
                  .ifPresent( diagnostics::add );
         }
         final boolean isLowercaseType = objectsForPredicates( predicateObjectMap, TYPE_DEFINITION_PREDICATES )
               .map( TurtleSyntaxTree.Node::content )
               .anyMatch( LOWERCASE_TYPES::contains );
         if ( isLowercaseType ) {
            lowercaseFirstLetterDiagnostic( node, parsedDocument )
                  .ifPresent( diagnostics::add );
         }
         acronymCasingDiagnostic( node, parsedDocument ).ifPresent( diagnostics::add );
         redundantTypeNameDiagnostic( node, predicateObjectMap, parsedDocument ).ifPresent( diagnostics::add );
      } );

      descriptionValues( predicateObjectMap ).forEach( description ->
            descriptionDiagnostic( description, parsedDocument ).forEach( diagnostics::add )
      );

      return diagnostics.build();
   }

   private static Optional<Diagnostic<?>> uppercaseFirstLetterDiagnostic( final TurtleSyntaxTree.Node node,
         final ParsedDocument parsedDocument ) {
      final String content = stripQuotes( node.content() );
      if ( content.isEmpty() || Character.isUpperCase( content.charAt( 0 ) ) ) {
         return Optional.empty();
      }
      final String suggestion = Character.toUpperCase( content.charAt( 0 ) ) + content.substring( 1 );
      final String message = ( "'%s' should start with an uppercase letter, e.g. '%s'%n%s" ).formatted( content, suggestion,
            NAMING_CONVENTION_DOCS );
      return Optional.of(
            new AspectDocumentDiagnostic( message, NAMING_CONVENTION_CODE, parsedDocument.getUri(), node.location(),
                  Diagnostic.Severity.WARNING ) );
   }

   private static Optional<Diagnostic<?>> lowercaseFirstLetterDiagnostic( final TurtleSyntaxTree.Node node,
         final ParsedDocument parsedDocument ) {
      final String content = stripQuotes( node.content() );
      if ( content.isEmpty() || Character.isLowerCase( content.charAt( 0 ) ) ) {
         return Optional.empty();
      }
      final String suggestion = Character.toLowerCase( content.charAt( 0 ) ) + content.substring( 1 );
      final String message = "'%s' should start with a lowercase letter, e.g. '%s'%n%s".formatted( content, suggestion,
            NAMING_CONVENTION_DOCS );
      return Optional.of(
            new AspectDocumentDiagnostic( message, NAMING_CONVENTION_CODE, parsedDocument.getUri(), node.location(),
                  Diagnostic.Severity.WARNING ) );
   }

   private static List<Diagnostic<?>> descriptionDiagnostic( final TurtleSyntaxTree.Node node,
         final ParsedDocument parsedDocument ) {
      final List<Diagnostic<?>> diagnostics = new ArrayList<>();
      final String content = stripQuotes( node.content() );
      if ( !content.isEmpty() && content.charAt( content.length() - 1 ) != '.' ) {
         final String message = "Description should end with a period%n%s".formatted( BEST_PRACTICES_DOCS );
         diagnostics.add( new AspectDocumentDiagnostic( message, NAMING_BEST_PRACTICE_CODE, parsedDocument.getUri(), node.location(),
               Diagnostic.Severity.WARNING ) );
      }
      if ( !content.isEmpty() && Character.isLowerCase( content.charAt( 0 ) ) ) {
         final String message = "Description should start with an uppercase letter%n%s".formatted( BEST_PRACTICES_DOCS );
         diagnostics.add( new AspectDocumentDiagnostic( message, NAMING_BEST_PRACTICE_CODE, parsedDocument.getUri(), node.location(),
               Diagnostic.Severity.WARNING ) );
      }
      return diagnostics;
   }

   private static Optional<Diagnostic<?>> acronymCasingDiagnostic( final TurtleSyntaxTree.Node node, final ParsedDocument parsedDocument ) {
      return acronymCasingSuggestion( node.content() ).map( suggestion -> {
         final String message = "Name '%s' should use camel case for acronyms instead of all-uppercase, e.g. '%s'%n%s"
               .formatted( node.content(), suggestion, BEST_PRACTICES_DOCS );
         return new AspectDocumentDiagnostic( message, NAMING_BEST_PRACTICE_CODE, parsedDocument.getUri(), node.location(),
               Diagnostic.Severity.WARNING );
      } );
   }

   private static Optional<Diagnostic<?>> redundantTypeNameDiagnostic( final TurtleSyntaxTree.Node localName,
         final Map<TurtleSyntaxTree.Node, TurtleSyntaxTree.Node> predicateObjectMap, final ParsedDocument parsedDocument ) {
      final String name = localName.content();
      return objectsForPredicates( predicateObjectMap, TYPE_DEFINITION_PREDICATES )
            .map( TurtleSyntaxTree.Node::content )
            .flatMap( typeReference -> sammTypeLocalName( typeReference ).stream() )
            .filter( name::contains )
            .max( Comparator.comparingInt( String::length ) )
            .map( typeName -> {
               final int matchIndex = name.indexOf( typeName );
               final String remainder = name.substring( 0, matchIndex ) + name.substring( matchIndex + typeName.length() );
               final String message = remainder.isEmpty()
                     ? "Name '%s' should not simply repeat the meta model element type '%s', choose a more descriptive name%n%s"
                     .formatted( name, typeName, BEST_PRACTICES_DOCS )
                     : "Name '%s' should not contain the meta model element type '%s', e.g. '%s'%n%s"
                     .formatted( name, typeName, remainder, BEST_PRACTICES_DOCS );
               return new AspectDocumentDiagnostic( message, NAMING_BEST_PRACTICE_CODE, parsedDocument.getUri(), localName.location(),
                     Diagnostic.Severity.WARNING );
            } );
   }

   private static Optional<String> sammTypeLocalName( final String typeReference ) {
      final int separatorIndex = typeReference.indexOf( ':' );
      if ( separatorIndex < 0 ) {
         return Optional.empty();
      }
      final String prefix = typeReference.substring( 0, separatorIndex );
      if ( !SAMM_PREFIXES.contains( prefix ) ) {
         return Optional.empty();
      }
      return Optional.of( typeReference.substring( separatorIndex + 1 ) );
   }

   private static String stripQuotes( final String rawContent ) {
      for ( final String delimiter : List.of( "\"\"\"", "'''", "\"", "'" ) ) {
         if ( rawContent.startsWith( delimiter ) && rawContent.endsWith( delimiter )
               && rawContent.length() >= delimiter.length() * 2 ) {
            return rawContent.substring( delimiter.length(), rawContent.length() - delimiter.length() );
         }
      }
      return rawContent;
   }

   private static Stream<TurtleSyntaxTree.Node> descriptionValues(
         final Map<TurtleSyntaxTree.Node, TurtleSyntaxTree.Node> predicateObjectMap ) {
      return objectsForPredicates( predicateObjectMap, List.of( SAMM_DESCRIPTION_PREDICATE ) )
            .flatMap( objectList -> descriptionValueNode( objectList ).stream() );
   }

   private static Optional<String> acronymCasingSuggestion( final String name ) {
      final Matcher matcher = UPPERCASE_ACRONYM.matcher( name );
      final StringBuilder result = new StringBuilder();
      int lastEnd = 0;
      boolean changed = false;
      while ( matcher.find() ) {
         final String acronym = matcher.group();
         final int acronymEnd = matcher.end();
         final boolean keepLastLetterUppercase = acronymEnd < name.length() && Character.isLowerCase( name.charAt( acronymEnd ) );
         final String replacement = camelCase( acronym, keepLastLetterUppercase );
         changed = changed || !replacement.equals( acronym );
         result.append( name, lastEnd, matcher.start() ).append( replacement );
         lastEnd = acronymEnd;
      }
      if ( !changed ) {
         return Optional.empty();
      }
      return Optional.of( result.append( name.substring( lastEnd ) ).toString() );
   }

   private static String camelCase( final String acronym, final boolean keepLastLetterUppercase ) {
      if ( keepLastLetterUppercase ) {
         return acronym.charAt( 0 )
               + acronym.substring( 1, acronym.length() - 1 ).toLowerCase( Locale.ROOT )
               + acronym.charAt( acronym.length() - 1 );
      }
      return acronym.charAt( 0 ) + acronym.substring( 1 ).toLowerCase( Locale.ROOT );
   }

   private static Stream<TurtleSyntaxTree.Node> objectsForPredicates(
         final Map<TurtleSyntaxTree.Node, TurtleSyntaxTree.Node> predicateObjectMap,
         final Collection<String> predicates ) {
      return predicateObjectMap.entrySet().stream()
            .filter( entry -> predicates.contains( entry.getKey().content() ) )
            .map( Map.Entry::getValue );
   }

   private static Optional<TurtleSyntaxTree.Node> subjectLocalName( final TurtleSyntaxTree.Token triple ) {
      return triple.childWithType( ParserTokenType.SUBJECT )
            .flatMap( subject -> subject.childWithType( ParserTokenType.PREFIXED_NAME ) )
            .flatMap( prefixedName -> prefixedName.childWithType( ParserTokenType.PN_LOCAL ) );
   }

   private static Optional<TurtleSyntaxTree.Node> descriptionValueNode( final TurtleSyntaxTree.Node objectList ) {
      return objectList.childWithType( ParserTokenType.RDF_LITERAL )
            .flatMap( rdfLiteral -> rdfLiteral.childWithType( ParserTokenType.STRING ) );
   }
}
