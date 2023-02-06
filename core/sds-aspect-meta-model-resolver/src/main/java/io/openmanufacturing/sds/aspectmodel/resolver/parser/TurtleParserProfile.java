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

package io.openmanufacturing.sds.aspectmodel.resolver.parser;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.BlankNode;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.LiteralNode;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Blank;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.graph.TokenNode;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.UriNode;
import org.apache.jena.query.ARQ;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.system.ErrorHandler;
import org.apache.jena.riot.system.FactoryRDF;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.riot.tokens.Token;
import org.apache.jena.riot.tokens.TokenType;
import org.apache.jena.sparql.core.Quad;

/**
 * Customized parser profile that overwrites Jena's built-in Node generation to instead return {@link TokenNode}s that retain a link to their
 * originating token.
 */
public class TurtleParserProfile implements ParserProfile {

   private final ParserProfile parserProfile;

   public TurtleParserProfile( final ParserProfile parserProfile ) {
      this.parserProfile = parserProfile;
   }

   @Override
   public Node create( final Node currentGraph, final Token token ) {
      // Dispatches to the underlying ParserFactory operation
      final long line = token.getLine();
      final long col = token.getColumn();
      final String str = token.getImage();
      final SmartToken smartToken = new SmartToken( token );
      switch ( token.getType() ) {
      case BNODE:
         return new BlankNode( (Node_Blank) createBlankNode( currentGraph, str, line, col ), smartToken );
      case IRI:
         return new UriNode( (Node_URI) createURI( str, line, col ), smartToken );
      case PREFIXED_NAME: {
         final String suffix = token.getImage2();
         final String expansion = expandPrefixedName( str, suffix, token );
         return new UriNode( (Node_URI) createURI( expansion, line, col ), smartToken );
      }
      case DECIMAL:
         return new LiteralNode( (Node_Literal) createTypedLiteral( str, XSDDatatype.XSDdecimal, line, col ), smartToken );
      case DOUBLE:
         return new LiteralNode( (Node_Literal) createTypedLiteral( str, XSDDatatype.XSDdouble, line, col ), smartToken );
      case INTEGER:
         return new LiteralNode( (Node_Literal) createTypedLiteral( str, XSDDatatype.XSDinteger, line, col ), smartToken );
      case LITERAL_DT: {
         final Token tokenDT = token.getSubToken2();
         String uriStr;
         switch ( tokenDT.getType() ) {
         case IRI -> uriStr = tokenDT.getImage();
         case PREFIXED_NAME -> {
            final String prefix = tokenDT.getImage();
            final String suffix = tokenDT.getImage2();
            uriStr = expandPrefixedName( prefix, suffix, tokenDT );
            break;
         }
         default -> throw new RiotException( "Expected IRI for datatype: " + token );
         }
         uriStr = resolveIRI( uriStr, tokenDT.getLine(), tokenDT.getColumn() );
         final RDFDatatype dt = NodeFactory.getType( uriStr );
         return new LiteralNode( (Node_Literal) createTypedLiteral( str, dt, line, col ), smartToken );
      }

      case LITERAL_LANG:
         return new LiteralNode( (Node_Literal) createLangLiteral( str, token.getImage2(), line, col ), smartToken );

      case STRING:
         return new LiteralNode( (Node_Literal) createStringLiteral( str, line, col ), smartToken );

      case BOOLEAN:
         return new LiteralNode( (Node_Literal) createTypedLiteral( str, XSDDatatype.XSDboolean, line, col ), smartToken );

      default: {
         final Node x = createNodeFromToken( currentGraph, token, line, col );
         if ( x != null ) {
            return new TokenNode( x, token );
         }
         getErrorHandler().fatal( "Not a valid token for an RDF term: " + token, line, col );
         return null;
      }
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
   public void setBaseIRI( final String baseIRI ) {
      parserProfile.setBaseIRI( baseIRI );
   }

   @Override
   public Triple createTriple( final Node subject, final Node predicate, final Node object, final long line, final long col ) {
      return parserProfile.createTriple( subject, predicate, object, line, col );
   }

   @Override
   public Quad createQuad( final Node graph, final Node subject, final Node predicate, final Node object, final long line, final long col ) {
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
      return new BlankNode( (Node_Blank) parserProfile.createBlankNode( scope, line, col ), new SmartToken( token ) );
   }

   @Override
   public Node createTripleNode( final Node subject, final Node predicate, final Node object, final long line, final long col ) {
      return parserProfile.createTripleNode( subject, predicate, object, line, col );
   }

   @Override
   public Node createTripleNode( final Triple triple, final long line, final long col ) {
      return parserProfile.createTripleNode( triple, line, col );
   }

   @Override
   public Node createGraphNode( final Graph graph, final long line, final long col ) {
      return parserProfile.createGraphNode( graph, line, col );
   }

   @Override
   public Node createNodeFromToken( final Node scope, final Token token, final long line, final long col ) {
      return parserProfile.createNodeFromToken( scope, token, line, col );
   }

   /*
    * (non-javadoc)
    * Implementation adapted from {@link ParserProfileStd#expandPrefixedName(String, String, Token)}
    */
   private String expandPrefixedName( final String prefix, final String localPart, final Token token ) {
      final String expansion = getPrefixMap().expand( prefix, localPart );
      if ( expansion == null ) {
         if ( ARQ.isTrue( ARQ.fixupUndefinedPrefixes ) ) {
            return RiotLib.fixupPrefixIRI( prefix, localPart );
         }
      }
      return expansion;
   }
}
