/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.WordUtils;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.eclipse.esmf.aspectmodel.UnsupportedVersionException;
import org.eclipse.esmf.aspectmodel.generator.LanguageCollector;
import org.eclipse.esmf.aspectmodel.resolver.services.MetaModelUrls;
import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.samm.KnownVersion;

import com.google.common.collect.ImmutableList;

import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

public class AspectModelDiagramGenerator {
   public enum Format {
      PNG,
      SVG,
      DOT;

      public String getArtifactFilename( final String aspectName, final Locale language ) {
         return String.format( "%s_%s.%s", aspectName, language.toLanguageTag(), toString().toLowerCase() );
      }
   }

   private final Map<KnownVersion, List<String>> aspectToBoxmodelQueryFiles = new EnumMap<>( KnownVersion.class );

   private static final String FONT_NAME = "Roboto Condensed";
   private static final String FONT_FILE = "diagram/RobotoCondensed-Regular.ttf";

   static final String GET_ELEMENT_NAME_FUNC = "urn:samm:org.eclipse.esmf.samm:function:2.0.0#getElementName";

   private final Query boxmodelToDotQuery;
   private final BoxModel boxModelNamespace;

   protected final Model model;
   final KnownVersion metaModelVersion;

   private final SparqlExecutor sparqlExecutor;

   public AspectModelDiagramGenerator( final VersionedModel versionedModel ) {
      final ImmutableList<String> queryFilesForAllMetaModelVersions = ImmutableList.of(
            "aspect",
            "characteristic",
            "collection",
            "constraint",
            "entity",
            "enumeration",
            "metamodelnode",
            "operation",
            "property",
            "unit",
            "aspect-operation-edges",
            "aspect-property-edges",
            "characteristic-characteristic-edges",
            "characteristic-entity-edges",
            "characteristic-metamodelnode-edges",
            "operation-property-edges",
            "property-characteristic-edges",
            "enumeration-entityinstance",
            "entityinstance",
            "quantifiable-unit-edges",
            "either",
            "either-left-characteristic-edges",
            "either-right-characteristic-edges",
            "structuredvalue",
            "structuredvalue-property-edges",
            "entityinstance-nestedentityinstance-edges",
            "entityinstance-entity-edges",
            "collection-elementcharacteristic-edges",
            "characteristic-constraint-edges"
      );

      final ImmutableList<String> queryFilesForMetaModelVersionsAsOf2_0_0 = ImmutableList.of(
            "abstractentity",
            "entity-abstractentity-edges",
            "abstractproperty",
            "entity-abstractproperty-edges"
      );

      aspectToBoxmodelQueryFiles.put( KnownVersion.SAMM_1_0_0,
            ImmutableList.<String> builder().addAll( queryFilesForAllMetaModelVersions )
                  .build() );

      aspectToBoxmodelQueryFiles.put( KnownVersion.SAMM_2_0_0,
            ImmutableList.<String> builder().addAll( queryFilesForAllMetaModelVersions )
                  .addAll( queryFilesForMetaModelVersionsAsOf2_0_0 )
                  .build() );

      ARQ.init();
      model = versionedModel.getModel();
      metaModelVersion = KnownVersion.fromVersionString( versionedModel.getMetaModelVersion().toString() )
            .orElseThrow( () -> new UnsupportedVersionException( versionedModel.getMetaModelVersion() ) );
      boxmodelToDotQuery = QueryFactory.create( getInputStreamAsString( "boxmodel2dot.sparql" ) );
      boxModelNamespace = new BoxModel( metaModelVersion );

      sparqlExecutor = new SparqlExecutor().useCustomFunction( GET_ELEMENT_NAME_FUNC, new GetElementNameFunctionFactory( model ) );
   }

   InputStream getInputStream( final String resource ) {
      return getClass().getClassLoader().getResourceAsStream( resource );
   }

   String getInputStreamAsString( final String resource ) {
      try ( final InputStream resourceStream = getInputStream(
            "diagram/" + metaModelVersion.toString().toLowerCase() + "/" + resource );
            final Scanner scanner = new Scanner( resourceStream, StandardCharsets.UTF_8.name() ) ) {
         return scanner.useDelimiter( "\\A" ).next();
      } catch ( final IOException ioException ) {
         throw new UncheckedIOException( ioException );
      }
   }

