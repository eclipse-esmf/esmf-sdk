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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.treesitter.TSLanguage;
import org.treesitter.TSNode;
import org.treesitter.TSParser;
import org.treesitter.TSTree;

public class TreeSitterTurtleTest {
   @Test
   void testParser() {
      final TSParser parser = new TSParser();
      final TSLanguage turtle = new TreeSitterTurtle();
      parser.setLanguage( turtle );
      final TSTree tree = parser.parseString( null, """
         @prefix : <http://example.com> .
         :a a :b .
         """ );
      final TSNode rootNode = tree.getRootNode();
      assertThat( rootNode.toString() ).doesNotContain( "ERROR" );
      assertThat( rootNode.getChild( 0 ).getChild( 0 ).getChild( 0 ).getGrammarType() ).isEqualTo( "@prefix" );
      System.out.println( rootNode );
   }
}
