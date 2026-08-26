/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.turtle.languageserver.aspect.navigation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

@SuppressWarnings( { "HttpUrlsUsage" } )
class AspectCrossFileDefinitionServiceTest {

   private TreeSitterTurtleParserService parserService;

   @BeforeEach
   void setUp() {
      parserService = new TreeSitterTurtleParserService();
   }

   private static Path modelFromTestAspectModels( final String relativePath ) {
      Path current = Path.of( "" ).toAbsolutePath();
      while ( current != null ) {
         final Path candidate = current.resolve( "core/esmf-test-aspect-models/src/main/resources" ).resolve( relativePath );
         if ( Files.isRegularFile( candidate ) ) {
            return candidate;
         }
         current = current.getParent();
      }
      throw new IllegalStateException( "Could not locate model resource in esmf-test-aspect-models: " + relativePath );
   }

   // Do not clean tempDir to work around file locking issues on windows
   @Test
   void testGoToDefinitionInSeparateFile_structuredLayout( @TempDir( cleanup = CleanupMode.NEVER ) final Path tmpDir ) throws IOException {
      final Path modelsRoot = tmpDir.resolve( "org.eclipse.esmf.test" ).resolve( "1.0.0" );
      final Path sourceModel = modelFromTestAspectModels( "valid/org.eclipse.esmf.test/1.0.0/Aspect.ttl" );
      final Path targetModel = modelsRoot.resolve( "Aspect.ttl" );
      Files.createDirectories( targetModel.getParent() );
      Files.copy( sourceModel, targetModel );

      final String referencingContent = """
         @prefix ext: <urn:samm:org.eclipse.esmf.test:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

         :MyAspect a samm:Aspect ;
            samm:properties () ;
            samm:extends ext:Aspect .
         """;
      final Path aspectPath = Files.writeString( modelsRoot.resolve( "MyAspect.ttl" ), referencingContent );
      final Document document = new Document( aspectPath.toUri().toString(), referencingContent );
      final ParsedDocument parsedDocument = parserService.apply( document );

      final AspectCrossFileDefinitionService resolver = new AspectCrossFileDefinitionService(
            parserService,
            Map.of() );

      // Position inside "ext:Aspect" (line 5 in the text block).
      final Optional<Location> result = resolver.findDefinition( parsedDocument, new Position( 5, 22 ) );

      assertThat( result ).isPresent();
      final Location location = result.get();
      assertThat( location.getUri() ).endsWith( "Aspect.ttl" );
      assertThat( location.getRange().getStart() ).isEqualTo( new Position( 17, 1 ) );
   }

   // Do not clean tempDir to work around file locking issues on windows
   @Test
   void testGoToDefinitionInSeparateFile_flatLayout( @TempDir( cleanup = CleanupMode.NEVER ) final Path tmpDir ) throws IOException {
      final String definingContent = """
         @prefix : <urn:samm:com.example:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#> .

         :SharedType a samm:AbstractEntity .
         """;
      Files.writeString( tmpDir.resolve( "SharedType.ttl" ), definingContent );

      final String referencingContent = """
         @prefix ext: <urn:samm:com.example:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#> .

         :MyAspect a samm:Aspect ;
            samm:extends ext:SharedType .
         """;
      final Path aspectPath = Files.writeString( tmpDir.resolve( "MyAspect.ttl" ), referencingContent );
      final Document document = new Document( aspectPath.toUri().toString(), referencingContent );
      final ParsedDocument parsedDocument = parserService.apply( document );

      final AspectCrossFileDefinitionService resolver = new AspectCrossFileDefinitionService(
            parserService,
            Map.of() );

      final Optional<Location> result = resolver.findDefinition( parsedDocument, new Position( 4, 19 ) );

      assertThat( result ).isPresent();
      assertThat( result.get().getUri() ).endsWith( "SharedType.ttl" );
      assertThat( result.get().getRange().getStart() ).isEqualTo( new Position( 3, 1 ) );
   }

   // Do not clean tempDir to work around file locking issues on windows
   @Test
   void testReturnsEmptyWhenElementNotFound( @TempDir( cleanup = CleanupMode.NEVER ) final Path tmpDir ) throws IOException {
      final Path nsDir = tmpDir.resolve( "com.example" ).resolve( "1.0.0" );
      Files.createDirectories( nsDir );
      Files.writeString( nsDir.resolve( "ExistingType.ttl" ), """
         @prefix : <urn:samm:com.example:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#> .

         :ExistingType a samm:AbstractEntity .
         """ );

      final String referencingContent = """
         @prefix ext: <urn:samm:com.example:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#> .

         :MyAspect a samm:Aspect ;
            samm:extends ext:MissingType .
         """;
      final ParsedDocument parsedDocument = parserService.apply(
            new Document( "file:///workspace/MyAspect.ttl", referencingContent ) );

      final AspectCrossFileDefinitionService resolver = new AspectCrossFileDefinitionService(
            parserService,
            Map.of() );

      final Optional<Location> result = resolver.findDefinition( parsedDocument, new Position( 4, 19 ) );

      assertThat( result ).isEmpty();
   }

