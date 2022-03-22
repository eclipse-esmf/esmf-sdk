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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;

import io.openmanufacturing.sds.aspectmodel.generator.AbstractGenerator;
import io.openmanufacturing.sds.aspectmodel.generator.AspectModelHelper;
import io.openmanufacturing.sds.aspectmodel.generator.I18nLanguageBundle;
import io.openmanufacturing.sds.aspectmodel.generator.LanguageCollector;
import io.openmanufacturing.sds.aspectmodel.generator.TemplateEngine;
import io.openmanufacturing.sds.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.IsDescribed;
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import io.openmanufacturing.sds.metamodel.visitor.AspectStreamTraversalVisitor;

/**
 * Asciidoc generator for a aspect model.
 */
public class AspectModelDocumentationGenerator extends AbstractGenerator {

   public enum HtmlGenerationOption {
      /**
       * Set the CSS stylesheet to use in the generated HTML
       */
      STYLESHEET
   }

   private static final Logger LOG = LoggerFactory.getLogger( AspectModelDocumentationGenerator.class );

   private static final String DOCU_ROOT_DIR = "/docu";
   private static final String DOCU_TEMPLATE_ROOT_DIR = DOCU_ROOT_DIR + "/templates";

   private enum Format {
      HTML,
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

   /**
    * Generates HTML documentation for Aspect Models. As this generation produces multiple documents
    * (one for each supported language), the generator provides the caller with the name of the document
    * (e.g., "FooAspect_en") via the callback function. The caller needs to provide an {@link OutputStream}
    * for the respective artifact, e.g. a suitable FileOutputStream.
    *
    * @param nameMapper        The callback function that maps documentation artifact names to OutputStreams
    * @param generationOptions Additional optional options that control the document generation. See {@link
    *                          HtmlGenerationOption} for the usable keys.
    * @throws IOException when serialization or deserialization fails
    */
   public void generate( final Function<String, OutputStream> nameMapper, final Map<HtmlGenerationOption, String> generationOptions ) throws IOException {
      final BufferingNameMapper bufferingMapper = new BufferingNameMapper();
      generateHtmlDocu( bufferingMapper, Format.NONE );
      generateInternal( nameMapper, generationOptions, bufferingMapper );
   }

   /**
    * Generates HTML documentation for Aspect Models. This version generates the document only for the language explicitly requested by the user.
    *
    * @param nameMapper        The callback function that maps documentation artifact names to OutputStreams
    * @param generationOptions Additional optional options that control the document generation. See {@link
    *                          HtmlGenerationOption} for the usable keys.
    * @param language          The language for which a document should be generated
    * @throws IOException when serialization or deserialization fails
    */
   public void generate( final Function<String, OutputStream> nameMapper, final Map<HtmlGenerationOption, String> generationOptions, final Locale language )
         throws IOException {
      final BufferingNameMapper bufferingMapper = new BufferingNameMapper();
      generateHtmlDocu( bufferingMapper, Format.NONE, language );
      generateInternal( nameMapper, generationOptions, bufferingMapper );
   }

   /**
    * Generates HTML documentation for Aspect Models. As this generation produces multiple documents
    * (one for each supported language), the generator provides the caller with the name of the document
    * (e.g., "FooAspect_en") via the callback function. The caller needs to provide an {@link OutputStream}
    * for the respective artifact, e.g. a suitable FileOutputStream.
    *
    * @param nameMapper        The callback function that maps documentation artifact names to OutputStreams
    * @param generationOptions Additional optional options that control the document generation. See {@link
    *                          HtmlGenerationOption} for the usable keys.
    * @throws IOException when serialization or deserialization fails
    */
   private void generateInternal( final Function<String, OutputStream> nameMapper, final Map<HtmlGenerationOption, String> generationOptions,
         final BufferingNameMapper bufferingMapper ) throws IOException {
      for ( final Map.Entry<String, ByteArrayOutputStream> entry : bufferingMapper.artifacts.entrySet() ) {
         final String artifactName = entry.getKey();
         final Locale artifactLanguage = bufferingMapper.artifactLanguages.get( artifactName );
         String html = byteArrayOutputStreamToString( entry.getValue() );
         html = insertAspectModelDiagram( html, artifactLanguage );
         html = insertPanZoomJs( html );
         html = insertPanZoomLicense( html );
         html = insertTocbotCSS( html );
         html = insertTocbotLicense( html );
         html = insertTocbotJs( html );
         html = insertTailwindCSS( html );
         html = insertTailwindLicense( html );
         html = insertCustomCSS( html, generationOptions.get( HtmlGenerationOption.STYLESHEET ) );

         try ( final OutputStream outputStreamForArtifactName = nameMapper
               .apply( Format.HTML.getArtifactFilename( artifactName ) ) ) {
            writeCharSequenceToOutputStream( html, outputStreamForArtifactName );
         } catch ( final IOException e ) {
            LOG.error( "Failure on writing generated HTML", e );
         }
      }
   }

