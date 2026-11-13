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
package org.eclipse.esmf.turtle.languageserver.graphical.navigation;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategySupport;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.graphical.source.GraphicalViewSourceContext;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleService;

import org.eclipse.lsp4j.Location;

/** Resolves Graphical View semantic locators against immutable current source snapshots. */
public final class GraphicalViewTargetResolver extends TurtleService {
   private static final ResolutionStrategySupport RESOLUTION_SUPPORT = new AspectModelLoader();

   private final TreeSitterTurtleParserService parser;
   private final ResolutionStrategyService strategies;
   private final GraphicalViewSourceContext sources;

   public GraphicalViewTargetResolver( final TreeSitterTurtleParserService parser,
         final ResolutionStrategyService strategies, final GraphicalViewSourceContext sources ) {
      this.parser = parser;
      this.strategies = strategies;
      this.sources = sources;
   }

   /** Header resolution deliberately accepts prefixed-name subjects only. */
   public Resolution resolveHeader( final Document source, final AspectModelUrn targetUrn ) {
      final ParsedDocument parsedSource = parse( source );
      if ( !documentIsAspectModel( parsedSource ) || hasSyntaxErrors( parsedSource ) ) {
         return Resolution.unresolvable();
      }
      final Resolution local = findHeaderInDocument( parsedSource, targetUrn );
      return local.outcome() == Outcome.NOT_FOUND ? resolveExternalHeader( parsedSource, targetUrn ) : local;
   }

   /** Attribute lookup supports prefixed-name and full-IRI named owners. */
   public Resolution resolveAttribute( final Document source, final AspectModelUrn ownerUrn,
         final String predicateUrn, final String language ) {
      final ParsedDocument parsedSource = parse( source );
      if ( !documentIsAspectModel( parsedSource ) || hasSyntaxErrors( parsedSource ) ) {
         return Resolution.unresolvable();
      }
      final Resolution local = findAttributeInDocument( parsedSource, ownerUrn, predicateUrn, language );
      return local.outcome() == Outcome.NOT_FOUND
            ? resolveExternalAttribute( parsedSource, ownerUrn, predicateUrn, language )
            : local;
   }

   private Resolution resolveExternalHeader( final ParsedDocument source, final AspectModelUrn targetUrn ) {
      try {
         final URI targetUri = resolveUri( source, targetUrn );
         if ( !GraphicalViewSourceContext.isLocalFileUri( targetUri.toString() ) ) {
            return Resolution.unsupportedUri();
         }
         return sources.resolvedSnapshot( targetUri ).map( this::parse )
               .map( target -> hasSyntaxErrors( target ) ? Resolution.unresolvable()
                     : findHeaderInDocument( target, targetUrn ) )
               .orElseGet( Resolution::unresolvable );
      } catch ( final ModelResolutionException exception ) {
         return Resolution.notFound();
      } catch ( final IllegalArgumentException exception ) {
         return Resolution.unsupportedUri();
      }
   }

   private Resolution resolveExternalAttribute( final ParsedDocument source, final AspectModelUrn ownerUrn,
         final String predicateUrn, final String language ) {
      try {
         final URI targetUri = resolveUri( source, ownerUrn );
         if ( !GraphicalViewSourceContext.isLocalFileUri( targetUri.toString() ) ) {
            return Resolution.unsupportedUri();
         }
         return sources.resolvedSnapshot( targetUri ).map( this::parse )
               .map( target -> hasSyntaxErrors( target ) ? Resolution.unresolvable()
                     : findAttributeInDocument( target, ownerUrn, predicateUrn, language ) )
               .orElseGet( Resolution::unresolvable );
      } catch ( final ModelResolutionException exception ) {
         return Resolution.notFound();
      } catch ( final IllegalArgumentException exception ) {
         return Resolution.unsupportedUri();
      }
   }

   private URI resolveUri( final ParsedDocument source, final AspectModelUrn urn ) throws ModelResolutionException {
      return strategies.buildResolutionStrategyForDocument( source ).apply( urn, RESOLUTION_SUPPORT ).sourceUri();
   }

