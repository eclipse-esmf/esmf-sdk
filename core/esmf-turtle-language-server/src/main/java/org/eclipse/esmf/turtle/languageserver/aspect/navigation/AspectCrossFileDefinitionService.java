/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.turtle.languageserver.aspect.navigation;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategySupport;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewAttributeSelection;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleService;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolves "go to definition" requests that point to elements defined in a separate file.
 *
 * <p>
 * Resolution steps:
 * <ol>
 * <li>Find the prefixed name under the cursor (e.g. {@code ext:MyClass}).</li>
 * <li>Look up the prefix IRI from the current document's prefix declarations.</li>
 * <li>Build a full URN ({@code iriRef + localName}) and parse it as an {@link AspectModelUrn}.</li>
 * <li>Locate the target file via the injected {@link ResolutionStrategy}.</li>
 * <li>Reuse an already-open {@link ParsedDocument} if available; otherwise load from disk.</li>
 * <li>Search the target document for a type-definition triple whose subject has the matching local
 * name.</li>
 * </ol>
 *
 * <p>
 * The resolution strategy is fully injected, making it straightforward to add further
 * strategies in the future (e.g. a GitHub-based strategy) by wrapping them in an
 * {@link org.eclipse.esmf.aspectmodel.resolver.EitherStrategy}.
 */
public class AspectCrossFileDefinitionService extends TurtleService {
   private static final Logger LOG = LoggerFactory.getLogger( AspectCrossFileDefinitionService.class );
   private static final ResolutionStrategySupport RESOLUTION_SUPPORT = new AspectModelLoader();

   private final TreeSitterTurtleParserService parserService;
   private final Map<String, Document> openDocuments;
   private final ResolutionStrategyService resolutionStrategyService;

   public AspectCrossFileDefinitionService(
         final TreeSitterTurtleParserService parserService,
         final Map<String, Document> openDocuments ) {
      this( parserService, openDocuments, new ResolutionStrategyService() );
   }

   public AspectCrossFileDefinitionService(
         final TreeSitterTurtleParserService parserService,
         final Map<String, Document> openDocuments,
         final ResolutionStrategyService resolutionStrategyService ) {
      this.parserService = parserService;
      this.openDocuments = openDocuments;
      this.resolutionStrategyService = resolutionStrategyService;
   }

   public Optional<Location> findDefinition( final ParsedDocument parsedDocument, final Position position ) {
      if ( !documentIsAspectModel( parsedDocument ) ) {
         return Optional.empty();
      }

      final TurtleSyntaxTree tree = parsedDocument.turtleSyntaxTree();
      final TurtleSyntaxTree.Node prefixedName = tree.findMatchingTreeSitterToken(
            List.of( ParserTokenType.PREFIXED_NAME ), position.getLine(), position.getCharacter() );

      if ( prefixedName == null ) {
         return Optional.empty();
      }

      final Optional<String> localName = prefixedName.children().stream()
            .filter( n -> ParserTokenType.PN_LOCAL.equals( n.type() ) && n.isToken() )
            .map( TurtleSyntaxTree.Node::content )
            .findFirst();

      final Optional<String> prefix = prefixedName.children().stream()
            .filter( n -> ParserTokenType.NAMESPACE.equals( n.type() ) )
            .map( TurtleSyntaxTree.Node::content )
            .findFirst();

      if ( localName.isEmpty() || prefix.isEmpty() ) {
         return Optional.empty();
      }

      final String prefixWithColon = prefix.get();
      final String prefixName = prefixWithColon.endsWith( ":" )
            ? prefixWithColon.substring( 0, prefixWithColon.length() - 1 )
            : prefixWithColon;

      final Optional<String> prefixIri = getPrefixIri( prefixName, tree );
      if ( prefixIri.isEmpty() ) {
         return Optional.empty();
      }

      final Optional<AspectModelUrn> urn = buildUrn( prefixIri.get(), localName.get() );
      if ( urn.isEmpty() ) {
         return Optional.empty();
      }

      final Optional<Path> targetFile = resolveFilePath( parsedDocument, urn.get() );
      if ( targetFile.isEmpty() ) {
         return Optional.empty();
      }

      final Optional<ParsedDocument> targetDocument = getOrLoadDocument( targetFile.get() );
      return targetDocument.flatMap( doc -> findByLocalName( localName.get(), doc ) );
   }

