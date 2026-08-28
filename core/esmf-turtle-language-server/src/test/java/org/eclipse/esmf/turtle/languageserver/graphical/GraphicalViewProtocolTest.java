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
package org.eclipse.esmf.turtle.languageserver.graphical;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import org.eclipse.esmf.aspectmodel.generator.diagram.DiagramGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.diagram.DiagramGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.turtle.languageserver.TurtleLanguageServer;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewAttributeTarget;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderParams;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderResult;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewResolveTargetParams;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewResolveTargetResult;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewResolveTargetWarning;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewTarget;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TurtleTextDocumentService;

import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.ObjectMapper;

class GraphicalViewProtocolTest {
   private final ObjectMapper mapper = new ObjectMapper();

   @Test
   void recordsRoundTripAndFacadeUsesExactMethodNames() throws Exception {
      final GraphicalViewRenderParams render = mapper.readValue( "{\"uri\":\"file:///model.ttl\"}", GraphicalViewRenderParams.class );
      final GraphicalViewResolveTargetParams resolve = mapper.readValue( "{\"sourceUri\":\"file:///model.ttl\",\"elementUrn\":\"urn:samm:example:1.0.0#Thing\"}", GraphicalViewResolveTargetParams.class );
      assertThat( render.uri() ).isEqualTo( "file:///model.ttl" ); assertThat( resolve.elementUrn() ).contains( "Thing" );
      assertThat( mapper.writeValueAsString( new GraphicalViewRenderResult( render.uri(), "<svg/>", List.of(), List.of() ) ) ).doesNotContain( "renderId", "version", "sourceText" );
      final Method renderMethod = TurtleLanguageServer.class.getMethod( "renderGraphicalView", GraphicalViewRenderParams.class );
      final Method resolveMethod = TurtleLanguageServer.class.getMethod( "resolveGraphicalViewTarget", GraphicalViewResolveTargetParams.class );
      assertThat( renderMethod.getAnnotation( JsonRequest.class ).value() ).isEqualTo( "turtle/graphicalView/render" );
      assertThat( resolveMethod.getAnnotation( JsonRequest.class ).value() ).isEqualTo( "turtle/graphicalView/resolveTarget" );
   }

   @Test
   void facadeRendersUnsavedMainSnapshotAndResolvesCurrentCoordinates( @TempDir final Path directory ) throws Exception {
      final Path file = directory.resolve( "Model.ttl" ); Files.writeString( file, model( "Persisted" ) ); final String uri = file.toUri().toString();
      final TurtleLanguageServer server = new TurtleLanguageServer();
      ( (TurtleTextDocumentService) server.getTextDocumentService() ).didOpen( new DidOpenTextDocumentParams( new TextDocumentItem( uri, "turtle", 1, model( "Unsaved" ) ) ) );
      final GraphicalViewRenderResult rendered = server.renderGraphicalView( new GraphicalViewRenderParams( uri ) ).get( 30, TimeUnit.SECONDS );
      assertThat( rendered.warnings() ).isEmpty();
      assertThat( rendered.svg() ).contains( "Unsaved", "preferredName&#160;[de]:&#160;Deutsche&#160;Bezeichnung",
            "preferredName&#160;[en]:&#160;English&#160;label" ).doesNotContain( "Persisted" );
      final GraphicalViewResolveTargetResult resolved = server.resolveGraphicalViewTarget( new GraphicalViewResolveTargetParams( uri, "urn:samm:example.graphical:1.0.0#Unsaved" ) ).get( 10, TimeUnit.SECONDS );
      assertThat( resolved.warning() ).isNull(); assertThat( resolved.location().getUri() ).isEqualTo( uri ); assertThat( resolved.location().getRange().getStart().getLine() ).isEqualTo( 3 ); server.shutdown();
   }

   @Test
   void closingRenderedSourceKeepsPersistedSameAndCrossFileNavigationAvailable( @TempDir final Path directory )
         throws Exception {
      final String sourceModel = """
            @prefix : <urn:samm:example.closed.source:1.0.0#> .
            @prefix target: <urn:samm:example.closed.target:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            :ClosedAspect a samm:Aspect ; samm:properties (); samm:operations () .
            """;
      final Path source = Files.writeString( directory.resolve( "ClosedAspect.ttl" ), sourceModel );
      final Path target = Files.writeString( directory.resolve( "CrossTarget.ttl" ), """
            @prefix : <urn:samm:example.closed.target:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            :CrossTarget a samm:Characteristic ; samm:dataType xsd:string .
            """ );
      final String sourceUri = source.toUri().toString();
      final TurtleLanguageServer server = new TurtleLanguageServer();
      final TurtleTextDocumentService documents = (TurtleTextDocumentService) server.getTextDocumentService();
      documents.didOpen( new DidOpenTextDocumentParams( new TextDocumentItem( sourceUri, "turtle", 1, sourceModel ) ) );
      assertThat( server.renderGraphicalView( new GraphicalViewRenderParams( sourceUri ) ).get( 30, TimeUnit.SECONDS ).warnings() )
            .isEmpty();

      documents.didClose( new DidCloseTextDocumentParams( new TextDocumentIdentifier( sourceUri ) ) );

      final GraphicalViewResolveTargetResult sameFile = server.resolveGraphicalViewTarget(
            new GraphicalViewResolveTargetParams( sourceUri,
                  "urn:samm:example.closed.source:1.0.0#ClosedAspect" ) ).get( 10, TimeUnit.SECONDS );
      assertThat( sameFile.warning() ).isNull();
      assertThat( sameFile.location().getUri() ).isEqualTo( sourceUri );
      final GraphicalViewResolveTargetResult crossFile = server.resolveGraphicalViewTarget(
            new GraphicalViewResolveTargetParams( sourceUri,
                  "urn:samm:example.closed.target:1.0.0#CrossTarget" ) ).get( 10, TimeUnit.SECONDS );
      assertThat( crossFile.warning() ).isNull();
      assertThat( crossFile.location().getUri() ).isEqualTo( target.toUri().toString() );
      server.shutdown();
   }

