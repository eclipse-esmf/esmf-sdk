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

package org.eclipse.esmf.turtle.languageserver.turtle.completion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Optional;

import org.apache.jena.sys.JenaSystem;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CompletionUtilsTest {

   @BeforeAll
   static void initJena() {
      JenaSystem.init();
   }

   @Test
   void positionAtHandlesBothLfAndCrlfLineEndings() {
      final String sourceLf = "abc\ndef\nxyz";
      assertThat( CompletionUtils.positionAt( sourceLf, 0 ) ).isEqualTo( new Position( 0, 0 ) );
      assertThat( CompletionUtils.positionAt( sourceLf, 4 ) ).isEqualTo( new Position( 1, 0 ) );
      assertThat( CompletionUtils.positionAt( sourceLf, 6 ) ).isEqualTo( new Position( 1, 2 ) );

      final String sourceCrlf = "abc\r\ndef\r\nxyz";
      assertThat( CompletionUtils.positionAt( sourceCrlf, 0 ) ).isEqualTo( new Position( 0, 0 ) );
      assertThat( CompletionUtils.positionAt( sourceCrlf, 5 ) ).isEqualTo( new Position( 1, 0 ) );
      assertThat( CompletionUtils.positionAt( sourceCrlf, 7 ) ).isEqualTo( new Position( 1, 2 ) );
   }

   @Test
   void expandResolvesDeclaredPrefixesAndIriBrackets() {
      final Map<String, String> namespaces = Map.of( "samm:", "urn:samm:test#", "ex:", "http://example.org/" );
      assertThat( CompletionUtils.expand( "samm:Aspect", namespaces ) ).isEqualTo( "urn:samm:test#Aspect" );
      assertThat( CompletionUtils.expand( "ex:thing", namespaces ) ).isEqualTo( "http://example.org/thing" );
      assertThat( CompletionUtils.expand( "<http://example.org/a>", namespaces ) ).isEqualTo( "http://example.org/a" );
   }

   @Test
   void expandReturnsEmptyForUnknownPrefixOrNoPrefix() {
      final Map<String, String> namespaces = Map.of( "samm:", "urn:samm:test#" );
      assertThat( CompletionUtils.expand( "unknown:thing", namespaces ) ).isEmpty();
      assertThat( CompletionUtils.expand( "noPrefix", namespaces ) ).isEmpty();
   }

   @Test
   void prefixedNameUsesWellKnownPrefixOfLatestSammNamespace() {
      assertThat( CompletionUtils.prefixedName( SammNs.SAMM.Characteristic().getURI() ) )
            .contains( "samm:Characteristic" );
   }

   @Test
   void prefixedNameIsEmptyForUnknownNamespace() {
      assertThat( CompletionUtils.prefixedName( "http://example.org/other#Thing" ) ).isEqualTo( Optional.empty() );
   }
}
