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

package org.eclipse.esmf.aspectmodel.loader;

import java.util.Optional;

import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.impl.DefaultProperty;

public class DefaultPropertyWrapper extends DefaultProperty {
   private Property property;
   private String payloadName;

   public DefaultPropertyWrapper( final MetaModelBaseAttributes metaModelBaseAttributes ) {
      super( metaModelBaseAttributes, null, null, false, false, null, false, null );
   }

   @Override
   public String getPayloadName() {
      return payloadName != null ? payloadName : property.getPayloadName();
   }

   @Override
   public Optional<ScalarValue> getExampleValue() {
      return property.getExampleValue();
   }

   @Override
   public boolean isNotInPayload() {
      return property.isNotInPayload();
   }

   @Override
   public boolean isOptional() {
      return property.isOptional();
   }

   @Override
   public boolean isAbstract() {
      return property.isAbstract();
   }

   @Override
   public Optional<Property> getExtends() {
      return property.getExtends();
   }

   @Override
   public Optional<Characteristic> getCharacteristic() {
      return property.getCharacteristic();
   }

   @Override
   public boolean equals( final Object o ) {
      if ( o != null ) {
         return o.equals( property );
      }
      return false;
   }

   @Override
   public int hashCode() {
      return property.hashCode();
   }

   public void setProperty( final Property property ) {
      this.property = property;
   }

   public void setPayloadName( final String payloadName ) {
      this.payloadName = payloadName;
   }
}
