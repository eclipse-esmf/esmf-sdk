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

class GraphicalViewJsonRpcTransportTest {
   private static final String NAMESPACE = "urn:samm:example.transport:1.0.0#";
   private static final String META_MODEL = "urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#";

   @Test
   void productionLauncherEmitsCanonicalWarningsAndValidLocationsForBothResolveMethods(
         @TempDir final Path directory ) throws Exception {
      final String validUri = Files.writeString( directory.resolve( "Transport.ttl" ), validModel() ).toUri().toString();
      final String ambiguousUri = Files.writeString( directory.resolve( "AmbiguousTransport.ttl" ), ambiguousModel() )
            .toUri().toString();
      final TurtleLanguageServer server = new TurtleLanguageServer();
      final TurtleTextDocumentService textDocuments = (TurtleTextDocumentService) server.getTextDocumentService();
      open( textDocuments, validUri, validModel() );
      open( textDocuments, ambiguousUri, ambiguousModel() );

      try {
         withConnection( server, client -> {
            int id = 1;
            assertWarning( client, id++, "turtle/graphicalView/resolveTarget",
                  headerParams( validUri, NAMESPACE + "Missing" ), "notFound" );
            assertWarning( client, id++, "turtle/graphicalView/resolveTarget",
                  headerParams( ambiguousUri, NAMESPACE + "Duplicate" ), "ambiguous" );
            assertWarning( client, id++, "turtle/graphicalView/resolveTarget",
                  headerParams( "https://example.test/model.ttl", NAMESPACE + "Transport" ), "unsupportedUri" );
            assertWarning( client, id++, "turtle/graphicalView/resolveTarget",
                  headerParams( validUri, "not-a-urn" ), "unresolvable" );

            assertWarning( client, id++, "turtle/graphicalView/resolveAttributeTarget",
                  attributeParams( validUri, NAMESPACE + "Transport", META_MODEL + "name" ), "notFound" );
            assertWarning( client, id++, "turtle/graphicalView/resolveAttributeTarget",
                  attributeParams( ambiguousUri, NAMESPACE + "Ambiguous", META_MODEL + "see" ), "ambiguous" );
            assertWarning( client, id++, "turtle/graphicalView/resolveAttributeTarget",
                  attributeParams( "https://example.test/model.ttl", NAMESPACE + "Transport", META_MODEL + "see" ),
                  "unsupportedUri" );
            assertWarning( client, id++, "turtle/graphicalView/resolveAttributeTarget",
                  attributeParams( validUri, "not-a-urn", META_MODEL + "see" ), "unresolvable" );

            assertLocation( client, id++, "turtle/graphicalView/resolveTarget",
                  headerParams( validUri, NAMESPACE + "Transport" ), validUri );
            assertLocation( client, id, "turtle/graphicalView/resolveAttributeTarget",
                  attributeParams( validUri, NAMESPACE + "Transport", META_MODEL + "description" ), validUri );
         } );
      } finally {
         close( textDocuments, validUri );
         close( textDocuments, ambiguousUri );
         server.shutdown().get( 5, TimeUnit.SECONDS );
      }
   }

   private void assertWarning( final Socket client, final int id, final String method, final String params,
         final String expected ) throws Exception {
      final JsonNode response = request( client, id, method, params );
      assertThat( fieldNames( response ) ).containsExactlyInAnyOrder( "jsonrpc", "id", "result" );
      final JsonNode result = response.get( "result" );
      assertThat( fieldNames( result ) ).as( "raw response: %s", response ).containsExactly( "warning" );
      assertThat( result.has( "location" ) ).isFalse();
      assertThat( result.get( "warning" ).isTextual() ).as( "raw response: %s", response ).isTrue();
      assertThat( result.get( "warning" ).asText() ).as( "raw response: %s", response ).isEqualTo( expected );
   }

   private void assertLocation( final Socket client, final int id, final String method, final String params,
         final String expectedUri ) throws Exception {
      final JsonNode response = request( client, id, method, params );
      final JsonNode result = response.get( "result" );
      assertThat( fieldNames( result ) ).containsExactly( "location" );
      assertThat( result.has( "warning" ) ).isFalse();
      final JsonNode location = result.get( "location" );
      assertThat( fieldNames( location ) ).containsExactlyInAnyOrder( "uri", "range" );
      assertThat( location.get( "uri" ).asText() ).isEqualTo( expectedUri );
      assertThat( fieldNames( location.get( "range" ) ) ).containsExactlyInAnyOrder( "start", "end" );
      assertPosition( location.at( "/range/start" ) );
      assertPosition( location.at( "/range/end" ) );
   }

   private static void assertPosition( final JsonNode position ) {
      assertThat( fieldNames( position ) ).containsExactlyInAnyOrder( "line", "character" );
      assertThat( position.get( "line" ).isIntegralNumber() ).isTrue();
      assertThat( position.get( "character" ).isIntegralNumber() ).isTrue();
      assertThat( position.get( "line" ).intValue() ).isNotNegative();
      assertThat( position.get( "character" ).intValue() ).isNotNegative();
   }

   private static String headerParams( final String sourceUri, final String elementUrn ) {
      return "{\"sourceUri\":\"" + sourceUri + "\",\"elementUrn\":\"" + elementUrn + "\"}";
   }

   private static String attributeParams( final String sourceUri, final String ownerUrn, final String predicateUrn ) {
      return "{\"sourceUri\":\"" + sourceUri + "\",\"ownerUrn\":\"" + ownerUrn
            + "\",\"predicateUrn\":\"" + predicateUrn + "\",\"selection\":\"predicateStart\"}";
   }

   private static void open( final TurtleTextDocumentService service, final String uri, final String content ) {
      service.didOpen( new DidOpenTextDocumentParams( new TextDocumentItem( uri, "turtle", 1, content ) ) );
   }

   private static void close( final TurtleTextDocumentService service, final String uri ) {
      service.didClose( new DidCloseTextDocumentParams( new TextDocumentIdentifier( uri ) ) );
   }

   private static String validModel() {
      return """
         @prefix : <urn:samm:example.transport:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

         :Transport a samm:Aspect ;
            samm:description "Transport aspect."@en ;
            samm:see <https://example.test/reference> ;
            samm:properties () ;
            samm:operations () .
         """;
   }

   private static String ambiguousModel() {
      return """
         @prefix : <urn:samm:example.transport:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

         :Ambiguous a samm:Aspect ;
            samm:see <https://example.test/one> ;
            samm:see <https://example.test/two> ;
            samm:properties () ;
            samm:operations () .
         :Duplicate a samm:Characteristic .
         :Duplicate a samm:Characteristic .
         """;
   }
}
