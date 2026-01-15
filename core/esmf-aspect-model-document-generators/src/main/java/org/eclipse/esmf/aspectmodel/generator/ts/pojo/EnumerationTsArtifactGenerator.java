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

import org.eclipse.esmf.aspectmodel.generator.ArtifactGenerator;
import org.eclipse.esmf.aspectmodel.generator.TemplateEngine;
import org.eclipse.esmf.aspectmodel.generator.exception.CodeGenerationException;
import org.eclipse.esmf.aspectmodel.generator.ts.AspectModelTsUtil;
import org.eclipse.esmf.aspectmodel.generator.ts.TsArtifact;
import org.eclipse.esmf.aspectmodel.generator.ts.TsArtifactGenerator;
import org.eclipse.esmf.aspectmodel.generator.ts.TsCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.ts.TsFormatPretter;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.characteristic.State;

import com.google.common.collect.ImmutableMap;
import org.apache.velocity.runtime.RuntimeConstants;

/**
 * A {@link ArtifactGenerator} that generates Ts Pojo code
 * for {@link Enumeration}s
 *
 * @param <E> the element type
 */
public class EnumerationTsArtifactGenerator<E extends Enumeration> implements TsArtifactGenerator<E> {
   @Override
   public TsArtifact apply( final E element, final TsCodeGenerationConfig config ) {
      config.importTracker().clear();

      final Map<String, Object> context = ImmutableMap.<String, Object> builder()
            .put( "className", element.getName() )
            .put( "codeGenerationConfig", config )
            .put( "codeGeneratorName", AspectModelTsUtil.codeGeneratorName() )
            .put( "codeGeneratorDate", AspectModelTsUtil.CURRENT_DATE_ISO_8601 )
            .put( "currentYear", Year.now() )
            .put( "dataType", AspectModelTsUtil.getDataType( element.getDataType(), config.importTracker(), config ) )
            .put( "elementUrn", element.isAnonymous() ? "" : element.urn() )
            .put( "enumeration", element )
            .put( "importTracker", config.importTracker() )
            .put( "localeEn", Locale.ENGLISH )
            .put( "State", State.class )
            .put( "util", AspectModelTsUtil.class )
            .build();

      try {
         final Properties engineConfiguration = new Properties();
         if ( config.executeLibraryMacros() ) {
            engineConfiguration.put( RuntimeConstants.VM_LIBRARY, config.templateLibFile().getName() );
            engineConfiguration.put( RuntimeConstants.FILE_RESOURCE_LOADER_PATH, config.templateLibFile().getParent() );
         }

         String generatedSource = new TemplateEngine( context, engineConfiguration ).apply( "/ts/ts-enumeration" );

         if ( !config.disablePrettierFormatter() ) {
            generatedSource = TsFormatPretter.applyFormatter( generatedSource, config.prettierConfigPath() );
         }

         return new TsArtifact( generatedSource, element.getName(),
               config.packageName() );
      } catch ( final Exception e ) {
         throw new CodeGenerationException( e );
      }
   }
}
