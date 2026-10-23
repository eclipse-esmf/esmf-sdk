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

import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

final class JsonRpcTransportTestSupport {
   private JsonRpcTransportTestSupport() {}

   static void withConnection( final TurtleLanguageServer server, final TransportAssertions assertions ) throws Exception {
      try ( final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open() ) {
         listener.bind( new InetSocketAddress( "localhost", 0 ) );
         final Future<AsynchronousSocketChannel> accepted = listener.accept();
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
            try {
               assertions.verify( client );
            } finally {
               client.close();
               handler.get( 5, TimeUnit.SECONDS );
            }
         }
      }
   }

   static JsonNode request( final Socket client, final int id, final String method, final String params ) throws Exception {
      final String request = "{\"jsonrpc\":\"2.0\",\"id\":" + id + ",\"method\":\"" + method
            + "\",\"params\":" + params + "}";
      writeMessage( client.getOutputStream(), request );
      final ObjectMapper mapper = new ObjectMapper();
      while ( true ) {
         final JsonNode message = mapper.readTree( readMessage( client.getInputStream() ) );
         if ( message.has( "id" ) && message.get( "id" ).asInt() == id ) {
            return message;
         }
      }
   }

   static Set<String> fieldNames( final JsonNode node ) {
      return node.properties().stream().map( Map.Entry::getKey )
            .collect( Collectors.toCollection( LinkedHashSet::new ) );
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

   @FunctionalInterface
   interface TransportAssertions {
      void verify( Socket client ) throws Exception;
   }
}
