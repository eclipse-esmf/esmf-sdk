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
import java.net.URI;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
      final Path ambiguousSource = Files.writeString( directory.resolve( "AmbiguousTransport.ttl" ), ambiguousModel() );
      final String ambiguousUri = ambiguousSource.toUri().toString();
      final TurtleLanguageServer server = new TurtleLanguageServer();
      final TurtleTextDocumentService textDocuments = (TurtleTextDocumentService) server.getTextDocumentService();
      textDocuments.didOpen( new DidOpenTextDocumentParams( new TextDocumentItem( uri, "turtle", 1, model() ) ) );
      textDocuments.didOpen(
            new DidOpenTextDocumentParams( new TextDocumentItem( ambiguousUri, "turtle", 1, ambiguousModel() ) ) );

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
                  {"jsonrpc":"2.0","id":1,"method":"turtle/graphicalView/render","params":{"uri":"%s","includeAttributeRows":true}}
                  """.formatted( uri ).strip();
            assertThat( fieldNames( mapper.readTree( renderRequest ) ) )
                  .containsExactlyInAnyOrder( "jsonrpc", "id", "method", "params" );
            assertThat( fieldNames( mapper.readTree( renderRequest ).get( "params" ) ) )
                  .containsExactlyInAnyOrder( "uri", "includeAttributeRows" );
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
            final JsonNode attributeTarget = renderResponse.at( "/result/targets" ).valueStream()
                  .filter( target -> "attributeRow".equals( target.get( "kind" ).asText() ) ).findFirst().orElseThrow();
            assertThat( fieldNames( attributeTarget ) )
                  .containsExactlyInAnyOrder( "id", "kind", "ownerUrn", "predicateUrn", "selection", "language" );
            assertThat( attributeTarget.get( "selection" ).asText() ).isEqualTo( "singleOccurrence" );
            assertThat( attributeTarget.get( "language" ).asText() ).isEqualTo( "en" );
            final List<JsonNode> wrappedSeeTargets = renderResponse.at( "/result/targets" ).valueStream()
                  .filter( target -> "attributeRow".equals( target.path( "kind" ).asText() ) )
                  .filter( target -> "urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#see".equals(
                        target.path( "predicateUrn" ).asText() ) )
                  .toList();
            assertThat( wrappedSeeTargets ).hasSize( 2 )
                  .extracting( target -> target.get( "id" ).asText() ).doesNotHaveDuplicates();
            assertThat( wrappedSeeTargets ).allSatisfy( target -> {
               assertThat( target.get( "ownerUrn" ).asText() ).isEqualTo( "urn:samm:example.transport:1.0.0#Transport" );
               assertThat( target.get( "selection" ).asText() ).isEqualTo( "predicateStart" );
            } );

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

            final String resolveAttributeRequest = """
                  {"jsonrpc":"2.0","id":3,"method":"turtle/graphicalView/resolveAttributeTarget","params":{"sourceUri":"%s","ownerUrn":"urn:samm:example.transport:1.0.0#Transport","predicateUrn":"urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#description","selection":"singleOccurrence","language":"en"}}
                  """.formatted( uri ).strip();
            assertThat( fieldNames( mapper.readTree( resolveAttributeRequest ).get( "params" ) ) )
                  .containsExactlyInAnyOrder( "sourceUri", "ownerUrn", "predicateUrn", "selection", "language" );
            writeMessage( client.getOutputStream(), resolveAttributeRequest );
            final JsonNode resolveAttributeResponse = readResponse( client.getInputStream(), 3 );
            assertThat( fieldNames( resolveAttributeResponse.get( "result" ) ) ).containsExactly( "location" );
            assertThat( resolveAttributeResponse.at( "/result/location/uri" ).asText() ).isEqualTo( uri );

            JsonNode sharedSeeLocation = null;
            for ( int index = 0; index < wrappedSeeTargets.size(); index++ ) {
               final int requestId = 4 + index;
               final String resolveWrappedSeeRequest = """
                     {"jsonrpc":"2.0","id":%d,"method":"turtle/graphicalView/resolveAttributeTarget","params":{"sourceUri":"%s","ownerUrn":"urn:samm:example.transport:1.0.0#Transport","predicateUrn":"urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#see","selection":"predicateStart"}}
                     """.formatted( requestId, uri ).strip();
               writeMessage( client.getOutputStream(), resolveWrappedSeeRequest );
               final JsonNode response = readResponse( client.getInputStream(), requestId );
               assertThat( fieldNames( response.get( "result" ) ) ).containsExactly( "location" );
               final JsonNode location = response.at( "/result/location" );
               assertValidSeeLocation( location, uri, model() );
               if ( sharedSeeLocation == null ) {
                  sharedSeeLocation = location;
               } else {
                  assertThat( location ).isEqualTo( sharedSeeLocation );
               }
            }

            final String ambiguousRenderRequest = """
                  {"jsonrpc":"2.0","id":10,"method":"turtle/graphicalView/render","params":{"uri":"%s","includeAttributeRows":true}}
                  """.formatted( ambiguousUri ).strip();
            writeMessage( client.getOutputStream(), ambiguousRenderRequest );
            final JsonNode ambiguousRenderResponse = readResponse( client.getInputStream(), 10 );
            final List<JsonNode> ambiguousSeeTargets = ambiguousRenderResponse.at( "/result/targets" ).valueStream()
                  .filter( target -> "attributeRow".equals( target.path( "kind" ).asText() ) )
                  .filter( target -> "urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#see".equals(
                        target.path( "predicateUrn" ).asText() ) )
                  .toList();
            assertThat( ambiguousSeeTargets ).isNotEmpty();
            for ( int index = 0; index < ambiguousSeeTargets.size(); index++ ) {
               final int requestId = 11 + index;
               final String request = """
                     {"jsonrpc":"2.0","id":%d,"method":"turtle/graphicalView/resolveAttributeTarget","params":{"sourceUri":"%s","ownerUrn":"urn:samm:example.transport:1.0.0#Transport","predicateUrn":"urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#see","selection":"predicateStart"}}
                     """.formatted( requestId, ambiguousUri ).strip();
               writeMessage( client.getOutputStream(), request );
               final JsonNode response = readResponse( client.getInputStream(), requestId );
               assertThat( fieldNames( response.get( "result" ) ) ).containsExactly( "warning" );
               assertThat( response.at( "/result/warning" ).isTextual() ).isTrue();
               assertThat( response.at( "/result/warning" ).asText() ).isEqualTo( "ambiguous" );
            }

            client.close();
            handler.get( 5, TimeUnit.SECONDS );
         }
      }
   }

   private static Set<String> fieldNames( final JsonNode node ) {
      return node.properties().stream().map( java.util.Map.Entry::getKey )
            .collect( java.util.stream.Collectors.toCollection( java.util.LinkedHashSet::new ) );
   }

   private static void assertValidSeeLocation( final JsonNode location, final String expectedUri, final String source ) {
      assertThat( fieldNames( location ) ).containsExactlyInAnyOrder( "uri", "range" );
      assertThat( location.get( "uri" ).isTextual() ).isTrue();
      assertThat( location.get( "uri" ).asText() ).isEqualTo( expectedUri );
      final URI uri = URI.create( location.get( "uri" ).asText() );
      assertThat( uri.isAbsolute() ).isTrue();
      assertThat( uri.getScheme() ).isEqualToIgnoringCase( "file" );
      assertThat( Path.of( uri ).isAbsolute() ).isTrue();

      final JsonNode range = location.get( "range" );
      assertThat( fieldNames( range ) ).containsExactlyInAnyOrder( "start", "end" );
      final int[] start = position( range.get( "start" ) );
      final int[] end = position( range.get( "end" ) );
      assertThat( start ).containsExactly( 5, 3 );
      assertThat( end ).containsExactly( 5, 11 );
      assertThat( start[0] < end[0] || ( start[0] == end[0] && start[1] <= end[1] ) ).isTrue();
      final List<String> lines = source.lines().toList();
      assertThat( start[0] ).isLessThan( lines.size() );
      assertThat( start[1] ).isLessThanOrEqualTo( lines.get( start[0] ).length() );
      assertThat( lines.get( start[0] ).substring( start[1] ) ).startsWith( "samm:see" );
   }

   private static int[] position( final JsonNode position ) {
      assertThat( fieldNames( position ) ).containsExactlyInAnyOrder( "line", "character" );
      assertThat( position.get( "line" ).isIntegralNumber() ).isTrue();
      assertThat( position.get( "character" ).isIntegralNumber() ).isTrue();
      assertThat( position.get( "line" ).canConvertToInt() ).isTrue();
      assertThat( position.get( "character" ).canConvertToInt() ).isTrue();
      final int line = position.get( "line" ).intValue();
      final int character = position.get( "character" ).intValue();
      assertThat( line ).isNotNegative();
      assertThat( character ).isNotNegative();
      return new int[] { line, character };
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
               samm:description "Transport aspect"@en ;
               samm:see <https://example.test/reference/with/a/long/path>, <urn:irdi:0173:1:02:AAO677:003> ;
               samm:properties () ;
               samm:operations () .
            """;
   }

   private static String ambiguousModel() {
      return model().replace(
            "samm:see <https://example.test/reference/with/a/long/path>, <urn:irdi:0173:1:02:AAO677:003> ;",
            "samm:see <https://example.test/reference/with/a/long/path> ;\n"
                  + "   samm:see <urn:irdi:0173:1:02:AAO677:003> ;" );
   }
}
