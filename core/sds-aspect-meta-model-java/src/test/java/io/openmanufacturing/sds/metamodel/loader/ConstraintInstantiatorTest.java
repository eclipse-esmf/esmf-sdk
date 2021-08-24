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

package io.openmanufacturing.sds.metamodel.loader;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.EncodingConstraint;
import io.openmanufacturing.sds.metamodel.LanguageConstraint;
import io.openmanufacturing.sds.metamodel.LengthConstraint;
import io.openmanufacturing.sds.metamodel.RegularExpressionConstraint;
import io.openmanufacturing.sds.metamodel.Trait;
import io.openmanufacturing.sds.test.TestAspect;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

public class ConstraintInstantiatorTest extends MetaModelInstantiatorTest {

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testLanguageConstraintInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LANGUAGE_CONSTRAINT, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic();
      final LanguageConstraint languageConstraint = (LanguageConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( languageConstraint,
            "Test Language Constraint",
            "This is a test language constraint.",
            "http://example.com/omp" );

      assertThat( languageConstraint.getLanguageCode() ).isEqualTo( Locale.GERMANY );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEncodingConstraintInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENCODING_CONSTRAINT, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic();
      final EncodingConstraint encodingConstraint = (EncodingConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( encodingConstraint,
            "Test Encoding Constraint",
            "This is a test encoding constraint.",
            "http://example.com/omp" );

      assertThat( encodingConstraint.getValue() ).isEqualTo( StandardCharsets.UTF_8 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testLengthConstraintInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LENGTH_CONSTRAINT, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic();
      final LengthConstraint lengthConstraint = (LengthConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( lengthConstraint,
            "Test Length Constraint",
            "This is a test length constraint.",
            "http://example.com/omp" );

      assertThat( lengthConstraint.getMaxValue() ).isPresent();
      assertThat( lengthConstraint.getMaxValue().get() ).isEqualTo( new BigInteger( "10" ) );
      assertThat( lengthConstraint.getMinValue() ).isPresent();
      assertThat( lengthConstraint.getMinValue().get() ).isEqualTo( new BigInteger( "5" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRegularExpressionConstraintInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_REGULAR_EXPRESSION_CONSTRAINT, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic();
      final RegularExpressionConstraint regularExpressionConstraint = (RegularExpressionConstraint) trait
            .getConstraints().get( 0 );

      assertBaseAttributes( regularExpressionConstraint,
            "Test Regular Expression Constraint",
            "This is a test regular expression constraint.",
            "http://example.com/omp" );

      assertThat( regularExpressionConstraint.getValue() ).isEqualTo( "^[0-9]*$" );
   }
}
