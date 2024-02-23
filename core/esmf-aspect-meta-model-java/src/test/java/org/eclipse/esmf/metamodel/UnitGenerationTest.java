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
package org.eclipse.esmf.metamodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class UnitGenerationTest {
   @Test
   public void testUnitFromName() {
      final Optional<Unit> optionalUnit = Units.fromName( "degreeCelsius" );
      final Unit unit = optionalUnit.get();
      assertThat( unit.getPreferredName( Locale.ENGLISH ) ).isEqualTo( "degree Celsius" );

      assertThat( unit.getReferenceUnit().get() ).isEqualTo( "kelvin" );
      assertThat( unit.getConversionFactor().get() ).isEqualTo( "1 × K" );
   }

   @Test
   public void testUnitWithoutReferenceUnit() {
      final Unit ampere = Units.fromName( "ampere" ).get();
      assertThat( ampere.getReferenceUnit() ).isNotPresent();
   }

   @Test
   public void testUnitFromcode() {
      final Optional<Unit> unit = Units.fromCode( "A97" );
      assertThat( unit.get() ).isEqualTo( Units.fromName( "hectopascal" ).get() );
   }

   @Test
   public void testUnitFromSymbol() {
      // Unit with unique symbol
      assertThat( Units.fromSymbol( "kg" ) ).containsExactly( Units.fromName( "kilogram" ).get() );

      // Mutiple units with the same symbol
      assertThat( Units.fromSymbol( "rad" ).size() ).isGreaterThan( 1 );
   }

   @Test
   public void testUnitsWithQuantityKind() {
      final Set<Unit> units = Units.unitsWithQuantityKind( QuantityKinds.DISTANCE );
      assertThat( units ).containsAnyOf( Units.fromName( "nanometre" ).get(), Units.fromName( "metre" ).get(),
            Units.fromName( "kilometre" ).get(), Units.fromName( "foot" ).get(), Units.fromName( "lightYear" ).get() );
      assertThat( units ).doesNotContain( Units.fromName( "degreeCelsius" ).get(), Units.fromName( "litre" ).get(),
            Units.fromName( "electronvoltPerMetre" ).get() );
   }

   @Test
   public void testGeneratedQuantityKinds() {
      assertThat( QuantityKinds.values().length ).isGreaterThanOrEqualTo( 80 );
   }

   @Test
   public void testQuantityKindFromName() {
      final Optional<QuantityKind> optionalQuantityKind = QuantityKinds.fromName( "distance" );
      assertThat( optionalQuantityKind.get() ).isEqualTo( QuantityKinds.DISTANCE );
      final QuantityKind quantityKind = optionalQuantityKind.get();
      assertThat( quantityKind.getName() ).isEqualTo( "distance" );
      assertThat( quantityKind.getLabel() ).isEqualTo( "distance" );
   }
}
