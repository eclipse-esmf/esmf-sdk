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

package org.eclipse.esmf.metamodel.characteristic.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;

public class DefaultTrait extends DefaultCharacteristic implements Trait {

   private final Characteristic baseCharacteristic;
   private final List<Constraint> contraints;

   public DefaultTrait(
         final MetaModelBaseAttributes metaModelBaseAttributes,
         final Characteristic baseCharacteristic,
         final List<Constraint> constraints ) {
      super( metaModelBaseAttributes, baseCharacteristic.getDataType() );
      this.baseCharacteristic = baseCharacteristic;
      contraints = constraints;
   }

   /**
    * The Characteristic which is wrapped by a Constraint.
    *
    * @return the Characteristic that is wrapped by this Constraint
    */
   @Override
   public Characteristic getBaseCharacteristic() {
      return baseCharacteristic;
   }

   /**
    * A Constraint's data type is its base Characteristic's data type.
    *
    * @return the base Characteristic's data type
    */
   @Override
   public Optional<Type> getDataType() {
      return baseCharacteristic.getDataType();
   }

   @Override
   public List<Constraint> getConstraints() {
      return contraints;
   }

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
      final DefaultTrait that = (DefaultTrait) o;
      return Objects.equals( baseCharacteristic, that.baseCharacteristic );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), baseCharacteristic );
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
      return visitor.visitTrait( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultTrait.class.getSimpleName() + "[", "]" )
            .add( "baseCharacteristic=" + baseCharacteristic )
            .add( "contraints=" + contraints )
            .toString();
   }
}
