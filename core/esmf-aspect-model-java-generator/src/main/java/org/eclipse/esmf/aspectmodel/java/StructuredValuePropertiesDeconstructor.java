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

package org.eclipse.esmf.aspectmodel.java;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.loader.DefaultPropertyWrapper;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.HasProperties;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.characteristic.StructuredValue;
import org.eclipse.esmf.metamodel.impl.DefaultProperty;

import org.apache.commons.lang3.StringUtils;

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
      record Association( Property originalProperty, String deconstructionRule, Property referredProperty ) {}

      final List<Association> associations = element.getProperties().stream()
            .flatMap( property -> {
               if ( property.getEffectiveCharacteristic().isPresent() && property.getEffectiveCharacteristic()
                     .get() instanceof final StructuredValue structuredValue ) {
                  return structuredValue.getElements().stream()
                        .filter( Property.class::isInstance )
                        .map( Property.class::cast )
                        .map( structuredValueProperty ->
                              new Association( property, structuredValue.getDeconstructionRule(), structuredValueProperty ) );
               }
               return Stream.empty();
            } )
            .toList();

      final List<DeconstructionSet> result;
      if ( associations.stream()
            .anyMatch( association -> associations.stream()
                  .filter( a -> a.referredProperty().equals( association.referredProperty() ) )
                  .count() > 1 ) ) {
         // At least one property is referred to multiple times, so the field names are prefixed with the referring property
         return associations.stream()
               .collect( Collectors.groupingBy( association -> association.originalProperty ) )
               .entrySet()
               .stream()
               .map( entry -> new DeconstructionSet( entry.getKey(), entry.getValue().getFirst().deconstructionRule(),
                     entry.getValue().stream().map( association -> {
                        final Property originalProperty = association.referredProperty();
                        final String newPropertyName = createQualifiedPropertyName( association.originalProperty(),
                              association.referredProperty() );
                        final DefaultPropertyWrapper defaultPropertyWrapper = new DefaultPropertyWrapper( MetaModelBaseAttributes.builder()
                              .fromModelElement( originalProperty )
                              .withUrn( originalProperty.urn().withName( newPropertyName ) )
                              .build() );
                        defaultPropertyWrapper.setProperty( originalProperty );
                        defaultPropertyWrapper.setPayloadName( newPropertyName );
                        return (Property) defaultPropertyWrapper;
                     } ).toList() ) )
               .toList();
      }
      return associations.stream()
            .collect( Collectors.groupingBy( association -> association.originalProperty ) )
            .entrySet()
            .stream()
            .map( entry -> new DeconstructionSet( entry.getKey(), entry.getValue().getFirst().deconstructionRule(),
                  entry.getValue().stream().map( Association::referredProperty ).toList() ) )
            .toList();
   }

   public String createQualifiedPropertyName( final Property originalProperty, final Property referredProperty ) {
      return originalProperty.getPayloadName() + StringUtils.capitalize( referredProperty.getPayloadName() );
   }

   /**
    * Collects all {@link Property}s from all given DeconstructionSets
    *
    * @param deconstructionSets the list of DeconstructionSets
    * @return all Properties referenced in any set
    */
   private List<Property> getAllProperties( final List<DeconstructionSet> deconstructionSets ) {
      return deconstructionSets.stream()
            .map( DeconstructionSet::elementProperties )
            .flatMap( List::stream )
            .map( property -> (Property) new DefaultProperty(
                  MetaModelBaseAttributes.builder()
                        .fromModelElement( property )
                        .build(),
                  property.getCharacteristic(),
                  property.getExampleValue(),
                  true, // making it optional
                  property.isNotInPayload(),
                  Optional.of( property.getPayloadName() ),
                  property.isAbstract(),
                  property.getExtends()
            ) )
            .toList();
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
