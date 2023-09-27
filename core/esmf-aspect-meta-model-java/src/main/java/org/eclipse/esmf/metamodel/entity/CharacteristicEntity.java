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
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.impl.ModelElementImpl;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class CharacteristicEntity extends ModelEntityImpl implements Characteristic {
   private final Optional<Type> dataType;

   public CharacteristicEntity(final MetaModelBaseAttributes metaModelBaseAttributes, final Optional<Type> dataType ) {
      super( metaModelBaseAttributes );
      this.dataType = dataType;
   }

   /**
    * Defines the data type of all Properties which use this Characteristic.
    *
    * @return the dataType.
    */
   @Override
   public Optional<Type> getDataType() {
      return dataType;
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
      return visitor.visitCharacteristic( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", CharacteristicEntity.class.getSimpleName() + "[", "]" )
            .add( "dataType=" + dataType )
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
      return super.equals( o );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode() );
   }
}
