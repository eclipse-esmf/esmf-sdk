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

import static org.graphper.api.Html.table;
import static org.graphper.api.Html.td;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.generator.DocumentGenerationException;
import org.eclipse.esmf.aspectmodel.generator.LanguageCollector;
import org.eclipse.esmf.metamodel.AspectContext;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.graphper.api.Graphviz;
import org.graphper.api.Html;
import org.graphper.api.Line;
import org.graphper.api.Node;
import org.graphper.api.attributes.Color;
import org.graphper.api.attributes.Labeljust;
import org.graphper.api.attributes.NodeShapeEnum;
import org.graphper.draw.ExecuteException;

/**
 * Generate SVG and PNG diagrams from Aspect Models
 */
public class AspectModelDiagramGenerator {
   public enum Format {
      PNG,
      SVG;

      public String getArtifactFilename( final String aspectName, final Locale language ) {
         return String.format( "%s_%s.%s", aspectName, language.toLanguageTag(), toString().toLowerCase() );
      }
   }

   private static final String FONT_NAME = "Roboto Condensed";
   private static final String FONT_FILE = "diagram/RobotoCondensed-Regular.ttf";

   private final AspectContext aspectContext;

   public AspectModelDiagramGenerator( final AspectContext aspectContext ) {
      this.aspectContext = aspectContext;
   }

   InputStream getInputStream( final String resource ) {
      return getClass().getClassLoader().getResourceAsStream( resource );
   }

