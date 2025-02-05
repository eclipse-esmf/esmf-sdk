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

package org.eclipse.esmf.aspectmodel.loader;

import static org.eclipse.esmf.test.shared.AspectModelAsserts.assertThat;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.QuantityKinds;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.characteristic.Duration;
import org.eclipse.esmf.metamodel.characteristic.Measurement;
import org.eclipse.esmf.metamodel.characteristic.Quantifiable;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestModel;

import org.apache.jena.vocabulary.XSD;
import org.junit.jupiter.api.Test;

public class QuantifiableInstantiatorTest extends AbstractAspectModelInstantiatorTest {
   @Test
   public void testQuantifiableCharacteristicWithUnitInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestQuantifiable" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_QUANTIFIABLE_AND_UNIT );

      assertThat( aspect ).properties().hasSize( 1 );

      final Property property = aspect.getProperties().get( 0 );
      final Quantifiable quantifiable = (Quantifiable) property.getCharacteristic().get();

      assertBaseAttributes( quantifiable, expectedAspectModelUrn, "TestQuantifiable",
            "Test Quantifiable", "This is a test Quantifiable", "http://example.com/" );

      final Scalar scalar = (Scalar) property.getDataType().get();
      assertThat( scalar ).hasUrn( XSD.xfloat.getURI() );

      final Unit unit = quantifiable.getUnit().get();
      assertThat( unit ).hasName( "hertz" ).hasSymbol( "Hz" ).quantityKinds().contains( QuantityKinds.FREQUENCY );
   }

   @Test
   public void testQuantifiableCharacteristicWithoutUnitInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestQuantifiable" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_QUANTIFIABLE_WITHOUT_UNIT );

      assertThat( aspect ).properties().hasSize( 1 );

      final Quantifiable quantifiable = (Quantifiable) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( quantifiable, expectedAspectModelUrn, "TestQuantifiable",
            "Test Quantifiable", "This is a test Quantifiable", "http://example.com/" );
      final Scalar scalar = (Scalar) quantifiable.getDataType().get();
      assertThat( scalar ).hasUrn( XSD.xfloat.getURI() );
      assertThat( quantifiable ).hasNoUnit();
   }

   @Test
   public void testMeasurementCharacteristicInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestMeasurement" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_MEASUREMENT );

      assertThat( aspect ).properties().hasSize( 1 );

      final Measurement measurement = (Measurement) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( measurement, expectedAspectModelUrn, "TestMeasurement",
            "Test Measurement", "This is a test Measurement", "http://example.com/" );

      final Scalar scalar = (Scalar) measurement.getDataType().get();
      assertThat( scalar ).hasUrn( XSD.xfloat.getURI() );

      final Unit unit = measurement.getUnit().get();
      assertThat( unit ).hasName( "kelvin" ).hasSymbol( "K" ).quantityKinds().hasSize( 6 );
   }

   @Test
   public void testDurationCharacteristicInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestDuration" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_DURATION );

      assertThat( aspect ).properties().hasSize( 1 );

      final Duration duration = (Duration) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( duration, expectedAspectModelUrn, "TestDuration",
            "Test Duration", "This is a test Duration", "http://example.com/" );

      final Scalar scalar = (Scalar) duration.getDataType().get();
      assertThat( scalar ).hasUrn( XSD.xint.getURI() );

      final Unit unit = duration.getUnit().get();
      assertThat( unit ).hasName( "kilosecond" ).hasSymbol( "ks" ).quantityKinds().hasSize( 1 );
   }
}
