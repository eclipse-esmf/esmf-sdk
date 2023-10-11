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

import org.eclipse.esmf.characteristic.Collection;
import org.eclipse.esmf.characteristic.impl.DefaultCollection;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class CharacteristicCollectionEntity extends CharacteristicEntity implements Collection {

   private boolean allowDuplicates;
   private boolean ordered;
   private Characteristic elementCharacteristic;

   public CharacteristicCollectionEntity( MetaModelBaseAttributes metaModelBaseAttributes,
                                          Type dataType, Characteristic elementCharacteristic ) {
      super( metaModelBaseAttributes, dataType );
      allowDuplicates = true;
      ordered = false;
      this.elementCharacteristic = elementCharacteristic;
   }

   CharacteristicCollectionEntity( final MetaModelBaseAttributes metaModelBaseAttributes,
                      final Type dataType, final boolean allowDuplicates, final boolean ordered,
                      final Characteristic elementCharacteristic ) {
      super( metaModelBaseAttributes, dataType );
      this.allowDuplicates = allowDuplicates;
      this.ordered = ordered;
      this.elementCharacteristic = elementCharacteristic;
   }

   /**
    * Ensures that the property allowDuplicates is set at least once for Collections.
    *
    * @return the allowDuplicates.
    */
   @Override
   public boolean isAllowDuplicates() {
      return allowDuplicates;
   }

   /**
    * Ensures that the property ordered is set at least once for Collections.
    *
    * @return the ordered.
    */
   @Override
   public boolean isOrdered() {
      return ordered;
   }

   @Override
   public CollectionValue.CollectionType getCollectionType() {
      return CollectionValue.CollectionType.COLLECTION;
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
      return visitor.visitCollection( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", CharacteristicCollectionEntity.class.getSimpleName() + "[", "]" )
            .add( "allowDuplicates=" + allowDuplicates )
            .add( "ordered=" + ordered )
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
      final CharacteristicCollectionEntity that = (CharacteristicCollectionEntity) o;
      return allowDuplicates == that.allowDuplicates &&
              ordered == that.ordered &&
              Objects.equals( elementCharacteristic, that.elementCharacteristic );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), allowDuplicates, ordered, elementCharacteristic );
   }
}
