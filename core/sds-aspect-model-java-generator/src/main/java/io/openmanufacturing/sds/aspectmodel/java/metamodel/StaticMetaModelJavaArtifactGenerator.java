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

package io.openmanufacturing.sds.aspectmodel.java.metamodel;

import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;

import io.openmanufacturing.sds.aspectmodel.generator.TemplateEngine;
import io.openmanufacturing.sds.aspectmodel.java.AspectModelJavaUtil;
import io.openmanufacturing.sds.aspectmodel.java.ImportTracker;
import io.openmanufacturing.sds.aspectmodel.java.JavaArtifact;
import io.openmanufacturing.sds.aspectmodel.java.JavaCodeGenerationConfig;
import io.openmanufacturing.sds.aspectmodel.java.StructuredValuePropertiesDeconstructor;
import io.openmanufacturing.sds.aspectmodel.java.ValueInitializer;
import io.openmanufacturing.sds.aspectmodel.java.exception.CodeGenerationException;
import io.openmanufacturing.sds.aspectmodel.java.pojo.JavaArtifactGenerator;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMMC;
import io.openmanufacturing.sds.metamodel.Code;
import io.openmanufacturing.sds.metamodel.Collection;
import io.openmanufacturing.sds.metamodel.Constraint;
import io.openmanufacturing.sds.metamodel.Duration;
import io.openmanufacturing.sds.metamodel.Either;
import io.openmanufacturing.sds.metamodel.EncodingConstraint;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Enumeration;
import io.openmanufacturing.sds.metamodel.FixedPointConstraint;
import io.openmanufacturing.sds.metamodel.LanguageConstraint;
import io.openmanufacturing.sds.metamodel.LengthConstraint;
import io.openmanufacturing.sds.metamodel.List;
import io.openmanufacturing.sds.metamodel.Measurement;
import io.openmanufacturing.sds.metamodel.Quantifiable;
import io.openmanufacturing.sds.metamodel.RangeConstraint;
import io.openmanufacturing.sds.metamodel.RegularExpressionConstraint;
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.Set;
import io.openmanufacturing.sds.metamodel.SingleEntity;
import io.openmanufacturing.sds.metamodel.SortedSet;
import io.openmanufacturing.sds.metamodel.State;
import io.openmanufacturing.sds.metamodel.StructureElement;
import io.openmanufacturing.sds.metamodel.StructuredValue;
import io.openmanufacturing.sds.metamodel.Trait;
import io.openmanufacturing.sds.metamodel.Unit;
import io.openmanufacturing.sds.metamodel.Units;
import io.openmanufacturing.sds.metamodel.impl.BoundDefinition;
import io.openmanufacturing.sds.metamodel.impl.DefaultCharacteristic;
import io.openmanufacturing.sds.metamodel.impl.DefaultCode;
import io.openmanufacturing.sds.metamodel.impl.DefaultCollection;
import io.openmanufacturing.sds.metamodel.impl.DefaultDuration;
import io.openmanufacturing.sds.metamodel.impl.DefaultEncodingConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultEntity;
import io.openmanufacturing.sds.metamodel.impl.DefaultEnumeration;
import io.openmanufacturing.sds.metamodel.impl.DefaultFixedPointConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultLanguageConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultLengthConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultList;
import io.openmanufacturing.sds.metamodel.impl.DefaultMeasurement;
import io.openmanufacturing.sds.metamodel.impl.DefaultQuantifiable;
import io.openmanufacturing.sds.metamodel.impl.DefaultRangeConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultRegularExpressionConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultScalar;
import io.openmanufacturing.sds.metamodel.impl.DefaultSet;
import io.openmanufacturing.sds.metamodel.impl.DefaultSingleEntity;
import io.openmanufacturing.sds.metamodel.impl.DefaultSortedSet;
import io.openmanufacturing.sds.metamodel.impl.DefaultState;
import io.openmanufacturing.sds.metamodel.impl.DefaultStructuredValue;
import io.openmanufacturing.sds.metamodel.impl.DefaultTrait;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.staticmetamodel.PropertyContainer;
import io.openmanufacturing.sds.staticmetamodel.StaticContainerProperty;
import io.openmanufacturing.sds.staticmetamodel.StaticMetaClass;
import io.openmanufacturing.sds.staticmetamodel.StaticProperty;
import io.openmanufacturing.sds.staticmetamodel.StaticUnitProperty;
import io.openmanufacturing.sds.staticmetamodel.constraint.StaticConstraintContainerProperty;
import io.openmanufacturing.sds.staticmetamodel.constraint.StaticConstraintProperty;
import io.openmanufacturing.sds.staticmetamodel.constraint.StaticConstraintUnitProperty;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMap;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

/**
 * A {@link io.openmanufacturing.sds.aspectmodel.generator.ArtifactGenerator} that generates static meta classes
 * for {@link StructureElement}s in Aspect models
 *
 * @param <E> the type to generate code for
 */
