/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.test.shared;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.metamodel.QuantityKind;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.Units;

import org.assertj.core.api.ListAssert;

/**
 * Assert for {@link Unit}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class UnitAssert<SELF extends UnitAssert<SELF, ACTUAL>, ACTUAL extends Unit> extends ModelElementAssert<SELF, ACTUAL> {
   protected UnitAssert( final ACTUAL actual ) {
      super( actual, UnitAssert.class, "Unit" );
   }

   public SELF hasNoSymbol() {
      assertThat( actual.getSymbol() ).isEmpty();
      return myself;
   }

   public SELF hasSymbol( final String symbol ) {
      assertThat( actual.getSymbol() ).isPresent().contains( symbol );
      return myself;
   }

   public SELF hasNoCode() {
      assertThat( actual.getCode() ).isEmpty();
      return myself;
   }

   public SELF hasCode( final String code ) {
      assertThat( actual.getCode() ).isPresent().contains( code );
      return myself;
   }

   public SELF hasNoReferenceUnit() {
      assertThat( actual.getReferenceUnit() ).isEmpty();
      return myself;
   }

   public SELF hasReferenceUnit( final String referenceUnit ) {
      assertThat( actual.getReferenceUnit() ).isPresent().contains( referenceUnit );
      assertThat( Units.fromName( referenceUnit ) ).as( "Unknown unit %s", referenceUnit ).isPresent();
      return myself;
   }

   public SELF hasNoConversionFactor() {
      assertThat( actual.getConversionFactor() ).isEmpty();
      return myself;
   }

   public SELF hasConversionFactor( final String conversionFactor ) {
      assertThat( actual.getConversionFactor() ).isPresent().contains( conversionFactor );
      return myself;
   }

   public SELF hasAtLeastOneQuantityKind() {
      assertThat( actual.getQuantityKinds() ).isNotEmpty();
      return myself;
   }

   public SELF hasNoQuantityKind() {
      assertThat( actual.getQuantityKinds() ).isEmpty();
      return myself;
   }

   public SELF hasQuantityKind( final QuantityKind quantityKind ) {
      assertThat( actual.getQuantityKinds() ).contains( quantityKind );
      return myself;
   }

   public ListAssert<QuantityKind> quantityKinds() {
      return assertThat( actual.getQuantityKinds().stream().toList() );
   }
}
