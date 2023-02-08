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
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.graph.NodeConst;
import org.apache.jena.vocabulary.RDF;

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

   private StringBuffer buffer;
   private SmartToken highlightToken;
   private int currentColumn;

   public String constructDetailedMessage( final RDFNode highlight, final String message ) {
      currentColumn = 0;
      highlightToken = extractToken( highlight.asNode() );

      if ( highlightToken == null ) {
         // without meaningful position information (line/col), we are not able to provide any additional context/details
         return message;
      }

      final Model model = highlight.getModel();
      final List<Statement> statements = model.listStatements()
            .filterKeep( statement -> !statement.getSubject().isAnon() && spansLine( statement, highlightToken.line() ) )
            .toList();
      return formatError( statements, message );
   }

   private boolean spansLine( final Statement statement, final int lineNumber ) {
      return isOnLine( statement.getSubject(), lineNumber )
            || isOnLine( statement.getPredicate(), lineNumber )
            || isOnLine( statement.getObject(), lineNumber );
   }

   private boolean isOnLine( final RDFNode node, final int lineNumber ) {
      final SmartToken nodePosition = extractToken( node.asNode() );
      return nodePosition != null && nodePosition.line() == lineNumber;
   }

   private String formatError( final List<Statement> statements, final String errorMessage ) {
      final int prefixWidth = String.valueOf( highlightToken.line() ).length() + 1;
      final String prefix = " ".repeat( prefixWidth ) + "| ";
      return String.format( "%s> Error at line %d column %d%n", "-".repeat( prefixWidth ), highlightToken.line(), highlightToken.column() )
            + prefix + System.lineSeparator()
            + highlightToken.line() + " | " + formatStatements( statements ) + System.lineSeparator()
            + prefix + " ".repeat( highlightToken.column() ) + "^".repeat( highlightToken.content().length() ) + " " + errorMessage + System.lineSeparator()
            + prefix + System.lineSeparator();
   }

   private String formatStatements( final List<Statement> statements ) {
      sortSequentially( statements );
      buffer = new StringBuffer();
      for ( int i = 0; i < statements.size(); i++ ) {
         formatStatement( statements.get( i ), i + 1 < statements.size() ? statements.get( i + 1 ) : null );
      }
      return buffer.toString();
   }

   // model.listStatements() returns the statements in random order, but we want to format them as they appear in the source document,
   // so reconstruct the original sequence
   private void sortSequentially( final List<Statement> statements ) {
      statements.sort( Comparator.comparingInt( s -> firstPositionedNode( (Statement) s ).line() )
            .thenComparingInt( s -> firstPositionedNode( (Statement) s ).column() )
      );
   }

   // Several statements can share a subject with the same position, so try to determine the source order based on predicates and objects
   private SmartToken firstPositionedNode( final Statement statement ) {
      return Stream.of( statement.getPredicate(), statement.getObject() )
            .map( part -> extractToken( part.asNode() ) )
            .filter( Objects::nonNull )
            .findFirst()
            .orElse( null );
   }

   private boolean formatStatement( final Statement statement, final Statement followingStatement ) {
      if ( !formatNode( statement.getSubject().asNode(), statement.getPredicate().asNode() ) ) {
         return false;
      }
      if ( !formatNode( statement.getPredicate().asNode(), statement.getObject().asNode() ) ) {
         return false;
      }
      final RDFNode object = statement.getObject();
      if ( isList( statement.getModel(), object ) ) {
         if ( !formatList( object ) ) {
            return false;
         }
      } else if ( object.isAnon() ) {
         if ( !formatAnonymousNodes( object ) ) {
            return false;
         }
      } else {
         if ( !formatNode( object.asNode(), null ) ) {
            return false;
         }
      }

      if ( !statement.getSubject().isAnon() ) {
         spacedIfPossible( isLastSubjectedStatement( statement ) ? "." : ";", null );
      }

      return true;
   }

   // as the "whitespace" si not preserved by the parser ( in this case ';' and '.' ), we have to reconstruct it by looking at the relative positions of the
   // statements in the original document
   private boolean isLastSubjectedStatement( final Statement statement ) {
      final List<Statement> sameSubject = statement.getModel().listStatements( statement.getSubject(), null, (RDFNode) null ).toList();
      sortSequentially( sameSubject );
      return sameSubject.indexOf( statement ) == sameSubject.size() - 1;
   }

   private boolean formatNode( final Node node, final Node followingNode ) {
      final SmartToken nodePosition = extractToken( node );
      if ( null == nodePosition ) {
         // ugly special case: Jena replaces the RDF keyword 'a' with 'rdf:type' without position information internally
         if ( node.equals( NodeConst.nodeRDFType ) ) {
            spacedIfPossible( "a", followingNode );
         }
         return true;
      }

      // one RDF statement can span multiple lines, we are only interested in parts located on exactly the given line and not rendered yet
      if ( nodePosition.line() != highlightToken.line() || nodePosition.column() < currentColumn ) {
         return !(nodePosition.line() > highlightToken.line());
      }

      // whitespace is swallowed by the lexer, but we can reconstruct the proper positioning from the position information
      final int colDiff = nodePosition.column() - currentColumn;
      buffer.append( " ".repeat( colDiff ) );
      currentColumn += colDiff;

      final String nodeText = nodePosition.content();
      buffer.append( nodeText );
      currentColumn += nodeText.length();
      return true;
   }

   private boolean isList( final Model model, final RDFNode node ) {
      return node.equals( RDF.nil ) || (node.isResource() && model.contains( node.asResource(), RDF.rest, (RDFNode) null ));
   }

   private boolean formatList( final RDFNode listNode ) {
      final List<RDFNode> elementList = listNode.as( RDFList.class ).asJavaList();

      if ( elementList.isEmpty() ) {
         spacedIfPossible( "()", null );
         return true;
      }

      int index = 0;
      for ( final RDFNode element : elementList ) {
         if ( index == 0 && isOnLine( element, highlightToken.line() ) ) {
            spacedIfPossible( "(", null );
         }
         final boolean toContinue = element.isAnon() ? formatAnonymousNodes( element ) : formatNode( element.asNode(), null );
         if ( !toContinue ) {
            return false;
         }
         index++;
      }

      spacedIfPossible( ")", null );
      return true;
   }

   private boolean formatAnonymousNodes( final RDFNode object ) {
      final List<Statement> anons = object.getModel().listStatements( object.asResource(), null, (RDFNode) null ).toList();
      sortSequentially( anons );

      if ( anons.isEmpty() ) {
         spacedIfPossible( "[]", null );
         return true;
      }

      int index = 0;
      for ( final Statement anon : anons ) {
         if ( !formatStatement( anon, null ) ) {
            return false;
         }
         if ( index != anons.size() - 1 ) {
            spacedIfPossible( ";", null );
         }
         index++;
      }

      spacedIfPossible( "]", null );
      return true;
   }

   private boolean enoughRoomForSpace( final String content, final Node followingNode ) {
      if ( followingNode == null ) {
         return true;
      }
      final SmartToken followingToken = extractToken( followingNode );
      return followingToken.line() != highlightToken.line() || followingToken.column() > (currentColumn + content.length() + 1);
   }

   private void spacedIfPossible( final String content, final Node followingNode ) {
      if ( enoughRoomForSpace( content, followingNode ) ) {
         buffer.append( ' ' );
         currentColumn++;
      }
      buffer.append( content );
      currentColumn += content.length();
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
