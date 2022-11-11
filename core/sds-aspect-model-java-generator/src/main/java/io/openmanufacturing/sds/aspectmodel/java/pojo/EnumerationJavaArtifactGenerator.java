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

package io.openmanufacturing.sds.aspectmodel.java.pojo;

import java.time.Year;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.jboss.forge.roaster.Roaster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;

import io.openmanufacturing.sds.aspectmodel.generator.TemplateEngine;
import io.openmanufacturing.sds.aspectmodel.java.AspectModelJavaUtil;
import io.openmanufacturing.sds.aspectmodel.java.ImportTracker;
import io.openmanufacturing.sds.aspectmodel.java.JavaArtifact;
import io.openmanufacturing.sds.aspectmodel.java.JavaCodeGenerationConfig;
import io.openmanufacturing.sds.aspectmodel.java.exception.CodeGenerationException;
import io.openmanufacturing.sds.aspectmodel.java.exception.EnumAttributeNotFoundException;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.characteristic.Enumeration;
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.characteristic.State;

/**
 * A {@link io.openmanufacturing.sds.aspectmodel.generator.ArtifactGenerator} that generates Java Pojo code
 * for {@link Enumeration}s
 *
 * @param <E> the element type
 */
public class EnumerationJavaArtifactGenerator<E extends Enumeration> implements JavaArtifactGenerator<E> {
   @Override
   public JavaArtifact apply( final E element, final JavaCodeGenerationConfig config ) {
      final ImportTracker importTracker = config.getImportTracker();
      importTracker.importExplicit( EnumAttributeNotFoundException.class );

      final Map<String, Object> context = ImmutableMap.<String, Object> builder()
            .put( "Arrays", Arrays.class )
            .put( "className", element.getName() )
            .put( "codeGenerationConfig", config )
            .put( "currentYear", Year.now() )
            .put( "dataType", AspectModelJavaUtil.getDataType( element.getDataType(), config.getImportTracker() ) )
            .put( "Entity", Entity.class )
            .put( "enumeration", element )
            .put( "importTracker", importTracker )
            .put( "JsonValue", JsonValue.class )
            .put( "JsonCreator", JsonCreator.class )
            .put( "JsonFormat", JsonFormat.class )
            .put( "Optional", Optional.class )
            .put( "Scalar", Scalar.class )
            .put( "State", State.class )
            .put( "util", AspectModelJavaUtil.class )
            .build();

      try {
         final Properties engineConfiguration = new Properties();
         if ( config.doExecuteLibraryMacros() ) {
            engineConfiguration.put( "velocimacro.library", config.getTemplateLibFile().getName() );
            engineConfiguration.put( "file.resource.loader.path", config.getTemplateLibFile().getParent() );
         }

         final String generatedSource = new TemplateEngine( context, engineConfiguration ).apply( "java-enumeration" );
         return new JavaArtifact( Roaster.format( generatedSource ), element.getName(),
               config.getPackageName() );
      } catch ( final Exception e ) {
         throw new CodeGenerationException( e );
      }
   }
}
