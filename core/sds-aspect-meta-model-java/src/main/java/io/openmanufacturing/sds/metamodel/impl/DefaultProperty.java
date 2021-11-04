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
package io.openmanufacturing.sds.metamodel.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

public class DefaultProperty extends BaseImpl implements Property {
   private final Characteristic characteristic;
   private final Optional<Object> exampleValue;
   private final boolean optional;
   private final boolean notInPayload;
   private final Optional<String> payloadName;

   public DefaultProperty( final MetaModelBaseAttributes metaModelBaseAttributes,
         final Characteristic characteristic,
         final Optional<Object> exampleValue,
         final boolean optional,
         final boolean notInPayload,
         final Optional<String> payloadName ) {
      super( metaModelBaseAttributes );
      this.characteristic = characteristic;
      this.exampleValue = exampleValue;
      this.optional = optional;
      this.notInPayload = notInPayload;
      this.payloadName = payloadName;
   }

   /**
    * The Characteristic describing the semantics of the Property.
    *
    * @return the characteristic.
    */
   @Override
   public Characteristic getCharacteristic() {
      return characteristic;
   }

   /**
    * A real world example value.
    *
    * @return the exampleValue.
    */
   @Override
   public Optional<Object> getExampleValue() {
      return exampleValue;
   }

   /**
    * Defines whether a property is optional.
    *
    * @return the optional.
    */
   @Override
   public boolean isOptional() {
      return optional;
   }

   /**
    * Defines whether a property is included in the runtime data of an Aspect.
    *
    */
   @Override
   public boolean isNotInPayload() {
      return notInPayload;
   }

   @Override
   public String getPayloadName() {
      return payloadName.orElseGet( this::getName );
   }

   /**
    * Accepts an Aspect visitor
    *
    * @param visitor The visitor to accept
    * @param <T> The result type of the traversal operation
    * @param <C> The context of the visitor traversal
    */
   @Override
   public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
      return visitor.visitProperty( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultProperty.class.getSimpleName() + "[", "]" )
            .add( "characteristic=" + characteristic )
            .add( "exampleValue=" + exampleValue )
            .add( "optional=" + optional )
            .add( "notInPayload=" + notInPayload )
            .toString();
   }

   @SuppressWarnings( "squid:S1067" ) // Generated equals method
   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      if ( !super.equals( o ) ) {
         return false;
      }
      final DefaultProperty that = (DefaultProperty) o;
      return optional == that.optional &&
            notInPayload == that.notInPayload &&
            Objects.equals( characteristic, that.characteristic ) &&
            Objects.equals( exampleValue, that.exampleValue );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), characteristic, exampleValue, optional, notInPayload );
   }
}
