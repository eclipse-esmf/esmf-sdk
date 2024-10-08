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

package org.eclipse.esmf.aspectmodel.shacl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import org.eclipse.esmf.aspectmodel.resolver.parser.PlainTextFormatter;
import org.eclipse.esmf.aspectmodel.resolver.parser.RdfTextFormatter;
import org.eclipse.esmf.aspectmodel.resolver.parser.SmartToken;
import org.eclipse.esmf.aspectmodel.resolver.parser.TokenRegistry;

import com.google.common.collect.Ordering;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.graph.NodeConst;
import org.apache.jena.vocabulary.RDF;

/**
 * Rust-like message formatter. Formatted messages look something like the following example:
 * <br/><br/>
 * <pre>
 * ---> Error at line 11 column 20
 *    |
 * 11 |   :testProperty [ a :SomethingElse ]
 *    |                     ^^^^^^^^^^^^^^ Property ':testProperty' on ':Foo' has type ':SomethingElse', but only ':TestClass2' is allowed.
 *    |
 * </pre>
 */
public class RustLikeFormatter {

   private SmartToken highlightToken;
   private int currentColumn;
   private final RdfTextFormatter textFormatter;

   // some statements (like anonymous nodes or lists) can be handled out-of-order, so we remember them to not go over them twice
   private final Set<Statement> seen = new HashSet<>();
   private List<Statement> candidateStatements;

   // The parsed model does not contain all the original tokens ( braces in lists, semicolons between statements etc.). But as we want to
   // achieve as nice and natural formatting as possible, we look at the available information to achieve the proper spacing.
   private List<Integer> knownPositions;

   // Will use the PlainTextFormatter as default formatter.
   public RustLikeFormatter() {
      this( new PlainTextFormatter() );
   }

   public RustLikeFormatter( final RdfTextFormatter textFormatter ) {
      this.textFormatter = textFormatter;
   }

   public String constructDetailedMessage( final RDFNode highlight, final String message ) {
      return constructDetailedMessage( highlight, message, highlight.getModel() );
   }

   public String constructDetailedMessage( final RDFNode highlight, final String message, @Nullable final Model rawModel ) {
      final Model sourceModel = rawModel == null ? highlight.getModel() : rawModel;
      highlightToken = extractToken( highlight );

      if ( highlightToken == null ) {
         // without meaningful position information (line/col), we are not able to provide any additional context/details
         return message;
      }

      candidateStatements = sourceModel.listStatements()
            .filterDrop( statement -> Objects.equals( statement.getPredicate(),
                  RDF.rest ) ) // internal Jena list bookkeeping, nothing interesting for us
            .filterKeep( statement -> spansLine( statement, highlightToken.line() ) )
            .toList();
      return formatError( message );
   }

   private boolean spansLine( final Statement statement, final int lineNumber ) {
      return isOnLine( statement.getSubject(), lineNumber )
            || isOnLine( statement.getPredicate(), lineNumber )
            || isOnLine( statement.getObject(), lineNumber );
   }

   private boolean isOnLine( final RDFNode node, final int lineNumber ) {
      final SmartToken nodePosition = extractToken( node );
      return nodePosition != null && nodePosition.line() == lineNumber;
   }

   private String formatError( final String errorMessage ) {
      final int prefixWidth = String.valueOf( highlightToken.line() ).length() + 1;
      final String prefix = " ".repeat( prefixWidth ) + "| ";
      return errorStyle(
            String.format( "%s> Error at line %d column %d%n", "-".repeat( prefixWidth ), highlightToken.line(), highlightToken.column() ) )
            + prefix + System.lineSeparator()
            + highlightToken.line() + " | " + formatStatements() + System.lineSeparator()
            + prefix + " ".repeat( highlightToken.column() ) + errorStyle(
            "^".repeat( highlightToken.content().length() ) + " " + errorMessage ) + System.lineSeparator()
            + prefix + System.lineSeparator();
   }

   private String errorStyle( final String plainText ) {
      textFormatter.reset();
      textFormatter.formatError( plainText );
      return textFormatter.getResult();
   }

