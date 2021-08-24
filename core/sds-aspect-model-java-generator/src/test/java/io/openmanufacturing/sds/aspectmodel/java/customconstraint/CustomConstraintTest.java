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

package io.openmanufacturing.sds.aspectmodel.java.customconstraint;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class CustomConstraintTest {

   private static Validator validator;

   @BeforeAll
   public static void setup() {
      final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
      validator = validatorFactory.getValidator();
   }

   @Test
   public void testValidTestClassExpectSuccess() {
      final TestAspect testAspect = new TestAspect( 6d, 8d, 2f, 5f, 6, 8 );

      final Set<ConstraintViolation<TestAspect>> violations = validator.validate( testAspect );
      assertThat( violations ).isEmpty();
   }

   @Test
   public void testDoubleMinConstraint() {
      final TestAspect testAspect = new TestAspect( 4d, 8d, 2f, 5f, 6, 8 );

      final Set<ConstraintViolation<TestAspect>> violations = validator.validate( testAspect );
      assertThat( violations ).hasSize( 1 );

      final ConstraintViolation<TestAspect> violation = violations.iterator().next();
      assertThat( violation.getMessage() ).isEqualTo( "The value must be at least 5." );
   }

   @Test
   public void testDoubleMaxConstraint() {
      final TestAspect testAspect = new TestAspect( 6d, 15d, 2f, 5f, 6, 8 );

      final Set<ConstraintViolation<TestAspect>> violations = validator.validate( testAspect );
      assertThat( violations ).hasSize( 1 );

      final ConstraintViolation<TestAspect> violation = violations.iterator().next();
      assertThat( violation.getMessage() ).isEqualTo( "The value must be less than 10." );
   }

   @Test
   public void testFloatMinConstraint() {
      final TestAspect testAspect = new TestAspect( 6d, 8d, 1.25f, 5f, 6, 8 );

      final Set<ConstraintViolation<TestAspect>> violations = validator.validate( testAspect );
      assertThat( violations ).hasSize( 1 );

      final ConstraintViolation<TestAspect> violation = violations.iterator().next();
      assertThat( violation.getMessage() ).isEqualTo( "The value must be greater than 1.5." );
   }

   @Test
   public void testFloatMaxConstraint() {
      final TestAspect testAspect = new TestAspect( 6d, 8d, 2f, 6.8f, 6, 8 );

      final Set<ConstraintViolation<TestAspect>> violations = validator.validate( testAspect );
      assertThat( violations ).hasSize( 1 );

      final ConstraintViolation<TestAspect> violation = violations.iterator().next();
      assertThat( violation.getMessage() ).isEqualTo( "The value must be at most 5.6." );
   }

   @Test
   public void testMinConstraint() {
      final TestAspect testAspect = new TestAspect( 6d, 8d, 2f, 5f, 4, 8 );

      final Set<ConstraintViolation<TestAspect>> violations = validator.validate( testAspect );
      assertThat( violations ).hasSize( 1 );

      final ConstraintViolation<TestAspect> violation = violations.iterator().next();
      assertThat( violation.getMessage() ).isEqualTo( "The value must be at least 5." );
   }

   @Test
   public void testMaxConstraint() {
      final TestAspect testAspect = new TestAspect( 6d, 8d, 2f, 5f, 6, 15 );

      final Set<ConstraintViolation<TestAspect>> violations = validator.validate( testAspect );
      assertThat( violations ).hasSize( 1 );

      final ConstraintViolation<TestAspect> violation = violations.iterator().next();
      assertThat( violation.getMessage() ).isEqualTo( "The value must be at most 10." );
   }
}
