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

import org.treesitter.TSLanguage;
import org.treesitter.TSParser;
import org.treesitter.utils.NativeUtils;

/**
 * Language definition for RDF/Turtle to be used with {@link TSParser}:
 * {@code
 *    TSParser parser = new TSParser();
 *    parser.setLanguage( new TreeSitterTurtle() );
 * }
 */
public class TreeSitterTurtle extends TSLanguage {
   static {
      NativeUtils.loadLib( "lib/tree-sitter-turtle" );
   }

   private static native long tree_sitter_turtle();

   public TreeSitterTurtle() {
      super( tree_sitter_turtle() );
   }

   private TreeSitterTurtle( final long ptr ) {
      super( ptr );
   }

   @Override
   public TSLanguage copy() {
      return new TreeSitterTurtle( copyPtr() );
   }
}
