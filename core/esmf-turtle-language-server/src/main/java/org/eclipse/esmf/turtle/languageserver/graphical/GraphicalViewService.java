/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.turtle.languageserver.graphical;

import static org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderWarning.MISSING_DOCUMENT;
import static org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderWarning.TEMPORARILY_UNRESOLVABLE;
import static org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderWarning.TIMEOUT;
import static org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderWarning.UNSUPPORTED_URI;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.turtle.languageserver.graphical.navigation.GraphicalViewTargetResolver;
import org.eclipse.esmf.turtle.languageserver.graphical.navigation.GraphicalViewTargetResolver.Resolution;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewAttributeSelection;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderParams;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderResult;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewResolveAttributeTargetParams;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewResolveAttributeTargetResult;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewResolveTargetParams;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewResolveTargetResult;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewResolveTargetWarning;
import org.eclipse.esmf.turtle.languageserver.graphical.render.GraphicalViewRenderer;
import org.eclipse.esmf.turtle.languageserver.graphical.source.GraphicalViewSourceContext;
import org.eclipse.esmf.turtle.languageserver.graphical.source.GraphicalViewSourceSnapshot;
import org.eclipse.esmf.turtle.languageserver.graphical.validation.GraphicalViewLocationValidator;
import org.eclipse.esmf.turtle.languageserver.graphical.validation.GraphicalViewSvgSidecarValidator;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

/** Small asynchronous facade for Graphical View render and navigation requests. */
public final class GraphicalViewService implements AutoCloseable {
   static final long RENDER_TIMEOUT_MILLIS = 30_000;
   private static final Pattern LANGUAGE = Pattern.compile( "[a-z]{2,8}(?:-[a-z0-9]{1,8})*" );

   private final GraphicalViewSourceContext sources;
   private final GraphicalViewRenderer renderer;
   private final GraphicalViewTargetResolver resolver;
   private final GraphicalViewLocationValidator locationValidator;
   private final ExecutorService executor;
   private final ScheduledExecutorService timeouts;

   public GraphicalViewService( final Map<String, Document> documents, final TreeSitterTurtleParserService parser,
         final ResolutionStrategyService strategies ) {
      sources = new GraphicalViewSourceContext( documents );
      renderer = new GraphicalViewRenderer( strategies, new GraphicalViewSvgSidecarValidator() );
      resolver = new GraphicalViewTargetResolver( parser, strategies, sources );
      locationValidator = new GraphicalViewLocationValidator();
      executor = Executors.newFixedThreadPool( 2, runnable -> new Thread( runnable, "graphical-view-worker" ) );
      timeouts = Executors.newSingleThreadScheduledExecutor( runnable -> new Thread( runnable, "graphical-view-timeout" ) );
   }

   public CompletableFuture<GraphicalViewRenderResult> render( final GraphicalViewRenderParams params ) {
      if ( params == null || params.uri() == null || !GraphicalViewSourceContext.isLocalFileUri( params.uri() ) ) {
         return CompletableFuture.completedFuture(
               GraphicalViewRenderResult.warning( params == null ? null : params.uri(), UNSUPPORTED_URI ) );
      }
      final Optional<GraphicalViewSourceSnapshot> snapshot = sources.openSnapshot( params.uri() );
      if ( snapshot.isEmpty() ) {
         return CompletableFuture.completedFuture( GraphicalViewRenderResult.warning( params.uri(), MISSING_DOCUMENT ) );
      }

      final CompletableFuture<GraphicalViewRenderResult> result = new CompletableFuture<>();
      final Future<?> worker = executor.submit( () -> {
         final GraphicalViewRenderResult rendered;
         try {
            rendered = renderer.render( snapshot.get(), params.uri(), params.attributeRowsRequested() );
         } catch ( final RuntimeException exception ) {
            result.complete( GraphicalViewRenderResult.warning( params.uri(), TEMPORARILY_UNRESOLVABLE ) );
            return;
         }
         synchronized ( result ) {
            if ( !result.isDone() ) {
               if ( rendered.svg() != null && rendered.warnings().isEmpty() ) {
                  sources.trustAfterValidatedRender( params.uri() );
               }
               result.complete( rendered );
            }
         }
      } );
      final ScheduledFuture<?> timeout = timeouts.schedule( () -> {
         synchronized ( result ) {
            if ( result.complete( GraphicalViewRenderResult.warning( params.uri(), TIMEOUT ) ) ) {
               worker.cancel( true );
            }
         }
      }, RENDER_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS );
      result.whenComplete( ( value, error ) -> timeout.cancel( false ) );
      return result;
   }