   /** Resolves a full semantic URN to the current location of its named subject. */
   public UrnResolution findDefinition( final ParsedDocument sourceDocument, final AspectModelUrn urn ) {
      if ( !documentIsAspectModel( sourceDocument ) ) return UrnResolution.temporarilyUnresolvable();
      final UrnResolution local = findByUrn( urn, sourceDocument );
      if ( local.outcome() != UrnResolution.Outcome.NOT_FOUND ) return local;
      try {
         final URI target = resolutionStrategyService.buildResolutionStrategyForDocument( sourceDocument ).apply( urn, RESOLUTION_SUPPORT ).sourceUri();
         if ( !"file".equalsIgnoreCase( target.getScheme() ) ) return UrnResolution.unsupportedUri();
         return getOrLoadDocument( Paths.get( target ) ).map( document -> findByUrn( urn, document ) ).orElseGet( UrnResolution::temporarilyUnresolvable );
      } catch ( final ModelResolutionException e ) { return UrnResolution.notFound();
      } catch ( final IllegalArgumentException e ) { return UrnResolution.unsupportedUri(); }
   }

   /** Resolves one current predicate statement on a named owner without matching displayed values. */
   public UrnResolution findAttributeStatement( final ParsedDocument sourceDocument, final AspectModelUrn ownerUrn,
         final String predicateUrn, final GraphicalViewAttributeSelection selection, final String language ) {
      if ( !documentIsAspectModel( sourceDocument ) || hasSyntaxErrors( sourceDocument ) ) {
         return UrnResolution.temporarilyUnresolvable();
      }
      final UrnResolution local = findAttributeInDocument( sourceDocument, ownerUrn, predicateUrn, selection, language );
      if ( local.outcome() != UrnResolution.Outcome.NOT_FOUND ) {
         return local;
      }
      try {
         final URI target = resolutionStrategyService.buildResolutionStrategyForDocument( sourceDocument )
               .apply( ownerUrn, RESOLUTION_SUPPORT ).sourceUri();
         if ( !"file".equalsIgnoreCase( target.getScheme() ) ) {
            return UrnResolution.unsupportedUri();
         }
         return getOrLoadDocument( Paths.get( target ) )
               .map( document -> hasSyntaxErrors( document )
                     ? UrnResolution.temporarilyUnresolvable()
                     : findAttributeInDocument( document, ownerUrn, predicateUrn, selection, language ) )
               .orElseGet( UrnResolution::temporarilyUnresolvable );
      } catch ( final ModelResolutionException e ) {
         return UrnResolution.notFound();
      } catch ( final IllegalArgumentException e ) {
         return UrnResolution.unsupportedUri();
      }
   }

   private UrnResolution findAttributeInDocument( final ParsedDocument document, final AspectModelUrn ownerUrn,
         final String predicateUrn, final GraphicalViewAttributeSelection selection, final String language ) {
      final TurtleSyntaxTree tree = document.turtleSyntaxTree();
      final List<TurtleSyntaxTree.Node> matchingProperties = tree.tokens()
            .filter( token -> ParserTokenType.TRIPLE.equals( token.type() ) )
            .filter( token -> tripleSubjectUrn( token, tree ).map( ownerUrn.toString()::equals ).orElse( false ) )
            .flatMap( token -> token.childWithType( ParserTokenType.PROPERTY_LIST ).stream() )
            .flatMap( propertyList -> propertyList.children().stream() )
            .filter( property -> ParserTokenType.PROPERTY.equals( property.type() ) )
            .filter( property -> property.childWithType( ParserTokenType.PREDICATE )
                  .flatMap( predicate -> expandedIri( predicate, tree ) )
                  .map( predicateUrn::equals ).orElse( false ) )
            .filter( property -> language == null || hasLanguage( property, language ) )
            .toList();
      if ( matchingProperties.isEmpty() ) {
         return UrnResolution.notFound();
      }
      if ( matchingProperties.size() > 1 ) {
         return UrnResolution.ambiguous();
      }
      final TurtleSyntaxTree.Node property = matchingProperties.getFirst();
      final Optional<TurtleSyntaxTree.Node> predicate = property.childWithType( ParserTokenType.PREDICATE );
      if ( predicate.isEmpty() || selection == null ) {
         return UrnResolution.temporarilyUnresolvable();
      }
      return UrnResolution.found( super.getLocationForLsp( document, predicate.get() ) );
   }

