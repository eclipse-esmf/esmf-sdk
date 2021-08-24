/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.generator.docu;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.generator.AbstractGenerator;
import io.openmanufacturing.sds.aspectmodel.generator.AspectModelUtil;
import io.openmanufacturing.sds.aspectmodel.generator.I18nLanguageBundle;
import io.openmanufacturing.sds.aspectmodel.generator.LanguageCollector;
import io.openmanufacturing.sds.aspectmodel.generator.TemplateEngine;
import io.openmanufacturing.sds.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.IsDescribed;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import io.openmanufacturing.sds.metamodel.visitor.AspectStreamTraversalVisitor;

import com.google.common.io.CharStreams;

/**
 * Asciidoc generator for a aspect model.
 */
public class AspectModelDocumentationGenerator extends AbstractGenerator {

   public enum HtmlGenerationOption {
      /**
       * Set the CSS stylesheet to use in the generated HTML
       */
      STYLESHEET,

      /**
       * Set the URI of the icon font to use
       */
      ICON_FONT_URI,

      /**
       * Set the path to the doc info HTML
       */
      DOC_INFO_HTML,

      /**
       * Set the path to the license txt file
       */
      LICENSE_TXT
   }

   private static final Logger LOG = LoggerFactory.getLogger( AspectModelDocumentationGenerator.class );

   private static final String DOCU_ROOT_DIR = "/docu";
   private static final String DOCU_TEMPLATE_ROOT_DIR = DOCU_ROOT_DIR + "/templates";

   private enum Format {
      HTML,
      ADOC,
      NONE;

      public String getArtifactFilename( final String artifactName ) {
         if ( Format.NONE.equals( this ) ) {
            return artifactName;
         }
         return String.format( "%s.%s", artifactName, toString().toLowerCase() );
      }
   }

   private final Aspect aspect;
   private final VersionedModel versionedModel;
   private Locale selectedLanguage = null;

   public AspectModelDocumentationGenerator( final VersionedModel versionedModel ) {
      this.versionedModel = versionedModel;
      aspect = AspectModelLoader.fromVersionedModelUnchecked( versionedModel );
   }

   public AspectModelDocumentationGenerator( final String language, final VersionedModel versionedModel ) {
      this( versionedModel );
      selectedLanguage = Locale.forLanguageTag( language );
   }

   private void generateAsciidoc( final Function<String, OutputStream> nameMapper, final Format format ) {
      final Set<Locale> languagesInModel = LanguageCollector.collectUsedLanguages( aspect );
      final Set<Locale> languages = languagesInModel.isEmpty() ? Set.of( Locale.ENGLISH ) : languagesInModel;

      final Map<String, Object> context = new HashMap<>();
      context.put( "aspectModel", aspect );
      context.put( "aspectModelUtil", AspectModelUtil.class );

      final Properties engineConfiguration = new Properties();
      engineConfiguration.put( "file.resource.loader.path", ".," + DOCU_TEMPLATE_ROOT_DIR + "/asciidoc" );
      engineConfiguration.put( "velocimacro.library",
            DOCU_TEMPLATE_ROOT_DIR + "/asciidoc/characteristic-documentation-lib.vm," +
                  DOCU_TEMPLATE_ROOT_DIR + "/asciidoc/constraint-documentation-lib.vm," +
                  DOCU_TEMPLATE_ROOT_DIR + "/asciidoc/diagram-documentation-lib.vm," +
                  DOCU_TEMPLATE_ROOT_DIR + "/asciidoc/entity-documentation-lib.vm," +
                  DOCU_TEMPLATE_ROOT_DIR + "/asciidoc/operation-documentation-lib.vm," +
                  DOCU_TEMPLATE_ROOT_DIR + "/asciidoc/property-documentation-lib.vm," +
                  DOCU_TEMPLATE_ROOT_DIR + "/asciidoc/common-documentation-lib.vm" );

      final Predicate<Locale> byLanguage = locale -> selectedLanguage == null || locale.getLanguage().equals(
            selectedLanguage.getLanguage() );
      languages.stream().filter( byLanguage ).forEach( language -> {

         logMissingTranslations( aspect, language );

         context.put( "i18n", new I18nLanguageBundle( language ) );
         final TemplateEngine templateEngine = new TemplateEngine( context, engineConfiguration );

         final String artifactName = getArtifactName( aspect, language );
         if ( nameMapper instanceof BufferingNameMapper ) {
            ((BufferingNameMapper) nameMapper).setLanguageForArtifact( artifactName, language );
         }

         try ( final OutputStream asciiDocOutputStream = nameMapper
               .apply( format.getArtifactFilename( artifactName ) ) ) {

            final String generatedSource = templateEngine
                  .apply( DOCU_ROOT_DIR + "/templates/asciidoc/aspect-model-documentation" );

            writeCharSequenceToOutputStream( generatedSource, asciiDocOutputStream );
         } catch ( final IOException e ) {
            LOG.error( "Failure on writing generated Asciidoc", e );
         }
      } );
   }

