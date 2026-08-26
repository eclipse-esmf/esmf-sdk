/* Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0 */
package org.eclipse.esmf.turtle.languageserver.graphical;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.esmf.turtle.languageserver.aspect.navigation.AspectCrossFileDefinitionService;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderParams;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderResult;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewResolveTargetParams;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewResolveTargetWarning;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewTarget;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GraphicalViewServiceTest {
   @Test
   void timeoutIsTerminalCancelsWorkerAndSuppressesLateSuccess() throws Exception {
      final String uri = "file:///timeout.ttl";
      final AtomicBoolean interrupted = new AtomicBoolean();
      final GraphicalViewService service = service( Map.of( uri, new Document( uri, "snapshot" ) ), ( snapshot, ignored ) -> {
         try { Thread.sleep( 10_000 ); } catch ( final InterruptedException e ) { interrupted.set( true ); }
         return new GraphicalViewRenderResult( uri, "<svg>late</svg>", java.util.List.of(), java.util.List.of() );
      }, 20 );
      final GraphicalViewRenderResult result = service.render( new GraphicalViewRenderParams( uri ) ).get( 2, TimeUnit.SECONDS );
      assertThat( result.warnings() ).containsExactly( org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderWarning.TIMEOUT ); assertThat( result.svg() ).isNull(); assertThat( result.targets() ).isEmpty();
      Thread.sleep( 50 ); assertThat( interrupted ).isTrue(); service.close();
   }

   @Test
   void productionRenderMapsOneThousandOneBoxesToExplicitModelTooLargeWithoutSvg( @TempDir final java.nio.file.Path directory )
         throws Exception {
      final String model = modelWithBoxes( 1_001 );
      final java.nio.file.Path source = java.nio.file.Files.writeString( directory.resolve( "TooLarge.ttl" ), model );
      final String uri = source.toUri().toString();
      final Map<String, Document> documents = Map.of( uri, new Document( uri, model ) );
      final TreeSitterTurtleParserService parser = new TreeSitterTurtleParserService();
      final ResolutionStrategyService strategies = new ResolutionStrategyService();
      final GraphicalViewService service = new GraphicalViewService( documents, parser, strategies,
            new AspectCrossFileDefinitionService( parser, documents, strategies ) );

      final GraphicalViewRenderResult result = service.render( new GraphicalViewRenderParams( uri ) )
            .get( 30, TimeUnit.SECONDS );

      assertThat( result.warnings() ).containsExactly(
            org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderWarning.MODEL_TOO_LARGE );
      assertThat( result.svg() ).isNull();
      assertThat( result.targets() ).isEmpty();
      service.close();
   }

   @Test
   void outOfOrderRequestsRetainImmutableRequestLocalSnapshots() throws Exception {
      final String uri = "file:///snapshot.ttl";
      final Document open = new Document( uri, "first snapshot" );
      final CountDownLatch firstStarted = new CountDownLatch( 1 );
      final CountDownLatch releaseFirst = new CountDownLatch( 1 );
      final TreeSitterTurtleParserService parser = new TreeSitterTurtleParserService();
      final ResolutionStrategyService strategies = new ResolutionStrategyService();
      final Map<String, Document> documents = Map.of( uri, open );
      final GraphicalViewService service = new GraphicalViewService( documents, parser, strategies,
            new AspectCrossFileDefinitionService( parser, documents, strategies ), Executors.newFixedThreadPool( 2 ),
            Executors.newSingleThreadScheduledExecutor(), 1_000, ( snapshot, ignored ) -> {
               if ( snapshot.content().startsWith( "first" ) ) {
                  firstStarted.countDown();
                  try { releaseFirst.await(); } catch ( final InterruptedException exception ) { Thread.currentThread().interrupt(); }
               }
               return new GraphicalViewRenderResult( uri, "<svg>" + snapshot.content() + "</svg>", java.util.List.of(), java.util.List.of() );
            } );

      final var first = service.render( new GraphicalViewRenderParams( uri ) );
      assertThat( firstStarted.await( 1, TimeUnit.SECONDS ) ).isTrue();
      open.update( null, "second snapshot" );
      final var second = service.render( new GraphicalViewRenderParams( uri ) );
      assertThat( second.get( 1, TimeUnit.SECONDS ).svg() ).contains( "second snapshot" );
      assertThat( first ).isNotDone();
      releaseFirst.countDown();
      assertThat( first.get( 1, TimeUnit.SECONDS ).svg() ).contains( "first snapshot" ).doesNotContain( "second snapshot" );
      service.close();
   }

   @Test
   void malformedMissingAndNonLocalInputsAreControlled() throws Exception {
      final GraphicalViewService service = service( Map.of(), ( snapshot, uri ) -> null, 1_000 );
      assertThat( service.render( new GraphicalViewRenderParams( "https://example/model.ttl" ) ).get().warnings() ).containsExactly( org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderWarning.UNSUPPORTED_URI );
      assertThat( service.render( new GraphicalViewRenderParams( "file:///missing.ttl" ) ).get().warnings() ).containsExactly( org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderWarning.MISSING_DOCUMENT );
      assertThat( service.resolveTarget( new GraphicalViewResolveTargetParams( "file:///missing.ttl", "bad" ) ).get().warning() ).isEqualTo( org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      assertThat( service.resolveTarget( new GraphicalViewResolveTargetParams( "https://example/model.ttl", "urn:samm:x:1.0.0#x" ) ).get().warning() ).isEqualTo( org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewResolveTargetWarning.UNSUPPORTED_URI ); service.close();
   }

   @Test
   void validSourceMapsMalformedUrnInvalidSyntaxNotFoundAndAmbiguousToClosedWarnings( @TempDir final java.nio.file.Path directory )
         throws Exception {
      final String model = """
            @prefix : <urn:samm:example.resolve:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            :Source a samm:Aspect ; samm:properties (); samm:operations () .
            :Duplicate a samm:Characteristic .
            :Duplicate a samm:Characteristic .
            """;
      final java.nio.file.Path source = java.nio.file.Files.writeString( directory.resolve( "Resolve.ttl" ), model );
      final String uri = source.toUri().toString();
      final Map<String, Document> documents = Map.of( uri, new Document( uri, model ) );
      final TreeSitterTurtleParserService parser = new TreeSitterTurtleParserService();
      final ResolutionStrategyService strategies = new ResolutionStrategyService();
      final GraphicalViewService service = new GraphicalViewService( documents, parser, strategies,
            new AspectCrossFileDefinitionService( parser, documents, strategies ) );

      assertThat( service.resolveTarget( new GraphicalViewResolveTargetParams( uri, "not-a-urn" ) ).get().warning() )
            .isEqualTo( GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      assertThat( service.resolveTarget( new GraphicalViewResolveTargetParams( uri,
            "urn:samm:example.resolve:1.0.0#RenamedOrDeleted" ) ).get().warning() )
            .isEqualTo( GraphicalViewResolveTargetWarning.NOT_FOUND );
      assertThat( service.resolveTarget( new GraphicalViewResolveTargetParams( uri,
            "urn:samm:example.resolve:1.0.0#Duplicate" ) ).get().warning() )
            .isEqualTo( GraphicalViewResolveTargetWarning.AMBIGUOUS );
      service.close();

      final String invalidUri = directory.resolve( "Invalid.ttl" ).toUri().toString();
      final GraphicalViewService invalid = service( Map.of( invalidUri, new Document( invalidUri, "this is not Turtle" ) ),
            ( snapshot, ignored ) -> null, 1_000 );
      assertThat( invalid.resolveTarget( new GraphicalViewResolveTargetParams( invalidUri,
            "urn:samm:example.resolve:1.0.0#Target" ) ).get().warning() )
            .isEqualTo( GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      invalid.close();
   }

   @Test
   void unsupportedResolvedSchemeAndResolverExceptionAreControlled( @TempDir final java.nio.file.Path directory ) throws Exception {
      final String sourceText = """
            @prefix : <urn:samm:example.resolve:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            :Source a samm:Aspect ; samm:properties (); samm:operations () .
            """;
      final String uri = java.nio.file.Files.writeString( directory.resolve( "Source.ttl" ), sourceText ).toUri().toString();
      final Map<String, Document> documents = Map.of( uri, new Document( uri, sourceText ) );
      final TreeSitterTurtleParserService parser = new TreeSitterTurtleParserService();
      final ResolutionStrategyService strategies = new ResolutionStrategyService();
      final String urn = "urn:samm:example.resolve:1.0.0#Target";
      final AspectCrossFileDefinitionService unsupported = new AspectCrossFileDefinitionService( parser, documents, strategies ) {
         @Override public UrnResolution findDefinition( final org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument source,
               final org.eclipse.esmf.aspectmodel.urn.AspectModelUrn target ) {
            return new UrnResolution( UrnResolution.Outcome.FOUND,
                  new Location( "https://example.invalid/Target.ttl", new Range( new Position(), new Position() ) ) );
         }
      };
      final GraphicalViewService unsupportedService = resolverService( documents, parser, strategies, unsupported );
      assertThat( unsupportedService.resolveTarget( new GraphicalViewResolveTargetParams( uri, urn ) ).get().warning() )
            .isEqualTo( GraphicalViewResolveTargetWarning.UNSUPPORTED_URI );
      unsupportedService.close();

      final AspectCrossFileDefinitionService throwing = new AspectCrossFileDefinitionService( parser, documents, strategies ) {
         @Override public UrnResolution findDefinition( final org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument source,
               final org.eclipse.esmf.aspectmodel.urn.AspectModelUrn target ) {
            throw new IllegalStateException( "temporarily unavailable" );
         }
      };
      final GraphicalViewService throwingService = resolverService( documents, parser, strategies, throwing );
      assertThat( throwingService.resolveTarget( new GraphicalViewResolveTargetParams( uri, urn ) ).get().warning() )
            .isEqualTo( GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      throwingService.close();
   }

   @Test
   void unsafeSourceUriShapesAreRejectedBeforeAnyFilesystemAccess() throws Exception {
      final GraphicalViewService service = service( Map.of(), ( snapshot, uri ) -> { throw new AssertionError( "must not render" ); }, 1_000 );
      for ( final String uri : java.util.List.of( "file:opaque.ttl", "file://server/share.ttl", "file:relative.ttl", "file:///tmp/../secret.ttl", "file:///bad%ZZ.ttl", "https://example/model.ttl" ) ) {
         assertThat( service.resolveTarget( new org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewResolveTargetParams( uri, "urn:samm:example:1.0.0#Target" ) ).get().warning() )
               .isEqualTo( org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewResolveTargetWarning.UNSUPPORTED_URI );
      }
      service.close();
   }

   @Test
   void strictSvgSidecarValidationRejectsEveryInconsistentPair() {
      final String urn = "urn:samm:org.eclipse.esmf.example:1.0.0#Target"; final String id = "gv-header-1234567890abcdef";
      final GraphicalViewTarget valid = new GraphicalViewTarget( id, GraphicalViewTarget.ELEMENT_HEADER, urn );
      assertThat( GraphicalViewService.valid( "<svg id=\"" + id + "\"/>", java.util.List.of( valid ) ) ).isTrue();
      assertThat( GraphicalViewService.valid( "<svg id=\"" + id + "\" id=\"" + id + "\"/>", java.util.List.of( valid ) ) ).isFalse();
      assertThat( GraphicalViewService.valid( "<svg/>", java.util.List.of( valid ) ) ).isFalse();
      assertThat( GraphicalViewService.valid( "<svg id=\"" + id + "\" id=\"gv-header-extra12345678\"/>", java.util.List.of( valid ) ) ).isFalse();
      assertThat( GraphicalViewService.valid( "<svg id=\"gv-header-BAD\"/>", java.util.List.of() ) ).isFalse();
      assertThat( GraphicalViewService.valid( "<svg id=\"" + id + "\"/>", java.util.List.of( valid, valid ) ) ).isFalse();
      assertThat( GraphicalViewService.valid( "<svg id=\"" + id + "\"/>", java.util.List.of( new GraphicalViewTarget( id, GraphicalViewTarget.ELEMENT_HEADER, "not-a-urn" ) ) ) ).isFalse();
   }

   @Test
   void renderUsesPersistedImportNotUnsavedOpenImportAndUsesItAfterLaterSave( @TempDir final java.nio.file.Path directory ) throws Exception {
      final java.nio.file.Path imported = directory.resolve( "ImportedCharacteristic.ttl" );
      java.nio.file.Files.writeString( imported, importedCharacteristic( "Persisted import" ) );
      final java.nio.file.Path main = java.nio.file.Files.writeString( directory.resolve( "Main.ttl" ), mainModel() );
      final String mainUri = main.toUri().toString();
      final Map<String, Document> open = new java.util.concurrent.ConcurrentHashMap<>();
      open.put( mainUri, new Document( main.toUri(), java.nio.file.Files.readString( main ) ) );
      // This document deliberately differs from disk and must not participate in main-model loading.
      open.put( imported.toUri().toString(), new Document( imported.toUri(), importedCharacteristic( "Unsaved import" ) ) );
      final TreeSitterTurtleParserService parser = new TreeSitterTurtleParserService(); final ResolutionStrategyService strategies = new ResolutionStrategyService();
      final GraphicalViewService service = new GraphicalViewService( open, parser, strategies, new AspectCrossFileDefinitionService( parser, open, strategies ) );
      final GraphicalViewRenderResult beforeSave = service.render( new GraphicalViewRenderParams( mainUri ) ).get( 30, TimeUnit.SECONDS );
      assertThat( beforeSave.warnings() ).isEmpty(); assertThat( beforeSave.svg() ).contains( "Persisted&#160;import" ).doesNotContain( "Unsaved&#160;import" );
      java.nio.file.Files.writeString( imported, importedCharacteristic( "Saved import" ) );
      final GraphicalViewRenderResult afterSave = service.render( new GraphicalViewRenderParams( mainUri ) ).get( 30, TimeUnit.SECONDS );
      assertThat( afterSave.warnings() ).isEmpty(); assertThat( afterSave.svg() ).contains( "Saved&#160;import" ).doesNotContain( "Unsaved&#160;import" ); service.close();
   }

   @Test
   void transitiveConfiguredImportAlsoUsesPersistedContentAcrossRequests( @TempDir final java.nio.file.Path directory )
         throws Exception {
      final java.nio.file.Path mainDirectory = java.nio.file.Files.createDirectory( directory.resolve( "main" ) );
      final java.nio.file.Path configuredDirectory = java.nio.file.Files.createDirectory( directory.resolve( "configured" ) );
      java.nio.file.Files.writeString( mainDirectory.resolve( "DirectCharacteristic.ttl" ), """
            @prefix : <urn:samm:example.direct:1.0.0#> .
            @prefix configured: <urn:samm:example.configured:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            :DirectCharacteristic a samm:Characteristic ; samm:dataType configured:ConfiguredEntity .
            """ );
      final java.nio.file.Path configured = configuredDirectory.resolve( "ConfiguredEntity.ttl" );
      java.nio.file.Files.writeString( configured, configuredEntity( "Persisted configured" ) );
      final java.nio.file.Path main = java.nio.file.Files.writeString( mainDirectory.resolve( "Main.ttl" ), """
            @prefix : <urn:samm:example.main.configured:1.0.0#> .
            @prefix direct: <urn:samm:example.direct:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            :Main a samm:Aspect ; samm:properties ( :property ); samm:operations () .
            :property a samm:Property ; samm:characteristic direct:DirectCharacteristic .
            """ );
      final String mainUri = main.toUri().toString();
      final Map<String, Document> open = new java.util.concurrent.ConcurrentHashMap<>();
      open.put( mainUri, new Document( main.toUri(), java.nio.file.Files.readString( main ) ) );
      open.put( configured.toUri().toString(), new Document( configured.toUri(), configuredEntity( "Unsaved configured" ) ) );
      final TreeSitterTurtleParserService parser = new TreeSitterTurtleParserService();
      final ResolutionStrategyService strategies = new ResolutionStrategyService() {
         @Override public org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy buildResolutionStrategyForDocument(
               final org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument parsed ) {
            return new org.eclipse.esmf.aspectmodel.resolver.EitherStrategy(
                  new org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy(
                        new org.eclipse.esmf.aspectmodel.resolver.fs.FlatModelsRoot( configuredDirectory ) ),
                  super.buildResolutionStrategyForDocument( parsed ) );
         }
      };
      final GraphicalViewService service = new GraphicalViewService( open, parser, strategies,
            new AspectCrossFileDefinitionService( parser, open, strategies ) );

      final GraphicalViewRenderResult beforeSave = service.render( new GraphicalViewRenderParams( mainUri ) )
            .get( 30, TimeUnit.SECONDS );
      assertThat( beforeSave.warnings() ).isEmpty();
      assertThat( beforeSave.svg() ).contains( "Persisted&#160;configured" ).doesNotContain( "Unsaved&#160;configured" );
      java.nio.file.Files.writeString( configured, configuredEntity( "Saved configured" ) );
      final GraphicalViewRenderResult afterSave = service.render( new GraphicalViewRenderParams( mainUri ) )
            .get( 30, TimeUnit.SECONDS );
      assertThat( afterSave.warnings() ).isEmpty();
      assertThat( afterSave.svg() ).contains( "Saved&#160;configured" ).doesNotContain( "Unsaved&#160;configured" );
      service.close();
   }

   private static String mainModel() { return """
      @prefix : <urn:samm:example.main:1.0.0#> .
      @prefix ext: <urn:samm:example.import:1.0.0#> .
      @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
      :Main a samm:Aspect ; samm:properties ( :property ) ; samm:operations () .
      :property a samm:Property ; samm:characteristic ext:ImportedCharacteristic .
      """; }

   private static String importedCharacteristic( final String name ) { return """
      @prefix : <urn:samm:example.import:1.0.0#> .
      @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
      @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
      :ImportedCharacteristic a samm:Characteristic ; samm:preferredName "%s"@en ; samm:dataType xsd:string .
      """.formatted( name ); }

   private static String configuredEntity( final String name ) { return """
      @prefix : <urn:samm:example.configured:1.0.0#> .
      @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
      :ConfiguredEntity a samm:Entity ; samm:preferredName "%s"@en ; samm:properties () .
      """.formatted( name ); }

   private static String modelWithBoxes( final int boxCount ) {
      final StringBuilder properties = new StringBuilder();
      final StringBuilder definitions = new StringBuilder();
      for ( int index = 1; index <= boxCount - 2; index++ ) {
         properties.append( " :property" ).append( index );
         definitions.append( ":property" ).append( index )
               .append( " a samm:Property ; samm:characteristic samm-c:Text .\n" );
      }
      return """
            @prefix : <urn:samm:example.lsp.limit:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
            :LimitAspect a samm:Aspect ; samm:properties (%s ); samm:operations () .
            %s
            """.formatted( properties, definitions );
   }

   private static GraphicalViewService service( final Map<String, Document> documents, final GraphicalViewService.RenderOperation operation, final long timeout ) {
      final TreeSitterTurtleParserService parser = new TreeSitterTurtleParserService(); final ResolutionStrategyService strategies = new ResolutionStrategyService();
      return new GraphicalViewService( documents, parser, strategies, new AspectCrossFileDefinitionService( parser, documents, strategies ), Executors.newSingleThreadExecutor(), Executors.newSingleThreadScheduledExecutor(), timeout, operation );
   }

   private static GraphicalViewService resolverService( final Map<String, Document> documents,
         final TreeSitterTurtleParserService parser, final ResolutionStrategyService strategies,
         final AspectCrossFileDefinitionService definitions ) {
      return new GraphicalViewService( documents, parser, strategies, definitions, Executors.newSingleThreadExecutor(),
            Executors.newSingleThreadScheduledExecutor(), 1_000, ( snapshot, uri ) -> null );
   }
}
