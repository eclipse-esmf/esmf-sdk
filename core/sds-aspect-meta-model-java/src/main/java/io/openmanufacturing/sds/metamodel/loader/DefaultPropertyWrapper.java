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

package io.openmanufacturing.sds.metamodel.loader;

import java.util.Objects;
import java.util.Optional;

import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.ScalarValue;
import io.openmanufacturing.sds.metamodel.impl.DefaultCharacteristic;
import io.openmanufacturing.sds.metamodel.impl.DefaultProperty;

public class DefaultPropertyWrapper extends DefaultProperty {

   private Characteristic characteristic;
   private Optional<ScalarValue> exampleValue;
   private boolean optional;
   private boolean notInPayload;
   private boolean isAbstract;
   private Optional<Property> extends_;
   private Optional<String> payloadName;

   public DefaultPropertyWrapper( final MetaModelBaseAttributes metaModelBaseAttributes ) {
      super( metaModelBaseAttributes, new DefaultCharacteristic( metaModelBaseAttributes, Optional.empty() ),
            Optional.empty(), false, false, Optional.empty(), false, Optional.empty() );
   }

   @Override
   public String getPayloadName() {
      return payloadName.orElseGet( this::getName );
   }

   public void setPayloadName( final Optional<String> payloadName ) {
      this.payloadName = payloadName;
   }

   @Override
   public Optional<ScalarValue> getExampleValue() {
      return exampleValue;
   }

   public void setExampleValue( final Optional<ScalarValue> exampleValue ) {
      this.exampleValue = exampleValue;
   }

   @Override
   public boolean isNotInPayload() {
      return notInPayload;
   }

   public void setNotInPayload( final boolean notInPayload ) {
      this.notInPayload = notInPayload;
   }

   @Override
   public boolean isOptional() {
      return optional;
   }

   public void setOptional( final boolean optional ) {
      this.optional = optional;
   }

   @Override
   public boolean isAbstract() {
      return isAbstract;
   }

   public void setAbstract( final boolean isAbstract ) {
      this.isAbstract = isAbstract;
   }

   @Override
   public Optional<Property> getExtends() {
      return extends_;
   }

   public void setExtends_( final Optional<Property> extends_ ) {
      this.extends_ = extends_;
   }

   @Override
   public Characteristic getCharacteristic() {
      return characteristic;
   }

   public void setCharacteristic( final Characteristic characteristic ) {
      this.characteristic = characteristic;
   }

   @SuppressWarnings( "squid:S1067" )
   // Shall be the same as in super class just without calling super due to possible recursive dependencies
   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final DefaultPropertyWrapper that = (DefaultPropertyWrapper) o;
      return optional == that.optional &&
            notInPayload == that.notInPayload &&
            isAbstract == that.isAbstract &&
            Objects.equals( extends_, that.extends_ ) &&
            Objects.equals( characteristic, that.characteristic ) &&
            Objects.equals( exampleValue, that.exampleValue );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), exampleValue, optional, notInPayload, isAbstract, extends_ );
   }
}