   public String getArtifactName( final Aspect aspectModel, final Locale locale ) {
      return aspectModel.getName() + "_" + locale.toLanguageTag();
   }

   /**
    * Generates Asciidoc documentation for Aspect Models. As this generation produces multiple documents
    * (one for each supported language), the generator provides the caller with the name of the document
    * (e.g., "FooAspect_en") via the callback function. The caller needs to provide an {@link java.io.OutputStream}
    * for the respective artifact, e.g. a suitable FileOutputStream.
    *
    * @param nameMapper The callback function that maps documentation artifact names to OutputStreams
    */
   public void generateAsciidoc( final Function<String, OutputStream> nameMapper ) {
      generateAsciidoc( nameMapper, Format.ADOC );
   }

   private static class BufferingNameMapper implements Function<String, OutputStream> {
      private final Map<String, ByteArrayOutputStream> artifacts = new HashMap<>();
      private final Map<String, Locale> artifactLanguages = new HashMap<>();

      public void setLanguageForArtifact( final String artifactName, final Locale language ) {
         artifactLanguages.put( artifactName, language );
      }

      @Override
      public OutputStream apply( final String artifactName ) {
         final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         artifacts.put( artifactName, outputStream );
         return outputStream;
      }
   }

   @SuppressWarnings( "squid:S1166" )
   private String byteArrayOutputStreamToString( final ByteArrayOutputStream outputStream ) throws IOException {
      try ( final ByteArrayOutputStream stream = outputStream ) {
         return stream.toString( StandardCharsets.UTF_8.name() );
      } catch ( final UnsupportedEncodingException e ) {
         // Will not happen, because encoding is hardcoded
      }
      return null;
   }

   /**
    * Generates HTML documentation for Aspect Models. As this generation produces multiple documents
    * (one for each supported language), the generator provides the caller with the name of the document
    * (e.g., "FooAspect_en") via the callback function. The caller needs to provide an {@link java.io.OutputStream}
    * for the respective artifact, e.g. a suitable FileOutputStream.
    *
    * @param nameMapper The callback function that maps documentation artifact names to OutputStreams
    * @throws IOException when serialization or deserialization fails
    */
   public void generateHtml( final Function<String, OutputStream> nameMapper ) throws IOException {
      generateHtml( nameMapper, Collections.emptyMap() );
   }