   private String formatStatements() {
      currentColumn = 0;
      seen.clear();
      final List<Statement> inDocumentOrder = sortSequentially( candidateStatements );
      knownPositions = inDocumentOrder.stream()
            .flatMap( statement -> Stream.of( statement.getSubject(), statement.getPredicate(), statement.getObject() ) )
            .map( RustLikeFormatter::extractToken )
            .filter( Objects::nonNull )
            .filter( token -> token.line() == highlightToken.line() )
            .map( SmartToken::column )
            .toList();

      textFormatter.reset();
      inDocumentOrder.forEach( this::formatStatement );
      return textFormatter.getResult();
   }

   // model.listStatements() returns the statements in random order, but we want to format them as they appear in the source document,
   // so reconstruct the original sequence
   private List<Statement> sortSequentially( final List<Statement> statements ) {
      return Ordering.from( this::documentOrder ).immutableSortedCopy( statements );
   }

   private int documentOrder( final Statement s1, final Statement s2 ) {
      final SmartToken t1;
      final SmartToken t2;
      if ( Objects.equals( s1.getSubject(), s2.getSubject() ) ) {
         // several statements can share a subject with the same position, so try to determine the source order based on predicates and
         // objects
         t1 = excludeSubject( s1 );
         t2 = excludeSubject( s2 );
      } else {
         t1 = includeSubject( s1 );
         t2 = includeSubject( s2 );
      }
      final int res = Integer.compare( t1.line(), t2.line() );
      return ( res != 0 ) ? res : Integer.compare( t1.column(), t2.column() );
   }

   private SmartToken includeSubject( final Statement statement ) {
      return extractToken( firstPositionedNode( List.of( statement.getSubject(), statement.getPredicate(), statement.getObject() ) ) );
   }

   private SmartToken excludeSubject( final Statement statement ) {
      return extractToken( firstPositionedNode( List.of( statement.getPredicate(), statement.getObject() ) ) );
   }

   private RDFNode firstPositionedNode( final List<RDFNode> nodes ) {
      return nodes.stream()
            .filter( node -> extractToken( node ) != null )
            .findFirst()
            .orElse( null );
   }

   private boolean formatStatement( final Statement statement ) {
      if ( seen.contains( statement ) ) {
         return true;
      }

      if ( isListStatement( statement ) ) { // special case: an element in the "middle" of a list
         final Statement listHead = findListHead( statement );
         final Statement listOwner = listHead.getModel().listStatements( null, null, listHead.getSubject() ).nextStatement();
         // to be able to render everything correctly, we need to start rendering at the list-owning statement
         return formatStatement( listOwner );
      }

      if ( !formatNode( statement.getSubject() ) ) {
         return false;
      }

      if ( !formatNode( statement.getPredicate() ) ) {
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
         if ( !formatNode( object ) ) {
            return false;
         }
      }

      if ( !statement.getSubject().isAnon() ) {
         spacedIfPossible( isLastSubjectedStatement( statement ) ? "." : ";" );
      }

      return true;
   }

   // as the "whitespace" is not preserved by the parser ( in this case ';' and '.' ), we have to reconstruct it by looking at the
   // relative positions of the
   // statements as they would appear in the original document
   private boolean isLastSubjectedStatement( final Statement statement ) {
      final List<Statement> sameSubject = statement.getModel().listStatements( statement.getSubject(), null, (RDFNode) null ).toList();
      final List<Statement> inDocumentOrder = sortSequentially( sameSubject );
      return inDocumentOrder.indexOf( statement ) == sameSubject.size() - 1;
   }

