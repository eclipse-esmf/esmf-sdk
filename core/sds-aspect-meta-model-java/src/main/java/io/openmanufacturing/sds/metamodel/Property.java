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

package io.openmanufacturing.sds.metamodel;

import java.util.Optional;

/**
 * A Property has a name that is unique to its use in an Aspect or an Entity, and a Characteristic (i.e., the
 * description of the Property). It describes a single piece of information in the Aspect or Entity.
 *
 * @since BAMM 1.0.0
 */
public interface Property extends Base, IsDescribed {

   /**
    * @return the {@link Characteristic} describing this Property.
    */
   Characteristic getCharacteristic();

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
    * @see <a href="https://openmanufacturingplatform.github.io/sds-bamm-aspect-meta-model/bamm-specification/snapshot/modeling-guidelines.html#declaring-enumerations">BAMM Aspect Meta Model
    *       Specification - Declaring Enumerations</a>
    * @since BAMM 1.0.0
    */
   default boolean isNotInPayload() {
      return false;
   }

   /**
    * Returns the Property's unconstrained Characteristic
    *
    * @return The Property's Characteristic without Constraints
    */
   default Characteristic getEffectiveCharacteristic() {
      Characteristic characteristic = getCharacteristic();
      while ( characteristic instanceof Trait ) {
         characteristic = ((Trait) characteristic).getBaseCharacteristic();
      }
      return characteristic;
   }

   /**
    * @return the type for the Property.
    */
   default Optional<Type> getDataType() {
      return getEffectiveCharacteristic().getDataType();
   }
}
