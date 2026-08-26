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
   private final ObjectMapper mapper = new ObjectMapper();

   @Test
   void bothGraphicalMethodsRoundTripThroughRealLauncherWithExactWireShapes( @TempDir final Path directory ) throws Exception {
      final Path source = Files.writeString( directory.resolve( "Transport.ttl" ), model() );
      final String uri = source.toUri().toString();
      final TurtleLanguageServer server = new TurtleLanguageServer();
      ( (TurtleTextDocumentService) server.getTextDocumentService() ).didOpen(
            new DidOpenTextDocumentParams( new TextDocumentItem( uri, "turtle", 1, model() ) ) );

      try ( final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open() ) {
         listener.bind( new InetSocketAddress( "localhost", 0 ) );
         final java.util.concurrent.Future<AsynchronousSocketChannel> accepted = listener.accept();
         try ( final Socket client = new Socket() ) {
            client.connect( (InetSocketAddress) listener.getLocalAddress() );
            client.setSoTimeout( 30_000 );
            final CompletableFuture<Void> handler = CompletableFuture.runAsync(
                  () -> {
                     try {
                        TurtleLanguageServer.handleClientConnection( accepted.get( 2, TimeUnit.SECONDS ), server );
                     } catch ( final Exception exception ) {
                        throw new IllegalStateException( exception );
                     }
                  } );

            final String renderRequest = """
                  {"jsonrpc":"2.0","id":1,"method":"turtle/graphicalView/render","params":{"uri":"%s"}}
                  """.formatted( uri ).strip();
            assertThat( fieldNames( mapper.readTree( renderRequest ) ) )
                  .containsExactlyInAnyOrder( "jsonrpc", "id", "method", "params" );
            assertThat( fieldNames( mapper.readTree( renderRequest ).get( "params" ) ) ).containsExactly( "uri" );
            writeMessage( client.getOutputStream(), renderRequest );
            final JsonNode renderResponse = readResponse( client.getInputStream(), 1 );
            assertThat( fieldNames( renderResponse ) ).containsExactlyInAnyOrder( "jsonrpc", "id", "result" );
            assertThat( fieldNames( renderResponse.get( "result" ) ) )
                  .containsExactlyInAnyOrder( "uri", "svg", "targets", "warnings" );
            assertThat( renderResponse.at( "/result/uri" ).asText() ).isEqualTo( uri );
            assertThat( renderResponse.at( "/result/svg" ).asText() ).contains( "<svg" );
            assertThat( renderResponse.at( "/result/warnings" ).isArray() ).isTrue();
            assertThat( renderResponse.at( "/result/warnings" ).isEmpty() ).isTrue();
            assertThat( renderResponse.at( "/result/targets/0" ).properties().stream().map( java.util.Map.Entry::getKey ) )
                  .containsExactlyInAnyOrder( "id", "kind", "elementUrn" );

            final String resolveRequest = """
                  {"jsonrpc":"2.0","id":2,"method":"turtle/graphicalView/resolveTarget","params":{"sourceUri":"%s","elementUrn":"urn:samm:example.transport:1.0.0#Transport"}}
                  """.formatted( uri ).strip();
            assertThat( fieldNames( mapper.readTree( resolveRequest ).get( "params" ) ) )
                  .containsExactlyInAnyOrder( "sourceUri", "elementUrn" );
            writeMessage( client.getOutputStream(), resolveRequest );
            final JsonNode resolveResponse = readResponse( client.getInputStream(), 2 );
            assertThat( fieldNames( resolveResponse ) ).containsExactlyInAnyOrder( "jsonrpc", "id", "result" );
            assertThat( fieldNames( resolveResponse.get( "result" ) ) ).containsExactly( "location" );
            assertThat( resolveResponse.at( "/result/location/uri" ).asText() ).isEqualTo( uri );
            assertThat( fieldNames( resolveResponse.at( "/result/location/range" ) ) )
                  .containsExactlyInAnyOrder( "start", "end" );
            assertThat( resolveResponse.get( "result" ).has( "warning" ) ).isFalse();

            client.close();
            handler.get( 5, TimeUnit.SECONDS );
         }
      }
   }

   private static Set<String> fieldNames( final JsonNode node ) {
      return node.properties().stream().map( java.util.Map.Entry::getKey )
            .collect( java.util.stream.Collectors.toCollection( java.util.LinkedHashSet::new ) );
   }

   private JsonNode readResponse( final InputStream input, final int id ) throws Exception {
      while ( true ) {
         final JsonNode message = mapper.readTree( readMessage( input ) );
         if ( message.has( "id" ) && message.get( "id" ).asInt() == id ) return message;
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
      if ( contentLength < 0 ) throw new EOFException( "Missing Content-Length" );
      return new String( input.readNBytes( contentLength ), StandardCharsets.UTF_8 );
   }

   private static String readHeaderLine( final InputStream input ) throws Exception {
      final StringBuilder line = new StringBuilder();
      for ( int value; ( value = input.read() ) >= 0; ) {
         if ( value == '\n' ) return line.toString();
         if ( value != '\r' ) line.append( (char) value );
      }
      throw new EOFException();
   }

   private static String model() {
      return """
            @prefix : <urn:samm:example.transport:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

            :Transport a samm:Aspect ;
               samm:properties () ;
               samm:operations () .
            """;
   }
}
