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

package org.eclipse.esmf.turtle.languageserver.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleService;

import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolKind;

public class DocumentSymbolService extends TurtleService {
   private final TreeSitterTurtleParserService parserService;

   public DocumentSymbolService( final TreeSitterTurtleParserService parserService ) {
      this.parserService = parserService;
   }

   public List<DocumentSymbol> symbols( final Document document ) {
      final ParsedDocument parsedDocument = parserService.apply( document );
      final TurtleSyntaxTree turtleSyntaxTree = parsedDocument.turtleSyntaxTree();
      final List<DocumentSymbol> symbols = new ArrayList<>();

      for ( final Iterator<TurtleSyntaxTree.Node> it = turtleSyntaxTree.nodes().iterator(); it.hasNext(); ) {
         try {
            final TurtleSyntaxTree.Node node = it.next();
            if ( !node.isToken() ) {
               continue;
            }
            final TurtleSyntaxTree.Token token = node.asToken();
            if ( ParserTokenType.DIRECTIVE.equals( node.type() ) ) {
               symbolForPrefixDeclaration( token ).ifPresent( symbols::add );
            }
            if ( ParserTokenType.TRIPLE.equals( node.type() ) ) {
               symbolForTriple( token ).ifPresent( symbols::add );
            }
         } catch ( final Exception exception ) {
            continue;
         }
      }

      return symbols;
   }

   private Optional<DocumentSymbol> symbolForPrefixDeclaration( final TurtleSyntaxTree.Token directive ) {
      final TurtleSyntaxTree.Node prefixId = directive.childWithType( ParserTokenType.PREFIX_ID ).orElseThrow();
      final TurtleSyntaxTree.Node namespaceToken = prefixId.childWithType( ParserTokenType.NAMESPACE ).orElseThrow();
      if ( namespaceToken.children().size() != 1 || namespaceToken.childWithType( ParserTokenType.SYMBOL_COLON ).isEmpty() ) {
         return Optional.empty();
      }
      final TurtleSyntaxTree.Token uri = prefixId.childWithType( ParserTokenType.IRI_REFERENCE )
            .map( TurtleSyntaxTree.Node::asToken ).orElseThrow();
      final DocumentSymbol symbol = new DocumentSymbol();
      final String namespace = uri.content()
            .replace( "<", "" )
            .replace( ">", "" )
            .replaceAll( "#+$", "" );
      symbol.setName( namespace );
      symbol.setKind( SymbolKind.Namespace );
      final Range range = new Range();
      range.setStart( new Position( prefixId.location().fromLine(), prefixId.location().fromColumn() ) );
      range.setEnd( new Position( namespaceToken.location().toLine(), namespaceToken.location().toColumn() ) );
      symbol.setRange( range );
      symbol.setSelectionRange( range );
      final String detail = AspectModelUrn.from( namespace ).isSuccess()
            ? "samm:Namespace"
            : "Namespace";
      symbol.setDetail( detail );
      return Optional.of( symbol );
   }

   private Optional<DocumentSymbol> symbolForTriple( final TurtleSyntaxTree.Token triple ) {
      final TurtleSyntaxTree.Token subject = triple.childWithType( ParserTokenType.SUBJECT )
            .map( TurtleSyntaxTree.Node::asToken )
            .orElseThrow();

      return predicateObjectMapForTriple( triple ).entrySet().stream()
            .filter( entry -> TYPE_DEFINITION_PREDICATES.contains( entry.getKey().content() ) )
            .map( entry -> {
               final DocumentSymbol symbol = new DocumentSymbol();
               symbol.setName( subject.content() );
               symbol.setKind( SymbolKind.Method );
               final Range range = new Range();
               range.setStart( new Position( subject.location().fromLine(), subject.location().fromColumn() ) );
               range.setEnd( new Position( triple.location().toLine(), triple.location().toColumn() ) );
               symbol.setRange( range );
               symbol.setSelectionRange( range );
               symbol.setDetail( entry.getValue().content() );
               return symbol;
            } )
            .findFirst();
   }
}
