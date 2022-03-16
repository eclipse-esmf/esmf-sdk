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
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;
import org.apache.velocity.app.FieldMethodizer;
import org.jboss.forge.roaster.Roaster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;

import io.openmanufacturing.sds.aspectmodel.generator.TemplateEngine;
import io.openmanufacturing.sds.aspectmodel.java.AspectModelJavaUtil;
import io.openmanufacturing.sds.aspectmodel.java.ImportTracker;
import io.openmanufacturing.sds.aspectmodel.java.JavaArtifact;
import io.openmanufacturing.sds.aspectmodel.java.JavaCodeGenerationConfig;
import io.openmanufacturing.sds.aspectmodel.java.StructuredValuePropertiesDeconstructor;
import io.openmanufacturing.sds.aspectmodel.java.ValueInitializer;
import io.openmanufacturing.sds.aspectmodel.java.exception.CodeGenerationException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.DataType;
import io.openmanufacturing.sds.metamodel.ComplexType;
import io.openmanufacturing.sds.metamodel.Constraint;
import io.openmanufacturing.sds.metamodel.StructureElement;
import io.openmanufacturing.sds.metamodel.Trait;

/**
 * A {@link io.openmanufacturing.sds.aspectmodel.generator.ArtifactGenerator} that generates Java Pojo code
 * for {@link StructureElement}s
 *
 * @param <E> the element type
 */
public class StructureElementJavaArtifactGenerator<E extends StructureElement> implements JavaArtifactGenerator<E> {

   // Needs to instantiate XSD in order to use Velocity's FieldMethodizer
   @SuppressWarnings( { "squid:S2440", "InstantiationOfUtilityClass" } )
   @Override
   public JavaArtifact apply( final E element, final JavaCodeGenerationConfig config ) {
      final ImportTracker importTracker = config.getImportTracker();
      importTracker.importExplicit( java.util.Optional.class );
      importTracker.importExplicit( java.util.List.class );
      importTracker.importExplicit( java.util.Locale.class );
      importTracker.importExplicit( java.util.Objects.class );

      final Map<String, Object> context = ImmutableMap.<String, Object> builder()
            .put( "ArrayList", ArrayList.class )
            .put( "Base64BinarySerializer", "io.openmanufacturing.sds.aspectmodel.jackson.Base64BinarySerializer" )
            .put( "Base64BinaryDeserializer", "io.openmanufacturing.sds.aspectmodel.jackson.Base64BinaryDeserializer" )
            .put( "codeGenerationConfig", config )
            .put( "Constraint", Constraint.class )
            .put( "ComplexType", ComplexType.class )
            .put( "currentYear", Year.now() )
            .put( "DataType", DataType.class )
            .put( "DatatypeConfigurationException", DatatypeConfigurationException.class )
            .put( "DatatypeConstants", DatatypeConstants.class )
            .put( "DatatypeFactory", DatatypeFactory.class )
            .put( "deconstructor", new StructuredValuePropertiesDeconstructor( element ) )
            .put( "element", element )
            .put( "HexBinarySerializer", "io.openmanufacturing.sds.aspectmodel.jackson.HexBinarySerializer" )
            .put( "HexBinaryDeserializer", "io.openmanufacturing.sds.aspectmodel.jackson.HexBinaryDeserializer" )
            .put( "JsonProperty", JsonProperty.class )
            .put( "JsonCreator", JsonCreator.class )
            .put( "JsonSerialize", JsonSerialize.class )
            .put( "JsonDeserialize", JsonDeserialize.class )
            .put( "localeEn", Locale.ENGLISH )
            .put( "Matcher", Matcher.class )
            .put( "NotNull", NotNull.class )
            .put( "Pattern", Pattern.class )
            .put( "ResourceFactory", ResourceFactory.class )
            .put( "StringUtils", StringUtils.class )
            .put( "Trait", Trait.class )
            .put( "util", AspectModelJavaUtil.class )
            .put( "valueInitializer", new ValueInitializer() )
            .put( "XSD", new FieldMethodizer( new XSD() ) )
            .build();

      final Properties engineConfiguration = new Properties();
      if ( config.doExecuteLibraryMacros() ) {
         engineConfiguration.put( "velocimacro.library", config.getTemplateLibFile().getName() );
         engineConfiguration.put( "file.resource.loader.path", config.getTemplateLibFile().getParent() );
      }

      final String generatedSource = new TemplateEngine( context, engineConfiguration ).apply( "java-pojo" );
      try {
         return new JavaArtifact( Roaster.format( generatedSource ), element.getName(),
               config.getPackageName() );
      } catch ( final Exception exception ) {
         throw new CodeGenerationException( generatedSource, exception );
      }
   }
}
