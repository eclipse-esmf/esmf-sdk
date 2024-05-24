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
package org.eclipse.esmf.aspectmodel.vocabulary;

import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.esmf.samm.KnownVersion;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * The RDF jena vocabulary of the Characteristic meta model.
 */
// Since the class is an RDF vocabulary, naming rules for the class and for several methods (which should be named identically
// to the corresponding model elements) are suppressed.
@SuppressWarnings( { "checkstyle:AbbreviationAsWordInName", "NewMethodNamingConvention" } )
public class SAMMC implements Namespace {
   private final KnownVersion metaModelVersion;

   public SAMMC( final KnownVersion metaModelVersion ) {
      this.metaModelVersion = metaModelVersion;
   }

   @Override
   public String getUri() {
      return SammNs.SAMM.getBaseUri() + "characteristic:" + metaModelVersion.toVersionString();
   }

   /*
    * Constraints
    */

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource RangeConstraint() {
      return resource( "RangeConstraint" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource EncodingConstraint() {
      return resource( "EncodingConstraint" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource RegularExpressionConstraint() {
      return resource( "RegularExpressionConstraint" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource LengthConstraint() {
      return resource( "LengthConstraint" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource LanguageConstraint() {
      return resource( "LanguageConstraint" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource LocaleConstraint() {
      return resource( "LocaleConstraint" );
   }

   /*
    * Characteristic classes and their properties
    */

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource StructuredValue() {
      return resource( "StructuredValue" );
   }

   public Property minValue() {
      return property( "minValue" );
   }

   public Property maxValue() {
      return property( "maxValue" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Quantifiable() {
      return resource( "Quantifiable" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Measurement() {
      return resource( "Measurement" );
   }

   public Property unit() {
      return property( "unit" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Duration() {
      return resource( "Duration" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource State() {
      return resource( "State" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Enumeration() {
      return resource( "Enumeration" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Collection() {
      return resource( "Collection" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Set() {
      return resource( "Set" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SortedSet() {
      return resource( "SortedSet" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource List() {
      return resource( "List" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource TimeSeries() {
      return resource( "TimeSeries" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SingleEntity() {
      return resource( "SingleEntity" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Code() {
      return resource( "Code" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource FixedPointConstraint() {
      return resource( "FixedPointConstraint" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Trait() {
      return resource( "Trait" );
   }

   public Property baseCharacteristic() {
      return property( "baseCharacteristic" );
   }

   public Property values() {
      return property( "values" );
   }

   public Property defaultValue() {
      return property( "defaultValue" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Either() {
      return resource( "Either" );
   }

   public Property left() {
      return property( "left" );
   }

   public Property right() {
      return property( "right" );
   }

   public Property lowerBoundDefinition() {
      return property( "lowerBoundDefinition" );
   }

   public Property upperBoundDefinition() {
      return property( "upperBoundDefinition" );
   }

   public Property elements() {
      return property( "elements" );
   }

   public Property deconstructionRule() {
      return property( "deconstructionRule" );
   }

   public Property scale() {
      return property( "scale" );
   }

   public Property integer() {
      return property( "integer" );
   }

   public Property elementCharacteristic() {
      return property( "elementCharacteristic" );
   }

   public Property constraint() {
      return property( "constraint" );
   }

   public Property languageCode() {
      return property( "languageCode" );
   }

   public Property localeCode() {
      return property( "localeCode" );
   }

   /*
    * Characteristic instances
    */

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Timestamp() {
      return resource( "Timestamp" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Text() {
      return resource( "Text" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MultiLanguageText() {
      return resource( "MultiLanguageText" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Language() {
      return resource( "Language" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Locale() {
      return resource( "Locale" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Boolean() {
      return resource( "Boolean" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource ResourcePath() {
      return resource( "ResourcePath" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MimeType() {
      return resource( "MimeType" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource UnitReference() {
      return resource( "UnitReference" );
   }

   /*
    * Convenience methods
    */

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
}
