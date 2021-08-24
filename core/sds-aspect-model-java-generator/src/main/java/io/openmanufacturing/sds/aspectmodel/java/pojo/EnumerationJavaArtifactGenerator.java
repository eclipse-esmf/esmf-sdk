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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import io.openmanufacturing.sds.aspectmodel.generator.TemplateEngine;
import io.openmanufacturing.sds.aspectmodel.java.AspectModelJavaUtil;
import io.openmanufacturing.sds.aspectmodel.java.ImportTracker;
import io.openmanufacturing.sds.aspectmodel.java.JavaArtifact;
import io.openmanufacturing.sds.aspectmodel.java.JavaCodeGenerationConfig;
import io.openmanufacturing.sds.aspectmodel.java.exception.CodeGenerationException;
import io.openmanufacturing.sds.aspectmodel.java.exception.EnumAttributeNotFoundException;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Enumeration;
import io.openmanufacturing.sds.metamodel.State;

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
            .put( "util", AspectModelJavaUtil.class )
            .put( "enableJacksonAnnotations", config.doEnableJacksonAnnotations() )
            .put( "enumeration", element )
            .put( "packageName", config.getPackageName() )
            .put( "currentYear", Year.now() )
            .put( "importTracker", importTracker )
            .put( "className", element.getName() )
            .put( "dataType", AspectModelJavaUtil.getDataType( element.getDataType(), importTracker ) )
            .put( "Optional", Optional.class )
            .put( "Arrays", Arrays.class )
            .put( "JsonValue", JsonValue.class )
            .put( "JsonCreator", JsonCreator.class )
            .put( "JsonFormat", JsonFormat.class )
            .put( "State", State.class )
            .put( "Entity", Entity.class )
            .build();

      try {
         final String generatedSource = new TemplateEngine( context ).apply( "java-enumeration" );
         final Formatter formatter = new Formatter();
         return new JavaArtifact( formatter.formatSource( generatedSource ), element.getName(),
               config.getPackageName() );
      } catch ( final FormatterException e ) {
         throw new CodeGenerationException( e );
      }
   }
}
