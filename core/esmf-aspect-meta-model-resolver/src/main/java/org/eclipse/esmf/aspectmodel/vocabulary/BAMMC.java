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

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import org.eclipse.esmf.samm.KnownVersion;

/**
 * The RDF jena vocabulary of the Characteristic meta model.
 */
@SuppressWarnings( "squid:S00100" ) // Method names should match model element
public class BAMMC implements Namespace {
   private final KnownVersion bammVersion;
   private final BAMM bamm;

   public BAMMC( final KnownVersion bammVersion ) {
      this.bammVersion = bammVersion;
      bamm = new BAMM( bammVersion );
   }

   @Override
   public String getUri() {
      return bamm.getBaseUri() + "characteristic:" + bammVersion.toVersionString();
   }

   public Resource RangeConstraint() {
      return resource( "RangeConstraint" );
   }

   public Resource EncodingConstraint() {
      return resource( "EncodingConstraint" );
   }

   public Resource RegularExpressionConstraint() {
      return resource( "RegularExpressionConstraint" );
   }

   public Resource LengthConstraint() {
      return resource( "LengthConstraint" );
   }

   public Resource LanguageConstraint() {
      return resource( "LanguageConstraint" );
   }

   public Resource LocaleConstraint() {
      return resource( "LocaleConstraint" );
   }

   public Resource StructuredValue() {
      return resource( "StructuredValue" );
   }

   public Property minValue() {
      return property( "minValue" );
   }

   public Property maxValue() {
      return property( "maxValue" );
   }

   public Resource Quantifiable() {
      return resource( "Quantifiable" );
   }

   public Resource Measurement() {
      return resource( "Measurement" );
   }

   public Property unit() {
      return property( "unit" );
   }

   public Resource Duration() {
      return resource( "Duration" );
   }

   public Resource State() {
      return resource( "State" );
   }

   public Resource Enumeration() {
      return resource( "Enumeration" );
   }

   public Resource Collection() {
      return resource( "Collection" );
   }

   public Resource Set() {
      return resource( "Set" );
   }

   public Resource SortedSet() {
      return resource( "SortedSet" );
   }

   public Resource List() {
      return resource( "List" );
   }

   public Resource TimeSeries() {
      return resource( "TimeSeries" );
   }

   public Resource SingleEntity() {
      return resource( "SingleEntity" );
   }

   public Resource Code() {
      return resource( "Code" );
   }

   public Resource FixedPointConstraint() {
      return resource( "FixedPointConstraint" );
   }

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
