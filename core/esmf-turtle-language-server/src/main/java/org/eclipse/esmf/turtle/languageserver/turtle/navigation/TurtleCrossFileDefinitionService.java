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

package org.eclipse.esmf.turtle.languageserver.turtle.navigation;

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
public class TurtleCrossFileDefinitionService extends TurtleService {
   private static final Logger LOG = LoggerFactory.getLogger( TurtleCrossFileDefinitionService.class );
   private static final ResolutionStrategySupport RESOLUTION_SUPPORT = new AspectModelLoader();

   private final TreeSitterTurtleParserService parserService;
   private final Map<String, Document> openDocuments;

   public TurtleCrossFileDefinitionService(
         final TreeSitterTurtleParserService parserService,
         final Map<String, Document> openDocuments ) {
      this.parserService = parserService;
      this.openDocuments = openDocuments;
   }

   public Optional<Location> findDefinition( final ParsedDocument parsedDocument, final Position position ) {
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
         final URI sourceUri = buildResolutionStrategyForDocument( parsedDocument ).apply( urn, RESOLUTION_SUPPORT )
               .sourceLocation()
               .orElseThrow( () -> new ModelResolutionException( "No source location in resolved file for " + urn ) );
         return Optional.of( Paths.get( sourceUri ) );
      } catch ( final ModelResolutionException e ) {
         return Optional.empty();
      }
   }

   Optional<ParsedDocument> getOrLoadDocument( final Path filePath ) {
      final String fileUri = filePath.toUri().toString();
      final Document openDocument = openDocuments.get( fileUri );
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

   private Optional<TurtleSyntaxTree.Node> findLocalNameInStream(
         final String localName, final Stream<TurtleSyntaxTree.Node> stream ) {
      return stream
            .filter( n -> n.children().stream()
                  .filter( c -> c.isToken() && ParserTokenType.PN_LOCAL.equals( c.type() ) )
                  .anyMatch( c -> localName.equals( c.content() ) ) )
            .findFirst();
   }
}
