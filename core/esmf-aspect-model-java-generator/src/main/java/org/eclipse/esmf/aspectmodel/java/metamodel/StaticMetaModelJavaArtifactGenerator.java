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

package org.eclipse.esmf.aspectmodel.java.metamodel;

import java.nio.charset.Charset;
import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.annotation.processing.Generated;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;

import org.eclipse.esmf.aspectmodel.generator.ArtifactGenerator;
import org.eclipse.esmf.aspectmodel.generator.TemplateEngine;
import org.eclipse.esmf.aspectmodel.generator.exception.CodeGenerationException;
import org.eclipse.esmf.aspectmodel.java.AspectModelJavaUtil;
import org.eclipse.esmf.aspectmodel.java.ImportTracker;
import org.eclipse.esmf.aspectmodel.java.JavaArtifact;
import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.java.StructuredValuePropertiesDeconstructor;
import org.eclipse.esmf.aspectmodel.java.ValueInitializer;
import org.eclipse.esmf.aspectmodel.java.pojo.JavaArtifactGenerator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.BoundDefinition;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.StructureElement;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.Units;
import org.eclipse.esmf.metamodel.characteristic.Code;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.characteristic.Duration;
import org.eclipse.esmf.metamodel.characteristic.Either;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.characteristic.List;
import org.eclipse.esmf.metamodel.characteristic.Measurement;
import org.eclipse.esmf.metamodel.characteristic.Quantifiable;
import org.eclipse.esmf.metamodel.characteristic.Set;
import org.eclipse.esmf.metamodel.characteristic.SingleEntity;
import org.eclipse.esmf.metamodel.characteristic.SortedSet;
import org.eclipse.esmf.metamodel.characteristic.State;
import org.eclipse.esmf.metamodel.characteristic.StructuredValue;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultCode;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultCollection;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultDuration;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultEnumeration;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultList;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultMeasurement;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultQuantifiable;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultSet;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultSingleEntity;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultSortedSet;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultState;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultStructuredValue;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultTrait;
import org.eclipse.esmf.metamodel.constraint.EncodingConstraint;
import org.eclipse.esmf.metamodel.constraint.FixedPointConstraint;
import org.eclipse.esmf.metamodel.constraint.LanguageConstraint;
import org.eclipse.esmf.metamodel.constraint.LengthConstraint;
import org.eclipse.esmf.metamodel.constraint.RangeConstraint;
import org.eclipse.esmf.metamodel.constraint.RegularExpressionConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultEncodingConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultFixedPointConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultLanguageConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultLengthConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultRangeConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultRegularExpressionConstraint;
import org.eclipse.esmf.metamodel.datatype.LangString;
import org.eclipse.esmf.metamodel.impl.DefaultAbstractEntity;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.metamodel.impl.DefaultComplexType;
import org.eclipse.esmf.metamodel.impl.DefaultEntity;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.impl.DefaultScalarValue;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.staticmetamodel.PropertyContainer;
import org.eclipse.esmf.staticmetamodel.StaticContainerProperty;
import org.eclipse.esmf.staticmetamodel.StaticMetaClass;
import org.eclipse.esmf.staticmetamodel.StaticProperty;
import org.eclipse.esmf.staticmetamodel.StaticUnitProperty;
import org.eclipse.esmf.staticmetamodel.constraint.StaticConstraintContainerProperty;
import org.eclipse.esmf.staticmetamodel.constraint.StaticConstraintProperty;
import org.eclipse.esmf.staticmetamodel.constraint.StaticConstraintUnitProperty;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;
import org.apache.velocity.runtime.RuntimeConstants;
import org.jboss.forge.roaster.Roaster;

/**
 * A {@link ArtifactGenerator} that generates static meta classes
 * for {@link StructureElement}s in Aspect models
 *
 * @param <E> the type to generate code for
 */