   private void generatePng( final String svgInput, final OutputStream output ) {
      // To make the font available during PNG generation, it needs to be registered
      // in Java Runtime's graphics environment
      try {
         final File tmpFontFile = generateTmpFontFile();
         final Font f = Font.createFont( Font.TRUETYPE_FONT, tmpFontFile );
         final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         ge.registerFont( f );

         final String input = svgInput.replaceAll(
               "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">", "" );

         final TranscoderInput inputSvgImage = new TranscoderInput( new StringReader( input ) );
         final TranscoderOutput outputPngImage = new TranscoderOutput( output );
         final PNGTranscoder pngTranscoder = new PNGTranscoder();
         pngTranscoder.transcode( inputSvgImage, outputPngImage );
         output.flush();
         output.close();
      } catch ( final FontFormatException exception ) {
         // Will only happen if the loaded .ttf file is invalid
      } catch ( final IOException | TranscoderException exception ) {
         throw new DocumentGenerationException( exception );
      }
   }

   private File generateTmpFontFile() throws IOException {
      final File tempFontFile = new File( System.getProperty( "java.io.tmpdir" ) + File.separator + "aspect-model-diagram.tmp" );
      if ( !tempFontFile.exists() ) {
         try ( final InputStream fontStream = getInputStream( FONT_FILE );
               final OutputStream output = new FileOutputStream( tempFontFile, false ) ) {
            fontStream.transferTo( output );
         }
      }
      tempFontFile.deleteOnExit();
      return tempFontFile;
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

   public void generateSvg( final Locale language, final OutputStream out )
         throws IOException {
      final DiagramVisitor diagramVisitor = new DiagramVisitor( language );
      final AbstractDiagram diagram = aspectContext.aspect().accept( diagramVisitor, Optional.empty() );
      final Graphviz graphviz = render( diagram );

      try ( final InputStream fontStream = getInputStream( FONT_FILE ) ) {
         final String svgDocument = graphviz.toSvgStr().replaceAll( "Â«", "«" )
               .replaceAll( "Â»", "»" );

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
         final String result = svgDocument
               .replace( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "" )
               .replace( "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">", "" )
               .replaceFirst( ">", ">" + css );
         out.write( result.getBytes( StandardCharsets.UTF_8 ) );
      } catch ( final ExecuteException exception ) {
         throw new DocumentGenerationException( exception );
      }
   }

   /**
    * Generates a diagram for the Aspect in the given output format and target language.
    *
    * @param outputFormat One of SVG or PNG
    * @param language The language for which the diagram should be generated
    * @param out The output stream the diagram is written to
    * @throws DocumentGenerationException if diagram generation fails
    */
   public void generateDiagram( final Format outputFormat, final Locale language, final OutputStream out ) {
      final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      try {
         generateSvg( language, buffer );
         final String svgResult = buffer.toString( StandardCharsets.UTF_8 );

         switch ( outputFormat ) {
            case PNG -> generatePng( svgResult, out );
            case SVG -> IOUtils.copy( new ByteArrayInputStream( svgResult.getBytes() ), out );
         }
      } catch ( final IOException exception ) {
         throw new DocumentGenerationException( exception );
      }
   }

   /**
    * Generates diagrams for each used language in an Aspect, in a given output format.
    * The callback function will be called with the name of the diagram, which follows the format
    * ASPECTNAME_XX.EXT where ASPECTNAME is the samm:name of the Aspect, XX is the language tag
    * and EXT is the file extension for the respective output format.
    *
    * @param outputFormat One of SVG or PNG
    * @param nameMapper The callback function that maps diagram artifact names to OutputStreams
    * @throws IOException if a write error occurs
    */
   public void generateDiagrams( final Format outputFormat, final Function<String, OutputStream> nameMapper )
         throws IOException {
      for ( final Locale language : LanguageCollector.collectUsedLanguages( aspectContext.aspect() ) ) {
         try ( final OutputStream outputStream = nameMapper
               .apply( outputFormat.getArtifactFilename( aspectContext.aspect().getName(), language ) ) ) {
            generateDiagram( outputFormat, language, outputStream );
         }
      }
   }

   /**
    * Generates diagrams for an Aspect, in a given language and multiple output formats.
    * The callback function will be called with the name of the diagram, which follows the format
    * ASPECTNAME_XX.EXT where ASPECTNAME is the samm:name of the Aspect, XX is the language tag
    * and EXT is the file extension for the respective output format.
    *
    * @param targetFormats The set of formats in which diagrams should be generated
    * @param language The language for which the diagram should be generated
    * @param nameMapper The callback function that maps diagram artifact names to OutputStreams
    * @throws IOException if a write error occurs
    */
   public void generateDiagrams( final Set<Format> targetFormats, final Locale language,
         final Function<String, OutputStream> nameMapper ) throws IOException {
      final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      generateSvg( language, buffer );
      final String svgDocument = buffer.toString( StandardCharsets.UTF_8 );
      final String aspectName = aspectContext.aspect().getName();

      for ( final Format format : targetFormats ) {
         try ( final OutputStream outputStream = nameMapper.apply( format.getArtifactFilename( aspectName, language ) ) ) {
            switch ( format ) {
               case PNG -> generatePng( svgDocument, outputStream );
               case SVG -> outputStream.write( svgDocument.getBytes( StandardCharsets.UTF_8 ) );
            }
         }
      }
   }

   /**
    * Generates diagrams for each used language in an Aspect, in multiple output formats.
    * The callback function will be called with the name of the diagram, which follows the format
    * ASPECTNAME_XX.EXT where ASPECTNAME is the samm:name of the Aspect, XX is the language tag
    * and EXT is the file extension for the respective output format.
    *
    * @param targetFormats The set of formats in which diagrams should be generated
    * @param nameMapper The callback function that maps diagram artifact names to OutputStreams
    * @throws IOException if a write error occurs
    */
   public void generateDiagrams( final Set<Format> targetFormats, final Function<String, OutputStream> nameMapper ) throws IOException {
      for ( final Locale language : LanguageCollector.collectUsedLanguages( aspectContext.aspect() ) ) {
         generateDiagrams( targetFormats, language, nameMapper );
      }
   }

   private Graphviz render( final AbstractDiagram diagram ) {
      final Color bgColor = Color.ofRGB( "#cfdbed" );
      final String fontName = "Roboto Condensed";
      final Map<Box, Node> nodes = new HashMap<>();

      final Graphviz.GraphvizBuilder graphvizBuilder = Graphviz.digraph()
            .fontSize( 12f )
            .tempNode( Node.builder().shape( NodeShapeEnum.PLAIN ).build() );

      final Map<Box, Node> boxMap = diagram.getBoxes()
            .stream()
            .map( box -> {
               final Html.Table table = table()
                     .color( Color.BLACK ).bgColor( bgColor ).cellBorder( 0 ).border( 1 ).cellSpacing( 0 ).cellPadding( 4 )
                     .tr( td().text( "«" + box.getPrototype() + "»" ).fontName( fontName ).align( Labeljust.CENTER ) );
               if ( !box.getTitle().isEmpty() ) {
                  table.tr( td().text( box.getTitle() ).fontName( fontName ).align( Labeljust.CENTER ) );
               }
               if ( !box.getEntries().isEmpty() ) {
                  table.tr( td().cellPadding( 1 ).bgColor( Color.BLACK ).height( 1 ) );
                  box.getEntries()
                        .forEach( entry -> table.tr( td().cellPadding( 3 ).text( entry ).fontName( fontName ).align( Labeljust.LEFT ) ) );
               }
               final Node node = Node.builder().color( Color.BLACK ).table( table ).build();
               return new AbstractMap.SimpleEntry<>( box, node );
            } )
            .collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) );
      boxMap.values().forEach( graphvizBuilder::addNode );
      diagram.getEdges()
            .stream()
            .map( edge -> {
               final Node from = boxMap.get( edge.from() );
               final Node to = boxMap.get( edge.to() );
               return Line.builder( from, to )
                     .label( edge.label() )
                     .fontName( fontName )
                     .fontSize( 12 )
                     .build();
            } )
            .forEach( graphvizBuilder::addLine );
      return graphvizBuilder.build();
   }
}
