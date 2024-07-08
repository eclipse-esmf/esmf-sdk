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

import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;

public class DefaultCollection extends DefaultCharacteristic implements Collection {

   private final boolean allowDuplicates;
   private final boolean ordered;
   private final Optional<Characteristic> elementCharacteristic;

   public DefaultCollection( final MetaModelBaseAttributes metaModelBaseAttributes,
         final Optional<Type> dataType, final Optional<Characteristic> elementCharacteristic ) {
      super( metaModelBaseAttributes, dataType );
      allowDuplicates = true;
      ordered = false;
      this.elementCharacteristic = elementCharacteristic;
   }

   DefaultCollection( final MetaModelBaseAttributes metaModelBaseAttributes,
         final Optional<Type> dataType, final boolean allowDuplicates, final boolean ordered,
         final Optional<Characteristic> elementCharacteristic ) {
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
   public Optional<Characteristic> getElementCharacteristic() {
      return elementCharacteristic;
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
      return new StringJoiner( ", ", DefaultCollection.class.getSimpleName() + "[", "]" )
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
      final DefaultCollection that = (DefaultCollection) o;
      return allowDuplicates == that.allowDuplicates
            && ordered == that.ordered
            && Objects.equals( elementCharacteristic, that.elementCharacteristic );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), allowDuplicates, ordered, elementCharacteristic );
   }
}
