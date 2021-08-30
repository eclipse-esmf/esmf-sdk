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

package io.openmanufacturing.sds.aspectmodel.urn;

/**
 * Provides the element types defined for the Aspect Model URN.
 *
 * It is differentiated between root model elements, these are the element types defined in the BAMM, and
 * model elements, these are elements which are defined in the scope of a root element. For example a Property
 * defined in an Aspect, or a Property defined in an Entity.
 */
public enum ElementType {

   //root model elements
   META_MODEL( "meta-model" ),
   ASPECT_MODEL( "aspect-model" ),
   ENTITY( "entity" ),
   CHARACTERISTIC( "characteristic" ),
   UNIT( "unit" ),
   //additional constants which help to differentiate between a root model element and a model element
   ASPECT_MODEL_ELEMENT( "aspect-model" ),
   ENTITY_MODEL_ELEMENT( "entity" ),
   CHARACTERISTIC_MODEL_ELEMENT( "characteristic" ),
   NONE( "" );

   private final String value;

   ElementType( final String value ) {
      this.value = value;
   }

   public String getValue() {
      return value;
   }
}