public class StaticMetaModelJavaArtifactGenerator<E extends StructureElement> implements JavaArtifactGenerator<E> {
   @Override
   public JavaArtifact apply( final E element, final JavaCodeGenerationConfig config ) {
      final ImportTracker importTracker = config.importTracker();
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
      importTracker.importExplicit( Generated.class );

      final CharMatcher matchHash = CharMatcher.is( '#' );
      final String modelUrnPrefix = element.urn().getUrnPrefix();
      final String characteristicBaseUrn = matchHash.trimTrailingFrom( SammNs.SAMMC.getNamespace() );

      final Map<String, Object> context = ImmutableMap.<String, Object> builder()
            .put( "Arrays", java.util.Arrays.class )
            .put( "BoundDefinition", BoundDefinition.class )
            .put( "characteristicBaseUrn", characteristicBaseUrn )
            .put( "Charset", Charset.class )
            .put( "Code", Code.class )
            .put( "codeGenerationConfig", config )
            .put( "codeGeneratorName", AspectModelJavaUtil.codeGeneratorName() )
            .put( "codeGeneratorDate", AspectModelJavaUtil.CURRENT_DATE_ISO_8601 )
            .put( "Collection", Collection.class )
            .put( "Collections", Collections.class )
            .put( "Constraint", Constraint.class )
            .put( "context", new StaticCodeGenerationContext( config, modelUrnPrefix, characteristicBaseUrn, null, null, null ) )
            .put( "currentYear", Year.now() )
            .put( "DatatypeConfigurationException", DatatypeConfigurationException.class )
            .put( "DatatypeConstants", DatatypeConstants.class )
            .put( "DatatypeFactory", DatatypeFactory.class )
            .put( "deconstructor", new StructuredValuePropertiesDeconstructor( element ) )
            .put( "DefaultCharacteristic", DefaultCharacteristic.class )
            .put( "DefaultCode", DefaultCode.class )
            .put( "DefaultCollection", DefaultCollection.class )
            .put( "DefaultDuration", DefaultDuration.class )
            .put( "DefaultEncodingConstraint", DefaultEncodingConstraint.class )
            .put( "DefaultEntity", DefaultEntity.class )
            .put( "DefaultAbstractEntity", DefaultAbstractEntity.class )
            .put( "DefaultComplexType", DefaultComplexType.class )
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
            .put( "DefaultScalarValue", DefaultScalarValue.class )
            .put( "DefaultSet", DefaultSet.class )
            .put( "DefaultSingleEntity", DefaultSingleEntity.class )
            .put( "DefaultSortedSet", DefaultSortedSet.class )
            .put( "DefaultState", DefaultState.class )
            .put( "DefaultStructuredValue", DefaultStructuredValue.class )
            .put( "DefaultTrait", DefaultTrait.class )
            .put( "Duration", Duration.class )
            .put( "element", element )
            .put( "Either", Either.class )
            .put( "EncodingConstraint", EncodingConstraint.class )
            .put( "Entity", Entity.class )
            .put( "AbstractEntity", AbstractEntity.class )
            .put( "elementUrn", element.isAnonymous() ? "" : element.urn() )
            .put( "Enumeration", Enumeration.class )
            .put( "FixedPointConstraint", FixedPointConstraint.class )
            .put( "Generator", Generated.class )
            .put( "HashSet", HashSet.class )
            .put( "LangString", LangString.class )
            .put( "LanguageConstraint", LanguageConstraint.class )
            .put( "LengthConstraint", LengthConstraint.class )
            .put( "List", List.class )
            .put( "Locale", Locale.class )
            .put( "localeEn", Locale.ENGLISH )
            .put( "Map", Map.class )
            .put( "Measurement", Measurement.class )
            .put( "modelUrnPrefix", modelUrnPrefix )
            .put( "modelVisitor", new StaticMetaModelVisitor() )
            .put( "nonNegativeInteger", new DefaultScalar( XSD.nonNegativeInteger.getURI() ) )
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

      final Properties engineConfiguration = new Properties();
      if ( config.executeLibraryMacros() ) {
         engineConfiguration.put( RuntimeConstants.VM_LIBRARY, config.templateLibFile().getName() );
         engineConfiguration.put( RuntimeConstants.FILE_RESOURCE_LOADER_PATH, config.templateLibFile().getParent() );
      }

      final String generatedSource = new TemplateEngine( context, engineConfiguration ).apply( "java-static-class" );
      try {
         return new JavaArtifact( Roaster.format( generatedSource ), "Meta" + element.getName(),
               config.packageName() );
      } catch ( final Exception exception ) {
         throw new CodeGenerationException( generatedSource, exception );
      }
   }
}
