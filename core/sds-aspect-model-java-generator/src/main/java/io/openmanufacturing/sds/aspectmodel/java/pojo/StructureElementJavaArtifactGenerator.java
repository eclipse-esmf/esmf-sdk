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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;
import org.apache.velocity.app.FieldMethodizer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

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
            .put( "util", AspectModelJavaUtil.class )
            .put( "XSD", new FieldMethodizer( new XSD() ) )
            .put( "localeEn", Locale.ENGLISH )
            .put( "currentYear", Year.now() )
            .put( "ArrayList", ArrayList.class )
            .put( "Constraint", Constraint.class )
            .put( "ComplexType", ComplexType.class )
            .put( "Trait", Trait.class )
            .put( "NotNull", NotNull.class )
            .put( "JsonProperty", JsonProperty.class )
            .put( "JsonCreator", JsonCreator.class )
            .put( "JsonSerialize", JsonSerialize.class )
            .put( "JsonDeserialize", JsonDeserialize.class )
            .put( "ResourceFactory", ResourceFactory.class )
            .put( "Matcher", Matcher.class )
            .put( "Pattern", Pattern.class )
            .put( "DataType", DataType.class )
            .put( "HexBinarySerializer", "io.openmanufacturing.sds.aspectmodel.jackson.HexBinarySerializer" )
            .put( "HexBinaryDeserializer", "io.openmanufacturing.sds.aspectmodel.jackson.HexBinaryDeserializer" )
            .put( "Base64BinarySerializer", "io.openmanufacturing.sds.aspectmodel.jackson.Base64BinarySerializer" )
            .put( "Base64BinaryDeserializer", "io.openmanufacturing.sds.aspectmodel.jackson.Base64BinaryDeserializer" )
            .put( "codeGenerationConfig", config )
            .put( "element", element )
            .put( "deconstructor", new StructuredValuePropertiesDeconstructor( element ) )
            .put( "valueInitializer", new ValueInitializer() )
            .put( "StringUtils", StringUtils.class )
            .build();

      final String generatedSource = new TemplateEngine( context ).apply( "java-pojo" );
      try {
         final Formatter formatter = new Formatter();
         String source = formatter.formatSource( generatedSource );
         return new JavaArtifact( source, element.getName(), config.getPackageName() );
      } catch ( final FormatterException exception ) {
         throw new CodeGenerationException( generatedSource, exception );
      }
   }
}