   public CompletableFuture<GraphicalViewResolveTargetResult> resolveTarget( final GraphicalViewResolveTargetParams params ) {
      if ( params == null || params.sourceUri() == null || !GraphicalViewSourceContext.isLocalFileUri( params.sourceUri() ) ) {
         return CompletableFuture.completedFuture(
               GraphicalViewResolveTargetResult.warning( GraphicalViewResolveTargetWarning.UNSUPPORTED_URI ) );
      }
      final Optional<AspectModelUrn> urn = params.elementUrn() == null ? Optional.empty()
            : AspectModelUrn.from( params.elementUrn() ).toJavaOptional();
      final Optional<GraphicalViewSourceSnapshot> snapshot = sources.requestSnapshot( params.sourceUri() );
      if ( urn.isEmpty() || snapshot.isEmpty() ) {
         return CompletableFuture.completedFuture(
               GraphicalViewResolveTargetResult.warning( GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE ) );
      }
      return CompletableFuture.supplyAsync( () -> headerResult( resolver.resolveHeader( snapshot.get(), urn.get() ) ), executor )
            .exceptionally( error -> GraphicalViewResolveTargetResult.warning(
                  GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE ) );
   }

   public CompletableFuture<GraphicalViewResolveAttributeTargetResult> resolveAttributeTarget(
         final GraphicalViewResolveAttributeTargetParams params ) {
      if ( params == null || params.sourceUri() == null || !GraphicalViewSourceContext.isLocalFileUri( params.sourceUri() ) ) {
         return CompletableFuture.completedFuture(
               GraphicalViewResolveAttributeTargetResult.warning( GraphicalViewResolveTargetWarning.UNSUPPORTED_URI ) );
      }
      final Optional<AspectModelUrn> owner = params.ownerUrn() == null ? Optional.empty()
            : AspectModelUrn.from( params.ownerUrn() ).toJavaOptional();
      final Optional<AspectModelUrn> predicate = params.predicateUrn() == null ? Optional.empty()
            : AspectModelUrn.from( params.predicateUrn() ).toJavaOptional();
      final Optional<GraphicalViewAttributeSelection> selection = GraphicalViewAttributeSelection.fromWireValue( params.selection() );
      final boolean validLanguage = params.language() == null || ( LANGUAGE.matcher( params.language() ).matches()
            && selection.filter( GraphicalViewAttributeSelection.SINGLE_OCCURRENCE::equals ).isPresent() );
      final Optional<GraphicalViewSourceSnapshot> snapshot = sources.requestSnapshot( params.sourceUri() );
      if ( owner.isEmpty() || predicate.isEmpty() || selection.isEmpty() || !validLanguage || snapshot.isEmpty() ) {
         return CompletableFuture.completedFuture( GraphicalViewResolveAttributeTargetResult.warning(
               GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE ) );
      }
      return CompletableFuture.supplyAsync( () -> attributeResult( resolver.resolveAttribute( snapshot.get(), owner.get(),
            predicate.get().toString(), params.language() ) ), executor )
            .exceptionally( error -> GraphicalViewResolveAttributeTargetResult.warning(
                  GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE ) );
   }

   private GraphicalViewResolveTargetResult headerResult( final Resolution resolution ) {
      if ( resolution.outcome() != GraphicalViewTargetResolver.Outcome.FOUND ) {
         return GraphicalViewResolveTargetResult.warning( warningFor( resolution ) );
      }
      final GraphicalViewLocationValidator.Result validated = locationValidator.validate( resolution.location() );
      return validated.warning() == null ? new GraphicalViewResolveTargetResult( validated.location(), null )
            : GraphicalViewResolveTargetResult.warning( validated.warning() );
   }

   private GraphicalViewResolveAttributeTargetResult attributeResult( final Resolution resolution ) {
      if ( resolution.outcome() != GraphicalViewTargetResolver.Outcome.FOUND ) {
         return GraphicalViewResolveAttributeTargetResult.warning( warningFor( resolution ) );
      }
      final GraphicalViewLocationValidator.Result validated = locationValidator.validate( resolution.location() );
      return validated.warning() == null ? new GraphicalViewResolveAttributeTargetResult( validated.location(), null )
            : GraphicalViewResolveAttributeTargetResult.warning( validated.warning() );
   }

   private static GraphicalViewResolveTargetWarning warningFor( final Resolution resolution ) {
      return switch ( resolution.outcome() ) {
         case NOT_FOUND -> GraphicalViewResolveTargetWarning.NOT_FOUND;
         case AMBIGUOUS -> GraphicalViewResolveTargetWarning.AMBIGUOUS;
         case UNSUPPORTED_URI -> GraphicalViewResolveTargetWarning.UNSUPPORTED_URI;
         case TEMPORARILY_UNRESOLVABLE, FOUND -> GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE;
      };
   }

   @Override
   public void close() {
      executor.shutdownNow();
      timeouts.shutdownNow();
   }
}