   @SuppressWarnings( "squid:S1166" )
   private void generatePng( final String dotInput, final OutputStream output ) throws IOException {
      // To make the font available during PNG generation, it needs to be registered
      // in Java Runtime's graphics environment
      try ( final InputStream fontStream = getInputStream( FONT_FILE ) ) {
         final Font f = Font.createFont( Font.TRUETYPE_FONT, fontStream );
         final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         ge.registerFont( f );
      } catch ( final FontFormatException e ) {
         // Will only happen if the loaded .ttf file is invalid
      }

      final MutableGraph g = new Parser().read( dotInput );
      final Graphviz graphviz = Graphviz.fromGraph( g );
      graphviz.render( guru.nidi.graphviz.engine.Format.PNG ).toOutputStream( output );
   }

   private String base64EncodeInputStream( final InputStream in ) throws IOException {
      try ( final ByteArrayOutputStream os = new ByteArrayOutputStream() ) {
         final byte[] buffer = new byte[1024];
         for ( int len = in.read( buffer ); len != -1; len = in.read( buffer ) ) {
            os.write( buffer, 0, len );
         }
         return Base64.getEncoder().encodeToString( os.toByteArray() );
      }
   }

   private void generateSvg( final String dotInput, final OutputStream output ) throws IOException {
      final MutableGraph g = new Parser().read( dotInput );
      final Graphviz graphviz = Graphviz.fromGraph( g );

      // Render SVG
      try ( final ByteArrayOutputStream svgOutput = new ByteArrayOutputStream();
            final InputStream fontStream = getInputStream( FONT_FILE ) ) {
         graphviz.render( guru.nidi.graphviz.engine.Format.SVG ).toOutputStream( svgOutput );
         // To make the font available in the generated SVG, it needs to be Base64-encoded
         // and embedded in the file.
         final String fontInBase64 = base64EncodeInputStream( fontStream );
         final String css = "\n<style>\n"
               + "@font-face {\n"
               + "    font-family: \"" + FONT_NAME + "\";\n"
               + "    src: url(\"data:application/font-truetype;charset=utf-8;base64," + fontInBase64
               + "\");\n"
               + "}\n"
               + "</style>";
         final String result = svgOutput.toString( StandardCharsets.UTF_8.name() )
               .replaceFirst( ">", ">" + css );
         output.write( result.getBytes( StandardCharsets.UTF_8.name() ) );
      }
   }

   private void breakLongLinesAndEscapeTexts( final Model model ) {
      final Property text = boxModelNamespace.text();
      final Iterable<Statement> statementIterable = () -> model.listStatements( null, text, (RDFNode) null );
      StreamSupport.stream( statementIterable.spliterator(), false ).collect( Collectors.toList() )
            .forEach( oldStatement -> {
               final String newValue = WordUtils.wrap( oldStatement.getLiteral().getString(), 60, "\\l   ", false );
               final String escapedValue = StringEscapeUtils.escapeHtml4( newValue );
               final Statement newStatement = ResourceFactory.createStatement( oldStatement.getSubject(), oldStatement.getPredicate(),
                     ResourceFactory.createPlainLiteral( escapedValue ) );
               model.remove( oldStatement );
               model.add( newStatement );
            } );
   }

   /**
    * Takes a RDF/Turtle input stream of the Aspect Model and creates the corresponding diagram in DOT format
    *
    * @return The corresponding diagram in DOT format
    */
   private String generateDot( final Locale language ) {
      final Model targetModel = ModelFactory.createDefaultModel();
      aspectToBoxmodelQueryFiles.get( metaModelVersion )
            .stream()
            .map( queryName -> getInputStreamAsString( queryName + "2boxmodel.sparql" ) )
            .map( queryString -> queryString
                  .replace( "\"en\"", "\"" + language.toLanguageTag() + "\"" ) )
            .map( QueryFactory::create )
            .forEach( query -> sparqlExecutor.executeConstruct( model, query, targetModel ) );

      breakLongLinesAndEscapeTexts( targetModel );

      targetModel.add( targetModel.createResource(), boxModelNamespace.rootElement(), getAspect() );
      MetaModelUrls.url( "meta-model", metaModelVersion, "prefix-declarations.ttl" )
            .map( TurtleLoader::openUrl )
            .map( TurtleLoader::loadTurtle )
            .ifPresent( tryModel -> tryModel.forEach( targetModel::add ) );

      final String queryResult = sparqlExecutor.executeQuery( targetModel, boxmodelToDotQuery, "dotStatement" );
      final String template = getInputStreamAsString( "aspect2dot.mustache" );
      return template.replace( "{{&statements}}", queryResult )
            .replace( "{{&fontname}}", FONT_NAME )
            .replace( "\\\"", "\"" );
   }

