/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.turtle.languageserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.esmf.turtle.languageserver.lsp.text.TurtleTextDocumentService;

import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.TextDocumentItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class GraphicalViewJsonRpcTransportTest {
   private static final String NAMESPACE = "urn:samm:example.transport:1.0.0#";
   private static final String META_MODEL = "urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#";
   private final ObjectMapper mapper = new ObjectMapper();

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

      try ( final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open() ) {
         listener.bind( new InetSocketAddress( "localhost", 0 ) );
         final java.util.concurrent.Future<AsynchronousSocketChannel> accepted = listener.accept();
         try ( final Socket client = new Socket() ) {
            client.connect( (InetSocketAddress) listener.getLocalAddress() );
            client.setSoTimeout( 30_000 );
            final CompletableFuture<Void> handler = CompletableFuture.runAsync( () -> {
               try {
                  TurtleLanguageServer.handleClientConnection( accepted.get( 2, TimeUnit.SECONDS ), server );
               } catch ( final Exception exception ) {
                  throw new IllegalStateException( exception );
               }
            } );

            int id = 1;
            assertWarning( client, id++, "turtle/graphicalView/resolveTarget",
                  headerParams( validUri, NAMESPACE + "Missing" ), "notFound" );
            assertWarning( client, id++, "turtle/graphicalView/resolveTarget",
                  headerParams( ambiguousUri, NAMESPACE + "Duplicate" ), "ambiguous" );
            assertWarning( client, id++, "turtle/graphicalView/resolveTarget",
                  headerParams( "https://example.test/model.ttl", NAMESPACE + "Transport" ), "unsupportedUri" );
            assertWarning( client, id++, "turtle/graphicalView/resolveTarget",
                  headerParams( validUri, "not-a-urn" ), "temporarilyUnresolvable" );

            assertWarning( client, id++, "turtle/graphicalView/resolveAttributeTarget",
                  attributeParams( validUri, NAMESPACE + "Transport", META_MODEL + "name" ), "notFound" );
            assertWarning( client, id++, "turtle/graphicalView/resolveAttributeTarget",
                  attributeParams( ambiguousUri, NAMESPACE + "Ambiguous", META_MODEL + "see" ), "ambiguous" );
            assertWarning( client, id++, "turtle/graphicalView/resolveAttributeTarget",
                  attributeParams( "https://example.test/model.ttl", NAMESPACE + "Transport", META_MODEL + "see" ),
                  "unsupportedUri" );
            assertWarning( client, id++, "turtle/graphicalView/resolveAttributeTarget",
                  attributeParams( validUri, "not-a-urn", META_MODEL + "see" ), "temporarilyUnresolvable" );

            assertLocation( client, id++, "turtle/graphicalView/resolveTarget",
                  headerParams( validUri, NAMESPACE + "Transport" ), validUri );
            assertLocation( client, id, "turtle/graphicalView/resolveAttributeTarget",
                  attributeParams( validUri, NAMESPACE + "Transport", META_MODEL + "description" ), validUri );

            client.close();
            handler.get( 5, TimeUnit.SECONDS );
         }
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

   private JsonNode request( final Socket client, final int id, final String method, final String params ) throws Exception {
      final String request = "{\"jsonrpc\":\"2.0\",\"id\":" + id + ",\"method\":\"" + method
            + "\",\"params\":" + params + "}";
      writeMessage( client.getOutputStream(), request );
      return readResponse( client.getInputStream(), id );
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

   private static Set<String> fieldNames( final JsonNode node ) {
      return node.properties().stream().map( java.util.Map.Entry::getKey )
            .collect( java.util.stream.Collectors.toCollection( LinkedHashSet::new ) );
   }

   private JsonNode readResponse( final InputStream input, final int id ) throws Exception {
      while ( true ) {
         final JsonNode message = mapper.readTree( readMessage( input ) );
         if ( message.has( "id" ) && message.get( "id" ).asInt() == id ) {
            return message;
         }
      }
   }

   private static void writeMessage( final OutputStream output, final String json ) throws Exception {
      final byte[] content = json.getBytes( StandardCharsets.UTF_8 );
      output.write( ( "Content-Length: " + content.length + "\r\n\r\n" ).getBytes( StandardCharsets.US_ASCII ) );
      output.write( content );
      output.flush();
   }

   private static String readMessage( final InputStream input ) throws Exception {
      int contentLength = -1;
      String line;
      while ( !( line = readHeaderLine( input ) ).isEmpty() ) {
         if ( line.regionMatches( true, 0, "Content-Length:", 0, "Content-Length:".length() ) ) {
            contentLength = Integer.parseInt( line.substring( "Content-Length:".length() ).trim() );
         }
      }
      if ( contentLength < 0 ) {
         throw new EOFException( "Missing Content-Length" );
      }
      return new String( input.readNBytes( contentLength ), StandardCharsets.UTF_8 );
   }

   private static String readHeaderLine( final InputStream input ) throws Exception {
      final StringBuilder line = new StringBuilder();
      for ( int value; ( value = input.read() ) >= 0; ) {
         if ( value == '\n' ) {
            return line.toString();
         }
         if ( value != '\r' ) {
            line.append( (char) value );
         }
      }
      throw new EOFException();
   }

   private static String validModel() {
      return """
         @prefix : <urn:samm:example.transport:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

         :Transport a samm:Aspect ;
            samm:description "Transport aspect"@en ;
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