   private boolean hasSyntaxErrors( final ParsedDocument document ) {
      return document.turtleSyntaxTree().nodes().anyMatch( TurtleSyntaxTree.Node::isError );
   }

   private Optional<String> tripleSubjectUrn( final TurtleSyntaxTree.Token triple, final TurtleSyntaxTree tree ) {
      return triple.childWithType( ParserTokenType.SUBJECT )
            .flatMap( subject -> descendantWithType( subject, ParserTokenType.PREFIXED_NAME ) )
            .flatMap( prefixedName -> expandedPrefixedName( prefixedName, tree ) );
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

   private Optional<TurtleSyntaxTree.Node> descendantWithType( final TurtleSyntaxTree.Node node, final String type ) {
      if ( type.equals( node.type() ) ) {
         return Optional.of( node );
      }
      return node.children().stream().map( child -> descendantWithType( child, type ) )
            .flatMap( Optional::stream ).findFirst();
   }

   private boolean hasLanguage( final TurtleSyntaxTree.Node property, final String language ) {
      return descendantsWithType( property, ParserTokenType.LANG_TAG )
            .map( TurtleSyntaxTree.Node::content )
            .map( value -> value.startsWith( "@" ) ? value.substring( 1 ) : value )
            .map( value -> value.toLowerCase( java.util.Locale.ROOT ) )
            .anyMatch( language::equals );
   }

   private Stream<TurtleSyntaxTree.Node> descendantsWithType( final TurtleSyntaxTree.Node node, final String type ) {
      final Stream<TurtleSyntaxTree.Node> current = type.equals( node.type() ) ? Stream.of( node ) : Stream.empty();
      return Stream.concat( current, node.children().stream().flatMap( child -> descendantsWithType( child, type ) ) );
   }

   Optional<String> getPrefixIri( final String prefixName, final TurtleSyntaxTree tree ) {
      return tree.nodes()
            .filter( n -> ParserTokenType.DIRECTIVE.equals( n.type() ) )
            .flatMap( n -> n.children().stream() )
            .filter( n -> ParserTokenType.PREFIX_ID.equals( n.type() ) )
            .filter( prefixIdNode -> matchesPrefixName( prefixIdNode, prefixName ) )
            .findFirst()
            .flatMap( prefixIdNode -> prefixIdNode.childWithType( ParserTokenType.IRI_REFERENCE ) )
            .map( iriNode -> {
               final String iri = iriNode.content();
               // IRI_REFERENCE content includes angle brackets: <urn:samm:...#>
               if ( iri.startsWith( "<" ) && iri.endsWith( ">" ) ) {
                  return iri.substring( 1, iri.length() - 1 );
               }
               return iri;
            } );
   }

   private boolean matchesPrefixName( final TurtleSyntaxTree.Node prefixIdNode, final String prefixName ) {
      final Optional<TurtleSyntaxTree.Node> namespaceNode = prefixIdNode.childWithType( ParserTokenType.NAMESPACE );
      if ( namespaceNode.isEmpty() ) {
         // Default prefix (:) has no pn_prefix child; empty prefixName matches it
         return prefixName.isEmpty();
      }
      final Optional<String> pnPrefix = namespaceNode.get().children().stream()
            .filter( n -> ParserTokenType.PN_PREFIX.equals( n.type() ) && n.isToken() )
            .map( TurtleSyntaxTree.Node::content )
            .findFirst();
      // If there is no PN_PREFIX token, it's the default prefix (@prefix : <iri>)
      return pnPrefix.map( p -> p.equals( prefixName ) ).orElse( prefixName.isEmpty() );
   }

   Optional<AspectModelUrn> buildUrn( final String prefixIri, final String localName ) {
      final String fullUrn = prefixIri + localName;
      return AspectModelUrn.from( fullUrn ).toJavaOptional();
   }

   Optional<Path> resolveFilePath( final ParsedDocument parsedDocument, final AspectModelUrn urn ) {
      try {
         final URI sourceUri =
               resolutionStrategyService.buildResolutionStrategyForDocument( parsedDocument ).apply( urn, RESOLUTION_SUPPORT ).sourceUri();
         return Optional.of( Paths.get( sourceUri ) );
      } catch ( final ModelResolutionException | IllegalArgumentException e ) {
         LOG.debug( "[cross-file] could not resolve {} to a local file: {}", urn, e.getMessage() );
         return Optional.empty();
      }
   }

   Optional<ParsedDocument> getOrLoadDocument( final Path filePath ) {
      final URI fileUri = filePath.toUri();
      final Document openDocument = openDocuments.get( fileUri.toString() );
      if ( openDocument != null ) {
         return Optional.of( parserService.apply( openDocument ) );
      }
      try {
         final String content = Files.readString( filePath );
         final Document document = new Document( fileUri, content );
         return Optional.of( parserService.apply( document ) );
      } catch ( final IOException e ) {
         LOG.warn( "[cross-file] could not read file {}: {}", filePath, e.getMessage() );
         return Optional.empty();
      }
   }

   Optional<Location> findByLocalName( final String localName, final ParsedDocument targetDocument ) {
      final TurtleSyntaxTree tree = targetDocument.turtleSyntaxTree();

      final Optional<TurtleSyntaxTree.Node> definitionNode = findLocalNameInStream(
            localName, typeDefinitionSubjectPrefixedNames( tree ) )
                  .or( () -> findLocalNameInStream( localName, allSubjectPrefixedNames( tree ) ) )
                  .flatMap( n -> n.children().stream()
                        .filter( c -> c.isToken() && ParserTokenType.PN_LOCAL.equals( c.type() ) )
                        .findFirst() );

      return definitionNode.map( n -> super.getLocationForLsp( targetDocument, n ) );
   }

   private UrnResolution findByUrn( final AspectModelUrn urn, final ParsedDocument document ) {
      final TurtleSyntaxTree tree = document.turtleSyntaxTree();
      final List<TurtleSyntaxTree.Node> matches = allSubjectPrefixedNames( tree ).filter( n -> subjectUrn( n, tree ).map( urn.toString()::equals ).orElse( false ) ).toList();
      if ( matches.isEmpty() ) return UrnResolution.notFound();
      if ( matches.size() > 1 ) return UrnResolution.ambiguous();
      return matches.getFirst().children().stream().filter( n -> n.isToken() && ParserTokenType.PN_LOCAL.equals( n.type() ) ).findFirst()
            .map( n -> UrnResolution.found( super.getLocationForLsp( document, n ) ) ).orElseGet( UrnResolution::temporarilyUnresolvable );
   }

   private Optional<String> subjectUrn( final TurtleSyntaxTree.Node subject, final TurtleSyntaxTree tree ) {
      final Optional<String> local = subject.children().stream().filter( n -> n.isToken() && ParserTokenType.PN_LOCAL.equals( n.type() ) ).map( TurtleSyntaxTree.Node::content ).findFirst();
      final String prefix = subject.children().stream().filter( n -> ParserTokenType.NAMESPACE.equals( n.type() ) ).map( TurtleSyntaxTree.Node::content ).findFirst().orElse( "" );
      final String prefixName = prefix.endsWith( ":" ) ? prefix.substring( 0, prefix.length() - 1 ) : prefix;
      return local.flatMap( name -> getPrefixIri( prefixName, tree ).map( iri -> iri + name ) );
   }

   public record UrnResolution( Outcome outcome, Location location ) {
      public enum Outcome { FOUND, NOT_FOUND, AMBIGUOUS, UNSUPPORTED_URI, TEMPORARILY_UNRESOLVABLE }
      static UrnResolution found( final Location value ) { return new UrnResolution( Outcome.FOUND, value ); }
      static UrnResolution notFound() { return new UrnResolution( Outcome.NOT_FOUND, null ); }
      static UrnResolution ambiguous() { return new UrnResolution( Outcome.AMBIGUOUS, null ); }
      static UrnResolution unsupportedUri() { return new UrnResolution( Outcome.UNSUPPORTED_URI, null ); }
      static UrnResolution temporarilyUnresolvable() { return new UrnResolution( Outcome.TEMPORARILY_UNRESOLVABLE, null ); }
   }

   private Optional<TurtleSyntaxTree.Node> findLocalNameInStream(
         final String localName, final Stream<TurtleSyntaxTree.Node> stream ) {
      return stream
            .filter( n -> n.children().stream()
                  .filter( c -> c.isToken() && ParserTokenType.PN_LOCAL.equals( c.type() ) )
                  .anyMatch( c -> localName.equals( c.content() ) ) )
            .findFirst();
   }
}
