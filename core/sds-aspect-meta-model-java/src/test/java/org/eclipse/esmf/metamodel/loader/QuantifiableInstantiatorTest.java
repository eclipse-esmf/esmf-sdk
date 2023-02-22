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

package org.eclipse.esmf.metamodel.loader;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.jena.vocabulary.XSD;
import org.assertj.core.api.Assertions;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.Unit;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.characteristic.Duration;
import org.eclipse.esmf.characteristic.Measurement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.characteristic.Quantifiable;
import io.openmanufacturing.sds.metamodel.QuantityKinds;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestModel;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

public class QuantifiableInstantiatorTest extends MetaModelInstantiatorTest {

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testQuantifiableCharacteristicWithUnitInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn
            .fromUrn( TestModel.TEST_NAMESPACE + "TestQuantifiable" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_QUANTIFIABLE_AND_UNIT, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Property property = aspect.getProperties().get( 0 );
      final Quantifiable quantifiable = (Quantifiable) property.getCharacteristic().get();

      assertBaseAttributes( quantifiable, expectedAspectModelUrn, "TestQuantifiable",
            "Test Quantifiable", "This is a test Quantifiable", "http://example.com/omp" );

      final Scalar scalar = (Scalar) property.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( "http://www.w3.org/2001/XMLSchema#float" );

      final Unit unit = quantifiable.getUnit().get();
      assertThat( unit.getName() ).isEqualTo( "hertz" );
      assertThat( unit.getSymbol().get() ).isEqualTo( "Hz" );
      assertThat( unit.getQuantityKinds() ).contains( QuantityKinds.FREQUENCY );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testQuantifiableCharacteristicWithoutUnitInstantiationExpectSuccess(
         final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn
            .fromUrn( TestModel.TEST_NAMESPACE + "TestQuantifiable" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_QUANTIFIABLE_WITHOUT_UNIT, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Quantifiable quantifiable = (Quantifiable) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( quantifiable, expectedAspectModelUrn, "TestQuantifiable",
            "Test Quantifiable", "This is a test Quantifiable", "http://example.com/omp" );
      final Scalar scalar = (Scalar) quantifiable.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xfloat.getURI() );
      Assertions.assertThat( quantifiable.getUnit() ).isNotPresent();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testMeasurementCharacteristicInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn
            .fromUrn( TestModel.TEST_NAMESPACE + "TestMeasurement" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_MEASUREMENT, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Measurement measurement = (Measurement) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( measurement, expectedAspectModelUrn, "TestMeasurement",
            "Test Measurement", "This is a test Measurement", "http://example.com/omp" );

      final Scalar scalar = (Scalar) measurement.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xfloat.getURI() );

      final Unit unit = measurement.getUnit().get();
      assertThat( unit.getName() ).isEqualTo( "kelvin" );
      assertThat( unit.getSymbol().get() ).isEqualTo( "K" );
      assertThat( unit.getQuantityKinds() ).hasSize( 6 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testDurationCharacteristicInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn
            .fromUrn( TestModel.TEST_NAMESPACE + "TestDuration" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_DURATION, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Duration duration = (Duration) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( duration, expectedAspectModelUrn, "TestDuration",
            "Test Duration", "This is a test Duration", "http://example.com/omp" );

      final Scalar scalar = (Scalar) duration.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xint.getURI() );

      final Unit unit = duration.getUnit().get();
      assertThat( unit.getName() ).isEqualTo( "kilosecond" );
      assertThat( unit.getSymbol().get() ).isEqualTo( "ks" );
      assertThat( unit.getQuantityKinds() ).hasSize( 10 );
   }
}
