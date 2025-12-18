/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.resolver.parser;

import org.eclipse.esmf.aspectmodel.ValueParsingException;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.irix.IRIx;
import org.apache.jena.riot.system.ErrorHandler;
import org.apache.jena.riot.system.FactoryRDF;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.tokens.Token;
import org.apache.jena.riot.tokens.TokenType;
import org.apache.jena.sparql.core.Quad;

/**
 * Customized parser profile that delegates to Jena's built-in Node generation but also registers the nodes in the {@link TokenRegistry},
 * where information about the line/column/token can be retrieved at a later time.
 */
public class TurtleParserProfile implements ParserProfile {
   private final ParserProfile parserProfile;

   public TurtleParserProfile( final ParserProfile parserProfile ) {
      this.parserProfile = parserProfile;
   }

   @Override
   public String getBaseURI() {
      return parserProfile.getBaseURI();
   }

   @Override
   public Node create( final Node currentGraph, final Token token ) {
      try {
         final Node node = parserProfile.create( currentGraph, token );
         TokenRegistry.put( node, new SmartToken( token ) );
         return node;
      } catch ( final ValueParsingException exception ) {
         exception.setLine( token.getLine() );
         exception.setColumn( token.getColumn() );
         throw exception;
      }
   }

   @Override
   public boolean isStrictMode() {
      return parserProfile.isStrictMode();
   }

   @Override
   public PrefixMap getPrefixMap() {
      return parserProfile.getPrefixMap();
   }

   @Override
   public ErrorHandler getErrorHandler() {
      return parserProfile.getErrorHandler();
   }

   @Override
   public FactoryRDF getFactorRDF() {
      return parserProfile.getFactorRDF();
   }

   @Override
   public String resolveIRI( final String uriStr, final long line, final long col ) {
      return parserProfile.resolveIRI( uriStr, line, col );
   }

   @Override
   public void setBaseIRI( final String baseIri ) {
      parserProfile.setBaseIRI( baseIri );
   }

   @Override
   public Triple createTriple( final Node subject, final Node predicate, final Node object, final long line, final long col ) {
      return parserProfile.createTriple( subject, predicate, object, line, col );
   }

   @Override
   public Quad createQuad( final Node graph, final Node subject, final Node predicate, final Node object, final long line,
         final long col ) {
      return parserProfile.createQuad( graph, subject, predicate, object, line, col );
   }

   @Override
   public Node createURI( final String uriStr, final long line, final long col ) {
      return parserProfile.createURI( uriStr, line, col );
   }

   @Override
   public Node createTypedLiteral( final String lexical, final RDFDatatype datatype, final long line, final long col ) {
      return parserProfile.createTypedLiteral( lexical, datatype, line, col );
   }

   @Override
   public Node createLangLiteral( final String lexical, final String langTag, final long line, final long col ) {
      return parserProfile.createLangLiteral( lexical, langTag, line, col );
   }

   @Override
   public Node createLangDirLiteral( final String lexical, final String langTag, final String direction, final long line, final long col ) {
      return parserProfile.createLangDirLiteral( lexical, langTag, direction, line, col );
   }

   @Override
   public Node createStringLiteral( final String lexical, final long line, final long col ) {
      return parserProfile.createStringLiteral( lexical, line, col );
   }

   @Override
   public Node createBlankNode( final Node scope, final String label, final long line, final long col ) {
      return parserProfile.createBlankNode( scope, label, line, col );
   }

   @Override
   public Node createBlankNode( final Node scope, final long line, final long col ) {
      final Token token = new Token( line, col );
      token.setType( TokenType.LBRACKET );
      final Node node = parserProfile.createBlankNode( scope, line, col );
      TokenRegistry.put( node, new SmartToken( token ) );
      return node;
   }

   @Override
   public Node createTripleTerm( final Node subject, final Node predicate, final Node object, final long line, final long col ) {
      return parserProfile.createTripleTerm( subject, predicate, object, line, col );
   }

   @Override
   public Node createTripleTerm( final Triple triple, final long line, final long col ) {
      return parserProfile.createTripleTerm( triple, line, col );
   }

   @Override
   public Node createGraphNode( final Graph graph, final long line, final long col ) {
      return parserProfile.createGraphNode( graph, line, col );
   }

   @Override
   public Node createNodeFromToken( final Node scope, final Token token, final long line, final long col ) {
      return parserProfile.createNodeFromToken( scope, token, line, col );
   }

   @Override
   public Node createURI( final IRIx iriX, final long line, final long col ) {
      return parserProfile.createURI( iriX, line, col );
   }
}