   private boolean formatNode( final RDFNode node ) {
      final SmartToken nodePosition = extractToken( node );
      if ( null == nodePosition ) {
         // ugly special case: Jena internally replaces the RDF keyword 'a' with 'rdf:type' without position information
         if ( NodeConst.nodeRDFType.equals( node.asNode() ) ) {
            spacedIfPossible( "a " );
         }
         if ( NodeConst.nodeTrue.equals( node.asNode() ) ) {
            spacedIfPossible( "true" );
         }
         if ( NodeConst.nodeFalse.equals( node.asNode() ) ) {
            spacedIfPossible( "false" );
         }
         return true;
      }

      // one RDF statement can span multiple lines, we are only interested in parts located on exactly the given line and not rendered yet
      if ( nodePosition.line() != highlightToken.line() || nodePosition.column() < currentColumn ) {
         return !( nodePosition.line() > highlightToken.line() );
      }

      // whitespace is swallowed by the lexer, but we can reconstruct the proper positioning from the position information
      final int colDiff = nodePosition.column() - currentColumn;
      formatText( " ".repeat( colDiff ) );
      formatText( nodePosition );
      return true;
   }

   private boolean isListStatement( final Statement statement ) {
      return statement.getPredicate().asNode().equals( NodeConst.nodeFirst ) || statement.getPredicate().asNode()
            .equals( NodeConst.nodeRest );
   }

   private boolean isList( final Model model, final RDFNode node ) {
      return node.equals( RDF.nil ) || ( node.isResource() && model.contains( node.asResource(), RDF.rest, (RDFNode) null ) );
   }

   private Statement findListHead( final Statement listElement ) {
      Statement listElementStatement = listElement;
      while ( true ) {
         final StmtIterator iter = listElementStatement.getModel().listStatements( null, RDF.rest, listElementStatement.getSubject() );
         if ( !iter.hasNext() ) {
            break;
         }
         listElementStatement = iter.nextStatement();
      }
      return listElementStatement;
   }

   private boolean formatList( final RDFNode listNode ) {
      RDFList list = listNode.as( RDFList.class );
      while ( !list.isEmpty() ) {
         seen.add( list.getProperty( RDF.first ) );
         list = list.getTail();
      }

      final List<RDFNode> elementList = listNode.as( RDFList.class ).asJavaList();
      if ( elementList.isEmpty() ) {
         spacedIfPossible( "()" );
         return true;
      }

      int index = 0;
      for ( final RDFNode element : elementList ) {
         if ( index == 0 && isOnLine( element, highlightToken.line() ) ) {
            spacedIfPossible( "(" );
         }
         final boolean toContinue = element.isAnon() ? formatAnonymousNodes( element ) : formatNode( element );
         if ( !toContinue ) {
            return false;
         }
         index++;
      }

      spacedIfPossible( ")" );
      return true;
   }

   private boolean formatAnonymousNodes( final RDFNode object ) {
      final List<Statement> anons = object.getModel().listStatements( object.asResource(), null, (RDFNode) null ).toList();
      if ( anons.isEmpty() ) {
         spacedIfPossible( "[]" );
         return true;
      }

      final List<Statement> inDocumentOrder = sortSequentially( anons );

      int index = 0;
      for ( final Statement anon : inDocumentOrder ) {
         if ( !formatStatement( anon ) ) {
            seen.addAll( anons );
            return false;
         }
         seen.add( anon );
         if ( index != anons.size() - 1 ) {
            spacedIfPossible( ";" );
         }
         index++;
      }

      spacedIfPossible( "]" );
      return true;
   }

   private boolean enoughRoomForSpace( final String content ) {
      final int upperBoundIndex = Math.abs( Collections.binarySearch( knownPositions, currentColumn ) ) - 1;
      return upperBoundIndex >= knownPositions.size() || knownPositions.get( upperBoundIndex ) > ( currentColumn + content.length() );
   }

   private void spacedIfPossible( final String content ) {
      if ( enoughRoomForSpace( content ) ) {
         formatText( " " );
      }
      formatText( content );
   }

   private void formatText( final SmartToken token ) {
      currentColumn += token.structureContent( textFormatter );
   }

   private void formatText( final String reconstructedText ) {
      reconstructedText.chars().forEach( chr -> textFormatter.formatPrimitive( String.valueOf( (char) chr ) ) );
      currentColumn += reconstructedText.length();
   }

   private static SmartToken extractToken( final RDFNode rdfNode ) {
      return TokenRegistry.getToken( rdfNode.asNode() ).orElse( null );
   }
}
