/* Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0 */
package org.eclipse.esmf.turtle.languageserver.graphical.navigation;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategySupport;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.turtle.languageserver.graphical.navigation.GraphicalViewTargetResolver.Outcome;
import org.eclipse.esmf.turtle.languageserver.graphical.source.GraphicalViewSourceContext;
import org.eclipse.esmf.turtle.languageserver.graphical.source.GraphicalViewSourceSnapshot;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import org.eclipse.lsp4j.Location;
import org.junit.jupiter.api.Test;

class GraphicalViewTargetResolverTest {
   private static final String URI_VALUE = "file:///tmp/graphical/resolver.ttl";
   private static final String OWNER = "urn:samm:example.resolve:1.0.0#Owner";
   private static final String DESCRIPTION = "urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#description";
   private static final String SEE = "urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#see";

   @Test
   void headerResolverReturnsFoundNotFoundAndAmbiguousWithoutFullIriSubjectSupport() {
      final GraphicalViewTargetResolver resolver = resolver( Map.of(), new ResolutionStrategyService() );
      final GraphicalViewSourceSnapshot source = snapshot( """
            @prefix : <urn:samm:example.resolve:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            :Owner a samm:Aspect ; samm:properties (); samm:operations () .
            :Duplicate a samm:Characteristic .
            :Duplicate a samm:Characteristic .
            <urn:samm:example.resolve:1.0.0#FullIri> a samm:Characteristic .
            """ );

      final var found = resolver.resolveHeader( source, urn( OWNER ) );
      assertThat( found.outcome() ).isEqualTo( Outcome.FOUND );
      assertStartsAt( found.location(), source.content(), "Owner" );
      assertThat( resolver.resolveHeader( source, urn( "urn:samm:example.resolve:1.0.0#Missing" ) ).outcome() )
            .isEqualTo( Outcome.NOT_FOUND );
      assertThat( resolver.resolveHeader( source, urn( "urn:samm:example.resolve:1.0.0#Duplicate" ) ).outcome() )
            .isEqualTo( Outcome.AMBIGUOUS );
      assertThat( resolver.resolveHeader( source, urn( "urn:samm:example.resolve:1.0.0#FullIri" ) ).outcome() )
            .isEqualTo( Outcome.NOT_FOUND );
   }

   @Test
   void attributeResolverKeepsLanguageFullIriOwnerAndDistinctSeeSemantics() {
      final GraphicalViewTargetResolver resolver = resolver( Map.of(), new ResolutionStrategyService() );
      final String sourceText = model();
      final GraphicalViewSourceSnapshot source = snapshot( sourceText );

      final var english = resolver.resolveAttribute( source, urn( OWNER ), DESCRIPTION, "en" );
      final var german = resolver.resolveAttribute( source, urn( OWNER ), DESCRIPTION, "de" );
      assertThat( english.outcome() ).isEqualTo( Outcome.FOUND );
      assertThat( german.outcome() ).isEqualTo( Outcome.FOUND );
      assertThat( english.location().getRange() ).isNotEqualTo( german.location().getRange() );
      assertStartsAt( english.location(), sourceText, "samm:description" );

      final String fullIri = model().replace( ":Owner a samm:Aspect", "<" + OWNER + "> a samm:Aspect" );
      assertThat( resolver.resolveAttribute( snapshot( fullIri ), urn( OWNER ), DESCRIPTION, "en" ).outcome() )
            .isEqualTo( Outcome.FOUND );

      final String distinctSee = model().replace(
            "samm:see <https://example.test/one>, <https://example.test/two> ;",
            "samm:see <https://example.test/one> ;\n   samm:see <https://example.test/two> ;" );
      assertThat( resolver.resolveAttribute( snapshot( distinctSee ), urn( OWNER ), SEE, null ).outcome() )
            .isEqualTo( Outcome.AMBIGUOUS );
   }

   @Test
   void externalOwnerUsesSourceContextSnapshotInsteadOfFilesystem() {
      final String externalUri = "file:///tmp/graphical/external-owner.ttl";
      final String externalText = """
            @prefix : <urn:samm:example.external:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            :ExternalOwner a samm:Property ; samm:description "Current open"@en .
            """;
      final TreeSitterTurtleParserService parser = new TreeSitterTurtleParserService();
      final Document externalDocument = new Document( externalUri, externalText );
      final AspectModelFile resolvedFile = AspectModelFileLoader.load( parser.apply( externalDocument ).turtleSyntaxTree(),
            URI.create( externalUri ) );
      final ResolutionStrategyService strategies = new ResolutionStrategyService() {
         @Override
         public ResolutionStrategy buildResolutionStrategyForDocument(
               final org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument ignored ) {
            return fixedStrategy( resolvedFile );
         }
      };
      final GraphicalViewTargetResolver resolver = resolver( Map.of( externalUri, externalDocument ), strategies );

      final var result = resolver.resolveAttribute( snapshot( model() ),
            urn( "urn:samm:example.external:1.0.0#ExternalOwner" ), DESCRIPTION, "en" );

      assertThat( result.outcome() ).isEqualTo( Outcome.FOUND );
      assertThat( result.location().getUri() ).isEqualTo( externalUri );
      assertStartsAt( result.location(), externalText, "samm:description" );
   }

   private static GraphicalViewTargetResolver resolver( final Map<String, Document> documents,
         final ResolutionStrategyService strategies ) {
      return new GraphicalViewTargetResolver( new TreeSitterTurtleParserService(), strategies,
            new GraphicalViewSourceContext( documents ) );
   }

   private static ResolutionStrategy fixedStrategy( final AspectModelFile file ) {
      return new ResolutionStrategy() {
         @Override
         public AspectModelFile apply( final AspectModelUrn urn, final ResolutionStrategySupport support ) {
            return file;
         }

         @Override public Stream<URI> listContents() { return Stream.of( file.sourceUri() ); }
         @Override public Stream<URI> listContentsForNamespace( final AspectModelUrn urn ) { return listContents(); }
         @Override public Stream<AspectModelFile> loadContents() { return Stream.of( file ); }
         @Override public Stream<AspectModelFile> loadContentsForNamespace( final AspectModelUrn urn ) { return loadContents(); }
      };
   }

   private static GraphicalViewSourceSnapshot snapshot( final String content ) {
      return new GraphicalViewSourceSnapshot( URI.create( URI_VALUE ), content );
   }

   private static AspectModelUrn urn( final String value ) {
      return AspectModelUrn.from( value ).get();
   }

   private static void assertStartsAt( final Location location, final String source, final String expected ) {
      final String line = source.lines().toList().get( location.getRange().getStart().getLine() );
      assertThat( line.substring( location.getRange().getStart().getCharacter() ) ).startsWith( expected );
   }

   private static String model() {
      return """
            @prefix : <urn:samm:example.resolve:1.0.0#> .
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
            :Owner a samm:Aspect ;
               samm:description "English"@en ;
               samm:description "Deutsch"@de ;
               samm:see <https://example.test/one>, <https://example.test/two> ;
               samm:properties () ;
               samm:operations () .
            """;
   }
}
