/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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
package org.eclipse.esmf.metamodel.vocabulary;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.esmf.samm.KnownVersion;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * The RDF jena vocabulary of the Characteristic meta model.
 */
// Since the class is an RDF vocabulary, naming rules for the class and for several methods (which
// should be named identically
// to the corresponding model elements) are suppressed.
@SuppressWarnings( { "checkstyle:AbbreviationAsWordInName", "NewMethodNamingConvention" } )
public class SAMMC implements RdfNamespace {
   private final KnownVersion metaModelVersion;

   public SAMMC( final KnownVersion metaModelVersion ) {
      this.metaModelVersion = metaModelVersion;
   }

   @Override
   public String getShortForm() {
      return "samm-c";
   }

   @Override
   public String getUri() {
      return SammNs.SAMM.getBaseUri() + "characteristic:" + metaModelVersion.toVersionString();
   }

   /*
    * Resource constants (classes, constraints, and characteristic instances)
    */
   public static final String RANGE_CONSTRAINT = "RangeConstraint";
   public static final String ENCODING_CONSTRAINT = "EncodingConstraint";
   public static final String REGULAR_EXPRESSION_CONSTRAINT = "RegularExpressionConstraint";
   public static final String LENGTH_CONSTRAINT = "LengthConstraint";
   public static final String LANGUAGE_CONSTRAINT = "LanguageConstraint";
   public static final String LOCALE_CONSTRAINT = "LocaleConstraint";
   public static final String FIXED_POINT_CONSTRAINT = "FixedPointConstraint";
   public static final String STRUCTURED_VALUE = "StructuredValue";
   public static final String QUANTIFIABLE = "Quantifiable";
   public static final String MEASUREMENT = "Measurement";
   public static final String DURATION = "Duration";
   public static final String STATE = "State";
   public static final String ENUMERATION = "Enumeration";
   public static final String COLLECTION = "Collection";
   public static final String SET = "Set";
   public static final String SORTED_SET = "SortedSet";
   public static final String LIST = "List";
   public static final String TIME_SERIES = "TimeSeries";
   public static final String SINGLE_ENTITY = "SingleEntity";
   public static final String CODE = "Code";
   public static final String TRAIT = "Trait";
   public static final String EITHER = "Either";
   public static final String TIMESTAMP = "Timestamp";
   public static final String TEXT = "Text";
   public static final String MULTI_LANGUAGE_TEXT = "MultiLanguageText";
   public static final String LANGUAGE = "Language";
   public static final String LOCALE = "Locale";
   public static final String BOOLEAN = "Boolean";
   public static final String RESOURCE_PATH = "ResourcePath";
   public static final String MIME_TYPE = "MimeType";
   public static final String UNIT_REFERENCE = "UnitReference";
   public static final String REFERENCE = "Reference";

   /*
    * Property constants
    */
   public static final String MIN_VALUE = "minValue";
   public static final String MAX_VALUE = "maxValue";
   public static final String UNIT = "unit";
   public static final String BASE_CHARACTERISTIC = "baseCharacteristic";
   public static final String VALUES = "values";
   public static final String DEFAULT_VALUE = "defaultValue";
   public static final String LEFT = "left";
   public static final String RIGHT = "right";
   public static final String LOWER_BOUND_DEFINITION = "lowerBoundDefinition";
   public static final String UPPER_BOUND_DEFINITION = "upperBoundDefinition";
   public static final String ELEMENTS = "elements";
   public static final String DECONSTRUCTION_RULE = "deconstructionRule";
   public static final String SCALE = "scale";
   public static final String INTEGER = "integer";
   public static final String ELEMENT_CHARACTERISTIC = "elementCharacteristic";
   public static final String CONSTRAINT = "constraint";
   public static final String LANGUAGE_CODE = "languageCode";
   public static final String LOCALE_CODE = "localeCode";

