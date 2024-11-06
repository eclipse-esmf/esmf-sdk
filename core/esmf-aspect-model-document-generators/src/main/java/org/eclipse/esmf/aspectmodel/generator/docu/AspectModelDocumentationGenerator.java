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

package org.eclipse.esmf.aspectmodel.generator.docu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.AspectModelHelper;
import org.eclipse.esmf.aspectmodel.generator.DocumentGenerationException;
import org.eclipse.esmf.aspectmodel.generator.Generator;
import org.eclipse.esmf.aspectmodel.generator.I18nLanguageBundle;
import org.eclipse.esmf.aspectmodel.generator.LanguageCollector;
import org.eclipse.esmf.aspectmodel.generator.TemplateEngine;
import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import org.eclipse.esmf.aspectmodel.visitor.AspectStreamTraversalVisitor;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Scalar;

import com.google.common.io.CharStreams;
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Asciidoc generator for a aspect model.
 */
public class AspectModelDocumentationGenerator extends Generator<String, String, DocumentationGenerationConfig, DocumentationArtifact> {
   public static final DocumentationGenerationConfig DEFAULT_CONFIG = DocumentationGenerationConfigBuilder.builder().build();
   private static final Logger LOG = LoggerFactory.getLogger( AspectModelDocumentationGenerator.class );
   private static final String DOCU_ROOT_DIR = "/docu";
   private static final String DOCU_TEMPLATE_ROOT_DIR = DOCU_ROOT_DIR + "/templates";
   private final Properties engineConfiguration;

   /**
    * @deprecated Replaced by {@link DocumentationGenerationConfig}
    */
   @Deprecated( forRemoval = true )
   public enum HtmlGenerationOption {
      /**
       * Set the CSS stylesheet to use in the generated HTML
       */
      STYLESHEET
   }

   public AspectModelDocumentationGenerator( final Aspect aspect ) {
      this( aspect, DEFAULT_CONFIG );
   }

   public AspectModelDocumentationGenerator( final Aspect aspect, final DocumentationGenerationConfig config ) {
      super( aspect, config );
      engineConfiguration = new Properties();
      engineConfiguration.put( RuntimeConstants.FILE_RESOURCE_LOADER_PATH, ".," + DOCU_TEMPLATE_ROOT_DIR + "/html" );
      engineConfiguration.put( "event_handler.reference_insertion.class", "org.apache.velocity.app.event.implement.EscapeHtmlReference" );
      engineConfiguration.put( RuntimeConstants.VM_LIBRARY,
            DOCU_TEMPLATE_ROOT_DIR + "/html/characteristic-documentation-lib.vm,"
                  + DOCU_TEMPLATE_ROOT_DIR + "/html/constraint-documentation-lib.vm,"
                  + DOCU_TEMPLATE_ROOT_DIR + "/html/diagram-documentation-lib.vm,"
                  + DOCU_TEMPLATE_ROOT_DIR + "/html/entity-documentation-lib.vm,"
                  + DOCU_TEMPLATE_ROOT_DIR + "/html/operation-documentation-lib.vm,"
                  + DOCU_TEMPLATE_ROOT_DIR + "/html/property-documentation-lib.vm,"
                  + DOCU_TEMPLATE_ROOT_DIR + "/html/common-documentation-lib.vm" );
   }

   @Override
   public Stream<DocumentationArtifact> generate() {
      final Map<String, Object> templateContext = new HashMap<>();
      templateContext.put( "aspectModel", aspect );
      templateContext.put( "aspectModelHelper", new AspectModelHelper() );
      templateContext.put( "Scalar", Scalar.class );

      final Set<Locale> targetLanguages = config.locale() == null
            ? LanguageCollector.collectUsedLanguages( aspect )
            : Set.of( Locale.ENGLISH );
      return targetLanguages.stream().map( language -> {
         logMissingTranslations( aspect, language );
         templateContext.put( "i18n", new I18nLanguageBundle( language ) );
         final TemplateEngine templateEngine = new TemplateEngine( templateContext, engineConfiguration );
         final String artifactName = getArtifactName( aspect, language );
         String source = templateEngine.apply( DOCU_ROOT_DIR + "/templates/html/aspect-model-documentation" );
         source = insertAspectModelDiagram( source, language );
         source = insertStaticPlaceholders( source );
         source = insertCustomCss( source );
         return new DocumentationArtifact( artifactName, source );
      } );
   }

