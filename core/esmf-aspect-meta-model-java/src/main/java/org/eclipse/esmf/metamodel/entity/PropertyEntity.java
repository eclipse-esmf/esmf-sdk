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
package org.eclipse.esmf.metamodel.entity;

import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import static java.util.Optional.ofNullable;

public class PropertyEntity extends ModelEntityImpl implements Property {
   private Characteristic characteristic;
   private ScalarValue exampleValue;
   private boolean optional;
   private boolean notInPayload;
   private String payloadName;
   private boolean isAbstract;
   private Property extends_;

   public PropertyEntity(MetaModelBaseAttributes metaModelBaseAttributes,
                         CharacteristicEntity characteristic,
                         ScalarValue exampleValue,
                         boolean optional,
                         boolean notInPayload,
                         String payloadName,
                         boolean isAbstract,
                         Property extends_
   ) {
      super(metaModelBaseAttributes);
      this.characteristic = characteristic;
      this.exampleValue = exampleValue;
      this.optional = optional;
      this.notInPayload = notInPayload;
      this.payloadName = payloadName;
      this.isAbstract = isAbstract;
      this.extends_ = extends_;
   }

   /**
    * The Characteristic describing the semantics of the Property.
    *
    * @return the characteristic.
    */
   @Override
   public Optional<Characteristic> getCharacteristic() {
      return ofNullable(characteristic);
   }

   /**
    * A real world example value.
    *
    * @return the exampleValue.
    */
   @Override
   public Optional<ScalarValue> getExampleValue() {
      return ofNullable(exampleValue);
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
      return ofNullable(payloadName).orElseGet( this::getName );
   }

   @Override
   public boolean isAbstract() {
      return isAbstract;
   }

   @Override
   public Optional<Property> getExtends() {
      return ofNullable(extends_);
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
      return new StringJoiner( ", ", PropertyEntity.class.getSimpleName() + "[", "]" )
            .add( "characteristic=" + characteristic )
            .add( "exampleValue=" + exampleValue )
            .add( "optional=" + optional )
            .add( "notInPayload=" + notInPayload )
            .add( "isAbstract=" + isAbstract )
            .add( "extends=" + extends_ )
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
      final PropertyEntity that = (PropertyEntity) o;
      return Objects.equals( getName(), that.getName() ) &&
            Objects.equals( getAspectModelUrn(), that.getAspectModelUrn() ) &&
            optional == that.optional &&
            notInPayload == that.notInPayload &&
            isAbstract == that.isAbstract &&
            Objects.equals( characteristic, that.characteristic ) &&
            Objects.equals( exampleValue, that.exampleValue ) &&
            Objects.equals( extends_, that.extends_ );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), characteristic, exampleValue, optional, notInPayload, isAbstract, extends_ );
   }
}
