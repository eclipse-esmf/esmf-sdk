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
import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectDiagnosticCode;
import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectDocumentDiagnostic;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticsProvider;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleService;

import org.apache.jena.rdf.model.Resource;

public class AspectModelNamingConventionValidationService extends TurtleService implements DiagnosticsProvider {
   private static final AspectDiagnosticCode NAMING_CONVENTION_CODE = new AspectDiagnosticCode( "ERR_NAMING_CONVENTION" );
   private static final Pattern UPPERCASE_ACRONYM = Pattern.compile( "\\p{Upper}{2,}" );
   private static final String SAMM_DESCRIPTION_PREDICATE = qualifiedName( SammNs.SAMM, SammNs.SAMM.description() );
   private static final Set<String> CHARACTERISTIC_TYPES = characteristicTypes();
   private static final Set<String> PROPERTY_TYPES = propertyTypes();

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

      subjectLocalName( triple ).ifPresent( localName -> {
         final boolean isCharacteristic = objectsForPredicates( predicateObjectMap, TYPE_DEFINITION_PREDICATES )
               .map( TurtleSyntaxTree.Node::content )
               .anyMatch( CHARACTERISTIC_TYPES::contains );
         if ( isCharacteristic ) {
            uppercaseFirstLetterDiagnostic( "Characteristic name", localName.content(), localName, parsedDocument )
                  .ifPresent( diagnostics::add );
         }
         final boolean isProperty = objectsForPredicates( predicateObjectMap, TYPE_DEFINITION_PREDICATES )
               .map( TurtleSyntaxTree.Node::content )
               .anyMatch( PROPERTY_TYPES::contains );
         if ( isProperty ) {
            lowercaseFirstLetterDiagnostic( "Property name", localName.content(), localName, parsedDocument )
                  .ifPresent( diagnostics::add );
         }
         acronymCasingDiagnostic( localName, parsedDocument ).ifPresent( diagnostics::add );
         redundantTypeNameDiagnostic( localName, predicateObjectMap, parsedDocument ).ifPresent( diagnostics::add );
      } );

      descriptionValues( predicateObjectMap ).forEach( description -> {
         uppercaseFirstLetterDiagnostic( "Description", stripQuotes( description.content() ), description, parsedDocument )
               .ifPresent( diagnostics::add );

         lastLetterPunctuationDiagnostic( "Description", stripQuotes( description.content() ), description, parsedDocument )
               .ifPresent( diagnostics::add );
      } );

      return diagnostics.build();
   }

   private static Optional<Diagnostic<?>> uppercaseFirstLetterDiagnostic( final String label, final String text,
         final TurtleSyntaxTree.Node location, final ParsedDocument parsedDocument ) {
      if ( text.isEmpty() || Character.isUpperCase( text.charAt( 0 ) ) ) {
         return Optional.empty();
      }
      final String suggestion = Character.toUpperCase( text.charAt( 0 ) ) + text.substring( 1 );
      final String message = "%s '%s' should start with an uppercase letter, e.g. '%s'".formatted( label, text, suggestion );
      return Optional.of(
            new AspectDocumentDiagnostic( message, NAMING_CONVENTION_CODE, parsedDocument.getUri(), location.location(),
                  Diagnostic.Severity.WARNING ) );
   }

   private static Optional<Diagnostic<?>> lowercaseFirstLetterDiagnostic( final String label, final String text,
         final TurtleSyntaxTree.Node location, final ParsedDocument parsedDocument ) {
      if ( text.isEmpty() || Character.isLowerCase( text.charAt( 0 ) ) ) {
         return Optional.empty();
      }
      final String suggestion = Character.toLowerCase( text.charAt( 0 ) ) + text.substring( 1 );
      final String message = "%s '%s' should start with a lowercase letter, e.g. '%s'".formatted( label, text, suggestion );
      return Optional.of(
            new AspectDocumentDiagnostic( message, NAMING_CONVENTION_CODE, parsedDocument.getUri(), location.location(),
                  Diagnostic.Severity.WARNING ) );
   }

   private static Optional<Diagnostic<?>> lastLetterPunctuationDiagnostic( final String label, final String text,
         final TurtleSyntaxTree.Node location, final ParsedDocument parsedDocument ) {
      if ( text.isEmpty() || text.charAt( text.length() - 1 ) == '.' ) {
         return Optional.empty();
      }
      final String suggestion = text + ".";
      final String message = "%s '%s' should end with a period, e.g. '%s'".formatted( label, text, suggestion );
      return Optional.of(
            new AspectDocumentDiagnostic( message, NAMING_CONVENTION_CODE, parsedDocument.getUri(), location.location(),
                  Diagnostic.Severity.WARNING ) );
   }

   private static Optional<Diagnostic<?>> acronymCasingDiagnostic( final TurtleSyntaxTree.Node localName,
         final ParsedDocument parsedDocument ) {
      return acronymCasingSuggestion( localName.content() ).map( suggestion -> {
         final String message = "Name '%s' should use camel case for acronyms instead of all-uppercase, e.g. '%s'"
               .formatted( localName.content(), suggestion );
         return new AspectDocumentDiagnostic( message, NAMING_CONVENTION_CODE, parsedDocument.getUri(), localName.location(),
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
                     ? "Name '%s' should not simply repeat the meta model element type '%s', choose a more descriptive name"
                           .formatted( name, typeName )
                     : "Name '%s' should not contain the meta model element type '%s', e.g. '%s'"
                           .formatted( name, typeName, remainder );
               return new AspectDocumentDiagnostic( message, NAMING_CONVENTION_CODE, parsedDocument.getUri(), localName.location(),
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

   private static String qualifiedName( final RdfNamespace namespace, final Resource resource ) {
      return namespace.getShortForm() + ":" + resource.getLocalName();
   }

   private static Set<String> characteristicTypes() {
      return Stream.concat(
            Stream.of( SammNs.SAMM.Characteristic() )
                  .map( resource -> qualifiedName( SammNs.SAMM, resource ) ),
            Stream.of(
                  SammNs.SAMMC.StructuredValue(), SammNs.SAMMC.Quantifiable(), SammNs.SAMMC.Measurement(),
                  SammNs.SAMMC.Duration(), SammNs.SAMMC.State(), SammNs.SAMMC.Enumeration(),
                  SammNs.SAMMC.Collection(), SammNs.SAMMC.Set(), SammNs.SAMMC.SortedSet(),
                  SammNs.SAMMC.List(), SammNs.SAMMC.TimeSeries(), SammNs.SAMMC.SingleEntity(),
                  SammNs.SAMMC.Code(), SammNs.SAMMC.Trait(), SammNs.SAMMC.Either() )
                  .map( resource -> qualifiedName( SammNs.SAMMC, resource ) ) )
            .collect( Collectors.toUnmodifiableSet() );
   }

   private static Set<String> propertyTypes() {
      return Stream.of( SammNs.SAMM.Property(), SammNs.SAMM.AbstractProperty() )
            .map( resource -> qualifiedName( SammNs.SAMM, resource ) )
            .collect( Collectors.toUnmodifiableSet() );
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
