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

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import org.eclipse.esmf.metamodel.characteristic.Quantifiable;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;

public class DefaultQuantifiable extends DefaultCharacteristic implements Quantifiable {
   private final Optional<Unit> unit;

   public DefaultQuantifiable( final MetaModelBaseAttributes metaModelBaseAttributes, final Type dataType, final Optional<Unit> unit ) {
      super( metaModelBaseAttributes, Optional.of( dataType ) );
      this.unit = unit;
   }

   /**
    * The Unit of the Quantifiable.
    *
    * @return the unit.
    */
   @Override
   public Optional<Unit> getUnit() {
      return unit;
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
      return visitor.visitQuantifiable( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultQuantifiable.class.getSimpleName() + "[", "]" )
            .add( "unit=" + unit )
            .toString();
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
      final DefaultQuantifiable that = (DefaultQuantifiable) o;
      return Objects.equals( unit, that.unit );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), unit );
   }
}
