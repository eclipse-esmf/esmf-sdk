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

package org.eclipse.esmf.aspectmodel.generator.ts.pojo;

import java.time.Year;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.generator.ArtifactGenerator;
import org.eclipse.esmf.aspectmodel.generator.TemplateEngine;
import org.eclipse.esmf.aspectmodel.generator.exception.CodeGenerationException;
import org.eclipse.esmf.aspectmodel.generator.ts.AspectModelTsUtil;
import org.eclipse.esmf.aspectmodel.generator.ts.StructuredValuePropertiesDeconstructor;
import org.eclipse.esmf.aspectmodel.generator.ts.TsArtifact;
import org.eclipse.esmf.aspectmodel.generator.ts.TsArtifactGenerator;
import org.eclipse.esmf.aspectmodel.generator.ts.TsCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.ts.TsFormatPretter;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.StructureElement;

import com.google.common.collect.ImmutableMap;
import org.apache.velocity.runtime.RuntimeConstants;

/**
 * A {@link ArtifactGenerator} that generates Ts Pojo code
 * for {@link StructureElement}s
 *
 * @param <E> the element type
 */
public class StructureElementTsArtifactGenerator<E extends StructureElement> implements TsArtifactGenerator<E> {
   private final Set<ComplexType> extendingEntities;

   public StructureElementTsArtifactGenerator() {
      extendingEntities = Set.of();
   }

   public StructureElementTsArtifactGenerator( final Set<ComplexType> extendingEntitiesInModel ) {
      extendingEntities = extendingEntitiesInModel;
   }

   // Needs to instantiate XSD in order to use Velocity's FieldMethodizer
   @SuppressWarnings( { "squid:S2440", "InstantiationOfUtilityClass" } )
   @Override
   public TsArtifact apply( final E element, final TsCodeGenerationConfig config ) {

      config.importTracker().clear();

      final Map<String, Object> context = ImmutableMap.<String, Object> builder()
            .put( "codeGenerationConfig", config )
            .put( "currentYear", Year.now() )
            .put( "codeGeneratorName", AspectModelTsUtil.codeGeneratorName() )
            .put( "codeGeneratorDate", AspectModelTsUtil.CURRENT_DATE_ISO_8601 )
            .put( "element", element )
            .put( "elementUrn", element.isAnonymous() ? "" : element.urn() )
            .put( "localeEn", Locale.ENGLISH )
            .put( "extendingEntities", extendingEntities )
            .put( "util", AspectModelTsUtil.class )
            .put( "deconstructor", new StructuredValuePropertiesDeconstructor( element ) )
            .build();

      final Properties engineConfiguration = new Properties();
      if ( config.executeLibraryMacros() ) {
         engineConfiguration.put( RuntimeConstants.VM_LIBRARY, config.templateLibFile().getName() );
         engineConfiguration.put( RuntimeConstants.FILE_RESOURCE_LOADER_PATH, config.templateLibFile().getParent() );
      }

      String generatedSource = new TemplateEngine( context, engineConfiguration ).apply( "/ts/ts-pojo" );

      if ( !config.disablePrettierFormatter() ) {
         generatedSource = TsFormatPretter.applyFormatter( generatedSource, config.prettierConfigPath() );
      }

      try {
         return new TsArtifact( generatedSource, AspectModelTsUtil.generateClassName( element, config ),
               config.packageName() );
      } catch ( final Exception exception ) {
         throw new CodeGenerationException( generatedSource, exception );
      }
   }
}
