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

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.constraint.EncodingConstraint;
import org.eclipse.esmf.metamodel.constraint.LanguageConstraint;
import org.eclipse.esmf.metamodel.constraint.LengthConstraint;
import org.eclipse.esmf.metamodel.constraint.RegularExpressionConstraint;
import org.eclipse.esmf.test.TestAspect;

import org.junit.jupiter.api.Test;

public class ConstraintInstantiatorTest extends AbstractAspectModelInstantiatorTest {
   @Test
   public void testLanguageConstraintInstantiationExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LANGUAGE_CONSTRAINT );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final LanguageConstraint languageConstraint = (LanguageConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( languageConstraint,
            "Test Language Constraint",
            "This is a test language constraint.",
            "http://example.com/" );

      assertThat( languageConstraint.getLanguageCode() ).isEqualTo( Locale.forLanguageTag( "de" ) );
   }

   @Test
   public void testEncodingConstraintInstantiationExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENCODING_CONSTRAINT );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final EncodingConstraint encodingConstraint = (EncodingConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( encodingConstraint,
            "Test Encoding Constraint",
            "This is a test encoding constraint.",
            "http://example.com/" );

      assertThat( encodingConstraint.getValue() ).isEqualTo( StandardCharsets.UTF_8 );
   }

   @Test
   public void testLengthConstraintInstantiationExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LENGTH_CONSTRAINT );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final LengthConstraint lengthConstraint = (LengthConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( lengthConstraint,
            "Test Length Constraint",
            "This is a test length constraint.",
            "http://example.com/" );

      assertThat( lengthConstraint.getMaxValue() ).isPresent();
      assertThat( lengthConstraint.getMaxValue().get() ).isEqualTo( new BigInteger( "10" ) );
      assertThat( lengthConstraint.getMinValue() ).isPresent();
      assertThat( lengthConstraint.getMinValue().get() ).isEqualTo( new BigInteger( "5" ) );
   }

   @Test
   public void testRegularExpressionConstraintInstantiationExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_REGULAR_EXPRESSION_CONSTRAINT );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final RegularExpressionConstraint regularExpressionConstraint = (RegularExpressionConstraint) trait
            .getConstraints().get( 0 );

      assertBaseAttributes( regularExpressionConstraint,
            "Test Regular Expression Constraint",
            "This is a test regular expression constraint.",
            "http://example.com/" );

      assertThat( regularExpressionConstraint.getValue() ).isEqualTo( "^[0-9]*$" );
   }
}
