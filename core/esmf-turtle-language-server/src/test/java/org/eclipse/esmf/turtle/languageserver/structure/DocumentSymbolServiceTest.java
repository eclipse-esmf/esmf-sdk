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

package org.eclipse.esmf.turtle.languageserver.structure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.SymbolKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DocumentSymbolServiceTest {
   private DocumentSymbolService documentSymbolService;

   @BeforeEach
   void setUp() {
      documentSymbolService = new DocumentSymbolService( new TreeSitterTurtleParserService() );
   }

   @Test
   void testDocumentSymbolDefinition() {
      final Document document = new Document( "test.ttl", """
         @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
         @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
         @prefix unit: <urn:samm:org.eclipse.esmf.samm:unit:2.2.0#> .

         :Aspect a samm:Aspect ;
            samm:preferredName "Test Aspect"@en ;
            samm:description "This is a test description"@en ;
            samm:properties ( :property ) ;
            samm:operations ( ) .

         :property a samm:Property ;
            samm:preferredName "Test Property"@en ;
            samm:description "This is a test property description"@en ;
            samm:characteristic samm-c:Text .
         """ );
      final List<DocumentSymbol> symbols = documentSymbolService.symbols( document );

      assertThat( symbols ).isNotEmpty();
      assertThat( symbols ).anySatisfy( namespaceSymbol -> {
         assertThat( namespaceSymbol.getKind().equals( SymbolKind.Namespace ) );
         assertThat( namespaceSymbol.getName().equals( "urn:samm:org.eclipse.esmf.test:1.0.0" ) );
      } );
      assertThat( symbols ).anySatisfy( aspectSymbol -> {
         assertThat( aspectSymbol.getKind().equals( SymbolKind.Method ) );
         assertThat( aspectSymbol.getName().equals( ":Aspect" ) );
      } );
      assertThat( symbols ).anySatisfy( propertySymbol -> {
         assertThat( propertySymbol.getKind().equals( SymbolKind.Method ) );
         assertThat( propertySymbol.getName().equals( ":property" ) );
      } );
   }
}
