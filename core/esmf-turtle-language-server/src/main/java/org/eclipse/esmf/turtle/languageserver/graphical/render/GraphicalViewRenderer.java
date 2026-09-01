/* Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0 */
package org.eclipse.esmf.turtle.languageserver.graphical.render;

import static org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderWarning.MODEL_TOO_LARGE;
import static org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderWarning.TEMPORARILY_UNRESOLVABLE;

import java.util.List;
import java.util.Locale;

import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator.DiagramBoxLimitExceededException;
import org.eclipse.esmf.aspectmodel.generator.diagram.DiagramGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.diagram.DiagramGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.generator.diagram.DiagramNavigationResult;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewAttributeTarget;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderResult;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderTarget;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewTarget;
import org.eclipse.esmf.turtle.languageserver.graphical.source.GraphicalViewSourceSnapshot;
import org.eclipse.esmf.turtle.languageserver.graphical.validation.GraphicalViewSvgSidecarValidator;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

/** Renders one immutable source snapshot and constructs its Graphical View sidecar. */
public final class GraphicalViewRenderer {
   public static final int MAX_BOXES = 1_000;

   private final ResolutionStrategyService strategies;
   private final GraphicalViewSvgSidecarValidator validator;

   public GraphicalViewRenderer( final ResolutionStrategyService strategies,
         final GraphicalViewSvgSidecarValidator validator ) {
      this.strategies = strategies;
      this.validator = validator;
   }

   public GraphicalViewRenderResult render( final GraphicalViewSourceSnapshot snapshot, final String requestedUri,
         final boolean includeAttributeRows ) {
      try {
         final ParsedDocument parsed = new TreeSitterTurtleParserService().apply( snapshot.document() );
         final var aspect = new AspectModelLoader( strategies.buildResolutionStrategyForDocument( parsed ) )
               .load( AspectModelFileLoader.load( parsed.turtleSyntaxTree(), parsed.getUri() ) ).aspect();
         final AspectModelDiagramGenerator generator = new AspectModelDiagramGenerator( aspect, svgConfig() );
         return successfulRender( requestedUri,
               generator.generateSvgWithNavigationMetadata( MAX_BOXES, includeAttributeRows ) );
      } catch ( final DiagramBoxLimitExceededException exception ) {
         return GraphicalViewRenderResult.warning( requestedUri, MODEL_TOO_LARGE );
      } catch ( final RuntimeException exception ) {
         return GraphicalViewRenderResult.warning( requestedUri, TEMPORARILY_UNRESOLVABLE );
      }
   }

   private GraphicalViewRenderResult successfulRender( final String uri, final DiagramNavigationResult diagram ) {
      final List<GraphicalViewRenderTarget> targets = java.util.stream.Stream.concat(
            diagram.navigationTargets().entrySet().stream()
                  .map( entry -> (GraphicalViewRenderTarget) new GraphicalViewTarget( entry.getKey(),
                        GraphicalViewTarget.ELEMENT_HEADER, entry.getValue() ) ),
            diagram.attributeNavigationTargets().stream()
                  .map( target -> (GraphicalViewRenderTarget) new GraphicalViewAttributeTarget( target.id(),
                        GraphicalViewAttributeTarget.ATTRIBUTE_ROW, target.ownerUrn(), target.predicateUrn(), target.selection(),
                        target.language() ) ) ).toList();
      return validator.isValid( diagram.svg(), targets )
            ? new GraphicalViewRenderResult( uri, diagram.svg(), targets, List.of() )
            : GraphicalViewRenderResult.warning( uri, TEMPORARILY_UNRESOLVABLE );
   }

   private static DiagramGenerationConfig svgConfig() {
      return DiagramGenerationConfigBuilder.builder().format( DiagramGenerationConfig.Format.SVG )
            .language( Locale.ENGLISH ).build();
   }
}
