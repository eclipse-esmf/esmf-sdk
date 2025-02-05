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
import static org.eclipse.esmf.test.shared.AspectModelAsserts.assertThat;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class UnitGenerationTest {
   @Test
   public void testUnitFromName() {
      final Optional<Unit> optionalUnit = Units.fromName( "degreeCelsius" );
      final Unit unit = optionalUnit.get();
      assertThat( unit )
            .hasPreferredName( "degree Celsius", Locale.ENGLISH )
            .hasReferenceUnit( "kelvin" )
            .hasConversionFactor( "1 × K" );
   }

   @Test
   public void testUnitWithoutReferenceUnit() {
      final Unit ampere = Units.fromName( "ampere" ).get();
      assertThat( ampere ).hasNoReferenceUnit();
   }

   @Test
   public void testUnitFromcode() {
      final Unit unit = Units.fromCode( "A97" ).get();
      assertThat( unit ).isEqualTo( Units.fromName( "hectopascal" ).get() );
   }

   @Test
   public void testUnitFromSymbol() {
      // Unit with unique symbol
      assertThat( Units.fromSymbol( "kg" ) ).containsExactly( Units.fromName( "kilogram" ).get() );

      // Mutiple units with the same symbol
      assertThat( Units.fromSymbol( "rad" ) ).size().isGreaterThan( 1 );
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
      final QuantityKind quantityKind = QuantityKinds.fromName( "distance" ).get();
      assertThat( quantityKind ).hasName( "distance" ).hasLabel( "distance" );
   }
}
