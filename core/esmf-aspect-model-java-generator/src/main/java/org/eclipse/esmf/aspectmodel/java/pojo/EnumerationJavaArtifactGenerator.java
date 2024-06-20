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

package org.eclipse.esmf.aspectmodel.java.pojo;

import java.time.Year;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.eclipse.esmf.aspectmodel.generator.ArtifactGenerator;
import org.eclipse.esmf.aspectmodel.generator.TemplateEngine;
import org.eclipse.esmf.aspectmodel.java.AspectModelJavaUtil;
import org.eclipse.esmf.aspectmodel.java.ImportTracker;
import org.eclipse.esmf.aspectmodel.java.JavaArtifact;
import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.java.exception.CodeGenerationException;
import org.eclipse.esmf.aspectmodel.java.exception.EnumAttributeNotFoundException;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.characteristic.State;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Scalar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;
import org.apache.velocity.runtime.RuntimeConstants;
import org.jboss.forge.roaster.Roaster;

/**
 * A {@link ArtifactGenerator} that generates Java Pojo code
 * for {@link Enumeration}s
 *
 * @param <E> the element type
 */
public class EnumerationJavaArtifactGenerator<E extends Enumeration> implements JavaArtifactGenerator<E> {
   @Override
   public JavaArtifact apply( final E element, final JavaCodeGenerationConfig config ) {
      final ImportTracker importTracker = config.importTracker();
      importTracker.importExplicit( EnumAttributeNotFoundException.class );

      final Map<String, Object> context = ImmutableMap.<String, Object> builder()
            .put( "Arrays", Arrays.class )
            .put( "className", element.getName() )
            .put( "codeGenerationConfig", config )
            .put( "currentYear", Year.now() )
            .put( "dataType", AspectModelJavaUtil.getDataType( element.getDataType(), config.importTracker() ) )
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
         if ( config.executeLibraryMacros() ) {
            engineConfiguration.put( RuntimeConstants.VM_LIBRARY, config.templateLibFile().getName() );
            engineConfiguration.put( RuntimeConstants.FILE_RESOURCE_LOADER_PATH, config.templateLibFile().getParent() );
         }

         final String generatedSource = new TemplateEngine( context, engineConfiguration ).apply( "java-enumeration" );
         return new JavaArtifact( Roaster.format( generatedSource ), element.getName(),
               config.packageName() );
      } catch ( final Exception e ) {
         throw new CodeGenerationException( e );
      }
   }
}
