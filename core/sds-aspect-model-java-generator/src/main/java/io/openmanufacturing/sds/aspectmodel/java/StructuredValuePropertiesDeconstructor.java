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

package io.openmanufacturing.sds.aspectmodel.java;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.openmanufacturing.sds.metamodel.ModelElement;
import io.openmanufacturing.sds.metamodel.HasProperties;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.characteristic.StructuredValue;
import io.openmanufacturing.sds.metamodel.visitor.AspectStreamTraversalVisitor;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

/**
 * When a {@link Property} uses the {@link StructuredValue} Characteristic, this class retrieves the
 * Properties that are referenced in the Characteristic so that they are available for POJO code generation.
 * In other words, it "deconstructs" a Property into a List of Properties, all of which must be handled
 * in POJO initialization and (differently) in the POJO's constructor.
 */
public class StructuredValuePropertiesDeconstructor {
   private final List<DeconstructionSet> deconstructionSets;
   private final List<Property> allProperties;

   /**
    * Initializes the deconstructor for a given model element
    *
    * @param element the element that has Properties
    */
   public StructuredValuePropertiesDeconstructor( final HasProperties element ) {
      deconstructionSets = deconstructProperties( element );
      allProperties = getAllProperties( deconstructionSets );
   }

   /**
    * Retrieves a list of {@link DeconstructionSet}s for the given model element
    *
    * @param element the element hat has Properties
    * @return the list of {@link DeconstructionSet}s
    */
   private List<DeconstructionSet> deconstructProperties( final HasProperties element ) {
      final AspectVisitor<Stream<ModelElement>, Void> visitor = new AspectStreamTraversalVisitor();
      return element.getProperties().stream().flatMap( property ->
                  visitor.visitProperty( property, null )
                        .filter( StructuredValue.class::isInstance )
                        .map( StructuredValue.class::cast )
                        .map( structuredValue -> new DeconstructionSet( property,
                              structuredValue.getDeconstructionRule(),
                              structuredValue.getElements().stream()
                                    .filter( Property.class::isInstance )
                                    .map( Property.class::cast )
                                    .collect( Collectors.toList() ) ) ) )
            .collect( Collectors.toList() );
   }

   /**
    * Collects all {@link Property}s from all given DeconstructionSets
    *
    * @param deconstructionSets the list of DeconstructionSets
    * @return all Properties referenced in any set
    */
   private List<Property> getAllProperties( final List<DeconstructionSet> deconstructionSets ) {
      return deconstructionSets.stream()
            .map( DeconstructionSet::getElementProperties )
            .flatMap( List::stream )
            .collect( Collectors.toList() );
   }

   /**
    * Returns true if the given model element uses a StructuredValue Characteristic, i.e. if DeconstructionSets
    * are available for the model element
    *
    * @return true if DeconstructionSets are available for the model element, otherwise false
    */
   public boolean isApplicable() {
      return !getDeconstructionSets().isEmpty();
   }

   public List<DeconstructionSet> getDeconstructionSets() {
      return deconstructionSets;
   }

   public List<Property> getAllProperties() {
      return allProperties;
   }
}
