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

package org.eclipse.esmf.treesitterturtle;

import org.jspecify.annotations.Nullable;
import org.treesitter.TSNode;
import org.treesitter.TSTree;

public class TreeSitterUtil {
   private TreeSitterUtil() {}

   public static String print( final TSTree tree ) {
      return print( tree.getRootNode() );
   }

   public static String print( final TSNode node ) {
      final StringBuilder builder = new StringBuilder();
      print( node, builder, 0, null );
      return builder.toString();
   }

   public static String print( final TSTree tree, final TurtleSyntaxTree.TokenProvider tokenProvider ) {
      return print( tree.getRootNode(), tokenProvider );
   }

   public static String print( final TSNode node, final TurtleSyntaxTree.TokenProvider tokenProvider ) {
      final StringBuilder builder = new StringBuilder();
      print( node, builder, 0, tokenProvider );
      return builder.toString();
   }

   private static void print( final TSNode node, final StringBuilder builder, final int indentLevel,
         final TurtleSyntaxTree.@Nullable TokenProvider tokenProvider ) {
      builder.repeat( "  ", indentLevel );
      builder.append( "- '" );
      builder.append( node.getType() );
      builder.append( "'" );
      if ( node.hasError() ) {
         builder.append( " (ERROR)" );
      } else if ( tokenProvider != null && node.getStartPoint().getRow() == node.getEndPoint().getRow() ) {
         final TurtleSyntaxTree.Location location = new TurtleSyntaxTree.Location(
               node.getStartPoint().getRow(),
               node.getStartPoint().getColumn(),
               node.getEndPoint().getRow(),
               node.getEndPoint().getColumn() );
         final String nodeContent = tokenProvider.apply( location );
         if ( !nodeContent.equals( node.getType() ) ) {
            builder.append( " (" );
            builder.append( nodeContent );
            builder.append( ")" );
         }
      }
      builder.append( "\n" );
      for ( int i = 0; i < node.getChildCount(); i++ ) {
         print( node.getChild( i ), builder, indentLevel + 1, tokenProvider );
      }
   }
}
