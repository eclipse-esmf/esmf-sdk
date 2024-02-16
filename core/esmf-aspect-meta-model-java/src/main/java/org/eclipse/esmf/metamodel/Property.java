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

package org.eclipse.esmf.metamodel;

import java.util.Optional;

import org.eclipse.esmf.characteristic.Trait;

/**
 * A Property has a name that is unique to its use in an Aspect or an Entity, and a Characteristic (i.e., the
 * description of the Property). It describes a single piece of information in the Aspect or Entity.
 *
 * @since SAMM 1.0.0
 */
public interface Property extends NamedElement {

   /**
    * @return the {@link Characteristic} describing this Property. This can be empty when the Property is abstract.
    */
   Optional<Characteristic> getCharacteristic();

   /**
    * @return an {@link Optional} which may contain an example value for the Property. The type of the value is
    *       determined by the {@link Type} returned by {@link Property#getDataType()}.
    */
   Optional<ScalarValue> getExampleValue();

   /**
    * @return the name of the Property used in the runtime payload.
    */
   String getPayloadName();

   /**
    * @return a {@link boolean} which determines whether the Property is optional. Properties are mandatory by default.
    */
   default boolean isOptional() {
      return false;
   }

   /**
    * @return a {@link boolean} which determines whether the Property is included in the runtime data of an Aspect.
    *       By default Properties are included in the runtime data.
    *
    * @see
    * <a href="https://eclipse-esmf.github.io/samm-specification/2.0.0/modeling-guidelines.html#declaring-enumerations">Semantic Aspect Meta Model
    *       Specification - Declaring Enumerations</a>
    * @since SAMM 1.0.0
    */
   default boolean isNotInPayload() {
      return false;
   }

   /**
    * Returns true if the Property is abstract
    *
    * @return true if the Property is abstract
    */
   default boolean isAbstract() {
      return false;
   }

   /**
    * Returns the Property's unconstrained Characteristic.
    * This is undefined when the Property is abstract
    *
    * @return The Property's Characteristic without Constraints, or empty, if {@link #isAbstract()} is true
    */
   default Optional<Characteristic> getEffectiveCharacteristic() {
      return getCharacteristic().map( characteristic -> {
         while ( characteristic.is( Trait.class ) ) {
            characteristic = characteristic.as( Trait.class ).getBaseCharacteristic();
         }
         return characteristic;
      } );
   }

   /**
    * @return the type for the Property.
    */
   default Optional<Type> getDataType() {
      return getEffectiveCharacteristic().flatMap( Characteristic::getDataType );
   }

   /**
    * @return the Property that is extended by this Property, if present
    */
   default Optional<Property> getExtends() {
      return Optional.empty();
   }
}