   private Resolution findHeaderInDocument( final ParsedDocument document, final AspectModelUrn urn ) {
      final TurtleSyntaxTree tree = document.turtleSyntaxTree();
      final List<TurtleSyntaxTree.Node> matches = allSubjectPrefixedNames( tree )
            .filter( subject -> subjectUrn( subject, tree ).map( urn.toString()::equals ).orElse( false ) ).toList();
      if ( matches.isEmpty() ) {
         return Resolution.notFound();
      }
      if ( matches.size() > 1 ) {
         return Resolution.ambiguous();
      }
      return matches.getFirst().children().stream()
            .filter( node -> node.isToken() && ParserTokenType.PN_LOCAL.equals( node.type() ) ).findFirst()
            .map( node -> Resolution.found( getLocationForLsp( document, node ) ) )
            .orElseGet( Resolution::unresolvable );
   }

   private Resolution findAttributeInDocument( final ParsedDocument document, final AspectModelUrn ownerUrn,
         final String predicateUrn, final String language ) {
      final TurtleSyntaxTree tree = document.turtleSyntaxTree();
      final List<TurtleSyntaxTree.Node> matchingProperties = tree.tokens()
            .filter( token -> ParserTokenType.TRIPLE.equals( token.type() ) )
            .filter( token -> tripleSubjectUrn( token, tree ).map( ownerUrn.toString()::equals ).orElse( false ) )
            .flatMap( token -> token.childWithType( ParserTokenType.PROPERTY_LIST ).stream() )
            .flatMap( propertyList -> propertyList.children().stream() )
            .filter( property -> ParserTokenType.PROPERTY.equals( property.type() ) )
            .filter( property -> property.childWithType( ParserTokenType.PREDICATE )
                  .flatMap( predicate -> expandedIri( predicate, tree ) ).map( predicateUrn::equals ).orElse( false ) )
            .filter( property -> language == null || hasLanguage( property, language ) )
            .toList();
      if ( matchingProperties.isEmpty() ) {
         return Resolution.notFound();
      }
      if ( matchingProperties.size() > 1 ) {
         return Resolution.ambiguous();
      }
      return matchingProperties.getFirst().childWithType( ParserTokenType.PREDICATE )
            .map( predicate -> Resolution.found( getLocationForLsp( document, predicate ) ) )
            .orElseGet( Resolution::unresolvable );
   }

   private ParsedDocument parse( final Document snapshot ) {
      return parser.apply( snapshot );
   }

   private boolean hasSyntaxErrors( final ParsedDocument document ) {
      return document.turtleSyntaxTree().nodes().anyMatch( TurtleSyntaxTree.Node::isError );
   }

   private Optional<String> tripleSubjectUrn( final TurtleSyntaxTree.Token triple, final TurtleSyntaxTree tree ) {
      return triple.childWithType( ParserTokenType.SUBJECT ).flatMap( subject -> expandedIri( subject, tree ) );
   }

   private Optional<String> subjectUrn( final TurtleSyntaxTree.Node subject, final TurtleSyntaxTree tree ) {
      return expandedPrefixedName( subject, tree );
   }

   private boolean hasLanguage( final TurtleSyntaxTree.Node property, final String language ) {
      return descendantsWithType( property, ParserTokenType.LANG_TAG ).map( TurtleSyntaxTree.Node::content )
            .map( value -> value.startsWith( "@" ) ? value.substring( 1 ) : value )
            .map( value -> value.toLowerCase( Locale.ROOT ) ).anyMatch( language::equals );
   }

   private Stream<TurtleSyntaxTree.Node> descendantsWithType( final TurtleSyntaxTree.Node node, final String type ) {
      final Stream<TurtleSyntaxTree.Node> current = type.equals( node.type() ) ? Stream.of( node ) : Stream.empty();
      return Stream.concat( current, node.children().stream().flatMap( child -> descendantsWithType( child, type ) ) );
   }

   public enum Outcome {
      FOUND, NOT_FOUND, AMBIGUOUS, UNSUPPORTED_URI, UNRESOLVABLE
   }

   public record Resolution(
         Outcome outcome, Location location
   ) {
      static Resolution found( final Location location ) {
         return new Resolution( Outcome.FOUND, location );
      }

      static Resolution notFound() {
         return new Resolution( Outcome.NOT_FOUND, null );
      }

      static Resolution ambiguous() {
         return new Resolution( Outcome.AMBIGUOUS, null );
      }

      static Resolution unsupportedUri() {
         return new Resolution( Outcome.UNSUPPORTED_URI, null );
      }

      static Resolution unresolvable() {
         return new Resolution( Outcome.UNRESOLVABLE, null );
      }
   }
}