   private void generateHtmlDocu( final Function<String, OutputStream> nameMapper, final Format format, final Locale desiredLanguage ) {
      final Set<Locale> languagesInModel = LanguageCollector.collectUsedLanguages( aspect );
      if ( !languagesInModel.contains( desiredLanguage ) ) {
         throw new RuntimeException( String.format( "The model does not contain the desired language: %s.", desiredLanguage.toString() ) );
      }
      final Set<Locale> selectedLanguage = Set.of( desiredLanguage );
      generateHtmlDocu( nameMapper, format, selectedLanguage );
   }

   private void generateHtmlDocu( final Function<String, OutputStream> nameMapper, final Format format ) {
      final Set<Locale> languagesInModel = LanguageCollector.collectUsedLanguages( aspect );
      final Set<Locale> languages = languagesInModel.isEmpty() ? Set.of( Locale.ENGLISH ) : languagesInModel;
      generateHtmlDocu( nameMapper, format, languages );
   }

   private void generateHtmlDocu( final Function<String, OutputStream> nameMapper, final Format format, final Set<Locale> languages ) {
      final Map<String, Object> context = new HashMap<>();
      context.put( "aspectModel", aspect );
      context.put( "aspectModelHelper", new AspectModelHelper( versionedModel ) );

      final Properties engineConfiguration = new Properties();
      engineConfiguration.put( "file.resource.loader.path", ".," + DOCU_TEMPLATE_ROOT_DIR + "/html" );
      engineConfiguration.put( "event_handler.reference_insertion.class", "org.apache.velocity.app.event.implement.EscapeHtmlReference" );
      engineConfiguration.put( "velocimacro.library",
            DOCU_TEMPLATE_ROOT_DIR + "/html/characteristic-documentation-lib.vm," +
                  DOCU_TEMPLATE_ROOT_DIR + "/html/constraint-documentation-lib.vm," +
                  DOCU_TEMPLATE_ROOT_DIR + "/html/diagram-documentation-lib.vm," +
                  DOCU_TEMPLATE_ROOT_DIR + "/html/entity-documentation-lib.vm," +
                  DOCU_TEMPLATE_ROOT_DIR + "/html/operation-documentation-lib.vm," +
                  DOCU_TEMPLATE_ROOT_DIR + "/html/property-documentation-lib.vm," +
                  DOCU_TEMPLATE_ROOT_DIR + "/html/common-documentation-lib.vm" );

      final Predicate<Locale> byLanguage = locale -> selectedLanguage == null || locale.getLanguage().equals( selectedLanguage.getLanguage() );
      languages.stream().filter( byLanguage ).forEach( language -> {

         logMissingTranslations( aspect, language );

         context.put( "i18n", new I18nLanguageBundle( language ) );
         context.put( "Scalar", Scalar.class );
         final TemplateEngine templateEngine = new TemplateEngine( context, engineConfiguration );

         final String artifactName = getArtifactName( aspect, language );
         if ( nameMapper instanceof BufferingNameMapper ) {
            ((BufferingNameMapper) nameMapper).setLanguageForArtifact( artifactName, language );
         }

         try ( final OutputStream outputStream = nameMapper.apply( format.getArtifactFilename( artifactName ) ) ) {
            final String generatedSource = templateEngine.apply( DOCU_ROOT_DIR + "/templates/html/aspect-model-documentation" );
            writeCharSequenceToOutputStream( generatedSource, outputStream );
         } catch ( final IOException e ) {
            LOG.error( "Failure on writing generated Asciidoc", e );
         }
      } );
   }

