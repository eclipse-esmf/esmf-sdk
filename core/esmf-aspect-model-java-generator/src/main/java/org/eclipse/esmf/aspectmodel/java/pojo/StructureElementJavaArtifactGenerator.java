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
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;

import org.eclipse.esmf.aspectmodel.generator.ArtifactGenerator;
import org.eclipse.esmf.aspectmodel.generator.TemplateEngine;
import org.eclipse.esmf.aspectmodel.java.AspectModelJavaUtil;
import org.eclipse.esmf.aspectmodel.java.ImportTracker;
import org.eclipse.esmf.aspectmodel.java.JavaArtifact;
import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.java.StructuredValuePropertiesDeconstructor;
import org.eclipse.esmf.aspectmodel.java.ValueInitializer;
import org.eclipse.esmf.aspectmodel.java.exception.CodeGenerationException;
import org.eclipse.esmf.aspectmodel.resolver.services.DataType;
import org.eclipse.esmf.characteristic.Trait;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.StructureElement;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.impl.DefaultScalarValue;
import org.eclipse.esmf.samm.KnownVersion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;
import org.apache.velocity.app.FieldMethodizer;
import org.apache.velocity.runtime.RuntimeConstants;
import org.jboss.forge.roaster.Roaster;

/**
 * A {@link ArtifactGenerator} that generates Java Pojo code
 * for {@link StructureElement}s
 *
 * @param <E> the element type
 */
public class StructureElementJavaArtifactGenerator<E extends StructureElement> implements JavaArtifactGenerator<E> {
   private final Set<ComplexType> extendingEntities;

   public StructureElementJavaArtifactGenerator() {
      extendingEntities = Set.of();
   }

   public StructureElementJavaArtifactGenerator( final Set<ComplexType> extendingEntitiesInModel ) {
      extendingEntities = extendingEntitiesInModel;
   }

   // Needs to instantiate XSD in order to use Velocity's FieldMethodizer
   @SuppressWarnings( { "squid:S2440", "InstantiationOfUtilityClass" } )
   @Override
   public JavaArtifact apply( final E element, final JavaCodeGenerationConfig config ) {
      final ImportTracker importTracker = config.importTracker();
      importTracker.importExplicit( java.util.Optional.class );
      importTracker.importExplicit( java.util.List.class );
      importTracker.importExplicit( java.util.Locale.class );
      importTracker.importExplicit( java.util.Objects.class );

      final Map<String, Object> context = ImmutableMap.<String, Object> builder()
            .put( "ArrayList", ArrayList.class )
            .put( "Base64BinarySerializer", "org.eclipse.esmf.aspectmodel.jackson.Base64BinarySerializer" )
            .put( "Base64BinaryDeserializer", "org.eclipse.esmf.aspectmodel.jackson.Base64BinaryDeserializer" )
            .put( "codeGenerationConfig", config )
            .put( "Constraint", Constraint.class )
            .put( "ComplexType", ComplexType.class )
            .put( "currentYear", Year.now() )
            .put( "DataType", DataType.class )
            .put( "DatatypeConfigurationException", DatatypeConfigurationException.class )
            .put( "DatatypeConstants", DatatypeConstants.class )
            .put( "DatatypeFactory", DatatypeFactory.class )
            .put( "deconstructor", new StructuredValuePropertiesDeconstructor( element ) )
            .put( "DefaultScalar", DefaultScalar.class )
            .put( "DefaultScalarValue", DefaultScalarValue.class )
            .put( "element", element )
            .put( "HexBinarySerializer", "org.eclipse.esmf.aspectmodel.jackson.HexBinarySerializer" )
            .put( "HexBinaryDeserializer", "org.eclipse.esmf.aspectmodel.jackson.HexBinaryDeserializer" )
            .put( "JsonProperty", JsonProperty.class )
            .put( "JsonCreator", JsonCreator.class )
            .put( "JsonSerialize", JsonSerialize.class )
            .put( "JsonDeserialize", JsonDeserialize.class )
            .put( "KnownVersion", KnownVersion.class )
            .put( "localeEn", Locale.ENGLISH )
            .put( "Matcher", Matcher.class )
            .put( "NotNull", NotNull.class )
            .put( "Object", Object.class )
            .put( "Pattern", Pattern.class )
            .put( "ResourceFactory", ResourceFactory.class )
            .put( "Scalar", Scalar.class )
            .put( "String", String.class )
            .put( "StringUtils", StringUtils.class )
            .put( "Trait", Trait.class )
            .put( "util", AspectModelJavaUtil.class )
            .put( "valueInitializer", new ValueInitializer() )
            .put( "XSD", new FieldMethodizer( new XSD() ) )
            .put( "extendingEntities", extendingEntities )
            .build();

      final Properties engineConfiguration = new Properties();
      if ( config.executeLibraryMacros() ) {
         engineConfiguration.put( RuntimeConstants.VM_LIBRARY, config.templateLibFile().getName() );
         engineConfiguration.put( RuntimeConstants.FILE_RESOURCE_LOADER_PATH, config.templateLibFile().getParent() );
      }

      final String generatedSource = new TemplateEngine( context, engineConfiguration ).apply( "java-pojo" );
      try {
         return new JavaArtifact( Roaster.format( generatedSource ), element.getName(),
               config.packageName() );
      } catch ( final Exception exception ) {
         throw new CodeGenerationException( generatedSource, exception );
      }
   }
}
