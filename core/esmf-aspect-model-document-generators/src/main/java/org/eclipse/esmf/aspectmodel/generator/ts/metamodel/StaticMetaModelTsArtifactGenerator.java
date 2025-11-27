/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.ts.metamodel;

import java.time.Year;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.eclipse.esmf.aspectmodel.generator.ArtifactGenerator;
import org.eclipse.esmf.aspectmodel.generator.TemplateEngine;
import org.eclipse.esmf.aspectmodel.generator.exception.CodeGenerationException;
import org.eclipse.esmf.aspectmodel.generator.ts.AspectModelTsUtil;
import org.eclipse.esmf.aspectmodel.generator.ts.StructuredValuePropertiesDeconstructor;
import org.eclipse.esmf.aspectmodel.generator.ts.TsArtifact;
import org.eclipse.esmf.aspectmodel.generator.ts.TsArtifactGenerator;
import org.eclipse.esmf.aspectmodel.generator.ts.TsCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.ts.TsFormatPretter;
import org.eclipse.esmf.metamodel.StructureElement;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMap;
import org.apache.jena.vocabulary.XSD;
import org.apache.velocity.runtime.RuntimeConstants;

/**
 * A {@link ArtifactGenerator} that generates static meta classes
 * for {@link StructureElement}s in Aspect models
 *
 * @param <E> the type to generate code for
 */
public class StaticMetaModelTsArtifactGenerator<E extends StructureElement> implements TsArtifactGenerator<E> {
   @Override
   public TsArtifact apply( final E element, final TsCodeGenerationConfig config ) {

      config.importTracker().clear();

      final CharMatcher matchHash = CharMatcher.is( '#' );
      final String modelUrnPrefix = element.urn().getUrnPrefix();
      final String characteristicBaseUrn = matchHash.trimTrailingFrom( SammNs.SAMMC.getNamespace() );

      final Map<String, Object> context = ImmutableMap.<String, Object> builder()
            .put( "characteristicBaseUrn", characteristicBaseUrn )
            .put( "codeGenerationConfig", config )
            .put( "deconstructor", new StructuredValuePropertiesDeconstructor( element ) )
            .put( "codeGeneratorName", AspectModelTsUtil.codeGeneratorName() )
            .put( "codeGeneratorDate", AspectModelTsUtil.CURRENT_DATE_ISO_8601 )
            .put( "context", new StaticCodeGenerationContext( config, modelUrnPrefix, characteristicBaseUrn, null, null, null ) )
            .put( "currentYear", Year.now() )
            .put( "element", element )
            .put( "elementUrn", element.isAnonymous() ? "" : element.urn() )
            .put( "localeEn", Locale.ENGLISH )
            .put( "modelVisitor", new StaticMetaModelVisitor() )
            .put( "modelUrnPrefix", modelUrnPrefix )
            .put( "nonNegativeInteger", new DefaultScalar( XSD.nonNegativeInteger.getURI() ) )
            .put( "util", AspectModelTsUtil.class )
            .build();

      final Properties engineConfiguration = new Properties();
      if ( config.executeLibraryMacros() ) {
         engineConfiguration.put( RuntimeConstants.VM_LIBRARY, config.templateLibFile().getName() );
         engineConfiguration.put( RuntimeConstants.FILE_RESOURCE_LOADER_PATH, config.templateLibFile().getParent() );
      }
      String generatedSource = new TemplateEngine( context, engineConfiguration ).apply( "/ts/ts-static-class" );

      if ( !config.disablePrettierFormatter() ) {
         generatedSource = TsFormatPretter.applyFormatter( generatedSource, config.prettierConfigPath() );
      }

      try {
         return new TsArtifact( generatedSource, "Meta" + AspectModelTsUtil.generateClassName( element, config ),
               config.packageName() );
      } catch ( final Exception exception ) {
         throw new CodeGenerationException( generatedSource, exception );
      }
   }
}
