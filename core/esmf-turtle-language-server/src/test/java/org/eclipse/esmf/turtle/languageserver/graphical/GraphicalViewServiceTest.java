/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.turtle.languageserver.graphical;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderParams;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderWarning;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewResolveAttributeTargetParams;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewResolveTargetParams;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewResolveTargetWarning;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GraphicalViewServiceTest {
   @Test
   void successfulValidatedRenderEnablesClosedPersistedFallback( @TempDir final Path directory ) throws Exception {
      final Path file = Files.writeString( directory.resolve( "Trusted.ttl" ), model() );
      final String uri = file.toUri().toString();
      final Map<String, Document> documents = new ConcurrentHashMap<>();
      documents.put( uri, new Document( uri, model() ) );
      try ( final GraphicalViewService service = service( documents ) ) {
         assertThat( service.render( new GraphicalViewRenderParams( uri, true ) ).get( 30, TimeUnit.SECONDS ).warnings() )
               .isEmpty();

         documents.remove( uri );
         final var resolved = service.resolveTarget( new GraphicalViewResolveTargetParams( uri,
               "urn:samm:example.facade:1.0.0#Facade" ) ).get( 5, TimeUnit.SECONDS );

         assertThat( resolved.warning() ).isNull();
         assertThat( resolved.location().getUri() ).isEqualTo( uri );
      }
   }

   @Test
   void failedRenderDoesNotTrustClosedPersistedFallback( @TempDir final Path directory ) throws Exception {
      final Path file = Files.writeString( directory.resolve( "Untrusted.ttl" ), model() );
      final String uri = file.toUri().toString();
      final Map<String, Document> documents = new ConcurrentHashMap<>();
      documents.put( uri, new Document( uri, "not valid Turtle" ) );
      try ( final GraphicalViewService service = service( documents ) ) {
         assertThat( service.render( new GraphicalViewRenderParams( uri ) ).get( 10, TimeUnit.SECONDS ).warnings() )
               .containsExactly( GraphicalViewRenderWarning.TEMPORARILY_UNRESOLVABLE );

         documents.remove( uri );
         final var resolved = service.resolveTarget( new GraphicalViewResolveTargetParams( uri,
               "urn:samm:example.facade:1.0.0#Facade" ) ).get( 5, TimeUnit.SECONDS );

         assertThat( resolved.location() ).isNull();
         assertThat( resolved.warning() ).isEqualTo( GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      }
   }

   @Test
   void malformedMissingAndNonLocalInputsAreControlled() throws Exception {
      try ( final GraphicalViewService service = service( Map.of() ) ) {
         assertThat( service.render( new GraphicalViewRenderParams( "https://example/model.ttl" ) ).get().warnings() )
               .containsExactly( GraphicalViewRenderWarning.UNSUPPORTED_URI );
         assertThat( service.render( new GraphicalViewRenderParams( "file:///missing.ttl" ) ).get().warnings() )
               .containsExactly( GraphicalViewRenderWarning.MISSING_DOCUMENT );
         assertThat( service.resolveTarget( new GraphicalViewResolveTargetParams( "file:///missing.ttl", "bad" ) )
               .get().warning() ).isEqualTo( GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
         assertThat( service.resolveTarget( new GraphicalViewResolveTargetParams( "https://example/model.ttl",
               "urn:samm:x:1.0.0#x" ) ).get().warning() )
                     .isEqualTo( GraphicalViewResolveTargetWarning.UNSUPPORTED_URI );
      }
   }

   @Test
   void headerResolveParsesTheSnapshotCapturedBeforeConcurrentChange() throws Exception {
      final String uri = "file:///tmp/graphical/header-snapshot.ttl";
      final Document document = new Document( uri, model() );
      final BlockingParser parser = new BlockingParser();
      try ( final GraphicalViewService service = new GraphicalViewService( Map.of( uri, document ), parser,
            new ResolutionStrategyService() ) ) {
         final var future = service.resolveTarget( new GraphicalViewResolveTargetParams( uri,
               "urn:samm:example.facade:1.0.0#Facade" ) );
         assertThat( parser.started.await( 1, TimeUnit.SECONDS ) ).isTrue();
         document.update( null, model().replace( ":Facade", ":Renamed" ) );
         parser.release.countDown();

         assertThat( future.get( 5, TimeUnit.SECONDS ).warning() ).isNull();
      }
   }

   @Test
   void attributeResolveParsesTheSnapshotCapturedBeforeConcurrentChange() throws Exception {
      final String uri = "file:///tmp/graphical/attribute-snapshot.ttl";
      final String source = model().replace( "samm:properties ()", "samm:description \"Current\"@en ; samm:properties ()" );
      final Document document = new Document( uri, source );
      final BlockingParser parser = new BlockingParser();
      try ( final GraphicalViewService service = new GraphicalViewService( Map.of( uri, document ), parser,
            new ResolutionStrategyService() ) ) {
         final var future = service.resolveAttributeTarget( new GraphicalViewResolveAttributeTargetParams( uri,
               "urn:samm:example.facade:1.0.0#Facade",
               "urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#description", "singleOccurrence", "en" ) );
         assertThat( parser.started.await( 1, TimeUnit.SECONDS ) ).isTrue();
         document.update( null, model() );
         parser.release.countDown();

         assertThat( future.get( 5, TimeUnit.SECONDS ).warning() ).isNull();
      }
   }

   @Test
   void closeStopsOwnedWorkerAndTimeoutThreads( @TempDir final Path directory ) throws Exception {
      final Path file = Files.writeString( directory.resolve( "Lifecycle.ttl" ), model() );
      final String uri = file.toUri().toString();
      final GraphicalViewService service = service( Map.of( uri, new Document( uri, model() ) ) );
      service.render( new GraphicalViewRenderParams( uri ) ).get( 30, TimeUnit.SECONDS );
      assertThat( liveGraphicalThreads() ).isNotEmpty();

      service.close();

      final long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos( 2 );
      while ( System.nanoTime() < deadline && !liveGraphicalThreads().isEmpty() ) {
         Thread.sleep( 10 );
      }
      assertThat( liveGraphicalThreads() ).isEmpty();
   }

   private static GraphicalViewService service( final Map<String, Document> documents ) {
      return new GraphicalViewService( documents, new TreeSitterTurtleParserService(), new ResolutionStrategyService() );
   }

   private static java.util.List<String> liveGraphicalThreads() {
      return Thread.getAllStackTraces().keySet().stream().filter( Thread::isAlive ).map( Thread::getName )
            .filter( name -> name.startsWith( "graphical-view-" ) ).toList();
   }

   private static final class BlockingParser extends TreeSitterTurtleParserService {
      private final CountDownLatch started = new CountDownLatch( 1 );
      private final CountDownLatch release = new CountDownLatch( 1 );

      @Override
      public org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument apply( final Document document ) {
         started.countDown();
         try {
            release.await();
         } catch ( final InterruptedException exception ) {
            Thread.currentThread().interrupt();
         }
         return super.apply( document );
      }
   }

   private static String model() {
      return """
         @prefix : <urn:samm:example.facade:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         :Facade a samm:Aspect ; samm:properties (); samm:operations () .
         """;
   }
}
