/* Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0 */
package org.eclipse.esmf.turtle.languageserver.graphical;

import static org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderWarning.MISSING_DOCUMENT;
import static org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderWarning.MODEL_TOO_LARGE;
import static org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderWarning.TEMPORARILY_UNRESOLVABLE;
import static org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderWarning.TIMEOUT;
import static org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderWarning.UNSUPPORTED_URI;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator.DiagramBoxLimitExceededException;
import org.eclipse.esmf.aspectmodel.generator.diagram.DiagramGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.diagram.DiagramGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.generator.diagram.DiagramNavigationResult;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.turtle.languageserver.aspect.navigation.AspectCrossFileDefinitionService;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderParams;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderResult;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewResolveTargetParams;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewResolveTargetResult;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewResolveTargetWarning;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewTarget;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

/** Request-local graphical rendering and live semantic target resolution. */
public class GraphicalViewService implements AutoCloseable {
   static final int MAX_BOXES = 1_000;
   static final long RENDER_TIMEOUT_MILLIS = 30_000;
   private static final Pattern MARKER = Pattern.compile( "gv-header-[a-z0-9]{16,32}" );
   private static final Pattern SVG_ID = Pattern.compile( "\\bid=\\\"([^\\\"]+)\\\"" );
   private final Map<String, Document> documents;
   private final TreeSitterTurtleParserService parser;
   private final ResolutionStrategyService strategies;
   private final AspectCrossFileDefinitionService definitions;
   private final ExecutorService executor;
   private final ScheduledExecutorService timeouts;
   private final long timeoutMillis;
   private final RenderOperation renderOperation;
   private final Set<String> trustedRenderedSourceUris = java.util.concurrent.ConcurrentHashMap.newKeySet();

   public GraphicalViewService( final Map<String, Document> documents, final TreeSitterTurtleParserService parser,
         final ResolutionStrategyService strategies, final AspectCrossFileDefinitionService definitions ) {
      this.documents = documents;
      this.parser = parser;
      this.strategies = strategies;
      this.definitions = definitions;
      executor = Executors.newFixedThreadPool( 2, r -> new Thread( r, "graphical-view-render" ) );
      timeouts = Executors.newSingleThreadScheduledExecutor( r -> new Thread( r, "graphical-view-timeout" ) );
      timeoutMillis = RENDER_TIMEOUT_MILLIS;
      renderOperation = this::renderSnapshot;
   }

   GraphicalViewService( final Map<String, Document> documents, final TreeSitterTurtleParserService parser,
         final ResolutionStrategyService strategies, final AspectCrossFileDefinitionService definitions, final ExecutorService executor,
         final ScheduledExecutorService timeouts, final long timeoutMillis, final RenderOperation renderOperation ) {
      this.documents = documents;
      this.parser = parser;
      this.strategies = strategies;
      this.definitions = definitions;
      this.executor = executor;
      this.timeouts = timeouts;
      this.timeoutMillis = timeoutMillis;
      this.renderOperation = renderOperation;
   }

   public CompletableFuture<GraphicalViewRenderResult> render( final GraphicalViewRenderParams params ) {
      if ( params == null || params.uri() == null || !isFile( params.uri() ) ) {
         return CompletableFuture.completedFuture(
               GraphicalViewRenderResult.warning( params == null ? null : params.uri(), UNSUPPORTED_URI ) );
      }
      final Document open = documents.get( params.uri() );
      if ( open == null ) {
         return CompletableFuture.completedFuture( GraphicalViewRenderResult.warning( params.uri(), MISSING_DOCUMENT ) );
      }
      trustedRenderedSourceUris.add( params.uri() );
      final Document snapshot = new Document( open.uri(), open.content() );
      final CompletableFuture<GraphicalViewRenderResult> result = new CompletableFuture<>();
      final Future<?> worker = executor.submit( () -> {
         try {
            result.complete( renderOperation.render( snapshot, params.uri() ) );
         } catch ( final RuntimeException exception ) {
            result.complete( GraphicalViewRenderResult.warning( params.uri(), TEMPORARILY_UNRESOLVABLE ) );
         }
      } );
      final ScheduledFuture<?> timeout = timeouts.schedule( () -> {
         if ( result.complete( GraphicalViewRenderResult.warning( params.uri(), TIMEOUT ) ) ) {
            worker.cancel( true );
         }
      }, timeoutMillis, TimeUnit.MILLISECONDS );
      result.whenComplete( ( value, error ) -> timeout.cancel( false ) );
      return result;
   }

   private GraphicalViewRenderResult renderSnapshot( final Document snapshot, final String uri ) {
      try {
         final ParsedDocument parsed = new TreeSitterTurtleParserService().apply( snapshot );
         final var aspect = new AspectModelLoader( strategies.buildResolutionStrategyForDocument( parsed ) )
               .load( AspectModelFileLoader.load( parsed.turtleSyntaxTree(), parsed.getUri() ) ).aspect();
         final AspectModelDiagramGenerator generator = new AspectModelDiagramGenerator( aspect, svgConfig() );
         return successfulRender( uri, generator.generateSvgWithNavigationMetadata( MAX_BOXES ) );
      } catch ( final DiagramBoxLimitExceededException exception ) {
         return GraphicalViewRenderResult.warning( uri, MODEL_TOO_LARGE );
      } catch ( final RuntimeException exception ) {
         return GraphicalViewRenderResult.warning( uri, TEMPORARILY_UNRESOLVABLE );
      }
   }

