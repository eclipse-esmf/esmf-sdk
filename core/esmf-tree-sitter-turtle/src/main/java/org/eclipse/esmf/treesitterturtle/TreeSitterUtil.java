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

import org.treesitter.TSNode;
import org.treesitter.TSTree;

public class TreeSitterUtil {
   private TreeSitterUtil() {}

   public static String print( final TSTree tree ) {
      return print( tree.getRootNode() );
   }

   public static String print( final TSNode node ) {
      final StringBuilder builder = new StringBuilder();
      print( node, builder, 0 );
      return builder.toString();
   }

   private static void print( final TSNode node, final StringBuilder builder, final int indentLevel ) {
      builder.append( "  ".repeat( indentLevel ) );
      builder.append( "- '" );
      builder.append( node.getType() );
      builder.append( "'" );
      if ( node.hasError() ) {
         builder.append( " (ERROR)" );
      }
      builder.append( "\n" );
      for ( int i = 0; i < node.getChildCount(); i++ ) {
         print( node.getChild( i ), builder, indentLevel + 1 );
      }
   }
}