   private Resource getAspect() {
      final SAMM SAMM = new SAMM( metaModelVersion );
      return model.listStatements( null, RDF.type, SAMM.Aspect() ).nextStatement().getSubject();
   }

   private String getAspectName() {
      final Resource aspect = getAspect();
      final AspectModelUrn aspectUrn = AspectModelUrn.fromUrn( aspect.getURI() );
      return aspectUrn.getName();
   }

   /**
    * Generates a diagram for the Aspect in the given output format and target language.
    *
    * @param outputFormat One of SVG, PNG or DOT
    * @param language The language for which the diagram should be generated
    * @param out The output stream the diagram is written to
    * @throws IOException if a write error occurs
    */
   public void generateDiagram( final Format outputFormat, final Locale language, final OutputStream out )
         throws IOException {
      final String dotResult = generateDot( language );

      switch ( outputFormat ) {
      case DOT:
         out.write( dotResult.getBytes( StandardCharsets.UTF_8 ) );
         break;
      case PNG:
         generatePng( dotResult, out );
         break;
      case SVG:
         generateSvg( dotResult, out );
         break;
      }
   }

   /**
    * Generates diagrams for each used language in an Aspect, in a given output format.
    * The callback function will be called with the name of the diagram, which follows the format
    * ASPECTNAME_XX.EXT where ASPECTNAME is the bamm:name of the Aspect, XX is the language tag
    * and EXT is the file extension for the respective output format.
    *
    * @param outputFormat One of SVG, PNG or DOT
    * @param nameMapper The callback function that maps diagram artifact names to OutputStreams
    * @throws IOException if a write error occurs
    */
   public void generateDiagrams( final Format outputFormat, final Function<String, OutputStream> nameMapper )
         throws IOException {
      for ( final Locale language : LanguageCollector.collectUsedLanguages( model ) ) {
         try ( final OutputStream outputStream = nameMapper
               .apply( outputFormat.getArtifactFilename( getAspectName(), language ) ) ) {
            generateDiagram( outputFormat, language, outputStream );
         }
      }
   }

   /**
    * Generates diagrams for an Aspect, in a given language and multiple output formats.
    * The callback function will be called with the name of the diagram, which follows the format
    * ASPECTNAME_XX.EXT where ASPECTNAME is the bamm:name of the Aspect, XX is the language tag
    * and EXT is the file extension for the respective output format.
    *
    * @param targetFormats The set of formats in which diagrams should be generated
    * @param language The language for which the diagram should be generated
    * @param nameMapper The callback function that maps diagram artifact names to OutputStreams
    * @throws IOException if a write error occurs
    */
   public void generateDiagrams( final Set<Format> targetFormats, final Locale language,
         final Function<String, OutputStream> nameMapper ) throws IOException {
      final String dotResult = generateDot( language );
      final String aspectName = getAspectName();

      for ( final Format format : targetFormats ) {
         try ( final OutputStream outputStream = nameMapper
               .apply( format.getArtifactFilename( aspectName, language ) ) ) {
            switch ( format ) {
            case DOT:
               outputStream.write( dotResult.getBytes( StandardCharsets.UTF_8.name() ) );
               break;
            case PNG:
               generatePng( dotResult, outputStream );
               break;
            case SVG:
               generateSvg( dotResult, outputStream );
               break;
            }
         }
      }
   }

   /**
    * Generates diagrams for each used language in an Aspect, in multiple output formats.
    * The callback function will be called with the name of the diagram, which follows the format
    * ASPECTNAME_XX.EXT where ASPECTNAME is the bamm:name of the Aspect, XX is the language tag
    * and EXT is the file extension for the respective output format.
    *
    * @param targetFormats The set of formats in which diagrams should be generated
    * @param nameMapper The callback function that maps diagram artifact names to OutputStreams
    * @throws IOException if a write error occurs
    */
   public void generateDiagrams( final Set<Format> targetFormats, final Function<String, OutputStream> nameMapper ) throws IOException {
      for ( final Locale language : LanguageCollector.collectUsedLanguages( model ) ) {
         generateDiagrams( targetFormats, language, nameMapper );
      }
   }
}
