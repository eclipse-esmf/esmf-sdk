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
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.AspectGenerator;
import org.eclipse.esmf.aspectmodel.generator.DocumentGenerationException;
import org.eclipse.esmf.aspectmodel.generator.LanguageCollector;
import org.eclipse.esmf.metamodel.Aspect;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
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
public class AspectModelDiagramGenerator extends AspectGenerator<String, byte[], DiagramGenerationConfig, DiagramArtifact> {
   public static final DiagramGenerationConfig DEFAULT_CONFIG = DiagramGenerationConfigBuilder.builder().build();
   private static final String FONT_NAME = "Roboto Condensed";
   private static final String FONT_FILE = "diagram/RobotoCondensed-Regular.ttf";

   public AspectModelDiagramGenerator( final Aspect aspect ) {
      this( aspect, DEFAULT_CONFIG );
   }

   public AspectModelDiagramGenerator( final Aspect aspect, final DiagramGenerationConfig config ) {
      super( aspect, config );
   }

   @Override
   public Stream<DiagramArtifact> generate() {
      final Set<Locale> targetLanguages = config.language() == null
            ? LanguageCollector.collectUsedLanguages( aspect() )
            : Set.of( config.language() );
      return targetLanguages.stream().map( language -> {
         final String artifactName = "%s_%s.%s".formatted( aspect().getName(), language.toLanguageTag(),
               config.format().toString().toLowerCase() );
         final String svg = generateSvg();
         final byte[] content = config.format() == DiagramGenerationConfig.Format.SVG
               ? svg.getBytes( StandardCharsets.UTF_8 )
               : generatePng( svg );
         return new DiagramArtifact( artifactName, content, language );
      } );
   }

   private InputStream getInputStream( final String resource ) {
      return getClass().getClassLoader().getResourceAsStream( resource );
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

   private String generateSvg() {
      final DiagramVisitor diagramVisitor = new DiagramVisitor( config.language() );
      final Diagram diagram = aspect().accept( diagramVisitor, Optional.empty() );
      final Graphviz graphviz = render( diagram );

      try ( final InputStream fontStream = getInputStream( FONT_FILE ) ) {
         final String svgDocument = graphviz.toSvgStr()
               .replace( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "" )
               .replace( "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">", "" );

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
         return svgDocument.replaceFirst( ">", ">" + css );
      } catch ( final ExecuteException | IOException exception ) {
         throw new DocumentGenerationException( exception );
      }
   }

   private byte[] generatePng( final String svg ) {
      // To make the font available during PNG generation, it needs to be registered
      // in Java Runtime's graphics environment
      try {
         final File tmpFontFile = generateTmpFontFile();
         final Font f = Font.createFont( Font.TRUETYPE_FONT, tmpFontFile );
         final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         ge.registerFont( f );

         final String input = svg.replaceAll(
               "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">", "" );

         final TranscoderInput inputSvgImage = new TranscoderInput( new StringReader( input ) );
         final ByteArrayOutputStream output = new ByteArrayOutputStream();
         final TranscoderOutput outputPngImage = new TranscoderOutput( output );
         final PNGTranscoder pngTranscoder = new PNGTranscoder();
         pngTranscoder.transcode( inputSvgImage, outputPngImage );
         output.flush();
         output.close();
         return output.toByteArray();
      } catch ( final FontFormatException exception ) {
         // Will only happen if the loaded .ttf file is invalid
         throw new DocumentGenerationException( exception );
      } catch ( final IOException | TranscoderException exception ) {
         throw new DocumentGenerationException( exception );
      }
   }

   private Graphviz render( final Diagram diagram ) {
      final String fontName = "Roboto Condensed";

      final Graphviz.GraphvizBuilder graphvizBuilder = Graphviz.digraph()
            .fontSize( 12f )
            .tempNode( Node.builder().shape( NodeShapeEnum.PLAIN ).build() );

      final Map<Diagram.Box, Node> boxMap = diagram.getBoxes()
            .stream()
            .map( box -> {
               final Html.Table table = table()
                     .color( Color.BLACK ).bgColor( Color.ofRGB( box.getBackground().getColor() ) )
                     .cellBorder( 0 ).border( 1 ).cellSpacing( 0 ).cellPadding( 4 )
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
