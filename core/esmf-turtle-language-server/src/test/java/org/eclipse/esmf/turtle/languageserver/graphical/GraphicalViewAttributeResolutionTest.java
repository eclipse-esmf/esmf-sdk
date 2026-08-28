/* Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0 */
package org.eclipse.esmf.turtle.languageserver.graphical;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.esmf.turtle.languageserver.aspect.navigation.AspectCrossFileDefinitionService;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderParams;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewRenderResult;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewResolveAttributeTargetParams;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewResolveAttributeTargetResult;
import org.eclipse.esmf.turtle.languageserver.lsp.request.GraphicalViewResolveTargetWarning;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GraphicalViewAttributeResolutionTest {
   private static final String OWNER = "urn:samm:example.attribute:1.0.0#Owner";
   private static final String IMPORTED_OWNER = "urn:samm:example.imported:1.0.0#ImportedOwner";
   private static final String DESCRIPTION = "urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#description";
   private static final String SEE = "urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#see";

   @Test
   void resolvesDirectLanguageAndAggregatedPredicateStartAtCurrentCoordinates( @TempDir final Path directory ) throws Exception {
      final Path source = Files.writeString( directory.resolve( "Main.ttl" ), model( "" ) );
      final String uri = source.toUri().toString();
      final Document document = new Document( uri, model( "" ) );
      final Map<String, Document> open = new ConcurrentHashMap<>();
      open.put( uri, document );
      final TestContext context = trustedService( open, uri );
      final GraphicalViewService service = context.service();

      final GraphicalViewResolveAttributeTargetResult description = resolve( service, uri, OWNER, DESCRIPTION,
            "singleOccurrence", "en" );
      assertThat( description.warning() ).isNull();
      assertStartsAt( description.location(), document.content(), "samm:description" );
      final GraphicalViewResolveAttributeTargetResult germanDescription = resolve( service, uri, OWNER, DESCRIPTION,
            "singleOccurrence", "de" );
      assertThat( germanDescription.warning() ).isNull();
      assertStartsAt( germanDescription.location(), document.content(), "samm:description" );
      assertThat( germanDescription.location().getRange() ).isNotEqualTo( description.location().getRange() );

      final GraphicalViewResolveAttributeTargetResult see = resolve( service, uri, OWNER, SEE, "predicateStart", null );
      assertThat( see.warning() ).isNull();
      assertStartsAt( see.location(), document.content(), "samm:see" );

      final String moved = model( "\n\n# reformatted before the owner\n" );
      update( context.parser(), document, moved );
      final GraphicalViewResolveAttributeTargetResult movedDescription = resolve( service, uri, OWNER, DESCRIPTION,
            "singleOccurrence", "en" );
      final GraphicalViewResolveAttributeTargetResult movedGermanDescription = resolve( service, uri, OWNER, DESCRIPTION,
            "singleOccurrence", "de" );
      assertThat( movedDescription.location().getRange().getStart().getLine() )
            .isGreaterThan( description.location().getRange().getStart().getLine() );
      assertStartsAt( movedDescription.location(), moved, "samm:description" );
      assertThat( movedGermanDescription.location().getRange().getStart().getLine() )
            .isGreaterThan( germanDescription.location().getRange().getStart().getLine() );
      assertStartsAt( movedGermanDescription.location(), moved, "samm:description" );
      service.close();
   }

   @Test
   void resolvesImportedClosedOwnerFromCurrentPersistedLocalFile( @TempDir final Path directory ) throws Exception {
      final Path main = Files.writeString( directory.resolve( "Main.ttl" ), model( "" ) );
      final Path imported = Files.writeString( directory.resolve( "ImportedOwner.ttl" ), importedModel() );
      final String uri = main.toUri().toString();
      final Map<String, Document> open = new ConcurrentHashMap<>();
      open.put( uri, new Document( uri, model( "" ) ) );
      final GraphicalViewService service = trustedService( open, uri ).service();

      final GraphicalViewResolveAttributeTargetResult result = resolve( service, uri, IMPORTED_OWNER, DESCRIPTION,
            "singleOccurrence", "en" );

      assertThat( result.warning() ).isNull();
      assertThat( result.location().getUri() ).isEqualTo( imported.toUri().toString() );
      assertStartsAt( result.location(), importedModel(), "samm:description" );
      service.close();
   }

   @Test
   void resolvesSameFileOwnerWrittenAsFullIri( @TempDir final Path directory ) throws Exception {
      final String fullIriModel = model( "" ).replace( ":Owner a samm:Aspect", "<" + OWNER + "> a samm:Aspect" );
      final Path source = Files.writeString( directory.resolve( "FullIriOwner.ttl" ), fullIriModel );
      final String uri = source.toUri().toString();
      final Map<String, Document> open = new ConcurrentHashMap<>();
      open.put( uri, new Document( uri, fullIriModel ) );
      final GraphicalViewService service = trustedService( open, uri ).service();

      final GraphicalViewResolveAttributeTargetResult result = resolve( service, uri, OWNER, DESCRIPTION,
            "singleOccurrence", "en" );

      assertThat( result.warning() ).isNull();
      assertThat( result.location().getUri() ).isEqualTo( uri );
      assertStartsAt( result.location(), fullIriModel, "samm:description" );
      service.close();
   }

   @Test
   void resolvesImportedOwnerWrittenAsFullIri( @TempDir final Path directory ) throws Exception {
      final Path main = Files.writeString( directory.resolve( "Main.ttl" ), model( "" ) );
      final String fullIriImportedModel = importedModel().replace( ":ImportedOwner a samm:Property",
            "<" + IMPORTED_OWNER + "> a samm:Property" );
      final Path imported = Files.writeString( directory.resolve( "ImportedOwner.ttl" ), fullIriImportedModel );
      final String uri = main.toUri().toString();
      final Map<String, Document> open = new ConcurrentHashMap<>();
      open.put( uri, new Document( uri, model( "" ) ) );
      final GraphicalViewService service = trustedService( open, uri ).service();

      final GraphicalViewResolveAttributeTargetResult result = resolve( service, uri, IMPORTED_OWNER, DESCRIPTION,
            "singleOccurrence", "en" );

      assertThat( result.warning() ).isNull();
      assertThat( result.location().getUri() ).isEqualTo( imported.toUri().toString() );
      assertStartsAt( result.location(), fullIriImportedModel, "samm:description" );
      service.close();
   }

   @Test
   void missingAmbiguousMalformedInvalidSyntaxUnsupportedAndUntrustedInputsFailClosed( @TempDir final Path directory ) throws Exception {
      final Path source = Files.writeString( directory.resolve( "Main.ttl" ), model( "" ) );
      final String uri = source.toUri().toString();
      final Document document = new Document( uri, model( "" ) );
      final Map<String, Document> open = new ConcurrentHashMap<>();
      open.put( uri, document );
      final TestContext context = trustedService( open, uri );
      final GraphicalViewService service = context.service();

      assertWarning( resolve( service, uri, OWNER,
            "urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#missing", "singleOccurrence", null ),
            GraphicalViewResolveTargetWarning.NOT_FOUND );
      assertWarning( resolve( service, uri, OWNER, DESCRIPTION, "unknown", null ),
            GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      assertWarning( resolve( service, uri, OWNER, DESCRIPTION, "predicateStart", "en" ),
            GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );

      update( context.parser(), document, modelWithDuplicateDescriptions() );
      assertWarning( resolve( service, uri, OWNER, DESCRIPTION, "singleOccurrence", "en" ),
            GraphicalViewResolveTargetWarning.AMBIGUOUS );
      update( context.parser(), document, model( "" ).replace( ";\n   samm:see", "\n   samm:see" ) );
      assertWarning( resolve( service, uri, OWNER, DESCRIPTION, "singleOccurrence", "en" ),
            GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      update( context.parser(), document, model( "" ).replace( ":Owner", ":DeletedOwner" ) );
      assertWarning( resolve( service, uri, OWNER, DESCRIPTION, "singleOccurrence", "en" ),
            GraphicalViewResolveTargetWarning.NOT_FOUND );

      assertWarning( resolve( service, "https://example.test/model.ttl", OWNER, DESCRIPTION, "singleOccurrence", "en" ),
            GraphicalViewResolveTargetWarning.UNSUPPORTED_URI );
      final String closedUntrusted = directory.resolve( "Untrusted.ttl" ).toUri().toString();
      Files.writeString( Path.of( java.net.URI.create( closedUntrusted ) ), model( "" ) );
      assertWarning( resolve( service, closedUntrusted, OWNER, DESCRIPTION, "singleOccurrence", "en" ),
            GraphicalViewResolveTargetWarning.TEMPORARILY_UNRESOLVABLE );
      service.close();
   }

   @Test
   void distinctSeePredicateStatementsAreAmbiguous( @TempDir final Path directory ) throws Exception {
      final String duplicateSeeModel = model( "" ).replace(
            "samm:see <https://example.test/one>, <https://example.test/two> ;",
            "samm:see <https://example.test/reference/with/a/long/path> ;\n"
                  + "   samm:see <urn:irdi:0173:1:02:AAO677:003> ;" );
      final Path source = Files.writeString( directory.resolve( "DuplicateSee.ttl" ), duplicateSeeModel );
      final String uri = source.toUri().toString();
      final Map<String, Document> open = new ConcurrentHashMap<>();
      open.put( uri, new Document( uri, duplicateSeeModel ) );
      final GraphicalViewService service = trustedService( open, uri ).service();

      assertWarning( resolve( service, uri, OWNER, SEE, "predicateStart", null ),
            GraphicalViewResolveTargetWarning.AMBIGUOUS );
      service.close();
   }

   private static TestContext trustedService( final Map<String, Document> documents, final String uri ) throws Exception {
      final TreeSitterTurtleParserService parser = new TreeSitterTurtleParserService();
      final ResolutionStrategyService strategies = new ResolutionStrategyService();
      final GraphicalViewService service = new GraphicalViewService( documents, parser, strategies,
            new AspectCrossFileDefinitionService( parser, documents, strategies ), Executors.newSingleThreadExecutor(),
            Executors.newSingleThreadScheduledExecutor(), 1_000,
            ( snapshot, sourceUri ) -> new GraphicalViewRenderResult( sourceUri, "<svg/>", java.util.List.of(), java.util.List.of() ) );
      service.render( new GraphicalViewRenderParams( uri ) ).get( 2, TimeUnit.SECONDS );
      return new TestContext( service, parser );
   }

   private static void update( final TreeSitterTurtleParserService parser, final Document document, final String content ) {
      document.update( null, content );
      final TextDocumentContentChangeEvent change = new TextDocumentContentChangeEvent();
      change.setText( content );
      parser.onChange( document, change );
   }

   private record TestContext(
         GraphicalViewService service,
         TreeSitterTurtleParserService parser
   ) {}

   private static GraphicalViewResolveAttributeTargetResult resolve( final GraphicalViewService service, final String sourceUri,
         final String ownerUrn, final String predicateUrn, final String selection, final String language ) throws Exception {
      return service.resolveAttributeTarget(
            new GraphicalViewResolveAttributeTargetParams( sourceUri, ownerUrn, predicateUrn, selection, language ) )
            .get( 2, TimeUnit.SECONDS );
   }

   private static void assertWarning( final GraphicalViewResolveAttributeTargetResult result,
         final GraphicalViewResolveTargetWarning warning ) {
      assertThat( result.location() ).isNull();
      assertThat( result.warning() ).isEqualTo( warning.wireValue() );
   }

   private static void assertStartsAt( final Location location, final String source, final String expected ) {
      final String line = source.lines().toList().get( location.getRange().getStart().getLine() );
      assertThat( line.substring( location.getRange().getStart().getCharacter() ) ).startsWith( expected );
   }

   private static String model( final String beforeOwner ) {
      return """
            @prefix : <urn:samm:example.attribute:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            %s
            :Owner a samm:Aspect ;
               samm:description "English"@en ;
               samm:description "Deutsch"@de ;
               samm:see <https://example.test/one>, <https://example.test/two> ;
               samm:properties () ;
               samm:operations () .
            """.formatted( beforeOwner );
   }

   private static String modelWithDuplicateDescriptions() {
      return model( "" ).replace( "samm:description \"English\"@en ;",
            "samm:description \"English one\"@en ;\n   samm:description \"English two\"@en ;" );
   }

   private static String importedModel() {
      return """
            @prefix : <urn:samm:example.imported:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

            :ImportedOwner a samm:Property ;
               samm:description "Imported English"@en .
            """;
   }
}
