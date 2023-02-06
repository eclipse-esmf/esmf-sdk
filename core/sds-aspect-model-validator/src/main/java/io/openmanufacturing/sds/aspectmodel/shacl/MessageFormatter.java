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

package io.openmanufacturing.sds.aspectmodel.shacl;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.jena.graph.AnyNode;
import org.apache.jena.graph.BlankNode;
import org.apache.jena.graph.LiteralNode;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.UriNode;
import org.apache.jena.graph.VariableNode;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.graph.NodeConst;

import io.openmanufacturing.sds.aspectmodel.resolver.parser.SmartToken;

/**
 * Rust-like message formatter. Formatted messages look something like the following example:
 *
 * ---> Error at line 11 column 20
 *    |
 * 11 |   :testProperty [ a :SomethingElse ]
 *    |                     ^^^^^^^^^^^^^^ Property ':testProperty' on ':Foo' has type ':SomethingElse', but only ':TestClass2' is allowed.
 *    |
 */
public class MessageFormatter {

   public static String constructDetailedMessage( final RDFNode highlight, final String message ) {
      final SmartToken highlightToken = extractToken( highlight.asNode() );
      if ( highlightToken == null ) {
         // without meaningful position information (line/col), we are not able to provide any additional context/details
         return message;
      }
      final Model model = highlight.getModel();
      final List<Statement> statements = model.listStatements()
            .filterKeep( statement -> spansLine( statement, highlightToken.line() ) )
            .toList();
      return formatError( statements, highlightToken, message );
   }

   private static boolean spansLine( final Statement statement, final int lineNumber ) {
      return isOnLine( statement.getSubject(), lineNumber )
            || isOnLine( statement.getPredicate(), lineNumber )
            || isOnLine( statement.getObject(), lineNumber );
   }

   private static boolean isOnLine( final RDFNode node, final int lineNumber ) {
      final SmartToken nodePosition = extractToken( node.asNode() );
      return nodePosition != null && nodePosition.line() == lineNumber;
   }

   private static String formatError( final List<Statement> statements, final SmartToken highlightToken, final String errorMessage ) {
      final int prefixWidth = String.valueOf( highlightToken.line() ).length() + 1;
      final String prefix = " ".repeat( prefixWidth ) + "| ";
      return String.format( "%s> Error at line %d column %d%n", "-".repeat( prefixWidth ), highlightToken.line(), highlightToken.column() )
            + prefix + System.lineSeparator()
            + highlightToken.line() + " | " + formatStatements( statements, highlightToken ) + System.lineSeparator()
            + prefix + " ".repeat( highlightToken.column() ) + "^".repeat( highlightToken.content().length() ) + " " + errorMessage + System.lineSeparator()
            + prefix + System.lineSeparator();
   }

   private static SmartToken firstPositionedNode( final Statement statement ) {
      return Stream.of( statement.getSubject(), statement.getPredicate(), statement.getObject() )
            .map( part -> extractToken( part.asNode() ) )
            .filter( Objects::nonNull )
            .findFirst()
            .orElse( null );
   }

   private static String formatStatements( final List<Statement> statements, final SmartToken highlightToken ) {
      // model.listStatements() returns the statements in random order, but we want to format them as they appear in the source document,
      // so reconstruct the original sequence
      statements.sort( Comparator.comparingInt( s -> firstPositionedNode( (Statement) s ).line() )
            .thenComparingInt( s -> firstPositionedNode( (Statement) s ).column() )
      );

      final StringBuffer buffer = new StringBuffer();
      int currentColumn = 0;
      for ( final Statement statement : statements ) {
         currentColumn = formatStatement( statement, highlightToken, buffer, currentColumn );
      }

      return buffer.toString();
   }

   private static int formatStatement( final Statement statement, final SmartToken highlightToken, final StringBuffer buffer, int currentColumn ) {
      final boolean subjectIsAnon = statement.getSubject().isAnon();
      currentColumn = formatNode( buffer, statement.getSubject().asNode(), highlightToken, currentColumn );
      currentColumn = formatPredicateNode( buffer, statement.getPredicate().asNode(), statement.getObject().asNode(), highlightToken, currentColumn );
      final RDFNode object = statement.getObject();
      final int columnBeforeObject = currentColumn;
      if ( !object.isAnon() ) { // nested anonymous nodes will have one more statement, which will get a proper formatting
         currentColumn = formatNode( buffer, object.asNode(), highlightToken, currentColumn );
      }

      // we do not have a token for the closing brace of the anonymous nodes and therefore no position information, so we have to reconstruct it:
      // we add it only if the statement has been fully rendered
      if ( subjectIsAnon && columnBeforeObject != currentColumn ) {
         buffer.append( " " ).append( "]" );
         currentColumn += 2;
      }
      return currentColumn;
   }

   private static int formatPredicateNode( final StringBuffer buffer, final Node node, final Node objectNode, final SmartToken highlightToken,
         final int currentColumn ) {
      final SmartToken nodePosition = extractToken( node );
      if ( null == nodePosition ) {
         // ugly special case: Jena replaces the RDF keyword 'a' with 'rdf:type' without position information internally,
         // so we have to reconstruct the formatting as good as we can
         if ( node.equals( NodeConst.nodeRDFType ) ) {
            final SmartToken objectPosition = extractToken( objectNode );
            if ( objectPosition.line() != highlightToken.line() || objectPosition.column() > (currentColumn + 2) ) {
               // enough room for a space
               buffer.append( ' ' ).append( 'a' );
               return currentColumn + 2;
            } else {
               buffer.append( 'a' );
               return currentColumn + 1;
            }
         }
         return currentColumn;
      }

      return formatNode( buffer, node, highlightToken, currentColumn );
   }

   private static int formatNode( final StringBuffer buffer, final Node node, final SmartToken highlightToken, int currentColumn ) {
      final SmartToken nodePosition = extractToken( node );
      // one RDF statement can span multiple lines, we are only interested in parts located on exactly the given line
      if ( nodePosition.line() != highlightToken.line() ) {
         return currentColumn;
      }
      // whitespace is swallowed by the lexer, but we can reconstruct the proper positioning from the position information
      final int colDiff = nodePosition.column() - currentColumn;
      buffer.append( " ".repeat( colDiff ) );
      currentColumn += colDiff;

      final String nodeText = nodePosition.content();
      buffer.append( nodeText );
      return currentColumn + nodeText.length();
   }

   private static SmartToken extractToken( final Node n ) {
      if ( n instanceof AnyNode an ) {
         return an.getToken();
      } else if ( n instanceof BlankNode bn ) {
         return bn.getToken();
      } else if ( n instanceof LiteralNode ln ) {
         return ln.getToken();
      } else if ( n instanceof UriNode un ) {
         return un.getToken();
      } else if ( n instanceof VariableNode vn ) {
         return vn.getToken();
      } else {
         return null;
      }
   }
}