   @Test
   void testGoToDefinitionForUnitNamespace() {
      final String content = """
         @prefix : <urn:samm:com.example:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#> .
         @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.1.0#> .
         @prefix unit: <urn:samm:org.eclipse.esmf.samm:unit:2.1.0#> .
         @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

         :Quantity a samm-c:Quantifiable ;
            samm:dataType xsd:float ;
            samm-c:unit unit:piece .
         """;

      final ParsedDocument parsedDocument = parserService.apply(
            new Document( "file:///workspace/Quantity.ttl", content ) );

      final AspectCrossFileDefinitionService resolver = new AspectCrossFileDefinitionService(
            parserService,
            Map.of() );

      final Optional<Location> result = resolver.findDefinition( parsedDocument, new Position( 8, 20 ) );

      assertThat( result ).isPresent();
      final Location location = result.get();
      assertThat( location.getUri() ).contains( "unit" );
      assertThat( location.getUri() ).endsWith( ".ttl" );
      assertThat( location.getRange().getStart().getLine() ).isGreaterThan( 1000 );
   }

   @Test
   void fullUrnResolutionUsesOpenTargetCoordinatesAndClosedPersistedContent( @TempDir final Path directory ) throws IOException {
      final Path target = directory.resolve( "Thing.ttl" );
      Files.writeString( target, """
            @prefix : <urn:samm:example.cross:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            :PersistedThing a samm:Characteristic .
            """ );
      final Path source = Files.writeString( directory.resolve( "Source.ttl" ), """
            @prefix : <urn:samm:source:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            :Source a samm:Aspect ; samm:properties (); samm:operations () .
            """ );
      final Document sourceDocument = new Document( source.toUri(), Files.readString( source ) );
      final Map<String, Document> open = new HashMap<>();
      final AspectCrossFileDefinitionService service = new AspectCrossFileDefinitionService( parserService, open );
      final var persisted = service.findDefinition( parserService.apply( sourceDocument ),
            org.eclipse.esmf.aspectmodel.urn.AspectModelUrn.from( "urn:samm:example.cross:1.0.0#PersistedThing" ).get() );
      assertThat( persisted.outcome() ).isEqualTo( AspectCrossFileDefinitionService.UrnResolution.Outcome.FOUND );
      assertThat( persisted.location().getRange().getStart().getLine() ).isEqualTo( 2 );

      open.put( target.toUri().toString(), new Document( target.toUri(), """
            @prefix : <urn:samm:example.cross:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

            :PersistedThing a samm:Characteristic .
            """ ) );
      final var moved = service.findDefinition( parserService.apply( sourceDocument ),
            org.eclipse.esmf.aspectmodel.urn.AspectModelUrn.from( "urn:samm:example.cross:1.0.0#PersistedThing" ).get() );
      assertThat( moved.outcome() ).isEqualTo( AspectCrossFileDefinitionService.UrnResolution.Outcome.FOUND );
      assertThat( moved.location().getRange().getStart().getLine() ).isEqualTo( 3 );
   }

   @Test
   void fullUrnResolutionReturnsNotFoundAndAmbiguousOutcomes( @TempDir final Path directory ) throws IOException {
      final String model = """
            @prefix : <urn:samm:example.outcome:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            :Source a samm:Aspect ; samm:properties (); samm:operations () .
            :Duplicate a samm:Characteristic .
            :Duplicate a samm:Characteristic .
            """;
      final Path file = Files.writeString( directory.resolve( "outcomes.ttl" ), model );
      final Document document = new Document( file.toUri(), model );
      final AspectCrossFileDefinitionService service = new AspectCrossFileDefinitionService( parserService, Map.of() );
      assertThat( service.findDefinition( parserService.apply( document ), org.eclipse.esmf.aspectmodel.urn.AspectModelUrn.from( "urn:samm:example.outcome:1.0.0#Missing" ).get() ).outcome() )
            .isEqualTo( AspectCrossFileDefinitionService.UrnResolution.Outcome.NOT_FOUND );
      assertThat( service.findDefinition( parserService.apply( document ), org.eclipse.esmf.aspectmodel.urn.AspectModelUrn.from( "urn:samm:example.outcome:1.0.0#Duplicate" ).get() ).outcome() )
            .isEqualTo( AspectCrossFileDefinitionService.UrnResolution.Outcome.AMBIGUOUS );
   }
}