   public String getArtifactName( final Aspect aspectModel, final Locale locale ) {
      return aspectModel.getName() + "_" + locale.toLanguageTag();
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

   private String byteArrayOutputStreamToString( final ByteArrayOutputStream outputStream ) throws IOException {
      try ( final ByteArrayOutputStream stream = outputStream ) {
         return stream.toString( StandardCharsets.UTF_8.name() );
      } catch ( final UnsupportedEncodingException e ) {
         // Will not happen, because encoding is hardcoded
         throw new RuntimeException( e );
      }
   }

   private String insertAspectModelDiagram( final String html, final Locale language ) throws IOException {
      final AspectModelDiagramGenerator diagramGenerator = new AspectModelDiagramGenerator( versionedModel );
      final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      diagramGenerator.generateDiagram( AspectModelDiagramGenerator.Format.SVG, language, buffer );
      final String encodedImage = "data:image/svg+xml;base64," +
            Base64.getEncoder().encodeToString( buffer.toByteArray() );
      return html.replace( "diagram_svg_placeholder", encodedImage );
   }

   private String insertCustomCSS( final String html, final String customCSS ) throws IOException {
      if ( customCSS != null && !customCSS.isEmpty() ) {
         return html.replace( "custom_css_placeholder", customCSS );
      }
      try ( final InputStream ompCSS = getClass().getResourceAsStream( DOCU_ROOT_DIR + "/styles/omp-aspect-docu-theme.css" ) ) {
         final String defaultCSS = CharStreams.toString( new InputStreamReader( ompCSS ) );
         return html.replace( "custom_css_placeholder", defaultCSS );
      }
   }

   private String insertPanZoomJs( final String html ) throws IOException {
      try ( final InputStream panZoomJs = getClass().getResourceAsStream(
            DOCU_ROOT_DIR + "/static/panzoom-9-4-2.min.js" ) ) {
         final String javaScript = CharStreams.toString( new InputStreamReader( panZoomJs ) );
         return html.replace( "panzoom_js_placeholder", javaScript );
      }
   }

   private String insertPanZoomLicense( final String html ) throws IOException {
      try ( final InputStream panZoomJs = getClass().getResourceAsStream(
            DOCU_ROOT_DIR + "/static/panzoom-license.txt" ) ) {
         final String javaScript = CharStreams.toString( new InputStreamReader( panZoomJs ) );
         return html.replace( "panzoom_license_placeholder", javaScript );
      }
   }

   private String insertTocbotJs( final String html ) throws IOException {
      try ( final InputStream tocbot = getClass().getResourceAsStream(
            DOCU_ROOT_DIR + "/static/tocbot-4-11-1.min.js" ) ) {
         final String javaScript = CharStreams.toString( new InputStreamReader( tocbot ) );
         return html.replace( "tocbot_js_placeholder", javaScript );
      }
   }

   private String insertTocbotLicense( final String html ) throws IOException {
      try ( final InputStream panZoomJs = getClass().getResourceAsStream(
            DOCU_ROOT_DIR + "/static/tocbot-license.txt" ) ) {
         final String javaScript = CharStreams.toString( new InputStreamReader( panZoomJs ) );
         return html.replace( "tocbot_license_placeholder", javaScript );
      }
   }

   private String insertTocbotCSS( final String html ) throws IOException {
      try ( final InputStream tailwindCss = getClass().getResourceAsStream(
            DOCU_ROOT_DIR + "/styles/tocbot-4-11-1.css" ) ) {
         final String css = CharStreams.toString( new InputStreamReader( tailwindCss ) );
         return html.replace( "tocbot_css_placeholder", css );
      }
   }

   private String insertTailwindCSS( final String html ) throws IOException {
      try ( final InputStream tailwindCss = getClass().getResourceAsStream(
            DOCU_ROOT_DIR + "/styles/tailwind.purged.css" ) ) {
         final String css = CharStreams.toString( new InputStreamReader( tailwindCss ) );
         return html.replace( "tailwind_css_placeholder", css );
      }
   }

   private String insertTailwindLicense( final String html ) throws IOException {
      try ( final InputStream panZoomJs = getClass().getResourceAsStream(
            DOCU_ROOT_DIR + "/static/tailwind-license.txt" ) ) {
         final String javaScript = CharStreams.toString( new InputStreamReader( panZoomJs ) );
         return html.replace( "tailwind_license_placeholder", javaScript );
      }
   }

   private void logMissingTranslations( final Aspect aspectMetaModel, final Locale locale ) {
      aspectMetaModel.accept( new AspectStreamTraversalVisitor(), null )
            .filter( element -> element instanceof IsDescribed )
            .map( element -> (IsDescribed) element )
            .forEach( modelElement -> {
               final boolean hasPreferredNameWithLocale = modelElement.getPreferredNames().stream()
                     .anyMatch( preferredName -> preferredName.getLanguageTag().equals( locale ) );
               if ( !modelElement.getPreferredNames().isEmpty() && !hasPreferredNameWithLocale ) {
                  LOG.warn( "Missing preferred name for {} with locale {}", modelElement.getName(), locale );
               }

               final boolean hasDescriptionWithLocale = modelElement.getDescriptions().stream()
                     .anyMatch( description -> description.getLanguageTag().equals( locale ) );
               if ( !modelElement.getDescriptions().isEmpty() && !hasDescriptionWithLocale ) {
                  LOG.warn( "Missing description for {} with locale {}", modelElement.getName(), locale );
               }
            } );
   }
}