public class StaticMetaModelJavaArtifactGenerator<E extends StructureElement> implements JavaArtifactGenerator<E> {
   @Override
   public JavaArtifact apply( final E element, final JavaCodeGenerationConfig config ) {
      final ImportTracker importTracker = config.getImportTracker();
      importTracker.importExplicit( java.util.Optional.class );
      importTracker.importExplicit( java.util.List.class );
      importTracker.importExplicit( PropertyContainer.class );
      importTracker.importExplicit( StaticMetaClass.class );
      importTracker.importExplicit( StaticProperty.class );
      importTracker.importExplicit( MetaModelBaseAttributes.class );
      importTracker.importExplicit( KnownVersion.class );
      importTracker.importExplicit( AspectModelUrn.class );
      importTracker.importExplicit( Constraint.class );
      importTracker.importExplicit( Trait.class );
      importTracker.importExplicit( Arrays.class );
      importTracker.importExplicit( Locale.class );

      final CharMatcher matchHash = CharMatcher.is( '#' );
      final String modelUrnPrefix = element.getAspectModelUrn().map( AspectModelUrn::getUrnPrefix ).orElseThrow( () -> {
         throw new CodeGenerationException( "Aspect or Entity may not be declared as an anonymous node" );
      } );

      final Map<String, Object> context = ImmutableMap.<String, Object> builder()
            .put( "Arrays", java.util.Arrays.class )
            .put( "BoundDefinition", BoundDefinition.class )
            .put( "characteristicBaseUrn",
                  matchHash.trimTrailingFrom( new BAMMC( element.getMetaModelVersion() ).getNamespace() ) )
            .put( "Code", Code.class )
            .put( "Collection", Collection.class )
            .put( "Collections", Collections.class )
            .put( "Constraint", Constraint.class )
            .put( "currentYear", Year.now() )
            .put( "deconstructor", new StructuredValuePropertiesDeconstructor( element ) )
            .put( "DefaultCharacteristic", DefaultCharacteristic.class )
            .put( "DefaultCode", DefaultCode.class )
            .put( "DefaultCollection", DefaultCollection.class )
            .put( "DefaultDuration", DefaultDuration.class )
            .put( "DefaultEncodingConstraint", DefaultEncodingConstraint.class )
            .put( "DefaultEntity", DefaultEntity.class )
            .put( "DefaultEnumeration", DefaultEnumeration.class )
            .put( "DefaultFixedPointConstraint", DefaultFixedPointConstraint.class )
            .put( "DefaultLanguageConstraint", DefaultLanguageConstraint.class )
            .put( "DefaultLengthConstraint", DefaultLengthConstraint.class )
            .put( "DefaultList", DefaultList.class )
            .put( "DefaultMeasurement", DefaultMeasurement.class )
            .put( "DefaultQuantifiable", DefaultQuantifiable.class )
            .put( "DefaultRangeConstraint", DefaultRangeConstraint.class )
            .put( "DefaultRegularExpressionConstraint", DefaultRegularExpressionConstraint.class )
            .put( "DefaultScalar", DefaultScalar.class )
            .put( "DefaultSet", DefaultSet.class )
            .put( "DefaultSingleEntity", DefaultSingleEntity.class )
            .put( "DefaultSortedSet", DefaultSortedSet.class )
            .put( "DefaultState", DefaultState.class )
            .put( "DefaultStructuredValue", DefaultStructuredValue.class )
            .put( "DefaultTrait", DefaultTrait.class )
            .put( "Duration", Duration.class )
            .put( "element", element )
            .put( "enableJacksonAnnotations", config.doEnableJacksonAnnotations() )
            .put( "Either", Either.class )
            .put( "EncodingConstraint", EncodingConstraint.class )
            .put( "Entity", Entity.class )
            .put( "Enumeration", Enumeration.class )
            .put( "FixedPointConstraint", FixedPointConstraint.class )
            .put( "importTracker", importTracker )
            .put( "LanguageConstraint", LanguageConstraint.class )
            .put( "LengthConstraint", LengthConstraint.class )
            .put( "List", List.class )
            .put( "Locale", Locale.class )
            .put( "localeEn", Locale.ENGLISH )
            .put( "Map", Map.class )
            .put( "Measurement", Measurement.class )
            .put( "modelUrnPrefix", modelUrnPrefix )
            .put( "nonNegativeInteger",
                  new DefaultScalar( XSD.nonNegativeInteger.getURI(), element.getMetaModelVersion() ) )
            .put( "packageName", config.getPackageName() )
            .put( "Quantifiable", Quantifiable.class )
            .put( "RangeConstraint", RangeConstraint.class )
            .put( "RegularExpressionConstraint", RegularExpressionConstraint.class )
            .put( "ResourceFactory", ResourceFactory.class )
            .put( "Scalar", Scalar.class )
            .put( "Set", Set.class )
            .put( "SingleEntity", SingleEntity.class )
            .put( "SortedSet", SortedSet.class )
            .put( "State", State.class )
            .put( "StaticConstraintContainerProperty", StaticConstraintContainerProperty.class )
            .put( "StaticConstraintProperty", StaticConstraintProperty.class )
            .put( "StaticConstraintUnitProperty", StaticConstraintUnitProperty.class )
            .put( "StaticContainerProperty", StaticContainerProperty.class )
            .put( "StaticUnitProperty", StaticUnitProperty.class )
            .put( "StringEscapeUtils", StringEscapeUtils.class )
            .put( "StructuredValue", StructuredValue.class )
            .put( "Trait", Trait.class )
            .put( "Unit", Unit.class )
            .put( "Units", Units.class )
            .put( "util", AspectModelJavaUtil.class )
            .put( "valueInitializer", new ValueInitializer() )
            .build();

      final String generatedSource = new TemplateEngine( context ).apply( "java-static-class" );
      try {
         final Formatter formatter = new Formatter();
         return new JavaArtifact( formatter.formatSource( generatedSource ), "Meta" + element.getName(),
               config.getPackageName() );
      } catch ( final FormatterException exception ) {
         throw new CodeGenerationException( generatedSource, exception );
      }
   }
}