   @Test
   void untrustedClosedSourceUriIsNotLoadedFromFilesystem( @TempDir final Path directory ) throws Exception {
      final Path untrusted = Files.writeString( directory.resolve( "Untrusted.ttl" ), """
            @prefix : <urn:samm:example.untrusted:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            :WouldResolveIfRead a samm:Aspect ; samm:properties (); samm:operations () .
            """ );
      final TurtleLanguageServer server = new TurtleLanguageServer();

      final GraphicalViewResolveTargetResult result = server.resolveGraphicalViewTarget(
            new GraphicalViewResolveTargetParams( untrusted.toUri().toString(),
                  "urn:samm:example.untrusted:1.0.0#WouldResolveIfRead" ) ).get( 10, TimeUnit.SECONDS );

      assertThat( result.location() ).isNull();
      assertThat( result.warning() ).isEqualTo( GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      server.shutdown();
   }

   @Test
   void lspRenderReturnsOnlyNamedAndEligibleAnonymousElementHeaderSidecars( @TempDir final Path directory ) throws Exception {
      final Path file = Files.writeString( directory.resolve( "Navigation.ttl" ), navigationModel() );
      final String uri = file.toUri().toString();
      final TurtleLanguageServer server = new TurtleLanguageServer();
      ( (TurtleTextDocumentService) server.getTextDocumentService() ).didOpen(
            new DidOpenTextDocumentParams( new TextDocumentItem( uri, "turtle", 1, navigationModel() ) ) );

      final GraphicalViewRenderResult rendered = server.renderGraphicalView( new GraphicalViewRenderParams( uri ) )
            .get( 30, TimeUnit.SECONDS );

      assertThat( rendered.warnings() ).isEmpty();
      assertThat( rendered.targets() ).allMatch( target -> target.kind().equals( GraphicalViewTarget.ELEMENT_HEADER ) );
      assertThat( rendered.targets().stream().map( GraphicalViewTarget.class::cast ) ).extracting( GraphicalViewTarget::elementUrn )
            .contains( "urn:samm:example.sidecar:1.0.0#SidecarAspect",
                  "urn:samm:example.sidecar:1.0.0#NamedProperty",
                  "urn:samm:example.sidecar:1.0.0#NamedEntity" );
      assertThat( rendered.svg() ).contains( "«SingleEntity»", "«Either»" );
      assertThat( rendered.targets().stream().map( GraphicalViewTarget.class::cast ) )
            .noneMatch( target -> target.elementUrn().contains( "Either" ) );

      final GraphicalViewRenderResult explicitFalse = server.renderGraphicalView( new GraphicalViewRenderParams( uri, false ) )
            .get( 30, TimeUnit.SECONDS );
      assertThat( explicitFalse.targets() ).allMatch( GraphicalViewTarget.class::isInstance );
      assertThat( explicitFalse.svg() ).doesNotContain( "gv-attribute-" );
      assertThat( explicitFalse.svg() ).contains( "preferredName&#160;[en]:&#160;Sidecar&#160;Aspect" );

      final GraphicalViewRenderResult withRows = server.renderGraphicalView( new GraphicalViewRenderParams( uri, true ) )
            .get( 30, TimeUnit.SECONDS );
      assertThat( withRows.targets() ).anyMatch( target -> target.kind().equals( "attributeRow" ) );
      assertThat( withRows.svg() ).contains( "gv-attribute-" );
      server.shutdown();
   }

   @Test
   void lspRenderReturnsAllLocalizedRowsWithQualifiedSidecarsAndBoundedDisplayValues( @TempDir final Path directory )
         throws Exception {
      final String longDescription = "x".repeat( 257 );
      final String model = """
            @prefix : <urn:samm:example.multilingual:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            :MultilingualAspect a samm:Aspect ;
               samm:preferredName "English name"@en ;
               samm:preferredName "Deutscher Name"@de ;
               samm:description "%s"@en ;
               samm:description "Deutsche Beschreibung"@de ;
               samm:properties () ;
               samm:operations () .
            """.formatted( longDescription );
      final Path file = Files.writeString( directory.resolve( "Multilingual.ttl" ), model );
      final String uri = file.toUri().toString();
      final TurtleLanguageServer server = new TurtleLanguageServer();
      ( (TurtleTextDocumentService) server.getTextDocumentService() ).didOpen(
            new DidOpenTextDocumentParams( new TextDocumentItem( uri, "turtle", 1, model ) ) );

      final GraphicalViewRenderResult rendered = server.renderGraphicalView( new GraphicalViewRenderParams( uri, true ) )
            .get( 30, TimeUnit.SECONDS );

      assertThat( rendered.warnings() ).isEmpty();
      assertThat( rendered.svg() ).contains(
            "preferredName&#160;[de]:&#160;Deutscher&#160;Name",
            "preferredName&#160;[en]:&#160;English&#160;name",
            "description&#160;[de]:&#160;Deutsche&#160;Beschreibung",
            "x".repeat( 253 ) + "..." ).doesNotContain( longDescription );
      final List<GraphicalViewAttributeTarget> localizedTargets = rendered.targets().stream()
            .filter( GraphicalViewAttributeTarget.class::isInstance ).map( GraphicalViewAttributeTarget.class::cast )
            .filter( target -> target.predicateUrn().endsWith( "#preferredName" ) || target.predicateUrn().endsWith( "#description" ) )
            .toList();
      assertThat( localizedTargets.stream()
            .map( target -> target.predicateUrn().substring( target.predicateUrn().lastIndexOf( '#' ) + 1 ) + ":" + target.language() )
            .collect( java.util.stream.Collectors.toSet() ) )
            .containsExactlyInAnyOrder( "preferredName:de", "preferredName:en", "description:de", "description:en" );
      assertThat( localizedTargets ).allMatch( target -> target.selection().equals( "singleOccurrence" )
            && target.ownerUrn().equals( "urn:samm:example.multilingual:1.0.0#MultilingualAspect" ) );
      assertThat( localizedTargets ).extracting( GraphicalViewAttributeTarget::id ).doesNotHaveDuplicates();
      assertThat( localizedTargets ).allSatisfy( target -> assertThat( rendered.svg() ).contains( "id=\"" + target.id() + "\"" ) );
      assertThat( localizedTargets.stream().filter( target -> target.predicateUrn().endsWith( "#preferredName" ) ) )
            .hasSize( 2 );
      assertThat( localizedTargets.stream().filter( target -> target.predicateUrn().endsWith( "#description" )
            && "de".equals( target.language() ) ) ).hasSize( 1 );
      final List<GraphicalViewAttributeTarget> wrappedEnglishDescription = localizedTargets.stream()
            .filter( target -> target.predicateUrn().endsWith( "#description" ) && "en".equals( target.language() ) )
            .toList();
      assertThat( wrappedEnglishDescription ).hasSizeGreaterThan( 1 ).allSatisfy( target -> {
         assertThat( target.ownerUrn() ).isEqualTo( "urn:samm:example.multilingual:1.0.0#MultilingualAspect" );
         assertThat( target.selection() ).isEqualTo( "singleOccurrence" );
         assertThat( target.language() ).isEqualTo( "en" );
      } );

      server.shutdown();
   }

   @Test
   void defaultGeneratorPathContainsNoLspNavigationMetadata() {
      final var aspect = new AspectModelLoader().load( model( "DefaultOnly" ), java.net.URI.create( "file:///default.ttl" ) ).aspect();
      final byte[] svg = new AspectModelDiagramGenerator( aspect, DiagramGenerationConfigBuilder.builder().format( DiagramGenerationConfig.Format.SVG ).build() ).getContent();
      assertThat( new String( svg, java.nio.charset.StandardCharsets.UTF_8 ) ).doesNotContain( "gv-header-", "elementHeader", "sourceUri", "elementUrn" );
   }

   private static String model( final String name ) { return """
      @prefix : <urn:samm:example.graphical:1.0.0#> .
      @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

      :%s a samm:Aspect ;
         samm:preferredName "English label"@en, "Deutsche Bezeichnung"@de ;
         samm:properties () ;
         samm:operations () .
      """.formatted( name ); }

   private static String navigationModel() { return """
      @prefix : <urn:samm:example.sidecar:1.0.0#> .
      @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
      @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .

      :SidecarAspect a samm:Aspect ;
         samm:preferredName "Sidecar Aspect"@en ;
         samm:properties ( :NamedProperty :AnonymousProperty :AmbiguousProperty ) ;
         samm:operations () .
      :NamedProperty a samm:Property ; samm:characteristic samm-c:Text .
      :AnonymousProperty a samm:Property ; samm:characteristic [
         a samm-c:SingleEntity ; samm:dataType :NamedEntity ] .
      :AmbiguousProperty a samm:Property ; samm:characteristic [
         a samm-c:Either ; samm-c:left samm-c:Text ; samm-c:right samm-c:Text ] .
      :NamedEntity a samm:Entity ; samm:properties () .
      """; }
}