   private static GraphicalViewRenderResult successfulRender( final String uri, final DiagramNavigationResult diagram ) {
      final List<GraphicalViewTarget> targets = diagram.navigationTargets().entrySet().stream()
            .map( e -> new GraphicalViewTarget( e.getKey(), GraphicalViewTarget.ELEMENT_HEADER, e.getValue() ) ).toList();
      return valid( diagram.svg(), targets ) ? new GraphicalViewRenderResult( uri, diagram.svg(), targets, List.of() )
            : GraphicalViewRenderResult.warning( uri, TEMPORARILY_UNRESOLVABLE );
   }

   public CompletableFuture<GraphicalViewResolveTargetResult> resolveTarget( final GraphicalViewResolveTargetParams params ) {
      if ( params == null || params.sourceUri() == null || !isFile( params.sourceUri() ) ) {
         return CompletableFuture.completedFuture(
               GraphicalViewResolveTargetResult.warning( GraphicalViewResolveTargetWarning.UNSUPPORTED_URI ) );
      }
      final var urn = params.elementUrn() == null ?
            java.util.Optional.<AspectModelUrn> empty() :
            AspectModelUrn.from( params.elementUrn() ).toJavaOptional();
      final Document source = sourceContext( params.sourceUri() );
      if ( urn.isEmpty() || source == null ) {
         return CompletableFuture.completedFuture(
               GraphicalViewResolveTargetResult.warning( GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE ) );
      }
      return CompletableFuture.supplyAsync( () -> {
         try {
            final var r = definitions.findDefinition( parser.apply( source ), urn.get() );
            return switch ( r.outcome() ) {
               case FOUND -> isFile( r.location().getUri() ) ?
                     new GraphicalViewResolveTargetResult( r.location(), null ) :
                     GraphicalViewResolveTargetResult.warning( GraphicalViewResolveTargetWarning.UNSUPPORTED_URI );
               case NOT_FOUND -> GraphicalViewResolveTargetResult.warning( GraphicalViewResolveTargetWarning.NOT_FOUND );
               case AMBIGUOUS -> GraphicalViewResolveTargetResult.warning( GraphicalViewResolveTargetWarning.AMBIGUOUS );
               case UNSUPPORTED_URI -> GraphicalViewResolveTargetResult.warning( GraphicalViewResolveTargetWarning.UNSUPPORTED_URI );
               case TEMPORARILY_UNRESOLVABLE ->
                     GraphicalViewResolveTargetResult.warning( GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
            };
         } catch ( final RuntimeException exception ) {
            return GraphicalViewResolveTargetResult.warning( GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
         }
      }, executor );
   }

   private Document sourceContext( final String sourceUri ) {
      final Document open = documents.get( sourceUri );
      if ( open != null ) {
         return open;
      }
      if ( !trustedRenderedSourceUris.contains( sourceUri ) ) {
         return null;
      }
      try {
         final URI uri = URI.create( sourceUri );
         return new Document( uri, Files.readString( Path.of( uri ) ) );
      } catch ( final IOException | IllegalArgumentException exception ) {
         return null;
      }
   }

   private static DiagramGenerationConfig svgConfig() {
      return DiagramGenerationConfigBuilder.builder().format( DiagramGenerationConfig.Format.SVG ).language( Locale.ENGLISH ).build();
   }

   static boolean valid( final String svg, final List<GraphicalViewTarget> targets ) {
      final Set<String> svgMarkers = new HashSet<>();
      final var matcher = SVG_ID.matcher( svg );
      while ( matcher.find() ) {
         final String id = matcher.group( 1 );
         if ( id.startsWith( "gv-header-" ) && !id.matches( "gv-header-[a-z0-9]{16,32}_(polygon|text_0)" ) && (
               !MARKER.matcher( id ).matches() || !svgMarkers.add( id ) ) ) {
            return false;
         }
      }
      final Set<String> sidecarMarkers = new HashSet<>();
      for ( final GraphicalViewTarget target : targets ) {
         if ( !MARKER.matcher( target.id() ).matches() || !GraphicalViewTarget.ELEMENT_HEADER.equals( target.kind() )
               || AspectModelUrn.from( target.elementUrn() ).toJavaOptional().isEmpty() || !sidecarMarkers.add( target.id() ) ) {
            return false;
         }
      }
      return svgMarkers.equals( sidecarMarkers );
   }

   static boolean isFile( final String value ) {
      try {
         final URI uri = URI.create( value );
         if ( !"file".equalsIgnoreCase( uri.getScheme() ) || uri.isOpaque() || uri.getAuthority() != null || uri.getRawPath() == null ) {
            return false;
         }
         final Path path = Path.of( uri );
         return path.isAbsolute() && path.normalize().equals( path );
      } catch ( final IllegalArgumentException e ) {
         return false;
      }
   }

   public boolean executorsShutdown() {
      return executor.isShutdown() && timeouts.isShutdown();
   }

   public boolean executorsTerminated() {
      return executor.isTerminated() && timeouts.isTerminated();
   }

   @Override
   public void close() {
      executor.shutdownNow();
      timeouts.shutdownNow();
   }

   @FunctionalInterface
   interface RenderOperation {
      GraphicalViewRenderResult render( Document snapshot, String uri );
   }
}