   /**
    * Generates HTML documentation for Aspect Models. As this generation produces multiple documents
    * (one for each supported language), the generator provides the caller with the name of the document
    * (e.g., "FooAspect_en") via the callback function. The caller needs to provide an {@link OutputStream}
    * for the respective artifact, e.g. a suitable FileOutputStream.
    *
    * @param nameMapper The callback function that maps documentation artifact names to OutputStreams
    * @param generationOptions Additional optional options that control the document generation. See {@link
    * HtmlGenerationOption} for the usable keys.
    * @deprecated Use {@link #AspectModelDocumentationGenerator(Aspect, DocumentationGenerationConfig)} and {@link #generate()} instead
    */
   @Deprecated( forRemoval = true )
   public void generate( final Function<String, OutputStream> nameMapper, final Map<HtmlGenerationOption, String> generationOptions ) {
      final DocumentationGenerationConfig config = DocumentationGenerationConfigBuilder.builder()
            .stylesheet( generationOptions.get( HtmlGenerationOption.STYLESHEET ) )
            .build();
      new AspectModelDocumentationGenerator( aspect, config ).generate( nameMapper );
   }

   /**
    * Generates HTML documentation for Aspect Models. As this generation produces multiple documents
    * (one for each supported language), the generator provides the caller with the name of the document
    * (e.g., "FooAspect_en") via the callback function. The caller needs to provide an {@link OutputStream}
    * for the respective artifact, e.g. a suitable FileOutputStream.
    *
    * @param nameMapper The callback function that maps documentation artifact names to OutputStreams
    * @param generationOptions Additional optional options that control the document generation. See {@link
    * @param language The language for which a document should be generated
    * HtmlGenerationOption} for the usable keys.
    * @deprecated Use {@link #AspectModelDocumentationGenerator(Aspect, DocumentationGenerationConfig)} and {@link #generate()} instead
    */
   @Deprecated( forRemoval = true )
   public void generate( final Function<String, OutputStream> nameMapper, final Map<HtmlGenerationOption, String> generationOptions,
         final Locale language ) {
      final DocumentationGenerationConfig config = DocumentationGenerationConfigBuilder.builder()
            .stylesheet( generationOptions.get( HtmlGenerationOption.STYLESHEET ) )
            .locale( language )
            .build();
      new AspectModelDocumentationGenerator( aspect, config ).generate( nameMapper );
   }

   private String getArtifactName( final Aspect aspectModel, final Locale locale ) {
      return aspectModel.getName() + "_" + locale.toLanguageTag();
   }

   private String insertAspectModelDiagram( final String html, final Locale language ) {
      final AspectModelDiagramGenerator diagramGenerator = new AspectModelDiagramGenerator( aspect );
      final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      diagramGenerator.generateDiagram( AspectModelDiagramGenerator.Format.SVG, language, buffer );
      final String encodedImage = "data:image/svg+xml;base64," + Base64.getEncoder().encodeToString( buffer.toByteArray() );
      return html.replace( "diagram_svg_placeholder", encodedImage );
   }

   private String content( final String filePath ) {
      try ( final InputStream content = getClass().getResourceAsStream( DOCU_ROOT_DIR + filePath ) ) {
         if ( content == null ) {
            throw new DocumentGenerationException( "Could not read " + filePath );
         }
         return CharStreams.toString( new InputStreamReader( content ) );
      } catch ( final IOException exception ) {
         throw new DocumentGenerationException( "Could not read " + filePath );
      }
   }

   private String insertCustomCss( final String html ) {
      final String customCss = config.stylesheet();
      return html.replace( "custom_css_placeholder", customCss != null && !customCss.isEmpty()
            ? customCss
            : content( "/styles/default-aspect-docu-theme.css" ) );
   }

   private String insertStaticPlaceholders( final String html ) {
      String result = html;
      for ( final Map.Entry<String, String> entry : Map.of(
            "panzoom_js_placeholder", "/static/panzoom-9-4-2.min.js",
            "panzoom_license_placeholder", "/static/panzoom-license.txt",
            "tocbot_js_placeholder", "/static/tocbot-4-11-1.min.js",
            "tocbot_license_placeholder", "/static/tocbot-license.txt",
            "tocbot_css_placeholder", "/styles/tocbot-4-11-1.css",
            "tailwind_css_placeholder", "/styles/tailwind.purged.css",
            "tailwind_license_placeholder", "/static/tailwind-license.txt"
      ).entrySet() ) {
         result = result.replace( entry.getKey(), content( entry.getValue() ) );
      }
      return result;
   }

   private void logMissingTranslations( final Aspect aspect, final Locale locale ) {
      aspect.accept( new AspectStreamTraversalVisitor(), null )
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
