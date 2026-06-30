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

package org.eclipse.esmf.turtle.languageserver.aspect.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings( { "HttpUrlsUsage" } )
class ParsedAspectModelFileLoaderTest {
   private TreeSitterTurtleParserService parserService;
   private ParsedAspectModelFileLoader loader;

   @BeforeEach
   void setUp() {
      parserService = new TreeSitterTurtleParserService();
      loader = new ParsedAspectModelFileLoader();
   }

   @Test
   void supportsAspectModelDocuments() {
      final ParsedDocument document = parse( """
         @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

         :Aspect a samm:Aspect .
         """ );

      assertThat( loader.supports( document ) ).isTrue();
   }

   @Test
   void doesNotSupportPlainTurtleDocuments() {
      final ParsedDocument document = parse( """
         @prefix ex: <http://example.org/> .

         ex:subject ex:predicate ex:object .
         """ );

      assertThat( loader.supports( document ) ).isFalse();
   }

   @Test
   void loadsRawAspectModelFileFromParsedDocument() {
      final ParsedDocument document = parse( """
         @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

         :Aspect a samm:Aspect .
         """ );

      final RawAspectModelFile file = loader.load( document );

      assertThat( file.sourceLocation() ).contains( java.net.URI.create( "test.ttl" ) );
      assertThat( file.sourceRepresentation() ).contains( ":Aspect" );
   }

   private ParsedDocument parse( final String content ) {
      final Document document = new Document( "test.ttl", content );
      parserService.onOpen( document );
      return parserService.apply( document );
   }
}
