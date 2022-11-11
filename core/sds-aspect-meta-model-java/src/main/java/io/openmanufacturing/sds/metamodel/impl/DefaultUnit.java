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

import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import com.google.common.base.Objects;

import io.openmanufacturing.sds.metamodel.QuantityKind;
import io.openmanufacturing.sds.metamodel.Unit;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

public class DefaultUnit extends ModelElementImpl implements Unit {
   private final Optional<String> symbol;
   private final Optional<String> code;
   private final Optional<String> referenceUnit;
   private final Optional<String> conversionFactor;
   private final Set<QuantityKind> quantityKinds;

   public DefaultUnit(
         final MetaModelBaseAttributes metaModelBaseAttributes,
         final Optional<String> symbol,
         final Optional<String> code,
         final Optional<String> referenceUnit,
         final Optional<String> conversionFactor,
         final Set<QuantityKind> quantityKinds ) {
      super( metaModelBaseAttributes );
      this.symbol = symbol;
      this.code = code;
      this.referenceUnit = referenceUnit;
      this.conversionFactor = conversionFactor;
      this.quantityKinds = quantityKinds;
   }

   @Override
   public Optional<String> getSymbol() {
      return symbol;
   }

   @Override
   public Optional<String> getCode() {
      return code;
   }

   @Override
   public Optional<String> getReferenceUnit() {
      return referenceUnit;
   }

   @Override
   public Optional<String> getConversionFactor() {
      return conversionFactor;
   }

   @Override
   public Set<QuantityKind> getQuantityKinds() {
      return quantityKinds;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final DefaultUnit that = (DefaultUnit) o;
      return Objects.equal( getName(), that.getName() ) &&
            Objects.equal( code, that.code );
   }

   @Override
   public int hashCode() {
      return Objects.hashCode( symbol, code, referenceUnit, conversionFactor, quantityKinds );
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
      return visitor.visitUnit( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultUnit.class.getSimpleName() + "[", "]" )
            .add( "symbol=" + symbol )
            .add( "code=" + code )
            .add( "referenceUnit=" + referenceUnit )
            .add( "conversionFactor=" + conversionFactor )
            .add( "quantityKinds=" + quantityKinds )
            .toString();
   }
}
