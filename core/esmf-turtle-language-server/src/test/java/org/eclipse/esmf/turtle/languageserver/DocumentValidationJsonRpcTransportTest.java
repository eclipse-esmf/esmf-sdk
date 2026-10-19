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
package org.eclipse.esmf.turtle.languageserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.esmf.turtle.languageserver.JsonRpcTransportTestSupport.fieldNames;
import static org.eclipse.esmf.turtle.languageserver.JsonRpcTransportTestSupport.request;
import static org.eclipse.esmf.turtle.languageserver.JsonRpcTransportTestSupport.withConnection;

import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.eclipse.esmf.turtle.languageserver.lsp.text.TurtleTextDocumentService;

import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tools.jackson.databind.JsonNode;

class DocumentValidationJsonRpcTransportTest {
   @Test
   void validateDocumentReturnsViolationReports( @TempDir final Path directory ) throws Exception {
      withValidationDocuments( directory, ( client, validUri, invalidUri ) -> {
         assertValidationReport( client, 1, validUri, true );
         assertValidationReport( client, 2, invalidUri, false );
      } );
   }

   @Test
   void validateDocumentV2ReturnsValidationStatus( @TempDir final Path directory ) throws Exception {
      withValidationDocuments( directory, ( client, validUri, invalidUri ) -> {
         assertValidationStatus( client, 1, validUri, "Success" );
         assertValidationStatus( client, 2, invalidUri, "Error" );
      } );
   }

   @Test
   void validateDocumentV2RejectsInvalidParameters() throws Exception {
      final TurtleLanguageServer server = new TurtleLanguageServer();
      try {
         withConnection( server, client -> {
            int id = 1;
            for ( final String params : new String[] { "null", "{}", "{\"uri\":null}", "{\"uri\":\"\"}" } ) {
               final JsonNode response = request( client, id++, "turtle/aspectValidation/validateDocumentV2", params );
               assertThat( fieldNames( response ) ).containsExactlyInAnyOrder( "jsonrpc", "id", "error" );
               assertThat( response.at( "/error/code" ).asInt() ).isEqualTo( -32602 );
               assertThat( response.at( "/error/message" ).asText() ).isEqualTo( "Missing or invalid parameters" );
            }
         } );
      } finally {
         server.shutdown().get( 5, TimeUnit.SECONDS );
      }
   }

   private void withValidationDocuments( final Path directory, final ValidationAssertions assertions ) throws Exception {
      final String validContent = """
         @prefix : <urn:samm:example.transport:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

         :Transport a samm:Aspect ;
            samm:description "Transport aspect."@en ;
            samm:see <https://example.test/reference> ;
            samm:properties () ;
            samm:operations () .
         """;
      final String validUri = Files.writeString( directory.resolve( "Transport.ttl" ), validContent ).toUri().toString();
      final String invalidContent = "This is not valid Turtle";
      final String invalidUri = Files.writeString( directory.resolve( "Invalid.ttl" ), invalidContent ).toUri().toString();
      final TurtleLanguageServer server = new TurtleLanguageServer();
      final TurtleTextDocumentService textDocuments = (TurtleTextDocumentService) server.getTextDocumentService();
      try {
         textDocuments.didOpen( new DidOpenTextDocumentParams( new TextDocumentItem( validUri, "turtle", 1, validContent ) ) );
         textDocuments.didOpen( new DidOpenTextDocumentParams( new TextDocumentItem( invalidUri, "turtle", 1, invalidContent ) ) );
         withConnection( server, client -> assertions.verify( client, validUri, invalidUri ) );
      } finally {
         textDocuments.didClose( new DidCloseTextDocumentParams( new TextDocumentIdentifier( validUri ) ) );
         textDocuments.didClose( new DidCloseTextDocumentParams( new TextDocumentIdentifier( invalidUri ) ) );
         server.shutdown().get( 5, TimeUnit.SECONDS );
      }
   }

   private void assertValidationReport( final Socket client, final int id, final String uri, final boolean empty )
         throws Exception {
      final JsonNode response = request( client, id, "turtle/aspectValidation/validateDocument",
            "{\"uri\":\"" + uri + "\"}" );
      assertThat( fieldNames( response ) ).containsExactlyInAnyOrder( "jsonrpc", "id", "result" );
      final JsonNode result = response.get( "result" );
      assertThat( fieldNames( result ) ).containsExactly( "violations" );
      assertThat( result.get( "violations" ).isArray() ).isTrue();
      assertThat( result.get( "violations" ).isEmpty() ).as( "raw response: %s", response ).isEqualTo( empty );
   }

   private void assertValidationStatus( final Socket client, final int id, final String uri, final String expected )
         throws Exception {
      final JsonNode response = request( client, id, "turtle/aspectValidation/validateDocumentV2",
            "{\"uri\":\"" + uri + "\"}" );
      assertThat( fieldNames( response ) ).containsExactlyInAnyOrder( "jsonrpc", "id", "result" );
      assertThat( response.get( "result" ).isTextual() ).isTrue();
      assertThat( response.get( "result" ).asText() ).isEqualTo( expected );
   }

   @FunctionalInterface
   private interface ValidationAssertions {
      void verify( Socket client, String validUri, String invalidUri ) throws Exception;
   }
}