   /**
    * Generates HTML documentation for Aspect Models. As this generation produces multiple documents
    * (one for each supported language), the generator provides the caller with the name of the document
    * (e.g., "FooAspect_en") via the callback function. The caller needs to provide an {@link java.io.OutputStream}
    * for the respective artifact, e.g. a suitable FileOutputStream.
    *
    * @param nameMapper The callback function that maps documentation artifact names to OutputStreams
    * @param generationOptions Additional optional options that control the document generation. See {@link
    *       HtmlGenerationOption}
    *       for the usable keys.
    * @throws IOException when serialization or deserialization fails
    */
   public void generateHtml( final Function<String, OutputStream> nameMapper,
         final Map<HtmlGenerationOption, String> generationOptions ) throws IOException {
      final BufferingNameMapper bufferingMapper = new BufferingNameMapper();
      generateAsciidoc( bufferingMapper, Format.NONE );

      final Asciidoctor asciidoctor = Asciidoctor.Factory.create();
      java.util.logging.Logger.getLogger( "asciidoctor" ).setUseParentHandlers( false );

      for ( final Map.Entry<String, ByteArrayOutputStream> entry : bufferingMapper.artifacts.entrySet() ) {
         final String artifactName = entry.getKey();

         final String asciidoc = byteArrayOutputStreamToString( entry.getValue() );
         final Locale artifactLanguage = bufferingMapper.artifactLanguages.get( artifactName );

         final String defaultDocInfoPath = System.getProperty( "user.home" ) + "/aspect-doc";
         final String docInfoPath = generationOptions.getOrDefault( HtmlGenerationOption.DOC_INFO_HTML,
               defaultDocInfoPath ).replaceAll( "/$", "" );

         final String licensePath = generationOptions
               .getOrDefault( HtmlGenerationOption.LICENSE_TXT, docInfoPath ).replaceAll( "/$", "" );

         final Attributes attributes = new Attributes();
         attributes.setStylesDir( docInfoPath + "/styles" );
         attributes.setStyleSheetName(
               generationOptions.getOrDefault( HtmlGenerationOption.STYLESHEET, "all.css" ) );
         copyResourceToBaseDir( DOCU_ROOT_DIR + "/styles/all.css", docInfoPath + "/styles/all.css" );

         attributes.setBackend( "html5" );
         attributes.setLinkCss( false );
         attributes.setDataUri( true );
         attributes.setNoFooter( true );
         attributes.setIconFontRemote( false );
         attributes.setCopyCss( true );

         final Options options = new Options();
         options.setAttributes( attributes );
         options.setHeaderFooter( true );
         options.setSafe( SafeMode.UNSAFE );
         options.setBaseDir( docInfoPath );
         copyResourceToBaseDir( DOCU_ROOT_DIR + "/docinfo.html", docInfoPath + "/docinfo.html" );
         copyResourceToBaseDir( DOCU_ROOT_DIR + "/docinfo-footer.html", docInfoPath + "/docinfo-footer.html" );
         copyResourceToBaseDir( DOCU_ROOT_DIR + "/static/panzoom-license.txt",
               licensePath + "/static/panzoom-license.txt" );
         final String html = insertAspectModelDiagram( asciidoctor.convert( asciidoc, options ), artifactLanguage );
         final String htmlInclZoom = insertPanZoomJs( html );
         try ( final OutputStream outputStreamForArtifactName = nameMapper
               .apply( Format.HTML.getArtifactFilename( artifactName ) ) ) {
            writeCharSequenceToOutputStream( htmlInclZoom, outputStreamForArtifactName );
         } catch ( final IOException e ) {
            LOG.error( "Failure on writing generated HTML", e );
         }
      }
   }

   private void copyResourceToBaseDir( final String source, final String target ) {
      final File targetFile = new File( target );
      if ( targetFile.exists() ) {
         try {
            Files.delete( targetFile.toPath() );
         } catch ( final IOException e ) {
            LOG.error( "Can not delete File: {}.", targetFile.getName(), e );
         }
      }
      try ( final InputStream resourceAsStream = getClass().getResourceAsStream( source ) ) {
         FileUtils.copyInputStreamToFile( resourceAsStream, targetFile );
      } catch ( final IOException e ) {
         LOG.error( "Can not copy file from resource: {}", source, e );
      }
   }

   private String insertAspectModelDiagram( final String html, final Locale language ) throws IOException {
      final AspectModelDiagramGenerator diagramGenerator = new AspectModelDiagramGenerator( versionedModel );
      final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      diagramGenerator.generateDiagram( AspectModelDiagramGenerator.Format.SVG, language, buffer );
      final String encodedImage = "<img id='aspect_model_diagram_img' src=\"data:image/svg+xml;base64," +
            Base64.getEncoder().encodeToString( buffer.toByteArray() );
      return html.replaceAll( "<img src=\"data:image/svg\\+xml;base64,", encodedImage );
   }

   private String insertPanZoomJs( final String html ) throws IOException {
      try ( final InputStream panZoomJs = getClass().getResourceAsStream( DOCU_ROOT_DIR + "/static/panzoom.min.js" ) ) {
         final String javaScript = CharStreams.toString( new InputStreamReader( panZoomJs ) );
         return html.replace( "panzoom_js_placeholder", javaScript );
      }
   }

   private void logMissingTranslations( final Aspect aspectMetaModel, final Locale locale ) {
      aspectMetaModel.accept( new AspectStreamTraversalVisitor(), null )
                     .filter( element -> element instanceof IsDescribed )
                     .map( element -> (IsDescribed) element )
                     .forEach( metaModelElement -> {
                        final Map<Locale, String> preferredNames = metaModelElement.getPreferredNames();
                        if ( !preferredNames.isEmpty() && !preferredNames.containsKey( locale ) ) {
                           LOG.warn( "Missing preferred names for {} with locale {}", metaModelElement.getName(),
                                 locale );
                        }

                        final Map<Locale, String> descriptions = metaModelElement.getDescriptions();
                        if ( !descriptions.isEmpty() && !descriptions.containsKey( locale ) ) {
                           LOG.warn( "Missing description for {} with locale {}", metaModelElement.getName(), locale );
                        }
                     } );
   }
}
