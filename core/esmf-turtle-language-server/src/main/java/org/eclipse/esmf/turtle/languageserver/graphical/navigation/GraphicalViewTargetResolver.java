/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.turtle.languageserver.graphical.navigation;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategySupport;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.graphical.source.GraphicalViewSourceContext;
import org.eclipse.esmf.turtle.languageserver.graphical.source.GraphicalViewSourceSnapshot;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;
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
   public Resolution resolveHeader( final GraphicalViewSourceSnapshot source, final AspectModelUrn targetUrn ) {
      final ParsedDocument parsedSource = parse( source );
      if ( !documentIsAspectModel( parsedSource ) || hasSyntaxErrors( parsedSource ) ) {
         return Resolution.temporarilyUnresolvable();
      }
      final Resolution local = findHeaderInDocument( parsedSource, targetUrn );
      return local.outcome() == Outcome.NOT_FOUND ? resolveExternalHeader( parsedSource, targetUrn ) : local;
   }

   /** Attribute lookup supports prefixed-name and full-IRI named owners. */
   public Resolution resolveAttribute( final GraphicalViewSourceSnapshot source, final AspectModelUrn ownerUrn,
         final String predicateUrn, final String language ) {
      final ParsedDocument parsedSource = parse( source );
      if ( !documentIsAspectModel( parsedSource ) || hasSyntaxErrors( parsedSource ) ) {
         return Resolution.temporarilyUnresolvable();
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
               .map( target -> hasSyntaxErrors( target ) ? Resolution.temporarilyUnresolvable()
                     : findHeaderInDocument( target, targetUrn ) )
               .orElseGet( Resolution::temporarilyUnresolvable );
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
               .map( target -> hasSyntaxErrors( target ) ? Resolution.temporarilyUnresolvable()
                     : findAttributeInDocument( target, ownerUrn, predicateUrn, language ) )
               .orElseGet( Resolution::temporarilyUnresolvable );
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
            .orElseGet( Resolution::temporarilyUnresolvable );
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
            .orElseGet( Resolution::temporarilyUnresolvable );
   }

   private ParsedDocument parse( final GraphicalViewSourceSnapshot snapshot ) {
      return parser.apply( snapshot.document() );
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

   private Optional<String> expandedIri( final TurtleSyntaxTree.Node node, final TurtleSyntaxTree tree ) {
      final Optional<TurtleSyntaxTree.Node> iriReference = descendantWithType( node, ParserTokenType.IRI_REFERENCE );
      if ( iriReference.isPresent() ) {
         final String value = iriReference.get().content();
         return Optional.of( value.startsWith( "<" ) && value.endsWith( ">" ) ? value.substring( 1, value.length() - 1 ) : value );
      }
      return descendantWithType( node, ParserTokenType.PREFIXED_NAME )
            .flatMap( prefixedName -> expandedPrefixedName( prefixedName, tree ) );
   }

   private Optional<String> expandedPrefixedName( final TurtleSyntaxTree.Node prefixedName, final TurtleSyntaxTree tree ) {
      final Optional<String> local = prefixedName.children().stream()
            .filter( child -> child.isToken() && ParserTokenType.PN_LOCAL.equals( child.type() ) )
            .map( TurtleSyntaxTree.Node::content ).findFirst();
      final String prefix = prefixedName.children().stream()
            .filter( child -> ParserTokenType.NAMESPACE.equals( child.type() ) )
            .map( TurtleSyntaxTree.Node::content ).findFirst().orElse( "" );
      final String prefixName = prefix.endsWith( ":" ) ? prefix.substring( 0, prefix.length() - 1 ) : prefix;
      return local.flatMap( name -> getPrefixIri( prefixName, tree ).map( iri -> iri + name ) );
   }

   private Optional<String> getPrefixIri( final String prefixName, final TurtleSyntaxTree tree ) {
      return tree.nodes().filter( node -> ParserTokenType.DIRECTIVE.equals( node.type() ) )
            .flatMap( node -> node.children().stream() )
            .filter( node -> ParserTokenType.PREFIX_ID.equals( node.type() ) )
            .filter( prefixId -> matchesPrefixName( prefixId, prefixName ) ).findFirst()
            .flatMap( prefixId -> prefixId.childWithType( ParserTokenType.IRI_REFERENCE ) )
            .map( iriNode -> {
               final String iri = iriNode.content();
               return iri.startsWith( "<" ) && iri.endsWith( ">" ) ? iri.substring( 1, iri.length() - 1 ) : iri;
            } );
   }

   private boolean matchesPrefixName( final TurtleSyntaxTree.Node prefixIdNode, final String prefixName ) {
      final Optional<TurtleSyntaxTree.Node> namespaceNode = prefixIdNode.childWithType( ParserTokenType.NAMESPACE );
      if ( namespaceNode.isEmpty() ) {
         return prefixName.isEmpty();
      }
      return namespaceNode.get().children().stream()
            .filter( node -> node.isToken() && ParserTokenType.PN_PREFIX.equals( node.type() ) )
            .map( TurtleSyntaxTree.Node::content ).findFirst()
            .map( prefixName::equals ).orElse( prefixName.isEmpty() );
   }

   private Optional<TurtleSyntaxTree.Node> descendantWithType( final TurtleSyntaxTree.Node node, final String type ) {
      if ( type.equals( node.type() ) ) {
         return Optional.of( node );
      }
      return node.children().stream().map( child -> descendantWithType( child, type ) )
            .flatMap( Optional::stream ).findFirst();
   }

   private boolean hasLanguage( final TurtleSyntaxTree.Node property, final String language ) {
      return descendantsWithType( property, ParserTokenType.LANG_TAG ).map( TurtleSyntaxTree.Node::content )
            .map( value -> value.startsWith( "@" ) ? value.substring( 1 ) : value )
            .map( value -> value.toLowerCase( java.util.Locale.ROOT ) ).anyMatch( language::equals );
   }

   private Stream<TurtleSyntaxTree.Node> descendantsWithType( final TurtleSyntaxTree.Node node, final String type ) {
      final Stream<TurtleSyntaxTree.Node> current = type.equals( node.type() ) ? Stream.of( node ) : Stream.empty();
      return Stream.concat( current, node.children().stream().flatMap( child -> descendantsWithType( child, type ) ) );
   }

   public enum Outcome {
      FOUND, NOT_FOUND, AMBIGUOUS, UNSUPPORTED_URI, TEMPORARILY_UNRESOLVABLE
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

      static Resolution temporarilyUnresolvable() {
         return new Resolution( Outcome.TEMPORARILY_UNRESOLVABLE, null );
      }
   }
}