   /*
    * Constraints
    */

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource RangeConstraint() {
      return resource( RANGE_CONSTRAINT );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource EncodingConstraint() {
      return resource( ENCODING_CONSTRAINT );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource RegularExpressionConstraint() {
      return resource( REGULAR_EXPRESSION_CONSTRAINT );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource LengthConstraint() {
      return resource( LENGTH_CONSTRAINT );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource LanguageConstraint() {
      return resource( LANGUAGE_CONSTRAINT );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource LocaleConstraint() {
      return resource( LOCALE_CONSTRAINT );
   }

   /*
    * Characteristic classes and their properties
    */

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource StructuredValue() {
      return resource( STRUCTURED_VALUE );
   }

   public Property minValue() {
      return property( MIN_VALUE );
   }

   public Property maxValue() {
      return property( MAX_VALUE );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Quantifiable() {
      return resource( QUANTIFIABLE );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Measurement() {
      return resource( MEASUREMENT );
   }

   public Property unit() {
      return property( UNIT );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Duration() {
      return resource( DURATION );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource State() {
      return resource( STATE );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Enumeration() {
      return resource( ENUMERATION );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Collection() {
      return resource( COLLECTION );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Set() {
      return resource( SET );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SortedSet() {
      return resource( SORTED_SET );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource List() {
      return resource( LIST );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource TimeSeries() {
      return resource( TIME_SERIES );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SingleEntity() {
      return resource( SINGLE_ENTITY );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Code() {
      return resource( CODE );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource FixedPointConstraint() {
      return resource( FIXED_POINT_CONSTRAINT );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Trait() {
      return resource( TRAIT );
   }

   public Property baseCharacteristic() {
      return property( BASE_CHARACTERISTIC );
   }

   public Property values() {
      return property( VALUES );
   }

   public Property defaultValue() {
      return property( DEFAULT_VALUE );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Either() {
      return resource( EITHER );
   }

   public Property left() {
      return property( LEFT );
   }

   public Property right() {
      return property( RIGHT );
   }

   public Property lowerBoundDefinition() {
      return property( LOWER_BOUND_DEFINITION );
   }

   public Property upperBoundDefinition() {
      return property( UPPER_BOUND_DEFINITION );
   }

   public Property elements() {
      return property( ELEMENTS );
   }

   public Property deconstructionRule() {
      return property( DECONSTRUCTION_RULE );
   }

   public Property scale() {
      return property( SCALE );
   }

   public Property integer() {
      return property( INTEGER );
   }

   public Property elementCharacteristic() {
      return property( ELEMENT_CHARACTERISTIC );
   }

   public Property constraint() {
      return property( CONSTRAINT );
   }

   public Property languageCode() {
      return property( LANGUAGE_CODE );
   }

   public Property localeCode() {
      return property( LOCALE_CODE );
   }

   /*
    * Characteristic instances
    */

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Timestamp() {
      return resource( TIMESTAMP );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Text() {
      return resource( TEXT );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MultiLanguageText() {
      return resource( MULTI_LANGUAGE_TEXT );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Language() {
      return resource( LANGUAGE );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Locale() {
      return resource( LOCALE );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Boolean() {
      return resource( BOOLEAN );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource ResourcePath() {
      return resource( RESOURCE_PATH );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MimeType() {
      return resource( MIME_TYPE );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource UnitReference() {
      return resource( UNIT_REFERENCE );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Reference() {
      return resource( REFERENCE );
   }

   /*
    * Convenience methods
    */

   public List<Resource> allCharacteristicInstances() {
      return List.of( Text(), Boolean(), Timestamp(), MultiLanguageText(), Language(), Locale(),
            MimeType(), ResourcePath(), Reference(), UnitReference() );
   }

   public List<Resource> allCharacteristicClasses() {
      return Stream.concat(
            allCollections(),
            Stream.of( Code(), Duration(), Either(), Enumeration(), Measurement(), Quantifiable(),
                  SingleEntity(), State(), StructuredValue(), Trait() )
      ).toList();
   }

   public Stream<Resource> allCollections() {
      return Stream.of( Collection(), Set(), SortedSet(), List(), TimeSeries() );
   }

   public Stream<Resource> allConstraints() {
      return Stream.of( EncodingConstraint(), FixedPointConstraint(), LanguageConstraint(), LengthConstraint(),
            LocaleConstraint(), RangeConstraint(), RegularExpressionConstraint() );
   }

   public Stream<Resource> allCharacteristics() {
      final Stream<Resource> miscCharacteristics = Stream
            .of( Code(), Duration(), Either(), Enumeration(), Measurement(), Quantifiable(), SingleEntity(), State(),
                  StructuredValue() );
      return Stream.of( allCollections(), allConstraints(), miscCharacteristics ).flatMap( Function.identity() );
   }

   @Override
   public List<Resource> allResources() {
      return List.of( RangeConstraint(), EncodingConstraint(), RegularExpressionConstraint(), LengthConstraint(), LanguageConstraint(),
            LocaleConstraint(), StructuredValue(), Quantifiable(), Measurement(), Duration(), State(), Enumeration(), Collection(),
            Set(), SortedSet(), List(), TimeSeries(), SingleEntity(), Code(), FixedPointConstraint(), Trait(), Either(), Timestamp(),
            Text(), MultiLanguageText(), Language(), Locale(), Boolean(), ResourcePath(), MimeType(), Reference(), UnitReference() );
   }

   @Override
   public List<Property> allProperties() {
      return List.of( minValue(), maxValue(), unit(), baseCharacteristic(), values(), defaultValue(), left(), right(),
            lowerBoundDefinition(), upperBoundDefinition(), elements(), deconstructionRule(), scale(), integer(),
            elementCharacteristic(), constraint(), languageCode(), localeCode() );
   }
}
